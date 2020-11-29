package riscv5stages

import chisel3._

class Top extends Module{
  val io = IO(new Bundle() {
    val inst_i = Input(UInt(32.W))
    val inst_addr_o = Output(UInt(32.W))
  })
  io.inst_addr_o := io.inst_i
}
