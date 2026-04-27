package abc.abc334

import kotlin.math.abs

fun main() {
    val (n, k) = readln().split(" ").map { it.toInt() }
    val a = readln().split(" ").map { it.toInt() }
    var ans = 0L
    if ((2 * n - k) % 2 == 0) {
        for (i in 0 until k step 2) {
            ans += Math.abs(a[i] - a[i + 1])
        }
    } else {
        val pre = LongArray(k + 1)
        val suf = LongArray(k + 1)
        for(i in 2 .. k step 2){
            pre[i] = pre[i -2] + abs(a[i-2] - a[i-1]).toLong()
        }
        for(i in k-2 downTo 0 step 2 ){
            suf[i] = suf[i+2] + abs(a[i] - a[i+1]).toLong()
        }
        ans = Long.MAX_VALUE
        for(i in 0 until k step 2){
            ans = minOf(ans,pre[i] + suf[i+1])
        }
    }
    println(ans)
}