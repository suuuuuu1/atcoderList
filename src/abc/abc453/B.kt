package abc.abc453

fun main() {
    val (t,x) = readln().split(" ").map{it.toInt()}
    val a = readln().split(" ").map{it.toInt()}
    var before = a[0]
    println("0 ${before}")
    for(i in 1 .. t){
        if(Math.abs(before - a[i]) >= x){
            before = a[i]
            println("$i ${a[i]}")
        }

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