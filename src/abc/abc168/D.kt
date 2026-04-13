package abc.abc168

import kotlin.coroutines.coroutineContext

fun main() {
    val (n,m) = readln().split(" ").map{it.toInt()}
    val heya = Array(n){ mutableListOf<Int>()}
    repeat(m){
        val (a,b) = readln().split(" ").map{it.toInt()-1}
        heya[a].add(b)
        heya[b].add(a)
    }
    val ans = IntArray(n){-1}
    val bfs = ArrayDeque<Int>()
    bfs.add(0)
    ans[0] = 0
    while (bfs.isNotEmpty()){
        val now = bfs.removeFirst()

        for (ints in heya[now]) {
            if(ans[ints] == -1){
                ans[ints] = now
                bfs.add(ints)
            }
        }
    }
    val sb = StringBuilder()
    var possible = true
    for (i in 1 until n) {
        if (ans[i] == -1) {
            possible = false
            break
        }
    }

    if (possible) {
        println("Yes")
        for(i in 1 until n){
            sb.append(ans[i] + 1).append("\n")
        }
        println(sb)
    } else {
        println("No")
    }
}

@Suppress("unused")
private fun chmin(a: IntArray, idx: Int, v: Int): Boolean {
    if (v < a[idx]) {
        a[idx] = v
        return true
    }
    return false
}

@Suppress("unused")
private fun chmax(a: IntArray, idx: Int, v: Int): Boolean {
    if (v > a[idx]) {
        a[idx] = v
        return true
    }
    return false
}

@Suppress("unused")
private fun chmin(a: LongArray, idx: Int, v: Long): Boolean {
    if (v < a[idx]) {
        a[idx] = v
        return true
    }
    return false
}

@Suppress("unused")
private fun chmax(a: LongArray, idx: Int, v: Long): Boolean {
    if (v > a[idx]) {
        a[idx] = v
        return true
    }
    return false
}