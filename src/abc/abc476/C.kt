package abc.abc476

import java.util.PriorityQueue

fun main() {
    val n = readln().toInt()
    val a = readln().split(" ").map { it.toInt() }.toMutableList()
    val yuusendo = mutableListOf<Int>()
    for (i in 0 until 3) {
        yuusendo.add(a[i])
    }
    val sort = yuusendo.sorted().reversed().toMutableList()
    val ans = mutableListOf<Int>()
    ans.add(sort[2])
    for (i in 3 until n) {

            if(sort[0] < a[i]){
                sort.removeLast()
                sort.addFirst(a[i])
            } else if(sort[1] < a[i]){
                sort.removeLast()
                sort.add(1, a[i])
            } else if(sort[2] < a[i]){
                sort.removeLast()
                sort.addLast(a[i])
            }
        ans.add(sort[2])
    }
    println(ans.joinToString("\n"))
}