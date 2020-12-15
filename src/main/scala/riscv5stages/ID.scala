package riscv5stages

import chisel3._
import chisel3.stage.{ChiselGeneratorAnnotation, ChiselStage}
import chisel3.util._
//import riscv5stages.ControlSignal._
import riscv5stages.InstPat._


class ID(implicit p: Param) extends Module{
  val io = IO(new Bundle() {
    val pc = Input(UInt(p.xlen.W))
    val inst = Input(UInt(p.ilen.W))
    val rs1 = Input(UInt(p.xlen.W))
    val rs2 = Input(UInt(p.xlen.W))
    val aluA = Output(UInt(p.xlen.W))
    val aluB = Output(UInt(p.xlen.W))
    val aluOp = Output(UInt())
    val writeWhat = Output(UInt(2.W))
    val sign = Output(Bool())
    val shift = Output(UInt(2.W))
    val jump = Output(UInt())
    val memOp = Output(UInt())
    val exp = Output(UInt())
  })


  val instDecodeRes = MuxLookup(io.inst, ControlSignal.instDefault, ControlSignal.instMap)

  val immMap = Seq(
    ControlSignal.i -> Cat(Fill(21, io.inst(31)), io.inst(30,20)),
    ControlSignal.s -> Cat(Fill(21, io.inst(31)), io.inst(30,25), io.inst(11,8), io.inst(7)),
    ControlSignal.b -> Cat(Fill(20, io.inst(31)), io.inst(7), io.inst(30,25), io.inst(11,8), 0.U(1)),
    ControlSignal.u -> Cat(io.inst(31), io.inst(30,20), io.inst(19,12), Fill(12, 0.U(1.W))),
    ControlSignal.i -> Cat(Fill(12, io.inst(31)), io.inst(19,12), io.inst(20), io.inst(30,25), io.inst(24,21), 0.U(1.W))
  )

  val imm = MuxLookup(instDecodeRes(7), io.inst, immMap)
  io.aluA := Mux(instDecodeRes(2) === ControlSignal.rs1, io.rs1, io.pc)
  io.aluB := Mux(instDecodeRes(3) === ControlSignal.rs2, io.rs2, imm)
  io.aluOp := instDecodeRes(1)
  io.writeWhat := instDecodeRes(4)
  io.sign = instDecodeRes()
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
