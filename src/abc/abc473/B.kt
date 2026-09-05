package abc.abc473

fun main() {
    val n = readln().toInt()
    val a = readln().split(" ").map{it.toInt()}
    val b = HashMap<Int, Int>()
    for(i in 0 until n){
        b.put(a[i],b.getOrDefault(a[i],0)+1)
    }
    var ans = 0
    for (i in b.keys) {
        if(b[i]!! % 2 != 0){
            ans += i
        }
    }
    println(ans)
}