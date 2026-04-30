package abc.abc353
fun main() {
    val n = readln().toInt()
    // 10^8 を超える計算があるため LongArray を使用
    val a = readln().split(" ").map { it.toLong() }.toLongArray()
    val mod = 100_000_000L

    // 1. (N-1) * 全体の合計を計算
    var totalSum = 0L
    for (x in a) {
        totalSum += x * (n - 1)
    }

    // 2. A[i] + A[j] >= 10^8 となるペアの数を数えるためにソート
    a.sort()

    var count = 0L
    var r = n - 1
    for (l in 0 until n) {
        // a[l] に対して a[l] + a[r] >= mod を満たす最小の r を探す
        // 重複を防ぐため r > l の範囲で調整
        while (r > l && a[l] + a[r] >= mod) {
            r--
        }
        // r より大きいインデックスの要素（lより大きいもの）はすべて条件を満たす
        val num = n - 1 - maxOf(l, r)
        count += num
    }

    // 3. 全体の和から (条件を満たすペア数 * 10^8) を引く
    println(totalSum - count * mod)
}