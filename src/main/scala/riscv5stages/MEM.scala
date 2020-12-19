package riscv5stages

import chisel3._
import chisel3.util._

class MEMWBBundle extends Bundle with Param {
  val wb = Output(UInt())
  val data = Output(UInt(xlen.W))
  val rdInd = Output(UInt(5.W))
}

class MEM extends Module with MaskedMemParam {
  val io = IO(new Bundle() {
    val exmem = Flipped(new EXMEMBundle)
    val memio = Flipped(new MaskedMemBundle)
    val memwb = new MEMWBBundle
  })
  import io._
  val memMaskMap = Seq(
    ControlSignal.sw -> "b1111".asUInt,
    ControlSignal.sh -> Mux(exmem.eXout(1).asBool, "b0011".asUInt, "b1100".asUInt),
    ControlSignal.sb -> ("b0001".asUInt << exmem.eXout(1,0))
  )

  val memLoadMap = Seq(
    ControlSignal.lb -> memio.rData(bankWidth-1,0).asTypeOf(SInt(memWidth.W)).asUInt,
    ControlSignal.lbu -> memio.rData(bankWidth-1,0).asTypeOf(UInt(memWidth.W)),
    ControlSignal.lh -> memio.rData(2*bankWidth-1,0).asTypeOf(SInt(memWidth.W)).asUInt,
    ControlSignal.lhu -> memio.rData(2*bankWidth-1,0).asTypeOf(UInt(memWidth.W)),
    ControlSignal.lw -> memio.rData
  )

  memio.wMask := MuxLookup(exmem.memOp, 0.U(4.W), memMaskMap).asTypeOf(Vec(maskBits, Bool()))
  memio.addr := exmem.eXout
  memio.wData := exmem.rs2

  val wbmemOp = RegNext(exmem.memOp)

  memwb.data :=  MuxLookup(wbmemOp, RegNext(exmem.eXout), memLoadMap)
  memwb.rdInd := RegNext(exmem.rdInd)
  memwb.wb := RegNext(exmem.wb)

}
