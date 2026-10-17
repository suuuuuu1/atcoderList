---
title: 二部グラフ判定
category: グラフ
acl: -
genres: グラフ, BFS, DFS
order: 309.5
summary: 隣り合う頂点が違う色になるよう 2 色で塗れるか判定する
level: 緑
keywords: 二部グラフ, bipartite, 2色塗り分け, 彩色, 奇閉路, 奇数長のサイクル, グループ分け, 敵味方, 偶奇
---

## 何ができる？

グラフの頂点を **白と黒の 2 色** で塗り、**どの辺も両端の色が違う** ようにできるかを判定します。
できるグラフを **二部グラフ** といいます。できる場合は実際の塗り方も分かります。

| 関数 | 内容 | 計算量 |
| --- | --- | --- |
| `bipartiteColoring(g)` | 2 色塗り分けできるなら各頂点の色 (0 か 1) の配列、できないなら `null` | O(N + M) |

## いつ使う？

- 「2 つのグループに分けて、**同じグループの人どうしは仲が悪くない** ように」
- 「隣り合うものは違う色 / 違うチーム」「辺の両端は必ず別」
- 「長さが奇数のサイクルがあるか」(二部グラフ ⇔ 奇数長のサイクルがない)
- 木は必ず二部グラフ。根からの距離の偶奇で塗り分けられる
- 二部グラフの最大マッチング (→ 最大流) の前準備として、左右に分ける

## しくみ

まだ色のない頂点を 1 つ選んで色 0 を塗り、BFS (DFS でも可) で広げながら **隣の頂点には反対の色** を塗っていきます。

- 隣がまだ塗られていない → 反対の色を塗る
- 隣がすでに **同じ色** で塗られている → 矛盾。二部グラフではない

1 つの連結成分の中では、最初の頂点の色を決めると残りの色は全部決まってしまうので、矛盾が出たら「塗れない」で確定です。
連結でないグラフは、成分ごとに独立に塗ります。

```
 二部グラフ (偶数長のサイクル)       二部グラフでない (三角形 = 奇数長のサイクル)
   0(白) --- 1(黒)                       0(白) --- 1(黒)
     |         |                           \       /
   3(黒) --- 2(白)                          2(白? 黒?)  ← どちらでも矛盾
```

## 実装 (Kotlin)

```kotlin
// 2 色塗り分け (BFS)。できるなら color[v] = 0 or 1 の配列、できないなら null
// 連結でないグラフでも OK (成分ごとに、番号の一番小さい頂点を色 0 にする)
fun bipartiteColoring(g: Array<out List<Int>>): IntArray? {
    val n = g.size
    val color = IntArray(n) { -1 } // -1 = まだ塗っていない
    val queue = ArrayDeque<Int>()
    for (s in 0 until n) {
        if (color[s] != -1) continue
        color[s] = 0
        queue.addLast(s)
        while (queue.isNotEmpty()) {
            val v = queue.removeFirst()
            for (to in g[v]) {
                if (color[to] == -1) {
                    color[to] = 1 - color[v] // 反対の色
                    queue.addLast(to)
                } else if (color[to] == color[v]) {
                    return null // 隣どうしが同じ色 → 二部グラフでない
                }
            }
        }
    }
    return color
}
```

## 使い方

入力は `N M` のあとに無向辺 `A_i B_i` (1-indexed) が M 行。
二部グラフなら `Yes` と各頂点の色、そうでなければ `No` を出力します。

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

    val color = bipartiteColoring(g)
    if (color == null) {
        println("No")
    } else {
        println("Yes")
        println(color.joinToString(" "))
    }
}
```

入力例 1

```
5 4
1 2
2 3
3 4
4 1
```

出力例 1

```
Yes
0 1 0 1 0
```

入力例 2

```
3 3
1 2
2 3
3 1
```

出力例 2

```
No
```

## 例題

- [ABC 327 D - Good Tuple Problem](https://atcoder.jp/contests/abc327/tasks/abc327_d) — 「A_i と B_i は違う値」を辺にして二部グラフ判定。A_i = B_i (自己ループ) があれば No
- [ABC 126 D - Even Relation](https://atcoder.jp/contests/abc126/tasks/abc126_d) — 木の 2 色塗り。根からの距離の偶奇で色を決める
- [ABC 282 D - Make Bipartite 2](https://atcoder.jp/contests/abc282/tasks/abc282_d) — 連結成分ごとに色の個数を数え、「二部グラフのまま足せる辺 = 白×黒 − 既存の辺」を数える

## 注意点・よくあるミス

- **連結でないグラフ** で、頂点 0 からの 1 回の BFS だけで判定しないこと。全頂点から (未着色なら) 始める。
- **自己ループ** (a = b の辺) があると、その頂点は自分と違う色にできないので二部グラフではありません。上の実装では `color[to] == color[v]` で正しく `null` になります。
- 塗り方は 1 通りではありません (成分ごとに白黒を入れ替えてもよい)。「白の個数を最小に」などを聞かれたら、成分ごとに少ないほうを選ぶ。
- 再帰 DFS で書くと深いグラフでスタックオーバーフローすることがあります。BFS なら安心です。
- 「重み付きの辺で、距離の偶奇で塗る」タイプ (ABC 126 D) は、辺の重みの偶奇で「同じ色 / 反対の色」を決める。
