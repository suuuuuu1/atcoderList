---
title: 遅延セグメント木
category: データ構造
acl: lazysegtree
genres: セグメント木・BIT
order: 140
summary: 区間への一括更新と区間の和・最小などを O(log N) で行う
level: 水
keywords: 遅延セグ木, lazy segment tree, 区間更新, 区間加算, 区間代入, RAQ, RUQ
---

## 何ができる？

セグメント木は「1 点の更新」しかできませんが、遅延セグメント木 (遅延セグ木) は
**区間 [l, r) 全体に同じ操作をまとめて行う** (例: 全部に x を足す、全部を x に変える) ことができます。
そのうえで区間の最小・和などを O(log N) で求められます。

渡すものは 5 つです。

| 名前 | 意味 | 区間加算・区間最小の場合 |
| --- | --- | --- |
| `op(a, b)` | 値 S を 2 つまとめる (結合法則を満たす) | `minOf(a, b)` |
| `e` | op の単位元 | `Long.MAX_VALUE` |
| `mapping(f, x)` | 操作 f を値 x に施した結果 | `x + f` (x が e のときは e のまま) |
| `composition(f, g)` | 「先に g、あとで f」をまとめた 1 つの操作 | `f + g` |
| `id` | 何もしない操作 (`mapping(id, x) == x`) | `0` |

S は「値」の型、F は「操作」の型です。

| 操作 | 内容 | 計算量 |
| --- | --- | --- |
| `LazySegTree(n, ...)` / `LazySegTree(list, ...)` | 全要素 e / list の内容で作る | O(n) |
| `set(p, x)` | a[p] = x | O(log n) |
| `get(p)` | a[p] を返す | O(log n) |
| `prod(l, r)` | op(a[l], ..., a[r-1]) を返す (**r は含まない**) | O(log n) |
| `allProd()` | op(a[0], ..., a[n-1]) を返す | O(1) |
| `apply(p, f)` | a[p] = mapping(f, a[p]) | O(log n) |
| `applyRange(l, r, f)` | l ≤ i < r のすべての i で a[i] = mapping(f, a[i]) | O(log n) |
| `maxRight(l, g)` / `minLeft(r, g)` | セグメント木と同じ二分探索 | O(log n) |

(ACL の `apply(l, r, f)` は、この実装では名前を分けて `applyRange(l, r, f)` にしています。)

## いつ使う？

- 「区間 [l, r) に x を足す」「区間 [l, r) を x に変える」のような **区間への更新** と、
  「区間の最小 / 最大 / 和」の **区間の取得** が両方たくさん来る
- 区間の 0 と 1 を反転させる、区間に一次関数 `a*x + b` を施す、など
- 区間への更新だけで、取得は 1 点だけ → いもす法 や BIT (差分を足す) でもできることが多い
- 更新が全部終わってから聞かれる → いもす法 / 累積和 で十分

## しくみ

木の形はセグメント木と同じです。違いは、各ノードに **「まだ子に伝えていない操作 (遅延)」** を持たせることです。

```
区間加算・区間最小。a = [5, 3, 7, 9, 6, 4, 1, 2] に applyRange(0, 4, +10) をした直後:

                     [0,8) min=1
            /                          \
   [0,4) min=13  lz=+10           [4,8) min=1
     /        \                    /        \
 [0,2)=3    [2,4)=7           [4,6)=4    [6,8)=1
  /  \       /  \              /  \       /  \
 5    3     7    9            6    4     1    2

 [0,4) のノードだけ値を 3 → 13 に更新し、「子に +10 を伝える予定」を lz に書いておく。
 その下 ([0,2), [2,4), 葉) はまだ古い値のまま (遅延中)。
 [0,4) の中身を細かく見る必要が出たときに初めて子へ +10 を渡す (push)。
```

- `applyRange(l, r, f)` では、区間 [l, r) をちょうど覆うノード (O(log N) 個) だけに
  「値を `mapping(f, 値)` に更新」+「遅延に f を重ねる (`composition(f, 遅延)`)」をします。
  その下の子孫には **まだ伝えません**。だから速いのです。
- あとで子を見る必要が出たとき (prod や別の applyRange で下へ降りるとき) に、
  遅延を子 2 つに渡して自分の遅延を id に戻します (**push**)。
- 子の値が変わったら、親の値を `op(左の子, 右の子)` で計算し直します (**update**)。

このためには次の条件が必要です。

- **mapping がまとめた値にも使える**: 区間の最小値 m に「全部に +x」をしたら、新しい最小値は m + x。
  個々の要素を見なくても、まとめた値だけで計算できる必要があります。
  (区間和で「全部に +x」なら、新しい和は「和 + x × 区間の長さ」。長さが必要なので S を (和, 長さ) のペアにします。)
- **composition で操作を 1 つにまとめられる**: +3 してから +5 = +8 のように。

## 実装 (Kotlin)

```kotlin
// S: 値の型, F: 操作の型
// op: 値をまとめる, e: op の単位元, mapping(f, x): 値 x に操作 f を施す,
// composition(f, g): g のあとに f をする操作, id: 何もしない操作
class LazySegTree<S, F>(
    v: List<S>,
    private val op: (S, S) -> S,
    private val e: S,
    private val mapping: (F, S) -> S,
    private val composition: (F, F) -> F,
    private val id: F,
) {
    constructor(
        n: Int, op: (S, S) -> S, e: S, mapping: (F, S) -> S, composition: (F, F) -> F, id: F,
    ) : this(List(n) { e }, op, e, mapping, composition, id)

    private val n = v.size
    private var size = 1
    private var log = 0
    init {
        while (size < n) { size *= 2; log++ }
    }
    private val d = MutableList(2 * size) { e }   // 各ノードの値
    private val lz = MutableList(size) { id }     // 子にまだ伝えていない操作
    init {
        for (i in 0 until n) d[size + i] = v[i]
        for (i in size - 1 downTo 1) update(i)
    }

    private fun update(k: Int) { d[k] = op(d[2 * k], d[2 * k + 1]) }

    private fun allApply(k: Int, f: F) {
        d[k] = mapping(f, d[k])
        if (k < size) lz[k] = composition(f, lz[k])
    }

    // ノード k の遅延を子に伝える
    private fun push(k: Int) {
        allApply(2 * k, lz[k])
        allApply(2 * k + 1, lz[k])
        lz[k] = id
    }

    fun set(p: Int, x: S) {
        require(p in 0 until n)
        val k = p + size
        for (i in log downTo 1) push(k shr i)
        d[k] = x
        for (i in 1..log) update(k shr i)
    }

    fun get(p: Int): S {
        require(p in 0 until n)
        val k = p + size
        for (i in log downTo 1) push(k shr i)
        return d[k]
    }

    // op(a[l], ..., a[r - 1])。l == r なら e
    fun prod(l: Int, r: Int): S {
        require(0 <= l && l <= r && r <= n)
        if (l == r) return e
        var lo = l + size
        var hi = r + size
        for (i in log downTo 1) {
            if (((lo shr i) shl i) != lo) push(lo shr i)
            if (((hi shr i) shl i) != hi) push((hi - 1) shr i)
        }
        var sml = e
        var smr = e
        while (lo < hi) {
            if (lo and 1 == 1) sml = op(sml, d[lo++])
            if (hi and 1 == 1) smr = op(d[--hi], smr)
            lo = lo shr 1
            hi = hi shr 1
        }
        return op(sml, smr)
    }

    fun allProd(): S = d[1]

    // a[p] = mapping(f, a[p])
    fun apply(p: Int, f: F) {
        require(p in 0 until n)
        val k = p + size
        for (i in log downTo 1) push(k shr i)
        d[k] = mapping(f, d[k])
        for (i in 1..log) update(k shr i)
    }

    // l <= i < r の a[i] すべてに f を施す
    fun applyRange(l: Int, r: Int, f: F) {
        require(0 <= l && l <= r && r <= n)
        if (l == r) return
        val l0 = l + size
        val r0 = r + size
        // 1. 上から遅延を伝える
        for (i in log downTo 1) {
            if (((l0 shr i) shl i) != l0) push(l0 shr i)
            if (((r0 shr i) shl i) != r0) push((r0 - 1) shr i)
        }
        // 2. 区間をちょうど覆うノードに f を施す
        var lo = l0
        var hi = r0
        while (lo < hi) {
            if (lo and 1 == 1) allApply(lo++, f)
            if (hi and 1 == 1) allApply(--hi, f)
            lo = lo shr 1
            hi = hi shr 1
        }
        // 3. 下から親の値を計算し直す
        for (i in 1..log) {
            if (((l0 shr i) shl i) != l0) update(l0 shr i)
            if (((r0 shr i) shl i) != r0) update((r0 - 1) shr i)
        }
    }

    // g(op(a[l], ..., a[r - 1])) が true となる最大の r (g(e) は true であること)
    fun maxRight(l: Int, g: (S) -> Boolean): Int {
        require(l in 0..n)
        require(g(e))
        if (l == n) return n
        var k = l + size
        for (i in log downTo 1) push(k shr i)
        var sm = e
        do {
            while (k % 2 == 0) k = k shr 1
            if (!g(op(sm, d[k]))) {
                while (k < size) {
                    push(k)
                    k *= 2
                    if (g(op(sm, d[k]))) {
                        sm = op(sm, d[k])
                        k++
                    }
                }
                return k - size
            }
            sm = op(sm, d[k])
            k++
        } while ((k and -k) != k)
        return n
    }

    // g(op(a[l], ..., a[r - 1])) が true となる最小の l (g(e) は true であること)
    fun minLeft(r: Int, g: (S) -> Boolean): Int {
        require(r in 0..n)
        require(g(e))
        if (r == 0) return 0
        var k = r + size
        for (i in log downTo 1) push((k - 1) shr i)
        var sm = e
        do {
            k--
            while (k > 1 && k % 2 == 1) k = k shr 1
            if (!g(op(d[k], sm))) {
                while (k < size) {
                    push(k)
                    k = 2 * k + 1
                    if (g(op(d[k], sm))) {
                        sm = op(d[k], sm)
                        k--
                    }
                }
                return k + 1 - size
            }
            sm = op(d[k], sm)
        } while ((k and -k) != k)
        return 0
    }
}
```

## 使い方

### 例 1: 区間加算・区間最小

入力: N Q、数列 a、続いて Q 個のクエリ (0-indexed)。
`0 l r x` なら a[l], ..., a[r-1] に x を足し、`1 l r` なら a[l], ..., a[r-1] の最小値を出力します。

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
    val a = List(n) { next().toLong() }

    val inf = Long.MAX_VALUE
    val seg = LazySegTree<Long, Long>(
        a,
        op = { x, y -> minOf(x, y) },
        e = inf,
        mapping = { f, x -> if (x == inf) x else x + f },  // e (= 空の区間) には足さない
        composition = { f, g -> f + g },                   // +g のあと +f = +(f+g)
        id = 0L,
    )

    val sb = StringBuilder()
    repeat(q) {
        val t = next().toInt()
        val l = next().toInt()
        val r = next().toInt()
        if (t == 0) {
            val x = next().toLong()
            seg.applyRange(l, r, x)
        } else {
            sb.appendLine(seg.prod(l, r))
        }
    }
    print(sb)
}
```

入力例

```
5 5
5 3 7 9 6
1 0 5
0 0 2 10
1 0 3
0 2 5 -4
1 1 5
```

出力例

```
3
7
2
```

(1 回目の更新で a = [15, 13, 7, 9, 6]、2 回目の更新で a = [15, 13, 3, 5, 2] になります。)

### 例 2: 区間アフィン変換・区間和

[practice2 K - Range Affine Range Sum](https://atcoder.jp/contests/practice2/tasks/practice2_k) を解くコードです。
`0 l r b c` なら a[i] = b × a[i] + c (l ≤ i < r)、`1 l r` なら区間和 mod 998244353 を出力します。
和を更新するには区間の長さが必要なので、S を (和, 長さ) にします。

```kotlin
const val MOD = 998244353L

data class SumLen(val sum: Long, val len: Long)   // 値: (区間和, 区間の長さ)
data class Affine(val b: Long, val c: Long)       // 操作: x → b*x + c

fun main() {
    val br = System.`in`.bufferedReader()
    var st = java.util.StringTokenizer("")
    fun next(): String {
        while (!st.hasMoreTokens()) st = java.util.StringTokenizer(br.readLine())
        return st.nextToken()
    }

    val n = next().toInt()
    val q = next().toInt()
    val a = List(n) { SumLen(next().toLong(), 1L) }

    val seg = LazySegTree<SumLen, Affine>(
        a,
        op = { x, y -> SumLen((x.sum + y.sum) % MOD, x.len + y.len) },
        e = SumLen(0L, 0L),
        // 区間の全要素に b*x+c → 和は b*和 + c*長さ
        mapping = { f, x -> SumLen((f.b * x.sum + f.c * x.len) % MOD, x.len) },
        // g のあと f: f(g(x)) = f.b*(g.b*x + g.c) + f.c
        composition = { f, g -> Affine(f.b * g.b % MOD, (f.b * g.c + f.c) % MOD) },
        id = Affine(1L, 0L),
    )

    val sb = StringBuilder()
    repeat(q) {
        val t = next().toInt()
        val l = next().toInt()
        val r = next().toInt()
        if (t == 0) {
            val b = next().toLong()
            val c = next().toLong()
            seg.applyRange(l, r, Affine(b, c))
        } else {
            sb.appendLine(seg.prod(l, r).sum)
        }
    }
    print(sb)
}
```

入力例

```
5 7
1 2 3 4 5
1 0 5
0 2 4 100 101
1 0 3
0 1 3 102 103
1 2 5
0 2 5 104 105
1 0 5
```

出力例

```
15
404
41511
4317767
```

## 例題

- [practice2 K - Range Affine Range Sum](https://atcoder.jp/contests/practice2/tasks/practice2_k) — 上の例 2 そのもの
- [practice2 L - Lazy Segment Tree](https://atcoder.jp/contests/practice2/tasks/practice2_l) — 0/1 列の区間反転と区間の転倒数。S に (0 の個数, 1 の個数, 転倒数) を持たせる
- [典型 90 問 029 - Long Bricks](https://atcoder.jp/contests/typical90/tasks/typical90_ac) — 区間を「max(今の値, x) に更新」+ 区間最大 (座標圧縮も必要)

## 注意点・よくあるミス

- **composition の順番**: `composition(f, g)` は「**先に g、あとで f**」。加算のように順番に関係ない操作なら
  気にしなくてよいですが、アフィン変換や「x に変更」では逆にすると間違えます。
- **mapping で e を壊さない**: 区間最小で e = `Long.MAX_VALUE` に `+ f` するとオーバーフローします。
  上の例のように「x が e ならそのまま」とするか、e を `Long.MAX_VALUE / 2` のような値にします。
  (N が 2 のべき乗でないとき、余った葉に e が入っているので、ここを壊すと答えがおかしくなります。)
- **区間和で長さを忘れる**: 「全部に +x」で和は `和 + x × 長さ` 。S に長さを持たせ、初期値の長さは 1、e の長さは 0。
- **id の選び方**: 加算なら 0、アフィンなら (1, 0)。「x に変更」なら「変更なし」を表す特別な値 (例: `Long.MIN_VALUE`) を id にし、
  mapping と composition で `f == id` なら何もしないように書きます。
- mod を取るときは掛け算の前に両方 mod 未満にしておき、Long でもあふれないようにする (998244353² < 9.2×10^18)。
- ジェネリクスなので S, F は箱 (オブジェクト) になり、普通のセグ木より遅めです。N, Q = 5×10^5 で
  時間が厳しい場合は、LongArray を使う専用版を書くことも考えましょう。
- `prod` / `applyRange` の区間は [l, r) で **r を含まない**。
