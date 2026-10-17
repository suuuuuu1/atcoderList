---
title: セグメント木
category: データ構造
acl: segtree
genres: セグメント木・BIT, 二分探索
order: 130
summary: 1 点更新と区間の和・最小・最大などを O(log N) で求める
---

## 何ができる？

長さ N の数列を管理し、**1 点の更新** と **区間をまとめた値 (和・最小・最大・gcd・xor など)** の取得を
どちらも O(log N) で行います。
「まとめ方」を自分で渡せる汎用版です。まとめ方は次の 2 つで指定します (これを **モノイド** と呼びます)。

- `op(a, b)`: 2 つの値をまとめる関数。**結合法則** `op(op(a, b), c) == op(a, op(b, c))` を満たすこと
- `e`: 単位元。どんな a についても `op(a, e) == op(e, a) == a` となる値

| やりたいこと | op | e |
| --- | --- | --- |
| 区間和 | `a + b` | `0` |
| 区間最小 | `minOf(a, b)` | `Long.MAX_VALUE` (十分大きい値) |
| 区間最大 | `maxOf(a, b)` | `Long.MIN_VALUE` (十分小さい値) |
| 区間 xor | `a xor b` | `0` |
| 区間 gcd | `gcd(a, b)` | `0` |

| 操作 | 内容 | 計算量 |
| --- | --- | --- |
| `SegTree(n, op, e)` | 長さ n、全要素 e で作る | O(n) |
| `SegTree(list, op, e)` | list の内容で作る | O(n) |
| `set(p, x)` | a[p] = x | O(log n) |
| `get(p)` | a[p] を返す | O(1) |
| `prod(l, r)` | op(a[l], ..., a[r-1]) を返す (**r は含まない**)。l == r なら e | O(log n) |
| `allProd()` | op(a[0], ..., a[n-1]) を返す | O(1) |
| `maxRight(l, f)` | f(op(a[l..r-1])) が true のままでいられる **最大の r** を返す | O(log n) |
| `minLeft(r, f)` | f(op(a[l..r-1])) が true のままでいられる **最小の l** を返す | O(log n) |

`maxRight` / `minLeft` の f は「区間を広げていくと、あるところまで true でそこから先はずっと false」
になる関数でなければいけません。また `f(e)` は true である必要があります。

## いつ使う？

- 「a[p] を x に変更」と「区間 [l, r) の最小値 / 最大値 / 和」が **混ざって** たくさん来る
- 区間の値を何度も聞かれるが、配列が途中で書き換わるので累積和が使えない
- 「位置 l から右に見て、初めて x 以上になる場所は？」のような **区間に対する二分探索** → `maxRight`
- DP の遷移で「dp[j] (j が範囲内) の最大値」を高速に取りたい (インライン DP)
- 和だけなら Fenwick Tree (BIT) のほうが軽い。区間に一括で足す・一括で変更するなら 遅延セグメント木

## しくみ

配列の要素を葉に置いた **完全二分木** を作り、各ノードに「その下にある区間をまとめた値」を持たせます。
葉の個数は N 以上の 2 のべき乗 (size) にそろえ、余った葉は e で埋めます。

```
区間最小の例: a = [5, 3, 7, 9, 6, 4, 1, 2]

                     1:[0,8) = 1
            /                         \
      2:[0,4) = 3                 3:[4,8) = 1
      /        \                  /         \
  4:[0,2)=3  5:[2,4)=7       6:[4,6)=4   7:[6,8)=1
   /   \      /   \           /   \       /   \
  5     3    7     9         6     4     1     2      ← 葉 (番号 8〜15)

ノード k の子は 2k と 2k+1、親は k/2。
```

- **set(p, x)**: 葉 (size + p) を書き換え、親へ向かって `op(左の子, 右の子)` で計算し直す。高さ分 = O(log N)。
- **prod(l, r)**: 区間 [l, r) を、木のノードがちょうど覆う区間の組み合わせに分けて、それらを op でまとめる。
  どんな区間でも O(log N) 個のノードで表せます。

```
prod(1, 7) = 区間 [1,7) は 次の 4 ノードで覆える
  葉[1] (3) , ノード5 [2,4) (7) , ノード6 [4,6) (4) , 葉[6] (1)
  → min(3, 7, 4, 1) = 1
```

- **maxRight(l, f)**: l から右へ、大きなノードごとに「くっつけても f が true か？」を確かめながら進み、
  false になるノードに当たったら、そのノードの中を子へ降りて境目を探します。O(log N)。

op が結合法則を満たしていれば、まとめる順番を変えても答えが同じなので、この分け方で正しい答えになります。
(交換法則は不要。左右の順番は守って計算しています。)

## 実装 (Kotlin)

```kotlin
// op: 結合法則を満たす二項演算, e: 単位元 (ACL の e() にあたる値)
class SegTree<S>(v: List<S>, private val op: (S, S) -> S, private val e: S) {
    constructor(n: Int, op: (S, S) -> S, e: S) : this(List(n) { e }, op, e)

    private val n = v.size
    private var size = 1
    private var log = 0
    init {
        while (size < n) { size *= 2; log++ }
    }
    // d[1] が根、d[size + i] が a[i] の葉
    private val d = MutableList(2 * size) { e }
    init {
        for (i in 0 until n) d[size + i] = v[i]
        for (i in size - 1 downTo 1) update(i)
    }

    private fun update(k: Int) { d[k] = op(d[2 * k], d[2 * k + 1]) }

    // a[p] = x
    fun set(p: Int, x: S) {
        require(p in 0 until n)
        val k = p + size
        d[k] = x
        for (i in 1..log) update(k shr i)
    }

    fun get(p: Int): S {
        require(p in 0 until n)
        return d[p + size]
    }

    // op(a[l], ..., a[r - 1])。l == r なら e
    fun prod(l: Int, r: Int): S {
        require(0 <= l && l <= r && r <= n)
        var sml = e
        var smr = e
        var lo = l + size
        var hi = r + size
        while (lo < hi) {
            if (lo and 1 == 1) sml = op(sml, d[lo++])
            if (hi and 1 == 1) smr = op(d[--hi], smr)
            lo = lo shr 1
            hi = hi shr 1
        }
        return op(sml, smr)
    }

    fun allProd(): S = d[1]

    // f(op(a[l], ..., a[r - 1])) が true となる最大の r を返す (f(e) は true であること)
    fun maxRight(l: Int, f: (S) -> Boolean): Int {
        require(l in 0..n)
        require(f(e))
        if (l == n) return n
        var k = l + size
        var sm = e
        do {
            while (k % 2 == 0) k = k shr 1
            if (!f(op(sm, d[k]))) {
                // このノードの中に境目がある → 子へ降りる
                while (k < size) {
                    k *= 2
                    if (f(op(sm, d[k]))) {
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

    // f(op(a[l], ..., a[r - 1])) が true となる最小の l を返す (f(e) は true であること)
    fun minLeft(r: Int, f: (S) -> Boolean): Int {
        require(r in 0..n)
        require(f(e))
        if (r == 0) return 0
        var k = r + size
        var sm = e
        do {
            k--
            while (k > 1 && k % 2 == 1) k = k shr 1
            if (!f(op(d[k], sm))) {
                while (k < size) {
                    k = 2 * k + 1
                    if (f(op(d[k], sm))) {
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

### 例 1: 区間最小と区間和

入力: N Q、数列 a、続いて Q 個のクエリ。`1 p x` なら a[p] = x、`2 l r` なら区間 [l, r) の
最小値と和を空白区切りで出力します (0-indexed)。
同じ実装に **op と e を変えて渡すだけ** で、最小用と和用の 2 本を作れます。

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

    // 区間最小: op = min, e = 十分大きい値
    val minSeg = SegTree(a, { x, y -> minOf(x, y) }, Long.MAX_VALUE)
    // 区間和: op = +, e = 0
    val sumSeg = SegTree(a, { x, y -> x + y }, 0L)

    val sb = StringBuilder()
    repeat(q) {
        val t = next().toInt()
        if (t == 1) {
            val p = next().toInt()
            val x = next().toLong()
            minSeg.set(p, x)
            sumSeg.set(p, x)
        } else {
            val l = next().toInt()
            val r = next().toInt()
            sb.append(minSeg.prod(l, r)).append(' ').append(sumSeg.prod(l, r)).append('\n')
        }
    }
    print(sb)
}
```

入力例

```
5 4
5 3 7 9 6
2 0 5
2 2 4
1 2 1
2 1 4
```

出力例

```
3 30
7 16
1 13
```

### 例 2: maxRight で二分探索

[practice2 J - Segment Tree](https://atcoder.jp/contests/practice2/tasks/practice2_j) を解くコードです (入力は 1-indexed)。

- `1 X V`: A[X] = V
- `2 L R`: A[L..R] の最大値
- `3 X V`: X ≤ j かつ A[j] ≥ V となる最小の j (なければ N+1)

タイプ 3 は「区間最大が V 未満のままでいられる最大の r」を `maxRight` で求めると、
r (0-indexed) が初めて V 以上になる位置になります。

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
    val a = List(n) { next().toInt() }
    val seg = SegTree(a, { x, y -> maxOf(x, y) }, -1)   // A[i] >= 0 なので e = -1 で十分

    val sb = StringBuilder()
    repeat(q) {
        val t = next().toInt()
        when (t) {
            1 -> {
                val x = next().toInt() - 1
                val v = next().toInt()
                seg.set(x, v)
            }
            2 -> {
                val l = next().toInt() - 1
                val r = next().toInt()          // [l, r) にするので r はそのまま
                sb.appendLine(seg.prod(l, r))
            }
            else -> {
                val x = next().toInt() - 1
                val v = next().toInt()
                val r = seg.maxRight(x) { it < v }  // 最大値が v 未満でいられる最大の r
                sb.appendLine(r + 1)                // 0-indexed の r → 1-indexed (r == n なら n+1)
            }
        }
    }
    print(sb)
}
```

入力例

```
5 5
1 2 3 2 1
2 1 5
3 2 3
1 3 1
2 2 4
3 1 3
```

出力例

```
3
3
2
6
```

## 例題

- [practice2 J - Segment Tree](https://atcoder.jp/contests/practice2/tasks/practice2_j) — 1 点更新・区間最大・maxRight
- [ABC 185 F - Range Xor Query](https://atcoder.jp/contests/abc185/tasks/abc185_f) — op = xor, e = 0 にするだけ
- [ABC 157 E - Simple String Queries](https://atcoder.jp/contests/abc157/tasks/abc157_e) — 各位置の文字を「26 ビットのビットマスク」にして op = or。区間の文字の種類数は popcount

## 注意点・よくあるミス

- `prod(l, r)` は **r を含まない** [l, r)。1-indexed で「L から R まで」なら `prod(L - 1, R)`。
- **e を間違えない**。区間最小で e = 0 にすると、答えが全部 0 以下になってしまいます。
  最小なら「どの値よりも大きい値」、最大なら「どの値よりも小さい値」。
- 値が Int に収まらないなら S を **Long** にする (和は特にあふれやすい)。`e` も `0L` のように Long で書く。
- op は結合法則を満たす必要があります。「平均」などはそのままでは使えないので、(和, 個数) のペアにするなどの工夫を。
- `maxRight` の f は **f(e) が true**、かつ「区間を伸ばすと true → false の一方向にしか変わらない」こと。
  例 2 の `{ it < v }` は、e = -1 < v なので OK です。
- この実装はジェネリクスなので、数値が箱 (オブジェクト) に入って少し遅くなります。
  N, Q が 2×10^5 程度なら問題ありませんが、ギリギリなら LongArray を使う専用版を書くと速くなります。
- S にミュータブルなオブジェクト (配列など) を使う場合、op の中で引数を書き換えず、新しいオブジェクトを返すこと。
