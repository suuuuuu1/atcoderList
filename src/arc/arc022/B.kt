
package arc.arc022

fun main() {
    val n = readln().toInt()
    val a = readln().split(" ").map { it.toInt() }
    var r = 0
    var ans = 0
    val set = mutableSetOf<Int>()
    for (l in 0 until n) {
        while (r < n && !set.contains(a[r])){
            set.add(a[r])
            r++
        }
        ans = maxOf(ans,r-l)
        set.remove(a[l])
    }
    println(ans)
}