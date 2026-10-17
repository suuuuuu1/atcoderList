---
title: 累積和
category: 基本テクニック
acl: -
genres: 累積和
order: 2010
summary: 区間の和を前計算 O(N) のあと 1 回 O(1) で求める
level: 茶
keywords: prefix sum, cumulative sum, 区間和, 部分和, 二次元累積和, 長方形の和, 連続する区間, runningFold
---

## 何ができる？

配列が **途中で変わらない** とき、「l 番目から r-1 番目までの和」を何度でも一瞬で答えられます。

| 操作 | 内容 | 計算量 |
| --- | --- | --- |
| `PrefixSum(a)` | 長さ N の配列 a から累積和を作る | O(N) |
| `sum(l, r)` | a[l] + a[l+1] + ... + a[r-1] (半開区間 [l, r)) | O(1) |
| `PrefixSum2D(a)` | H × W の 2 次元配列から 2 次元累積和を作る | O(HW) |
| `sum(r1, c1, r2, c2)` | 行 [r1, r2) × 列 [c1, c2) の長方形の和 | O(1) |

**できないこと**: 途中で値を書き換えると作り直し (O(N)) になります。
更新があるなら [Fenwick Tree (BIT)](fenwicktree.md) や [セグメント木](segtree.md) を使います。

## いつ使う？

- 「区間 [L, R] の和を答えよ」というクエリが **たくさん** (Q ≤ 10^5 など) 来る
- 「連続する K 個の和の最大値」「長さ K の区間をすべて調べる」
- 2 次元: 「長方形の範囲にある点の個数・数の合計」をたくさん聞かれる
- 「条件を満たす数が 1〜x の中にいくつあるか」を何度も聞かれる
  → 条件を満たすなら 1、そうでなければ 0 の配列を作って累積和
- 毎回 for 文で足すと O(NQ) で間に合わない、という場面の定番の高速化

## しくみ

先頭からの和 `s[i] = a[0] + ... + a[i-1]` (s[0] = 0) を前もって作っておきます。
すると区間の和は **引き算 1 回** です。

```
a =    [ 3,  1,  4,  1,  5 ]
s = [0, 3,  4,  8,  9, 14]

a[1] + a[2] + a[3] = 1 + 4 + 1 = 6
                   = s[4] - s[1] = 9 - 3 = 6
```

`s` を長さ N+1 にして s[0] = 0 を入れておくと、l = 0 のときも場合分けがいりません。

**2 次元** の場合は `s[i][j]` = 左上 (0,0) から (i-1, j-1) までの長方形の和 を作ります。
長方形の和は「大きい長方形 − 上 − 左 + 重複して引いた左上」で求まります (包除原理)。

```
       c1      c2
   +----+-------+
   | D  |   B   |
r1 +----+-------+
   | C  |  答え  |
r2 +----+-------+

答え = s[r2][c2] - s[r1][c2] - s[r2][c1] + s[r1][c1]
        (全体)     (D+B)       (D+C)       (D を足し戻す)
```

## 実装 (Kotlin)

```kotlin
// 1 次元累積和。s[i] = a[0] + a[1] + ... + a[i-1] (s[0] = 0)
class PrefixSum(a: LongArray) {
    private val s = LongArray(a.size + 1)

    init {
        for (i in a.indices) s[i + 1] = s[i] + a[i]
    }

    // a[l] + a[l+1] + ... + a[r-1] (半開区間 [l, r))。l == r なら 0
    fun sum(l: Int, r: Int): Long {
        require(0 <= l && l <= r && r < s.size)
        return s[r] - s[l]
    }
}

// 2 次元累積和。s[i][j] = 左上 (0, 0) から (i-1, j-1) までの長方形の和
class PrefixSum2D(a: Array<LongArray>) {
    private val h = a.size
    private val w = if (a.isEmpty()) 0 else a[0].size
    private val s = Array(h + 1) { LongArray(w + 1) }

    init {
        for (i in 0 until h) for (j in 0 until w) {
            s[i + 1][j + 1] = s[i][j + 1] + s[i + 1][j] - s[i][j] + a[i][j]
        }
    }

    // 行 [r1, r2) × 列 [c1, c2) の長方形の和
    fun sum(r1: Int, c1: Int, r2: Int, c2: Int): Long {
        require(0 <= r1 && r1 <= r2 && r2 <= h && 0 <= c1 && c1 <= c2 && c2 <= w)
        return s[r2][c2] - s[r1][c2] - s[r2][c1] + s[r1][c1]
    }
}
```

## 使い方

1 次元: 長さ N の数列と Q 個の質問 `L R` (1-indexed、両端を含む) に対し、A_L + ... + A_R を答えます。

```kotlin
fun main() {
    val br = System.`in`.bufferedReader()
    var st = java.util.StringTokenizer("")
    fun next(): String {
        while (!st.hasMoreTokens()) st = java.util.StringTokenizer(br.readLine())
        return st.nextToken()
    }

    val n = next().toInt()
    val q = next().toInt()
    val a = LongArray(n) { next().toLong() }
    val ps = PrefixSum(a)
    val sb = StringBuilder()
    repeat(q) {
        val l = next().toInt() - 1 // 0-indexed に直す
        val r = next().toInt()     // 「R を含む」= 半開区間の右端は R (0-indexed で R-1 の次)
        sb.appendLine(ps.sum(l, r))
    }
    print(sb)
}
```

入力例

```
5 3
3 1 4 1 5
2 4
1 5
3 3
```

出力例

```
6
14
4
```

2 次元: H × W のマス目の数と、Q 個の質問 `a b c d` (a〜c 行目、b〜d 列目。1-indexed、両端を含む) に対し、長方形の和を答えます。

```kotlin
fun main() {
    val br = System.`in`.bufferedReader()
    var st = java.util.StringTokenizer("")
    fun next(): String {
        while (!st.hasMoreTokens()) st = java.util.StringTokenizer(br.readLine())
        return st.nextToken()
    }

    val h = next().toInt()
    val w = next().toInt()
    val a = Array(h) { LongArray(w) { next().toLong() } }
    val ps = PrefixSum2D(a)
    val q = next().toInt()
    val sb = StringBuilder()
    repeat(q) {
        val r1 = next().toInt() - 1
        val c1 = next().toInt() - 1
        val r2 = next().toInt()
        val c2 = next().toInt()
        sb.appendLine(ps.sum(r1, c1, r2, c2))
    }
    print(sb)
}
```

入力例

```
3 4
1 2 3 4
5 6 7 8
9 10 11 12
3
1 1 3 4
2 2 3 3
1 4 1 4
```

出力例

```
78
34
4
```

## 例題

- [典型90 010 - Score Sum Queries（★2）](https://atcoder.jp/contests/typical90/tasks/typical90_j) — クラスごとに累積和を 2 本作る、1 次元累積和そのもの
- [ABC 084 D - 2017-like Number](https://atcoder.jp/contests/abc084/tasks/abc084_d) — 「条件を満たすなら 1」の配列を作って累積和で個数を数える
- [ABC 106 D - AtCoder Express 2](https://atcoder.jp/contests/abc106/tasks/abc106_d) — (左端, 右端) の 2 次元表に数を数えて、2 次元累積和

## 注意点・よくあるミス

- **オーバーフロー**: 和は大きくなりやすいので `Long` で持つ。10^9 × 2×10^5 は Int に入りません。
- **添字のずれ**: s の長さは N+1。「1-indexed で L〜R (両端含む)」は 0-indexed の半開区間 `[L-1, R)` なので `sum(L-1, R)`。
- 2 次元の作り方で `- s[i][j]` (左上を引く) を忘れると、重なった部分が 2 回足されます。
- 途中で配列を書き換える問題では使えません (BIT やセグメント木へ)。
- 手早く書くなら `val s = a.runningFold(0L) { acc, x -> acc + x }` でも 1 次元累積和 (長さ N+1 の List) が作れます。
- 累積「和」以外にも、XOR (引き算の代わりに XOR) や、mod 付きの和でも同じ形で使えます。
  ただし最大値・最小値は引き算ができないので累積和では区間クエリに答えられません。
