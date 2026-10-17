---
title: 木の基本と木 DP
category: 木
acl: -
genres: 木, DP, DFS
order: 4510
summary: 根付き木の親・深さ・部分木サイズを求め、葉から順に DP する
level: 緑
keywords: 根付き木, 部分木, 親, 深さ, tree dp, 木DP, 非再帰DFS, 葉から根へ, 部分木のサイズ
---

## 何ができる？

N 頂点 N-1 辺の連結なグラフ (= **木**) を、ある頂点を **根** にして「親子関係のある木」(根付き木) として扱います。
一度 DFS するだけで、次の情報がまとめて求まります。

| 求めるもの | 内容 | 計算量 |
| --- | --- | --- |
| `parent[v]` | v の親 (根は -1) | O(N) |
| `depth[v]` | 根からの深さ (辺の本数。根は 0) | O(N) |
| `size[v]` | v を根とする部分木の頂点数 (v 自身を含む) | O(N) |
| `order` | 訪問順。**親は必ず子より前** に並ぶ | O(N) |
| `children(v)` | v の子の一覧 | O(次数) |

`order` を **逆順** に見ると「子 → 親」の順になるので、
「子の答えをまとめて親の答えを作る」**木 DP** がループだけで書けます (再帰なし)。

## いつ使う？

- 「N 頂点 N-1 辺」「連結」と書いてある → 木です
- 「部分木の中の〜」「各頂点について、その頂点より下の〜」を求めたい
- 木の各頂点を色で塗り分ける数・選び方の数を数える (隣り合う頂点に条件がある)
- すべての頂点ペアの距離の合計 → 各辺について「その辺を通るペアの数 = size × (N − size)」
- 根から順に値を足していく (親の値を子に伝える) → `order` を **前から** 見る

## しくみ

### 根付き木の用語

```
         0        ← 根 (depth 0)
       / | \
      1  2  3     ← 0 の子 (depth 1)。親は 0
     / \     \
    4   5     6   ← depth 2

  parent[4] = 1,  depth[4] = 2
  1 の部分木 = {1, 4, 5}  → size[1] = 3
  葉 (子がいない頂点) = 4, 5, 2, 6
```

### 非再帰 DFS

スタックを使って根から順に頂点を取り出し、まだ親になっていない隣の頂点を「子」としてスタックに積みます。
取り出した順に `order` に並べるので、**子は必ず親より後** に来ます。

### 木 DP の向き

- **葉 → 根** (部分木の情報を集める): `order` を後ろから見て、自分の値を親に渡す
- **根 → 葉** (上からの情報を伝える): `order` を前から見て、親の値を使って自分の値を決める

部分木のサイズは「葉 → 根」の一番簡単な例です: `size[親] += size[子]`。

### 例: 隣り合う頂点を両方黒にしない塗り方 (EDPC P)

各頂点 v について 2 つの値を持ちます。

- `white[v]` = v を白にしたときの、v の部分木の塗り方の数
- `black[v]` = v を黒にしたときの、v の部分木の塗り方の数

子 c の値が分かれば、親 p の値は掛け算で更新できます。

```
white[p] *= (white[c] + black[c])   // p が白なら、子はどちらでもよい
black[p] *= white[c]                // p が黒なら、子は白だけ
```

## 実装 (Kotlin)

```kotlin
// 根付き木。build() で 親・深さ・部分木サイズ・訪問順 を非再帰 DFS で求める
class RootedTree(val n: Int, val root: Int = 0) {
    val adj = Array(n) { mutableListOf<Int>() }
    val parent = IntArray(n) { -1 }   // 根の親は -1
    val depth = IntArray(n)           // 根の深さは 0
    val size = IntArray(n) { 1 }      // 部分木の頂点数 (自分を含む)
    val order = IntArray(n)           // 訪問順 (行きがけ順)。親は必ず子より前に来る

    fun addEdge(u: Int, v: Int) {
        adj[u].add(v)
        adj[v].add(u)
    }

    // 辺をすべて追加した後に 1 回だけ呼ぶ
    fun build() {
        val stack = IntArray(n)
        var sp = 0
        var idx = 0
        stack[sp++] = root
        while (sp > 0) {
            val v = stack[--sp]
            order[idx++] = v
            for (c in adj[v]) {
                if (c == parent[v]) continue
                parent[c] = v
                depth[c] = depth[v] + 1
                stack[sp++] = c
            }
        }
        // order の後ろ (葉に近い方) から、自分のサイズを親に足す
        for (i in n - 1 downTo 1) {
            val v = order[i]
            size[parent[v]] += size[v]
        }
    }

    // v の子の一覧
    fun children(v: Int): List<Int> = adj[v].filter { it != parent[v] }
}
```

## 使い方

[EDPC P - Independent Set](https://atcoder.jp/contests/dp/tasks/dp_p) を解くコードです。
木の各頂点を白か黒に塗り、隣り合う頂点が両方黒にならない塗り方の数を 10^9+7 で割った余りを出力します。

```kotlin
const val MOD = 1_000_000_007L

fun main() {
    val br = System.`in`.bufferedReader()
    var st = java.util.StringTokenizer("")
    fun next(): String {
        while (!st.hasMoreTokens()) st = java.util.StringTokenizer(br.readLine())
        return st.nextToken()
    }

    val n = next().toInt()
    val tree = RootedTree(n, 0)
    repeat(n - 1) {
        val a = next().toInt() - 1   // 1-indexed → 0-indexed
        val b = next().toInt() - 1
        tree.addEdge(a, b)
    }
    tree.build()

    // white[v] = v を白に塗ったときの、v の部分木の塗り方の数
    // black[v] = v を黒に塗ったときの、v の部分木の塗り方の数
    val white = LongArray(n) { 1L }
    val black = LongArray(n) { 1L }
    // order を逆順に見ると「子 → 親」の順になる。子の値が確定してから親に渡す
    for (i in n - 1 downTo 1) {
        val c = tree.order[i]
        val p = tree.parent[c]
        white[p] = white[p] * ((white[c] + black[c]) % MOD) % MOD  // 親が白 → 子は白でも黒でも OK
        black[p] = black[p] * white[c] % MOD                       // 親が黒 → 子は白だけ
    }
    println((white[tree.root] + black[tree.root]) % MOD)
}
```

入力例

```
3
1 2
2 3
```

出力例

```
5
```

部分木のサイズ・深さ・親が欲しいだけなら、`build()` の後に `tree.size[v]` などを読むだけです。

## 例題

- [EDPC P - Independent Set](https://atcoder.jp/contests/dp/tasks/dp_p) — 上の「使い方」そのもの。白・黒の 2 状態の木 DP
- [ABC 138 D - Ki](https://atcoder.jp/contests/abc138/tasks/abc138_d) — 頂点に足した値を部分木全体に配る。`order` を前から見て `ans[v] += ans[parent[v]]`
- [典型90 039 - Tree Distance](https://atcoder.jp/contests/typical90/tasks/typical90_am) — 全ペアの距離の合計。各辺 (親 p − 子 c) について `size[c] * (N - size[c])` を足す (Long で)

## 注意点・よくあるミス

- **再帰 DFS は StackOverflow に注意**。N = 2×10^5 で一直線の木 (パス) だと再帰が 2×10^5 段になり、落ちることがあります。
  上の実装はスタック配列を使うので安全です。
- 入力が 1-indexed なら `- 1` するのを忘れない。
- 「子 → 親」は **`order` の逆順**、「親 → 子」は **`order` の順** です。向きを間違えると、まだ計算していない値を使ってしまいます。
- `children(v)` は呼ぶたびにリストを作ります。DP では上の例のように「`order` を逆順に見て親に足す」形にすると速くて簡単です。
- 掛け算・足し算の結果が大きくなるときは **Long と mod** を使う。全ペアの距離の合計なども Int を超えやすいです。
- 木でない (閉路がある・非連結) グラフにそのまま使うと、`parent` が正しく決まりません。
