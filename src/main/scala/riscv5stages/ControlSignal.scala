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
  // next jump pc
  val pc_jump :: pc_epc :: Nil = Enum(2)

  val aluOp = add
  val aluA = rs1
  val aluB = rs2
  val wrReg = rd
  val instT = r
  val jump = nonj
  val memOp = nonmem
  val exp = nonexp

  val instDefault =  //0     1     2      3     4      5      6     7      8     9         10      11        12
              List(aluOp, aluA, aluB, wrReg, sign, nonsh, instT, jump, memOp,  exp,    noncsr, csrrs1 , pc_jump)
  val instMap = Array(
    ADDI   -> List(add,   rs1,  imm,  rd,    sign, nonsh, i,     nonj, nonmem, nonexp, noncsr, csrrs1 , pc_jump),
    SLTI   -> List(slt,   rs1,  imm,  rd,    sign, nonsh, i,     nonj, nonmem, nonexp, noncsr, csrrs1 , pc_jump),
    SLTIU  -> List(slt,   rs1,  imm,  rd,    unsi, nonsh, i,     nonj, nonmem, nonexp, noncsr, csrrs1 , pc_jump),
    ANDI   -> List(and,   rs1,  imm,  rd,    sign, nonsh, i,     nonj, nonmem, nonexp, noncsr, csrrs1 , pc_jump),
    ORI    -> List(or ,   rs1,  imm,  rd,    sign, nonsh, i,     nonj, nonmem, nonexp, noncsr, csrrs1 , pc_jump),
    XORI   -> List(or ,   rs1,  imm,  rd,    sign, nonsh, i,     nonj, nonmem, nonexp, noncsr, csrrs1 , pc_jump),
    SLLI   -> List(non,   rs1,  imm,  rd,    sign, sll  , i,     nonj, nonmem, nonexp, noncsr, csrrs1 , pc_jump),
    SRLI   -> List(non,   rs1,  imm,  rd,    sign, srl  , i,     nonj, nonmem, nonexp, noncsr, csrrs1 , pc_jump),
    SRAI   -> List(non,   rs1,  imm,  rd,    sign, sra  , i,     nonj, nonmem, nonexp, noncsr, csrrs1 , pc_jump),
    LUI    -> List(copyb, rs1,  imm,  rd,    sign, nonsh, u,     nonj, nonmem, nonexp, noncsr, csrrs1 , pc_jump),
    AUIPC  -> List(add,   pc ,  imm,  rd,    sign, nonsh, u,     nonj, nonmem, nonexp, noncsr, csrrs1 , pc_jump),
    ADD    -> List(add,   rs1,  rs2,  rd,    sign, nonsh, r,     nonj, nonmem, nonexp, noncsr, csrrs1 , pc_jump),
    SLT    -> List(slt,   rs1,  rs2,  rd,    sign, nonsh, r,     nonj, nonmem, nonexp, noncsr, csrrs1 , pc_jump),
    SLTU   -> List(slt,   rs1,  rs2,  rd,    unsi, nonsh, r,     nonj, nonmem, nonexp, noncsr, csrrs1 , pc_jump),
    AND    -> List(and,   rs1,  rs2,  rd,    unsi, nonsh, r,     nonj, nonmem, nonexp, noncsr, csrrs1 , pc_jump),
    OR     -> List(or ,   rs1,  rs2,  rd,    unsi, nonsh, r,     nonj, nonmem, nonexp, noncsr, csrrs1 , pc_jump),
    XOR    -> List(xor,   rs1,  rs2,  rd,    unsi, nonsh, r,     nonj, nonmem, nonexp, noncsr, csrrs1 , pc_jump),
    SLL    -> List(non,   rs1,  rs2,  rd,    unsi, sll  , r,     nonj, nonmem, nonexp, noncsr, csrrs1 , pc_jump),
    SRL    -> List(non,   rs1,  rs2,  rd,    unsi, srl  , r,     nonj, nonmem, nonexp, noncsr, csrrs1 , pc_jump),
    SUB    -> List(sub,   rs1,  rs2,  rd,    sign, nonsh, r,     nonj, nonmem, nonexp, noncsr, csrrs1 , pc_jump),
    SRA    -> List(non,   rs1,  rs2,  rd,    sign, sra  , r,     nonj, nonmem, nonexp, noncsr, csrrs1 , pc_jump),
    JAL    -> List(add,   pc ,  imm,  rd,    sign, nonsh, j,     jal , nonmem, nonexp, noncsr, csrrs1 , pc_jump),
    JALR   -> List(add,   rs1,  imm,  rd,    sign, nonsh, i,     jal , nonmem, nonexp, noncsr, csrrs1 , pc_jump),
    BEQ    -> List(add,   pc ,  imm,  nonw,  sign, nonsh, b,     beq , nonmem, nonexp, noncsr, csrrs1 , pc_jump),
    BNE    -> List(add,   pc ,  imm,  nonw,  sign, nonsh, b,     bne , nonmem, nonexp, noncsr, csrrs1 , pc_jump),
    BLT    -> List(add,   pc ,  imm,  nonw,  sign, nonsh, b,     blt , nonmem, nonexp, noncsr, csrrs1 , pc_jump),
    BLTU   -> List(add,   pc ,  imm,  nonw,  unsi, nonsh, b,     bltu, nonmem, nonexp, noncsr, csrrs1 , pc_jump),
    BGE    -> List(add,   pc ,  imm,  nonw,  sign, nonsh, b,     bge , nonmem, nonexp, noncsr, csrrs1 , pc_jump),
    BGEU   -> List(add,   pc ,  imm,  nonw,  unsi, nonsh, b,     bgeu, nonmem, nonexp, noncsr, csrrs1 , pc_jump),
    LW     -> List(add,   rs1,  imm,  rd,    sign, nonsh, i,     nonj, lw    , nonexp, noncsr, csrrs1 , pc_jump),
    LH     -> List(add,   rs1,  imm,  rd,    sign, nonsh, i,     nonj, lh    , nonexp, noncsr, csrrs1 , pc_jump),
    LB     -> List(add,   rs1,  imm,  rd,    sign, nonsh, i,     nonj, lb    , nonexp, noncsr, csrrs1 , pc_jump),
    LHU    -> List(add,   rs1,  imm,  rd,    sign, nonsh, i,     nonj, lhu   , nonexp, noncsr, csrrs1 , pc_jump),
    LBU    -> List(add,   rs1,  imm,  rd,    sign, nonsh, i,     nonj, lbu   , nonexp, noncsr, csrrs1 , pc_jump),
    SW     -> List(add,   rs1,  imm,  nonw,  sign, nonsh, s,     nonj, sw    , nonexp, noncsr, csrrs1 , pc_jump),
    SH     -> List(add,   rs1,  imm,  nonw,  sign, nonsh, s,     nonj, sh    , nonexp, noncsr, csrrs1 , pc_jump),
    SB     -> List(add,   rs1,  imm,  nonw,  sign, nonsh, s,     nonj, sb    , nonexp, noncsr, csrrs1 , pc_jump),
    FENCE  -> List(non,   rs1,  imm,  nonw,  sign, nonsh, i,     nonj, nonmem, nonexp, noncsr, csrrs1 , pc_jump),
    ECALL  -> List(non,   rs1,  imm,  nonw,  sign, nonsh, i,     nonj, nonmem, ecall , noncsr, csrrs1 , pc_jump),
    EBREAK -> List(non,   rs1,  imm,  nonw,  sign, nonsh, i,     nonj, nonmem, ebreak, noncsr, csrrs1 , pc_jump),
    CSRRW  -> List(non,   rs1,  imm,  rd,    sign, nonsh, i,     nonj, nonmem, nonexp, csrRW , csrrs1 , pc_jump),
    CSRRS  -> List(non,   rs1,  imm,  rd,    sign, nonsh, i,     nonj, nonmem, nonexp, csrRS , csrrs1 , pc_jump),
    CSRRC  -> List(non,   rs1,  imm,  rd,    sign, nonsh, i,     nonj, nonmem, nonexp, csrRC , csrrs1 , pc_jump),
    CSRRWI -> List(non,   rs1,  imm,  rd,    sign, nonsh, i,     nonj, nonmem, nonexp, csrRW , csruimm, pc_jump),
    CSRRSI -> List(non,   rs1,  imm,  rd,    sign, nonsh, i,     nonj, nonmem, nonexp, csrRS , csruimm, pc_jump),
    CSRRCI -> List(non,   rs1,  imm,  rd,    sign, nonsh, i,     nonj, nonmem, nonexp, csrRC , csruimm, pc_jump),
    MRET   -> List(non,   rs1,  imm,  rd,    sign, nonsh, i,     jal , nonmem, nonexp, noncsr, csrrs1 , pc_epc ),
    WFI    -> List(non,   rs1,  imm,  nonw,  sign, nonsh, i,     nonj, nonmem, nonexp, noncsr, csrrs1 , pc_jump)    // implement as a nop now
  )
}
