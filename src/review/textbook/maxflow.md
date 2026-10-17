---
title: 最大流 (Dinic)
category: グラフ
acl: maxflow
genres: グラフ, フロー
order: 310
summary: 容量つきの辺で s から t へ最大いくら流せるかを求める
level: 青
keywords: フロー, 最大流, Dinic, 二部マッチング, 最小カット, max flow, 燃やす埋める
---

## 何ができる？

各辺に「容量 (一度に流せる量の上限)」がついた有向グラフで、始点 `s` から終点 `t` へ **最大でどれだけ水を流せるか** を求めます。
ついでに「最小カット」(s と t を切り離すために切る辺の容量の合計の最小値) も分かります。

| 操作 | 内容 | 計算量 |
|---|---|---|
| `MfGraph(n)` | 頂点 `n` 個・辺なしのグラフを作る | O(n) |
| `addEdge(from, to, cap)` | `from → to` に容量 `cap` の辺を追加。辺の番号 (0, 1, 2, ...) を返す | O(1) |
| `flow(s, t)` | `s → t` に流せるだけ流し、流した量を返す | O(n²m)、実際はかなり速い |
| `flow(s, t, flowLimit)` | 流す量の上限つき | 同上 |
| `minCut(s)` | `flow` の後に呼ぶ。最小カットの `s` 側の頂点なら `true` の配列 | O(n + m) |
| `getEdge(i)` | `i` 番目の辺の `(from, to, cap, flow)` | O(1) |
| `edges()` | 全辺の `getEdge` のリスト | O(m) |
| `changeEdge(i, newCap, newFlow)` | 辺 `i` の容量と流量を書き換える | O(1) |

- 二部マッチング (容量がすべて 1) の場合は O(m√n) で動きます。
- **最大流の値 = 最小カットの値** (最大流最小カット定理)。

## いつ使う？

- 「A のグループと B のグループを **ペアにする**。最大何組作れる？」 → 二部マッチング
- グリッドに **ドミノ (1×2) を最大何枚置けるか** → マスを市松模様で 2 色に分けて二部マッチング
- 「いくつか **切って (取り除いて)** s から t へ行けなくしたい。最小コストは？」 → 最小カット
- 「それぞれ容量・回数の上限がある」「割り当て」「輸送」といった言葉が出てくる
- 制約が小さめ (頂点数が数百〜数千、多くても 10⁴ 程度) で、貪欲や DP ではうまくいかない
- 「選ぶと得、選ばないと損、ただし組み合わせで罰金」のような問題 (いわゆる「燃やす埋める」) も最小カットで解けることがある

## しくみ

### 残余グラフと逆辺

辺 `u → v` に `f` だけ流したら、「残り容量 `cap - f`」の辺と、「取り消し用の逆辺 `v → u` (容量 `f`)」があると考えます。
これを **残余グラフ** といいます。逆辺に流す = さっき流した分を押し戻す、という意味です。

```
はじめ:  s --3--> a --2--> t        a→t に 2 流した後:
                                     s --1--> a --0--> t
                                     s <--2-- a <--2-- t   (逆辺)
```

逆辺があるおかげで、「最初に変な流し方をしても後から修正できる」ので、
**残余グラフで s → t のパスが見つからなくなるまで流し続ければ最大流** になります。

### Dinic 法

パスを 1 本ずつ適当に探すと遅くなることがあるので、Dinic 法では次をくり返します。

1. 残余グラフで `s` から BFS して、各頂点の距離 `level` を求める
2. `level` が **ちょうど 1 ずつ増える辺だけ** を使って、`s → t` のパスを DFS で探して流す。
   流せなくなるまでくり返す (一度行き止まりと分かった辺は `iter` で飛ばす)
3. `t` に届かなくなったら終了

最短路の長さが毎回増えていくので、くり返しは高々 n 回で終わります。

### 最小カット

最大流を流し終わった後、残余グラフで `s` から行ける頂点の集合を S とします。
S から外へ出る元の辺はすべて満杯で、その容量の合計が最大流と同じになります。これが最小カットです (`minCut(s)`)。

### 二部マッチングへの応用

左側の頂点 L と右側の頂点 R を、辺で結べるペアを最大何組作れるか (同じ頂点は 1 回だけ) という問題です。

```
   (辺の容量はすべて 1)

      +--> L1 --+--> R1 --+
      |         |         |
      |         +--> R2 --+
  s --+                   +--> t
      |                   |
      +--> L2 -----> R3 --+
```

- `s → (左の各頂点)` に容量 1
- `(左) → (右)` に、ペアにできるなら容量 1
- `(右の各頂点) → t` に容量 1

こうすると「各頂点を 1 回しか使えない」が容量 1 で表され、**最大流 = 最大マッチング数** になります。
どのペアを使ったかは、`getEdge(i).flow == 1L` の「左 → 右」の辺を見れば分かります。

## 実装 (Kotlin)

```kotlin
// 最大流 (Dinic 法)。ACL の mf_graph に対応
class MfGraph(private val n: Int) {
    // 内部用の辺: 行き先, 逆辺の番号, 残り容量
    private class E(val to: Int, val rev: Int, var cap: Long)

    // getEdge で返す辺の情報
    data class Edge(val from: Int, val to: Int, val cap: Long, val flow: Long)

    private val g = Array(n) { ArrayList<E>() }
    private val pos = ArrayList<IntArray>() // i 番目の辺 = g[pos[i][0]][pos[i][1]]

    // from → to に容量 cap の辺を追加し、辺の番号 (追加順 0,1,2,...) を返す
    fun addEdge(from: Int, to: Int, cap: Long): Int {
        require(from in 0 until n && to in 0 until n && cap >= 0)
        val m = pos.size
        pos.add(intArrayOf(from, g[from].size))
        val fromId = g[from].size
        var toId = g[to].size
        if (from == to) toId++ // 自己ループ対策
        g[from].add(E(to, toId, cap))
        g[to].add(E(from, fromId, 0L))
        return m
    }

    fun getEdge(i: Int): Edge {
        val e = g[pos[i][0]][pos[i][1]]
        val re = g[e.to][e.rev]
        return Edge(pos[i][0], e.to, e.cap + re.cap, re.cap)
    }

    fun edges(): List<Edge> = List(pos.size) { getEdge(it) }

    // 辺 i の容量と流量を変更する
    fun changeEdge(i: Int, newCap: Long, newFlow: Long) {
        require(newFlow in 0..newCap)
        val e = g[pos[i][0]][pos[i][1]]
        val re = g[e.to][e.rev]
        e.cap = newCap - newFlow
        re.cap = newFlow
    }

    private val level = IntArray(n)
    private val iter = IntArray(n)

    // s からの BFS で level (距離) を求める
    private fun bfs(s: Int, t: Int) {
        level.fill(-1)
        level[s] = 0
        val que = IntArray(n)
        var head = 0
        var tail = 0
        que[tail++] = s
        while (head < tail) {
            val v = que[head++]
            for (e in g[v]) {
                if (e.cap == 0L || level[e.to] >= 0) continue
                level[e.to] = level[v] + 1
                if (e.to == t) return
                que[tail++] = e.to
            }
        }
    }

    // level が 1 ずつ増える辺だけを通って s → t のパスを 1 本探し、流す (非再帰)
    private val pathV = IntArray(n)
    private val pathE = IntArray(n)
    private fun augment(s: Int, t: Int, up: Long): Long {
        var depth = 0
        var v = s
        while (true) {
            if (v == t) {
                var f = up
                for (k in 0 until depth) f = minOf(f, g[pathV[k]][pathE[k]].cap)
                for (k in 0 until depth) {
                    val e = g[pathV[k]][pathE[k]]
                    e.cap -= f
                    g[e.to][e.rev].cap += f
                }
                return f
            }
            var moved = false
            while (iter[v] < g[v].size) {
                val e = g[v][iter[v]]
                if (e.cap > 0 && level[e.to] == level[v] + 1) {
                    pathV[depth] = v
                    pathE[depth] = iter[v]
                    depth++
                    v = e.to
                    moved = true
                    break
                }
                iter[v]++
            }
            if (!moved) {
                // 行き止まり: 1 つ戻って、その辺はもう使わない
                level[v] = -1
                if (depth == 0) return 0L
                depth--
                v = pathV[depth]
                iter[v]++
            }
        }
    }

    // s → t に最大 flowLimit まで流し、流せた量を返す
    fun flow(s: Int, t: Int, flowLimit: Long = Long.MAX_VALUE): Long {
        require(s in 0 until n && t in 0 until n && s != t)
        var flow = 0L
        while (flow < flowLimit) {
            bfs(s, t)
            if (level[t] == -1) break
            iter.fill(0)
            while (flow < flowLimit) {
                val f = augment(s, t, flowLimit - flow)
                if (f == 0L) break
                flow += f
            }
        }
        return flow
    }

    // flow の後に呼ぶ。残余グラフで s から行ける頂点なら true (最小カットの s 側)
    fun minCut(s: Int): BooleanArray {
        val visited = BooleanArray(n)
        val que = IntArray(n)
        var head = 0
        var tail = 0
        que[tail++] = s
        visited[s] = true
        while (head < tail) {
            val v = que[head++]
            for (e in g[v]) {
                if (e.cap > 0 && !visited[e.to]) {
                    visited[e.to] = true
                    que[tail++] = e.to
                }
            }
        }
        return visited
    }
}
```

## 使い方

AtCoder Library Practice Contest D「Maxflow」を解く例です。
マス目を市松模様に塗り分けると、ドミノは必ず「黒マス 1 つ + 白マス 1 つ」を覆うので、黒 → 白の二部マッチングになります。

```kotlin
// practice2_D: グリッドに 1×2 のタイルをできるだけ多く置く (= 二部マッチング)
fun main() {
    val br = System.`in`.bufferedReader()
    val (n, m) = br.readLine().trim().split(" ").map { it.toInt() }
    val grid = Array(n) { br.readLine().toCharArray() }

    // マス (i, j) を頂点 i*m+j に。s = n*m, t = n*m+1
    val s = n * m
    val t = n * m + 1
    val g = MfGraph(n * m + 2)
    for (i in 0 until n) for (j in 0 until m) {
        if (grid[i][j] == '#') continue
        val v = i * m + j
        if ((i + j) % 2 == 0) {
            // 黒マス (左側): s → v, 隣の白マスへ辺を張る
            g.addEdge(s, v, 1)
            if (i + 1 < n && grid[i + 1][j] == '.') g.addEdge(v, v + m, 1)
            if (i - 1 >= 0 && grid[i - 1][j] == '.') g.addEdge(v, v - m, 1)
            if (j + 1 < m && grid[i][j + 1] == '.') g.addEdge(v, v + 1, 1)
            if (j - 1 >= 0 && grid[i][j - 1] == '.') g.addEdge(v, v - 1, 1)
        } else {
            // 白マス (右側): v → t
            g.addEdge(v, t, 1)
        }
    }
    val ans = g.flow(s, t)

    // 流量 1 の「マス → マス」の辺 = 置いたタイル
    for (e in g.edges()) {
        if (e.from == s || e.to == t || e.flow == 0L) continue
        val (i1, j1) = e.from / m to e.from % m
        val (i2, j2) = e.to / m to e.to % m
        when {
            i1 + 1 == i2 -> { grid[i1][j1] = 'v'; grid[i2][j2] = '^' }
            i1 - 1 == i2 -> { grid[i1][j1] = '^'; grid[i2][j2] = 'v' }
            j1 + 1 == j2 -> { grid[i1][j1] = '>'; grid[i2][j2] = '<' }
            else -> { grid[i1][j1] = '<'; grid[i2][j2] = '>' }
        }
    }
    val sb = StringBuilder()
    sb.appendLine(ans)
    for (row in grid) sb.appendLine(String(row))
    print(sb)
}
```

入力
```
3 3
#..
..#
...
```
出力 (置き方は一例)
```
3
#><
vv#
^^.
```

## 例題

- [AtCoder Library Practice Contest D - Maxflow](https://atcoder.jp/contests/practice2/tasks/practice2_d) (ドミノ敷き詰め = 二部マッチング)
- [ABC 205 F - Grid and Tokens](https://atcoder.jp/contests/abc205/tasks/abc205_f) (行・コマ・列の 3 段に分けて最大流)
- [ABC 239 G - Builder Takahashi](https://atcoder.jp/contests/abc239/tasks/abc239_g) (頂点を 2 つに分けて最小カット + `minCut` で復元)

## 注意点・よくあるミス

- **容量や答えは `Long`** にしておく (この実装は `Long`)。容量の合計が `Int` を超えることがよくあります。
- **無限大の容量** は `Long.MAX_VALUE` ではなく `1L shl 50` などにする。足し算でオーバーフローしないように。
- `minCut(s)` は **`flow(s, t)` を呼んだ後** に使う。
- `flow` を呼ぶとグラフの残り容量が変わる。同じグラフで別の `s, t` を試したいときは作り直す。
- **頂点を切る問題** (頂点に容量がある) は、頂点 v を `v_in → v_out` (容量 = 頂点のコスト) に分けて辺の問題に直す。
- 無向辺は `addEdge(u, v, c)` と `addEdge(v, u, c)` の 2 本で表す。
- 頂点番号の割り当て (`s = n`, `t = n + 1` など) を間違えやすい。`MfGraph` のサイズは全頂点数 (s, t を含む) にする。
- 再帰 DFS はスタックオーバーフローしやすいので、この実装の DFS は **非再帰** にしてあります。
  自分で再帰版を書く場合は `Thread(null, { solve() }, "", 1L shl 27).start()` のようにスタックを大きくしたスレッドで実行してください。
