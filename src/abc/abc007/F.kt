package abc.abc007

fun main(args: Array<String>) {
    var x = readln().toInt()
    val coins = arrayOf(500,100,50,10,5,1)
    var now = 0
    var ans = 0
    while(now != coins.size){
        val amari = x % coins[now]
        val shou = x / coins[now]
        now++
        x = amari
        ans +=  shou
    }
    print(ans)
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