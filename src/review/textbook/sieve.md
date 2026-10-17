---
title: エラトステネスの篩
category: 数学
acl: -
genres: 整数論, 数学
order: 202
summary: N 以下の素数を一気に列挙し、最小素因数表で高速に素因数分解する
level: 茶
keywords: エラトステネス, sieve, 素数列挙, 素数表, 最小素因数, spf, smallest prime factor, 高速素因数分解, 素数の個数
---

## 何ができる？

N (10^7 くらいまで) 以下の整数すべてについて、素数かどうかと **最小の素因数** をまとめて前計算します。

| 操作 | 内容 | 計算量 |
| --- | --- | --- |
| `Sieve(n)` | 0〜n の最小素因数テーブルと素数リストを作る | O(n log log n) |
| `isPrime(x)` | x が素数なら `true` (0 ≤ x ≤ n) | O(1) |
| `primes` | n 以下の素数のリスト (昇順) | — |
| `minFactor[x]` | x の最小の素因数 (x ≥ 2) | O(1) |
| `factorize(x)` | x の素因数分解 `(素数, 指数)` のリスト (2 ≤ x ≤ n) | O(log x) |

n = 10^7 でも 0.1〜0.2 秒程度で作れます。

## いつ使う？

- 「N 以下の素数をすべて」「区間 [L, R] に素数はいくつ？」を何度も聞かれる (累積和と組み合わせる)
- 10^5〜10^6 個の数を **それぞれ素因数分解** したい (1 個ずつ √ で割ると間に合わない)
- 「A_i どうしが互いに素か」「同じ素因数を持つものがあるか」を調べたい
- 数が 1 つだけなら [素数判定・素因数分解](prime-factorization.md) の √N の方法で十分です

## しくみ

2 から順に見ていき、**まだ消されていない数は素数**。その素数の倍数をすべて消します。

```
 2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20

 2 は素数 → 4, 6, 8, ..., 20 を消す (最小素因数 = 2)
 3 は素数 → 9, 12, 15, 18 を見る。新しく消えるのは 9, 15 (最小素因数 = 3)
            (12, 18 は 2 で消し済みなので最小素因数は 2 のまま)
 4 は消されている → とばす
 5 は素数 → 25 > 20 なので消すものなし
 ...
 残った 2 3 5 7 11 13 17 19 が素数
```

- 素数 p の倍数は **p × p から** 消せば十分です (それより小さい倍数は、もっと小さい素数で消えている)。
- 「消したときの素数」を `minFactor` に記録すると、それが **最小素因数** になります。

### 最小素因数で素因数分解

`x` を `minFactor[x]` で割り続けるだけです。1 回割るごとに x は半分以下になるので O(log x) です。

```
x = 360: minFactor = 2 → 180 → 90 → 45   (2 が 3 回)
x =  45: minFactor = 3 → 15 → 5          (3 が 2 回)
x =   5: minFactor = 5 → 1               (5 が 1 回)
```

## 実装 (Kotlin)

```kotlin
// エラトステネスの篩 + 最小素因数テーブル。前計算 O(n log log n)
class Sieve(val n: Int) {
    // minFactor[x] = x の最小の素因数 (x >= 2)。x が素数なら minFactor[x] == x
    val minFactor = IntArray(n + 1)
    val primes = mutableListOf<Int>()   // n 以下の素数 (昇順)

    init {
        for (i in 2..n) {
            if (minFactor[i] != 0) continue   // すでに誰かの倍数として消されている
            minFactor[i] = i
            primes.add(i)
            // i*i 未満の倍数は、もっと小さい素数で消されている
            var j = i.toLong() * i
            while (j <= n) {
                if (minFactor[j.toInt()] == 0) minFactor[j.toInt()] = i
                j += i
            }
        }
    }

    fun isPrime(x: Int): Boolean = x >= 2 && minFactor[x] == x

    // x (2 <= x <= n) の素因数分解。最小素因数で割り続けるので O(log x)
    fun factorize(x: Int): List<Pair<Int, Int>> {
        val res = mutableListOf<Pair<Int, Int>>()
        var y = x
        while (y > 1) {
            val p = minFactor[y]
            var e = 0
            while (y % p == 0) {
                y /= p
                e++
            }
            res.add(p to e)
        }
        return res
    }
}
```

## 使い方

[ABC 084 D - 2017-like Number](https://atcoder.jp/contests/abc084/tasks/abc084_d) を解くコードです。
「x と (x+1)/2 がどちらも素数」である奇数 x が、区間 [l, r] にいくつあるかを Q 回答えます。
篩で素数表を作り、累積和で数えます。

```kotlin
fun main() {
    val br = System.`in`.bufferedReader()
    var st = java.util.StringTokenizer("")
    fun next(): String {
        while (!st.hasMoreTokens()) st = java.util.StringTokenizer(br.readLine())
        return st.nextToken()
    }

    // ABC 084 D: x と (x+1)/2 が両方素数である奇数 x を「2017 に似た数」と呼ぶ
    val max = 100000
    val sieve = Sieve(max)
    // cnt[x] = 1 以上 x 以下の「2017 に似た数」の個数 (累積和)
    val cnt = IntArray(max + 1)
    for (x in 1..max) {
        val like = x % 2 == 1 && sieve.isPrime(x) && sieve.isPrime((x + 1) / 2)
        cnt[x] = cnt[x - 1] + (if (like) 1 else 0)
    }

    val q = next().toInt()
    val sb = StringBuilder()
    repeat(q) {
        val l = next().toInt()
        val r = next().toInt()
        sb.appendLine(cnt[r] - cnt[l - 1])
    }
    print(sb)

    // 素因数分解の例: sieve.factorize(360) → [(2, 3), (3, 2), (5, 1)]
}
```

入力例

```
4
13 13
7 11
7 11
2017 2017
```

出力例

```
1
0
0
1
```

## 例題

- [ABC 084 D - 2017-like Number](https://atcoder.jp/contests/abc084/tasks/abc084_d) — 上の「使い方」そのもの。篩 + 累積和
- [ABC 177 E - Coprime](https://atcoder.jp/contests/abc177/tasks/abc177_e) — 各 A_i を `factorize` して、同じ素因数が 2 回以上出てくるか調べる
- [ABC 250 D - 250-like Number](https://atcoder.jp/contests/abc250/tasks/abc250_d) — k = p × q^3 (p < q は素数) となる N 以下の k の個数。10^6 までの素数リストを作って数える

## 注意点・よくあるミス

- 配列は **n + 1 個** 必要 (0〜n)。`IntArray(n)` だと n 自身が範囲外になります。
- `i * i` は Int だと i が 46341 以上でオーバーフローします。上の実装では `i.toLong() * i` にしています。
- **0 と 1 は素数ではない**。`isPrime(0)`, `isPrime(1)` は `false` です。
- n = 10^7 で IntArray は約 40MB。10^8 はメモリ・時間ともに厳しいです。
- `factorize(1)` は空リストを返します。素因数分解の対象が 1 になりうる問題では気をつけましょう。
- 「篩を毎回作り直す」のはムダ。最大値で **1 回だけ** 作って使い回します。
