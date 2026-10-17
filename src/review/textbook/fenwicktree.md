---
title: Fenwick Tree (BIT)
category: データ構造
acl: fenwicktree
genres: セグメント木・BIT, 累積和, 座標圧縮
order: 120
summary: 1 点に足す操作と区間の和を、どちらも O(log N) で行う
---

## 何ができる？

長さ N の数列 a[0], a[1], ..., a[N-1] (最初は全部 0) を管理します。
「累積和」は値が変わると作り直しが必要ですが、Fenwick Tree (BIT, Binary Indexed Tree) なら
**値の更新** と **区間和** の両方を速く行えます。

| 操作 | 内容 | 計算量 |
| --- | --- | --- |
| `FenwickTree(n)` | 長さ n、全要素 0 で作る | O(n) |
| `add(p, x)` | a[p] += x | O(log n) |
| `sum(l, r)` | a[l] + a[l+1] + ... + a[r-1] を返す (**r は含まない**) | O(log n) |

比べてみると:

| | 1 点更新 | 区間和 |
| --- | --- | --- |
| 配列そのまま | O(1) | O(N) |
| 累積和 | O(N) | O(1) |
| Fenwick Tree | O(log N) | O(log N) |

## いつ使う？

- 「a[p] に x を足す」と「区間 [l, r) の和を答える」が **混ざって** たくさん来る
- **転倒数** (i < j かつ a[i] > a[j] となる組の数) を求めたい
- 「自分より左にある、自分より小さい要素の個数」のような数え上げ
  (値を添字にして、出現回数を BIT で数える。値が大きければ座標圧縮してから)
- 区間最小・区間最大など和以外の演算が必要なら → セグメント木 を使う

## しくみ

1-indexed で考えます。添字 i の箱 `data[i]` は、**i の 2 進数の一番下の 1 のビット (`i and -i`) の長さ分**
の区間の和を持っています。

```
添字 i :  1    2    3    4    5    6    7    8
i and -i: 1    2    1    4    1    2    1    8

data[8] = a1 + a2 + ... + a8
data[4] = a1 + a2 + a3 + a4
data[6] =                     a5 + a6
data[2] = a1 + a2
data[1] = a1   data[3] = a3   data[5] = a5   data[7] = a7

  |<---------------- data[8] ---------------->|
  |<------ data[4] ------>|
  |<-data[2]->|           |<-data[6]->|
  |d[1]|      |d[3]|      |d[5]|      |d[7]|
  | a1 | a2   | a3 | a4   | a5 | a6   | a7 | a8 |
```

- **先頭から r 個の和** (a1 + ... + ar): `i = r` から始めて `data[i]` を足し、`i -= i and -i` で次へ。
  例: r = 7 → data[7] + data[6] + data[4]。ビットが 1 つずつ消えるので O(log N) 回。
- **a[p] に x を足す**: `i = p` から始めて `data[i] += x`、`i += i and -i` で次へ。
  例: p = 3 → data[3], data[4], data[8] を更新。
- **区間和 [l, r)** は「先頭から r 個の和 − 先頭から l 個の和」で求めます。

外から使うときは 0-indexed (ACL と同じ) で、内部で +1 して 1-indexed にしています。

## 実装 (Kotlin)

```kotlin
class FenwickTree(private val n: Int) {
    // data[i - 1] が 1-indexed の i 番目の箱
    private val data = LongArray(n)

    // a[p] += x  (0-indexed)
    fun add(p: Int, x: Long) {
        require(p in 0 until n)
        var i = p + 1
        while (i <= n) {
            data[i - 1] += x
            i += i and -i
        }
    }

    // a[l] + ... + a[r - 1]  (r は含まない)
    fun sum(l: Int, r: Int): Long {
        require(0 <= l && l <= r && r <= n)
        return prefixSum(r) - prefixSum(l)
    }

    // a[0] + ... + a[r - 1]
    private fun prefixSum(r: Int): Long {
        var s = 0L
        var i = r
        while (i > 0) {
            s += data[i - 1]
            i -= i and -i
        }
        return s
    }
}
```

## 使い方

### 例 1: 1 点加算・区間和

[practice2 B - Fenwick Tree](https://atcoder.jp/contests/practice2/tasks/practice2_b) を解くコードです。
`0 p x` なら a[p] += x、`1 l r` なら a[l] + ... + a[r-1] を出力します。

```kotlin
fun main() {
    val br = System.`in`.bufferedReader()
    var st = java.util.StringTokenizer("")
    fun next(): String {
        while (!st.hasMoreTokens()) st = java.util.StringTokenizer(br.readLine())
        return st.nextToken()
    }

    val n = next().toInt()
    val q = next().toInt()
    val fw = FenwickTree(n)
    for (i in 0 until n) fw.add(i, next().toLong())   // 初期値は add で入れる
    val sb = StringBuilder()
    repeat(q) {
        val t = next().toInt()
        if (t == 0) {
            val p = next().toInt()
            val x = next().toLong()
            fw.add(p, x)
        } else {
            val l = next().toInt()
            val r = next().toInt()
            sb.appendLine(fw.sum(l, r))
        }
    }
    print(sb)
}
```

入力例

```
5 5
1 2 3 4 5
1 0 5
1 2 4
0 3 10
1 0 5
1 0 3
```

出力例

```
15
7
25
6
```

### 例 2: 転倒数

転倒数 = 「i < j かつ a[i] > a[j]」となる組 (i, j) の個数。バブルソートで必要な交換回数と同じです。

**左から順に見ていき、「これまでに出てきた値のうち、自分より大きいものの個数」を足していきます。**
BIT の添字を「値」にして、値 v が出てきたら `add(v, 1)` します。
すると「これまでに出てきた値で v より大きいものの個数」は `sum(v + 1, 上限)` で求まります。

```
a = [3, 1, 2, 0]

j=0: a=3  出てきた値 {}         → 3 より大きい: 0 個
j=1: a=1  出てきた値 {3}        → 1 より大きい: 1 個 (3)
j=2: a=2  出てきた値 {3,1}      → 2 より大きい: 1 個 (3)
j=3: a=0  出てきた値 {3,1,2}    → 0 より大きい: 3 個
合計 5
```

入力: 1 行目に N、2 行目に 0〜N-1 の並べ替え a が与えられ、転倒数を出力します。

```kotlin
// a の値が 0 以上 maxValue 未満のときの転倒数
fun inversionCount(a: IntArray, maxValue: Int): Long {
    val fw = FenwickTree(maxValue)
    var count = 0L
    for (j in a.indices) {
        count += fw.sum(a[j] + 1, maxValue)   // これまでに出た a[j] より大きい値の個数
        fw.add(a[j], 1L)
    }
    return count
}

fun main() {
    val br = System.`in`.bufferedReader()
    val n = br.readLine().trim().toInt()
    val a = br.readLine().trim().split(" ").map { it.toInt() }.toIntArray()
    println(inversionCount(a, n))
}
```

入力例

```
4
3 1 2 0
```

出力例

```
5
```

値が大きい (例: 10^9 まで) ときや同じ値が含まれるときは、まず **座標圧縮** して 0〜(種類数-1) に置き換えます。
同じ値どうしは `sum(a[j] + 1, ...)` に数えられないので、「a[i] > a[j] (等号なし)」がそのまま正しく数えられます。

## 例題

- [practice2 B - Fenwick Tree](https://atcoder.jp/contests/practice2/tasks/practice2_b) — 1 点加算・区間和そのもの
- [ABC 190 F - Shift and Inversions](https://atcoder.jp/contests/abc190/tasks/abc190_f) — 最初の転倒数を BIT で求め、先頭を末尾に回したときの変化を O(1) で更新
- [ABC 231 F - Jealous Two](https://atcoder.jp/contests/abc231/tasks/abc231_f) — ソート + 座標圧縮 + BIT で「条件を満たす組の数」を数える

## 注意点・よくあるミス

- `sum(l, r)` は **r を含まない** 半開区間 [l, r)。「l 番目から r 番目まで (両端含む)」なら `sum(l, r + 1)`。
- 入力が 1-indexed なら `- 1` してから渡す。
- 和は簡単に 2^31 を超えるので **Long** で持つ (この実装は LongArray)。転倒数も最大 N(N-1)/2 なので Long。
- 「a[p] を x に **変更**」は直接できません。`add(p, x - 今の値)` とし、今の値は別の配列で覚えておきます
  (`sum(p, p + 1)` でも取れます)。
- 転倒数で値を添字にするとき、値が N 以上・負・重複ありなら座標圧縮が必要。BIT のサイズは「値の種類数」。
- 区間最小・区間最大は BIT ではうまくいきません (引き算できないため)。セグメント木を使います。
