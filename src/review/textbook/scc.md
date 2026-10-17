---
title: 強連結成分分解 (SCC)
category: グラフ
acl: scc
genres: グラフ, DFS, 強連結成分分解, トポロジカルソート
order: 330
summary: 有向グラフを「互いに行き来できる頂点のグループ」に分ける
level: 水
keywords: 強連結成分, SCC, 有向グラフ, サイクル, 縮約, DAG
---

## 何ができる？

有向グラフで、**u から v へも v から u へも行ける** 頂点どうしを同じグループにまとめます。
このグループを **強連結成分 (SCC)** といいます。
さらに、成分を **トポロジカル順** (矢印が前から後ろへ向く順) に並べて返します。

| 操作 | 内容 | 計算量 |
|---|---|---|
| `SccGraph(n)` | 頂点 `n` 個・辺なしの有向グラフを作る | O(n) |
| `addEdge(from, to)` | 有向辺 `from → to` を追加 | O(1) |
| `scc()` | 強連結成分のリスト (トポロジカル順) を返す | O(n + m) |
| `sccIds()` | `(成分の個数, 各頂点の成分番号)` を返す (番号もトポロジカル順) | O(n + m) |

`scc()` の結果では、成分 A から成分 B へ辺があるなら、**A は B より前** に来ます (ACL と同じ)。

## いつ使う？

- 有向グラフで「**行って戻ってこられる**」「**サイクル上にある**」かを問われる
- 有向グラフに **サイクルがあるかもしれない** けど、DAG にして DP やトポロジカルソートをしたい
  → 成分を 1 つの頂点に縮めると DAG になる
- 「無限に移動し続けられる頂点は？」「どこからでも到達できる頂点は？」
- **2-SAT** (2-SAT の中身は SCC)
- 関数グラフ (各頂点から辺が 1 本) のサイクル検出にも使える

## しくみ

### 成分を縮めると DAG になる

```
  元のグラフ: 1 → 4, 4 → 1, 0 → 3, 3 → 0, 5 → 2, 4 → 2, 5 → 5

  成分ごとに縮めたグラフ (DAG):

     {5} ----+
             +---> {2}          {0, 3}  (どこともつながっていない)
   {1, 4} ---+
```

同じ成分の中はぐるぐる回れるので 1 つの頂点とみなせます。成分どうしの間にはサイクルがない (あったら同じ成分になる) ので、縮めると必ず DAG になります。

### Tarjan のアルゴリズム (この実装)

DFS を 1 回するだけで求めます。

1. DFS で訪れた順に番号 `ord[v]` を付け、頂点をスタック `visited` に積む
2. `low[v]` = 「v の子孫から、まだ成分が決まっていない頂点へ辺を 1 本たどって行ける頂点の `ord` の最小値」
3. v の探索が終わったとき `low[v] == ord[v]` なら、v は成分の「入口 (根)」。
   スタックから v までを取り出して 1 つの成分にする

成分は「後ろ (行き止まり側) のものから」確定するので、確定した順番は逆トポロジカル順です。
最後に番号を反転して、トポロジカル順にしています。

### 非再帰にする

DFS をそのまま再帰で書くと、頂点数 5×10⁵ の一直線のグラフなどで **スタックオーバーフロー** します。
この実装では、「今どの頂点のどの辺まで見たか」(`itr`) と「呼び出し中の頂点のスタック」(`cs`) を自分で管理して、再帰を使わずに書いています。

## 実装 (Kotlin)

```kotlin
// 強連結成分分解 (Tarjan 法・非再帰)。ACL の scc_graph に対応
class SccGraph(private val n: Int) {
    private val from = ArrayList<Int>()
    private val to = ArrayList<Int>()

    fun addEdge(from: Int, to: Int) {
        require(from in 0 until n && to in 0 until n)
        this.from.add(from)
        this.to.add(to)
    }

    // (成分の個数, 各頂点の成分番号) を返す。成分番号はトポロジカル順
    fun sccIds(): Pair<Int, IntArray> {
        // 隣接リストを CSR 形式 (start, elist) で作る
        val start = IntArray(n + 1)
        for (f in from) start[f + 1]++
        for (i in 0 until n) start[i + 1] += start[i]
        val elist = IntArray(from.size)
        val fill = start.copyOf()
        for (i in from.indices) elist[fill[from[i]]++] = to[i]

        var nowOrd = 0
        var groupNum = 0
        val visited = IntArray(n) // まだ成分が決まっていない頂点のスタック
        var vsz = 0
        val low = IntArray(n)
        val ord = IntArray(n) { -1 }
        val ids = IntArray(n)
        val itr = IntArray(n)     // 各頂点で次に見る辺の位置
        val cs = IntArray(n)      // 再帰の代わりのスタック
        var csz = 0

        for (root in 0 until n) {
            if (ord[root] != -1) continue
            ord[root] = nowOrd; low[root] = nowOrd; nowOrd++
            visited[vsz++] = root
            itr[root] = start[root]
            cs[csz++] = root
            while (csz > 0) {
                val v = cs[csz - 1]
                if (itr[v] < start[v + 1]) {
                    val u = elist[itr[v]++]
                    if (ord[u] == -1) {
                        // u へ「再帰呼び出し」
                        ord[u] = nowOrd; low[u] = nowOrd; nowOrd++
                        visited[vsz++] = u
                        itr[u] = start[u]
                        cs[csz++] = u
                    } else {
                        low[v] = minOf(low[v], ord[u])
                    }
                } else {
                    // v の処理が終わった (「return」)
                    csz--
                    if (low[v] == ord[v]) {
                        // v が成分の根: スタックから v までを 1 つの成分にする
                        while (true) {
                            val w = visited[--vsz]
                            ord[w] = n // 以後 low の計算に影響しないように
                            ids[w] = groupNum
                            if (w == v) break
                        }
                        groupNum++
                    }
                    if (csz > 0) {
                        val p = cs[csz - 1]
                        low[p] = minOf(low[p], low[v])
                    }
                }
            }
        }
        // Tarjan は逆トポロジカル順に番号が付くので反転する
        for (i in 0 until n) ids[i] = groupNum - 1 - ids[i]
        return groupNum to ids
    }

    // 強連結成分のリストをトポロジカル順に返す
    // (成分 A から成分 B へ辺があれば、A は B より前)
    fun scc(): List<List<Int>> {
        val (num, ids) = sccIds()
        val groups = List(num) { ArrayList<Int>() }
        for (i in 0 until n) groups[ids[i]].add(i)
        return groups
    }
}
```

## 使い方

AtCoder Library Practice Contest G「SCC」を解く例です。

```kotlin
// practice2_G: 強連結成分をトポロジカル順に出力
fun main() {
    val st = java.io.StreamTokenizer(System.`in`.bufferedReader())
    fun ni(): Int { st.nextToken(); return st.nval.toInt() }

    val n = ni()
    val m = ni()
    val g = SccGraph(n)
    repeat(m) {
        val a = ni()
        val b = ni()
        g.addEdge(a, b)
    }
    val groups = g.scc()

    val sb = StringBuilder()
    sb.appendLine(groups.size)
    for (comp in groups) {
        sb.append(comp.size)
        for (v in comp) sb.append(' ').append(v)
        sb.appendLine()
    }
    print(sb)
}
```

入力
```
6 7
1 4
5 2
3 0
5 5
4 1
0 3
4 2
```
出力 (成分の中の順番や、関係のない成分どうしの順番は違ってもよい)
```
4
1 5
2 1 4
1 2
2 0 3
```

成分を縮めた DAG を作るときは、`sccIds()` を使って次のようにします (`edges` は辺 `(a, b)` の `List<Pair<Int, Int>>`)。

```kotlin
val (num, ids) = g.sccIds()
val dag = List(num) { ArrayList<Int>() }
for ((a, b) in edges) if (ids[a] != ids[b]) dag[ids[a]].add(ids[b])
// 成分番号 0, 1, 2, ... の順がそのままトポロジカル順なので、この順に DP できる
```

## 例題

- [AtCoder Library Practice Contest G - SCC](https://atcoder.jp/contests/practice2/tasks/practice2_g)
- [ABC 245 F - Endless Walk](https://atcoder.jp/contests/abc245/tasks/abc245_f) (サイズ 2 以上の成分 (や自己ループ) に到達できる頂点を数える)
- [ABC 296 E - Transition Game](https://atcoder.jp/contests/abc296/tasks/abc296_e) (関数グラフでサイクル上の頂点を数える)

## 注意点・よくあるミス

- **再帰 DFS のスタックオーバーフロー** に注意。この実装は非再帰です。
  自分で再帰で書く場合は、`Thread(null, { solve() }, "", 1L shl 27).start()` のように
  スタックサイズを大きくしたスレッドの中で実行してください。
- 成分の並びは **トポロジカル順** (矢印の元が前)。逆向きに処理したい (DP を後ろから) ときは `reversed()` する。
- **自己ループ** (`v → v`) があっても成分のサイズは 1 のまま。「サイクルがあるか」を判定するときは、
  「サイズ 2 以上」または「自己ループがある」の両方をチェックする。
- 縮めた DAG には同じ辺が何本も入ることがある。必要なら重複を除く。
- 入力が 1-indexed なら `-1` を忘れずに。
