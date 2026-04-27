package abc.abc455
fun main() {
    // ご指定のシンプルな入力受け取り
    val (n, q) = readln().split(" ").map { it.toInt() }

    val up = IntArray(2 * n) { -1 }
    val down = IntArray(2 * n) { -1 }

    for (i in 0 until n) {
        up[n + i] = i
        down[i] = n + i
    }

    // ご指定の q 回ループと入力受け取り（-1のオフセット処理込み）
    repeat(q) {
        val (c, p) = readln().split(" ").map { it.toInt() - 1 }

        val d = down[c]
        down[c] = p
        up[p] = c

        if (d != -1) {
            up[d] = -1
        }
    }

    // 結果を配列に格納
    val result = IntArray(n)
    for (i in 0 until n) {
        var x = n + i
        var ans = 0
        while (up[x] != -1) {
            x = up[x]
            ans++
        }
        result[i] = ans
    }

    // 配列の要素をスペース区切りで一括出力（C++のループ出力の代替）
    println(result.joinToString(" "))
}