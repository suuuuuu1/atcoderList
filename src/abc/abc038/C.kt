package abc.abc038

fun main() {
    val n = readln().toInt()
    val a = readln().split(" ").map { it.toInt() }
    var r = 0
    var ans = 0L
    for (l in 0 until n) {
        if(r < l)r = l
        while(r+1 < n && a[r] < a[r+1]){
            r++
        }
        ans += (r -l +1)
    }
    println(ans)
}