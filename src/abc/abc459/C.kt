package abc.abc459

import java.util.HashMap
import java.util.TreeMap

fun main() {
    val (n,q) = readln().split(" ").map{it.toInt()}
    var minusSum = 0L
    val boxTotal = IntArray(n)
    var set = mutableSetOf(0)
    set.remove(0)
    val sb = StringBuilder()
    val tree = HashMap<Int,Int>()
    repeat(q){
        val (query , xy) = readln().split(" ").map{it.toInt()}
        val x = xy-1
        if(query == 1){
            boxTotal[x]++
            set.add(x)

            tree.put(boxTotal[x],tree.getOrDefault(boxTotal[x],0)+1)
            if(boxTotal[x] - minusSum > 1)tree.put(boxTotal[x]-1,tree.getOrDefault(boxTotal[x]-1,0)-1)

            if(set.size == n){
                minusSum++
                set = mutableSetOf()
                for (l in boxTotal) {
                    if(l > minusSum)set.add(l)
                }
            }
        }else{
            val y = xy
            var sum = 0
            for (key in tree.keys) {
                if(key - minusSum >= y){
                    sum += tree.getOrDefault(key,0)
                }
            }
            sb.append(sum).append("\n")
        }
    }
    println(sb)
}