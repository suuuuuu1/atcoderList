---
title: 二分探索 (lower_bound / upper_bound)
category: 全探索・探索
acl: -
genres: 二分探索
order: 1040
summary: ソート済み配列で「x 以上が初めて現れる位置」を O(log N) で求める
level: 茶
keywords: 二分探索, binary search, lower_bound, upper_bound, lowerBound, 以上の個数, 未満の個数, ソート済み, 境界
---

## 何ができる？

**昇順にソートされた配列** に対して、「x 以上の値が初めて現れる位置」などを O(log N) で求めます。
C++ の `lower_bound` / `upper_bound` にあたる関数を自分で書きます。

| 操作 | 内容 | 計算量 |
| --- | --- | --- |
| `a.lowerBound(x)` | `a[i] >= x` となる最小の i (無ければ `a.size`) | O(log N) |
| `a.upperBound(x)` | `a[i] > x` となる最小の i (無ければ `a.size`) | O(log N) |

これを使うと、次のような値がすぐ分かります (a はソート済み)。

| 知りたいもの | 式 |
| --- | --- |
| x **未満** の個数 | `a.lowerBound(x)` |
| x **以下** の個数 | `a.upperBound(x)` |
| x **以上** の個数 | `a.size - a.lowerBound(x)` |
| x **より大きい** 個数 | `a.size - a.upperBound(x)` |
| x と等しい個数 | `a.upperBound(x) - a.lowerBound(x)` |
| l 以上 r 未満の個数 | `a.lowerBound(r) - a.lowerBound(l)` |
| x 以上で最小の値 | `a[a.lowerBound(x)]` (添字が `a.size` でないか確認) |

## いつ使う？

- 「x 以下の要素はいくつ？」「x に一番近い値は？」を **何度も** 聞かれる (クエリが多い)
- 3 つの配列から 1 つずつ選ぶなど、**真ん中を固定して両側を数える** 問題
- 二重ループ O(N^2) が間に合わず、片方をソートしておけば O(N log N) にできる
- 値そのものではなく「答え」を二分探索したい → [答えで二分探索](#textbook/binary-search-answer)

## しくみ

辞書で単語を探すときのように、**真ん中を見て、探す範囲を半分に絞る** ことを繰り返します。
N = 10^6 でも約 20 回で終わります。

lowerBound(x) は「`a[i] >= x` が true になる最初の場所」= 境界を探しています。

```
a      =  1   3   3   5   8      x = 3 のとき
a[i]>=3:  F   T   T   T   T
              ↑
         lowerBound(3) = 1

a[i]> 3:  F   F   F   T   T
                      ↑
         upperBound(3) = 3      → 3 の個数 = 3 - 1 = 2
```

実装では「必ず false の位置 `ng`」と「必ず true の位置 `ok`」を持ち、
その間を半分ずつ縮めていきます (ng = -1, ok = N から始める。添字 -1 と N は番兵で、実際には見ません)。

## 実装 (Kotlin)

```kotlin
// 昇順ソート済みの配列で、a[i] >= x となる最小の i を返す (無ければ size)
fun IntArray.lowerBound(x: Int): Int {
    var ng = -1
    var ok = size
    while (ok - ng > 1) {
        val mid = (ng + ok) / 2
        if (this[mid] >= x) ok = mid else ng = mid
    }
    return ok
}

// 昇順ソート済みの配列で、a[i] > x となる最小の i を返す (無ければ size)
fun IntArray.upperBound(x: Int): Int {
    var ng = -1
    var ok = size
    while (ok - ng > 1) {
        val mid = (ng + ok) / 2
        if (this[mid] > x) ok = mid else ng = mid
    }
    return ok
}

// LongArray 版
fun LongArray.lowerBound(x: Long): Int {
    var ng = -1
    var ok = size
    while (ok - ng > 1) {
        val mid = (ng + ok) / 2
        if (this[mid] >= x) ok = mid else ng = mid
    }
    return ok
}

fun LongArray.upperBound(x: Long): Int {
    var ng = -1
    var ok = size
    while (ok - ng > 1) {
        val mid = (ng + ok) / 2
        if (this[mid] > x) ok = mid else ng = mid
    }
    return ok
}
```

## 使い方

[ABC 077 C - Snuke Festival](https://atcoder.jp/contests/abc077/tasks/arc084_a) を解くコードです。
上部 A、中部 B、下部 C から 1 つずつ選び、A < B < C となる組の数を数えます。
**中部 b を固定** すると、「b 未満の A の個数」×「b より大きい C の個数」を足せばよいです。

```kotlin
fun main() {
    val n = readln().toInt()
    val a = readln().split(" ").map { it.toInt() }.toIntArray()
    val b = readln().split(" ").map { it.toInt() }.toIntArray()
    val c = readln().split(" ").map { it.toInt() }.toIntArray()
    a.sort()                               // 二分探索の前に必ずソート
    c.sort()
    var ans = 0L
    for (x in b) {
        val small = a.lowerBound(x)        // A の中で x 未満の個数
        val large = n - c.upperBound(x)    // C の中で x より大きい個数
        ans += small.toLong() * large
    }
    println(ans)
}
```

入力例

```
2
1 5
2 4
3 6
```

出力例

```
3
```

## 例題

- [ABC 077 C - Snuke Festival](https://atcoder.jp/contests/abc077/tasks/arc084_a) — 上の使い方の問題
- [ABC 143 D - Triangles](https://atcoder.jp/contests/abc143/tasks/abc143_d) — 2 本を固定し、3 本目の候補の個数を二分探索で数える
- [典型90 007 - CP Classes (★3)](https://atcoder.jp/contests/typical90/tasks/typical90_g) — 各生徒のレートに一番近いクラスを lowerBound で探す

## 注意点・よくあるミス

- **ソートを忘れない**。ソートされていない配列に二分探索しても正しい答えは出ません。
- 戻り値が `a.size` (= 該当なし) のことがある。`a[a.lowerBound(x)]` の前に範囲チェックを。
  「x 以下で最大の値」は `a.upperBound(x) - 1` の位置 (これが -1 なら無い)。
- **Kotlin 標準の `binarySearch` の罠**: 値が見つからないと **負の値** (-(挿入位置) - 1) を返し、
  同じ値が複数あると **どれの位置を返すか決まっていません**。lower_bound の代わりにはそのまま使えません。

  ```kotlin
  val a = intArrayOf(1, 3, 3, 3, 8)
  println(a.binarySearch(5))   // -5  (見つからない。挿入位置 4 → -(4) - 1)
  println(a.binarySearch(3))   // 1〜3 のどれか (ここでは 2) ← 最初の 3 とは限らない！
  println(a.lowerBound(3))     // 1   (自作の lowerBound なら必ず最初の位置)
  ```

- `List<Int>` に対して使いたい場合は、`toIntArray()` で変換するか、同じ形の関数を `List<Int>` 用に書く。
- 降順に並んだ配列に使うと逆の結果になります。降順のまま使いたいときは比較の向きを反転させること。
- `(ng + ok) / 2` は ng, ok が添字 (Int の範囲) なのでオーバーフローしません。値で二分探索するときは
  `Long` を使いましょう ([答えで二分探索](#textbook/binary-search-answer) 参照)。
