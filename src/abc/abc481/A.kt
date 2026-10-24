package abc.abc481

// genre:
fun main() {
    val (a,b) = readln().split(" ").map{it.toInt()}
    println((a+b).toString().length)
}