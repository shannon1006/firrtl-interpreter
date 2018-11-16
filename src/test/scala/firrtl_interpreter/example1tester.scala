package firrtl_interpreter
import org.scalatest.{FlatSpec, Matchers}
class example1tester extends FlatSpec with Matchers {
	val firrtlStr: String =
"""
circuit Example : 
  module Example : 
    input clock : Clock
    input reset : UInt<1>
    input a : UInt<1>
    output carry_out : UInt<1>
    input c : UInt<1>
    output sum : UInt<1>
    input b : UInt<1>
    wire tmp6 : UInt<1>
    wire temp1 : UInt<1>
    wire tmp0 : UInt<1>
    wire tmp1 : UInt<1>
    wire tmp2 : UInt<1>
    wire tmp4 : UInt<1>
    wire tmp5 : UInt<1>
    wire tmp3 : UInt<1>
    wire tmp7 : UInt<1>

    tmp6 <= or(temp1, tmp0)
    tmp7 <= or(tmp6, tmp5)
    tmp5 <= and(b, c)
    tmp2 <= xor(tmp1, c)
    tmp4 <= and(a, c)
    sum <= tmp2
    temp1 <= tmp3
    carry_out <= tmp7
    tmp0 <= tmp4
    tmp1 <= xor(a, b)
    tmp3 <= and(a, b)
""".stripMargin
	it should "run with InterpretedTester" in {
		val tester = new InterpretiveTester(firrtlStr)
		tester.poke("reset", 1)
		tester.step(1)
		tester.poke("reset", 0)
		var a = List(0,0,0,1,1,1,0,1,0,0,1,1,1,1,1)
		var c = List(1,1,0,1,0,1,0,0,1,0,0,1,0,0,0)
		var carry_out = List(0,1,0,1,1,1,0,1,1,0,1,1,0,0,1)
		var sum = List(1,0,1,1,0,0,0,0,0,0,0,1,1,1,0)
		var b = List(0,1,1,1,1,0,0,1,1,0,1,1,0,0,1)
		for (i <- 0 to 15 - 1) {
			print("round " + i + "\n")
			tester.poke("a", a(i))
			tester.poke("b", b(i))
			tester.poke("c", c(i))
			tester.expect("carry_out", carry_out(i))
			tester.expect("sum", sum(i))
			tester.step(1)
		}
	}
}
