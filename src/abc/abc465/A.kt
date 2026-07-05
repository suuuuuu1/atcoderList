package abc.abc465

fun main() {
    val (a,b) = readln().split(" ").map{it.toInt()}
    println(if(a > b * (2/3.0)) "Yes" else "No")
}