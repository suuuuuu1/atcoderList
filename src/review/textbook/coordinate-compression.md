---
title: 座標圧縮
category: 基本テクニック
acl: -
genres: 座標圧縮, ソート
order: 2050
summary: 大きな値を「小さい方から何番目か」に置き換えて配列で扱えるようにする
level: 緑
keywords: coordinate compression, 座圧, 圧縮, 順位, ランク, rank, 大きな座標, 値が 10^9, lower_bound
---

## 何ができる？

値の **大小関係だけ** が大事な問題で、10^9 のような大きな値を 0, 1, 2, ... (小さい方から何番目か) に置き換えます。
置き換えた後は、長さ「値の種類数」の配列が使えるようになります。

| 操作 | 内容 | 計算量 |
| --- | --- | --- |
| `CoordinateCompression(values)` | 値の一覧から、重複を除いてソートした表を作る | O(N log N) |
| `size` | 値の種類数 | O(1) |
| `index(x)` | x が小さい方から何番目か (0-indexed)。x は values に含まれる値 | O(log N) |
| `lowerBound(x)` | x **以上** の値のうち最小のものの番号 (無ければ size) | O(log N) |
| `value(i)` | i 番目の値 (元に戻す) | O(1) |

## いつ使う？

- 座標や値が **最大 10^9** と大きいが、**個数は 2×10^5 程度** しかない
- 値の **大小関係 (順位) しか使わない** (「何番目に小さいか」「自分より小さいものはいくつ」)
- 値を配列の添字にしたい: [いもす法](imos.md)、[Fenwick Tree (BIT)](fenwicktree.md)、[セグメント木](segtree.md)、DP など
- 「転倒数 (反転数) を BIT で数える」「マス目の空の行・列を消して詰める」

## しくみ

1. 出てくる値を全部集める
2. ソートして重複を消す → これが「表」
3. 元の値 x を、表の中での位置 (二分探索) に置き換える

```
元の値:   [ 100, 5, 100, 1000000000, 42 ]
表:       [ 5, 42, 100, 1000000000 ]   (ソート + 重複削除)
           0   1    2         3
圧縮後:   [ 2, 0, 2, 3, 1 ]
```

大小関係と「同じ値かどうか」はそのまま保たれます。
差 (距離) の情報は失われるので、距離が必要なら `value(i)` で元の値に戻して使います。

## 実装 (Kotlin)

```kotlin
// 座標圧縮。値を「小さい方から何番目か」(0-indexed) に置き換える
class CoordinateCompression(values: LongArray) {
    // 重複を除いて昇順に並べた値の表
    private val sorted: LongArray = run {
        val s = values.copyOf()
        s.sort()
        var m = 0
        for (i in s.indices) {
            if (i == 0 || s[i] != s[i - 1]) s[m++] = s[i]
        }
        s.copyOf(m)
    }

    // 値の種類数
    val size: Int get() = sorted.size

    // x 以上の値のうち最小のものの番号。無ければ size
    fun lowerBound(x: Long): Int {
        var lo = 0
        var hi = sorted.size
        while (lo < hi) {
            val mid = (lo + hi) ushr 1
            if (sorted[mid] < x) lo = mid + 1 else hi = mid
        }
        return lo
    }

    // x が何番目か。x は最初に渡した values に含まれている値であること
    fun index(x: Long): Int {
        val i = lowerBound(x)
        require(i < sorted.size && sorted[i] == x) { "$x は登録されていない値です" }
        return i
    }

    // i 番目の値 (圧縮前の値に戻す)
    fun value(i: Int): Long = sorted[i]
}

// 配列をまるごと圧縮する便利関数
fun compress(a: LongArray): IntArray {
    val cc = CoordinateCompression(a)
    return IntArray(a.size) { cc.index(a[it]) }
}
```

## 使い方

N 個の数を読み、それぞれを「小さい方から何番目か (0 始まり)」に置き換えて出力します。
続けて、各値以上で最小の登録値の番号を求める例です。

```kotlin
fun main() {
    val br = System.`in`.bufferedReader()
    var st = java.util.StringTokenizer("")
    fun next(): String {
        while (!st.hasMoreTokens()) st = java.util.StringTokenizer(br.readLine())
        return st.nextToken()
    }

    val n = next().toInt()
    val a = LongArray(n) { next().toLong() }

    val b = compress(a)
    println(b.joinToString(" "))

    val cc = CoordinateCompression(a)
    println(cc.size)                 // 値の種類数
    println(cc.lowerBound(50))       // 50 以上で最小の値 (100) の番号
    println(cc.value(cc.size - 1))   // 一番大きい値
}
```

入力例

```
5
100 5 100 1000000000 42
```

出力例

```
2 0 2 3 1
4
2
1000000000
```

## 例題

- [ABC 036 C - 座圧](https://atcoder.jp/contests/abc036/tasks/abc036_c) — そのまま座標圧縮するだけの練習問題
- [ABC 213 C - Reorder Cards](https://atcoder.jp/contests/abc213/tasks/abc213_c) — 行番号と列番号をそれぞれ座標圧縮する
- [ABC 188 D - Snuke Prime](https://atcoder.jp/contests/abc188/tasks/abc188_d) — 日付を座標圧縮してからいもす法 (イベントソートでも可)

## 注意点・よくあるミス

- **重複を消し忘れる** と、同じ値なのに違う番号になります。
- 区間 [l, r) を扱うときは、**l と r の両方** (場合によっては r+1 も) を表に入れておく。
  入れていない値を `index` で探すと見つかりません。
- 圧縮すると **差 (距離) の情報が消える**。面積や長さを求めるときは `value(i+1) - value(i)` で元に戻して使う。
- `values.distinct().sorted()` でも表は作れますが、`List<Long>` (ボクシング) になるので少し遅めです。
- `java.util.Arrays.binarySearch` を使うなら、見つからないときに負の値が返ることに注意。
