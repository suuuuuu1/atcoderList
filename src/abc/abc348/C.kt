package abc.abc348

import java.util.TreeSet

fun main() {
    val n = readln().toInt()
    val a = Array(n){readln().split(" ").map{it.toInt()}.toIntArray()}
    val hashmap = HashMap<Int,TreeSet<Int>>()
    for (ints in a) {
        val oisisa = ints[0]
        val iro = ints[1]
        hashmap.getOrPut(iro){TreeSet()}.add(oisisa)
    }
    var minMax = 0
    for (mutableEntry in hashmap.values) {
        val t = mutableEntry.first
        if(minMax < t){
            minMax = t
        }
    }
    println(minMax)
}