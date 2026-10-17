---
title: 行列累乗
category: 数学
acl: -
genres: 行列累乗, 数学
order: 204
summary: 線形な漸化式の第 N 項 (N ≤ 10^18) を O(K^3 log N) で求める
level: 水
keywords: 行列, matrix, 行列の累乗, 繰り返し二乗法, フィボナッチ, fibonacci, 漸化式, 線形漸化式, 10^18項目
---

## 何ができる？

K×K 行列 A の N 乗 (N は 10^18 くらいまで) を mod 付きで計算します。
これを使うと、**「次の状態が前の状態の足し算・定数倍で決まる」漸化式の第 N 項** が高速に求まります。

| 関数 | 内容 | 計算量 |
| --- | --- | --- |
| `matMul(a, b, mod)` | 行列の積 a × b | O(n k m) (正方行列なら O(K^3)) |
| `matPow(a, e, mod)` | 正方行列 a の e 乗 | O(K^3 log e) |

K = 2 (フィボナッチ) なら、N = 10^18 でも約 60 回の掛け算で終わります。

## いつ使う？

- **N が 10^18 のように大きい** のに、DP の遷移が「前の数項の線形結合」になっている
  (例: `dp[i+1] = dp[i] + dp[i-1]`、`dp[i+1][j] = Σ dp[i][k] × (係数)`)
- 状態の種類 K が小さい (K ≤ 50〜100 程度。K^3 log N が間に合う範囲)
- グラフで「ちょうど N 歩で行ける経路の数」→ 隣接行列の N 乗
- 等比数列の和 1 + A + A^2 + … + A^(N-1) を、mod が素数でなくても求めたい

## しくみ

### 漸化式を行列で書く

フィボナッチ数 F(i+1) = F(i) + F(i−1) は、2 つ並べたベクトルで書くと

```
| F(i+1) |   | 1 1 |   | F(i)   |
| F(i)   | = | 1 0 | × | F(i-1) |
```

となります。これを繰り返すと

```
| F(n+1) |   | 1 1 |^n   | F(1) |
| F(n)   | = | 1 0 |   × | F(0) |
```

なので、行列 A = [[1, 1], [1, 0]] の n 乗が分かれば F(n) が分かります
(A^n = [[F(n+1), F(n)], [F(n), F(n−1)]] になります)。

### 繰り返し二乗法

A^n を n 回掛けると遅いので、**n を 2 進数で見て、A, A^2, A^4, A^8, … を掛け合わせます**。

```
A^13 = A^8 × A^4 × A^1     (13 = 1101 (2進))
base: A → A^2 → A^4 → A^8  (毎回 2 乗する)
```

ふつうの数の `pow_mod` と同じ考え方で、掛け算が「行列の積」に変わっただけです。
最初の `result` は **単位行列** (掛けても変わらない行列) にします。

## 実装 (Kotlin)

```kotlin
// 行列は Array<LongArray> で表す。要素は 0 以上 mod 未満にしておくこと
// mod は 3×10^9 程度まで (mod * mod が Long に収まる範囲)

// 行列の積 a × b (a は n×k、b は k×m)。O(n k m)
fun matMul(a: Array<LongArray>, b: Array<LongArray>, mod: Long): Array<LongArray> {
    val n = a.size
    val k = b.size
    val m = b[0].size
    val c = Array(n) { LongArray(m) }
    for (i in 0 until n) {
        for (l in 0 until k) {
            val x = a[i][l]
            if (x == 0L) continue
            for (j in 0 until m) {
                c[i][j] = (c[i][j] + x * b[l][j]) % mod
            }
        }
    }
    return c
}

// 正方行列 a の e 乗 (e >= 0)。繰り返し二乗法で O(n^3 log e)
fun matPow(a: Array<LongArray>, e: Long, mod: Long): Array<LongArray> {
    val n = a.size
    // 単位行列から始める
    var result = Array(n) { i -> LongArray(n).also { it[i] = 1L % mod } }
    var base = a
    var k = e
    while (k > 0) {
        if (k and 1L == 1L) result = matMul(result, base, mod)
        base = matMul(base, base, mod)
        k = k shr 1
    }
    return result
}
```

## 使い方

N (0 ≤ N ≤ 10^18) を読んで、フィボナッチ数 F(N) を 10^9+7 で割った余りを出力します。

```kotlin
fun main() {
    val n = readLine()!!.trim().toLong()   // 0 <= n <= 10^18
    val mod = 1_000_000_007L

    // フィボナッチ数 F(0) = 0, F(1) = 1, F(i+2) = F(i+1) + F(i)
    // (F(i+1), F(i)) = A × (F(i), F(i-1)) となる A = [[1, 1], [1, 0]]
    // A^n = [[F(n+1), F(n)], [F(n), F(n-1)]] なので、右上が F(n)
    val a = arrayOf(
        longArrayOf(1, 1),
        longArrayOf(1, 0),
    )
    val p = matPow(a, n, mod)
    println(p[0][1])
}
```

入力例

```
10
```

出力例

```
55
```

(N = 1000000000000000000 なら `209783453` を出力します)

## 例題

- [EDPC R - Walk](https://atcoder.jp/contests/dp/tasks/dp_r) — 隣接行列の K 乗の全要素の和が「長さ K のパスの数」
- [ABC 293 E - Geometric Progression](https://atcoder.jp/contests/abc293/tasks/abc293_e) — 等比数列の和を mod M (素数とは限らない) で。2×2 行列 [[A, 1], [0, 1]] の累乗で求まる
- [ABC 009 D - 漸化式](https://atcoder.jp/contests/abc009/tasks/abc009_4) — 足し算を XOR、掛け算を AND に置き換えた行列累乗 (`matMul` の中の演算を書き換える)

## 注意点・よくあるミス

- 行列の要素は **0 以上 mod 未満** にしておく。負の数や mod 以上の値が入っていると、掛け算でオーバーフローします。
- `x * b[l][j]` は最大 (mod − 1)^2。mod が 10^9 程度なら Long に収まりますが、**mod が 10^18 程度だと溢れます**
  (その場合は BigInteger や [整数計算の小技](integer-tricks.md) の `mulMod` を使う)。
- **0 乗は単位行列**。`matPow(a, 0, mod)` は単位行列を返すので、F(0) = 0 も正しく出ます。
- 行列とベクトルのどちらを左に置くか、行と列の向きを取り違えやすいです。
  小さい N で、ふつうの DP の結果と一致するか確かめましょう。
- 計算量は K^3 log N。K = 100, N = 10^18 だと 100^3 × 60 = 6×10^7 回程度。K が大きすぎると間に合いません。
- N を読むときは **Long**。`readLine()!!.toInt()` だと 10^18 は読めません。
