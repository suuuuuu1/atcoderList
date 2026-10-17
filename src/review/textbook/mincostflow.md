---
title: 最小費用流
category: グラフ
acl: mincostflow
genres: グラフ, フロー
order: 320
summary: 決まった量を流すときの合計コストの最小値を求める
level: 青
keywords: 最小費用流, min cost flow, 割り当て, 費用最小, 重み付きマッチング
---

## 何ができる？

各辺に「容量」と「1 単位流すごとのコスト」がついた有向グラフで、
`s` から `t` へ **F だけ流すときの合計コストの最小値** を求めます。
最大流に「コスト」が加わったもの、と考えると分かりやすいです。

| 操作 | 内容 | 計算量 |
|---|---|---|
| `McfGraph(n)` | 頂点 `n` 個・辺なしのグラフを作る | O(n) |
| `addEdge(from, to, cap, cost)` | `from → to` に容量 `cap`・コスト `cost` (0 以上) の辺を追加。辺の番号を返す | O(1) |
| `flow(s, t)` | 流せるだけ流し、`(流量, 合計コスト)` を返す | O(F (n + m) log(n + m)) |
| `flow(s, t, flowLimit)` | 流量 `flowLimit` まで流す | 同上 |
| `slope(s, t)` | 「流量 → 最小コスト」の折れ線の頂点のリスト | 同上 |
| `getEdge(i)` | `i` 番目の辺の `(from, to, cap, flow, cost)` | O(1) |
| `edges()` | 全辺の `getEdge` のリスト | O(m) |

F は流す量の合計です。1 回の Dijkstra で少なくとも 1 流れるので、F が大きすぎない (〜10⁴ 程度) ときに使います。

### slope とは？

流量 x のときの最小コストを g(x) とすると、g は **下に凸な折れ線** になります。
`slope` はその折れ線の頂点 (傾きが変わる点) を `(流量, コスト)` で返します。

```
コスト
 14 |                 *        slope = [(0,0), (2,2), (4,8), (5,14)]
    |                /           流量 0〜2 : 1 あたりコスト 1
  8 |              *             流量 2〜4 : 1 あたりコスト 3
    |           /                流量 4〜5 : 1 あたりコスト 6
  2 |      *
  0 *
    +------+-------+--+---- 流量
    0      2       4  5
```

- 最初の要素は必ず `(0, 0)`、最後の要素は `flow(s, t, flowLimit)` の結果と同じです。
- 「ちょうど x 流したときのコスト」は折れ線の直線補間で求まります。

## いつ使う？

- 最大流の問題に「**コスト・報酬**」がついていて、その合計を最小化/最大化したい
- 「N 人に N 個の仕事を割り当てて、コストの合計を最小に」(割り当て問題・重み付き二部マッチング)
- 「各行・各列から K 個以下選んで、合計を最大に」
- 「K 個選ぶとき、K = 1, 2, ..., N それぞれの最適値を出力」→ `slope` が便利
- 制約が小さめ (頂点・辺が数百〜数千)

## しくみ

### 最短路に沿って流す

「**残余グラフで、コストが最小の s → t パスに流す**」をくり返すと、各時点で最小費用流になっています。
(最大流と同じく逆辺を使います。逆辺のコストは `-cost` で、流した分を取り消すとコストも戻ります。)

```
   1. 最短路 (コストの和が最小のパス) を探す
   2. そのパスに、パス上の最小の残り容量だけ流す
   3. t に行けなくなるか、flowLimit に達したら終了
```

### ポテンシャルで Dijkstra を使う

逆辺のコストは負なので、そのままでは Dijkstra が使えません。
そこで各頂点に「ポテンシャル」`h[v]` (前回までの最短距離) を持たせ、
辺のコストを `cost + h[from] - h[to]` に置き換えると **すべて 0 以上** になり、Dijkstra が使えます。
(パス全体のコストは `h[t] - h[s]` だけずれるだけなので、最短路は変わりません。)

この方法を primal-dual 法といい、ACL もこれを使っています。

### 最大化したいときは

報酬 `a` を最大化したいときは、コストを `-a` にしたいところですが、負のコストは使えません。
そこで **大きな数 `BIG` を使って `BIG - a` をコストにする** のが定番です。
**どの s → t パスも `BIG` つきの辺をちょうど 1 本通る** ようにグラフを作ると
(「何も選ばない」用に コスト `BIG` の辺 `s → t` を足すなど)、
`答え = 流量 × BIG - コスト` で元に戻せます。下の使い方の例を参照してください。

## 実装 (Kotlin)

```kotlin
// 最小費用流 (ポテンシャル付き Dijkstra による primal-dual)。ACL の mcf_graph に対応
// コストは 0 以上であること
class McfGraph(private val n: Int) {
    private class E(val to: Int, val rev: Int, var cap: Long, val cost: Long)

    data class Edge(val from: Int, val to: Int, val cap: Long, val flow: Long, val cost: Long)

    private val g = Array(n) { ArrayList<E>() }
    private val pos = ArrayList<IntArray>()

    // from → to に容量 cap, 1 単位あたりコスト cost の辺を追加し、辺の番号を返す
    fun addEdge(from: Int, to: Int, cap: Long, cost: Long): Int {
        require(from in 0 until n && to in 0 until n && cap >= 0 && cost >= 0)
        val m = pos.size
        pos.add(intArrayOf(from, g[from].size))
        val fromId = g[from].size
        var toId = g[to].size
        if (from == to) toId++
        g[from].add(E(to, toId, cap, cost))
        g[to].add(E(from, fromId, 0L, -cost))
        return m
    }

    fun getEdge(i: Int): Edge {
        val e = g[pos[i][0]][pos[i][1]]
        val re = g[e.to][e.rev]
        return Edge(pos[i][0], e.to, e.cap + re.cap, re.cap, e.cost)
    }

    fun edges(): List<Edge> = List(pos.size) { getEdge(it) }

    // 流量 flowLimit まで流し、(流量, コスト) を返す
    fun flow(s: Int, t: Int, flowLimit: Long = Long.MAX_VALUE): Pair<Long, Long> =
        slope(s, t, flowLimit).last()

    // 「流量 → 最小コスト」の折れ線の頂点を返す。最初は (0, 0)、最後が最大流量
    fun slope(s: Int, t: Int, flowLimit: Long = Long.MAX_VALUE): List<Pair<Long, Long>> {
        require(s in 0 until n && t in 0 until n && s != t)
        val INF = Long.MAX_VALUE
        val h = LongArray(n)          // ポテンシャル (辺のコストを 0 以上に保つ)
        val dist = LongArray(n)
        val prevV = IntArray(n)
        val prevE = IntArray(n)
        var flow = 0L
        var cost = 0L
        var prevCostPerFlow = -1L
        val result = ArrayList<Pair<Long, Long>>()
        result.add(0L to 0L)
        while (flow < flowLimit) {
            // Dijkstra (コスト cost + h[v] - h[to] は 0 以上)
            dist.fill(INF)
            dist[s] = 0
            val pq = java.util.PriorityQueue<LongArray>(compareBy { it[0] })
            pq.add(longArrayOf(0L, s.toLong()))
            while (pq.isNotEmpty()) {
                val (d, vl) = pq.poll()
                val v = vl.toInt()
                if (dist[v] < d) continue
                for ((i, e) in g[v].withIndex()) {
                    if (e.cap == 0L) continue
                    val nd = d + e.cost + h[v] - h[e.to]
                    if (nd < dist[e.to]) {
                        dist[e.to] = nd
                        prevV[e.to] = v
                        prevE[e.to] = i
                        pq.add(longArrayOf(nd, e.to.toLong()))
                    }
                }
            }
            if (dist[t] == INF) break
            for (v in 0 until n) if (dist[v] != INF) h[v] += dist[v]

            // 経路上の最小容量だけ流す
            var c = flowLimit - flow
            var v = t
            while (v != s) {
                c = minOf(c, g[prevV[v]][prevE[v]].cap)
                v = prevV[v]
            }
            v = t
            while (v != s) {
                val e = g[prevV[v]][prevE[v]]
                e.cap -= c
                g[v][e.rev].cap += c
                v = prevV[v]
            }
            val d = h[t] - h[s] // 今回の経路の 1 単位あたりコスト
            flow += c
            cost += c * d
            if (prevCostPerFlow == d) result.removeAt(result.size - 1) // 傾きが同じなら点をまとめる
            result.add(flow to cost)
            prevCostPerFlow = d
        }
        return result
    }
}
```

## 使い方

AtCoder Library Practice Contest E「MinCostFlow」を解く例です。
「行 i → 列 j」の辺 1 本がマス (i, j) を選ぶことに対応します。各行・各列の容量を K にすれば「K 個以下」が表せます。

```kotlin
// practice2_E: 各行・各列から K 個以下のマスを選び、選んだ値の和を最大化
fun main() {
    val br = System.`in`.bufferedReader()
    val (n, k) = br.readLine().trim().split(" ").map { it.toInt() }
    val a = Array(n) { br.readLine().trim().split(" ").map { it.toLong() } }

    val BIG = 1_000_000_000L // 「最大化」を「コスト最小化」に直すための大きな数
    val s = 2 * n
    val t = 2 * n + 1
    val g = McfGraph(2 * n + 2)
    // s → t: 何も選ばない分の逃げ道 (コスト BIG)
    g.addEdge(s, t, (n * k).toLong(), BIG)
    for (i in 0 until n) {
        g.addEdge(s, i, k.toLong(), 0)      // s → 行 i
        g.addEdge(n + i, t, k.toLong(), 0)  // 列 i → t
    }
    val cellEdge = Array(n) { IntArray(n) }
    for (i in 0 until n) for (j in 0 until n) {
        // 行 i → 列 j (マス (i, j) を選ぶ): コスト BIG - a[i][j] (0 以上)
        cellEdge[i][j] = g.addEdge(i, n + j, 1, BIG - a[i][j])
    }
    val (_, cost) = g.flow(s, t, (n * k).toLong())
    val ans = (n * k).toLong() * BIG - cost

    val sb = StringBuilder()
    sb.appendLine(ans)
    for (i in 0 until n) {
        for (j in 0 until n) sb.append(if (g.getEdge(cellEdge[i][j]).flow == 1L) 'X' else '.')
        sb.appendLine()
    }
    print(sb)
}
```

入力
```
3 1
5 3 2
1 4 8
7 6 9
```
出力 (選び方は一例)
```
19
X..
..X
.X.
```

`slope` の使い方の例:

```kotlin
val g = McfGraph(4)
g.addEdge(0, 1, 2, 1)
g.addEdge(1, 3, 2, 0)
g.addEdge(0, 2, 3, 3)
g.addEdge(2, 3, 3, 0)
println(g.slope(0, 3)) // [(0, 0), (2, 2), (5, 11)]
```

## 例題

- [AtCoder Library Practice Contest E - MinCostFlow](https://atcoder.jp/contests/practice2/tasks/practice2_e)
- [ABC 247 G - Dream Team](https://atcoder.jp/contests/abc247/tasks/abc247_g) (大学 → 得意分野の二部グラフ。`slope` で全ての k の答えを出す)
- [ABC 214 H - Collecting](https://atcoder.jp/contests/abc214/tasks/abc214_h) (強連結成分分解で DAG にしてから最小費用流)

## 注意点・よくあるミス

- **コストは 0 以上** でなければならない (ACL と同じ)。負のコストを使いたいときは `BIG - a` の形に直す。
- `BIG` は `(報酬の最大値)` より大きく、しかも `BIG × 流量` が `Long` に収まる値にする。
- 流量・コストはすべて `Long`。`c * d` (流量 × 1 単位のコスト) はすぐ大きくなります。
- **流量が多すぎると TLE**。Dijkstra を最大で「流量の回数」行うので、F が 10⁵ を超えるような場合は工夫が必要。
- `flow` / `slope` を呼ぶとグラフの残り容量が変わる。両方使いたいなら `slope` だけ呼び、最後の要素を使う。
- 「ちょうど K 流す」問題で `flow(s, t, K)` の流量が K 未満なら、K は流せない (不可能) ということ。
- 結果の取り出しは `getEdge(i).flow`。`addEdge` の戻り値 (辺番号) を保存しておくと便利。
- この実装は再帰を使っていないので、スタックオーバーフローの心配はありません。
