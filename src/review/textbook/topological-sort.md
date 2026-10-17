---
title: トポロジカルソート
category: グラフ
acl: -
genres: トポロジカルソート, グラフ
order: 308
summary: 有向グラフの頂点を「矢印が前から後ろ」の順に並べる
level: 緑
keywords: トポロジカルソート, topological sort, DAG, 有向非巡回グラフ, 依存関係, 順序, 入次数, Kahn, 閉路検出, 辞書順最小
---

## 何ができる？

有向グラフの頂点を、**すべての辺 u → v について u が v より前に来る** ように一列に並べます。
並べられるのはグラフに **サイクル (閉路) がないとき (DAG)** だけで、サイクルがあれば「並べられない」ことも分かります。

| 関数 | 内容 | 計算量 |
| --- | --- | --- |
| `topologicalSort(g)` | トポロジカル順の頂点列を返す。サイクルがあれば `null` | O(N + M) |
| `topologicalSortLexMin(g)` | 辞書順最小のトポロジカル順を返す。サイクルがあれば `null` | O((N + M) log N) |

## いつ使う？

- 「A は B より前」「作業 A が終わらないと B を始められない」のような **前後関係 (依存関係)** の制約がたくさんある
- 「条件を満たす順列のうち **辞書順最小** を出力」「なければ -1」
- 有向グラフに **サイクルがあるか** の判定
- DAG 上の DP (最長経路・経路の数え上げなど) をするときの **計算順** を決める

## しくみ (Kahn 法)

**入次数** (その頂点に入ってくる辺の本数) が 0 の頂点は、前に来るべき頂点がないので、先頭に置いてよい頂点です。

1. 全頂点の入次数を数え、入次数 0 の頂点をキューに入れる
2. キューから頂点 v を取り出して答えの末尾に追加し、v から出る辺 v → to を消す (= `to` の入次数を 1 減らす)
3. 減らした結果 `to` の入次数が 0 になったらキューに入れる
4. キューが空になるまで繰り返す。**全頂点を取り出せなかったらサイクルがある**

```
  0 → 1 → 3        入次数: 0:0  1:1  2:1  3:2
  ↓       ↑
  2 ------+        キュー [0] → 0 を出す → 1,2 の入次数が 0 に → [1,2]
                   → 1 を出す → 3 の入次数 1 → 2 を出す → 3 の入次数 0 → [3]
                   答え: 0 1 2 3
```

**辞書順最小** にしたいときは、キューの代わりに **優先度付きキュー** (小さい順に取り出せる) を使って、
「今置ける頂点のうち一番番号が小さいもの」を毎回選びます。

## 実装 (Kotlin)

```kotlin
// Kahn 法。トポロジカル順の頂点列を返す。サイクルがあれば null
fun topologicalSort(g: Array<out List<Int>>): IntArray? {
    val n = g.size
    val indeg = IntArray(n)
    for (v in 0 until n) for (to in g[v]) indeg[to]++
    val queue = ArrayDeque<Int>()
    for (v in 0 until n) if (indeg[v] == 0) queue.addLast(v)
    val order = IntArray(n)
    var cnt = 0
    while (queue.isNotEmpty()) {
        val v = queue.removeFirst()
        order[cnt++] = v
        for (to in g[v]) {
            indeg[to]--
            if (indeg[to] == 0) queue.addLast(to)
        }
    }
    return if (cnt == n) order else null // 全部並べられなければサイクルあり
}

// 辞書順最小のトポロジカル順。サイクルがあれば null
fun topologicalSortLexMin(g: Array<out List<Int>>): IntArray? {
    val n = g.size
    val indeg = IntArray(n)
    for (v in 0 until n) for (to in g[v]) indeg[to]++
    val pq = java.util.PriorityQueue<Int>() // 番号の小さい順に取り出す
    for (v in 0 until n) if (indeg[v] == 0) pq.add(v)
    val order = IntArray(n)
    var cnt = 0
    while (pq.isNotEmpty()) {
        val v = pq.poll()
        order[cnt++] = v
        for (to in g[v]) {
            indeg[to]--
            if (indeg[to] == 0) pq.add(to)
        }
    }
    return if (cnt == n) order else null
}
```

## 使い方

[ABC 223 D - Restricted Permutation](https://atcoder.jp/contests/abc223/tasks/abc223_d) の形の問題です。
(1, 2, ..., N) の並べ替えで、「A_i は B_i より前」という条件を M 個すべて満たすもののうち辞書順最小を出力。なければ `-1`。

```kotlin
fun main() {
    val br = System.`in`.bufferedReader()
    var st = java.util.StringTokenizer("")
    fun next(): String {
        while (!st.hasMoreTokens()) st = java.util.StringTokenizer(br.readLine())
        return st.nextToken()
    }

    val n = next().toInt()
    val m = next().toInt()
    val g = Array(n) { mutableListOf<Int>() }
    repeat(m) {
        val a = next().toInt() - 1
        val b = next().toInt() - 1
        g[a].add(b) // a は b より前 → 辺 a → b
    }

    val order = topologicalSortLexMin(g) // 辞書順でなくてよければ topologicalSort(g)
    if (order == null) {
        println(-1)
    } else {
        println(order.joinToString(" ") { (it + 1).toString() })
    }
}
```

入力例 1

```
4 3
2 1
3 4
2 4
```

出力例 1

```
2 1 3 4
```

入力例 2 (1 → 2 → 3 → 1 のサイクルがある)

```
3 3
1 2
2 3
3 1
```

出力例 2

```
-1
```

## 例題

- [ABC 223 D - Restricted Permutation](https://atcoder.jp/contests/abc223/tasks/abc223_d) — 上の「使い方」そのもの。辞書順最小なので優先度付きキュー
- [EDPC G - Longest Path](https://atcoder.jp/contests/dp/tasks/dp_g) — DAG の最長経路。トポロジカル順に `dp[to] = max(dp[to], dp[v] + 1)` と更新する
- [ABC 291 E - Find Permutation](https://atcoder.jp/contests/abc291/tasks/abc291_e) — トポロジカル順が 1 通りに決まるか。Kahn 法でキューに 2 個以上入る瞬間があれば一意でない

## 注意点・よくあるミス

- トポロジカル順は **1 通りとは限りません**。「どれか 1 つ」でよいのか「辞書順最小」なのかを問題文で確認する。
- 辞書順最小を普通のキューで求めようとすると間違えます。必ず優先度付きキュー。
  逆に、ただ並べればよいときに優先度付きキューを使うと log の分だけ遅くなります (たいていは問題ありません)。
- サイクル判定は「取り出した個数 `cnt` が n か」で行います。
- 多重辺 (同じ a → b が何回も来る) があっても、入次数も同じ回数だけ増えて同じ回数だけ減るので、そのままで大丈夫です。
- 辺の向きを逆にしない。「A は B より前」なら A → B。「B を始めるには A が必要」も A → B です。
- DFS の帰りがけ順を逆にしてもトポロジカル順になりますが、Kahn 法のほうがサイクル判定もできて再帰の心配もありません。
