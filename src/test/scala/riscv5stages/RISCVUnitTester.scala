package riscv5stages

import chisel3.iotesters.PeekPokeTester

class RISCVUnitTester[T <: chisel3.MultiIOModule](c: T) extends PeekPokeTester(c){


}
