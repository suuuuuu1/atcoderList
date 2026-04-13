package abc.abc007

fun main() {
    val (r,c) = readln().split(" ").map{it.toInt()}
    val (sy,sx) = readln().split(" ").map{it.toInt()-1}
    val (gy,gx) = readln().split(" ").map{it.toInt()-1}
    val map = Array(r){
        readln()
    }
    val dydx = listOf(-1 to 0 ,1 to 0,0 to 1 ,0 to -1)
    val dist = Array(r){IntArray(c){-1} }
    val deque = ArrayDeque<Pair<Int,Int>>()
    dist [sy][sx] = 0
    deque.addLast(sy to sx)
    while(deque.isNotEmpty()){
        val (y,x) = deque.removeFirst()
        for ((dy,dx) in dydx){
            val ny = y + dy
            val nx = x + dx
            if(ny !in 0 until r || nx !in 0 until c)continue
            if(map[ny][nx] =='#')continue
            if(dist[ny][nx] != -1)continue

            dist[ny][nx] = dist[y][x] +1
            deque.addLast(ny to nx)
        }
    }
    println(dist[gy][gx])

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