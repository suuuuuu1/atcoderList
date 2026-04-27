package abc.abc455

fun main() {
    val (a,b,c)= readln().split(" ").map{it.toInt()}
    println(if(a!=b && b ==c)"Yes" else "No")
}