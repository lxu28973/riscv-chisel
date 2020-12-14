package riscv5stages

import chisel3._

class Param {
  val xlen: Int = 32
  val ilen: Int = 32

  /*** Mem ***/
  val memSize: Int = 1024
  val memWidth: Int = 32
  val memDataType: Data = UInt(memWidth.W)

}
