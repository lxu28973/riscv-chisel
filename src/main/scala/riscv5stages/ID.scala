package riscv5stages

import chisel3._
import chisel3.stage.{ChiselGeneratorAnnotation, ChiselStage}
import chisel3.util._
//import riscv5stages.ControlSignal._
import riscv5stages.InstPat._

class IDEXBundle extends Bundle with Param {
  val aluA = Output(UInt(xlen.W))
  val aluB = Output(UInt(xlen.W))
  val aluOp = Output(UInt())
  val wb = Output(UInt())
  val sign = Output(Bool())
  val shift = Output(UInt(2.W))
  val memOp = Output(UInt())
  val rdInd = Output(UInt(5.W))
  val rs2 = Output(UInt(xlen.W))
  val csrOp = Output(UInt())
  val csrInd = Output(UInt(12.W))
}

class ID extends Module with Param {
  val io = IO(new Bundle() {
    val pc = Input(UInt(xlen.W))
    val inst = Input(UInt(ilen.W))
    val rs1 = Input(UInt(xlen.W))
    val rs2 = Input(UInt(xlen.W))
    val haz = Input(Bool())
    val idex = new IDEXBundle()
    val exp = Output(UInt())
    val toJumpOrBranch = Output(Bool())
    val aluA_sel = Output(UInt())
    val aluB_sel = Output(UInt())
    val jump = Output(Bool())
  })

  val toJumpOrBranch = Reg(Bool())
  val inst = Mux(RegNext(toJumpOrBranch), 0.U, io.inst)
  val instDecodeRes = ListLookup(inst, ControlSignal.instDefault, ControlSignal.instMap)
  val aluOp :: aluA :: aluB :: wb :: sign :: shift :: instT :: jump :: memOp ::  exp :: csrOp :: csrin :: Nil = instDecodeRes

  io.aluA_sel := aluA
  io.aluB_sel := aluB
  io.jump := jump =/= ControlSignal.nonj

  val csrUimm = Cat(0.U((xlen-5).W), inst(19,15))

  val immMap = Seq(
    ControlSignal.i -> Cat(Fill(21, inst(31)), inst(30,20)),
    ControlSignal.s -> Cat(Fill(21, inst(31)), inst(30,25), inst(11,8), inst(7)),
    ControlSignal.b -> Cat(Fill(20, inst(31)), inst(7), inst(30,25), inst(11,8), 0.U(1)),
    ControlSignal.u -> Cat(inst(31), inst(30,20), inst(19,12), Fill(12, 0.U(1.W))),
    ControlSignal.j -> Cat(Fill(12, inst(31)), inst(19,12), inst(20), inst(30,25), inst(24,21), 0.U(1.W))
  )

  val imm = MuxLookup(instT, inst, immMap)
//  printf("Print during simulation: imm is %b\n", imm)
//  printf("Print during simulation: instT is %b\n", instT)
//  printf("Print during simulation: ControlSignal.i is %b\n", ControlSignal.i)
//  printf("Print during simulation: inst is %b\n", inst)
//  printf("Print during simulation: immcat is %b\n", Cat(Fill(21, inst(31)), inst(30,20)))
//  printf("Print during simulation: inst3020 is %b\n", inst(30,20))
//  printf("\n")
  io.idex.aluA := RegNext(Mux(io.haz | toJumpOrBranch, 0.U, (Mux(csrin.asBool, csrUimm, Mux(aluA === ControlSignal.rs1, io.rs1, io.pc)))))
  io.idex.aluB := RegNext(Mux(io.haz | toJumpOrBranch, 0.U, (Mux(aluB === ControlSignal.rs2, io.rs2, imm))))
  io.idex.aluOp := RegNext(Mux(io.haz | toJumpOrBranch, 0.U, (aluOp)))
  io.idex.wb := RegNext(Mux(io.haz | toJumpOrBranch, 0.U, (wb)))
  io.idex.sign := RegNext(Mux(io.haz | toJumpOrBranch, 0.U, (sign.asBool())))
  io.idex.shift := RegNext(Mux(io.haz | toJumpOrBranch, 0.U, (shift)))
  io.idex.memOp := RegNext(Mux(io.haz | toJumpOrBranch, 0.U, (memOp)))
  io.idex.rdInd := RegNext(Mux(io.haz | toJumpOrBranch, 0.U, (inst(11,7))))
  io.idex.rs2 := RegNext(Mux(io.haz | toJumpOrBranch, 0.U, (io.rs2)))
  io.idex.csrOp := RegNext(Mux(io.haz | toJumpOrBranch, 0.U, csrOp))
  io.idex.csrInd := RegNext(Mux(io.haz | toJumpOrBranch, 0.U, inst(31,20)))

  io.exp := RegNext(exp)

  /*** BRANCH ***/
  val rsEq = (io.rs1 === io.rs2)
  val rsLt = (io.rs1 < io.rs2)
  val rsLtU = (io.rs1.asSInt < io.rs2.asSInt)
  val branchMap = Seq(
    ControlSignal.nonj -> false.B,
    ControlSignal.jal -> true.B,
    ControlSignal.beq -> rsEq,
    ControlSignal.bne -> !rsEq,
    ControlSignal.blt -> rsLt,
    ControlSignal.bltu -> rsLtU,
    ControlSignal.bge -> !rsLt,
    ControlSignal.bgeu -> !rsLtU
  )

  toJumpOrBranch := Mux(toJumpOrBranch, false.B, MuxLookup(jump, false.B, branchMap))
  io.toJumpOrBranch := toJumpOrBranch



}