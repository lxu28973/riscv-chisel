package riscv5stages

import chisel3._
import chisel3.util._

class EXMEMBundle extends Bundle with Param {
  val eXout = Output(UInt(xlen.W))
  val memOp = Output(UInt())
}

class EX extends Module with Param {
  val io = IO(new Bundle() {
    val idex = Flipped(new IDEXBundle)
    val exmem = new EXMEMBundle()
  })
  import io._

  val aluA = WireInit(idex.aluA)
  val aluB = WireInit(idex.aluB)

  exmem.memOp := RegNext(idex.memOp)
  /*** ALU ***/
  val aluMap = Seq(
    ControlSignal.add -> (aluA + aluB),
    ControlSignal.sub -> (aluA - aluB),
    ControlSignal.slt -> Mux(idex.sign, (Mux(((aluA.asSInt - aluB.asSInt) < 0.S), 1.U, 0.U)), (Mux(((aluA - aluB) < 0.U), 1.U, 0.U))),
    ControlSignal.and -> (aluA & aluB),
    ControlSignal.or  -> (aluA | aluB),
    ControlSignal.xor -> (aluA ^ aluB)
  )

  /*** SHIFT ***/
  val shamt = aluB(4,0)
  val shiftmap = Seq(
    ControlSignal.sll -> (aluA << shamt),
    ControlSignal.srl -> (aluA >> shamt),
    ControlSignal.sra -> (aluA.asSInt >> shamt).asUInt
  )

  val aluRes = MuxLookup(idex.aluOp, 0.U, aluMap)
  val shiftRse = MuxLookup(idex.shift, 0.U, shiftmap)

  exmem.eXout := RegNext(Mux((idex.shift === ControlSignal.nonsh), aluRes, shiftRse))

}
