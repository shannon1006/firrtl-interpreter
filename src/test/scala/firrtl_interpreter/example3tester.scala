package firrtl_interpreter
import org.scalatest.{FlatSpec, Matchers}
class example3tester extends FlatSpec with Matchers {
	val firrtlStr: String =
"""
circuit Example : 
  module Example : 
    input clock : Clock
    input reset : UInt<1>
    output refund : UInt<1>
    input req_refund : UInt<1>
    input token_in : UInt<1>
    output dispense : UInt<1>
    wire tmp0 : UInt<1>
    wire tmp6 : UInt<1>
    wire tmp1 : UInt<1>
    wire tmp8 : UInt<1>
    wire tmp53 : UInt<1>
    wire tmp12 : UInt<1>
    wire tmp17 : UInt<1>
    wire tmp14 : UInt<1>
    wire tmp16 : UInt<1>
    wire tmp20 : UInt<1>
    wire tmp27 : UInt<1>
    wire tmp22 : UInt<1>
    wire tmp24 : UInt<1>
    wire tmp26 : UInt<1>
    wire tmp28 : UInt<1>
    wire tmp30 : UInt<1>
    wire tmp32 : UInt<1>
    wire tmp34 : UInt<1>
    wire tmp36 : UInt<1>
    wire tmp49 : UInt<3>
    wire tmp41 : UInt<1>
    wire tmp5 : UInt<1>
    wire tmp42 : UInt<1>
    wire tmp43 : UInt<1>
    wire tmp38 : UInt<1>
    wire tmp46 : UInt<3>
    node const_1_1 = UInt<3>(1)
    wire tmp47 : UInt<3>
    node const_3_3 = UInt<3>(3)
    wire tmp48 : UInt<3>
    node const_5_5 = UInt<3>(5)
    wire tmp4 : UInt<1>
    wire tmp50 : UInt<3>
    wire tmp52 : UInt<1>
    wire tmp10 : UInt<1>
    wire tmp7 : UInt<1>
    wire tmp18 : UInt<1>
    wire tmp11 : UInt<1>
    wire tmp13 : UInt<1>
    wire tmp15 : UInt<1>
    wire tmp19 : UInt<1>
    wire tmp21 : UInt<1>
    wire tmp23 : UInt<1>
    wire tmp25 : UInt<1>
    wire tmp29 : UInt<1>
    wire tmp39 : UInt<1>
    wire tmp31 : UInt<1>
    wire tmp33 : UInt<1>
    wire tmp35 : UInt<1>
    wire tmp37 : UInt<1>
    wire tmp2 : UInt<1>
    wire tmp40 : UInt<1>
    wire tmp45 : UInt<3>
    wire tmp44 : UInt<1>
    wire tmp3 : UInt<1>
    reg state : UInt<3>, clock
    wire tmp51 : UInt<3>
    node const_0_0 = UInt<3>(0)
    node const_2_2 = UInt<3>(2)
    node const_4_4 = UInt<3>(4)
    wire tmp9 : UInt<1>

    tmp0 <= eq(state, const_0_0)
    tmp48 <= mux(tmp17, const_3_3, tmp47)
    tmp40 <= or(tmp38, tmp39)
    tmp31 <= and(tmp29, tmp30)
    tmp16 <= and(tmp14, tmp15)
    refund <= tmp53
    tmp53 <= eq(state, const_5_5)
    tmp50 <= mux(tmp37, const_5_5, tmp49)
    tmp22 <= and(tmp20, tmp21)
    tmp51 <= mux(tmp44, const_0_0, tmp50)
    tmp34 <= not(tmp10)
    tmp11 <= not(req_refund)
    tmp33 <= and(tmp31, tmp32)
    tmp20 <= and(tmp19, token_in)
    tmp36 <= not(tmp18)
    tmp28 <= not(req_refund)
    tmp37 <= and(tmp35, tmp36)
    tmp39 <= eq(state, const_5_5)
    tmp49 <= mux(tmp27, const_4_4, tmp48)
    tmp13 <= not(tmp0)
    tmp52 <= eq(state, const_4_4)
    tmp3 <= and(tmp2, tmp0)
    tmp25 <= not(tmp10)
    tmp47 <= mux(tmp9, const_2_2, tmp46)
    tmp32 <= not(tmp4)
    tmp4 <= eq(state, const_1_1)
    tmp18 <= eq(state, const_3_3)
    tmp2 <= and(tmp1, token_in)
    tmp38 <= eq(state, const_4_4)
    tmp44 <= and(tmp43, tmp40)
    tmp27 <= and(tmp26, tmp18)
    dispense <= tmp52
    tmp46 <= mux(tmp3, const_1_1, tmp45)
    tmp6 <= and(tmp5, token_in)
    tmp45 <= mux(req_refund, const_5_5, state)
    tmp15 <= not(tmp4)
    tmp24 <= and(tmp22, tmp23)
    tmp7 <= not(tmp0)
    tmp21 <= not(tmp0)
    tmp43 <= and(tmp41, tmp42)
    tmp23 <= not(tmp4)
    tmp14 <= and(tmp12, tmp13)
    tmp10 <= eq(state, const_2_2)
    tmp41 <= not(req_refund)
    tmp29 <= and(tmp28, token_in)
    tmp1 <= not(req_refund)
    tmp35 <= and(tmp33, tmp34)
    tmp8 <= and(tmp6, tmp7)
    tmp30 <= not(tmp0)
    tmp5 <= not(req_refund)
    tmp42 <= not(token_in)
    tmp12 <= and(tmp11, token_in)
    tmp17 <= and(tmp16, tmp10)
    state <= mux(reset, UInt<3>(0), tmp51)
    tmp19 <= not(req_refund)
    tmp26 <= and(tmp24, tmp25)
    tmp9 <= and(tmp8, tmp4)
""".stripMargin
	it should "run with InterpretedTester" in {
		val tester = new InterpretiveTester(firrtlStr)
		tester.poke("reset", 1)
		tester.step(1)
		tester.poke("reset", 0)
		var req_refund = List(1,1,0,0,0,1,0,0,0,0,0,0,0,0,0,0)
		var token_in = List(0,0,1,0,1,0,0,1,1,1,0,1,0,0,0,0)
		var dispense = List(0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0)
		var state = List(0,5,5,5,0,1,5,0,1,2,3,3,4,0,0,0)
		var refund = List(0,1,1,1,0,0,1,0,0,0,0,0,0,0,0,0)
		for (i <- 0 to 16 - 1) {
			print("round " + i + "\n")
			tester.poke("req_refund", req_refund(i))
			tester.poke("token_in", token_in(i))
			tester.expect("dispense", dispense(i))
			tester.expect("refund", refund(i))
			tester.expect("state", state(i))
			tester.step(1)
		}
	}
}
