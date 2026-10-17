---
title: 深さ優先探索 (DFS)
category: グラフ
acl: -
genres: DFS, グラフ, 再帰
order: 303
summary: 行けるところまで進んで戻る探索。連結判定や木の計算に
level: 茶
keywords: 深さ優先探索, depth first search, 連結成分, 到達可能, 再帰, 行きがけ順, 帰りがけ順, preorder, postorder, スタック
---

## 何ができる？

スタートから「行けるところまでどんどん進み、行き止まりになったら 1 つ戻って別の道を試す」探索です。
**どの頂点に行けるか** (到達可能性・連結成分) を調べたり、**木の部分木の情報を集める** のに使います。

| 関数 | 内容 | 計算量 |
| --- | --- | --- |
| `dfs(g, v, visited, pre, post)` | 再帰版。v から行ける頂点を訪れ、行きがけ順を `pre`、帰りがけ順を `post` に追加 | O(N + M) |
| `dfsIterative(g, v, visited, pre, post)` | 非再帰版。結果は再帰版と完全に同じ | O(N + M) |
| `connectedComponents(g)` | 各頂点の連結成分の番号 (0, 1, 2, ...) を返す | O(N + M) |

※ **最短距離は DFS では求まりません** (→ BFS)。

## いつ使う？

- 「頂点 s から t に行ける？」「いくつのグループ (連結成分) に分かれる？」
- 木の問題: 部分木のサイズ・部分木の和・親を求める・根からの累積 (木 DP の土台)
- 「行きがけ順」「帰りがけ順」(オイラーツアー) を使う問題
- 全探索を再帰で書く (順列や組み合わせの列挙) のも DFS の一種
- サイクル検出、二部グラフ判定、トポロジカルソート (帰りがけ順の逆) などの部品

## しくみ

```
 グラフ                 dfs(0) の動き
   0                    0 に入る → 1 に入る → 3 に入る → 3 から出る
  / \                        → 1 から出る → 2 に入る → 2 から出る → 0 から出る
 1   2
 |                      行きがけ順 (pre)  : 0 1 3 2   (入った順)
 3                      帰りがけ順 (post) : 3 1 2 0   (出た順。子が全部終わってから親)
```

- **行きがけ順 (preorder)**: 頂点に「入った」順。親は必ず子より先。
- **帰りがけ順 (postorder)**: 頂点から「出た」順。子が必ず親より先なので、**子の結果を使って親を計算する** (部分木のサイズなど) ときはこの順番で処理します。

### 再帰と非再帰

再帰で書くのが一番分かりやすいですが、Kotlin (JVM) はスタックが小さく、**一直線のグラフで N = 10⁵ を超えると StackOverflowError** になります。対策は 2 つ。

1. main で **スタックを大きくしたスレッド** を作り、その中で解く (`Thread(null, ::solve, "", 1 shl 26).start()`)
2. **非再帰版** を使う。「どの頂点の、何本目の辺まで見たか」を自分のスタックに積んで、再帰と同じ動きをさせます

ただ「行ける頂点を全部見る」だけなら順番は関係ないので、`connectedComponents` のように単純なスタック (or キュー) で十分です。

## 実装 (Kotlin)

```kotlin
// 再帰版 DFS: v から行ける未訪問の頂点を全部訪れる
// pre に行きがけ順、post に帰りがけ順で頂点を追加する
fun dfs(g: Array<out List<Int>>, v: Int, visited: BooleanArray, pre: MutableList<Int>, post: MutableList<Int>) {
    visited[v] = true
    pre.add(v) // 行きがけ: 頂点に入ったときの処理はここ
    for (to in g[v]) {
        if (!visited[to]) dfs(g, to, visited, pre, post)
    }
    post.add(v) // 帰りがけ: 子を全部見終わったあとの処理はここ
}

// 非再帰版 DFS: 再帰版と同じ順番で pre, post を作る (深いグラフでも落ちない)
fun dfsIterative(g: Array<out List<Int>>, start: Int, visited: BooleanArray, pre: MutableList<Int>, post: MutableList<Int>) {
    val vs = ArrayDeque<Int>()   // 再帰の呼び出し中の頂点
    val idxs = ArrayDeque<Int>() // その頂点の「次に見る辺の番号」
    visited[start] = true
    pre.add(start)
    vs.addLast(start)
    idxs.addLast(0)
    while (vs.isNotEmpty()) {
        val v = vs.last()
        val i = idxs.last()
        if (i < g[v].size) {
            idxs[idxs.size - 1] = i + 1
            val to = g[v][i]
            if (!visited[to]) { // 再帰呼び出しの代わりにスタックに積む
                visited[to] = true
                pre.add(to)
                vs.addLast(to)
                idxs.addLast(0)
            }
        } else { // 辺を全部見終わった = 関数から return する
            vs.removeLast()
            idxs.removeLast()
            post.add(v)
        }
    }
}

// 連結成分の番号付け (無向グラフ)。comp[v] = v の成分番号 (0 始まり)。成分の個数は comp.max() + 1
fun connectedComponents(g: Array<out List<Int>>): IntArray {
    val n = g.size
    val comp = IntArray(n) { -1 }
    var id = 0
    val stack = ArrayDeque<Int>()
    for (s in 0 until n) {
        if (comp[s] != -1) continue
        comp[s] = id
        stack.addLast(s)
        while (stack.isNotEmpty()) {
            val v = stack.removeLast()
            for (to in g[v]) {
                if (comp[to] == -1) {
                    comp[to] = id
                    stack.addLast(to)
                }
            }
        }
        id++
    }
    return comp
}
```

## 使い方

入力は `N M` のあとに無向辺 `A_i B_i` (1-indexed) が M 行。
連結成分の個数と、頂点 1 からの行きがけ順・帰りがけ順を出力します。
再帰版を使うので、スタックを大きくしたスレッドの中で解いています。

```kotlin
fun main() {
    // 再帰が深くなっても落ちないように、スタック 64MB のスレッドで solve を動かす
    Thread(null, ::solve, "solve", 1 shl 26).start()
}

fun solve() {
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

    val comp = connectedComponents(g)
    println(comp.max() + 1) // 連結成分の個数

    val visited = BooleanArray(n)
    val pre = mutableListOf<Int>()
    val post = mutableListOf<Int>()
    dfs(g, 0, visited, pre, post) // 再帰版。dfsIterative(g, 0, visited, pre, post) でも同じ結果
    println(pre.joinToString(" ") { (it + 1).toString() })
    println(post.joinToString(" ") { (it + 1).toString() })
}
```

入力例

```
6 4
1 2
1 3
2 4
5 6
```

出力例

```
2
1 2 4 3
4 2 3 1
```

## 例題

- [ATC 001 A - 深さ優先探索](https://atcoder.jp/contests/atc001/tasks/dfs_a) — 迷路でスタートからゴールに行けるか。DFS の練習用
- [ABC 075 C - Bridge](https://atcoder.jp/contests/abc075/tasks/abc075_c) — 辺を 1 本消して連結成分が増えるかを、辺ごとに DFS で調べる
- [ABC 138 D - Ki](https://atcoder.jp/contests/abc138/tasks/abc138_d) — 各頂点に足した値を、根から DFS (行きがけ) で子に足し込んでいく。N = 2×10⁵ なので再帰ならスレッド必須

## 注意点・よくあるミス

- **再帰の深さ**: N が 10⁴ を超えるなら、スタックを大きくしたスレッドか非再帰版を使う。AtCoder の Kotlin で一番よくある RE の原因です。
- `visited` を付け忘れる・付ける場所が遅いと、同じ頂点を何度も訪れて無限ループ / TLE になります。
- 連結でないグラフでは、`dfs(g, 0, ...)` 1 回では全部の頂点を回れません。全頂点を回りたいなら
  `for (v in 0 until n) if (!visited[v]) dfs(...)` のように、未訪問の頂点から何度も始めます。
- 木の DFS では `visited` の代わりに「親 (来た頂点) には戻らない」(`if (to == parent) continue`) と書くことも多いです。
- 再帰関数の中で毎回 `IntArray` などを作ると遅くなります。配列は外で作って引数で渡す。
- DFS で最短距離は求まりません。最短距離が欲しいなら BFS。
