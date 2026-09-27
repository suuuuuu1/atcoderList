package abc.abc476

import java.util.Collections
import java.util.PriorityQueue

fun main() {
    val (n,m) = readln().split(" ").map { it.toInt() }
    val p = PriorityQueue<Pair<Int,Int>>(){a,b -> a.first.compareTo(b.first)}
    val pDISC = PriorityQueue<Pair<Int,Int>>(){a,b -> b.first.compareTo(a.first)}

    val pinput = readln().split(" ").map { it.toInt() }

    for (i in 0 until n) {
        p.add(Pair(pinput[i],i))
        pDISC.add(Pair(pinput[i],i))
    }


    println(p)
    println(pDISC)
    repeat(n){
        val (l,r) =readln().split(" ").map { it.toInt() }
        val maxIndex = p.poll().second
        val minIndex = pDISC.poll().second
        println(maxIndex)
        println(minIndex)
    }


}