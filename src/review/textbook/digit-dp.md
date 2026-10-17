---
title: 桁 DP
category: DP
acl: -
genres: 桁DP, DP
order: 3060
summary: N 以下の整数のうち条件を満たすものの個数を、上の桁から決めて数える
level: 水
keywords: 桁DP, digit dp, N以下, 以下の整数, 各桁の和, 桁和, 0でない桁, 巨大な数, 10^10000, smaller フラグ
---

## 何ができる？

「**1 以上 N 以下** の整数のうち、条件を満たすものはいくつ？」を、N が 10^10000 のように巨大でも数えられます。
N を **文字列** として受け取り、上の桁から 1 桁ずつ数字を決めていく DP です。

| 関数 | 内容 | 計算量 |
| --- | --- | --- |
| `countDigitSumMultiple(n, d)` | 1〜N で「各桁の和が d の倍数」のものの個数 (mod 10^9+7) | O(桁数 × d × 10) |
| `countNonZeroDigits(n, k)` | 1〜N で「0 でない桁がちょうど k 個」のものの個数 | O(桁数 × k × 10) |

## いつ使う？

- 「**N 以下** の正の整数のうち、〜を満たすものの個数」
- N が 10^18 や 10^10000 のように大きすぎて、1 つずつ調べられない
- 条件が **各桁の数字** だけで決まる (桁の和、3 を含む、0 でない桁の個数、d で割った余り…)
- 「L 以上 R 以下」→ `f(R) - f(L - 1)` (f は「N 以下の個数」) に分ける

## しくみ

上の桁から数字を決めていくとき、「ここまでの桁で **N より小さいことが確定したか**」が大事です。

- **確定した (smaller = 1)**: 残りの桁は 0〜9 を自由に置ける
- **まだ N とぴったり同じ (smaller = 0)**: 次の桁は 0〜(N のその桁の数字) までしか置けない。
  N の数字より小さい数字を置いたら、そこで smaller = 1 になる

```
N = 3 5 2 のとき、上の桁から:

1 桁目: 0,1,2 を置く → smaller=1 (以降 0〜9 自由)
        3 を置く     → smaller=0 (まだぴったり)
2 桁目 (smaller=0 から): 0〜4 → smaller=1 / 5 → smaller=0
3 桁目 (smaller=0 から): 0〜1 → smaller=1 / 2 → smaller=0 (= N 自身)
```

状態は `dp[smaller][条件に必要な情報]` です。

- 桁の和が d の倍数 → 情報は「桁の和 mod d」
- 0 でない桁が k 個 → 情報は「ここまでの 0 でない桁の個数 (k を超えたら捨てる)」

最後に `dp[0][..] + dp[1][..]` (N ちょうどと、N 未満) を足すと「0 以上 N 以下」の個数になります。
**0 も数えている** ので、条件を 0 が満たすなら 1 引きます。

先頭の 0 は「桁数が少ない数」を表します (例: 3 桁で 052 = 52)。なので桁数の違う数も一緒に数えられます。

## 実装 (Kotlin)

```kotlin
// 1 以上 n 以下で「各桁の和が d の倍数」の個数 mod 1e9+7 (n は 10 進の文字列)
fun countDigitSumMultiple(n: String, d: Int): Long {
    val MOD = 1_000_000_007L
    // dp[smaller][r]: smaller=1 なら n 未満が確定、0 なら n とぴったり同じ。r = 桁の和 mod d
    var dp = Array(2) { LongArray(d) }
    dp[0][0] = 1
    for (ch in n) {
        val c = ch - '0'
        val ndp = Array(2) { LongArray(d) }
        for (smaller in 0..1) {
            for (r in 0 until d) {
                val cur = dp[smaller][r]
                if (cur == 0L) continue
                val lim = if (smaller == 1) 9 else c  // この桁に置ける最大の数字
                for (x in 0..lim) {
                    val ns = if (smaller == 1 || x < c) 1 else 0
                    val nr = (r + x) % d
                    ndp[ns][nr] = (ndp[ns][nr] + cur) % MOD
                }
            }
        }
        dp = ndp
    }
    // 0 も数えている (桁の和 0 は d の倍数) ので 1 引く
    return ((dp[0][0] + dp[1][0] - 1) % MOD + MOD) % MOD
}

// 1 以上 n 以下で「0 でない桁がちょうど k 個」の個数 (k >= 1。答えが Long に収まる範囲で)
fun countNonZeroDigits(n: String, k: Int): Long {
    // dp[smaller][j]: j = ここまでの 0 でない桁の個数 (k を超えたものは捨てる)
    var dp = Array(2) { LongArray(k + 1) }
    dp[0][0] = 1
    for (ch in n) {
        val c = ch - '0'
        val ndp = Array(2) { LongArray(k + 1) }
        for (smaller in 0..1) {
            for (j in 0..k) {
                val cur = dp[smaller][j]
                if (cur == 0L) continue
                val lim = if (smaller == 1) 9 else c
                for (x in 0..lim) {
                    val nj = if (x != 0) j + 1 else j
                    if (nj > k) continue
                    val ns = if (smaller == 1 || x < c) 1 else 0
                    ndp[ns][nj] += cur
                }
            }
        }
        dp = ndp
    }
    return dp[0][k] + dp[1][k]  // k >= 1 なので 0 は数えられていない
}
```

## 使い方

[EDPC S - Digit Sum](https://atcoder.jp/contests/dp/tasks/dp_s) を解くコードです (1〜K で、桁の和が D の倍数の個数 mod 10^9+7。K < 10^10000)。

```kotlin
fun main() {
    val br = System.`in`.bufferedReader()
    val k = br.readLine().trim()
    val d = br.readLine().trim().toInt()
    println(countDigitSumMultiple(k, d))

    // ABC 154 E - Almost Everywhere Zero なら:
    // val n = br.readLine().trim(); val kk = br.readLine().trim().toInt()
    // println(countNonZeroDigits(n, kk))
}
```

入力例

```
30
4
```

出力例

```
6
```

(4, 8, 13, 17, 22, 26 の 6 個)

入力例2

```
1000000009
1
```

出力例2

```
2
```

(1〜1000000009 の全部 = 1000000009 個。mod 10^9+7 で 2)

## 例題

- [EDPC S - Digit Sum](https://atcoder.jp/contests/dp/tasks/dp_s) — 桁 DP の基本。`countDigitSumMultiple` そのもの
- [ABC 154 E - Almost Everywhere Zero](https://atcoder.jp/contests/abc154/tasks/abc154_e) — N < 10^100、0 でない桁がちょうど K 個 (K ≦ 3)。`countNonZeroDigits` そのもの

## 注意点・よくあるミス

- **N は文字列で読む**。10^10000 は Long に入りません。10^18 程度でも、`toString()` してから桁 DP するのが楽です。
- **0 を数えていないか** 確認する。「1 以上」なら、0 が条件を満たすとき最後に 1 引く (mod のときは `+ MOD` してから % MOD)。
- 「L 以上 R 以下」は `f(R) - f(L - 1)`。L - 1 を文字列で作るのが面倒なら、`f(R) - f(L) + (L 自身が条件を満たすなら 1)`。
- smaller の遷移: smaller = 0 のとき、置ける数字は **0〜c (c は N のその桁)**。c を置いたときだけ smaller = 0 のまま。
- 状態の情報 (mod d や個数) は、上限を決めて配列のサイズを小さく保つ。個数系は「k を超えたら捨てる」。
- 「隣り合う桁が同じでない」のような条件は、状態に「直前の桁の数字」と「まだ先頭の 0 が続いているか」を追加します。
