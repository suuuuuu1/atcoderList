package abc.abc475

fun main() {
    val (n, s, l) = readln().split(" ").map { it.toLong() }
    val a = readln().split(" ").map { it.toInt() }.toMutableList()
    val b = IntArray(n.toInt())
    for(i in 1 until a.size){
        b[i] = a[i] + a[i-1]
    }
    var c = 1

}