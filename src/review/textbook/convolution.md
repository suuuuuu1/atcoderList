---
title: 畳み込み (NTT)
category: 数学
acl: convolution
genres: 畳み込み, 数学, 組み合わせ, DP
order: 250
summary: 多項式の掛け算 (畳み込み) を O(N log N) で計算する
level: 青
keywords: NTT, FFT, 多項式, 掛け算, 畳み込み, 数論変換
---

## 何ができる？

長さ N の配列 a と長さ M の配列 b から、

```
c[k] = Σ_{i + j = k} a[i] × b[j]   (mod 998244353)    k = 0, 1, …, N+M-2
```

を計算します。これは **多項式の掛け算の係数** そのものです。

| 関数 | 内容 | 計算量 |
| --- | --- | --- |
| `convolution(a, b)` | 上の c を返す (長さ N+M-1)。どちらかが空なら空配列 (ACL の `convolution`) | O((N+M) log(N+M)) |

愚直に 2 重ループすると O(NM) で、N = M = 5×10^5 だと 2.5×10^11 回なので間に合いません。

## いつ使う？

- **「和が k になる組の数」** を全部の k について求めたい (a の値 + b の値 = k)
- サイコロ・カード・品物を組み合わせたときの **合計値ごとの場合の数**
- 多項式 (母関数) の掛け算がそのまま答えになる数え上げ
- DP の遷移が `dp2[k] = Σ dp[i] × w[k - i]` の形をしている
- 差が d になるペア数 (片方を反転して畳み込む)、文字列のパターン一致の数え上げ
- 答えを **998244353 で割った余り** で出力する問題 (この mod 専用。他の mod や正確な値には工夫が必要)

## しくみ

### 例 1: 多項式の掛け算

```
(1 + 2x + 3x^2) × (4 + 5x)
  a = [1, 2, 3], b = [4, 5]

               x^0  x^1  x^2  x^3
1    × (4+5x)    4    5
2x   × (4+5x)         8   10
3x^2 × (4+5x)             12   15
-----------------------------------
c =              4   13   22   15
```

c[k] は「次数の和が k になる組 (i, j) の a[i]·b[j] の合計」で、まさに畳み込みの式です。

### 例 2: サイコロの和の場合の数

サイコロ 1 個で出目が s になる場合の数を `dice[s]` とします (`dice = [0, 1, 1, 1, 1, 1, 1]`)。
2 個振って和が k になる場合の数は「1 個目が i、2 個目が k - i」の組の数なので

```
two[k] = Σ dice[i] × dice[k - i]   = convolution(dice, dice)[k]

k      : 2 3 4 5 6 7 8 9 10 11 12
two[k] : 1 2 3 4 5 6 5 4  3  2  1
```

3 個なら `convolution(two, dice)` です。「足し算の組み合わせを数える」ときは畳み込みを疑いましょう。

### なぜ速いのか (NTT のアイデア)

多項式は「係数の列」で表す代わりに、「いくつかの点での値」で表すこともできます。
**値で表すと、掛け算は各点の値を掛けるだけ (O(N))** です。

```
係数 a, b ──(変換 O(N log N))──→ 値 A, B
                                  │ 各点で掛ける O(N)
係数 c   ←──(逆変換 O(N log N))── 値 C = A × B
```

点として「1 の 2^k 乗根」を選ぶと、偶数番目と奇数番目に分けて半分の大きさの問題に再帰できるので、
変換が O(N log N) になります (高速フーリエ変換 FFT)。
これを複素数ではなく **mod 998244353 の世界** で行うのが NTT (数論変換) です。
998244353 = 119 × 2^23 + 1 なので、長さ 2^23 までの「1 の 2^k 乗根」が mod の中に存在し、誤差なく計算できます。

## 実装 (Kotlin)

```kotlin
const val MOD = 998244353L  // = 119 * 2^23 + 1。原始根は 3

fun powMod(a: Long, n: Long): Long {
    var base = (a % MOD + MOD) % MOD
    var e = n
    var res = 1L
    while (e > 0) {
        if (e and 1L == 1L) res = res * base % MOD
        base = base * base % MOD
        e = e shr 1
    }
    return res
}

// 数論変換 (NTT)。a.size は 2 のべき。invert = true で逆変換
fun ntt(a: LongArray, invert: Boolean) {
    val n = a.size
    // ビット反転の順に並べ替える
    var j = 0
    for (i in 1 until n) {
        var bit = n shr 1
        while (j and bit != 0) {
            j = j xor bit
            bit = bit shr 1
        }
        j = j xor bit
        if (i < j) { val t = a[i]; a[i] = a[j]; a[j] = t }
    }
    // 長さ 2, 4, 8, ... のブロックごとにバタフライ演算
    var len = 2
    while (len <= n) {
        var w = powMod(3, (MOD - 1) / len)  // 1 の len 乗根
        if (invert) w = powMod(w, MOD - 2)
        val half = len / 2
        val ws = LongArray(half)
        ws[0] = 1
        for (k in 1 until half) ws[k] = ws[k - 1] * w % MOD
        var i = 0
        while (i < n) {
            for (k in 0 until half) {
                val u = a[i + k]
                val v = a[i + k + half] * ws[k] % MOD
                a[i + k] = if (u + v >= MOD) u + v - MOD else u + v
                a[i + k + half] = if (u - v < 0) u - v + MOD else u - v
            }
            i += len
        }
        len = len shl 1
    }
    if (invert) {
        val nInv = powMod(n.toLong(), MOD - 2)
        for (i in 0 until n) a[i] = a[i] * nInv % MOD
    }
}

// c[k] = Σ_{i+j=k} a[i]*b[j] mod 998244353 (ACL の convolution)
// 長さ N, M → 長さ N+M-1。どちらかが空なら空配列。O((N+M) log(N+M))
fun convolution(a: LongArray, b: LongArray): LongArray {
    if (a.isEmpty() || b.isEmpty()) return LongArray(0)
    val resLen = a.size + b.size - 1
    var size = 1
    while (size < resLen) size = size shl 1
    val fa = LongArray(size)
    val fb = LongArray(size)
    for (i in a.indices) fa[i] = (a[i] % MOD + MOD) % MOD
    for (i in b.indices) fb[i] = (b[i] % MOD + MOD) % MOD
    ntt(fa, false)
    ntt(fb, false)
    for (i in 0 until size) fa[i] = fa[i] * fb[i] % MOD
    ntt(fa, true)
    return fa.copyOf(resLen)
}
```

## 使い方

AtCoder Library Practice Contest F - Convolution の解答例です。

```kotlin
import java.util.StringTokenizer

// AtCoder Library Practice Contest F - Convolution
fun main() {
    val st = StringTokenizer(System.`in`.bufferedReader().readText())
    val n = st.nextToken().toInt()
    val m = st.nextToken().toInt()
    val a = LongArray(n) { st.nextToken().toLong() }
    val b = LongArray(m) { st.nextToken().toLong() }
    val c = convolution(a, b)
    println(c.joinToString(" "))
}
```

入力例 1:

```
4 5
1 2 3 4
5 6 7 8 9
```

出力例 1:

```
5 16 34 60 70 70 59 36
```

入力例 2:

```
1 1
10000000
10000000
```

出力例 2:

```
871938225
```

多項式の掛け算とサイコロの例 (入力なし):

```kotlin
fun main() {
    // 多項式の掛け算: (1 + 2x + 3x^2)(4 + 5x) = 4 + 13x + 22x^2 + 15x^3
    println(convolution(longArrayOf(1, 2, 3), longArrayOf(4, 5)).joinToString(" "))

    // サイコロ: dice[s] = 1 個のサイコロで出目 s になる場合の数
    val dice = longArrayOf(0, 1, 1, 1, 1, 1, 1)
    val two = convolution(dice, dice)   // two[s] = 2 個の和が s になる場合の数
    println((2..12).joinToString(" ") { "$it:${two[it]}" })
    val three = convolution(two, dice)  // 3 個の和
    println("3個で和10: ${three[10]} 通り")  // 27
}
```

出力:

```
4 13 22 15
2:1 3:2 4:3 5:4 6:5 7:6 8:5 9:4 10:3 11:2 12:1
3個で和10: 27 通り
```

## 例題

- [AtCoder Library Practice Contest F - Convolution](https://atcoder.jp/contests/practice2/tasks/practice2_f) — そのまま。
- [ABC 291 G - OR Sum](https://atcoder.jp/contests/abc291/tasks/abc291_g) — ビットごとに「片方を反転した畳み込み」で、ずらし方ごとの一致数を求める。
- [ABC 392 G - Fine Triplets](https://atcoder.jp/contests/abc392/tasks/abc392_g) — 「B - A = C - B ⇔ A + C = 2B」。集合の要素の和ごとのペア数を畳み込みで数える。

## 注意点・よくあるミス

- **結果は mod 998244353**: 「正確な値」が必要で 998244353 を超えうるとき (例: 値が最大 10^9 になる数え上げ) はそのままでは使えない。
  ACL の `convolution_ll` のように複数の mod で計算して CRT で復元するなどの工夫が必要。
- **mod 10^9+7 では使えない**: 10^9+7 - 1 は 2 で 1 回しか割れないので NTT ができない。
- **結果の長さは N + M - 1**: 必要な範囲だけ使う。和の上限が決まっているなら、配列をそこで切ると速くなる。
- **配列は値の範囲で作る**: 「値 v の個数」を `cnt[v]` に入れて畳み込む。値が 10^6 までなら長さ 10^6 + 1 の配列。
- **差を数えたいときは片方を反転**: `convolution(a, b.reversedArray())` の添字 k は差 k - (M - 1) に対応する。
- **多数の小さい畳み込みは愚直の方が速い**: 長さが数十程度なら 2 重ループで十分 (ACL も小さいときは愚直に切り替える)。
