package abc.abc085

// genre:
fun main() {
    val n = readln().toInt()
    println(IntArray(n){readln().toInt()}.toSet().size)
}