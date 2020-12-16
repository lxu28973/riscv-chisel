package riscv5stages

import Chisel.{isPow2, log2Floor}
import chisel3._

trait MaskedMemParam extends Param {
  val memSize: Int = 1024
  val memWidth: Int = 32
  val addrOffset: Int = log2Floor(memWidth) - 3
  val maskBits: Int = 4
  val bankWidth: Int = memWidth / maskBits
  def memDataType: Vec[Data] = Vec(maskBits, UInt(bankWidth.W))
  require(memWidth % maskBits == 0, "maskBits cannot divide memWidth, memWidth % maskBits =/= 0")
  require(isPow2(memWidth), "memWidth must be pow 2")
}

class MaskedMemBundle extends Bundle with MaskedMemParam {
  val addr = Input(UInt(xlen.W))
  val rData = Output(UInt(memWidth.W))
  val wMask = Input(Vec(maskBits, Bool()))
  val wData = Input(UInt(memWidth.W))
}

class MemHasMask extends Module with MaskedMemParam {
  val io = IO(new MaskedMemBundle)

  val mem = SyncReadMem(memSize, memDataType)

  val wData = Wire(memDataType)
  val rData = Wire(memDataType)
  val ind = io.addr(xlen-1, addrOffset)

  wData := io.wData.asTypeOf(memDataType)
  mem.write(ind, wData, io.wMask)
  rData := mem.read(ind)
  io.rData := rData.asUInt()
}

trait NonMaskedMemParam extends Param {
  val memSize: Int = 1024
  val memWidth: Int = 32
  val addrOffset: Int = log2Floor(memWidth) - 3
  def memDataType: Data = UInt(memWidth.W)
  require(isPow2(memWidth), "memWidth must be pow 2")
}

class NonMaskedMemBundle extends Bundle with NonMaskedMemParam {
  val rAddr = Input(UInt(xlen.W))
  val rData = Output(UInt(memWidth.W))
  val wEn = Input(Bool())
  val wData = Input(UInt(memWidth.W))
  val wAddr = Input(UInt(xlen.W))
}

class Mem1r1w extends Module with NonMaskedMemParam {
  val io = IO(new NonMaskedMemBundle)

  val mem = SyncReadMem(memSize, memDataType)

  val wInd = io.wAddr(xlen-1, addrOffset)
  val rInd = io.rAddr(xlen-1, addrOffset)

  val wrDataReg = RegNext(io.wData)
  val doForwardReg = RegNext(io.wAddr === io.rAddr &&
    io.wEn)
  val memData = mem.read(rInd)
  when(io.wEn) {
    mem.write(wInd , io.rData)
  }
  io.rData := Mux(doForwardReg , wrDataReg , memData)
}

/*
class MemOutBundle(val dataWidth: Int = 32) extends Bundle {
  val rdData = UInt(dataWidth.W)
}

class MemInBundle(val addrWidth: Int = 32, val dataWidth: Int = 32) extends Bundle {
  val rdAddr = UInt(addrWidth.W)
  val wrEna = Bool()
  val wrData = UInt(dataWidth.W)
  val wrAddr = UInt(addrWidth.W)
}

class Mem1r1w(depth: Int = 1024, dataType: Data = UInt(32.W)) extends Module{
  val io = IO(new Bundle {
    val in = Input(new MemInBundle)
    val out = Output(new MemOutBundle)
  })
  val mem = SyncReadMem(depth, dataType)
  val wrDataReg = RegNext(io.in.wrData)
  val doForwardReg = RegNext(io.in.wrAddr === io.in.rdAddr &&
    io.in.wrEna)
  val memData = mem.read(io.in.rdAddr)
  when(io.in.wrEna) {
    mem.write(io.in.wrAddr , io.in.wrData)
  }
  io.out.rdData := Mux(doForwardReg , wrDataReg , memData)
}
*/

//class RWSmem extends Module {
//  val width: Int = 32
//  val io = IO(new Bundle {
//    val enable = Input(Bool())
//    val write = Input(Bool())
//    val addr = Input(UInt(10.W))
//    val dataIn = Input(UInt(width.W))
//    val dataOut = Output(UInt(width.W))
//  })
//
//  val mem = SyncReadMem(1024, UInt(width.W))
//  io.dataOut := DontCare
//  when(io.enable) {
//    val rdwrPort = mem(io.addr)
//    when (io.write) { rdwrPort := io.dataIn }
//      .otherwise    { io.dataOut := rdwrPort }
//  }
//}
//
//class Mem2r1w(depth: Int = 1024, addrWidth: Int = 32, dataWidth: Int = 32) extends Module {
//  val io = IO(new Bundle {
//    val rdAddr1 = Input(UInt(addrWidth.W))
//    val rdData1 = Output(UInt(dataWidth.W))
//    val rdAddr2 = Input(UInt(addrWidth.W))
//    val rdData2 = Output(UInt(dataWidth.W))
//    val wrEna = Input(Bool())
//    val wrData = Input(UInt(dataWidth.W))
//    val wrAddr = Input(UInt(addrWidth.W))
//  })
//  val mem = SyncReadMem(depth, UInt(dataWidth.W))
//  when(io.wrEna) {
//    mem.write(io.wrAddr, io.wrData)
//  }
//  io.rdData1 := mem.read(io.rdAddr1)
//  io.rdData2 := mem.read(io.rdAddr2)
//
//}