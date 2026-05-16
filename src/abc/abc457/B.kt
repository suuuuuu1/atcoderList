package abc.abc457

fun main() {
    val n = readln().toInt()
    val lArray = Array(n){  readln().split(" ").map{it.toInt()}}
    val (x,y) = readln().split(" ").map{it.toInt()}

    println(lArray[x-1][y])
}