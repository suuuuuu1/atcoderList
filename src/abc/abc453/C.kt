package abc.abc453

import kotlin.math.abs

fun main() {

    val n = readln().toInt()
    val l = readln().split(" ").map{it.toInt()}

    var num = 1 shl n
    var ans = 0
    for(i in 0 until num){
        var nowPos = 0.5
        var sum = 0
        for(j in 0 until n){
            val beforPos = nowPos
            if(i and (1 shl j) == 0){
                nowPos+=l[j]
            }else{
                nowPos-=l[j]
            }
            if((beforPos < 0 && nowPos > 0) || (beforPos > 0 && nowPos < 0)){
                sum ++
            }

        }
        ans = maxOf(ans,sum )
    }
    println(ans)
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