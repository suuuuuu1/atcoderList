---
title: 01-BFS
category: グラフ
acl: -
genres: BFS, グラフ
order: 304
summary: 辺の重みが 0 か 1 だけのグラフで最短距離を O(N+M) で求める
level: 緑
keywords: 01BFS, zero one bfs, 0-1 BFS, 両端キュー, deque, 壁を壊す, 最小コスト, 迷路
---

## 何ができる？

辺の重み (コスト) が **0 か 1 のどちらかだけ** のグラフで、スタートから各頂点への最短距離を求めます。
ダイクストラ (O((N+M) log N)) でも解けますが、01-BFS は **O(N + M)** で、書き方も BFS とほぼ同じです。

| 関数 | 内容 | 計算量 |
| --- | --- | --- |
| `zeroOneBfs(g, start)` | 重み 0/1 のグラフで start からの最短距離。届かない頂点は `-1` | O(N + M) |

## いつ使う？

- 「**壁を壊す回数の最小値**」「ワープ (コスト 0) と歩き (コスト 1)」のように、コストが 0 の移動と 1 の移動が混ざっている
- 「向きを変えるときだけコスト 1」「同じ色のマスへはタダで移動」など、**タダの移動** がある最短経路
- 重みが 0, 1 以外もあるならダイクストラ、全部 1 なら普通の BFS

## しくみ

普通の BFS はキューの中身が「距離 d の頂点 → 距離 d+1 の頂点」の順に並んでいるのがポイントでした。
01-BFS では **両端キュー (deque)** を使い、

- 重み **0** の辺で行ける頂点は、今と同じ距離なので **先頭** に入れる (`addFirst`)
- 重み **1** の辺で行ける頂点は、距離が 1 増えるので **末尾** に入れる (`addLast`)

とすることで、キューの中を常に「距離の小さい順」に保ちます。これはダイクストラの優先度付きキューを deque で代用しているのと同じです。

```
 deque の中身 (距離): [ d d d | d+1 d+1 ]
   重み 0 の行き先 → 先頭へ    重み 1 の行き先 → 末尾へ
```

BFS と違い、**一度距離が入った頂点が、あとでもっと短い距離に更新されることがあります**。
そこでダイクストラと同じく「`dist[v] + cost < dist[to]` なら更新して入れる」という書き方にし、
取り出した頂点が確定済みなら飛ばします。

## 実装 (Kotlin)

```kotlin
// 重み付きの辺: 行き先 to と重み cost (01-BFS では 0 か 1)
data class Edge(val to: Int, val cost: Long)

// 重み 0/1 のグラフで start からの最短距離。届かない頂点は -1
fun zeroOneBfs(g: Array<out List<Edge>>, start: Int): IntArray {
    val n = g.size
    val inf = Int.MAX_VALUE
    val dist = IntArray(n) { inf }
    val done = BooleanArray(n)
    val deque = ArrayDeque<Int>()
    dist[start] = 0
    deque.addLast(start)
    while (deque.isNotEmpty()) {
        val v = deque.removeFirst()
        if (done[v]) continue // もう確定している (古い情報)
        done[v] = true
        for (e in g[v]) {
            val nd = dist[v] + e.cost.toInt()
            if (nd < dist[e.to]) {
                dist[e.to] = nd
                if (e.cost == 0L) deque.addFirst(e.to) else deque.addLast(e.to)
            }
        }
    }
    for (i in 0 until n) if (dist[i] == inf) dist[i] = -1
    return dist
}
```

## 使い方

### グラフ版

入力は `N M` のあとに有向辺 `A_i B_i C_i` (C_i は 0 か 1、1-indexed) が M 行。頂点 1 から各頂点への最短距離を出力します。

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
    val g = Array(n) { mutableListOf<Edge>() }
    repeat(m) {
        val a = next().toInt() - 1
        val b = next().toInt() - 1
        val c = next().toLong()
        g[a].add(Edge(b, c))
    }
    val dist = zeroOneBfs(g, 0)
    println(dist.joinToString("\n"))
}
```

入力例

```
5 5
1 2 1
1 3 1
2 4 1
3 4 0
4 5 1
```

出力例

```
0
1
1
1
2
```

### グリッド版: 壁を壊す最小回数

グリッドでは隣接リストを作らず、その場で「行き先のマスが壁ならコスト 1、通路ならコスト 0」として 01-BFS します。
入力は `H W` と迷路 H 行 (`s` スタート、`g` ゴール、`.` 通路、`#` 壁)。壁を最小何回壊せばゴールできるか。

```kotlin
fun main() {
    val br = System.`in`.bufferedReader()
    var st = java.util.StringTokenizer("")
    fun next(): String {
        while (!st.hasMoreTokens()) st = java.util.StringTokenizer(br.readLine())
        return st.nextToken()
    }

    val h = next().toInt()
    val w = next().toInt()
    val grid = Array(h) { next() }
    var start = 0
    var goal = 0
    for (r in 0 until h) for (c in 0 until w) {
        if (grid[r][c] == 's') start = r * w + c
        if (grid[r][c] == 'g') goal = r * w + c
    }

    val dr = intArrayOf(-1, 1, 0, 0)
    val dc = intArrayOf(0, 0, -1, 1)
    val inf = Int.MAX_VALUE
    val dist = IntArray(h * w) { inf }
    val done = BooleanArray(h * w)
    val deque = ArrayDeque<Int>()
    dist[start] = 0
    deque.addLast(start)
    while (deque.isNotEmpty()) {
        val cur = deque.removeFirst()
        if (done[cur]) continue
        done[cur] = true
        val r = cur / w
        val c = cur % w
        for (k in 0 until 4) {
            val nr = r + dr[k]
            val nc = c + dc[k]
            if (nr !in 0 until h || nc !in 0 until w) continue
            val cost = if (grid[nr][nc] == '#') 1 else 0 // 壁に入る = 壊す
            val next = nr * w + nc
            if (dist[cur] + cost < dist[next]) {
                dist[next] = dist[cur] + cost
                if (cost == 0) deque.addFirst(next) else deque.addLast(next)
            }
        }
    }
    println(dist[goal])
}
```

入力例

```
4 5
s#...
.##.#
###.#
...#g
```

出力例

```
2
```

## 例題

- [ARC 005 C - 器物損壊！高橋君](https://atcoder.jp/contests/arc005/tasks/arc005_3) — 塀を壊す回数が 2 回以下でゴールできるか。上のグリッド版そのもの
- [ABC 176 D - Wizard in Maze](https://atcoder.jp/contests/abc176/tasks/abc176_d) — 歩き (コスト 0) とワープ (5×5 の範囲、コスト 1)
- [ABC 213 E - Stronger Takahashi](https://atcoder.jp/contests/abc213/tasks/abc213_e) — 通路への移動はコスト 0、パンチ (周りの 2×2 を壊す) はコスト 1

## 注意点・よくあるミス

- BFS と違って **同じ頂点が何度も deque に入ることがあります**。`done` で「確定済みなら飛ばす」を忘れないこと
  (なくても答えは合いますが、遅くなることがあります)。
- 普通の BFS のように「初めて見たときに距離を確定」にすると間違えます。重み 0 の辺であとからもっと短くなることがあるため、必ず `nd < dist[to]` で比べて更新する。
- 重み 0 → `addFirst`、重み 1 → `addLast`。逆にすると正しく動きません。
- グリッドでは「壁のマスに **入る** ときにコスト 1」なのか「壁のマスから **出る** ときにコスト 1」なのか、問題に合わせて決める。
- 重みが 0, 1 以外 (2 以上) の辺があるなら使えません。ダイクストラを使いましょう。
