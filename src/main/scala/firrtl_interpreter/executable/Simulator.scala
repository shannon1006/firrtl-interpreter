// See LICENSE for license details.

//TODO: (chick) handle reset and multi-clock for registers
//TODO: (chick) shift amounts can only be int, this may reduce match statements
//TODO: (chick) implement memories
//TODO: (chick) implement stop
//TODO: (chick) implement black boxes
//TODO: (chick) implement poison system maybe
//TODO: (chick) implement XZ system

package firrtl_interpreter.executable

import firrtl.PortKind
import firrtl.ir.Circuit
import firrtl_interpreter._
import logger.{LazyLogging, LogLevel, Logger}

//scalastyle:off magic.number
class Simulator(ast: Circuit, val optionsManager: HasInterpreterSuite) extends LazyLogging {
  val interpreterOptions: InterpreterOptions = optionsManager.interpreterOptions

  var lastStopResult: Option[Int] = None
  def stopped: Boolean = lastStopResult.nonEmpty
  var verbose: Boolean = false

  val loweredAst: Circuit = if(interpreterOptions.lowCompileAtLoad) {
    ToLoFirrtl.lower(ast, optionsManager)
  } else {
    ast
  }

  if(interpreterOptions.showFirrtlAtLoad) {
    println("LoFirrtl" + "=" * 120)
    println(loweredAst.serialize)
  }

  val blackBoxFactories: Seq[BlackBoxFactory] = interpreterOptions.blackBoxFactories

  /**
    * turns on evaluator debugging. Can make output quite
    * verbose.
    *
    * @param value  The desired verbose setting
    */
  def setVerbose(value: Boolean = true): Unit = {
    Logger.setLevel(classOf[FirrtlTerp], LogLevel.Debug)
    //TODO: This is supposed to set verbose execution
  }

  val timer = new Timer

  val symbolTable: SymbolTable = timer("build symbol table") {
    SymbolTable(loweredAst, Seq.empty)
  }
  val dataStore = DataStore(numberOfBuffers = 2)
  symbolTable.allocateData(dataStore)
  val scheduler = new Scheduler(dataStore, symbolTable)
  val program = Program(symbolTable, dataStore, scheduler)

  val compiler = new ExpressionCompiler(program)

  timer("compile") {
    compiler.compile(loweredAst, blackBoxFactories)
  }

  println(s"Scheduler before sort ${scheduler.render}")
  scheduler.sortCombinationalAssigns()
  println(s"Scheduler after sort ${scheduler.render}")

  /**
    * Once a stop has occured, the intepreter will not allow pokes until
    * the stop has been cleared
    */
  def clearStop(): Unit = {lastStopResult = None}

  def makeVCDLogger(fileName: String, showUnderscored: Boolean): Unit = {
    //TODO: (chick) circuitState.makeVCDLogger(dependencyGraph, circuitState, fileName, showUnderscored)
  }
  def disableVCD(): Unit = {
    //TODO: (chick) circuitState.disableVCD()
  }
  def writeVCD(): Unit = {
    //TODO: (chick) circuitState.writeVCD()
  }

  logger.debug(s"symbol table size is ${symbolTable.size}, dataStore allocations ${dataStore.getSizes}")

  logger.debug(s"SymbolTable:\n${program.symbolTable.render}")

  setVerbose(interpreterOptions.setVerbose)

  var isStale: Boolean = false

  def getValue(name: String): Concrete = {
    assert(symbolTable.contains(name),
      s"Error: getValue($name) is not an element of this circuit")

    if(isStale) scheduler.makeFresh()

    val symbol = symbolTable(name)
    TypeInstanceFactory(symbol.firrtlType, dataStore(symbolTable(name))
    )
  }

  /**
    * This function used to show the calculation of all dependencies resolved to get value
    * @param name
    * @return
    */
  def getSpecifiedValue(name: String): Concrete = {
    //TODO: (chick) Show this in some other way
    assert(symbolTable.contains(name),
      s"Error: getValue($name) is not an element of this circuit")

    if(isStale) scheduler.makeFresh()

    val symbol = symbolTable(name)
    TypeInstanceFactory(symbol.firrtlType, dataStore(symbolTable(name))
    )
  }

  /**
    * Update the circuit state with the supplied information
    * @param name  name of value to set
    * @param value new concrete value
    * @param force allows setting components other than top level inputs
    * @param registerPoke changes which side of a register is poked
    * @return the concrete value that was derived from type and value
    */
  def setValue(name: String, value: Concrete, force: Boolean = true, registerPoke: Boolean = false): Concrete = {
    assert(symbolTable.contains(name))
    val symbol = symbolTable(name)

    if(!force) {
      assert(symbol.dataKind == PortKind,
        s"Error: setValue($name) not on input, use setValue($name, force=true) to override")
      if(checkStopped("setValue")) return Concrete.poisonedUInt(1)
    }

    dataStore(symbol) = value.value
    value
  }

  /**
    * Creates a concrete based on current circuit and the value and poisoned state
    * It uses the type of any existing value for name and if it can't find that it
    * looks up the type in the dependency graph
    * this handles setting SInts with negative values, from positive bigInts when sized appropriately
    * @param name  name of value to set
    * @param value new value
    * @return the concrete value that was derived from type and value
    */
  def makeConcreteValue(name: String, value: BigInt, poisoned: Boolean = false): Concrete = {
    //TODO: (chick) former poison functionality is not here right now
    val symbol = symbolTable(name)
    TypeInstanceFactory(symbol.firrtlType, dataStore(symbolTable(name)))
  }

  /**
    * Update the circuit state with the supplied information
    * @param name  name of value to set
    * @param value new value
    * @param force allows setting components other than top level inputs
    * @param registerPoke changes which side of a register is poked
    * @return the concrete value that was derived from type and value
    */
  def setValueWithBigInt(
      name: String, value: BigInt, force: Boolean = true, registerPoke: Boolean = false): Concrete = {
    assert(symbolTable.contains(name))
    val symbol = symbolTable(name)

    if(!force) {
      assert(symbol.dataKind == PortKind,
        s"Error: setValue($name) not on input, use setValue($name, force=true) to override")
      if(checkStopped("setValue")) return Concrete.poisonedUInt(1)
    }

    dataStore(symbol) = value
    makeConcreteValue(name, value)
  }

  def evaluateCircuit(specificDependencies: Seq[String] = Seq()): Unit = {
    program.dataStore.advanceBuffers()
    println(s"a --  ${program.dataInColumns}")
    program.scheduler.getTriggerExpressions.foreach { key => program.scheduler.executeTriggeredAssigns(key) }
    println(s"h --  ${program.header}")
    println(s"r --  ${program.dataInColumns}")
    program.scheduler.executeCombinational()
    println(s"c --  ${program.dataInColumns}")
  }

  def reEvaluate(name: String): Unit = {
    setVerbose()
    evaluateCircuit(Seq(name))
  }

  def checkStopped(attemptedCommand: String = "command"): Boolean = {
    if(stopped) {
      logger.debug(s"circuit has been stopped: ignoring $attemptedCommand")
    }
    stopped
  }

  def cycle(showState: Boolean = false): Unit = {
    //TODO: (chick) VCD stuff is missing from here
    logger.debug("interpreter cycle called " + "="*80)
    if(checkStopped("cycle")) return

    if(isStale) {
      logger.debug("interpreter cycle() called, state is stale, re-evaluate Circuit")
      logger.debug(program.dataInColumns)

      logger.debug(s"process reset")
      evaluateCircuit()
    }
    else {
      logger.debug(s"interpreter cycle() called, state is fresh")
    }

    for (elem <- blackBoxFactories) {
      elem.cycle()
    }


    if(showState) println(s"FirrtlTerp: next state computed ${"="*80}\n${program.dataInColumns}")
  }

  def doCycles(n: Int): Unit = {
    if(checkStopped(s"doCycles($n)")) return

    println(s"Initial state ${"-"*80}\n${program.dataInColumns}")

    for(cycle_number <- 1 to n) {
      println(s"Cycle $cycle_number ${"-"*80}")
      cycle()
      if(stopped) return
    }
  }

  def poke(name: String, value: Int): Unit = {
    val symbol = program.symbolTable(name)
    program.dataStore(symbol) = value
  }
  def peek(name: String): Big = {
    val symbol = program.symbolTable(name)
    program.dataStore(symbol)
  }

  def step(steps: Int = 1): Unit = {
    program.dataStore.advanceBuffers()
    println(s"a --  ${program.dataInColumns}")
    program.scheduler.getTriggerExpressions.foreach { key => program.scheduler.executeTriggeredAssigns(key) }
    println(s"h --  ${program.header}")
    println(s"r --  ${program.dataInColumns}")
    program.scheduler.executeCombinational()
    println(s"c --  ${program.dataInColumns}")
  }

  println(s"h --  ${program.header}")
  println(s"i --  ${program.dataInColumns}")

  poke("io_a", 33)
  poke("io_b", 11)
  poke("io_e", 1)

  println(s"p --  ${program.dataInColumns}")

  step()

  poke("io_e", 0)
  println(s"p --  ${program.dataInColumns}")
  step()

  var count = 0
//  while(peek("io_v") == 0 && count < 50 && peek("x") > 0) {
  while(/*peek("io_v") == 0 &&*/ count < 12) {
    count += 1
    step()
  }

  println(timer.report())
}

object Simulator {
  def apply(
      input: String,
      optionsManager: InterpreterOptionsManager = new InterpreterOptionsManager
  ): Simulator = {
    val ast = firrtl.Parser.parse(input.split("\n").toIterator)
    val circuit = new Simulator(ast, optionsManager)
    circuit
  }

  def main(args: Array[String]): Unit = {
    val fileName = args.headOption.getOrElse("GCD.fir")
    val text = io.Source.fromFile(fileName).getLines().mkString("\n")

    apply(text)
  }

}