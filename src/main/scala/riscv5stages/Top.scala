package riscv5stages

import chisel3._
import riscv5stages.ControlSignal._

class Top extends Module with Param {
  val io = IO(new Bundle() {
    val iMemWrAddr = Input(UInt(32.W))
    val iMemWrData = Input(UInt(32.W))
    val iMemWrEn = Input(Bool())
    val haz = Input(Bool())
    val wen = Input(Bool())
    val wdata = Input(UInt(xlen.W))
    val waddr = Input(UInt(xlen.W))
    val exp = Output(UInt())
    val memwb = new MEMWBBundle
  })

  val iMem = Module(new Mem1r1w)
  val dMem = Module(new MemHasMask)
  val iF = Module(new IF)
  val iD = Module(new ID)
  val eX = Module(new EX)
  val mEM = Module(new MEM)
  val regFile = Module(new RegFile)

  iF.io.hazard := io.haz
  iF.io.jpc := eX.io.exmem.eXout
  iF.io.toJumpOrBranch := iD.io.toJumpOrBranch

  iD.io.inst := iMem.io.rData
  iD.io.pc := iF.io.pc
  iD.io.rs1 := regFile.io.rdata1
  iD.io.rs2 := regFile.io.rdata2
  io.exp := iD.io.exp

  eX.io.idex <> iD.io.idex

  mEM.io.exmem <>  eX.io.exmem
  mEM.io.memio <> dMem.io
  mEM.io.memwb <> io.memwb

  iMem.io.rAddr := iF.io.pc
  iMem.io.wData := io.iMemWrData
  iMem.io.wAddr := io.iMemWrAddr
  iMem.io.wEn := io.iMemWrEn

  regFile.io.raddr1 := iMem.io.rData(19,15)
  regFile.io.raddr2 := iMem.io.rData(24,20)
  regFile.io.wen := io.wen
  regFile.io.waddr := io.waddr
  regFile.io.wdata := io.wdata
}
