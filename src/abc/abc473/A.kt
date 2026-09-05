package abc.abc473

fun main() {
    val n = readln().toInt()
    val  a = readln().split(" ").map{it.toInt()}.drop(n/2)
    println(a.sum())

}