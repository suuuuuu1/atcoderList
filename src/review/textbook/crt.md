---
title: 中国剰余定理 (CRT)
category: 数学
acl: crt
genres: 整数論, 数学
order: 230
summary: 「3 で割って 2 余り、5 で割って 3 余る数」を求める
level: 水
keywords: 中国剰余定理, Chinese Remainder Theorem, 連立合同式, 余りの条件
---

## 何ができる？

`x ≡ r[i] (mod m[i])` (i = 0, 1, …, n-1) という条件を **全部同時に満たす x** を求めます。

| 関数 | 内容 | 計算量 |
| --- | --- | --- |
| `crt(r, m)` | 解があれば `(y, z)`: 解は x ≡ y (mod z)、0 ≤ y < z、z = lcm(m[0], …, m[n-1])。解がなければ `(0, 0)`。n = 0 なら `(0, 1)` | O(n log max m) |

ACL の `crt` と同じ仕様です。法 m[i] は **互いに素でなくてもよい** です (m[i] ≥ 1)。ただし lcm が Long に収まる必要があります。

## いつ使う？

- 「A で割った余りが B、C で割った余りが D である最小の数は？」
- **周期の違う 2 つ以上の繰り返し** が同時に起こる時刻を求める (信号、電車、惑星の公転など)
- 大きな数を、小さい数たちで割った余りから復元したい
- 解なし (`z == 0`) の判定もそのままできるので「いつまでも起きないなら -1」系の問題にも

## しくみ

### 2 つの式をまとめる

`x ≡ 2 (mod 3)` と `x ≡ 3 (mod 5)` を考えます。

1 つ目から x = 2 + 3t と書けます。これを 2 つ目に入れると

```
2 + 3t ≡ 3 (mod 5)
    3t ≡ 1 (mod 5)
     t ≡ 2 (mod 5)     ← 3 の逆元 (mod 5) は 2
```

なので t = 2 + 5s、x = 2 + 3(2 + 5s) = 8 + 15s。つまり **x ≡ 8 (mod 15)** にまとまりました。

```
x       : 0 1 2 3 4 5 6 7 8 9 10 11 12 13 14
x mod 3 : 0 1 2 0 1 2 0 1 2 0  1  2  0  1  2
x mod 5 : 0 1 2 3 4 0 1 2 3 4  0  1  2  3  4
                          ↑ x = 8 で「2 と 3」がそろう (次は 8 + 15 = 23)
```

この「2 つを 1 つにまとめる」操作を左から順に繰り返せば、何個でもまとめられます。

### 法が互いに素でないとき

`x = r0 + t·m0` を `x ≡ r1 (mod m1)` に入れると `t·m0 ≡ r1 - r0 (mod m1)`。
g = gcd(m0, m1) とすると、**r1 - r0 が g で割り切れないと解なし** です。
割り切れるなら両辺を g で割って逆元を掛ければ t が求まり、新しい法は lcm(m0, m1) になります。
(この部分は [pow_mod / inv_mod](pow-inv-mod.md) の一次合同式の解き方と同じです。)

```
x ≡ 1 (mod 4), x ≡ 3 (mod 6) → g = 2, 3 - 1 = 2 は 2 で割り切れる → x ≡ 9 (mod 12)
x ≡ 0 (mod 4), x ≡ 1 (mod 6) → 1 - 0 = 1 は 2 で割り切れない → 解なし (偶数かつ奇数は無理)
```

## 実装 (Kotlin)

```kotlin
// x を [0, m) に直す
fun safeMod(x: Long, m: Long): Long {
    val r = x % m
    return if (r < 0) r + m else r
}

// 戻り値 (g, x): g = gcd(a, b), a*x ≡ g (mod b), 0 <= x < b/g
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

// 中国剰余定理 (ACL の crt)
// x ≡ r[i] (mod m[i]) をすべて満たす x を求める。m[i] >= 1
// 解があれば (y, z): 解は x ≡ y (mod z), 0 <= y < z, z = lcm(m[0], ..., m[n-1])
// 解がなければ (0, 0)。n = 0 なら (0, 1)。z が Long に収まることが必要
fun crt(r: LongArray, m: LongArray): Pair<Long, Long> {
    require(r.size == m.size)
    var r0 = 0L
    var m0 = 1L
    for (i in r.indices) {
        require(m[i] >= 1)
        var r1 = safeMod(r[i], m[i])
        var m1 = m[i]
        if (m0 < m1) {  // m0 >= m1 になるように入れ替える
            var tmp = r0; r0 = r1; r1 = tmp
            tmp = m0; m0 = m1; m1 = tmp
        }
        if (m0 % m1 == 0L) {  // m1 が m0 の約数なら、矛盾がないか確認するだけ
            if (r0 % m1 != r1) return Pair(0L, 0L)
            continue
        }
        // x = r0 + t*m0 とおき、r0 + t*m0 ≡ r1 (mod m1) を t について解く
        val (g, im) = invGcd(m0, m1)
        val u1 = m1 / g
        if ((r1 - r0) % g != 0L) return Pair(0L, 0L)
        val t = (r1 - r0) / g % u1 * im % u1
        r0 += t * m0
        m0 *= u1  // m0 = lcm(m0, m1)
        if (r0 < 0) r0 += m0
    }
    return Pair(r0, m0)
}
```

## 使い方

ABC 193 E - Oversleeping の解答例です。電車が停車している時刻の余り (Y 通り) と、起きている時刻の余り (Q 通り) を全部試し、
それぞれ CRT で「両方を満たす最小の時刻」を求めて最小値を取ります (Y, Q ≤ 500 なので 25 万回)。

```kotlin
// ABC 193 E - Oversleeping
// 電車は時刻 [X, X+Y) (mod 2X+2Y) に停車中、起きているのは [P, P+Q) (mod P+Q)
fun main() {
    val t = readLine()!!.trim().toInt()
    val sb = StringBuilder()
    repeat(t) {
        val (x, y, p, q) = readLine()!!.trim().split(" ").map { it.toLong() }
        var best = Long.MAX_VALUE
        for (t1 in x until x + y) {        // 電車側の余り (Y 通り)
            for (t2 in p until p + q) {    // 起きている側の余り (Q 通り)
                val (ans, lcm) = crt(longArrayOf(t1, t2), longArrayOf(2 * x + 2 * y, p + q))
                if (lcm != 0L) best = minOf(best, ans)  // lcm == 0 は解なし
            }
        }
        sb.appendLine(if (best == Long.MAX_VALUE) "infinity" else best.toString())
    }
    print(sb)

    // 単体の使い方: x ≡ 2 (mod 3), x ≡ 3 (mod 5), x ≡ 2 (mod 7)  → (23, 105)
    println(crt(longArrayOf(2, 3, 2), longArrayOf(3, 5, 7)))
    // 法が互いに素でなくてもよい: x ≡ 1 (mod 4), x ≡ 3 (mod 6) → (9, 12)
    println(crt(longArrayOf(1, 3), longArrayOf(4, 6)))
    // 矛盾する: x ≡ 0 (mod 4), x ≡ 1 (mod 6) → (0, 0)
    println(crt(longArrayOf(0, 1), longArrayOf(4, 6)))
}
```

入力例:

```
3
5 2 7 6
1 1 3 1
999999999 1 1000000000 1
```

出力:

```
20
infinity
1000000000999999999
(23, 105)
(9, 12)
(0, 0)
```

(最後の 3 行は「単体の使い方」の出力です。)

## 例題

- [ABC 193 E - Oversleeping](https://atcoder.jp/contests/abc193/tasks/abc193_e) — 上の使い方の問題。2 つの周期 (2X+2Y と P+Q) の CRT。
- [ABC 286 F - Guess The Number 2](https://atcoder.jp/contests/abc286/tasks/abc286_f) — インタラクティブ問題。互いに素な小さい周期をいくつも用意し、各周期での余りから N を CRT で復元する。

(AtCoder Library Practice Contest には crt 単体の問題はありません。)

## 注意点・よくあるミス

- **解なしは `(0, 0)`**: 戻り値の z (lcm) が 0 かどうかで判定する。y == 0 は普通の解 (x ≡ 0) もありうるので y で判定しない。
- **lcm が Long を超えると壊れる**: 法をたくさん掛け合わせると簡単に 10^18 を超える。法の lcm が大きくなりすぎないか事前に確認。
- **答えは「最小の非負整数」**: 問題が「正の整数」を求めているなら y == 0 のとき z を答える、などの調整が必要。
- **r[i] は負でもよい**: 実装の中で [0, m[i]) に直している。
- **Pair の分解**: `val (ans, lcm) = crt(...)` と分解代入すると読みやすい。
