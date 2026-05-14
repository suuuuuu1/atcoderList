package abc.abc457

import java.util.TreeSet
import kotlin.time.Duration.Companion.seconds

fun main() {
    val (n,k) = readln().split(" ").map{it.toLong()}
    val a = readln().split(" ").map{it.toLong()}

    val indexPulsSortedA = a.withIndex().sortedBy { it.value  }

    val tree = TreeSet(compareBy<IndexedValue<Long>>{it.value}.thenBy { it.index })
    for (value in indexPulsSortedA) {
        tree.add(value)
    }
    var minMax = 0L
    val min = minOf(k , 300000000)
    for(i in 0 until min){
        val get = tree.pollFirst()
        val oneUpget = get.value + (get.index+1)

        tree.add(IndexedValue(get.index,oneUpget))
        minMax = tree.first.value
    }
    println(minMax)
}