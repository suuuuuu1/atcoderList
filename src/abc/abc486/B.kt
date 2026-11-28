package abc.abc486
 
//https://suuuuuu1.github.io/atcoderList/#
fun main() {
    val (n,m) = readln().split(" ").map{it.toInt()}
    val a = readln().split(" ").map{it.toInt()}
    val sebangou = mutableListOf<Int>()
    sebangou.addAll(a)
    repeat(m) {
        val (x,y)  = readln().split(" ").map{it.toInt()}
        sebangou.remove(x)
        sebangou.add(y)
    }
    println(sebangou.sorted().joinToString(" "))
}