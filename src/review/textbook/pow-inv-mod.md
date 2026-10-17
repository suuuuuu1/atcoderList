---
title: pow_mod / inv_mod (累乗と逆元)
category: 数学
acl: pow_mod/inv_mod
genres: 整数論, 数学
order: 220
summary: x^n mod m を高速に計算し、mod が素数でなくても逆元を求める
level: 緑
keywords: 繰り返し二乗法, べき乗, 累乗, 逆元, 拡張ユークリッド, modpow, 割り算
---

## 何ができる？

| 関数 | 内容 | 計算量 |
| --- | --- | --- |
| `powMod(x, n, m)` | x^n mod m (ACL の `pow_mod`)。x は負でもよい | O(log n) |
| `invGcd(a, b)` | (g, x) を返す。g = gcd(a, b)、a·x ≡ g (mod b)、0 ≤ x < b/g (ACL 内部の `inv_gcd`) | O(log b) |
| `invMod(x, m)` | x の逆元 (x·y ≡ 1 (mod m) となる 0 ≤ y < m)。**m は素数でなくてよい** (ACL の `inv_mod`) | O(log m) |

条件 (ACL と同じ): `powMod` は 0 ≤ n, 1 ≤ m < 2^31。`invMod` は gcd(x, m) = 1, 1 ≤ m。

## いつ使う？

- x^n の n が 10^18 のように巨大 (1 回ずつ掛けると間に合わない)
- 「mod m で割り算したい」が、**m が素数とは限らない** (フェルマーの小定理が使えない)
- `a·x ≡ b (mod m)` の形の一次合同式を解きたい (周期・円周上の移動・時計の問題など)
- 「何回操作すると元の位置に戻るか」「最小の x は？」系の整数問題

## しくみ

### 繰り返し二乗法

3^13 を計算したいとき、13 を 2 進数にすると `1101` = 8 + 4 + 1 です。

```
3^1, 3^2, 3^4, 3^8       ← 前の値を 2 乗するだけで作れる
3^13 = 3^8 × 3^4 × 3^1   ← ビットが立っているものだけ掛ける
```

n のビット数 (約 log n 個) だけの掛け算で済むので、n = 10^18 でも 60 回程度です。

### 拡張ユークリッドの互除法

ユークリッドの互除法 `gcd(a, b) = gcd(b, a % b)` を進めるとき、
「今の数が a と b の何倍の和で書けるか」を一緒に記録していくと、

```
a·x + b·y = gcd(a, b)
```

となる x が求まります。mod b で見ると `a·x ≡ gcd(a, b)` です。
gcd(a, b) = 1 なら `a·x ≡ 1`、つまり **x が a の逆元** です。

```
例: 3 の mod 10 での逆元
  10 = 3×3 + 1  →  1 = 10 - 3×3  →  3×(-3) ≡ 1 (mod 10)
  -3 を [0, 10) に直して 7。確かに 3×7 = 21 ≡ 1
```

フェルマーの小定理 (x^(p-2)) と違い、**m が素数でなくても** gcd(x, m) = 1 なら使えます。

### 一次合同式 a·x ≡ b (mod m) の解き方

1. g = gcd(a, m) を求める。b が g で割り切れなければ **解なし**
2. a, b, m を全部 g で割る → a' と m' は互いに素
3. x ≡ b' × (a' の mod m' での逆元) (mod m')

`invGcd(a, m)` は g と「a' の mod m' での逆元」を同時に返すので、この手順がそのまま書けます (使い方の例を参照)。

## 実装 (Kotlin)

```kotlin
// x を [0, m) に直す (Kotlin の % は負の数だと負を返すため)
fun safeMod(x: Long, m: Long): Long {
    val r = x % m
    return if (r < 0) r + m else r
}

// x^n mod m (ACL の pow_mod。0 <= n, 1 <= m < 2^31)
fun powMod(x: Long, n: Long, m: Long): Long {
    require(n >= 0 && m >= 1)
    if (m == 1L) return 0
    var r = 1L
    var y = safeMod(x, m)
    var e = n
    while (e > 0) {
        if (e and 1L == 1L) r = r * y % m
        y = y * y % m
        e = e shr 1
    }
    return r
}

// 拡張ユークリッドの互除法 (ACL の inv_gcd)
// 戻り値 (g, x): g = gcd(a, b), a*x ≡ g (mod b), 0 <= x < b/g   (b >= 1)
fun invGcd(a: Long, b: Long): Pair<Long, Long> {
    val a2 = safeMod(a, b)
    if (a2 == 0L) return Pair(b, 0L)
    var s = b; var t = a2
    var m0 = 0L; var m1 = 1L
    while (t != 0L) {
        val u = s / t
        s -= t * u
        m0 -= m1 * u
        var tmp = s; s = t; t = tmp
        tmp = m0; m0 = m1; m1 = tmp
    }
    if (m0 < 0) m0 += b / s
    return Pair(s, m0)
}

// x の mod m での逆元 (ACL の inv_mod)。gcd(x, m) = 1 が必要。m は素数でなくてよい
fun invMod(x: Long, m: Long): Long {
    require(m >= 1)
    val (g, inv) = invGcd(x, m)
    require(g == 1L) { "gcd(x, m) != 1 なので逆元はありません" }
    return inv
}
```

## 使い方

ABC 186 E - Throne の解答例です。「S + K·x ≡ 0 (mod N) となる最小の x ≥ 1」を求めます。

```kotlin
// ABC 186 E - Throne : S + K*x ≡ 0 (mod N) となる最小の x >= 1 を求める
fun main() {
    val t = readLine()!!.trim().toInt()
    val sb = StringBuilder()
    repeat(t) {
        val (n, s, k) = readLine()!!.trim().split(" ").map { it.toLong() }
        // K*x ≡ -S (mod N)。g = gcd(K, N) と、K/g の mod N/g での逆元を同時に得る
        val (g, inv) = invGcd(k, n)
        if (s % g != 0L) {
            sb.appendLine(-1)           // -S が g で割り切れないと解なし
        } else {
            val n2 = n / g
            val x = safeMod(-s / g, n2) * inv % n2
            sb.appendLine(x)
        }
    }
    print(sb)

    // 単体の使い方
    println(powMod(3, 100, 1_000_000_007))  // 3^100 mod 1e9+7
    println(invMod(3, 10))                  // 3 * 7 = 21 ≡ 1 (mod 10) なので 7
}
```

入力例 (自作。最後のケースは解なし):

```
5
10 4 3
1000 11 9
998244353 897581057 595591169
10000 6 14
6 1 2
```

出力:

```
2
221
249561088
3571
-1
886041711
7
```

(最後の 2 行は「単体の使い方」の出力です。)

## 例題

- [ABC 186 E - Throne](https://atcoder.jp/contests/abc186/tasks/abc186_e) — 上の使い方の問題。mod が素数ではないので拡張ユークリッドが必要。
- [ABC 156 D - Bouquet](https://atcoder.jp/contests/abc156/tasks/abc156_d) — 2^n (n ≤ 10^9) を繰り返し二乗法で。
- [ABC 228 E - Integer Sequence Fair](https://atcoder.jp/contests/abc228/tasks/abc228_e) — M^(K^N) mod 998244353。指数をフェルマーの小定理で mod 998244352 にする。M が 998244353 の倍数のときに注意。

(AtCoder Library Practice Contest には pow_mod / inv_mod 単体の問題はありません。)

## 注意点・よくあるミス

- **m が大きいと掛け算が溢れる**: `r * y % m` は m < 2^31 程度まで安全 (ACL も m は int)。m が 10^18 級なら `Math.multiplyHigh` や BigInteger が必要。
- **逆元は gcd(x, m) = 1 のときだけ存在する**: 例えば mod 10 で 2 の逆元はない。`invMod` はその場合に例外を投げる。
- **フェルマー版 (x^(m-2)) は m が素数のときだけ**: m = 10 などで使うと間違った値になるがエラーにならないので気付きにくい。
- **負の数の %**: Kotlin の `-7 % 3` は `-1`。`safeMod` で [0, m) に直してから計算する。
- **x^0 = 1**: ただし m = 1 のときは 0 (ACL の仕様)。
- **指数を mod p で割らない**: x^n の n を小さくしたいときは mod (p - 1) (フェルマー)。mod p ではない。
