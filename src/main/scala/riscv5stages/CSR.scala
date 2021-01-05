package riscv5stages

import chisel3._
import chisel3.util._

//TODO: Memory-mapped machine-mode read-write registers
//      mtime, mtimecmp are not implemented now.
//      mhpmcounter3~31 and mhpmevent3~31 are not implemented. (can hard-wire to 0)

object CSRAddr {
  /** CSR Address
    *
    * the top two bits (csr[11:10]) indicate whether the register is read/write (00, 01, or 10) or read-only (11),
    * the next two bits (csr[9:8]) encode the lowest privilege level that can access the CSR,
    * in this implement, only support M-Mode, so csr[9:8] always be "b11".
     */
  val misa = 0x301.U(12.W)
  val mvendorid = 0xf11.U(12.W)
  val mimpid = 0xf13.U(12.W)
  val mhartid = 0xf14.U(12.W)
  val mstatus = 0x300.U(12.W)
  val mtvec = 0x305.U(12.W)
  val mip = 0x344.U(12.W)
  val mie = 0x304.U(12.W)
  val mcycle = 0xb00.U(12.W)
  val mcycleh = 0xb80.U(12.W)
  val minstret = 0xb02.U(12.W)
  val minstreth = 0xb82.U(12.W)
  val mepc = 0x341.U(12.W)
  val mcause = 0x342.U(12.W)
  val mtval = 0x343.U(12.W)
  val mscratch = 0x340.U(12.W)

}

class CSRIO extends Bundle with Param {
  val csrOp = Input(UInt(2.W))
  val csrInd = Input(UInt(12.W))
  val scEn = Input(Bool())
  val rEn = Input(Bool())
  val wData = Input(UInt(xlen.W))
  val rData = Output(UInt(xlen.W))
  val epc = Output(UInt(xlen.W))
}


class CSR extends Module with Param {
  val io = IO(new CSRIO)

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


  val mtvec = 0x100.U(32.W)

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

  val mcycle = Reg(UInt(32.W))
  val mcycleh = Reg(UInt(32.W))
  val minstret = Reg(UInt(32.W))
  val minstreth = Reg(UInt(32.W))

  val mscratch = Reg(UInt(mxlen.W))
  val mepc = Reg(UInt(mxlen.W))
  val mcause = Reg(UInt(mxlen.W))
  val mtval = Reg(UInt(mxlen.W))


  io.epc := mepc


  /*** Read ***/
  val readMap = Seq(
    CSRAddr.misa -> misa,
    CSRAddr.mvendorid -> mvendorid,
    CSRAddr.mimpid -> mimpid,
    CSRAddr.mhartid -> mhartid,
    CSRAddr.mstatus -> mstatus,
    CSRAddr.mtvec -> mtvec,
    CSRAddr.mip -> mip,
    CSRAddr.mie -> mie,
    CSRAddr.mcycle -> mcycle,
    CSRAddr.mcycleh -> mcycleh,
    CSRAddr.minstret -> minstret,
    CSRAddr.minstreth -> minstreth,
    CSRAddr.mepc -> mepc,
    CSRAddr.mcause -> mcause,
    CSRAddr.mtval -> mtval
  )

  val rData = MuxLookup(io.csrInd, 0.U(32.W), readMap)

  io.rData := 0.U(32.W)
  when(io.csrOp === ControlSignal.csrRW){
    when(io.rEn){
      io.rData := rData
    }
  }.elsewhen(io.csrOp === ControlSignal.csrRC || io.csrOp === ControlSignal.csrRS){
    io.rData := rData
  }

  /*** Write ***/
  val wData = MuxLookup(io.csrOp, 0.U, Seq(
    ControlSignal.csrRW -> io.wData,
    ControlSignal.csrRC -> (io.rData & (~io.wData).asUInt),
    ControlSignal.csrRS -> (io.rData | io.wData)
  ))
  val wEn = (io.scEn && (io.csrOp === ControlSignal.csrRC || io.csrOp === ControlSignal.csrRS)) || io.csrOp === ControlSignal.csrRW
  when(wEn){
    when(io.csrInd === CSRAddr.mstatus){
      mIE := wData(3)
      mPIE := wData(7)
    }.elsewhen(io.csrInd === CSRAddr.mip){
      mEIP := wData(11)
      mTIP := wData(7)
      mSIP := wData(3)
    }.elsewhen(io.csrInd === CSRAddr.mie){
      mEIE := wData(11)
      mTIE := wData(7)
      mSIE := wData(3)
    }.elsewhen(io.csrInd === CSRAddr.mcycle){
      mcycle := wData
    }.elsewhen(io.csrInd === CSRAddr.mcycleh){
      mcycleh := wData
    }.elsewhen(io.csrInd === CSRAddr.minstret){
      minstret := wData
    }.elsewhen(io.csrInd === CSRAddr.minstreth){
      minstreth := wData
    }.elsewhen(io.csrInd === CSRAddr.mscratch){
      mscratch := wData
    }.elsewhen(io.csrInd === CSRAddr.mepc){
      mepc := Cat(wData(31,2), 0.U(2.W))
    }.elsewhen(io.csrInd === CSRAddr.mcause){
      mcause := wData
    }.elsewhen(io.csrInd === CSRAddr.mtval){
      mtval := wData
    }
  }
}
