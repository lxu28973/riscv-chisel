package riscv5stages

import chisel3.iotesters

//object TopMain extends App {
////  chisel3.Driver.execute(Array[String](), () => new Top)
////  iotesters.Driver.execute(args, () => new Top) {
////    c => new RISCVUnitTester(c)
////  }
//}

object TopMain extends App {
  iotesters.Driver.execute(args, () => new Top) {
    c => new TopTester(c)
  }
}