---
title: 半分全列挙
category: 全探索・探索
acl: -
genres: 半分全列挙, 二分探索
order: 1070
summary: N≦40 の部分集合探索を半分ずつ列挙して組み合わせ、高速化する
level: 水
keywords: 半分全列挙, meet in the middle, 部分和, ナップサック, N≦40, 2^20, 分割統治, 部分集合の和
---

## 何ができる？

bit全探索 (2^N 通り) では間に合わない N ≦ 40 程度の問題を、
**前半と後半に分けてそれぞれ全列挙し、あとで組み合わせる** ことで解きます。

| 操作 | 内容 | 計算量 |
| --- | --- | --- |
| `allSubsetSums(a)` | a の部分集合の和 2^n 個をすべて求め、昇順に並べて返す | O(2^n) |
| `b.upperBound(x)` | 昇順の b で `b[i] > x` となる最小の i | O(n) ※ log(2^n) = n |
| 全体 (N 個を半分に分ける) | 前半 2^(N/2) 個 × 後半を二分探索 | O(2^(N/2) × N) |

N = 40 なら 2^40 ≒ 10^12 (無理) が、2^20 × 40 ≒ 4×10^7 (間に合う) になります。

## いつ使う？

- **N ≦ 40 くらい** で、各要素を「選ぶ / 選ばない」(またはいくつかの選択肢) から選ぶ
- 「合計が T 以下で最大」「合計がちょうど K になる選び方は何通り」など、**和で条件が決まる**
- 値が大きく (10^9 など)、DP (dp[合計]) では配列が作れない
- グリッドの経路で、左上から真ん中まで・右下から真ん中までをそれぞれ列挙して突き合わせる

## しくみ

選び方全体 = 「前半からの選び方」と「後半からの選び方」の組です。

```
a = [ 2, 3, 5 | 7, 11 ]        T = 17 以下で最大の和を求める
      前半           後半

前半の部分和 L (昇順): 0 2 3 5 5 7 8 10
後半の部分和 R (昇順): 0 7 11 18

L の各 x について「x + y <= T となる最大の y ∈ R」を二分探索で探す:
  x = 0  → y <= 17 で最大は 11 → 11
  x = 5  → y <= 12 で最大は 11 → 16
  x = 10 → y <= 7  で最大は 7  → 17  ← 最大
```

- 片方 (R) を **ソート** しておけば、相手は **二分探索** で O(log) で見つかります。
- 「ちょうど K になる個数」なら `R.upperBound(K - x) - R.lowerBound(K - x)` を足します。
- 部分和の列挙は、「今までの和の列」と「それに a[i] を足した列」を **マージ** すると、ソート済みのまま O(2^n) で作れます。

## 実装 (Kotlin)

```kotlin
// a の部分集合の和をすべて (2^n 個、重複あり) 昇順で返す。n <= 20 程度まで
fun allSubsetSums(a: LongArray): LongArray {
    var sums = longArrayOf(0L)                     // 何も選ばない
    for (x in a) {
        val added = LongArray(sums.size) { sums[it] + x }   // x を選ぶ場合 (これも昇順)
        // 2 つの昇順列をマージ
        val merged = LongArray(sums.size * 2)
        var i = 0
        var j = 0
        for (k in merged.indices) {
            if (j == added.size || (i < sums.size && sums[i] <= added[j])) {
                merged[k] = sums[i++]
            } else {
                merged[k] = added[j++]
            }
        }
        sums = merged
    }
    return sums
}

// 昇順ソート済みの配列で、a[i] > x となる最小の i を返す (無ければ size)
fun LongArray.upperBound(x: Long): Int {
    var ng = -1
    var ok = size
    while (ok - ng > 1) {
        val mid = (ng + ok) / 2
        if (this[mid] > x) ok = mid else ng = mid
    }
    return ok
}
```

## 使い方

[ABC 184 F - Programming Contest](https://atcoder.jp/contests/abc184/tasks/abc184_f) を解くコードです。
N ≦ 40 問の中からいくつか選び、合計時間が T 以下で最大になる値を出力します。

```kotlin
fun main() {
    val (nStr, tStr) = readln().split(" ")
    val n = nStr.toInt()
    val t = tStr.toLong()
    val a = readln().split(" ").map { it.toLong() }.toLongArray()

    val left = allSubsetSums(a.copyOfRange(0, n / 2))    // 前半
    val right = allSubsetSums(a.copyOfRange(n / 2, n))   // 後半
    var best = 0L
    for (x in left) {
        if (x > t) break                       // left は昇順なので、これ以降も全部超える
        val j = right.upperBound(t - x) - 1    // right[j] <= t - x となる最大の j
        best = maxOf(best, x + right[j])       // right[0] = 0 なので j >= 0
    }
    println(best)
}
```

入力例

```
5 17
2 3 5 7 11
```

出力例

```
17
```

## 例題

- [ABC 184 F - Programming Contest](https://atcoder.jp/contests/abc184/tasks/abc184_f) — 上の使い方の問題
- [典型90 051 - Typical Shop (★5)](https://atcoder.jp/contests/typical90/tasks/typical90_ay) — 「K 個選んで合計 P 円以下」の個数。前半・後半を「選んだ個数ごと」に分けて列挙し、二分探索で数える
- [ABC 271 F - XOR on Grid Path](https://atcoder.jp/contests/abc271/tasks/abc271_f) — グリッドの経路を、左上から対角線まで・右下から対角線までに分けて列挙し、XOR が一致する組を数える

## 注意点・よくあるミス

- 片側の個数は 2^(N/2)。N = 40 で 2^20 ≒ 10^6 なので、**LongArray** で持つ (List<Long> は遅くメモリも食う)。
- N が奇数のときは前半 N/2 個、後半 N - N/2 個。`copyOfRange(0, n / 2)` と `copyOfRange(n / 2, n)` で漏れなく分かれます。
- 組み合わせ側 (上の `right`) は **必ずソート** する。`allSubsetSums` はソート済みで返すのでそのまま使えます。
  bit全探索で作った場合は `sort()` を忘れずに。
- 二分探索の代わりに、left を小さい順・right を大きい順に見る **尺取り法** でも組み合わせられます (log が消える)。
- 和は Long で。10^9 × 40 は Int を超えます。
- 「ちょうど K の個数」は答えが 2^40 近くまで大きくなりうるので、カウントも Long で。
