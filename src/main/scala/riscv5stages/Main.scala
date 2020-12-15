package riscv5stages
import chisel3._

object Main extends App {
  val p = new Param
  Driver.execute(Array[String](), () => new ID()(p))
}
