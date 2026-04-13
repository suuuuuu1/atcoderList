package atc.atc001

fun main() {
    val (h, w) = readln().split(" ").map { it.toInt() }
    val dydx = listOf(
        -1 to 0,
        1 to 0,
        0 to -1,
        0 to 1
    )

    val map = Array(h) { readln() }
    val dist = Array(h) { IntArray(w) { -1 } }
    var st = 0 to 0
    var goal = 0 to 0

    for (i in 0 until h) {
        for (j in 0 until w) {
            if (map[i][j] == 's') {
                st = i to j
            }
            if (map[i][j] == 'g') {
                goal = i to j
            }
        }
    }
    dist[st.first][st.second] = 1
    if (dfs(dist, dydx, st, goal, st.second, st.first, map, h, w)) {
        println("Yes")
        return
    }

    println("No")

}

fun dfs(
    dist: Array<IntArray>,
    dydx: List<Pair<Int, Int>>,
    s: Pair<Int, Int>,
    g: Pair<Int, Int>,
    nx: Int,
    ny: Int,
    map: Array<String>,
    h: Int,
    w: Int
): Boolean {
    var x: Int
    var y: Int
    for ((dy, dx) in dydx) {
        x = nx + dx
        y = ny + dy
        if (y in 0 until h && x in 0 until w && map[y][x] != '#') {
            if (dist[y][x] == -1) {
                dist[y][x] = 1
                if (map[y][x] == 'g') return true
                if (dfs(dist, dydx, s, g, x, y, map, h, w)) return true
            }
        }


    }

    return false
}
//ggyG


