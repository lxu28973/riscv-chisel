package riscv5stages

import chisel3._
import chisel3.util._

//TODO: Memory-mapped machine-mode read-write registers
//      mtime, mtimecmp are not implemented now.
//      mhpmcounter3~31 and mhpmevent3~31 are not implemented. (can hard-wire to 0)

object CSR {
  /** CSR Address
    *
    * the top two bits (csr[11:10]) indicate whether the register is read/write (00, 01, or 10) or read-only (11),
    * the next two bits (csr[9:8]) encode the lowest privilege level that can access the CSR,
    * in this implement, only support M-Mode, so csr[9:8] always be "b11".
     */
  val misa = 0xf00.U(12.W)
  val mvendorid = 0xf01.U(12.W)
  val mimpid = 0xf02.U(12.W)
  val mhartid = 0xf03.U(12.W)
  val mstatus = 0x304.U(12.W)
  val mtvec = 0xf05.U(12.W)
  val mip = 0x306.U(12.W)
  val mie = 0x307.U(12.W)
  val mcycle = 0x308.U(12.W)
  val mcycleh = 0x309.U(12.W)
  val minstret = 0x30a.U(12.W)
  val minstreth = 0x30b.U(12.W)
  val mepc = 0x30c.U(12.W)
  val mcause = 0x30d.U(12.W)
  val mtval = 0x30e.U(12.W)

}


class CSR extends Module with Param {
  val io = IO(new Bundle() {

  })

  val misa = RegInit(Cat(("b01".asUInt), 0.U(mxlen-28), Reverse("b00000000100000000000000000".asUInt)))
  val mvendorid = RegInit(0.U(mxlen))
  val mimpid = RegInit(0.U(mxlen))
  val mhartid = RegInit(0.U(mxlen))

  // mstatus
  val mIE = RegInit(0.U(1.W))
  val sIE = 0.U(1.W)
  val uIE = 0.U(1.W)
  val mPIE = RegInit(0.U(1.W))
  val sPIE = 0.U(1.W)
  val uPIE = 0.U(1.W)
  val mPP = 0.U(2.W)
  val sPP = 0.U(1.W)
  val mPRV = 0.U(1.W)
  val mXR = 0.U(1.W)
  val sUM = 0.U(1.W)
  val tVM = 0.U(1.W)
  val tW = 0.U(1.W)
  val tSR = 0.U(1.W)
  val fS = 0.U(2.W) // no S-Mode and floating-point unit now
  val xS = 0.U(2.W) // no user extension no need new state
  val sD = 0.U(1.W)
  val mstatus = Cat(sD, 0.U(8.W), tSR, tW, tVM, mXR, sUM, mPRV, xS, fS, mPP, 0.U(2.W), sPP, mPIE, 0.U(1), sPIE, uPIE, mIE, 0.U(1.W), sIE, uIE)

  val mtvec = Wire(0x100.U(32.W))

  // mip
  val mEIP = RegInit(0.U(1.W))
  val mTIP = RegInit(0.U(1.W))
  val mSIP = RegInit(0.U(1.W))
  val mip = Cat(0.U((mxlen-12).W), mEIP, 0.U(3.W), mTIP, 0.U(3.W), mSIP, 0.U(3.W))

  // mie
  val mEIE = RegInit(1.U(1.W))
  val mTIE = RegInit(1.U(1.W))
  val mSIE = RegInit(1.U(1.W))
  val mie = Cat(0.U((mxlen-12).W), mEIE, 0.U(3.W), mTIE, 0.U(3.W), mSIE, 0.U(3.W))

  val mcycle = Reg(UInt(64.W))
  val minstret = Reg(UInt(64.W))

  val mscratch = Reg(UInt(mxlen.W))
  val mepc = Reg(UInt(mxlen.W))
  val mcause = Reg(UInt(mxlen.W))
  val mtval = Reg(UInt(mxlen.W))

}
