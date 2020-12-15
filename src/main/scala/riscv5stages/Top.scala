package riscv5stages

import chisel3._
import riscv5stages.ControlSignal._

class Top(implicit p: Param) extends Module{
  val io = IO(new Bundle() {
    val iMemWrAddr = Input(UInt(32.W))
    val iMemWrData = Input(UInt(32.W))
    val iMemWrEn = Input(Bool())
    val haz = Input(Bool())
    val jpc = Input(UInt(32.W))

  })

  val iMem = Module(new Mem1r1w)
  val iF = Module(new IF)
  val iD = Module(new ID)
//  val inst = Output(UInt(32.W))
//  val jump = Input(Bool())
  iF.io.hazard := io.haz
  iF.io.jpc := io.jpc

  iMem.io.rAddr := iF.io.pc
  iD.io.inst := iMem.io.rData
  iD.io.pc := iF.io.pc
//  val jump = Wire(UInt())
//  jump := iD.io.jump
  iF.io.jumpOrBranch := (iD.io.jump =/= nonj)

  iMem.io.wData := io.iMemWrData
  iMem.io.wAddr := io.iMemWrAddr
  iMem.io.wEn := io.iMemWrEn
}
