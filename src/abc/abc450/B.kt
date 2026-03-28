package abc.abc450

fun main() {
    val n = readln().toInt()
    val c = Array(n){mutableListOf<Int>()}
    for(i in 0 until n-1){
        val tmp  = readln().split(" ").map{it.toInt()}.toMutableList()
        c[i] = tmp
    }


    for(i in 0 until n){
        for(j in i+1 until n){
            for(k in j+1 until n){

                if(c[i][j-1] + c[j-1][k-2] < c[i][k-2]){
                    println("Yes")
                    return
                }
            }
        }
    }
    println("No")

}

@Suppress("unused")
private fun chmin(a: LongArray, idx: Int, v: Long): Boolean {
    if (v < a[idx]) {
        a[idx] = v
        return true
    }
    return false
}

@Suppress("unused")
private fun chmax(a: LongArray, idx: Int, v: Long): Boolean {
    if (v > a[idx]) {
        a[idx] = v
        return true
    }
    return false
}

