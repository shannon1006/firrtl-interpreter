package firrtl_interpreter
import org.scalatest.{FlatSpec, Matchers}
class example6ROMtester extends FlatSpec with Matchers {
	val firrtlStr: String =
"""
circuit Example : 
  module Example : 
    input clock : Clock
    input reset : UInt<1>
    output rom_out_1 : UInt<5>
    output rom_out_2 : UInt<5>
    output rom_out_3 : UInt<5>
    output cmp_out : UInt<1>
    input rom_in_2 : UInt<4>
    input rom_in : UInt<4>
    wire tmp14 : UInt<5>
    wire tmp15 : UInt<1>
    wire tmp12 : UInt<5>
    wire tmp13 : UInt<5>

    cmp_out <= tmp15
    rom_out_1 <= tmp13
    wire tmp11 : UInt<5>[16]
    tmp11[0] <= UInt<5>(31)
    tmp11[1] <= UInt<5>(29)
    tmp11[2] <= UInt<5>(27)
    tmp11[3] <= UInt<5>(25)
    tmp11[4] <= UInt<5>(23)
    tmp11[5] <= UInt<5>(21)
    tmp11[6] <= UInt<5>(19)
    tmp11[7] <= UInt<5>(17)
    tmp11[8] <= UInt<5>(15)
    tmp11[9] <= UInt<5>(13)
    tmp11[10] <= UInt<5>(11)
    tmp11[11] <= UInt<5>(9)
    tmp11[12] <= UInt<5>(7)
    tmp11[13] <= UInt<5>(5)
    tmp11[14] <= UInt<5>(3)
    tmp11[15] <= UInt<5>(1)
    tmp12 <= tmp11[rom_in_2]
    rom_out_2 <= tmp14
    tmp15 <= eq(tmp13, tmp14)
    tmp14 <= tmp11[rom_in]
    wire tmp10 : UInt<5>[16]
    tmp10[0] <= UInt<5>(31)
    tmp10[1] <= UInt<5>(29)
    tmp10[2] <= UInt<5>(27)
    tmp10[3] <= UInt<5>(25)
    tmp10[4] <= UInt<5>(23)
    tmp10[5] <= UInt<5>(21)
    tmp10[6] <= UInt<5>(19)
    tmp10[7] <= UInt<5>(17)
    tmp10[8] <= UInt<5>(15)
    tmp10[9] <= UInt<5>(13)
    tmp10[10] <= UInt<5>(11)
    tmp10[11] <= UInt<5>(9)
    tmp10[12] <= UInt<5>(7)
    tmp10[13] <= UInt<5>(5)
    tmp10[14] <= UInt<5>(3)
    tmp10[15] <= UInt<5>(1)
    tmp13 <= tmp10[rom_in]
    rom_out_3 <= tmp12
""".stripMargin
	it should "run with InterpretedTester" in {
		val tester = new InterpretiveTester(firrtlStr)
		tester.poke("reset", 1)
		tester.step(1)
		tester.poke("reset", 0)
		var rom_in_2 = List(13,0,10,6,1,5,8,0,2,12,1,0,14,15,8,5,10,3,4,5)
		var rom_in = List(1,11,4,2,7,8,2,4,5,13,15,3,4,4,4,8,12,13,2,1)
		var rom_out_1 = List(29,9,23,27,17,15,27,23,21,5,1,25,23,23,23,15,7,5,27,29)
		var rom_out_2 = List(29,9,23,27,17,15,27,23,21,5,1,25,23,23,23,15,7,5,27,29)
		var rom_out_3 = List(5,31,11,19,29,21,15,31,27,7,29,31,3,1,15,21,11,25,23,21)
		var cmp_out = List(1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1)
		for (i <- 0 to 20 - 1) {
			print("round " + i + "\n")
			tester.poke("rom_in_2", rom_in_2(i))
			tester.poke("rom_in", rom_in(i))
			tester.expect("rom_out_1", rom_out_1(i))
			tester.expect("rom_out_2", rom_out_2(i))
			tester.expect("cmp_out", cmp_out(i))
			tester.expect("rom_out_3", rom_out_3(i))
			tester.step(1)
		}
	}
}
