package riscv5stages

import chisel3._

class Top extends Module{
  val io = IO(new Bundle() {
    val iMemWrAddr = Input(UInt(32.W))
    val iMemWrData = Input(UInt(32.W))
    val iMemWrEn = Input(Bool())
    val inst = Output(UInt(32.W))
    val jpc = Input(UInt(32.W))
    val haz = Input(Bool())
    val jump = Input(Bool())
  })

  val iMem = Module(new Mem1r1w)
  val iF = Module(new IF)

  iMem.io.rdAddr := iF.io.pc
  io.inst := iMem.io.rdData
  iF.io.hazard := io.haz
  iF.io.jpc := io.jpc
  iF.io.jumpOrBranch := io.jump

  iMem.io.wrData := io.iMemWrData
  iMem.io.wrAddr := io.iMemWrAddr
  iMem.io.wrEna := io.iMemWrEn
}
