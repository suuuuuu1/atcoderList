package abc.abc164

// genre:
fun main() {
    val n = readln().toInt()
    println(Array(n){readln()}.groupingBy{it}.eachCount().size)
}