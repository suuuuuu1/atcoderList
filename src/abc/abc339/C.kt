package abc.abc339

import kotlin.math.min

fun main() {
    val n = readln().toInt()
    val a = readln().split(" ").map{it.toInt()}
    var min = 0L
    var cur = 0L
    for (i in a) {
        cur += i
        if (cur < min){
            min = cur
        }
    }
    println(cur-min)
}