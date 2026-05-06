package abc.abc334

import kotlin.math.abs

fun main() {
    val (n,k) = readln().split(" ").map{it.toInt()}
    val a = readln().split(" ").map{it.toInt()}


    if((2 * n - k) % 2 == 0){
        var ans = 0L
        for(i in 0 until k-1 step 2) {
            ans += Math.abs(a[i] - a[i+1])
        }

        println(ans)
    }else{
        val m = k / 2
        val fRuisekiwa = LongArray(m+1)
        val lRuisekiwa = LongArray(m+1)
        for(i in 0 until m){
            fRuisekiwa[i+1] = fRuisekiwa[i] + (a[2*i+1] - a[2*i])
        }
        for(i in 0 until m){
            lRuisekiwa[i+1] = lRuisekiwa[i] + (a[k - 1 -2 * i] - a[k -2 -2 * i])
        }
        var ans = Long.MAX_VALUE
        for(i in 0 .. m ){
            ans = minOf(ans,fRuisekiwa[i] + lRuisekiwa[m-i])
        }
        println(ans)
    }
}