package abc.abc061
 
//https://suuuuuu1.github.io/atcoderList/#
fun main() {
    val (a,b,c) = readln().split(" ").map { it.toInt() }
    println(if(c >= a && c <= b) "Yes" else "No")
}