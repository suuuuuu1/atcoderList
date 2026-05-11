package abc.abc375
fun main() {
    val n = readln().toInt()
    // readln().toCharArray() の方針はそのまま活用できます
    val a = Array(n) { readln().toCharArray() }
    val ans = Array(n) { CharArray(n) }

    for (r in 0 until n) {
        for (c in 0 until n) {
            // 中心からどれだけ離れているか（何番目のリングか）を計算 (0-indexed)
            // minOf を使って上下左右の端からの最小距離を求める
            val d = minOf(r, c, n - 1 - r, n - 1 - c) + 1

            // 回転回数は リングの深さ d % 4 で決まる
            val rotations = d % 4

            var nr = r
            var nc = c

            // 必要な回数だけ90度時計回りに座標を回転させる
            repeat(rotations) {
                val temp = nr
                nr = nc
                nc = n - 1 - temp
            }

            // 計算した移動先の座標に元の文字を配置
            ans[nr][nc] = a[r][c]
        }
    }

    // 結果の出力
    ans.forEach { println(it.joinToString("")) }
}
