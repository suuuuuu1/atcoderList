package abc.abc374

fun main() {
    val (n, s) = readln().split(" ").map { it.toLong() }
    val a = readln().split(" ").map { it.toLong() }

    val total = a.sum()
    val rem = s % total

    // ケース 1: S が周期和の倍数なら、周期を丸ごと複数回取ればよい
    if (rem == 0L) {
        println("Yes")
        return
    }

    // ケース 2: 1 周期内で和が rem な部分列があるか尺取りで探す
    // 巡回を考慮するため 2N 長さで調査
    var sum = 0L
    var left = 0
    val nInt = n.toInt()  // ← ここで Long を Int に変換

    for (right in 0 until nInt * 2) {
        sum += a[right % nInt]  // ← nInt を使用

        // 左側を削る（和が rem を超えないようにする）
        while (sum > rem && left <= right) {
            sum -= a[left % nInt]  // ← nInt を使用
            left++
        }

        // 目標値に達したら
        if (sum == rem) {
            println("Yes")
            return
        }
    }

    println("No")
}
