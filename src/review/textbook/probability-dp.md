---
title: 確率・期待値 DP
category: DP
acl: -
genres: DP, 数学
order: 3070
summary: 確率や「平均何回かかるか」(期待値) を DP で求める。mod での期待値も
level: 水
keywords: 確率DP, 期待値DP, expected value, probability, 期待値, 平均回数, サイコロ, コイン, mod 998244353, 逆元
---

## 何ができる？

- **確率 DP**: 「i 回目までで状態が j になる確率」を前から順に計算します。
- **期待値 DP**: 「状態 x から終わるまでに平均何回かかるか」を **ゴール側から** 計算します。
- **mod での期待値**: 答えが分数 P/Q のとき「P × Q^{-1} mod 998244353」を出力する問題。割り算を逆元のかけ算にするだけです。

| 関数 | 内容 | 計算量 |
| --- | --- | --- |
| `coins(p)` | 表が出る確率 p[i] のコイン N 枚を投げ、表が裏より多い確率 | O(N^2) |
| `sushi(a)` | 皿をランダムに選んで寿司を 1 個食べる操作の、全部なくなるまでの回数の期待値 | O(N^3) |
| `criticalHit(n, p)` | 確率 p% で 2、それ以外で 1 減る。n が 0 以下になるまでの回数の期待値 (mod 998244353) | O(N + log MOD) |

## いつ使う？

- 「確率を求めよ」「**期待値** を求めよ」「平均で何回」
- 「答えは有理数 P/Q になるので、P × Q^{-1} mod 998244353 を出力せよ」(mod の期待値)
- 操作のたびに **ランダムに** 状態が変わり、状態の種類が少ない (数・個数・HP など)
- 「〜になるまで繰り返す」→ 終わりの状態の期待値が 0 なので、そこから逆向きに DP

## しくみ

### 確率 DP (前から)

`dp[i][j]` = i 枚目まで投げて表が j 枚になる確率。

```
dp[i+1][j+1] += dp[i][j] × p[i]        (表)
dp[i+1][j]   += dp[i][j] × (1 - p[i])  (裏)
答え = Σ_{j > N/2} dp[N][j]
```

### 期待値 DP (後ろから)

E[x] = 「状態 x から終わるまでの回数の期待値」とすると、**1 回操作して、行き先の期待値を確率で重み付け** します。

```
E[x] = 1 + Σ (x から y へ行く確率) × E[y]
E[終わり] = 0
```

例 (Critical Hit): HP が i のとき、確率 p で HP が 2 減り、確率 1-p で 1 減る。

```
E[0] = 0
E[i] = 1 + p × E[max(i-2, 0)] + (1-p) × E[i-1]
```

### 自分自身に戻る遷移がある場合 (Sushi)

寿司が 1 個・2 個・3 個の皿の枚数を i, j, k とします。N 枚の皿から 1 枚選ぶと、確率 (N-i-j-k)/N で **空の皿** (何も起きない) を選びます。

```
E = 1 + (i/N) E[i-1,j,k] + (j/N) E[i+1,j-1,k] + (k/N) E[i,j+1,k-1] + ((N-i-j-k)/N) E   ← 右辺にも E
```

E を左辺に集めて解くと

```
E[i,j,k] = ( N + i E[i-1,j,k] + j E[i+1,j-1,k] + k E[i,j+1,k-1] ) / (i + j + k)
```

「自分に戻る」遷移は、**移項して式を解く** のが定番です。

### mod での期待値

分数のまま計算する代わりに、すべての「÷ Q」を「× Q^{-1} (mod の逆元)」に置き換えます。
MOD = 998244353 は素数なので、Q^{-1} = Q^{MOD-2} mod MOD (フェルマーの小定理) で求まります。
確率 P% は `P × inv(100)` です。

## 実装 (Kotlin)

```kotlin
// EDPC I - Coins: 表の確率 p[i] のコインを全部投げて、表の枚数 > 裏の枚数 となる確率
fun coins(p: DoubleArray): Double {
    val n = p.size
    var dp = DoubleArray(n + 1)  // dp[j] = ここまでで表が j 枚の確率
    dp[0] = 1.0
    for (i in 0 until n) {
        val ndp = DoubleArray(n + 1)
        for (j in 0..i) {
            ndp[j + 1] += dp[j] * p[i]
            ndp[j] += dp[j] * (1 - p[i])
        }
        dp = ndp
    }
    var ans = 0.0
    for (j in 0..n) if (j > n - j) ans += dp[j]
    return ans
}

// EDPC J - Sushi: a[i] = 皿 i の寿司の個数 (1〜3)。全部なくなるまでの操作回数の期待値
fun sushi(a: IntArray): Double {
    val n = a.size
    val c = IntArray(4)
    for (x in a) c[x]++
    // dp[k][j][i] = 寿司 1 個の皿が i 枚、2 個が j 枚、3 個が k 枚のときの期待値
    val dp = Array(c[3] + 1) { Array(c[2] + c[3] + 1) { DoubleArray(n + 1) } }
    for (k in 0..c[3]) {
        for (j in 0..c[2] + c[3] - k) {
            for (i in 0..n - j - k) {
                if (i + j + k == 0) continue
                var e = n.toDouble()
                if (i > 0) e += i * dp[k][j][i - 1]
                if (j > 0) e += j * dp[k][j - 1][i + 1]
                if (k > 0) e += k * dp[k - 1][j + 1][i]
                dp[k][j][i] = e / (i + j + k)
            }
        }
    }
    return dp[c[3]][c[2]][c[1]]
}

const val MOD = 998244353L

fun powMod(x: Long, n: Long): Long {
    var r = 1L
    var b = x % MOD
    var e = n
    while (e > 0) {
        if ((e and 1L) == 1L) r = r * b % MOD
        b = b * b % MOD
        e = e shr 1
    }
    return r
}

// ABC 280 E - Critical Hit: HP n、確率 p% で 2 減り、それ以外は 1 減る。0 以下になるまでの回数の期待値 mod 998244353
fun criticalHit(n: Int, p: Int): Long {
    val pr = p * powMod(100, MOD - 2) % MOD  // p / 100
    val q = (1 - pr + MOD) % MOD             // 1 - p / 100
    val e = LongArray(n + 1)                  // e[i] = HP が i のときの期待値。e[0] = 0
    for (i in 1..n) {
        e[i] = (1 + pr * e[maxOf(i - 2, 0)] + q * e[i - 1]) % MOD
    }
    return e[n]
}
```

## 使い方

[EDPC J - Sushi](https://atcoder.jp/contests/dp/tasks/dp_j) を解くコードです。

```kotlin
fun main() {
    val br = System.`in`.bufferedReader()
    var st = java.util.StringTokenizer("")
    fun next(): String {
        while (!st.hasMoreTokens()) st = java.util.StringTokenizer(br.readLine())
        return st.nextToken()
    }

    val n = next().toInt()
    val a = IntArray(n) { next().toInt() }
    println(sushi(a))

    // EDPC I - Coins なら: println(coins(DoubleArray(n) { next().toDouble() }))
    // ABC 280 E なら: println(criticalHit(n, p))
}
```

入力例

```
3
1 1 1
```

出力例

```
5.5
```

入力例2

```
2
1 2
```

出力例2

```
4.5
```

## 例題

- [EDPC I - Coins](https://atcoder.jp/contests/dp/tasks/dp_i) — 確率 DP の基本 (前から)。`coins` そのもの
- [EDPC J - Sushi](https://atcoder.jp/contests/dp/tasks/dp_j) — 期待値 DP。自分に戻る遷移を移項して解く
- [ABC 280 E - Critical Hit](https://atcoder.jp/contests/abc280/tasks/abc280_e) — mod 998244353 での期待値。`criticalHit` そのもの

## 注意点・よくあるミス

- **期待値は「後ろから」**: 「終わりまであと何回」を E[x] にすると、E[終わり] = 0 から逆順に埋められます。
  前から「ここに来る確率」と「ここまでの回数」を両方持つより、ずっと簡単です。
- 自分自身に戻る遷移 (何も起きない、失敗してやり直し) があるときは、**移項して式を解いてから** DP にする。
  そのまま書くと E が E 自身に依存して計算できません。
- 小数の出力は `println(ans)` で OK なことが多いですが、指数表記 (`1.0E-5`) になることがあります。
  心配なら `println("%.10f".format(ans))`。
- mod の期待値で **割り算 `/` を使わない**。必ず `× powMod(q, MOD - 2)`。p% は `p × inv(100)`。
- mod の計算で `1 - pr` が負にならないよう `+ MOD` する。Long のかけ算は毎回 `% MOD` (2 つかけたら即 % MOD)。
- Sushi の dp は最悪 (全部の皿が 3 個) で 301^3 個の Double、約 220MB になります。
  EDPC のメモリ制限は 1024MB なので通りますが、ほかの問題で同じ形にするときはメモリを見積もる。
- Kotlin では `e and 1L == 1L` は `e and (1L == 1L)` と解釈されてコンパイルエラーになります。
  ビット演算は `(e and 1L) == 1L` のように必ずかっこを付ける。
