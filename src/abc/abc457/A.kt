package abc.abc457

fun main() {
    val n = readln().toInt()
    val a = readln().split(" ").map{it.toInt()}
    val x = readln().toInt()
    println(a[x-1])
}