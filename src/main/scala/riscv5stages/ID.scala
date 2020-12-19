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
  val aluOp :: aluA :: aluB :: wb :: sign :: shift :: instT :: jump :: memOp ::  exp :: Nil = instDecodeRes

  io.aluA_sel := aluA
  io.aluB_sel := aluB
  io.jump := jump =/= ControlSignal.nonj

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
  io.idex.aluA := RegNext(Mux(io.haz | toJumpOrBranch, 0.U, (Mux(aluA === ControlSignal.rs1, io.rs1, io.pc))))
  io.idex.aluB := RegNext(Mux(io.haz | toJumpOrBranch, 0.U, (Mux(aluB === ControlSignal.rs2, io.rs2, imm))))
  io.idex.aluOp := RegNext(Mux(io.haz | toJumpOrBranch, 0.U, (aluOp)))
  io.idex.wb := RegNext(Mux(io.haz | toJumpOrBranch, 0.U, (wb)))
  io.idex.sign := RegNext(Mux(io.haz | toJumpOrBranch, 0.U, (sign.asBool())))
  io.idex.shift := RegNext(Mux(io.haz | toJumpOrBranch, 0.U, (shift)))
  io.idex.memOp := RegNext(Mux(io.haz | toJumpOrBranch, 0.U, (memOp)))
  io.idex.rdInd := RegNext(Mux(io.haz | toJumpOrBranch, 0.U, (inst(11,7))))
  io.idex.rs2 := RegNext(Mux(io.haz | toJumpOrBranch, 0.U, (io.rs2)))

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






//
//class Mcsa extends Module {
//  val io = IO(new Bundle() {
//    val a = Input(UInt(8.W))
//    val b = Input(UInt(8.W))
//    val c = Output(UInt(16.W))
//  })
//
//  import io._
//  val pa1, pa2 = Wire(Vec(8, UInt(8.W)))
//
//  for (i <- 0 to 7) {
//    pa1(i) := a & Fill(8, b(i))
//  }
//
//  pa2(0) := pa1(0)
//  for (i <- 1 to 7) {
//    pa2(i) := pa2(i - 1) + pa1(i)
//  }
//
//  c := pa2(7)
//}
//
//
//class HA extends Module {
//  val io = IO(new Bundle(){
//    val in1 = Input(UInt(1.W))
//    val in2 = Input(UInt(1.W))
//    val c   = Output(UInt(1.W))
//    val s   = Output(UInt(1.W))
//  })
//  import io._
//  val res = in1 +& in2
//  c := res(1)
//  s := res(0)
//}
//
//class FA extends Module {
//  val io = IO(new Bundle(){
//    val in1 = Input(UInt(1.W))
//    val in2 = Input(UInt(1.W))
//    val ci  = Input(UInt(1.W))
//    val co  = Output(UInt(1.W))
//    val s   = Output(UInt(1.W))
//  })
//  import io._
//  val res = Wire(UInt(2.W))
//  res := in1 +& in2
//  co := res(1)
//  s := res(0)
//}
//
//object GenVerilog extends App {
//  Driver.execute(args, () => new HA)
//}
