package abc.abc454

fun main() {
    val (l,r) = readln().split(" ").map{it.toInt()}
    println(r - l + 1)
}