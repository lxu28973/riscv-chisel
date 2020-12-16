package riscv5stages

import chisel3._

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

trait MaskedMemParam extends Param {
  val memSize: Int = 1024
  val memWidth: Int = 32
  val maskBits: Int = 4
  val memDataType: Vec[Data] = Vec(maskBits, UInt((memWidth / maskBits).W))
  require(memWidth % maskBits == 0, "maskBits cannot divide memWidth, memWidth % maskBits =/= 0")
}

trait NonMaskedMemParam extends Param {
  val memSize: Int = 1024
  val memWidth: Int = 32
  val memDataType: Data = UInt(memWidth.W)
}

class MemHasMask extends Module with MaskedMemParam {
  val io = IO(new Bundle {
    val rAddr = Input(UInt(xlen.W))
    val rData = Output(UInt(memWidth.W))
    val wMask = Input(Vec(maskBits, Bool()))
    val wData = Input(UInt(memWidth.W))
    val wAddr = Input(UInt(xlen.W))
  })
  val mem = SyncReadMem(memSize, memDataType)
  val wData = Wire(memDataType)
  val rData = Wire(memDataType)
  wData := io.wData.asTypeOf(memDataType)
  mem.write(io.wAddr, wData, io.wMask)
  rData := mem.read(io.rAddr)
  io.rData := rData.asUInt()
}

class Mem1r1w extends Module with NonMaskedMemParam {
  val io = IO(new Bundle {
    val rAddr = Input(UInt(xlen.W))
    val rData = Output(UInt(memWidth.W))
    val wEn = Input(Bool())
    val wData = Input(UInt(memWidth.W))
    val wAddr = Input(UInt(xlen.W))
  })
  val mem = SyncReadMem(memSize, memDataType)
  val wrDataReg = RegNext(io.wData)
  val doForwardReg = RegNext(io.wAddr === io.rAddr &&
    io.wEn)
  val memData = mem.read(io.rAddr)
  when(io.wEn) {
    mem.write(io.wAddr , io.rData)
  }
  io.rData := Mux(doForwardReg , wrDataReg , memData)
}

class RWSmem extends Module {
  val width: Int = 32
  val io = IO(new Bundle {
    val enable = Input(Bool())
    val write = Input(Bool())
    val addr = Input(UInt(10.W))
    val dataIn = Input(UInt(width.W))
    val dataOut = Output(UInt(width.W))
  })

  val mem = SyncReadMem(1024, UInt(width.W))
  io.dataOut := DontCare
  when(io.enable) {
    val rdwrPort = mem(io.addr)
    when (io.write) { rdwrPort := io.dataIn }
      .otherwise    { io.dataOut := rdwrPort }
  }
}

class Mem2r1w(depth: Int = 1024, addrWidth: Int = 32, dataWidth: Int = 32) extends Module {
  val io = IO(new Bundle {
    val rdAddr1 = Input(UInt(addrWidth.W))
    val rdData1 = Output(UInt(dataWidth.W))
    val rdAddr2 = Input(UInt(addrWidth.W))
    val rdData2 = Output(UInt(dataWidth.W))
    val wrEna = Input(Bool())
    val wrData = Input(UInt(dataWidth.W))
    val wrAddr = Input(UInt(addrWidth.W))
  })
  val mem = SyncReadMem(depth, UInt(dataWidth.W))
  when(io.wrEna) {
    mem.write(io.wrAddr, io.wrData)
  }
  io.rdData1 := mem.read(io.rdAddr1)
  io.rdData2 := mem.read(io.rdAddr2)

}