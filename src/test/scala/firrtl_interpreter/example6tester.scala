package firrtl_interpreter
import org.scalatest.{FlatSpec, Matchers}
class example6tester extends FlatSpec with Matchers {
	val firrtlStr: String =
"""
circuit Example : 
  module Example : 
    input clock : Clock
    input reset : UInt<1>
    input waddr : UInt<3>
    input wdata : UInt<32>
    input we : UInt<1>
    input raddr : UInt<3>
    output rdata2 : UInt<32>
    output rdata1 : UInt<32>
    output validate : UInt<1>
    wire tmp1 : UInt<32>
    wire tmp9 : UInt<1>
    wire tmp0 : UInt<32>
    node const_0_1 = UInt<1>(1)
    wire tmp4 : UInt<4>
    node const_1_0 = UInt<1>(0)
    wire tmp2 : UInt<2>
    reg count : UInt<3>, clock
    wire tmp3 : UInt<3>
    wire tmp7 : UInt<4>
    node const_2_0 = UInt<1>(0)
    wire tmp5 : UInt<1>
    wire tmp6 : UInt<4>
    wire tmp8 : UInt<3>

    tmp7 <= mux(we, tmp4, tmp6)
    cmem mem_0 : UInt<32>[8]
    infer mport T_0  = mem_0[raddr], clock
    tmp0 <= T_0
    cmem mem_1 : UInt<32>[8]
    infer mport T_1  = mem_1[raddr], clock
    tmp1 <= T_1
    rdata2 <= tmp1
    validate <= tmp9
    tmp8 <= bits(tmp7, 2, 0)
    rdata1 <= tmp0
    when we :
      infer mport T_2  = mem_1[count], clock
      T_2 <= wdata
      skip
    tmp2 <= bits(const_1_0, 0, 0)
    when we :
      infer mport T_3  = mem_0[waddr], clock
      T_3 <= wdata
      skip
    tmp6 <= cat(tmp5, count)
    tmp3 <= cat(tmp2, const_0_1)
    tmp5 <= bits(const_2_0, 0, 0)
    tmp9 <= eq(waddr, count)
    count <= mux(reset, UInt<3>(0), tmp8)
    tmp4 <= add(count, tmp3)
""".stripMargin
	it should "run with InterpretedTester" in {
		val tester = new InterpretiveTester(firrtlStr)
		tester.poke("reset", 1)
		tester.step(1)
		tester.poke("reset", 0)
		var wdata = List(0,0,1,2,3,4,5,6,7,8,9,9,9,0,0,0,0,0,0,0,0,0,0,0,0,0)
		var we = List(0,0,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0)
		var rdata2 = List(0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,2,3,4,5,6,7,8,8,8)
		var raddr = List(0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,2,3,4,5,6,7,7,7)
		var waddr = List(0,0,0,1,2,3,4,5,6,7,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0)
		var count = List(0,0,0,1,2,3,4,5,6,7,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0)
		var rdata1 = List(0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,2,3,4,5,6,7,8,8,8)
		var validate = List(1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1)
		for (i <- 0 to 26 - 1) {
			print("round " + i + "\n")
			tester.poke("wdata", wdata(i))
			tester.poke("we", we(i))
			tester.poke("waddr", waddr(i))
			tester.poke("raddr", raddr(i))
			tester.expect("validate", validate(i))
			tester.expect("rdata2", rdata2(i))
			tester.expect("rdata1", rdata1(i))
			tester.expect("count", count(i))
			tester.step(1)
		}
	}
}
