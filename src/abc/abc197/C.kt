package abc.abc197

fun main(){
    val n = readln().toInt()
    val a = readln().split(" ").map { it.toInt() }
    var ans = Int.MAX_VALUE
    for (i in 0 until (1 shl (n-1))) {
        var orValue = 0
        var xorValue = 0
        for (j in 0 until n) {
            orValue = orValue or a[j]
            if(j == n-1 || (i and (1 shl j)) != 0){
                xorValue = xorValue xor orValue
                orValue = 0
            }
        }
        ans = minOf(ans , xorValue)
    }
    println(ans)
}