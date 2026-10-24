package abc.abc481

import java.util.TreeSet

//https://suuuuuu1.github.io/atcoderList/#
fun main() {
    val (n,m,s,t) = readln().split(" ").map{it.toInt()}
    val map = Array(n){ TreeSet<Int>() }
    val g = Array(n) { mutableListOf<Edge>() }
    repeat(m){
        val (a,b) = readln().split(" ").map{it.toInt()-1}
        map[a].add(b)
//        println(map.joinToString(","))
    }


    for(i in 0 until map.size){
        var first = true
        for(j in map[i]){
            if(first){
                g[i].add(Edge(j, 0))
                first =false
            }else{
                g[i].add(Edge(j, 1))
            }
        }
    }
//    println(g.joinToString(","))
    val ans = zeroOneBfs(g,s-1)
    ans.joinToString("")
    ans.toList()
    println(ans[t-1])

}
// 重み付きの辺: 行き先 to と重み cost (01-BFS では 0 か 1)
data class Edge(val to: Int, val cost: Long)

// 重み 0/1 のグラフで start からの最短距離。届かない頂点は -1
fun zeroOneBfs(g: Array<out List<Edge>>, start: Int): IntArray {
    val n = g.size
    val inf = Int.MAX_VALUE
    val dist = IntArray(n) { inf }
    val done = BooleanArray(n)
    val deque = ArrayDeque<Int>()
    dist[start] = 0
    deque.addLast(start)
    while (deque.isNotEmpty()) {
        val v = deque.removeFirst()
        if (done[v]) continue // もう確定している (古い情報)
        done[v] = true
        for (e in g[v]) {
            val nd = dist[v] + e.cost.toInt()
            if (nd < dist[e.to]) {
                dist[e.to] = nd
                if (e.cost == 0L) deque.addFirst(e.to) else deque.addLast(e.to)
            }
        }
    }
    for (i in 0 until n) if (dist[i] == inf) dist[i] = -1
    return dist
}
