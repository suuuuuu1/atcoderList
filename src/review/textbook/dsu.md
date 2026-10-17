---
title: Union-Find (DSU)
category: データ構造
acl: dsu
genres: Union-Find, グラフ
order: 110
summary: 「同じグループか？」の判定とグループの合体を高速に行う
---

## 何ができる？

N 個の要素 (頂点) 0, 1, ..., N-1 を「グループ分け」して管理するデータ構造です。
最初は全員がバラバラ (N 個のグループ) で、「2 つのグループをくっつける」操作だけができます。

| 操作 | 内容 | 計算量 |
| --- | --- | --- |
| `DSU(n)` | 要素 0〜n-1 を、それぞれ 1 人ずつのグループとして作る | O(n) |
| `merge(a, b)` | a のグループと b のグループを合体する。合体後の代表元を返す | ほぼ O(1) ※ |
| `same(a, b)` | a と b が同じグループなら `true` | ほぼ O(1) ※ |
| `leader(a)` | a のグループの代表元 (リーダー) を返す | ほぼ O(1) ※ |
| `size(a)` | a のグループの要素数を返す | ほぼ O(1) ※ |
| `groups()` | グループ分けの結果を `List<List<Int>>` で返す | O(n) |

※ 正確には償却 O(α(n))。α はアッカーマン関数の逆関数で、現実的な n では 4 以下なので定数と思って OK です。

**できないこと**: グループを「分ける」(辺を消す) 操作はできません。

## いつ使う？

- 「A さんと B さんは友達」「頂点 a と b を辺で結ぶ」のような **つなげる操作** が次々に来て、
  「x と y は (間接的にでも) つながっている？」を何度も聞かれる
- **連結成分の個数** や **各連結成分の大きさ** を知りたい
- グラフの辺を 1 本ずつ追加していく問題 (クラスカル法で最小全域木を作るときにも使う)
- 「辺を削除していく」問題 → **逆順に見て辺を追加する問題** に言い換えると DSU が使えることが多い
- BFS / DFS でもできるけど、クエリのたびにやり直すと間に合わない…というとき

## しくみ

各グループを **根付き木** で表し、木の根を「代表元 (leader)」とします。
「同じグループか？」は「根が同じか？」で判定します。

```
最初: 0  1  2  3  4  5     (全員が自分自身の根)

merge(0, 1), merge(2, 3), merge(1, 3) の後:

      0          4    5
     / \
    1   2
        |
        3
  → 0,1,2,3 は同じグループ (根は 0)。same(1, 3) は true
```

- **leader(a)**: a から親をたどって根まで登ります。
- **merge(a, b)**: a の根と b の根を求め、片方の根をもう片方の根の子にします。

このままだと木が一直線に伸びて遅くなることがあるので、2 つの工夫をします。

1. **union by size**: 合体するとき、**小さいグループを大きいグループの下につける**。
   木の高さが O(log n) に抑えられます。
2. **経路圧縮**: leader(a) で根を見つけたら、途中で通った頂点を全部 **根の直下につなぎ直す**。
   次からは 1 歩で根に着きます。

```
経路圧縮の前        leader(3) の後
    0                   0
    |                 / | \
    1                1  2  3
    |
    2
    |
    3
```

この 2 つを両方使うと、1 回あたりほぼ定数時間になります。

実装では配列 1 本 `parentOrSize` で親とサイズを兼用します。

- `parentOrSize[i] >= 0` … i は根ではなく、値は親の番号
- `parentOrSize[i] < 0`  … i は根で、`-parentOrSize[i]` がグループの要素数

## 実装 (Kotlin)

```kotlin
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
```

## 使い方

[practice2 A - Disjoint Set Union](https://atcoder.jp/contests/practice2/tasks/practice2_a) を解くコードです。
`0 u v` なら u と v を結び、`1 u v` なら u と v が連結なら 1、そうでなければ 0 を出力します。

```kotlin
fun main() {
    val br = System.`in`.bufferedReader()
    var st = java.util.StringTokenizer("")
    fun next(): String {
        while (!st.hasMoreTokens()) st = java.util.StringTokenizer(br.readLine())
        return st.nextToken()
    }

    val n = next().toInt()
    val q = next().toInt()
    val dsu = DSU(n)
    val sb = StringBuilder()
    repeat(q) {
        val t = next().toInt()
        val u = next().toInt()
        val v = next().toInt()
        if (t == 0) {
            dsu.merge(u, v)
        } else {
            sb.appendLine(if (dsu.same(u, v)) 1 else 0)
        }
    }
    print(sb)

    // おまけ: 連結成分の個数と、それぞれの大きさ
    // val gs = dsu.groups()
    // println(gs.size)                    // 連結成分の個数
    // for (g in gs) println(g.size)       // dsu.size(g[0]) と同じ
}
```

入力例

```
4 7
1 0 1
0 0 1
0 2 3
1 0 1
1 1 2
0 0 2
1 1 3
```

出力例

```
0
1
0
1
```

## 例題

- [practice2 A - Disjoint Set Union](https://atcoder.jp/contests/practice2/tasks/practice2_a) — そのまま使うだけの練習問題
- [ABC 177 D - Friends](https://atcoder.jp/contests/abc177/tasks/abc177_d) — 答えは「一番大きいグループのサイズ」。`size` を使う
- [ABC 120 D - Decayed Bridges](https://atcoder.jp/contests/abc120/tasks/abc120_d) — 橋が壊れていく → 逆順に見て橋を架けていく。merge の前に 2 つのサイズの積を足す

## 注意点・よくあるミス

- **入力が 1-indexed なら `- 1` する** のを忘れない。`DSU(n)` は 0〜n-1 しか扱えません。
- 「同じグループか」を `parent[a] == parent[b]` で比べてはダメ。必ず `leader` / `same` を使う
  (親が根とは限らないため)。
- `merge` で代表元が変わることがあります。`leader(a)` の値を変数に保存したまま merge すると、古い値になります。
- 連結成分の個数は `groups().size` でも求まりますが O(n) かかります。何度も必要なら
  「最初は n、merge で実際にくっついたら (merge 前に `same` が false なら) 1 減らす」と数えるほうが速いです。
- グループを分ける (辺を消す) 操作はできません。削除が来る問題は **クエリを逆順に処理** できないか考えましょう。
- 経路圧縮を再帰で書くと、N = 2×10^5 程度で一直線の木ができたときに StackOverflow することがあります。
  上の実装はループなので安全です。
