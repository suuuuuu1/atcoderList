---
title: グラフの表し方
category: グラフ
acl: -
genres: グラフ
order: 301
summary: 頂点と辺の入力を隣接リスト・隣接行列にして持つ
level: 茶
keywords: 隣接リスト, 隣接行列, adjacency list, 辺, 頂点, 有向グラフ, 無向グラフ, 重み付きグラフ, 0-indexed
---

## 何ができる？

「頂点」と「辺」でできたグラフを、プログラムで扱える形 (配列) にします。
BFS・DFS・ダイクストラなど、グラフのアルゴリズムはすべてここから始まります。

| 持ち方 | 書き方 (Kotlin) | メモリ | 「v の隣を全部見る」 | 「u と v は直接つながっている？」 |
| --- | --- | --- | --- | --- |
| 隣接リスト (重みなし) | `Array(n) { mutableListOf<Int>() }` | O(N + M) | O(v の次数) | O(v の次数) |
| 隣接リスト (重み付き) | `Array(n) { mutableListOf<Edge>() }` | O(N + M) | O(v の次数) | O(v の次数) |
| 隣接行列 | `Array(n) { LongArray(n) { INF } }` | O(N²) | O(N) | O(1) |

※ N = 頂点数、M = 辺の数。**基本は隣接リスト**。隣接行列は N が 500 くらいまでのとき (ワーシャルフロイドなど) に使います。

## いつ使う？

- 問題文に「頂点」「辺」「道路」「駅と路線」「友達関係」「町と町を結ぶ」などが出てきたら、まずグラフにする
- 入力が `N M` のあとに `A_i B_i` が M 行 (重みつきなら `A_i B_i C_i`) という形なら、ほぼ確実にグラフ
- 二次元グリッド (迷路) もグラフの一種ですが、わざわざ隣接リストにせず、マス (行, 列) を直接たどるのが普通です (→ BFS のページ)

## しくみ

### 無向グラフと有向グラフ

```
無向グラフ (道路を両方向に通れる)      有向グラフ (一方通行)
    0 --- 1                              0 --> 1
    |     |                              |     |
    3     2                              v     v
                                         3     2
g[0] = [1, 3]                          g[0] = [1, 3]
g[1] = [0, 2]                          g[1] = [2]
g[2] = [1]                             g[2] = []
g[3] = [0]                             g[3] = []
```

- **隣接リスト**: `g[v]` に「v から 1 本の辺で行ける頂点」を並べたリストを持ちます。
  無向グラフでは辺 a—b を **`g[a].add(b)` と `g[b].add(a)` の両方** で登録します。有向なら `g[a].add(b)` だけ。
- **重み付き**: 行き先と一緒に重み (距離・コスト) も持ちたいので、`Edge(to, cost)` を入れます。
- **隣接行列**: `d[u][v]` = 「u から v への辺の重み (なければ INF)」という N×N の表です。

### 1-indexed → 0-indexed

AtCoder の入力は頂点が **1 〜 N** で来ることがほとんどです。Kotlin の配列は 0 から始まるので、
**読んだ直後に `- 1` して 0 〜 N-1 にする** のが一番ミスしにくいです。出力するときに `+ 1` を忘れずに。

## 実装 (Kotlin)

グラフの入力は main に直接書くのが普通なので、テンプレートとして示します (重みなし・無向グラフ)。

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
    // 隣接リスト: g[v] = v から 1 本の辺で行ける頂点のリスト
    val g = Array(n) { mutableListOf<Int>() }
    repeat(m) {
        val a = next().toInt() - 1 // 1-indexed → 0-indexed
        val b = next().toInt() - 1
        g[a].add(b)
        g[b].add(a) // 有向グラフ (a → b だけ) ならこの行を消す
    }

    // 例: 各頂点の次数 (つながっている辺の本数) を出力
    val sb = StringBuilder()
    for (v in 0 until n) sb.appendLine(g[v].size)
    print(sb)
}
```

## 使い方

重み付きグラフを、隣接リストと隣接行列の両方で持つ例です。
入力は `N M` のあとに `A_i B_i C_i` (頂点 A_i と B_i を重み C_i の辺で結ぶ・1-indexed) が M 行。

```kotlin
// 重み付きの辺: 行き先 to と重み cost
data class Edge(val to: Int, val cost: Long)

fun main() {
    val br = System.`in`.bufferedReader()
    var st = java.util.StringTokenizer("")
    fun next(): String {
        while (!st.hasMoreTokens()) st = java.util.StringTokenizer(br.readLine())
        return st.nextToken()
    }

    val n = next().toInt()
    val m = next().toInt()
    val INF = Long.MAX_VALUE / 4

    val g = Array(n) { mutableListOf<Edge>() }      // 隣接リスト (重み付き)
    val d = Array(n) { LongArray(n) { INF } }       // 隣接行列 (辺がなければ INF)
    for (i in 0 until n) d[i][i] = 0L
    repeat(m) {
        val a = next().toInt() - 1
        val b = next().toInt() - 1
        val c = next().toLong()
        g[a].add(Edge(b, c))
        g[b].add(Edge(a, c))
        // 同じ 2 頂点を結ぶ辺が複数あるかもしれないので小さいほうを残す
        d[a][b] = minOf(d[a][b], c)
        d[b][a] = minOf(d[b][a], c)
    }

    val sb = StringBuilder()
    // 隣接リスト: 頂点 v の隣を全部見る
    for (v in 0 until n) {
        sb.append(v + 1).append(':')
        for (e in g[v]) sb.append(" (").append(e.to + 1).append(", ").append(e.cost).append(')')
        sb.appendLine()
    }
    // 隣接行列: d[u][v] を O(1) で見る
    for (u in 0 until n) {
        sb.appendLine((0 until n).joinToString(" ") { v -> if (d[u][v] == INF) "-" else d[u][v].toString() })
    }
    print(sb)
}
```

入力例

```
3 3
1 2 5
2 3 2
1 2 3
```

出力例

```
1: (2, 5) (2, 3)
2: (1, 5) (3, 2) (1, 3)
3: (2, 2)
0 3 -
3 0 2
- 2 0
```

## 例題

- [ABC 061 B - Counting Roads](https://atcoder.jp/contests/abc061/tasks/abc061_b) — 各都市につながる道路の本数 = 隣接リストのサイズ (上の「実装」そのもの)
- [ABC 054 C - One-stroke Path](https://atcoder.jp/contests/abc054/tasks/abc054_c) — N ≤ 8 なので隣接行列で「直接つながっているか」を O(1) で見ながら順列を全探索
- [ABC 168 D - .. (Double Dots)](https://atcoder.jp/contests/abc168/tasks/abc168_d) — 隣接リストを作って BFS (→ BFS のページ)

## 注意点・よくあるミス

- **1-indexed の `- 1` 忘れ**。読んだ直後に引くクセをつける。出力が頂点番号なら `+ 1` して戻す。
- 無向グラフで **片方向しか登録しない** ミス。`g[a].add(b)` と `g[b].add(a)` の両方が必要。
- 隣接行列は N² のメモリを使います。N = 2×10⁵ で `Array(n) { LongArray(n) }` を作ると確実に MLE。
- 隣接行列では **多重辺** (同じ 2 頂点を結ぶ辺が複数) に注意。`minOf` で小さいほうを残す。自己ループ (a = b) がある問題もあります。
- 重みの合計が 2^31 を超えそうなら `Long` を使う。INF は `Long.MAX_VALUE` そのものではなく `Long.MAX_VALUE / 4` くらいにすると、足し算であふれません。
- `Array(n) { mutableListOf<Int>() }` を `Array(n) { list }` (同じ list を n 回入れる) と書くと全頂点で同じリストを共有してしまいます。ラムダの中で毎回 `mutableListOf` を呼ぶこと。
- 入力が多い (M = 2×10⁵ など) ので `readLine()!!.split(" ")` を何度も呼ぶより、上のような `StringTokenizer` を使った読み込みが速くて安心です。
