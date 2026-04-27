package abc.abc455

fun main() {
    val (h, w) = readln().split(" ").map { it.toInt() }
    val grid = Array(h) { readln() }
    var ans = 0

    for (h1 in 0 until h) {
        for (h2 in h1 until h) {
            for (w1 in 0 until w) {
                for (w2 in w1 until w) {
                    var check = true
                    for (i in 0 until h) {
                        for (j in 0 until w) {
                            if (h1 <= i &&
                                i <= h2 &&
                                w1 <= j &&
                                j <= w2
                            ) {
                                if(grid[i][j] != grid[h1+h2-i][w1+w2-j]){
                                    check = false
                                }
                            }
                        }
                    }
                    if(check)ans++

                }
            }
        }
    }
    println(ans)
}