---
title: ダイクストラ法
category: グラフ
acl: -
genres: ダイクストラ, グラフ, 優先度付きキュー
order: 305
summary: 辺の重みが 0 以上のグラフで 1 点からの最短距離を求める
level: 緑
keywords: ダイクストラ, dijkstra, 最短経路, 最短距離, shortest path, 単一始点, 優先度付きキュー, PriorityQueue, 経路復元
---

## 何ができる？

辺に重み (距離・時間・コスト) がついたグラフで、スタート 1 点から **全頂点への最短距離** を求めます。
条件は **すべての辺の重みが 0 以上** であること。

| 関数 | 内容 | 計算量 |
| --- | --- | --- |
| `dijkstra(g, start)` | start から各頂点への最短距離。届かない頂点は `INF` | O((N + M) log N) |
| `dijkstra(g, start, prev)` | 上と同じ。さらに `prev[v]` に「最短経路で v の 1 つ手前の頂点」を記録 | O((N + M) log N) |
| `restorePath(prev, goal)` | `prev` をたどって start → goal の経路を返す | O(経路の長さ) |

## いつ使う？

- 「最短距離」「最小コスト」「最短時間」で、**辺の重みがバラバラ** (しかも負がない)
- N, M が 2×10⁵ くらいまで (ワーシャルフロイドは N ≤ 400 程度まで)
- 「全頂点から 1 点へ」の最短距離 → 辺の向きを逆にしたグラフで、その 1 点からダイクストラ
- 「頂点 k を必ず通る最短距離」→ 1 から k、k から N の 2 回に分けてダイクストラ (始点と終点からの 2 回で全 k をまとめて求めることも多い)
- 重みが全部同じなら BFS、0 と 1 だけなら 01-BFS、**負の重みがあるならベルマンフォード**

## しくみ

「まだ確定していない頂点のうち、**今わかっている距離が一番小さい頂点**」は、それ以上短くならないので確定できます。
これを優先度付きキュー (小さい順に取り出せる箱) で高速に選びます。

1. `dist` を全部 INF、`dist[start] = 0` にして、キューに `(0, start)` を入れる
2. キューから距離最小の `(d, v)` を取り出す。`d > dist[v]` なら古い情報なので捨てる
3. v から出る辺 (v → to, 重み c) で `dist[v] + c < dist[to]` なら `dist[to]` を更新し、`(dist[to], to)` をキューに入れる
4. キューが空になるまで 2〜3 を繰り返す

```
        2              start = 0
    0 ----- 1          取り出す順: 0(距離0) → 1(2) → 2(3) → 3(4)
    |     /
  5 |   / 1            dist[2] は最初 0→2 で 5 になるが、
    | /                1 を確定したあと 0→1→2 = 3 に更新される
    2 ----- 3          (キューに残った古い (5, 2) は取り出したときに捨てる)
        1              dist[3] = 0→1→2→3 = 4
```

なぜ確定してよいか: 取り出した v より遠回りの頂点を経由しても、重みが 0 以上なので距離は増える一方だからです。
**負の辺があるとこの理屈が崩れる** ので、ダイクストラは使えません。

## 実装 (Kotlin)

```kotlin
// 重み付きの辺: 行き先 to と重み cost
data class Edge(val to: Int, val cost: Long)

const val INF = Long.MAX_VALUE / 4

// start から各頂点への最短距離 (届かない頂点は INF)。辺の重みは 0 以上であること
// prev を渡すと、prev[v] に「最短経路で v の 1 つ手前の頂点」を入れる (start と未到達は -1)
fun dijkstra(g: Array<out List<Edge>>, start: Int, prev: IntArray? = null): LongArray {
    val n = g.size
    val dist = LongArray(n) { INF }
    prev?.fill(-1)
    // (距離, 頂点) を距離の小さい順に取り出す
    val pq = java.util.PriorityQueue<Pair<Long, Int>>(compareBy { it.first })
    dist[start] = 0L
    pq.add(Pair(0L, start))
    while (pq.isNotEmpty()) {
        val (d, v) = pq.poll()
        if (d > dist[v]) continue // 古い情報 (もっと短い距離で確定済み)
        for (e in g[v]) {
            val nd = d + e.cost
            if (nd < dist[e.to]) {
                dist[e.to] = nd
                if (prev != null) prev[e.to] = v
                pq.add(Pair(nd, e.to))
            }
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
```

## 使い方

入力は `N M` のあとに無向辺 `A_i B_i C_i` (1-indexed、重み C_i) が M 行。
頂点 1 から頂点 N への最短距離と、その経路を出力します (届かなければ `-1`)。

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
        g[b].add(Edge(a, c)) // 有向グラフならこの行を消す
    }

    val prev = IntArray(n)
    val dist = dijkstra(g, 0, prev)
    if (dist[n - 1] == INF) {
        println(-1)
    } else {
        println(dist[n - 1])
        println(restorePath(prev, n - 1).joinToString(" ") { (it + 1).toString() })
    }
}
```

入力例

```
4 5
1 2 2
1 3 5
2 3 1
2 4 6
3 4 1
```

出力例

```
4
1 2 3 4
```

## 例題

- [典型90 013 - Passing (★5)](https://atcoder.jp/contests/typical90/tasks/typical90_m) — 頂点 1 からと頂点 N からの 2 回ダイクストラ。k を通る最短 = dist1[k] + distN[k]
- [ABC 035 D - トレジャーハント](https://atcoder.jp/contests/abc035/tasks/abc035_d) — 行き (元のグラフ) と帰り (辺を逆向きにしたグラフ) で 2 回ダイクストラ
- [ABC 252 E - Road Reduction](https://atcoder.jp/contests/abc252/tasks/abc252_e) — 都市 1 からの最短経路で使った辺 (= `prev` の辺) を出力する

## 注意点・よくあるミス

- **負の重みの辺があると正しく動きません**。そのときはベルマンフォード。
- `if (d > dist[v]) continue` を忘れると、古い情報で何度も辺を見てしまい TLE になることがあります。
- 距離の合計は `Int` を超えやすいので `Long` を使う。INF を `Long.MAX_VALUE` にすると `d + e.cost` がオーバーフローするので、`Long.MAX_VALUE / 4` くらいにする。
- `PriorityQueue` は Java のものを使います (`java.util.PriorityQueue`)。デフォルトは **小さい順**。`compareBy { it.first }` で「距離が小さい順」を指定しています。
- 無向グラフなら両方向に辺を追加する。「頂点 X へ戻る」「X から全頂点へ」のような問題では、**辺の向きを逆にしたグラフ** を作ると 1 回で済むことがあります。
- 頂点数が少なく (N ≤ 400 程度) 全点間の距離が欲しいなら、ワーシャルフロイドのほうが簡単です。
