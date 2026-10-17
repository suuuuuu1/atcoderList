---
title: 全探索
category: 全探索・探索
acl: -
genres: 全探索
order: 1010
summary: ありうる候補をすべて試す。計算量 10^8 以内なら最強の解法
level: 灰
keywords: 全探索, brute force, 総当たり, 多重ループ, 計算量, 全部試す, 何通り
---

## 何ができる？

**ありうる候補をすべて試して、条件を満たすものを数えたり、最大・最小を求めたりする** 方法です。
考え方が単純なのでミスが少なく、間に合うなら最優先で使う解法です。

| パターン | 書き方 | 計算量 |
| --- | --- | --- |
| 1 つ選ぶ | `for (i in 0 until n)` | O(n) |
| 2 つ選ぶ (i < j) | 二重ループ `for (j in i + 1 until n)` | O(n^2) |
| 3 つ選ぶ (i < j < k) | 三重ループ | O(n^3) |
| 値の範囲を全部試す | `for (x in 0..k)` | O(k) |
| 部分集合を全部試す | [bit全探索](#textbook/bit-brute-force) | O(2^n × n) |
| 並べ方を全部試す | [順列全探索](#textbook/permutation) | O(n! × n) |

## いつ使う？

- **制約が小さい** (N ≦ 100, N ≦ 1000, 値が 10^4 以下など) → まず全探索で間に合わないか計算する
- 「〜を満たす組は何通り？」「〜の最大値は？」で、候補の数が数えられるくらい少ない
- 正解が分からない問題の **愚直解 (検証用)** として、ランダムテストの比較相手に使う
- 一見大きそうでも「1 つを決めると残りが決まる」なら、ループを 1 つ減らせる (下の例)

## しくみ

### 計算量の見積もり

AtCoder の制限時間 2 秒で、**だいたい 10^8 回の単純な計算** ができると考えます (Kotlin でも同程度)。

| N の上限 | 間に合う計算量の目安 |
| --- | --- |
| 10 前後 | O(N!), O(N × N!) |
| 20 前後 | O(2^N), O(N × 2^N) |
| 100〜500 | O(N^3) |
| 1000〜5000 | O(N^2) |
| 10^5〜10^6 | O(N log N), O(N) |

例: N = 2000 で O(N^2) = 4×10^6 → 余裕。N = 2000 で O(N^3) = 8×10^9 → 間に合わない。

### ループを減らす工夫

「3 つの値の合計が決まっている」なら、2 つを決めれば 3 つ目は計算で求まります。
三重ループ O(N^3) → 二重ループ O(N^2) にできます。

[ABC 085 C - Otoshidama](https://atcoder.jp/contests/abc085/tasks/abc085_c) の例
(N 枚のお札で Y 円。10000 円札 a 枚、5000 円札 b 枚、1000 円札 c 枚):

```kotlin
fun main() {
    val (n, y) = readln().split(" ").map { it.toInt() }
    for (a in 0..n) {             // 10000 円札の枚数
        for (b in 0..n - a) {     // 5000 円札の枚数
            val c = n - a - b     // 1000 円札は残りで決まる
            if (10000 * a + 5000 * b + 1000 * c == y) {
                println("$a $b $c")
                return
            }
        }
    }
    println("-1 -1 -1")
}
```

N ≦ 2000 なので約 2×10^6 回で済みます (答えが複数あるときはどれを出力しても正解)。

## 実装 (Kotlin)

よく使う「2 つ選ぶ」「3 つ選ぶ」の形です。`if` の中身を問題に合わせて書き換えて使います。

```kotlin
// i < j となるペアのうち a[i] + a[j] == k となるものの個数  O(n^2)
fun countPairs(a: IntArray, k: Int): Int {
    var cnt = 0
    for (i in 0 until a.size) {
        for (j in i + 1 until a.size) {     // j は i の次から (同じペアを 2 回数えない)
            if (a[i] + a[j] == k) cnt++
        }
    }
    return cnt
}

// i < j < l となる 3 つ組のうち和が s になるものの個数  O(n^3)
fun countTriples(a: IntArray, s: Int): Int {
    var cnt = 0
    for (i in 0 until a.size) {
        for (j in i + 1 until a.size) {
            for (l in j + 1 until a.size) {
                if (a[i] + a[j] + a[l] == s) cnt++
            }
        }
    }
    return cnt
}
```

## 使い方

1 行目に N と K、2 行目に数列 a が与えられたとき、
「和が K になるペアの個数」と「和が K になる 3 つ組の個数」を出力する例です。

```kotlin
fun main() {
    val (n, k) = readln().split(" ").map { it.toInt() }
    val a = readln().split(" ").map { it.toInt() }.toIntArray()
    println(countPairs(a, k))
    println(countTriples(a, k))
}
```

入力例

```
5 6
1 2 3 4 5
```

出力例

```
2
1
```

和が 6 になるペアは (1, 5) と (2, 4) の 2 個、3 つ組は (1, 2, 3) の 1 個です。

## 例題

- [ABC 087 B - Coins](https://atcoder.jp/contests/abc087/tasks/abc087_b) — 500 円・100 円・50 円玉の枚数を三重ループで全部試す
- [ABC 085 C - Otoshidama](https://atcoder.jp/contests/abc085/tasks/abc085_c) — 上の「しくみ」の例。2 つ決めれば 3 つ目が決まる
- [ABC 051 B - Sum of Three Integers](https://atcoder.jp/contests/abc051/tasks/abc051_b) — X, Y を全部試し、Z = S - X - Y が範囲内か調べる (三重ループだと TLE)

## 注意点・よくあるミス

- **書く前に計算量を見積もる**。ループ回数を掛け算して 10^8 を超えそうなら別の方法を考える。
- ペアを数えるとき `for (j in 0 until n)` にすると (i, j) と (j, i) を 2 回数え、i == j も含んでしまう。
  「順番を区別しない・同じものを 2 回使わない」なら `j in i + 1 until n`。
- `0..n` は n を **含む**、`0 until n` は n を **含まない**。枚数や個数を試すときは 0 個も含めて `0..n` が多い。
- 答えが見つかったら `return` で抜けると速い (main の中なら `return` でプログラム終了)。
  二重ループを抜けたいときは、関数にして `return` するか、ラベル付き `break@outer` を使う。
- 和や積が `Int` を超えないか確認する。超えるなら `Long`。
