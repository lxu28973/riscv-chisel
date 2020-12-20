package riscv5stages

import chisel3.util._
import riscv5stages.InstPat._

object ControlSignal {
  // aluOp
  val non :: add :: sub :: slt :: and :: or :: xor :: copyb :: Nil = Enum(8)
  // aluA_sel
  val rs1 :: pc :: Nil = Enum(2)
  // aluB_sel
  val rs2 :: imm :: Nil = Enum(2)
  // write to memory or register
  val nonw :: rd :: Nil = Enum(2)
  // expend imm signed or unsigned
  val sign :: unsi :: Nil = Enum(2)
  // shift instruction or not
  val nonsh :: sll :: srl :: sra :: Nil = Enum(4)
  // instruction format
  val r :: i :: s :: b :: u :: j :: Nil = Enum(6)
  // jump or link or not
  val nonj :: jal :: beq :: bne :: blt :: bltu :: bge :: bgeu :: Nil = Enum(8)
  // load store instruction
  val nonmem :: lb :: lh :: lbu :: lhu :: lw :: sb :: sh :: sw :: Nil = Enum(9)
  // exceptions
  val nonexp :: ecall :: ebreak :: Nil = Enum(3)
  // csr commend
  val noncsr :: csrRW :: csrRS :: csrRC :: Nil = Enum(4)
  // csr assist
  val csrrs1 :: csruimm :: Nil = Enum(2)

  val aluOp = add
  val aluA = rs1
  val aluB = rs2
  val wrReg = rd
  val instT = r
  val jump = nonj
  val memOp = nonmem
  val exp = nonexp

  val instDefault =  //0     1     2      3     4      5      6     7      8     9         10      11
              List(aluOp, aluA, aluB, wrReg, sign, nonsh, instT, jump, memOp,  exp,    noncsr, csrrs1 )
  val instMap = Array(
    ADDI   -> List(add,   rs1,  imm,  rd,    sign, nonsh, i,     nonj, nonmem, nonexp, noncsr, csrrs1 ),
    SLTI   -> List(slt,   rs1,  imm,  rd,    sign, nonsh, i,     nonj, nonmem, nonexp, noncsr, csrrs1 ),
    SLTIU  -> List(slt,   rs1,  imm,  rd,    unsi, nonsh, i,     nonj, nonmem, nonexp, noncsr, csrrs1 ),
    ANDI   -> List(and,   rs1,  imm,  rd,    sign, nonsh, i,     nonj, nonmem, nonexp, noncsr, csrrs1 ),
    ORI    -> List(or ,   rs1,  imm,  rd,    sign, nonsh, i,     nonj, nonmem, nonexp, noncsr, csrrs1 ),
    XORI   -> List(or ,   rs1,  imm,  rd,    sign, nonsh, i,     nonj, nonmem, nonexp, noncsr, csrrs1 ),
    SLLI   -> List(non,   rs1,  imm,  rd,    sign, sll  , i,     nonj, nonmem, nonexp, noncsr, csrrs1 ),
    SRLI   -> List(non,   rs1,  imm,  rd,    sign, srl  , i,     nonj, nonmem, nonexp, noncsr, csrrs1 ),
    SRAI   -> List(non,   rs1,  imm,  rd,    sign, sra  , i,     nonj, nonmem, nonexp, noncsr, csrrs1 ),
    LUI    -> List(copyb, rs1,  imm,  rd,    sign, nonsh, u,     nonj, nonmem, nonexp, noncsr, csrrs1 ),
    AUIPC  -> List(add,   pc ,  imm,  rd,    sign, nonsh, u,     nonj, nonmem, nonexp, noncsr, csrrs1 ),
    ADD    -> List(add,   rs1,  rs2,  rd,    sign, nonsh, r,     nonj, nonmem, nonexp, noncsr, csrrs1 ),
    SLT    -> List(slt,   rs1,  rs2,  rd,    sign, nonsh, r,     nonj, nonmem, nonexp, noncsr, csrrs1 ),
    SLTU   -> List(slt,   rs1,  rs2,  rd,    unsi, nonsh, r,     nonj, nonmem, nonexp, noncsr, csrrs1 ),
    AND    -> List(and,   rs1,  rs2,  rd,    unsi, nonsh, r,     nonj, nonmem, nonexp, noncsr, csrrs1 ),
    OR     -> List(or ,   rs1,  rs2,  rd,    unsi, nonsh, r,     nonj, nonmem, nonexp, noncsr, csrrs1 ),
    XOR    -> List(xor,   rs1,  rs2,  rd,    unsi, nonsh, r,     nonj, nonmem, nonexp, noncsr, csrrs1 ),
    SLL    -> List(non,   rs1,  rs2,  rd,    unsi, sll  , r,     nonj, nonmem, nonexp, noncsr, csrrs1 ),
    SRL    -> List(non,   rs1,  rs2,  rd,    unsi, srl  , r,     nonj, nonmem, nonexp, noncsr, csrrs1 ),
    SUB    -> List(sub,   rs1,  rs2,  rd,    sign, nonsh, r,     nonj, nonmem, nonexp, noncsr, csrrs1 ),
    SRA    -> List(non,   rs1,  rs2,  rd,    sign, sra  , r,     nonj, nonmem, nonexp, noncsr, csrrs1 ),
    JAL    -> List(add,   pc ,  imm,  rd,    sign, nonsh, j,     jal , nonmem, nonexp, noncsr, csrrs1 ),
    JALR   -> List(add,   rs1,  imm,  rd,    sign, nonsh, i,     jal , nonmem, nonexp, noncsr, csrrs1 ),
    BEQ    -> List(add,   pc ,  imm,  nonw,  sign, nonsh, b,     beq , nonmem, nonexp, noncsr, csrrs1 ),
    BNE    -> List(add,   pc ,  imm,  nonw,  sign, nonsh, b,     bne , nonmem, nonexp, noncsr, csrrs1 ),
    BLT    -> List(add,   pc ,  imm,  nonw,  sign, nonsh, b,     blt , nonmem, nonexp, noncsr, csrrs1 ),
    BLTU   -> List(add,   pc ,  imm,  nonw,  unsi, nonsh, b,     bltu, nonmem, nonexp, noncsr, csrrs1 ),
    BGE    -> List(add,   pc ,  imm,  nonw,  sign, nonsh, b,     bge , nonmem, nonexp, noncsr, csrrs1 ),
    BGEU   -> List(add,   pc ,  imm,  nonw,  unsi, nonsh, b,     bgeu, nonmem, nonexp, noncsr, csrrs1 ),
    LW     -> List(add,   rs1,  imm,  rd,    sign, nonsh, i,     nonj, lw    , nonexp, noncsr, csrrs1 ),
    LH     -> List(add,   rs1,  imm,  rd,    sign, nonsh, i,     nonj, lh    , nonexp, noncsr, csrrs1 ),
    LB     -> List(add,   rs1,  imm,  rd,    sign, nonsh, i,     nonj, lb    , nonexp, noncsr, csrrs1 ),
    LHU    -> List(add,   rs1,  imm,  rd,    sign, nonsh, i,     nonj, lhu   , nonexp, noncsr, csrrs1 ),
    LBU    -> List(add,   rs1,  imm,  rd,    sign, nonsh, i,     nonj, lbu   , nonexp, noncsr, csrrs1 ),
    SW     -> List(add,   rs1,  imm,  nonw,  sign, nonsh, s,     nonj, sw    , nonexp, noncsr, csrrs1 ),
    SH     -> List(add,   rs1,  imm,  nonw,  sign, nonsh, s,     nonj, sh    , nonexp, noncsr, csrrs1 ),
    SB     -> List(add,   rs1,  imm,  nonw,  sign, nonsh, s,     nonj, sb    , nonexp, noncsr, csrrs1 ),
    FENCE  -> List(non,   rs1,  imm,  nonw,  sign, nonsh, i,     nonj, nonmem, nonexp, noncsr, csrrs1 ),
    ECALL  -> List(non,   rs1,  imm,  nonw,  sign, nonsh, i,     nonj, nonmem, ecall , noncsr, csrrs1 ),
    EBREAK -> List(non,   rs1,  imm,  nonw,  sign, nonsh, i,     nonj, nonmem, ebreak, noncsr, csrrs1 ),
    CSRRW  -> List(non,   rs1,  imm,  rd,    sign, nonsh, i,     nonj, nonmem, nonexp, csrRW , csrrs1 ),
    CSRRS  -> List(non,   rs1,  imm,  rd,    sign, nonsh, i,     nonj, nonmem, nonexp, csrRS , csrrs1 ),
    CSRRC  -> List(non,   rs1,  imm,  rd,    sign, nonsh, i,     nonj, nonmem, nonexp, csrRC , csrrs1 ),
    CSRRWI -> List(non,   rs1,  imm,  rd,    sign, nonsh, i,     nonj, nonmem, nonexp, csrRW , csruimm),
    CSRRSI -> List(non,   rs1,  imm,  rd,    sign, nonsh, i,     nonj, nonmem, nonexp, csrRS , csruimm),
    CSRRCI -> List(non,   rs1,  imm,  rd,    sign, nonsh, i,     nonj, nonmem, nonexp, csrRC , csruimm)
  )
}
