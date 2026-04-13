package abc.abc453
import java.util.ArrayDeque

fun main() {
    val (h, w) = readln().split(" ").map { it.toInt() }
    val grid = Array(h) { readln() }

    val dy = intArrayOf(-1, 1, 0, 0)
    val dx = intArrayOf(0, 0, -1, 1)
    val dirChar = charArrayOf('U', 'D', 'L', 'R')

    var sy = 0; var sx = 0; var gy = 0; var gx = 0
    for (i in 0 until h) for (j in 0 until w) {
        if (grid[i][j] == 'S') { sy = i; sx = j }
        if (grid[i][j] == 'G') { gy = i; gx = j }
    }

    val visited = Array(h) { Array(w) { BooleanArray(5) } }
    val prev = Array(h) { Array(w) { arrayOfNulls<Triple<Int,Int,Int>>(5) } }

    val queue = ArrayDeque<Triple<Int,Int,Int>>()
    queue.add(Triple(sy, sx, 4))
    visited[sy][sx][4] = true

    var found = false
    var endDir = -1

    while (queue.isNotEmpty()) {
        val (cy, cx, cd) = queue.poll()
        val c = grid[cy][cx]

        for (d in 0 until 4) {
            if (c == 'o' && cd != d) continue
            if (c == 'x' && cd == d) continue

            val ny = cy + dy[d]
            val nx = cx + dx[d]
            if (ny !in 0 until h || nx !in 0 until w) continue
            if (grid[ny][nx] == '#') continue
            if (visited[ny][nx][d]) continue

            visited[ny][nx][d] = true
            prev[ny][nx][d] = Triple(cy, cx, cd)

            if (ny == gy && nx == gx) {
                found = true
                endDir = d
                break
            }
            queue.add(Triple(ny, nx, d))
        }
        if (found) break
    }

    if (!found) {
        println("No")
    } else {
        val path = StringBuilder()
        var cy = gy; var cx = gx; var cd = endDir
        while (cy != sy || cx != sx || cd != 4) {
            path.append(dirChar[cd])
            val (py, px, pd) = prev[cy][cx][cd]!!
            cy = py; cx = px; cd = pd
        }
        println("Yes")
        println(path.reverse())
    }
}