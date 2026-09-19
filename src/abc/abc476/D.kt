package abc.abc476

import java.util.PriorityQueue

fun main() {
    val (n,m,k) = readln().split(" ").map { it.toInt() }
    var (x,y) = readln().split(" ").map { it.toBigInteger() }
    val a = readln().split(" ").map { it.toLong() }
    val b = readln().split(" ").map { it.toLong() }
    val ap = PriorityQueue<Long>()
    val bp = PriorityQueue<Long>()
    for (i in a) {
        ap.add(i)
    }
    for (i in b) {
        bp.add(i)
    }
    while(bp.isNotEmpty() ){
        val drinkPrice = bp.poll().toLong()

        val costKdoll = (drinkPrice/k+ if(drinkPrice %k == 0L) 0 else 1).toBigInteger()
        y -= costKdoll
        if(y < 0.toBigInteger()){
            y+=costKdoll
            break
        }
        x += (10-(drinkPrice%k)%k).toBigInteger()
    }
    println()

}