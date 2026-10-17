---
title: 区間 DP
category: DP
acl: -
genres: 区間DP, DP
order: 3040
summary: 区間 [l, r) ごとの答えを短い区間から順に求める DP
level: 水
keywords: 区間DP, interval dp, 区間の分割, 合体, スライム, 両端から取る, ゲーム, 隣り合う, マージ
---

## 何ができる？

列の **区間 [l, r)** ごとに答え `dp[l][r]` を持ち、**短い区間から長い区間へ** 順に埋める DP です。

| 典型パターン | 遷移 | 計算量 |
| --- | --- | --- |
| 区間を 2 つに分ける (スライムの合体など) | `dp[l][r] = min(dp[l][m] + dp[m][r]) + コスト` (m を全部試す) | O(N^3) |
| 両端から 1 つずつ取る (Deque ゲームなど) | `dp[l][r]` を `dp[l+1][r]` と `dp[l][r-1]` から決める | O(N^2) |

このページの実装:

| 関数 | 内容 | 計算量 |
| --- | --- | --- |
| `slimes(a)` | 隣り合う 2 つを合体 (コスト = 大きさの和) して 1 つにする最小コスト | O(N^3) |
| `deque(a)` | 両端から交互に取るゲームで (先手の得点 − 後手の得点) | O(N^2) |

## いつ使う？

- 「**隣り合う** 2 つをくっつける / 消す」操作を繰り返す
- 「列の **両端** から取る」ゲームや操作
- 最後に 1 つ (または空) になるまで操作して、コストの最小・最大を聞かれる
- N ≦ 400 程度 (O(N^3)) や N ≦ 3000 程度 (O(N^2)) で、列の左右の位置が大事そうなとき
- 「前から順に」の DP だと、どこがくっついたか表せずに困るとき

## しくみ

### 区間を 2 つに分ける (スライム)

区間 [l, r) を最後に 1 つにする直前には、必ず「[l, m) が 1 つ」と「[m, r) が 1 つ」になっています。
その境目 m を全部試します。最後の合体のコストは、区間の和 (累積和で O(1)) です。

```
dp[l][r] = min_{l < m < r} ( dp[l][m] + dp[m][r] ) + (a[l] + ... + a[r-1])
dp[l][l+1] = 0     (1 匹だけなら合体不要)

a = 10 20 30 40
  [10 20 30 40]  の最後の合体の境目は 3 通り
  [10] [20 30 40] / [10 20] [30 40] / [10 20 30] [40]
```

**大事**: `dp[l][r]` を計算するとき、もっと短い区間の値がすでに必要です。
なので **外側のループを区間の長さ len (2, 3, ..., N)** にします。`for (l in 0..n) for (r in ...)` と素直に書くとうまくいきません。

### 両端から取る (Deque)

`dp[l][r]` = 残りが [l, r) で、**今の手番の人** から見た (自分の得点 − 相手の得点) の最大値。

```
dp[l][r] = max( a[l]   - dp[l+1][r],      (左端を取る。その後は相手の手番)
                a[r-1] - dp[l][r-1] )     (右端を取る)
dp[l][l] = 0
```

「相手の手番の値」を引くだけで、先手・後手を区別せずに書けるのがポイントです。

## 実装 (Kotlin)

```kotlin
// スライム (EDPC N): 隣り合う 2 つを合体 (コスト = 大きさの和) し、1 つにする最小コスト
fun slimes(a: LongArray): Long {
    val n = a.size
    val sum = LongArray(n + 1)  // 累積和
    for (i in 0 until n) sum[i + 1] = sum[i] + a[i]
    // dp[l][r] = 区間 [l, r) を 1 つにする最小コスト (半開区間)
    val dp = Array(n + 1) { LongArray(n + 1) }
    for (len in 2..n) {             // 短い区間から順に
        for (l in 0..n - len) {
            val r = l + len
            var best = Long.MAX_VALUE
            for (m in l + 1 until r) {  // 最後の合体の境目
                best = minOf(best, dp[l][m] + dp[m][r])
            }
            dp[l][r] = best + (sum[r] - sum[l])
        }
    }
    return dp[0][n]
}

// Deque (EDPC L): 両端のどちらかを交互に取る。先手の得点 - 後手の得点 (両者最善)
fun deque(a: LongArray): Long {
    val n = a.size
    // dp[l][r] = 残りが [l, r) のときの (手番の人の得点 - 相手の得点) の最大値
    val dp = Array(n + 1) { LongArray(n + 1) }
    for (len in 1..n) {
        for (l in 0..n - len) {
            val r = l + len
            dp[l][r] = maxOf(a[l] - dp[l + 1][r], a[r - 1] - dp[l][r - 1])
        }
    }
    return dp[0][n]
}
```

## 使い方

[EDPC N - Slimes](https://atcoder.jp/contests/dp/tasks/dp_n) を解くコードです。

```kotlin
fun main() {
    val br = System.`in`.bufferedReader()
    var st = java.util.StringTokenizer("")
    fun next(): String {
        while (!st.hasMoreTokens()) st = java.util.StringTokenizer(br.readLine())
        return st.nextToken()
    }

    val n = next().toInt()
    val a = LongArray(n) { next().toLong() }
    println(slimes(a))

    // EDPC L - Deque なら: println(deque(a))
}
```

入力例

```
4
10 20 30 40
```

出力例

```
190
```

## 例題

- [EDPC N - Slimes](https://atcoder.jp/contests/dp/tasks/dp_n) — 区間を 2 つに分ける区間 DP の基本。`slimes` そのもの
- [EDPC L - Deque](https://atcoder.jp/contests/dp/tasks/dp_l) — 両端から取るゲーム。`deque` そのもの
- [典型90 019 - Pick Two](https://atcoder.jp/contests/typical90/tasks/typical90_s) — 隣り合う 2 つを消していく。
  `dp[l][r]` = 区間を全部消す最小コストで、「l と r-1 をペアにする」場合と「2 つに分ける」場合を考える

## 注意点・よくあるミス

- **ループの順番**: 外側を区間の長さ len にする。または `l` を大きい方から (`for (l in n - 1 downTo 0)`)、`r` を小さい方から回す。
- 閉区間 [l, r] と半開区間 [l, r) を混ぜない。上の実装は半開区間で、配列サイズは `n + 1`。
- O(N^3) は N = 400 で 6.4×10^7 回程度。ループの中で `List` や `Pair` を作らない。2 次元配列は `Array(n + 1) { LongArray(n + 1) }`。
- 区間の和は毎回足し直さず、**累積和** で求める。
- min を取る初期値に `Long.MAX_VALUE` を使ったら、それに足し算する前に必ず min で置き換わっているか確認する
  (上の実装では len ≧ 2 なので m が必ず 1 つ以上あり、安全)。
- ゲームの DP は「今の手番の人から見た値」で持つと、先手・後手で場合分けしなくて済みます。
