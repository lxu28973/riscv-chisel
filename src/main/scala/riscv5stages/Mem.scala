package riscv5stages

import chisel3._

class Mem1r1wBundle(addrWidth: Int = 32, dataWidth: Int = 32) extends Bundle {
  val rdAddr = Input(UInt(addrWidth.W))
  val rdData = Output(UInt(dataWidth.W))
  val wrEna = Input(Bool())
  val wrData = Input(UInt(dataWidth.W))
  val wrAddr = Input(UInt(addrWidth.W))
}

class Mem1r1w(depth: Int = 1024, dataType: Data = UInt(32.W)) extends Module{
  val io = IO(new Mem1r1wBundle())
  val mem = SyncReadMem(depth, dataType)
  val wrDataReg = RegNext(io.wrData)
  val doForwardReg = RegNext(io.wrAddr === io.rdAddr &&
    io.wrEna)
  val memData = mem.read(io.rdAddr)
  when(io.wrEna) {
    mem.write(io.wrAddr , io.wrData)
  }
  io.rdData := Mux(doForwardReg , wrDataReg , memData)
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