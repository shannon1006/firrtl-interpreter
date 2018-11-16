package firrtl_interpreter
import org.scalatest.{FlatSpec, Matchers}
class example2tester extends FlatSpec with Matchers {
	val firrtlStr: String =
"""
circuit Example : 
  module Example : 
    input clock : Clock
    input reset : UInt<1>
    node const_3_0 = UInt<1>(0)
    wire tmp7 : UInt<1>
    wire tmp10 : UInt<1>
    wire tmp0 : UInt<2>
    wire tmp11 : UInt<2>
    wire tmp12 : UInt<2>
    node const_4_0 = UInt<1>(0)
    wire tmp14 : UInt<1>
    wire tmp15 : UInt<1>
    wire tmp16 : UInt<1>
    wire tmp17 : UInt<1>
    wire tmp18 : UInt<1>
    wire tmp19 : UInt<1>
    wire tmp20 : UInt<1>
    node const_0_1 = UInt<1>(1)
    wire tmp13 : UInt<1>
    wire tmp23 : UInt<1>
    wire tmp24 : UInt<1>
    wire tmp25 : UInt<1>
    wire tmp26 : UInt<1>
    wire tmp27 : UInt<1>
    wire tmp28 : UInt<1>
    wire tmp29 : UInt<1>
    wire tmp30 : UInt<1>
    wire tmp31 : UInt<2>
    wire tmp32 : UInt<3>
    wire tmp5 : UInt<1>
    wire tmp8 : UInt<1>
    wire tmp9 : UInt<1>
    reg counter : UInt<3>, clock
    wire tmp2 : UInt<1>
    node const_1_0 = UInt<1>(0)
    wire tmp21 : UInt<1>
    wire tmp1 : UInt<3>
    wire tmp3 : UInt<1>
    wire tmp4 : UInt<1>
    node const_2_0 = UInt<1>(0)
    wire tmp22 : UInt<1>
    wire tmp6 : UInt<1>

    tmp11 <= bits(counter, 2, 1)
    tmp31 <= cat(tmp25, tmp16)
    tmp19 <= or(tmp17, tmp18)
    tmp12 <= bits(tmp1, 2, 1)
    tmp20 <= and(tmp14, tmp10)
    tmp32 <= cat(tmp31, tmp5)
    tmp14 <= bits(tmp12, 0, 0)
    tmp8 <= or(tmp6, tmp7)
    tmp22 <= bits(tmp11, 1, 1)
    tmp2 <= bits(counter, 0, 0)
    tmp28 <= or(tmp26, tmp27)
    tmp26 <= and(tmp22, tmp23)
    tmp5 <= xor(tmp4, const_2_0)
    tmp7 <= and(tmp2, const_3_0)
    tmp3 <= bits(tmp1, 0, 0)
    tmp29 <= and(tmp23, tmp21)
    tmp27 <= and(tmp22, tmp21)
    tmp1 <= cat(tmp0, const_0_1)
    tmp17 <= and(tmp13, tmp14)
    tmp0 <= bits(const_1_0, 0, 0)
    tmp10 <= or(tmp8, tmp9)
    tmp4 <= xor(tmp2, tmp3)
    tmp30 <= or(tmp28, tmp29)
    tmp9 <= and(tmp3, const_4_0)
    tmp23 <= bits(tmp12, 1, 1)
    tmp16 <= xor(tmp15, tmp10)
    tmp24 <= xor(tmp22, tmp23)
    tmp25 <= xor(tmp24, tmp21)
    tmp6 <= and(tmp2, tmp3)
    tmp13 <= bits(tmp11, 0, 0)
    tmp18 <= and(tmp13, tmp10)
    tmp21 <= or(tmp19, tmp20)
    counter <= mux(reset, UInt<3>(0), tmp32)
    tmp15 <= xor(tmp13, tmp14)
""".stripMargin
	it should "run with InterpretedTester" in {
		val tester = new InterpretiveTester(firrtlStr)
		tester.poke("reset", 1)
		tester.step(1)
		tester.poke("reset", 0)
		var counter = List(0,1,2,3,4,5,6,7,0,1,2,3,4,5,6,7,0,1,2,3)
		for (i <- 0 to 20 - 1) {
			print("round " + i + "\n")
			tester.expect("counter", counter(i))
			tester.step(1)
		}
	}
}
