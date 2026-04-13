package abc.abc452

fun main() {
    val (m,d) = readln().split(" ").map{it.toInt()}
    var chenk = false
    if(m == 1 && d == 7){
        chenk = true
    }
    if(m == 3 && d == 3){
        chenk = true
    }
    if(m == 5 && d == 5){
        chenk = true
    }
    if(m == 7 && d == 7){
        chenk = true
    }
    if(m == 9 && d == 9){
        chenk = true
    }
    if(chenk){
        println("Yes")
    }else{
        println("No")
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