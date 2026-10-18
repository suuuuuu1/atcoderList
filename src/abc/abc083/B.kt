package abc.abc083

// genre:
fun main() {
    val (n , a , b) = readln().split(" ").map{it.toInt()}
    println((1..n).filter{it.toString().sumOf { it - '0' }in a..b}.sum())
}