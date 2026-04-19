package abc.abc358

fun main() {
    // StringTokenizerを使わない安全な入力の定跡
    val line = readlnOrNull()?.trim()?.split(Regex("\\s+")) ?: return
    val n = line[0].toInt()
    val m = line[1].toInt()

    val input = Array(n) { readlnOrNull() ?: "" }

    // 「最小値」を求めたいので、あり得る最大値（全部の店に行く = n）で初期化しておく
    var minShops = n

    val t = 1 shl n // 2^N 通り

    // bit: 2 から 2^N - 1 までのすべての組み合わせを試す
    for (bit in 0 until t) {
        var shopCount = 0 // 今回の組み合わせで選んだ店舗数
        val checkList = BooleanArray(m) { false }

        // 各店舗(0番目 〜 n-1番目)について、行くかどうかを判定
        for (i in 0 until n) {
            // bit の i桁目が「1」なら、その店に行く
            if ((bit and (1 shl i)) != 0) {
                shopCount++ // 行く店舗をカウント

                // その店で買える味(0番目 〜 m-1番目)をチェックリストに登録
                for (j in 0 until m) {
                    if (input[i][j] == 'o') {
                        checkList[j] = true
                    }
                }
            }
        }

        // M種類の味がすべて揃っているか確認
        var ok = true
        for (j in 0 until m) {
            if (!checkList[j]) {
                ok = false
                break
            }
        }

        // すべて揃っていたら、店舗数の「最小値」を更新
        if (ok) {
            minShops = minOf(minShops, shopCount)
        }
    }

    println(minShops)
}