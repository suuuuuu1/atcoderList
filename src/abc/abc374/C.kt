package abc.abc374


fun main() {
    val n = readln().toInt()
    val k = readln().split(" ").map { it.toInt() }
    var num = 1 shl n
    var ans = Int.MAX_VALUE
    for (i in 0 until num) {
        var a = 0
        var b = 0
        for (j in 0 until n) {
            println(1 shl j)
            if(i and (1 shl j)  == 0){
                a+=k[j]
            }else{
                b+=k[j]
            }
        }
        ans = minOf(maxOf(a,b),ans)

    }
    println(ans)
}