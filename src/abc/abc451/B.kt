package abc.abc451

fun main() {
    val (n,m) = readln().split(" ").map{it.toInt()}
    val ab = Array(n){  readln().split(" ").map{it.toInt()}.toList()}

    var count = IntArray(m)
    for(i in 0 until n){
        for(j in 0 until m){
            if(ab[i][0] == j+1){
                count[j] --
            }
            if(ab[i][1] == j+1){
                count[j]++
            }
        }

    }
    for (i1 in count) {
        println(i1)
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