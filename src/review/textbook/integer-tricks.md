---
title: 整数計算の小技
category: 数学
acl: -
genres: 数学, 実装
order: 206
summary: 切り上げ除算・負数の割り算・整数 sqrt・オーバーフロー対策をまとめる
level: 茶
keywords: 切り上げ, ceil, floorDiv, floorMod, 負の数の余り, 整数平方根, isqrt, オーバーフロー, overflow, 10^18
---

## 何ができる？

AtCoder でよく使う「整数のちょっとした計算」を、**誤差やオーバーフローなし** で行う関数集です。

| 関数 | 内容 | 計算量 |
| --- | --- | --- |
| `ceilDiv(a, b)` | 切り上げ ⌈a / b⌉ (負の数も OK) | O(1) |
| `floorDiv(a, b)` | 切り捨て ⌊a / b⌋ (負の数も OK) | O(1) |
| `floorMod(a, b)` | 0 以上 b 未満の余り (b > 0) | O(1) |
| `isqrt(n)` | ⌊√n⌋ を誤差なく | O(1) |
| `mulExceeds(a, b, limit)` | a × b が limit を超えるか (掛け算せずに判定) | O(1) |
| `mulCapped(a, b, limit)` | a × b。limit を超えたら limit | O(1) |
| `mulMod(a, b, mod)` | (a × b) mod m。a × b が Long を超えても OK | O(1) |

## いつ使う？

- 「A 円の物を B 個ずつ」「HP を攻撃力で割って何回で倒せるか」→ **切り上げ除算**
- 座標や値が **負になりうる** のに `/` や `%` を使っている → `floorDiv` / `floorMod`
- 「x^2 ≤ N となる最大の x」「N は平方数か」で N が 10^18 くらい → `isqrt`
- 「値が 10^18 を超えたら打ち切り」「掛け算した結果が上限を超えるか」→ `mulExceeds`
- mod が 10^18 くらいの大きな数で、掛け算すると Long を超える → `mulMod`

## しくみ

### Kotlin の `/` と `%` は 0 方向に切り捨て

```
 7 / 2 =  3,   7 % 2 =  1
-7 / 2 = -3,  -7 % 2 = -1    ← 数学の ⌊-3.5⌋ = -4 とは違う！
Math.floorDiv(-7, 2) = -4,  Math.floorMod(-7, 2) = 1
```

正の数どうしなら同じですが、**負の数が混ざると 1 ずれます**。
mod を取ったら負になって配列外参照…というのが典型的なバグです。

### 切り上げ

- 正の数どうしなら `(a + b - 1) / b` が定番です。
- 負の数も含めるなら **⌈a / b⌉ = −⌊−a / b⌋** を使うと、`floorDiv` 1 回で書けます。

### 整数の平方根

`Math.sqrt(n.toDouble()).toLong()` は、n が 10^15 を超えるあたりから **1 ずれることがあります**
(Double は整数を 2^53 ≈ 9×10^15 までしか正確に表せないため)。
そこで、Double で近い値 r を出してから

- r × r > n なら r を減らす
- (r+1) × (r+1) ≤ n なら r を増やす

と整数で微調整します。r × r 自体のオーバーフローを避けるため、`r > n / r` のように割り算で比べます。

### オーバーフロー判定

Long の最大値は約 9.2×10^18。10^18 どうしを掛けると簡単に溢れて、**エラーにならずに変な値 (負の数など)** になります。

- a × b > limit ⇔ b > limit / a (a > 0 のとき、整数の割り算で OK)
- 例外を投げてほしいなら `Math.multiplyExact(a, b)` (溢れたら `ArithmeticException`)

### 大きな mod の掛け算

(a × b) mod m で m が 10^18 程度だと a × b は最大 10^36 になり、Long に入りません。
`BigInteger` で計算するのが一番簡単で確実です (少し遅いので、ループで 10^7 回呼ぶような使い方は避ける)。

## 実装 (Kotlin)

```kotlin
// 切り上げ除算 ceil(a / b)。a, b は負でも OK (b != 0)
fun ceilDiv(a: Long, b: Long): Long = -Math.floorDiv(-a, b)

// 切り捨て除算 floor(a / b) と、0 以上 |b| 未満の余り (b > 0 のとき)
// Kotlin の / と % は「0 に向かって切り捨て」なので、負の数で答えがずれる
fun floorDiv(a: Long, b: Long): Long = Math.floorDiv(a, b)
fun floorMod(a: Long, b: Long): Long = Math.floorMod(a, b)

// floor(√n) (n >= 0)。Double の sqrt で近い値を出してから、ずれを ±1 ずつ直す
fun isqrt(n: Long): Long {
    require(n >= 0)
    var r = Math.sqrt(n.toDouble()).toLong()
    while (r > 0 && r > n / r) r--             // r * r > n の間 (掛け算せずに判定)
    while (r + 1 <= n / (r + 1)) r++           // (r + 1)^2 <= n の間
    return r
}

// a * b が limit を超えるか (a, b >= 0)。掛け算せずに判定するのでオーバーフローしない
fun mulExceeds(a: Long, b: Long, limit: Long = Long.MAX_VALUE): Boolean =
    a != 0L && b > limit / a

// a * b を計算し、limit を超えたら limit を返す (a, b >= 0)
fun mulCapped(a: Long, b: Long, limit: Long): Long =
    if (mulExceeds(a, b, limit)) limit else a * b

// (a * b) % mod を、a * b が Long を超えても正しく計算する (0 <= a, b < mod)
fun mulMod(a: Long, b: Long, mod: Long): Long =
    java.math.BigInteger.valueOf(a)
        .multiply(java.math.BigInteger.valueOf(b))
        .mod(java.math.BigInteger.valueOf(mod))
        .toLong()
```

## 使い方

a, b, n を読んで、それぞれの関数の結果を表示する例です。コメントは下の入力例のときの出力です。

```kotlin
fun main() {
    val br = System.`in`.bufferedReader()
    var st = java.util.StringTokenizer("")
    fun next(): String {
        while (!st.hasMoreTokens()) st = java.util.StringTokenizer(br.readLine())
        return st.nextToken()
    }

    val a = next().toLong()
    val b = next().toLong()
    val n = next().toLong()

    // 割り算 (a = -7, b = 2 のとき)
    println("${a / b} ${a % b}")                       // -3 -1  ← Kotlin の / と % (0 方向に切り捨て)
    println("${floorDiv(a, b)} ${floorMod(a, b)}")    // -4 1   ← 数学の floor と、0 以上の余り
    println(ceilDiv(a, b))                            // -3     ← 切り上げ

    // 整数の平方根 (n = 999999999999999999 のとき)
    println(Math.sqrt(n.toDouble()).toLong())         // 1000000000 ← Double だと 1 大きくなる
    println(isqrt(n))                                 // 999999999  ← 正しい

    // 10^18 を超える掛け算
    val limit = 1_000_000_000_000_000_000L
    println(mulExceeds(n, 10, limit))                 // true
    println(mulCapped(n, 10, limit))                  // 1000000000000000000
    println(mulMod(n, n, 998244353L))                 // 997388060 (n * n は Long を超えるが正しく計算できる)
}
```

入力例

```
-7 2 999999999999999999
```

出力例

```
-3 -1
-4 1
-3
1000000000
999999999
true
1000000000000000000
997388060
```

## 例題

- [ABC 169 C - Multiplication 3](https://atcoder.jp/contests/abc169/tasks/abc169_c) — 小数 B を Double で掛けると誤差で落ちる。B を 100 倍した整数にして計算する
- [ABC 215 B - log2(N)](https://atcoder.jp/contests/abc215/tasks/abc215_b) — N ≤ 10^18。`Math.log` は誤差が出るので、2 を掛けていく整数のループで求める
- [ABC 284 D - Happy New Year! 2023](https://atcoder.jp/contests/abc284/tasks/abc284_d) — N = p^2 × q。小さい方で割った後、平方根の判定に `isqrt` を使う

## 注意点・よくあるミス

- **入力が 10^9 を超えるなら Long**。`toInt()` で読むと例外になったり、計算途中で溢れたりします。
- `Int * Int` の結果は Int。`a * b` (どちらも Int) が 2.1×10^9 を超えると溢れます。`a.toLong() * b` と書く。
- `1e18.toLong()` は OK ですが、`Math.pow(10.0, 18.0).toLong()` のように Double を経由すると、
  大きな数では誤差が出ることがあります。`1_000_000_000_000_000_000L` と整数で書くのが安全です。
- `%` の結果が負になることを忘れない。`(x % m + m) % m` か `Math.floorMod(x, m)` を使う。
- 切り上げの定番 `(a + b - 1) / b` は **a が負だと間違えます**。符号が分からないときは `ceilDiv` を使う。
- Double どうしの比較 (`==`) や、Double を経由した `floor`/`ceil` は誤差の元。整数だけで計算できないか考えましょう。
