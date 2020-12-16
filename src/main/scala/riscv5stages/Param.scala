package riscv5stages

import chisel3._

trait Param {
  val xlen: Int = 32
  val ilen: Int = 32
}
