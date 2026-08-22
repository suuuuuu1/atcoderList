package abc.abc472

fun main() {
    val (h, w, k) = readln().split(" ").map { it.toInt() }
    val s = Array(h) {
        readln()
    }

    val dx = arrayOf(1, 0, -1, 0)
    val dy = arrayOf(0, 1, 0, -1)
    val anzenCount = Array(h) { IntArray(w) { 0 } }
    val anzen = Array(h) { IntArray(w) }
    for (i in 0 until h) {
        for (j in 0 until w) {
            if (s[i][j] == '#') {
                for (k in 0 until h) {
                    anzen[k][j] = 1
                }
                for (l in 0 until w) {
                    anzen[i][l] = 1
                }
                anzenCount[i][j] = Int.MAX_VALUE
            }
        }
    }


    val queue = ArrayDeque<Pair<Int, Int>>()
    for (i in 0 until h) {
        for (j in 0 until w) {
            if (anzen[i][j] == 0) {
                queue.add(Pair(i, j))

                var ima = k
                while (queue.isNotEmpty() && ima != 0) {

                    ima --
                    val (nowy, nowx) = queue.removeFirst()
                    for ((x, y) in (dx zip dy)) {
                        val nx = nowx + x
                        val ny = nowy + y

                        if (nx in 0 until w &&
                            ny in 0 until h &&
                            s[ny][nx] != '#' &&
                            anzenCount[ny][nx] <= anzenCount[nowy][nowx] &&
                            anzenCount[ny][nx] < k
                        ) {

                            queue.add(Pair(ny, nx))
                            anzenCount[ny][nx]++
                        }
                    }
                }
            }
        }
    }
    var ans = 0L
    for (ints in anzenCount) {
        for (i in ints) {
            if(i <= k)ans++
        }
    }
    println(ans)
}