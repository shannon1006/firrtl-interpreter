// See LICENSE for license details.
package firrtl_interpreter

import org.scalatest.{FlatSpec, Matchers}

// scalastyle:off magic.number

class MemoryTester extends FlatSpec with Matchers {

  /*val counterFirrtl: String =
    """
      |circuit Example :
      |  module Example :
      |    input clock : Clock
      |    input reset : UInt<1>
      |    input we : UInt<1>
      |    input raddr : UInt<3>
      |    input waddr : UInt<3>
      |    input wdata : UInt<32>
      |    output validate : UInt<1>
      |    output rdata2 : UInt<32>
      |    output rdata1 : UInt<32>
      |    node const_0_1 = UInt<1>(1)
      |    wire tmp6 : UInt<4>
      |    wire tmp8 : UInt<3>
      |    wire tmp2 : UInt<2>
      |    node const_2_0 = UInt<1>(0)
      |    wire tmp9 : UInt<1>
      |    wire tmp3 : UInt<3>
      |    wire tmp4 : UInt<4>
      |    wire tmp0 : UInt<32>
      |    wire tmp7 : UInt<4>
      |    node const_1_0 = UInt<1>(0)
      |    reg count : UInt<3>, clock
      |    wire tmp1 : UInt<32>
      |    wire tmp5 : UInt<1>
      |
      |    cmem mem_0 : UInt<32>[8]
      |    infer mport T_0  = mem_0[raddr], clock
      |    tmp0 <= T_0
      |    cmem mem_1 : UInt<32>[8]
      |    when we :
      |      infer mport T_1  = mem_1[count], clock
      |      T_1 <= wdata
      |      skip
      |    validate <= tmp9
      |    tmp6 <= cat(tmp5, count)
      |    count <= mux(reset, UInt<3>(0), tmp8)
      |    rdata2 <= tmp1
      |    rdata1 <= tmp0
      |    tmp2 <= bits(const_1_0, 0, 0)
      |    tmp5 <= bits(const_2_0, 0, 0)
      |    tmp7 <= mux(we, tmp4, tmp6)
      |    tmp4 <= add(count, tmp3)
      |    tmp8 <= bits(tmp7, 2, 0)
      |    infer mport T_2  = mem_1[raddr], clock
      |    tmp1 <= T_2
      |    tmp9 <= eq(waddr, count)
      |    tmp3 <= cat(tmp2, const_0_1)
      |    when we :
      |      infer mport T_3  = mem_0[waddr], clock
      |      T_3 <= wdata
      |      skip
      |
    """.stripMargin

  it should "run with InterpretedTester" in {
    val tester = new InterpretiveTester(counterFirrtl)
    // interpreter.setVerbose()

    tester.poke("reset", 1)
    tester.step(1)
    tester.poke("reset", 0)
    //tester.step(1)
    var we = List(0,0,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0)
    var waddr = List(0,0,0,1,2,3,4,5,6,7,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0)
    var wdata = List(0,0,1,2,3,4,5,6,7,8,9,9,9,0,0,0,0,0,0,0,0,0,0,0,0,0)
    var raddr = List(0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,2,3,4,5,6,7,7,7)
    var rdata1 = List(0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,2,3,4,5,6,7,8,8,8)
    var rdata2 = List(0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,2,3,4,5,6,7,8,8,8)
    val len = we.length
    for (i <- 0 to len - 1) {
      print("round " + i + "\n")
      tester.poke("we", we(i))
      tester.poke("waddr", waddr(i))
      tester.poke("wdata", wdata(i))
      tester.poke("raddr", raddr(i))
      if (i > 2) {
        tester.expect("rdata1", rdata1(i))
        tester.expect("rdata2", rdata2(i))
      }
      tester.step(1)
    }
  }*/

  val romFirrtl: String =
    """
      |circuit Example :
      |  module Example :
      |    input clock : Clock
      |    input reset : UInt<1>
      |    input io_rom_in : UInt<4>
      |    input io_rom_in_2 : UInt<4>
      |    output io_rom_out_3 : UInt<5>
      |    output io_rom_out_2 : UInt<5>
      |    output io_rom_out_1 : UInt<5>
      |    output io_cmp_out : UInt<1>
      |
      |    cmem tmp11_3 : UInt<5>[16]
      |    infer mport _T_16 = tmp11_3[io_rom_in], clock
      |    node _T_15 = _T_16
      |    cmem tmp10_2 : UInt<5>[16]
      |    infer mport _T_18 = tmp10_2[io_rom_in], clock
      |    node _T_17 = _T_18
      |    infer mport _T_20 = tmp11_3[io_rom_in_2], clock
      |    node _T_19 = _T_20
      |    io_rom_out_3 <= _T_19
      |    io_rom_out_2 <= _T_15
      |    node _T_21 = eq(_T_17, _T_15)
      |    io_rom_out_1 <= _T_17
      |    io_cmp_out <= _T_21
    """.stripMargin

  it should "run with InterpretedTester" in {
    val tester = new InterpretiveTester(romFirrtl)
    // interpreter.setVerbose()

    tester.poke("reset", 1)
    tester.step(1)
    tester.poke("reset", 0)
    //tester.step(1)
    var rom_in = List(1, 11, 4, 2, 7, 8, 2, 4, 5, 13, 15, 3, 4, 4, 4, 8, 12, 13, 2, 1)
    var rom_in_2 = List(1,12,5,3,2,11,0,0,5,12,10,3,0,7,7,1,10,14,7,8)
    var rom_out_1 = List(29,9,23,27,17,15,27,23,21,5,1,25,23,23,23,15,7,5,27,29)
    var rom_out_2 = List(29,9,23,27,17,15,27,23,21,5,1,25,23,23,23,15,7,5,27,29)
    var rom_out_3 = List(29,7,21,25,27,9,31,31,21,7,11,25,31,17,17,29,9,3,17,15)
    val len = rom_in.length
    for (i <- 0 to len - 1) {
      print("round " + i + "\n")
      tester.poke("io_rom_in", rom_in(i))
      tester.poke("io_rom_in_2", rom_in_2(i))
      tester.expect("io_rom_out_1", rom_out_1(i))
      tester.expect("io_rom_out_2", rom_out_2(i))
      tester.expect("io_rom_out_3", rom_out_3(i))
      //print(tester.peek("io_rom_out_1"))
      tester.step(1)
    }
  }
}
