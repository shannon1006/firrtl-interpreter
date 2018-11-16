package firrtl_interpreter
import org.scalatest.{FlatSpec, Matchers}
class example5tester extends FlatSpec with Matchers {
	val firrtlStr: String =
"""
circuit Example : 
  module Example : 
    input clock : Clock
    input reset : UInt<1>
    wire tmp0 : UInt<1>
    wire loopback : UInt<1>
    reg pipereg_0to1_n : UInt<1>, clock
    reg pipereg_1to2_n : UInt<1>, clock
    reg pipereg_2to3_n : UInt<1>, clock
    reg pipereg_3to4_n : UInt<1>, clock

    loopback <= pipereg_3to4_n
    pipereg_2to3_n <= mux(reset, UInt<1>(0), pipereg_1to2_n)
    pipereg_1to2_n <= mux(reset, UInt<1>(0), pipereg_0to1_n)
    tmp0 <= not(loopback)
    pipereg_0to1_n <= mux(reset, UInt<1>(0), tmp0)
    pipereg_3to4_n <= mux(reset, UInt<1>(0), pipereg_2to3_n)
""".stripMargin
	it should "run with InterpretedTester" in {
		val tester = new InterpretiveTester(firrtlStr)
		tester.poke("reset", 1)
		tester.step(1)
		tester.poke("reset", 0)
		var pipereg_0to1_n = List(0,1,1,1,1,0,0,0,0,1,1,1,1,0,0)
		var pipereg_1to2_n = List(0,0,1,1,1,1,0,0,0,0,1,1,1,1,0)
		var pipereg_2to3_n = List(0,0,0,1,1,1,1,0,0,0,0,1,1,1,1)
		var pipereg_3to4_n = List(0,0,0,0,1,1,1,1,0,0,0,0,1,1,1)
		for (i <- 0 to 15 - 1) {
			print("round " + i + "\n")
			tester.expect("pipereg_0to1_n", pipereg_0to1_n(i))
			tester.expect("pipereg_1to2_n", pipereg_1to2_n(i))
			tester.expect("pipereg_2to3_n", pipereg_2to3_n(i))
			tester.expect("pipereg_3to4_n", pipereg_3to4_n(i))
			tester.step(1)
		}
	}
}
