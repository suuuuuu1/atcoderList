package abc.abc473

fun main() {
    val (n,k) = readln().split(" ").map{it.toInt()}
    val a = readln().split(" ").map{it.toInt()}

    val b = HashMap<Int, Int>()
    for(i in 0 until n){
        b.put(a[i],b.getOrDefault(a[i],0)+1)
    }
    val c = mutableListOf<Int>()
    for (i in b.values) {
        c.add(i)
    }
    var ans = 0
    val max = c.max()
    for(i in 0 until c.size){
        if(c[i] >= max-1){
            ans++
        }
    }
    println(ans)
}