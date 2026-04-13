package abc.abc138

fun main() {
    val (n, q) = readln().split(" ").map { it.toInt() }
    val ki = Array(n) { mutableListOf<Int>() }
    for (i in 0 until n - 1) {
        val (a, b) = readln().split(" ").map { it.toInt() - 1 }
        ki[a].add(b)
        ki[b].add(a)
    }
    val ans = IntArray(n)
    fun dfs(v : Int, p : Int ){
        for (ints in ki[v]) {
           if(ints == p)continue
            ans[ints] += ans [v]
            dfs(ints,v)
        }
    }
    for(i in 0 until q){
        val (p,x) = readln().split(" ").map{it.toInt()}
        ans[p-1] += x
    }
    dfs(0,-1)
    for (an in ans) {
        print(an)
        print(" ")
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