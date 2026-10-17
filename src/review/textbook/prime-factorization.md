---
title: 素数判定・素因数分解・約数列挙
category: 数学
acl: -
genres: 整数論, 数学
order: 201
summary: √N まで試し割りして、素数判定・素因数分解・約数列挙をする
level: 茶
keywords: 素数, prime, 素因数分解, factorization, 約数, divisors, 試し割り, ルートN, 約数の個数
---

## 何ができる？

1 つの整数 N (10^12 くらいまで) について、次のことを **O(√N)** で行います。

| 関数 | 内容 | 計算量 |
| --- | --- | --- |
| `isPrime(n)` | n が素数なら `true` | O(√n) |
| `primeFactorize(n)` | 素因数分解。`(素数, 指数)` のリストを返す (例: 360 → `[(2,3), (3,2), (5,1)]`) | O(√n) |
| `divisors(n)` | 約数を昇順に列挙 (例: 12 → `[1, 2, 3, 4, 6, 12]`) | O(√n) |

N = 10^12 なら √N = 10^6 回のループなので一瞬です。

## いつ使う？

- 「N は素数か？」を 1 回 (または数回) 調べる
- 「N の約数をすべて試す」「約数の個数」→ 約数列挙
- 「N を素数の積で表す」「N = p^a × q^b …」「N! や N^2 の約数の個数」→ 素因数分解
- 「最大公約数の候補は M の約数のどれか」のように、**候補が約数に限られる** 問題
- たくさんの数 (例えば 10^6 個) を素因数分解したいとき → [エラトステネスの篩](sieve.md) の最小素因数テーブルを使う

## しくみ

**ポイント: 約数は √N を境にペアになっている**

```
N = 36 の約数のペア (a × b = 36)
  1 × 36
  2 × 18
  3 × 12
  4 ×  9
  6 ×  6   ← √36 = 6 で折り返し
```

- **素数判定**: 2 から √N までのどれでも割り切れなければ素数。
  N が合成数なら、ペアの小さい方は必ず √N 以下にあるので、そこまで調べれば十分です。
- **約数列挙**: 1 から √N まで割ってみて、割り切れた i と N / i の両方を約数にします。
- **素因数分解**: 2 から順に「割れるだけ割る」を繰り返します。
  √x まで割っても x が 1 より大きく残ったら、残りは 1 つの素数です。
  (小さい素数から割り切っているので、4 や 6 のような合成数で割り切れることはありません)

```
360 を素因数分解
  360 ÷ 2 ÷ 2 ÷ 2 = 45     → (2, 3)
   45 ÷ 3 ÷ 3     =  5     → (3, 2)
  4 × 4 > 5 なのでループ終了。残った 5 は素数 → (5, 1)
```

## 実装 (Kotlin)

```kotlin
// n が素数か。O(√n)
fun isPrime(n: Long): Boolean {
    if (n < 2) return false
    var i = 2L
    while (i * i <= n) {
        if (n % i == 0L) return false
        i++
    }
    return true
}

// 素因数分解。(素数, 指数) のリストを素数の昇順で返す。O(√n)
// 例: 360 → [(2, 3), (3, 2), (5, 1)]
fun primeFactorize(n: Long): List<Pair<Long, Int>> {
    val res = mutableListOf<Pair<Long, Int>>()
    var x = n
    var p = 2L
    while (p * p <= x) {
        if (x % p == 0L) {
            var e = 0
            while (x % p == 0L) {
                x /= p
                e++
            }
            res.add(p to e)
        }
        p++
    }
    if (x > 1) res.add(x to 1)   // 最後に残った数は素数
    return res
}

// 約数を昇順で列挙する。O(√n)
fun divisors(n: Long): List<Long> {
    val small = mutableListOf<Long>()
    val large = mutableListOf<Long>()
    var i = 1L
    while (i * i <= n) {
        if (n % i == 0L) {
            small.add(i)
            if (i != n / i) large.add(n / i)   // 平方数のとき同じ数を 2 回入れない
        }
        i++
    }
    return small + large.reversed()
}
```

## 使い方

[ABC 169 D - Div Game](https://atcoder.jp/contests/abc169/tasks/abc169_d) を解くコードです。
N (≤ 10^12) を「まだ使っていない素数べき p^e」で割る操作が最大何回できるかを答えます。
素因数ごとに、p^1, p^2, p^3, … と小さい順に使うのが最適です。

```kotlin
fun main() {
    val n = readLine()!!.trim().toLong()

    // ABC 169 D: 素因数 p^e ごとに、p^1, p^2, p^3, ... と小さい順に割れるだけ割る
    var ans = 0
    for ((_, e) in primeFactorize(n)) {
        var rest = e
        var k = 1
        while (rest >= k) {
            rest -= k
            k++
            ans++
        }
    }
    println(ans)

    // その他の使い方
    // println(isPrime(n))     // 素数か
    // println(divisors(n))    // 約数の一覧 (昇順)
}
```

入力例

```
24
```

出力例

```
3
```

(24 = 2^3 × 3。2^1, 2^2, 3^1 の 3 回)

## 例題

- [ABC 169 D - Div Game](https://atcoder.jp/contests/abc169/tasks/abc169_d) — 上の「使い方」そのもの。素因数分解
- [ABC 112 D - Partition](https://atcoder.jp/contests/abc112/tasks/abc112_d) — 和が M の N 個の数の最大公約数の最大値。M の約数を列挙して、`d * N <= M` を満たす最大の d
- [ABC 190 D - Staircase Sequences](https://atcoder.jp/contests/abc190/tasks/abc190_d) — 和が N の連続する整数列の個数。2N の約数のペアを調べる

## 注意点・よくあるミス

- **N は Long** で読む。10^12 は Int (約 2.1×10^9 まで) に入りません。ループ変数 `i` も Long にします
  (`i * i` が Int だとオーバーフローします)。
- ループ条件は `i * i <= n` と書く。`i <= Math.sqrt(n.toDouble())` は誤差で 1 ずれることがあります。
- **1 は素数ではない**。`isPrime(1)` は `false` です。
- 約数列挙で、N が平方数のとき √N を 2 回入れないように注意 (`i != n / i` のチェック)。
- 最後に残った `x > 1` を素因数に入れ忘れるミスが多いです (例: 2 × 1000000007 の 1000000007)。
- O(√N) なので、**N = 10^18 だと √N = 10^9 回で間に合いません**。また、10^6 以下の数を **たくさん** 分解するなら
  篩 (最小素因数テーブル) の方が速いです。
