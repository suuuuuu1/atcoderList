---
title: 最小全域木 (クラスカル法)
category: グラフ
acl: -
genres: 最小全域木, Union-Find, グラフ
order: 309
summary: 全頂点をつなぐ辺の選び方で、重みの合計が最小のものを求める
level: 緑
keywords: 最小全域木, MST, minimum spanning tree, クラスカル, kruskal, 全域木, 最小コストで全部つなぐ, Union-Find
---

## 何ができる？

無向グラフの辺からいくつかを選んで **全頂点をつなぐ** (連結にする) とき、選んだ辺の **重みの合計が最小** になるものを求めます。
選ばれた辺は必ず木 (N-1 本・サイクルなし) になるので、これを **最小全域木 (MST)** といいます。

| 関数 | 内容 | 計算量 |
| --- | --- | --- |
| `kruskal(n, u, v, w)` | 最小全域木の重みの合計と、使った辺の番号のリストを返す | O(M log M) |
| `DSU` | Union-Find (Union-Find のページと同じ API) | ほぼ O(1) |

グラフが連結でないときは、使った辺が N-1 本未満になります (各連結成分ごとの最小全域木 = 最小全域森)。

## いつ使う？

- 「すべての都市を道路でつなぐ (どの 2 都市も行き来できるようにする) **最小コスト**」
- 「辺をいくつか取り除いて木にする / 連結のまま取り除ける辺の **重みの合計を最大**」→ 全体の合計 − MST
- 「コストが小さい順に辺を見て、つながっていなければつなぐ」という貪欲が思い浮かんだとき
- 頂点どうしの全ペアに辺があるような問題では、辺を減らす工夫 (隣り合うものだけ考えるなど) と組み合わせる

## しくみ (クラスカル法)

**重みの小さい辺から順に見て、「その辺を加えてもサイクルができない (= 両端がまだつながっていない)」なら採用する** だけです。
「つながっているか」の判定と「つなぐ」操作に Union-Find を使います。

```
          2                 辺を重みの小さい順に見る:
    0 --------- 1           1-2:1  採用   {1,2}
    |         / |           0-1:2  採用   {0,1,2}
  3 |    1  /   | 5         0-2:3  不採用 (0 と 2 はもうつながっている = サイクルになる)
    |     /     |           2-3:4  採用   {0,1,2,3}  ← N-1 = 3 本そろった
    2 --------- 3           1-3:5  不採用
          4
                            合計 = 1 + 2 + 4 = 7
```

なぜ貪欲でよいか: 「今つながっていない 2 つのグループを結ぶ辺のうち、一番軽い辺」は、どこかの最小全域木に必ず含まれるからです (カット性)。

## 実装 (Kotlin)

```kotlin
// Union-Find (Union-Find (DSU) のページと同じもの)
class DSU(private val n: Int) {
    // 負なら根で、-値 がグループのサイズ。0 以上なら親の番号
    private val parentOrSize = IntArray(n) { -1 }

    // a と b のグループを合体し、合体後の代表元を返す
    fun merge(a: Int, b: Int): Int {
        require(a in 0 until n && b in 0 until n)
        var x = leader(a)
        var y = leader(b)
        if (x == y) return x
        // 小さい方 (y) を大きい方 (x) の下につける
        if (-parentOrSize[x] < -parentOrSize[y]) { val t = x; x = y; y = t }
        parentOrSize[x] += parentOrSize[y]
        parentOrSize[y] = x
        return x
    }

    fun same(a: Int, b: Int): Boolean {
        require(a in 0 until n && b in 0 until n)
        return leader(a) == leader(b)
    }

    // 代表元を返す (経路圧縮つき。再帰を使わないのでスタックオーバーフローしない)
    fun leader(a: Int): Int {
        require(a in 0 until n)
        var r = a
        while (parentOrSize[r] >= 0) r = parentOrSize[r]
        var x = a
        while (parentOrSize[x] >= 0) {
            val next = parentOrSize[x]
            parentOrSize[x] = r
            x = next
        }
        return r
    }

    fun size(a: Int): Int {
        require(a in 0 until n)
        return -parentOrSize[leader(a)]
    }

    // グループごとに要素を列挙する (各グループ内は番号の昇順)
    fun groups(): List<List<Int>> {
        val result = Array(n) { mutableListOf<Int>() }
        for (i in 0 until n) result[leader(i)].add(i)
        return result.filter { it.isNotEmpty() }
    }
}

// クラスカル法。辺 i は u[i] - v[i] (無向)、重み w[i]
// 戻り値: (最小全域木の重みの合計, 使った辺の番号のリスト)
// 使った辺が n - 1 本未満ならグラフは連結でない
fun kruskal(n: Int, u: IntArray, v: IntArray, w: LongArray): Pair<Long, List<Int>> {
    val m = u.size
    val idx = (0 until m).sortedBy { w[it] } // 辺の番号を重みの小さい順に並べる
    val dsu = DSU(n)
    var total = 0L
    val used = mutableListOf<Int>()
    for (i in idx) {
        if (dsu.same(u[i], v[i])) continue // もうつながっている → 加えるとサイクルになる
        dsu.merge(u[i], v[i])
        total += w[i]
        used.add(i)
    }
    return Pair(total, used)
}
```

## 使い方

入力は `N M` のあとに無向辺 `A_i B_i C_i` (1-indexed、重み C_i) が M 行。
最小全域木の重みの合計を出力します。全頂点をつなげないなら `-1`。

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
    val u = IntArray(m)
    val v = IntArray(m)
    val w = LongArray(m)
    for (i in 0 until m) {
        u[i] = next().toInt() - 1
        v[i] = next().toInt() - 1
        w[i] = next().toLong()
    }

    val (total, used) = kruskal(n, u, v, w)
    if (used.size < n - 1) {
        println(-1) // 連結でない
    } else {
        println(total)
        // 使った辺の番号 (入力の何行目か、1-indexed)
        println(used.joinToString(" ") { (it + 1).toString() })
    }
}
```

入力例

```
4 5
1 2 2
2 3 1
1 3 3
3 4 4
2 4 5
```

出力例

```
7
2 1 4
```

## 例題

- [ABC 218 E - Destruction](https://atcoder.jp/contests/abc218/tasks/abc218_e) — 連結のまま取り除く辺の報酬を最大化。報酬が負の辺は全部残す (最初から merge) のがポイント。答え = 全体 − MST
- [ABC 065 D - Built?](https://atcoder.jp/contests/abc065/tasks/arc076_b) — 全ペアの辺は多すぎるので、x 座標順・y 座標順で隣り合う町の間の辺だけ (2(N-1) 本) を候補にしてクラスカル

## 注意点・よくあるミス

- 最小全域木は **無向グラフ** の話です。有向グラフでは使えません。
- 重みの合計は `Int` を超えやすいので `Long`。
- グラフが連結とは限らない問題では、使った辺の本数 (`used.size == n - 1`) で連結かどうかを確認する。
- 重みが負の辺があってもクラスカル法はそのまま使えます (小さい順に見るだけなので)。
- 「最大全域木」が欲しいときは、重みの大きい順に並べる (`sortedByDescending`) か、重みを `-w` にする。
- 辺が多すぎる (全ペア N² 本など) ときは、そのままでは TLE / MLE。必要な辺だけに絞る工夫が必要です。
- Union-Find の使い方は「Union-Find (DSU)」のページも参照。
