---
title: 幅優先探索 (BFS)
category: グラフ
acl: -
genres: BFS, グラフ, 二次元グリッド
order: 302
summary: 辺の重みがすべて同じグラフ・迷路で最短距離を求める
level: 茶
keywords: 幅優先探索, breadth first search, 最短距離, 最短経路, 迷路, 最小手数, グリッド, 経路復元, キュー
---

## 何ができる？

辺に重みがない (全部 1) グラフで、スタート地点から **各頂点への最短距離 (最小の辺の本数)** を求めます。
迷路 (二次元グリッド) の最小手数もこれで求まります。

| 関数 | 内容 | 計算量 |
| --- | --- | --- |
| `bfs(g, start)` | start から各頂点への最短距離。届かない頂点は `-1` | O(N + M) |
| `bfs(g, start, prev)` | 上と同じ。さらに `prev[v]` に「v の 1 つ手前の頂点」を記録 | O(N + M) |
| `restorePath(prev, goal)` | `prev` をたどって start → goal の経路を返す | O(経路の長さ) |
| `gridBfs(grid, sr, sc)` | 迷路で (sr, sc) から各マスへの最小手数。`#` は壁。届かないマスは `-1` | O(H × W) |

## いつ使う？

- 「最短距離」「最小手数」「最小の回数で」と言われて、**1 回の移動のコストが全部同じ**
- 迷路・グリッド上の移動 (上下左右に 1 マスずつ)
- 「ある状態から 1 手で行ける状態」を辺と見て、状態の数が 10⁶ くらいまで (パズルの最小手数など)
- 連結かどうかを調べるだけなら BFS でも DFS でも OK
- 重みがバラバラなら **ダイクストラ**、重みが 0 と 1 だけなら **01-BFS** を使う

## しくみ

**キュー (先に入れたものから取り出す行列)** を使って、スタートから近い順に頂点を確定していきます。

```
グラフ (start = 0)        距離 0 → 1 → 2 → 3 の順に広がる
 0 - 1 - 3 - 5
 |       |               dist: 0:0  1:1  2:1  3:2  4:2  5:3
 2 ----- 4
                         キューの動き: [0] → [1,2] → [2,3] → [3,4] → [4,5] → [5] → []
```

1. `dist` を全部 `-1` (未訪問) にし、`dist[start] = 0` としてキューに入れる
2. キューの先頭 v を取り出し、v の隣 `to` のうち **未訪問のもの** に `dist[to] = dist[v] + 1` を入れてキューに追加
3. キューが空になるまで 2 を繰り返す

キューからは距離の小さい順に出てくるので、**最初に訪れたときの距離が最短** です。だから一度決めた `dist` は二度と書き換えません。

**経路復元**: `dist[to]` を決めるときに `prev[to] = v` (どこから来たか) をメモしておけば、ゴールから `prev` を逆にたどってスタートまで戻れます。

**グリッド**: マス (r, c) を頂点、上下左右の隣のマスへの移動を辺と考えます。隣接リストを作らずに、`dr`, `dc` の配列で 4 方向を回すのが定番です。

## 実装 (Kotlin)

```kotlin
// 重みなしグラフで start からの最短距離。届かない頂点は -1
// prev を渡すと、prev[v] に「最短経路で v の 1 つ手前の頂点」を入れる (start と未到達は -1)
fun bfs(g: Array<out List<Int>>, start: Int, prev: IntArray? = null): IntArray {
    val n = g.size
    val dist = IntArray(n) { -1 }
    prev?.fill(-1)
    val queue = ArrayDeque<Int>()
    dist[start] = 0
    queue.addLast(start)
    while (queue.isNotEmpty()) {
        val v = queue.removeFirst()
        for (to in g[v]) {
            if (dist[to] != -1) continue // 訪問済み
            dist[to] = dist[v] + 1
            if (prev != null) prev[to] = v
            queue.addLast(to)
        }
    }
    return dist
}

// prev をたどって start → goal の頂点列を返す (goal に届かない場合は使わないこと)
fun restorePath(prev: IntArray, goal: Int): List<Int> {
    val path = mutableListOf<Int>()
    var v = goal
    while (v != -1) {
        path.add(v)
        v = prev[v]
    }
    path.reverse()
    return path
}

// グリッドの BFS。'#' は壁。(sr, sc) から各マスへの最小手数 (届かないマスは -1)
fun gridBfs(grid: Array<String>, sr: Int, sc: Int): Array<IntArray> {
    val h = grid.size
    val w = grid[0].length
    val dr = intArrayOf(-1, 1, 0, 0)
    val dc = intArrayOf(0, 0, -1, 1)
    val dist = Array(h) { IntArray(w) { -1 } }
    val queue = ArrayDeque<Int>() // マス (r, c) を r * w + c の 1 つの数にして入れる
    dist[sr][sc] = 0
    queue.addLast(sr * w + sc)
    while (queue.isNotEmpty()) {
        val cur = queue.removeFirst()
        val r = cur / w
        val c = cur % w
        for (k in 0 until 4) {
            val nr = r + dr[k]
            val nc = c + dc[k]
            if (nr !in 0 until h || nc !in 0 until w) continue // 外に出る
            if (grid[nr][nc] == '#' || dist[nr][nc] != -1) continue // 壁 or 訪問済み
            dist[nr][nc] = dist[r][c] + 1
            queue.addLast(nr * w + nc)
        }
    }
    return dist
}
```

## 使い方

### グラフ: 頂点 1 から頂点 N への最短距離と経路

入力は `N M` のあとに無向辺 `A_i B_i` (1-indexed) が M 行。届かなければ `-1`。

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
        g[a].add(b)
        g[b].add(a)
    }

    val prev = IntArray(n)
    val dist = bfs(g, 0, prev)
    println(dist[n - 1])
    if (dist[n - 1] != -1) {
        println(restorePath(prev, n - 1).joinToString(" ") { (it + 1).toString() })
    }
}
```

入力例

```
5 5
1 2
1 3
2 4
3 5
4 5
```

出力例

```
2
1 3 5
```

### グリッド: 迷路の最小手数

入力は `H W`、スタート `sr sc`、ゴール `gr gc` (1-indexed)、そのあと迷路が H 行 (`.` は通路、`#` は壁)。

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
    val sr = next().toInt() - 1
    val sc = next().toInt() - 1
    val gr = next().toInt() - 1
    val gc = next().toInt() - 1
    val grid = Array(h) { next() }

    val dist = gridBfs(grid, sr, sc)
    println(dist[gr][gc])
}
```

入力例

```
4 5
1 1
4 5
..#..
.##.#
...#.
.#...
```

出力例

```
7
```

## 例題

- [ABC 007 C - 幅優先探索](https://atcoder.jp/contests/abc007/tasks/abc007_3) — 迷路の最小手数。`gridBfs` そのまま
- [ABC 168 D - .. (Double Dots)](https://atcoder.jp/contests/abc168/tasks/abc168_d) — 各部屋の道しるべ = BFS の `prev`。部屋 1 からの BFS で、全部屋に `prev` を出力する
- [ABC 151 D - Maze Master](https://atcoder.jp/contests/abc151/tasks/abc151_d) — すべての通路マスをスタートにして `gridBfs` し、最大の距離を答える (H, W ≤ 20 なので間に合う)

## 注意点・よくあるミス

- **キューに入れるときに訪問済みにする** (`dist` を入れる)。取り出すときに印をつけると、同じ頂点が何度もキューに入って遅くなります。
- `dist` の初期値 `-1` を「未訪問」の印に使っているので、`dist[to] == 0` で未訪問判定しないこと (start が 0 なので壊れる)。
- グリッドの範囲チェック (`nr in 0 until h`) を **壁チェックより先に** 書く。逆だと配列の外を読んで落ちます。
- 入力のスタート・ゴールが 1-indexed か 0-indexed か、(行, 列) か (x, y) かを問題文でよく確認する。
- `restorePath` は、ゴールに届かない (`dist[goal] == -1`) ときに呼ぶと、ゴール 1 頂点だけのリストを返してしまいます。先に届くかを確認しましょう。
- 辺の重みが 1 以外も混ざっていたら BFS では解けません (→ ダイクストラ / 01-BFS)。
- `ArrayDeque` は Kotlin 標準の `kotlin.collections.ArrayDeque` です (import 不要)。`java.util.LinkedList` より速いです。
