package abc.abc452

fun main() {
    val n = readln().toInt()
    val ab = Array(n){  readln().split(" ").map{it.toInt()}.toMutableList()}
    val m = readln().toInt()
    val s = Array(11){HashMap<Int,MutableSet<Char>>()}
    val t = Array(m){ readln()}
    for(i in 0 until m){
        val tSize = t[i].length
        for(j in 0 until t[i].length){
            s[tSize].putIfAbsent(j+1, mutableSetOf())
            s[tSize].get(j+1)?.add(t[i][j])
        }

    }


    val sb = StringBuilder()
    for(i in 0 until m){
        if(t[i].length != n){
            sb.append("No\n")
            continue
        }
        var check = true
        for(j in 0 until n){
            if(!s[ab[j][0]].get(ab[j][1],)?.contains(t[i][j])!!){
                check = false
                break
            }
        }
        if(check){
            sb.append("Yes")
        }else{
            sb.append("No")
        }
        sb.append("\n")


    }
    println(sb)
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