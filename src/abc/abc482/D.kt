package abc.abc482
 
//https://suuuuuu1.github.io/atcoderList/#
fun main() {
    val n = readln().toInt()
    val x = readln().split(" ").map{it.toInt()/2}
    val ans = IntArray(n)
    for(i in 0 until n){
        val left = x[i] - x[0]
        val right = x[n-1] - x[i]

        var min = Int.MAX_VALUE

        if(i > 0){
            val t = maxOf(left , right + x[i] - x[i-1])
            min = minOf(min, t)
        }
        if(i < n -1 ){
            val t = maxOf(right , left + x[i+1] - x[i])
            min = minOf(min, t)
        }
        ans[i] = if(n == 1) 0 else min
    }
    println(ans.joinToString("\n"))
}
