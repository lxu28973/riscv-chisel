package riscv5stages

import chisel3.iotesters

object TopMain extends App {
//  chisel3.Driver.execute(Array[String](), () => new Top)
  iotesters.Driver.execute(args, () => new Mem1r1w) {
    c => new RISCVUnitTester(c)
  }
}

