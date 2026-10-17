---
title: modint (mod の計算・nCr)
category: 数学
acl: modint
genres: 整数論, 組み合わせ, 数学
order: 210
summary: 998244353 などで割った余りを安全に計算し nCr も求める
---

## 何ができる？

答えがとても大きくなる問題では「998244353 で割った余りを出力せよ」「10^9+7 で割った余りを出力せよ」とよく言われます。
このページでは、Kotlin で実用的な **「Long で毎回 % を取る」スタイル** の関数セットを紹介します。

| 操作 | 書き方 | 計算量 |
| --- | --- | --- |
| 足し算 | `(a + b) % MOD` | O(1) |
| 引き算 | `(a - b + MOD) % MOD` | O(1) |
| 掛け算 | `a * b % MOD` | O(1) |
| 累乗 a^n | `powMod(a, n)` | O(log n) |
| 割り算 a / b | `a * invMod(b) % MOD` | O(log MOD) |
| 前計算 (階乗・逆元テーブル) | `Combination(maxN)` | O(maxN) |
| nCr, nPr, nHr | `comb.nCr(n, r)` など | O(1) |

おまけとして、`+ - * /` がそのまま書ける `value class ModInt` も載せています (ACL の `modint998244353` に近い使い心地)。

## いつ使う？

- 問題文に「**998244353 で割った余り**」「**10^9+7 で割った余り**」とある
- 場合の数・確率・期待値を求める問題 (確率は「mod で表した分数」で出力させることが多い)
- 「N 個から K 個選ぶ」「格子の道順」など **nCr** が出てくる数え上げ
- DP の値がどんどん大きくなる (DP テーブルの各要素を mod で持つ)

## しくみ

### 足し算・引き算・掛け算は「毎回 % を取る」だけ

余りの世界では、途中で何度 % を取っても最後の答えは変わりません。

```
(a + b) % M = ((a % M) + (b % M)) % M
(a * b) % M = ((a % M) * (b % M)) % M
```

`a, b < M ≒ 10^9` なら `a * b < 10^18` で、Long (最大 約 9.2×10^18) に収まります。**Int だと溢れる** ので必ず Long を使います。

引き算は Kotlin の `%` が負の数を返すことがあるので、`+ MOD` してから % を取ります。

```
(3 - 5) % 7        = -2    ← Kotlin ではマイナスになる
(3 - 5 + 7) % 7    =  5    ← 正しい
```

### 割り算は「逆元を掛ける」

mod の世界には小数がないので、`a / b` は「b を掛けると 1 になる数 (= b の逆元)」を a に掛けて計算します。

```
mod 7 で 1/3 は？  →  3 × 5 = 15 ≡ 1 (mod 7) なので 1/3 = 5
mod 7 で 4/3 は？  →  4 × 5 = 20 ≡ 6 (mod 7)   (確かに 6 × 3 = 18 ≡ 4)
```

MOD が素数 p のとき、**フェルマーの小定理** `b^(p-1) ≡ 1` から `b^(p-2)` が b の逆元になります。
これを繰り返し二乗法 `powMod` で O(log p) で計算するのが `invMod` です。
(MOD が素数でないときは [pow_mod / inv_mod](pow-inv-mod.md) の拡張ユークリッド版を使います)

### nCr の前計算

```
nCr = n! / (r! × (n-r)!)
```

なので、`fact[i] = i!` と `invFact[i] = 1 / i!` を表にしておけば、掛け算 2 回で答えが出ます。

`invFact` は 1 個ずつ invMod すると O(N log MOD) ですが、
`1/(i-1)! = (1/i!) × i` を使って **大きい方から順に** 作ると、invMod は 1 回だけで済みます。

```
invFact[maxN] = invMod(fact[maxN])        ← ここだけ逆元を計算
invFact[i-1]  = invFact[i] * i % MOD      ← あとは掛け算だけ
```

## 実装 (Kotlin)

```kotlin
const val MOD = 998244353L  // 問題文に合わせて 1_000_000_007L に変える

// a^n mod MOD (繰り返し二乗法, O(log n))
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

// a の逆元 (= 1/a)。MOD が素数で a が MOD の倍数でないときだけ使える (フェルマーの小定理)
fun invMod(a: Long): Long = powMod(a, MOD - 2)

// 階乗と階乗の逆元を前計算して nCr を O(1) で答える
class Combination(maxN: Int) {
    private val fact = LongArray(maxN + 1)
    private val invFact = LongArray(maxN + 1)

    init {
        fact[0] = 1
        for (i in 1..maxN) fact[i] = fact[i - 1] * i % MOD
        invFact[maxN] = invMod(fact[maxN])
        for (i in maxN downTo 1) invFact[i - 1] = invFact[i] * i % MOD
    }

    fun fact(n: Int): Long = fact[n]

    // n 個から r 個選ぶ
    fun nCr(n: Int, r: Int): Long {
        if (n < 0 || r < 0 || r > n) return 0
        return fact[n] * invFact[r] % MOD * invFact[n - r] % MOD
    }

    // n 個から r 個選んで並べる
    fun nPr(n: Int, r: Int): Long {
        if (n < 0 || r < 0 || r > n) return 0
        return fact[n] * invFact[n - r] % MOD
    }

    // n 種類から重複を許して r 個選ぶ
    fun nHr(n: Int, r: Int): Long = if (n == 0 && r == 0) 1 else nCr(n + r - 1, r)
}

// ---- おまけ: 演算子が使える ModInt (v は常に 0 <= v < MOD を保つ) ----
@JvmInline
value class ModInt(val v: Long) {
    operator fun plus(o: ModInt) = ModInt((v + o.v) % MOD)
    operator fun minus(o: ModInt) = ModInt((v - o.v + MOD) % MOD)
    operator fun times(o: ModInt) = ModInt(v * o.v % MOD)
    operator fun div(o: ModInt) = this * o.inv()
    fun pow(n: Long) = ModInt(powMod(v, n))
    fun inv() = ModInt(invMod(v))
    override fun toString() = v.toString()
}

fun Long.toModInt() = ModInt((this % MOD + MOD) % MOD)
fun Int.toModInt() = toLong().toModInt()
```

## 使い方

入力 `5 2` に対して `10`, `344440097`, `499122177`, `499122179`, `831870295`, `true` を (1 行ずつ) 出力します。

```kotlin
fun main() {
    // 入力: N K
    val (n, k) = readLine()!!.trim().split(" ").map { it.toInt() }

    // ① Long + % スタイル
    val comb = Combination(200000)            // 使う n の最大値まで前計算
    println(comb.nCr(n, k))                    // nCk mod 998244353
    println(powMod(2, 1_000_000_000_000L))     // 2^(10^12) mod 998244353
    println(invMod(2))                         // 1/2 = 499122177

    // 割り算は「逆元を掛ける」: (10 / 4) mod p
    println(10L * invMod(4) % MOD)

    // ② ModInt スタイル: 1/2 + 1/3 = 5/6
    val x = 1.toModInt() / 2.toModInt() + 1.toModInt() / 3.toModInt()
    println(x)
    println(x == 5.toModInt() / 6.toModInt())  // true
}
```

- `Combination(maxN)` の maxN は「nCr で使う n の最大値」にします。例えば `nCr(H + W, H)` なら H + W の最大値です。
- 10^9+7 の問題では先頭の `MOD` を `1_000_000_007L` に書き換えるだけで、他はそのまま使えます。

## 例題

- [ABC 034 C - 経路](https://atcoder.jp/contests/abc034/tasks/abc034_c) — 格子の道順 = nCr (mod 10^9+7)。nCr の前計算の練習に。
- [ABC 145 D - Knight](https://atcoder.jp/contests/abc145/tasks/abc145_d) — 移動回数を連立方程式で求めてから nCr (mod 10^9+7)。
- [ABC 156 D - Bouquet](https://atcoder.jp/contests/abc156/tasks/abc156_d) — `2^n - 1 - nCa - nCb`。n が 10^9 と大きいので powMod と「r が小さい nCr」を自分で組み立てる。
- [AtCoder Library Practice Contest K - Range Affine Range Sum](https://atcoder.jp/contests/practice2/tasks/practice2_k) — 遅延セグメント木の中で mod 998244353 の計算をする (ACL では modint を使う問題)。

## 注意点・よくあるミス

- **Int で掛け算して溢れる**: `a * b % MOD` は a, b ともに Long にする。`fact[i - 1] * i` のように Int を掛けるのは、左が Long なら OK。
- **引き算の後にマイナスになる**: `(a - b) % MOD` は負になりうる。`(a - b + MOD) % MOD` または `Math.floorMod` を使う。入力値が負の数のときも同様 (`toModInt()` は対応済み)。
- **% の取り忘れ**: 足し算を何回も続けると溢れる。1 回ごとに % を取る習慣を。
- **0 の逆元は存在しない**: `invMod(0)` は 0 を返してしまい、エラーにならない。割る数が MOD の倍数にならないか確認する。
- **MOD を間違える**: 998244353 と 1000000007 の取り違えはよくある。問題文を必ず確認。
- **前計算のサイズ不足**: `nCr(n, r)` で n > maxN だと配列外参照になる。n が 10^9 のように大きい場合は前計算できないので、`n × (n-1) × … × (n-r+1) / r!` を r 回のループで計算する (ABC 156 D)。
- **ModInt のコンストラクタに範囲外の値を直接入れない**: `ModInt(-1)` や `ModInt(10_000_000_000)` は壊れた値になる。外部の値は `toModInt()` で変換する。
- **ModInt の配列**: `Array<ModInt>` はボクシングされて遅くなることがある。速さが必要な DP では `LongArray` + % スタイルが安全。
