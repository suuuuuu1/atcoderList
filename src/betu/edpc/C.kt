package betu.edpc

fun main() {
    val n = readln().toInt()
    var yesterday = IntArray(3)
    repeat(n) {
        val a = readln().split(" ").map{it.toInt()}
        yesterday = IntArray(3){j -> (0 until 3).filter { it != j }.maxOf{yesterday[it]} + a[j] }
    }
    println(yesterday.max())
}

