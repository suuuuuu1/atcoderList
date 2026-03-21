package abc.abc032

fun main() {
    val (n, k) = readln().split(" ").map { it.toInt() }
    val list = IntArray(n)
    repeat(n) {
        list[it] = readln().toInt()
    }
    var r = 0
    var ans = 0
    var multiply = 1L
    if(list.contains(0)){
        println(n)
        return
    }
    for (l in list.indices) {
        while (r < n && multiply * list[r] <= k) {
            multiply *= list[r]
            r++
        }
        ans = maxOf(ans, r - l)
        if(r == l){
            r++
        }else{
            if(list[l] == 0)multiply = 0
            else multiply/=list[l]
        }
    }
    println(ans)

}
