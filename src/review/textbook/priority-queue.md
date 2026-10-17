---
title: 優先度付きキュー
category: データ構造
acl: -
genres: 優先度付きキュー
order: 102
summary: 最小値 (最大値) の取り出しと追加を O(log N) で行う
level: 茶
keywords: priority queue, PriorityQueue, ヒープ, heap, 最大ヒープ, 最小値を取り出す, K 番目に大きい, 二分ヒープ
---

## 何ができる？

`java.util.PriorityQueue` を使います。入れた値の中から **一番小さいもの** をすぐに取り出せます
(比較の仕方を変えれば一番大きいもの)。

| 操作 | 内容 | 計算量 |
| --- | --- | --- |
| `PriorityQueue<Long>()` | 空の最小ヒープを作る | O(1) |
| `PriorityQueue<Long>(compareByDescending { it })` | 空の最大ヒープを作る | O(1) |
| `add(x)` | x を追加 | O(log N) |
| `peek()` | 最小値を見る (取り出さない)。空なら null | O(1) |
| `poll()` | 最小値を取り出す。空なら null | O(log N) |
| `size` / `isEmpty()` | 個数 / 空か | O(1) |
| `remove(x)` | x を 1 個消す | **O(N)** (遅い) |

このページでは「K 番目に大きい値」を管理するクラス `KthLargest` も用意しています。

| 操作 | 内容 | 計算量 |
| --- | --- | --- |
| `KthLargest(k)` | 空で作る | O(1) |
| `add(x)` | x を追加 | O(log K) |
| `get()` | 今までに追加された値の中で K 番目に大きい値 (K 個未満なら null) | O(1) |

## いつ使う？

- 「**一番小さい (大きい) ものを取り出す → 何か足す**」を何度もくり返す
  (ソートは 1 回きりなので、途中で値が増える・変わるならこちら)
- ダイクストラ法 (距離が一番小さい頂点を取り出す)
- 「K 番目に大きい値」「上位 K 個の和」を、値が追加されるたびに答える
- 「今いちばん値段が高い品物に割引券を使う」のような貪欲法
- 締め切り・開始時刻つきの仕事のスケジューリング (時刻順に見て、使えるものの中で最良を選ぶ)

## しくみ

**二分ヒープ** という「親は子より小さい (以下)」というルールを守った二分木を、配列で持っています。
根 (配列の先頭) が常に最小値です。

```
        1            配列: [1, 2, 4, 5, 3]
      /   \          i 番目の子は 2i+1 と 2i+2
     2     4
    / \
   5   3
```

- **add**: 末尾に置いてから、親より小さい間は親と入れ替えて上に登る (高さ分 = O(log N))
- **poll**: 根を取り出し、末尾の要素を根に移して、子より大きい間は小さい方の子と入れ替えて下りる (O(log N))

ルールは「親子の大小」だけなので、配列全体はソートされていません。

**K 番目に大きい値**: 大きい方から K 個だけを **最小ヒープ** に入れておきます。
すると、その中の最小値 (先頭) がちょうど K 番目に大きい値です。
新しい値が先頭より大きければ、先頭を捨てて新しい値を入れます。

## 実装 (Kotlin)

```kotlin
import java.util.PriorityQueue

// 追加された値のうち「大きい方から K 番目」をいつでも答える (add は O(log K), get は O(1))
class KthLargest(private val k: Int) {
    // 大きい方の K 個だけを最小ヒープで持つ。先頭 (最小) がちょうど K 番目に大きい値
    private val pq = PriorityQueue<Long>()

    fun add(x: Long) {
        if (pq.size < k) {
            pq.add(x)
        } else if (pq.peek() < x) {
            pq.poll() // K+1 番目以下に落ちた値は二度と答えにならないので捨てる
            pq.add(x)
        }
    }

    // まだ K 個たまっていなければ null
    fun get(): Long? = if (pq.size < k) null else pq.peek()
}
```

## 使い方

ヒープの作り方 (最小・最大・Pair・配列) の例と、[ABC 234 D - Prefix K-th Max](https://atcoder.jp/contests/abc234/tasks/abc234_d) と同じ形の問題
「先頭 i 項の中で K 番目に大きい値を、i = K, …, N について出力」を解く例です。
`import java.util.PriorityQueue` は実装側の先頭に書いてあります。

```kotlin
fun main() {
    // ---- 1. 最小ヒープと最大ヒープ ----
    val minPq = PriorityQueue<Long>()                          // 小さい順に出てくる
    val maxPq = PriorityQueue<Long>(compareByDescending { it }) // 大きい順に出てくる
    for (x in longArrayOf(5, 1, 4, 2)) {
        minPq.add(x)
        maxPq.add(x)
    }
    println(minPq.peek()) // 1  (見るだけ)
    println(minPq.poll()) // 1  (取り出す)
    println(maxPq.poll()) // 5

    // ---- 2. Pair を入れる: (距離, 頂点) を距離の小さい順、同じなら頂点の小さい順 ----
    val pairPq = PriorityQueue<Pair<Long, Int>>(compareBy({ it.first }, { it.second }))
    pairPq.add(10L to 2)
    pairPq.add(3L to 7)
    pairPq.add(3L to 1)
    println(pairPq.poll()) // (3, 1)

    // ---- 3. 配列を入れる: [値, 番号] を値の大きい順 ----
    val arrPq = PriorityQueue<LongArray>(compareByDescending { it[0] })
    arrPq.add(longArrayOf(7, 0))
    arrPq.add(longArrayOf(9, 1))
    val top = arrPq.poll()
    println("${top[0]} ${top[1]}") // 9 1

    // ---- 4. 先頭 i 項の中で K 番目に大きい値 (i = K, K+1, ..., N) ----
    val (n, k) = readln().trim().split(" ").map { it.toInt() }
    val p = readln().trim().split(" ").map { it.toLong() }
    val kth = KthLargest(k)
    val sb = StringBuilder()
    for (i in 0 until n) {
        kth.add(p[i])
        if (i >= k - 1) sb.appendLine(kth.get())
    }
    print(sb)
}
```

入力例

```
5 2
3 1 4 1 5
```

出力例

```
1
1
5
(3, 1)
9 1
1
3
3
4
```

## 例題

- [ABC 141 D - Powerful Discount Tickets](https://atcoder.jp/contests/abc141/tasks/abc141_d) — 最大ヒープから一番高い品物を取り出し、半額にして戻すのを M 回
- [ABC 234 D - Prefix K-th Max](https://atcoder.jp/contests/abc234/tasks/abc234_d) — 上の `KthLargest` そのもの
- [ABC 212 D - Querying Multiset](https://atcoder.jp/contests/abc212/tasks/abc212_d) — 「全部に x を足す」は全体に足した合計を別に覚えておき、入れるときに引いておく

## 注意点・よくあるミス

- `PriorityQueue()` は **最小値** が出てきます。最大値が欲しいときは `compareByDescending { it }` を渡す
  (または値に -1 を掛けて入れる)。
- 比較を `Comparator { a, b -> a - b }` のように引き算で書くと、オーバーフローで順番が壊れることがあります。
  `compareBy` / `compareByDescending` / `compareValues` を使う。
- 空のときの `peek()` / `poll()` は null を返します。`val x: Long = pq.poll()` のように受けると NullPointerException。
- `for (x in pq)` や `println(pq)` の順番は **ソート順ではありません**。順番に欲しければ `poll()` をくり返す。
- `remove(x)` (任意の値の削除) は O(N) です。削除が多いときは
  「削除予定を別のヒープや HashMap に覚えておき、先頭に来たときに捨てる (遅延削除)」か、
  [順序付き集合](#textbook/treeset-treemap) の多重集合を使う。
- `Pair` を入れるとき、比較を渡さないと実行時に ClassCastException になります (Pair は Comparable ではない)。
- 中に入れた配列を外から書き換えると順番が壊れます。値を変えたいときは新しく入れ直す。
