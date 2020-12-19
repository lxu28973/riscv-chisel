package riscv5stages

import chisel3.iotesters.PeekPokeTester

class TopTester(c: Top) extends PeekPokeTester(c) {
  for(i <- 0 to 100){
    poke(c.io.iMemWrEn, 0)
    step(10)
  }
}
