package riscv5stages

import chisel3._
import chisel3.Bundle

class IF extends Module with Param {
  val io = IO(new Bundle() {
    val pc = Output(UInt(xlen.W))
    val hazard = Input(Bool())
    val jpc = Input(UInt(32.W))
    val toJumpOrBranch = Input(Bool())
  })

  val pc = RegInit(0.U(32.W))
  val muxOut = Mux(io.toJumpOrBranch, io.jpc, pc+4.U)

  when(io.hazard) {
    pc := pc
  }.otherwise(
    pc := muxOut
  )

  io.pc := pc
}
