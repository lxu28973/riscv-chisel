package riscv5stages

import chisel3._
import riscv5stages.ControlSignal._

class Top extends Module with Param {
  val io = IO(new Bundle() {
    val iMemWrAddr = Input(UInt(32.W))
    val iMemWrData = Input(UInt(32.W))
    val iMemWrEn = Input(Bool())
    val exp = Output(UInt(2.W))
  })

  val iMem = Module(new Mem1r1w)
  val dMem = Module(new MemHasMask)
  val iF = Module(new IF)
  val iD = Module(new ID)
  val eX = Module(new EX)
  val mEM = Module(new MEM)
  val regFile = Module(new RegFile)
  val hazard = Wire(Bool())

  iF.io.hazard := hazard
  iF.io.jpc := eX.io.pcForward
  iF.io.toJumpOrBranch := iD.io.toJumpOrBranch

  iD.io.inst := iMem.io.rData
  iD.io.pc := RegNext(iF.io.pc)
  iD.io.rs1 := regFile.io.rdata1
  iD.io.rs2 := regFile.io.rdata2
  iD.io.haz := hazard

  eX.io.idex <> iD.io.idex

  mEM.io.exmem <>  eX.io.exmem
  mEM.io.memio <> dMem.io
  io.exp := mEM.io.exmem.exp

  iMem.io.rAddr := Mux(hazard, iF.io.pc - 4.U, iF.io.pc)
  iMem.io.wData := io.iMemWrData
  iMem.io.wAddr := io.iMemWrAddr
  iMem.io.wEn := io.iMemWrEn

  regFile.io.raddr1 := iMem.io.rData(19,15)
  regFile.io.raddr2 := iMem.io.rData(24,20)
  regFile.io.wen := mEM.io.memwb.wb
  regFile.io.waddr := mEM.io.memwb.rdInd
  regFile.io.wdata := mEM.io.memwb.data

  /*** Hazard Detect ***/
  val rs1Ind = iMem.io.rData(19,15)
  val rs2Ind = iMem.io.rData(24,20)

  when((((rs1Ind === iD.io.idex.rdInd && (iD.io.aluA_sel === ControlSignal.rs1 || iD.io.jump)) || (rs2Ind === iD.io.idex.rdInd && (iD.io.aluB_sel === ControlSignal.rs2 || iD.io.jump))) && iD.io.idex.rdInd =/= 0.U)
    || (((rs1Ind === eX.io.exmem.rdInd && (iD.io.aluA_sel === ControlSignal.rs1 || iD.io.jump)) || (rs2Ind === eX.io.exmem.rdInd && (iD.io.aluB_sel === ControlSignal.rs2 || iD.io.jump))) && eX.io.exmem.rdInd =/= 0.U)
    || (((rs1Ind === mEM.io.memwb.rdInd && (iD.io.aluA_sel === ControlSignal.rs1 || iD.io.jump)) || (rs2Ind === mEM.io.memwb.rdInd && (iD.io.aluB_sel === ControlSignal.rs2 || iD.io.jump))) && mEM.io.memwb.rdInd =/= 0.U)){
    hazard := true.B
  }.otherwise(
    hazard := false.B
  )

}
