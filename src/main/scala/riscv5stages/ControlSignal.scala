package riscv5stages

import chisel3.util._
import riscv5stages.InstPat._

object ControlSignal {
  // aluOp
  val non :: add :: sub :: slt :: and :: or :: xor :: Nil = Enum(7)
  // aluA_sel
  val rs1 :: pc :: Nil = Enum(2)
  // aluB_sel
  val rs2 :: imm :: Nil = Enum(2)
  // write to memory or register
  val nonw :: rd :: mem :: Nil = Enum(3)
  // expend imm signed or unsigned
  val sign :: unsi :: Nil = Enum(2)
  // shift instruction or not
  val nonsh :: sll :: srl :: sra :: Nil = Enum(4)
  // instruction format
  val r :: i :: s :: b :: u :: j :: Nil = Enum(6)
  // jump or link or not
  val nonj :: jal :: beq :: bne :: blt :: bge :: Nil = Enum(6)
  // load store instruction
  val nonmem :: lb :: lh :: lbu :: lhu :: lw :: sb :: sh :: sw :: Nil = Enum(9)
  // exceptions
  val nonexp :: ecall :: ebreak :: Nil = Enum(3)

  val aluOp = add
  val aluA = rs1
  val aluB = rs2
  val rgMem = rd
  val instT = r
  val jump = nonj
  val memOp = nonmem
  val exp = nonexp

  val instDefault =  //0     1     2      3     4      5      6     7      8     9
              List(aluOp, aluA, aluB, rgMem, sign, nonsh, instT, jump, memOp,  exp)
  val instMap = Array(
    ADDI   -> List(add,   rs1,  imm,  rd,    sign, nonsh, i,     nonj, nonmem, nonexp),
    SLTI   -> List(slt,   rs1,  imm,  rd,    sign, nonsh, i,     nonj, nonmem, nonexp),
    SLTIU  -> List(slt,   rs1,  imm,  rd,    unsi, nonsh, i,     nonj, nonmem, nonexp),
    ANDI   -> List(and,   rs1,  imm,  rd,    sign, nonsh, i,     nonj, nonmem, nonexp),
    ORI    -> List(or ,   rs1,  imm,  rd,    sign, nonsh, i,     nonj, nonmem, nonexp),
    XORI   -> List(or ,   rs1,  imm,  rd,    sign, nonsh, i,     nonj, nonmem, nonexp),
    SLLI   -> List(non,   rs1,  imm,  rd,    sign, sll  , i,     nonj, nonmem, nonexp),
    SRLI   -> List(non,   rs1,  imm,  rd,    sign, srl  , i,     nonj, nonmem, nonexp),
    SRAI   -> List(non,   rs1,  imm,  rd,    sign, sra  , i,     nonj, nonmem, nonexp),
    LUI    -> List(non,   rs1,  imm,  rd,    sign, nonsh, u,     nonj, nonmem, nonexp),
    AUIPC  -> List(add,   pc ,  imm,  rd,    sign, nonsh, u,     nonj, nonmem, nonexp),
    ADD    -> List(add,   rs1,  rs2,  rd,    sign, nonsh, r,     nonj, nonmem, nonexp),
    SLT    -> List(slt,   rs1,  rs2,  rd,    sign, nonsh, r,     nonj, nonmem, nonexp),
    SLTU   -> List(slt,   rs1,  rs2,  rd,    unsi, nonsh, r,     nonj, nonmem, nonexp),
    AND    -> List(and,   rs1,  rs2,  rd,    unsi, nonsh, r,     nonj, nonmem, nonexp),
    OR     -> List(or ,   rs1,  rs2,  rd,    unsi, nonsh, r,     nonj, nonmem, nonexp),
    XOR    -> List(xor,   rs1,  rs2,  rd,    unsi, nonsh, r,     nonj, nonmem, nonexp),
    SLL    -> List(non,   rs1,  rs2,  rd,    unsi, sll  , r,     nonj, nonmem, nonexp),
    SRL    -> List(non,   rs1,  rs2,  rd,    unsi, srl  , r,     nonj, nonmem, nonexp),
    SUB    -> List(sub,   rs1,  rs2,  rd,    sign, nonsh, r,     nonj, nonmem, nonexp),
    SRA    -> List(non,   rs1,  rs2,  rd,    sign, sra  , r,     nonj, nonmem, nonexp),
    JAL    -> List(add,   pc ,  imm,  rd,    sign, nonsh, j,     jal , nonmem, nonexp),
    JALR   -> List(add,   rs1,  imm,  rd,    sign, nonsh, i,     jal , nonmem, nonexp),
    BEQ    -> List(add,   pc ,  imm,  nonw,  sign, nonsh, b,     beq , nonmem, nonexp),
    BNE    -> List(add,   pc ,  imm,  nonw,  sign, nonsh, b,     bne , nonmem, nonexp),
    BLT    -> List(add,   pc ,  imm,  nonw,  sign, nonsh, b,     blt , nonmem, nonexp),
    BLTU   -> List(add,   pc ,  imm,  nonw,  unsi, nonsh, b,     blt , nonmem, nonexp),
    BGE    -> List(add,   pc ,  imm,  nonw,  sign, nonsh, b,     bge , nonmem, nonexp),
    BGEU   -> List(add,   pc ,  imm,  nonw,  unsi, nonsh, b,     bge , nonmem, nonexp),
    LW     -> List(add,   rs1,  imm,  rd,    sign, nonsh, i,     nonj, lw    , nonexp),
    LH     -> List(add,   rs1,  imm,  rd,    sign, nonsh, i,     nonj, lh    , nonexp),
    LB     -> List(add,   rs1,  imm,  rd,    sign, nonsh, i,     nonj, lb    , nonexp),
    LHU    -> List(add,   rs1,  imm,  rd,    sign, nonsh, i,     nonj, lhu   , nonexp),
    LBU    -> List(add,   rs1,  imm,  rd,    sign, nonsh, i,     nonj, lbu   , nonexp),
    SW     -> List(add,   rs1,  imm,  mem,   sign, nonsh, s,     nonj, sw    , nonexp),
    SH     -> List(add,   rs1,  imm,  mem,   sign, nonsh, s,     nonj, sh    , nonexp),
    SB     -> List(add,   rs1,  imm,  mem,   sign, nonsh, s,     nonj, sb    , nonexp),
    FENCE  -> List(non,   rs1,  imm,  nonw,  sign, nonsh, i,     nonj, nonmem, nonexp),
    ECALL  -> List(non,   rs1,  imm,  nonw,  sign, nonsh, i,     nonj, nonmem, ecall ),
    EBREAK -> List(non,   rs1,  imm,  nonw,  sign, nonsh, i,     nonj, nonmem, ebreak)
  )
}
