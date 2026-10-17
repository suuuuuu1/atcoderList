---
title: 2-SAT
category: グラフ
acl: two_sat
genres: グラフ, 2-SAT
order: 340
summary: 「A または B」形式の条件をすべて満たす真偽の割り当てを探す
level: 青
keywords: 2-SAT, 充足可能性, 真偽, 条件, 論理式
---

## 何ができる？

true / false の変数 x₀, x₁, ..., x_{n-1} があり、
「**(x_i == f) または (x_j == g)**」という形の条件がたくさん与えられたとき、
**すべての条件を同時に満たす割り当てがあるか** を判定し、あれば 1 つ求めます。

| 操作 | 内容 | 計算量 |
|---|---|---|
| `TwoSat(n)` | 変数 `n` 個で作る | O(n) |
| `addClause(i, f, j, g)` | 条件「`x_i == f` または `x_j == g`」を追加 | O(1) |
| `satisfiable()` | 全部満たせるなら `true` | O(n + m) |
| `answer()` | 最後の `satisfiable()` が `true` のときの割り当て (`BooleanArray`) | O(1) |

m は条件の個数です。中身は強連結成分分解 (SCC) なので、`SccGraph` も一緒に貼ります。

## いつ使う？

- 各要素について **2 択** (置く / 置かない、左 / 右、X_i / Y_i、表 / 裏) を選ぶ
- 制約が「**2 つの要素の組** についての禁止条件」だけで書ける
  - 「i と j を両方 true にしてはいけない」 → `addClause(i, false, j, false)`
  - 「i が true なら j も true」 → `addClause(i, false, j, true)`
  - 「i は必ず true」 → `addClause(i, true, i, true)`
- 3 つ以上の変数が絡む条件 (3-SAT) は一般には解けないので、2 つに分解できるか考える
- 「最小値を最大化」などは **二分探索 + 2-SAT** になることも多い

よく使う条件の書き方:

| 条件 | 書き方 |
|---|---|
| x_i または x_j | `addClause(i, true, j, true)` |
| x_i と x_j の両方が true は禁止 | `addClause(i, false, j, false)` |
| x_i ならば x_j | `addClause(i, false, j, true)` |
| x_i == x_j | `addClause(i, false, j, true)` と `addClause(i, true, j, false)` |
| x_i != x_j | `addClause(i, true, j, true)` と `addClause(i, false, j, false)` |
| x_i は true に固定 | `addClause(i, true, i, true)` |

## しくみ

### 「または」を「ならば」に直す

「A または B」は、次の 2 つと同じ意味です。

- A でないなら、B
- B でないなら、A

そこで、各変数について 2 つの頂点「x_i = true」「x_i = false」を作り、
「ならば」を有向辺にしたグラフ (含意グラフ) を作ります。

```
条件: (x0 == true) または (x1 == false)

  [x0 = false] ──> [x1 = false]      x0 が false なら x1 は false でなければならない
  [x1 = true ] ──> [x0 = true ]      x1 が true  なら x0 は true でなければならない
```

### 強連結成分で判定する

辺をたどれる = 「左が成り立つなら右も成り立つ」です。
もし「x_i = true」と「x_i = false」が **同じ強連結成分** にあると、
「true なら false」「false なら true」となり矛盾します → 満たせない。

逆に、どの i でも 2 つが別の成分なら必ず満たせます。
割り当ては「**トポロジカル順で後ろにある方を選ぶ**」とすればよいことが知られています。
(後ろの方からは前の方へ戻れないので、「選んだものから矛盾が導かれる」ことがない。)

この実装では頂点 `2i` が「x_i = false」、`2i+1` が「x_i = true」で、
`id[2i] < id[2i+1]` (true の方が後ろ) なら x_i = true にしています。

## 実装 (Kotlin)

```kotlin
// 2-SAT。ACL の two_sat に対応 (下の SccGraph も一緒に貼ること)
class TwoSat(private val n: Int) {
    private val ans = BooleanArray(n)
    private val scc = SccGraph(2 * n)
    // 頂点 2i   : 「x_i = false」
    // 頂点 2i+1 : 「x_i = true」

    // 条件「(x_i == f) または (x_j == g)」を追加する
    fun addClause(i: Int, f: Boolean, j: Int, g: Boolean) {
        require(i in 0 until n && j in 0 until n)
        // x_i != f ならば x_j == g
        scc.addEdge(2 * i + (if (f) 0 else 1), 2 * j + (if (g) 1 else 0))
        // x_j != g ならば x_i == f
        scc.addEdge(2 * j + (if (g) 0 else 1), 2 * i + (if (f) 1 else 0))
    }

    // すべての条件を満たす割り当てがあるか
    fun satisfiable(): Boolean {
        val id = scc.sccIds().second
        for (i in 0 until n) {
            if (id[2 * i] == id[2 * i + 1]) return false // x_i と ¬x_i が同じ成分 → 矛盾
            ans[i] = id[2 * i] < id[2 * i + 1]           // トポロジカル順で後ろの方を採用
        }
        return true
    }

    // 最後に呼んだ satisfiable() が true のときの割り当て
    fun answer(): BooleanArray = ans
}

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

AtCoder Library Practice Contest H「Two SAT」を解く例です。
x_i = false なら旗 i を X_i に、true なら Y_i に置くことにして、
「距離が D 未満になる 2 本の置き方」をすべて禁止します。

```kotlin
// practice2_H: 旗 i を X_i か Y_i に置く。どの 2 本の距離も D 以上にできるか
fun main() {
    val st = java.io.StreamTokenizer(System.`in`.bufferedReader())
    fun ni(): Int { st.nextToken(); return st.nval.toInt() }

    val n = ni()
    val d = ni()
    // pos[i][0] = X_i (x_i = false のとき), pos[i][1] = Y_i (x_i = true のとき)
    val pos = Array(n) { intArrayOf(ni(), ni()) }
    val ts = TwoSat(n)
    for (i in 0 until n) for (j in i + 1 until n) {
        for (a in 0..1) for (b in 0..1) {
            if (Math.abs(pos[i][a] - pos[j][b]) < d) {
                // 「旗 i が a 側 かつ 旗 j が b 側」は禁止
                // → 「x_i != a または x_j != b」
                ts.addClause(i, a == 0, j, b == 0)
            }
        }
    }
    if (!ts.satisfiable()) {
        println("No")
        return
    }
    val ans = ts.answer()
    val sb = StringBuilder()
    sb.appendLine("Yes")
    for (i in 0 until n) sb.appendLine(pos[i][if (ans[i]) 1 else 0])
    print(sb)
}
```

入力
```
3 2
1 4
2 5
0 6
```
出力 (置き方は一例)
```
Yes
4
2
0
```

## 例題

- [AtCoder Library Practice Contest H - Two SAT](https://atcoder.jp/contests/practice2/tasks/practice2_h)
- [ARC 069 F - Flags](https://atcoder.jp/contests/arc069/tasks/arc069_d) (上の問題の「D を最大化」版。二分探索 + 2-SAT。辺が多すぎるのでセグメント木の形で辺を張る工夫が必要な難問)

## 注意点・よくあるミス

- **`addClause` の `f`, `g` は「その値なら条件 OK」という意味**。「禁止」の条件を書くときは true/false を反転させる。
  (「x_i == a かつ x_j == b は禁止」→ `addClause(i, !a, j, !b)`)
- `answer()` は **`satisfiable()` が `true` を返した後** にだけ意味がある。
- 頂点数は変数の 2 倍、辺数は条件の 2 倍になる。条件が O(n²) 個になる問題は n が数千までが目安。
- 中で使う `SccGraph` は非再帰なのでスタックオーバーフローしません。
  自分で再帰版の SCC を書く場合は `Thread(null, { solve() }, "", 1L shl 27).start()` でスタックを増やすこと。
- 「どちらでもよい」変数も `answer()` には true/false のどちらかが入る。
