---
title: ローリングハッシュ
category: 文字列
acl: -
genres: 文字列
order: 402
summary: 部分文字列が一致するかを O(1) で判定する (前計算 O(N))
level: 水
keywords: rolling hash, ハッシュ, 部分文字列の一致, 文字列検索, LCP, 最長共通接頭辞, mod 2^61-1, 回文判定
---

## 何ができる？

文字列 S を前計算しておくと、**任意の部分文字列 S[l, r) を 1 つの数 (ハッシュ値)** に O(1) で変換できます。
2 つの部分文字列が等しいかどうかを、文字を 1 つずつ比べずに **数の比較 1 回** で (ほぼ確実に) 判定できます。

| 操作 | 内容 | 計算量 |
| --- | --- | --- |
| `RollingHash(s)` | 前計算 | O(N) |
| `get(l, r)` | s[l, r) のハッシュ値 | O(1) |
| `connect(h1, h2, len2)` | 2 つの文字列をつなげたもののハッシュ値 | O(1) |
| `lcp(a, i, b, j)` | a の i 文字目からと b の j 文字目からの最長共通接頭辞の長さ | O(log N) |

※ ハッシュ値が等しくても、ごく低い確率で文字列が違うこと (衝突) があります。mod 2^61 - 1 を使えば実用上まず起きません。

## いつ使う？

- 「S の部分文字列と T の部分文字列が等しいか」を **何度も** 聞かれる
- 「長さ L の部分文字列で、2 回以上出てくるものはあるか」→ ハッシュ値を `HashMap` / `HashSet` に入れる
  (さらに「L が大きいほど難しい」なら **長さ L で二分探索**)
- パターン検索 (T が S のどこに出てくるか)。Z アルゴリズムや KMP の代わりにもなる
- 「S[l, r) は回文か」を何度も聞かれる → S と反転した S の両方のハッシュを作って比べる
- 「文字列を 2 つに分けて入れ替えた / つなげたものが等しいか」→ `connect`

## しくみ

文字列を **基数 B の数** とみなし、大きな素数 MOD で割った余りをハッシュ値にします。

```
s = "abc"  →  hash = ('a' × B^2 + 'b' × B^1 + 'c' × B^0) mod MOD
```

**先頭からの累積** `hash[i] = s[0, i) のハッシュ値` を前計算しておくと、

```
hash[i+1] = hash[i] × B + s[i]

s[l, r) のハッシュ値 = hash[r] - hash[l] × B^(r-l)     (mod MOD)
```

で O(1) で求まります (累積和の引き算と同じ考え方で、ずれた桁数ぶん B^(r-l) を掛けて引く)。

**MOD = 2^61 - 1 を使う理由**: 10^9+7 くらいの MOD だと、ハッシュ値の種類が約 10^9 しかありません。
10^5 個の部分文字列を比べると **誕生日のパラドックス** でかなりの確率で衝突します。
2^61 - 1 (約 2.3×10^18) なら衝突確率は無視できるほど小さくなります。

ただし 2^61 未満の数どうしの積は 64 bit に収まらないので、`Math.multiplyHigh` で 128 bit の積の上位を求め、
「2^61 ≡ 1 (mod 2^61 - 1)」を使って余りを計算しています。

**別のやり方**: 10^9+7 と 998244353 のように **2 つの MOD でハッシュを 2 個作り、両方一致したら等しい** とする方法もあります。
考え方は同じで、`Long` の掛け算がオーバーフローしないので実装が簡単です (その代わり 2 倍遅い)。

**基数 B はランダムに** 選びます。B を固定すると、衝突する文字列を意図的に作られることがあるためです。

## 実装 (Kotlin)

```kotlin
import kotlin.random.Random

// ローリングハッシュ (mod 2^61 - 1)。前計算 O(N)、部分文字列のハッシュ値を O(1) で返す
class RollingHash(s: CharSequence, private val base: Long = BASE) {
    companion object {
        const val MOD = (1L shl 61) - 1
        // 基数は実行のたびにランダムに選ぶ (狙い撃ちの撃墜対策)。比較する文字列どうしは同じ基数を使うこと
        val BASE = Random.nextLong(1L shl 32, MOD - 1)

        // a * b mod (2^61 - 1)。a, b < MOD を仮定。128 bit の積を上位・下位に分けて計算する
        fun mul(a: Long, b: Long): Long {
            val hi = Math.multiplyHigh(a, b) // 積の上位 64 bit
            val lo = a * b                   // 積の下位 64 bit
            // 2^61 ≡ 1 なので「61 bit 目より上」と「下 61 bit」を足せばよい
            var r = ((hi shl 3) or (lo ushr 61)) + (lo and MOD)
            r = (r and MOD) + (r ushr 61)
            return if (r >= MOD) r - MOD else r
        }

        fun add(a: Long, b: Long): Long {
            val r = a + b
            return if (r >= MOD) r - MOD else r
        }
    }

    val length = s.length
    private val hash = LongArray(length + 1) // hash[i] = s[0, i) のハッシュ値
    private val pow = LongArray(length + 1)  // pow[i] = base^i

    init {
        pow[0] = 1L
        for (i in 0 until length) {
            hash[i + 1] = add(mul(hash[i], base), s[i].code.toLong())
            pow[i + 1] = mul(pow[i], base)
        }
    }

    // s[l, r) のハッシュ値
    fun get(l: Int, r: Int): Long {
        val res = hash[r] - mul(hash[l], pow[r - l])
        return if (res < 0) res + MOD else res
    }

    // ハッシュ値 h1 の文字列の後ろに、長さ len2・ハッシュ値 h2 の文字列をつなげたもののハッシュ値 (len2 <= length)
    fun connect(h1: Long, h2: Long, len2: Int): Long = add(mul(h1, pow[len2]), h2)
}

// a の i 文字目からと、b の j 文字目からの最長共通接頭辞の長さ (二分探索で O(log N))
fun lcp(a: RollingHash, i: Int, b: RollingHash, j: Int): Int {
    var ok = 0
    var ng = minOf(a.length - i, b.length - j) + 1
    while (ng - ok > 1) {
        val mid = (ok + ng) / 2
        if (a.get(i, i + mid) == b.get(j, j + mid)) ok = mid else ng = mid
    }
    return ok
}
```

## 使い方

1 行目に S、2 行目に T を読み、

1. T が S に現れる位置を全部出力
2. S と T の最長共通接頭辞の長さを出力
3. S の中で **重ならずに 2 回以上現れる** 部分文字列の最大の長さを出力
   ([ABC 141 E - Who Says a Pun?](https://atcoder.jp/contests/abc141/tasks/abc141_e) と同じ。長さで二分探索)

する例です。`import kotlin.random.Random` は実装側の先頭にあります。

```kotlin
fun main() {
    // ---- 1. T が S のどこに現れるか (パターン検索) ----
    val s = readln().trim()
    val t = readln().trim()
    val hs = RollingHash(s)
    val ht = RollingHash(t)          // 同じ基数 (BASE) が使われるので比較できる
    val target = ht.get(0, t.length)
    val pos = (0..s.length - t.length).filter { hs.get(it, it + t.length) == target }
    println(pos.joinToString(" "))   // 0-indexed の開始位置

    // ---- 2. 最長共通接頭辞 ----
    println(lcp(hs, 0, ht, 0))

    // ---- 3. 重ならずに 2 回以上現れる部分文字列の最大の長さ (ABC 141 E) ----
    val n = s.length
    fun ok(len: Int): Boolean {
        val first = HashMap<Long, Int>() // ハッシュ値 -> 最初に現れた位置
        for (i in 0..n - len) {
            val j = first.getOrPut(hs.get(i, i + len)) { i }
            if (i - j >= len) return true // 重ならない 2 か所が見つかった
        }
        return false
    }
    var lo = 0         // ok(lo) は必ず true
    var hi = n / 2 + 1 // ok(hi) は必ず false
    while (hi - lo > 1) {
        val mid = (lo + hi) / 2
        if (ok(mid)) lo = mid else hi = mid
    }
    println(lo)
}
```

入力例

```
strangeorange
range
```

出力例

```
2 8
0
5
```

## 例題

- [ABC 141 E - Who Says a Pun?](https://atcoder.jp/contests/abc141/tasks/abc141_e) — 上の使い方の 3。長さを二分探索して、ハッシュ値の最初の出現位置を HashMap で覚える
- [ABC 284 F - ABCBAC](https://atcoder.jp/contests/abc284/tasks/abc284_f) — 分ける位置 i を全部試し、「前半 + 後半」と「真ん中を反転したもの」のハッシュを `connect` で比べる
- [ABC 331 F - Palindrome Query](https://atcoder.jp/contests/abc331/tasks/abc331_f) — 文字の書き換えがあるので、ハッシュをセグメント木に乗せる (発展)

## 注意点・よくあるミス

- 比べる 2 つの `RollingHash` は **同じ基数** で作ること。上の実装は既定で共通の `BASE` を使うので、
  `base` 引数を指定しなければ大丈夫です。
- **長さが違う** 部分文字列のハッシュ値は比べても意味がありません。必ず長さも一致させる。
- mod 10^9+7 の 1 つだけだと、ハッシュ値をたくさん `HashSet` に入れたときに衝突しやすい (誕生日のパラドックス)。
  2^61 - 1 を使うか、2 つの mod を組み合わせる。
- `a * b % MOD` を Long でそのまま計算すると、MOD が 2^61 - 1 のときはオーバーフローします。必ず `mul` を使う。
- `get(l, r)` の r は **含まない** (s[l, r) )。`get(i, i + len)` のように書くと間違いにくい。
- 確率的なアルゴリズムなので、「絶対に正しい」答えが必要なときは Z アルゴリズムや Suffix Array も検討する。
- 文字の書き換えがある問題では前計算が使えません。セグメント木にハッシュを乗せる (発展)。
