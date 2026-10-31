package abc.abc482

import java.util.PriorityQueue

//https://suuuuuu1.github.io/atcoderList/#
fun main() {
    val (h,w,n,q) = readln().split(" ").map{it.toInt()}
    val rc = Array(n){readln().split(" ").map{it.toInt()}}
    val query = Array(q){readln().split(" ").map{it.toInt()}}

    val countY = mutableListOf<Int>()
    val countX = mutableListOf<Int>()
    for((r,c) in rc){
        countY.add(r)
        countX.add(c)
    }
    countY.sort()
    countX.sort()


    fun MutableList<Int>.lowerBound(x: Int): Int {
        var ng = -1
        var ok = size
        while (ok - ng > 1) {
            val mid = (ng + ok) / 2
            if (this[mid] >= x) ok = mid else ng = mid
        }
        return ok
    }
    fun MutableList<Int>.upperBound(x: Int): Int {
        var ng = -1
        var ok = size
        while (ok - ng > 1) {
            val mid = (ng + ok) / 2
            if (this[mid] > x) ok = mid else ng = mid
        }
        return ok
    }
    val sb = StringBuilder()
    for((num , u , d) in query){
        var ans = 0
        when(num){
            1->{
                val up = countY.lowerBound(u)
                val down = countY.upperBound(d)
                ans = down - up

            }
            2->{
                val l = u
                val r = d
                val left = countX.lowerBound(u)
                val right = countX.upperBound(d)
                ans = right -left
            }
        }
        sb.append(ans).append("\n")
    }

    println(sb)
}

