---
title: ベルマンフォード法
category: グラフ
acl: -
genres: グラフ
order: 306
summary: 負の重みの辺があっても最短距離を求め、負閉路も検出する
level: 緑
keywords: ベルマンフォード, bellman ford, 負の辺, 負閉路, 負のサイクル, negative cycle, 最短経路, 最長経路, inf
---

## 何ができる？

辺の重みに **負の数** があっても、スタート 1 点から各頂点への最短距離を求めます。
さらに、**負閉路** (1 周すると合計が負になるサイクル。何周もすればいくらでも距離を小さくできる) の影響で
最短距離が `-∞` になる頂点も見つけます。

| 関数 | 内容 | 計算量 |
| --- | --- | --- |
| `bellmanFord(n, from, to, cost, start)` | start から各頂点への最短距離。届かない頂点は `INF`、負閉路のせいでいくらでも小さくできる頂点は `NEG_INF` | O(N × M) |

## いつ使う？

- 辺の重みに **負の数がある** 最短経路 (ダイクストラが使えない)
- 「**スコアを最大化**」する経路 → 重みの符号を反転すると最短経路になる。このとき正のサイクル (= 反転後の負閉路) があると「いくらでも大きくできる (inf)」
- 「負閉路があるか判定せよ」
- N × M が 10⁷〜10⁸ くらいまで (N = 1000, M = 2000 など)。大きいグラフでは使えない

## しくみ

すべての辺 (u → v, 重み c) について「`dist[u] + c < dist[v]` なら更新」(緩和) を、**全部の辺に対して N-1 回** 繰り返します。

なぜ N-1 回か: 負閉路がなければ、最短経路は同じ頂点を 2 回通らないので **辺の本数は N-1 本以下**。
1 周ごとに「辺 k 本以下で行ける最短距離」が確定していくので、N-1 周で全部確定します。

```
  0 --(2)--> 2        ダイクストラだと 2 を距離 2 で確定してしまうが、
  |          ^        本当は 0 → 1 → 2 = 4 + (-3) = 1 が最短。
 (4)         |        ベルマンフォードは全部の辺を何周も見直すので、
  v          |        あとから 1 に更新できる
  1 --(-3)---+
```

**負閉路の検出**: N-1 周したあとでも、まだ更新できる辺があれば負閉路があります。
ただし、問題で聞かれるのは「ゴールの距離が -∞ になるか」であることが多く、負閉路が
ゴールと関係ない場所にあっても答えは変わりません。そこでこの実装では、

- N-1 周したあと、**さらに N 周**「更新できる辺の行き先」(と、そこから行ける頂点) を `NEG_INF` にする

ことで、負閉路から行ける頂点だけを正確に `NEG_INF` にしています。

## 実装 (Kotlin)

```kotlin
const val INF = Long.MAX_VALUE / 4
const val NEG_INF = Long.MIN_VALUE / 4

// 辺 i は from[i] → to[i]、重み cost[i] (負でもよい)。頂点数 n、始点 start
// 戻り値: 最短距離。届かない頂点は INF、負閉路の影響でいくらでも小さくできる頂点は NEG_INF
fun bellmanFord(n: Int, from: IntArray, to: IntArray, cost: LongArray, start: Int): LongArray {
    val m = from.size
    val dist = LongArray(n) { INF }
    dist[start] = 0L
    // N-1 周すれば、負閉路の影響を受けない頂点の最短距離は確定する
    repeat(n - 1) {
        var updated = false
        for (i in 0 until m) {
            if (dist[from[i]] == INF) continue // まだ届いていない頂点からは更新しない
            val nd = dist[from[i]] + cost[i]
            if (nd < dist[to[i]]) {
                dist[to[i]] = nd
                updated = true
            }
        }
        if (!updated) return dist // もう変わらないなら負閉路はない
    }
    // さらに N 周: まだ更新できる頂点 (とそこから行ける頂点) は -∞ にできる
    repeat(n) {
        for (i in 0 until m) {
            if (dist[from[i]] == INF) continue
            if (dist[from[i]] == NEG_INF || dist[from[i]] + cost[i] < dist[to[i]]) {
                dist[to[i]] = NEG_INF
            }
        }
    }
    return dist
}
```

## 使い方

[ABC 061 D - Score Attack](https://atcoder.jp/contests/abc061/tasks/abc061_d) を解くコードです。
頂点 1 から N へ移動し、通った辺のスコア c_i の合計を **最大化** します。いくらでも大きくできるなら `inf`。
重みを `-c` にして最短経路にすれば、ベルマンフォードがそのまま使えます。

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
    val from = IntArray(m)
    val to = IntArray(m)
    val cost = LongArray(m)
    for (i in 0 until m) {
        from[i] = next().toInt() - 1
        to[i] = next().toInt() - 1
        cost[i] = -next().toLong() // 最大化 → 符号を反転して最小化
    }

    val dist = bellmanFord(n, from, to, cost, 0)
    if (dist[n - 1] == NEG_INF) {
        println("inf")
    } else {
        println(-dist[n - 1])
    }
}
```

入力例 1

```
3 3
1 2 4
2 3 3
1 3 5
```

出力例 1

```
7
```

入力例 2 (2 ⇄ 3 を回るとスコアが増え続ける)

```
3 3
1 2 1
2 3 1
3 2 1
```

出力例 2

```
inf
```

## 例題

- [ABC 061 D - Score Attack](https://atcoder.jp/contests/abc061/tasks/abc061_d) — 上の「使い方」そのもの。ゴールに関係ない正のサイクルは無視する必要がある
- [ABC 137 E - Coins Respawn](https://atcoder.jp/contests/abc137/tasks/abc137_e) — 辺の重みを `C_i - P` にして最長経路。いくらでも増やせるなら `-1`、答えが負なら 0

## 注意点・よくあるミス

- 「負閉路があれば即 inf」とすると間違えることがあります。**その負閉路を通ってゴールに行けるか** が大事です
  (上の実装の 2 段階目はそのためのものです)。
- `dist[from] == INF` の頂点から更新しないこと。INF + 負の数 が INF より小さくなり、届かない頂点に変な値が入ります。
- 計算量は O(N × M)。N, M が 2×10⁵ のような問題では使えません (負の辺がない → ダイクストラ)。
- 無向グラフで負の辺があると、その辺を往復するだけで負閉路になります。普通は有向グラフの問題で使います。
- 最大化問題では符号の反転を忘れずに。出力するときにもう一度反転します。
