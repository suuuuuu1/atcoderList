package abc.abc467

fun main() {
    val (n, m) = readln().split(" ").map { it.toInt() }
    val a = readln().split(" ").map { it.toInt() }
    val b = readln().split(" ").map { it.toInt() }
    var cost0 = 0
    var current0 = 0
    var cost1 = 0
    var current1 = 1
    if (a[0] != current0) cost0++
    if (a[0] != current1) cost1++
    for(i in 0 until n -1){
        current0 = current0 xor b[i]
        if(a[i+1] != current0)cost0++
        current1 = current1 xor b[i]
        if(a[i+1] != current1)cost1++
        println(cost0)
        println(cost1)
    }
    println(minOf(cost1,cost0))
    var f = arrayOf(1,2,3)
}