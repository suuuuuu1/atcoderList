---
title: 木の直径
category: 木
acl: -
genres: 木, BFS
order: 4530
summary: 木で一番遠い 2 頂点とその距離を、BFS 2 回で求める
level: 緑
keywords: 直径, diameter, 最遠点, 一番遠い2点, 最長パス, 最長経路, 2回BFS, double sweep
---

## 何ができる？

木の中で **一番離れている 2 頂点** と、その距離 (= **直径**) を求めます。

| 操作 | 内容 | 計算量 |
| --- | --- | --- |
| `TreeDiameter(n)` | 頂点数 n の木を作る | O(n) |
| `addEdge(a, b, w)` | 重み w の辺を追加 (w は省略すると 1) | O(1) |
| `diameter()` | 直径の長さ・両端 u, v・u から v へのパス を返す | O(n) |

## いつ使う？

- 「木の中で一番遠い 2 点の距離」「最長のパス」を求めたい
- 木に辺を 1 本足して **一番長い閉路** を作りたい → 直径の両端を結ぶ (直径 + 1 頂点)
- 「全頂点をめぐる最短の道のり」→ 全辺を往復 (重みの合計 × 2) して、直径の分だけ帰らなくてよい
- 「各頂点から一番遠い頂点までの距離」→ 直径の両端 u, v のどちらかが必ず一番遠い。u と v から BFS すれば全頂点分わかる

## しくみ

1. 適当な頂点 (0) から BFS して、**一番遠い頂点 u** を見つける
2. u から BFS して、**一番遠い頂点 v** を見つける
3. u − v の距離が直径

```
        5
        |
  1 --- 2 --- 3 --- 4
              |
              6 --- 7

 1 回目: 頂点 3 から始めると、一番遠いのは 1 か 5 か 7 (距離 2)。たとえば 7
 2 回目: 7 から一番遠いのは 1 か 5 (距離 4)
 → 直径は 4 (7-6-3-2-1)
```

「木の上で、ある頂点から一番遠い頂点は、必ず直径の端のどれか」という性質があるので、2 回で十分です。

木では 2 頂点を結ぶ道が 1 本しかないので、**重み付きでも** BFS (訪問順は何でもよい) で正しい距離が出ます
(重みは 0 以上に限ります)。

## 実装 (Kotlin)

```kotlin
// 木の直径を 2 回の BFS で求める。辺の重みは 0 以上 (重みなしなら全部 1)
class TreeDiameter(private val n: Int) {
    private val to = Array(n) { mutableListOf<Int>() }
    private val cost = Array(n) { mutableListOf<Long>() }

    // length: 直径の長さ, u と v: 両端, path: u から v への頂点列
    data class Result(val length: Long, val u: Int, val v: Int, val path: List<Int>)

    fun addEdge(a: Int, b: Int, w: Long = 1L) {
        to[a].add(b); cost[a].add(w)
        to[b].add(a); cost[b].add(w)
    }

    // start からの距離と、たどってきた親 を返す (木なので BFS で重み付きでも正しい)
    private fun bfs(start: Int): Pair<LongArray, IntArray> {
        val dist = LongArray(n) { -1L }
        val par = IntArray(n) { -1 }
        val queue = ArrayDeque<Int>()
        dist[start] = 0L
        queue.addLast(start)
        while (queue.isNotEmpty()) {
            val v = queue.removeFirst()
            for (i in to[v].indices) {
                val c = to[v][i]
                if (dist[c] != -1L) continue
                dist[c] = dist[v] + cost[v][i]
                par[c] = v
                queue.addLast(c)
            }
        }
        return dist to par
    }

    fun diameter(): Result {
        // 1 回目: 適当な頂点 0 から一番遠い頂点 u を探す
        val (d0, _) = bfs(0)
        val u = d0.indices.maxByOrNull { d0[it] }!!
        // 2 回目: u から一番遠い頂点 v を探す。u-v の距離が直径
        val (du, par) = bfs(u)
        val v = du.indices.maxByOrNull { du[it] }!!
        // v から親をたどって u までのパスを復元
        val path = mutableListOf<Int>()
        var x = v
        while (x != -1) {
            path.add(x)
            x = par[x]
        }
        path.reverse()
        return Result(du[v], u, v, path)
    }
}
```

## 使い方

[典型90 003 - Longest Circular Road](https://atcoder.jp/contests/typical90/tasks/typical90_c) を解くコードです。
木に辺を 1 本足してできる閉路の、最大の長さ (頂点数) を出力します。

```kotlin
fun main() {
    val br = System.`in`.bufferedReader()
    var st = java.util.StringTokenizer("")
    fun next(): String {
        while (!st.hasMoreTokens()) st = java.util.StringTokenizer(br.readLine())
        return st.nextToken()
    }

    val n = next().toInt()
    val td = TreeDiameter(n)
    repeat(n - 1) {
        val a = next().toInt() - 1
        val b = next().toInt() - 1
        td.addEdge(a, b)          // 重みなし (重み 1)
    }
    val res = td.diameter()
    // 直径の両端を辺で結ぶと、頂点数 (直径 + 1) の閉路ができる
    println(res.length + 1)
    // println(res.path.map { it + 1 })   // 直径のパス (1-indexed) も分かる
}
```

入力例

```
5
1 2
2 3
3 4
3 5
```

出力例

```
4
```

## 例題

- [典型90 003 - Longest Circular Road](https://atcoder.jp/contests/typical90/tasks/typical90_c) — 上の「使い方」そのもの。直径 + 1
- [ABC 361 E - Tree and Hamilton Path 2](https://atcoder.jp/contests/abc361/tasks/abc361_e) — 重み付き。答えは「辺の重みの合計 × 2 − 直径」

## 注意点・よくあるミス

- 入力が 1-indexed なら `- 1` する。
- **重みに負の値があると使えません** (2 回 BFS の性質が成り立たない)。
- 重みの合計は Int を超えやすいので **Long** で持つ (上の実装は Long)。
- 直径の「長さ」は **辺の本数 (重みの合計)**。頂点数で聞かれたら + 1 します。
- 再帰 DFS で距離を求めると、パス状の木で StackOverflow することがあります。BFS (キュー) なら安全です。
- 木ではない一般のグラフでは、この方法では最長パスは求まりません。
