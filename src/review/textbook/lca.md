---
title: LCA (最小共通祖先)
category: 木
acl: -
genres: 木
order: 4520
summary: 木の 2 頂点の共通の祖先と距離を O(log N) で求める
level: 緑
keywords: 最小共通祖先, lowest common ancestor, ダブリング, doubling, 木の2点間距離, 祖先, k個上の親
---

## 何ができる？

根付き木で、2 頂点 u, v の **最小共通祖先** (LCA: 両方の祖先のうち一番深いもの) を求めます。
LCA が分かると **2 頂点間の距離** もすぐ求まります。

| 操作 | 内容 | 計算量 |
| --- | --- | --- |
| `LCA(n, adj, root)` | 前計算 (親・深さ・2^k 個上の祖先の表) | O(N log N) |
| `lca(u, v)` | u と v の最小共通祖先 | O(log N) |
| `dist(u, v)` | u と v の距離 (辺の本数) | O(log N) |
| `ancestor(v, k)` | v から k 個上の祖先 | O(log N) |
| `depth[v]` | 根からの深さ | O(1) |

## いつ使う？

- 木の **2 点間の距離** を何度も (Q 回) 聞かれる
- 「木に辺を 1 本足すとできる閉路の長さ」→ `dist(u, v) + 1`
- 「u から v へのパス上に w があるか」→ `dist(u, w) + dist(w, v) == dist(u, v)`
- 「k 個上の親」「k 回親をたどった先」を何度も聞かれる
- 1 回だけなら BFS で十分。**クエリがたくさん** あるときに LCA を使います

## しくみ

### 距離の公式

```
             r (根)
             |
             a  ← LCA(u, v)
            / \
           .   .
          /     \
         u       v

dist(u, v) = depth[u] + depth[v] - 2 * depth[a]
```

### ダブリング

`up[k][v]` = 「v から 2^k 個上の祖先」という表を作ります。

- `up[0][v]` = 親
- `up[k][v]` = `up[k-1][ up[k-1][v] ]` (2^(k-1) 個上の、さらに 2^(k-1) 個上)

k 個上に登るときは、k を 2 進数で見て、立っているビットの分だけジャンプします
(例: 13 = 8 + 4 + 1 個上 → 3 回のジャンプ)。

### LCA の求め方

1. 深い方を登らせて、2 頂点の **深さをそろえる**。この時点で同じ頂点なら、それが LCA
2. 大きいジャンプ (2^(log-1)) から順に試し、**ジャンプ先が違う頂点なら 2 人とも登る**
3. 最後に 2 人の **親** が LCA

ジャンプ先が同じ = 「LCA かそれより上」なので登りすぎになる、だから登らない、という考え方です。

## 実装 (Kotlin)

```kotlin
// ダブリングによる LCA (最小共通祖先)。前計算 O(N log N)、クエリ O(log N)
class LCA(private val n: Int, adj: Array<out List<Int>>, root: Int = 0) {
    private val log: Int
    val depth = IntArray(n)
    private val up: Array<IntArray>   // up[k][v] = v から 2^k 個上の祖先 (根より上は根のまま)

    init {
        var lg = 1
        while ((1 shl lg) < n) lg++
        log = lg
        up = Array(log) { IntArray(n) }
        // 非再帰 DFS で 親 (up[0]) と深さを求める
        val visited = BooleanArray(n)
        val stack = IntArray(n)
        var sp = 0
        stack[sp++] = root
        visited[root] = true
        up[0][root] = root
        while (sp > 0) {
            val v = stack[--sp]
            for (c in adj[v]) {
                if (visited[c]) continue
                visited[c] = true
                up[0][c] = v
                depth[c] = depth[v] + 1
                stack[sp++] = c
            }
        }
        // 2^k 個上 = 2^(k-1) 個上 の 2^(k-1) 個上
        for (k in 1 until log) {
            for (v in 0 until n) up[k][v] = up[k - 1][up[k - 1][v]]
        }
    }

    // v から k 個上の祖先 (0 <= k <= depth[v])
    fun ancestor(v: Int, k: Int): Int {
        var x = v
        for (i in 0 until log) {
            if ((k shr i) and 1 == 1) x = up[i][x]
        }
        return x
    }

    fun lca(a: Int, b: Int): Int {
        var u = a
        var v = b
        if (depth[u] < depth[v]) { val t = u; u = v; v = t }
        u = ancestor(u, depth[u] - depth[v])   // 深さをそろえる
        if (u == v) return u
        // 祖先が一致しない限り、大きいジャンプから順に 2 人同時に登る
        for (k in log - 1 downTo 0) {
            if (up[k][u] != up[k][v]) {
                u = up[k][u]
                v = up[k][v]
            }
        }
        return up[0][u]
    }

    // 2 頂点間の距離 (辺の本数)
    fun dist(a: Int, b: Int): Int = depth[a] + depth[b] - 2 * depth[lca(a, b)]
}
```

## 使い方

[ABC 014 D - 閉路](https://atcoder.jp/contests/abc014/tasks/abc014_4) を解くコードです。
木に辺 (a, b) を 1 本足したときにできる閉路の長さを、クエリごとに答えます。

```kotlin
fun main() {
    val br = System.`in`.bufferedReader()
    var st = java.util.StringTokenizer("")
    fun next(): String {
        while (!st.hasMoreTokens()) st = java.util.StringTokenizer(br.readLine())
        return st.nextToken()
    }

    val n = next().toInt()
    val adj = Array(n) { mutableListOf<Int>() }
    repeat(n - 1) {
        val x = next().toInt() - 1
        val y = next().toInt() - 1
        adj[x].add(y)
        adj[y].add(x)
    }
    val lca = LCA(n, adj, 0)

    val q = next().toInt()
    val sb = StringBuilder()
    repeat(q) {
        val a = next().toInt() - 1
        val b = next().toInt() - 1
        // a と b を結ぶ辺を 1 本足すと、長さ dist(a, b) + 1 の閉路ができる
        sb.appendLine(lca.dist(a, b) + 1)
    }
    print(sb)
}
```

入力例

```
5
1 2
1 3
1 4
4 5
3
2 3
2 5
2 4
```

出力例

```
3
4
3
```

## 例題

- [ABC 014 D - 閉路](https://atcoder.jp/contests/abc014/tasks/abc014_4) — 上の「使い方」そのもの。`dist + 1`
- [ABC 209 D - Collision](https://atcoder.jp/contests/abc209/tasks/abc209_d) — 2 人が町で会うか道で会うか。距離の偶奇で決まる (実は `depth` の偶奇だけでも解ける)

## 注意点・よくあるミス

- 入力が 1-indexed なら `- 1` する。
- `up` の段数 `log` は 2^log ≥ N となるように取る。足りないと遠い祖先に届きません。
- 根の親は **根自身** にしています (はみ出したジャンプは根で止まる)。`-1` にすると配列外参照になります。
- **辺に重みがある** ときは、深さとは別に「根からの重み付き距離」`distRoot[v]` (Long) を DFS で求め、
  `distRoot[u] + distRoot[v] - 2 * distRoot[lca]` とします。
- 再帰 DFS で前計算すると、パス状の木で StackOverflow することがあります。上の実装は非再帰です。
- 森 (木が複数) だと、根から届かない頂点の情報が正しくありません。連結であることを確認しましょう。
