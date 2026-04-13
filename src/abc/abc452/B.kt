package abc.abc452

fun main() {
    val (h,w) = readln().split(" ").map{it.toInt()}
    for(i in 0 until h){
        for(j in 0 until w){
            if(i == 0 || i == h -1 ){
                print("#")
            }else{
                if(j == 0 || j == w -1){
                    print("#")
                }else{
                    print(".")
                }
            }
        }
        println()
    }
}

@Suppress("unused")
private fun chmin(a: IntArray, idx: Int, v: Int): Boolean {
    if (v < a[idx]) {
        a[idx] = v
        return true
    }
    return false
}

@Suppress("unused")
private fun chmax(a: IntArray, idx: Int, v: Int): Boolean {
    if (v > a[idx]) {
        a[idx] = v
        return true
    }
    return false
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