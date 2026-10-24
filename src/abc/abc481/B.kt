package abc.abc481

// genre:
fun main() {
    val (n, k) = readln().split(" ").map { it.toInt() }
    val a = readln().split(" ").map { it.toInt() }
    var ans = a.count { it == k }
    for (i in 0 until n-1) {
        if(k in a[i]-1 until a[i+1] || k in a[i+1]-1 until a[i]) {
            ans+=1
        }
    }
    println(ans)

}