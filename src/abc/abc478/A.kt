package abc.abc478

fun main() {
    val (n,m) = readln().split(" ").map{it.toInt()}
    val hito = IntArray(n)
    for(i in 0 until m){
        hito[i%n]++
    }
    println(hito.joinToString("\n"))
}