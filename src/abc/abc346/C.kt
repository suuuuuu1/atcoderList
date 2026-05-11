package abc.abc346

fun main() {
    val (n,k) = readln().split(" ").map{it.toLong()}
    val a = readln().split(" ").map{it.toLong()}.toSet()
    val t = (k * (k+1)) /2
    val minusSum = a.sumOf { if (it in 1..k) it else 0L }
    println(t - minusSum)
}