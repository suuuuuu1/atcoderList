---
title: ワーシャルフロイド法
category: グラフ
acl: -
genres: ワーシャルフロイド, グラフ
order: 307
summary: 全頂点ペア間の最短距離を三重ループで求める
level: 緑
keywords: ワーシャルフロイド, warshall floyd, floyd warshall, 全点対最短経路, 全頂点間, 最短距離, 隣接行列, 三重ループ
---

## 何ができる？

**すべての頂点の組 (i, j)** について、i から j への最短距離をまとめて求めます。
コードは三重ループだけでとても短く、負の重みの辺があっても (負閉路がなければ) 使えます。

| 関数 | 内容 | 計算量 |
| --- | --- | --- |
| `warshallFloyd(d)` | 隣接行列 `d` を、その場で「全頂点間の最短距離」に書き換える。届かない組は `INF` | O(N³) |
| `hasNegativeCycle(d)` | 実行後の `d` で `d[i][i] < 0` となる i があれば負閉路あり | O(N) |

## いつ使う？

- **N が小さい** (N ≤ 400 程度。N³ = 6.4×10⁷) とき
- 「すべての 2 都市間の距離」「どの 2 頂点を選んでも…」のように、**いろいろな始点からの距離** が必要
- 「頂点 k を経由してよい」を k = 1, 2, ... と増やしていく問題 (ループの途中経過を使う)
- 全頂点間の「行けるかどうか」(推移閉包) も同じ形で求まる
- N が大きくて始点が 1 つならダイクストラ (負の辺があればベルマンフォード)

## しくみ

`d[i][j]` = 「i から j への辺の重み (なければ INF、`d[i][i] = 0`)」から始めて、

```
for k in 0 until n:          ← 「経由してよい頂点」に k を追加する
    for i in 0 until n:
        for j in 0 until n:
            d[i][j] = min(d[i][j], d[i][k] + d[k][j])   ← i → k → j のほうが近ければ更新
```

とするだけです。k のループが終わった時点で、`d[i][j]` は「**途中で 0〜k の頂点だけを通ってよい** ときの最短距離」になっています。
最後の k まで終われば、どの頂点を通ってもよい = 本当の最短距離です。

```
  i ----------------> j     今の d[i][j]
   \                 ^
    \--> k ---------/       d[i][k] + d[k][j] のほうが短ければ置き換える
```

**ループの順番は k が一番外側**。ここを間違えると正しく求まりません。

負閉路があると、それに含まれる頂点 i で `d[i][i]` が負になるので検出できます。

## 実装 (Kotlin)

```kotlin
const val INF = Long.MAX_VALUE / 4

// d[i][j] = i から j への辺の重み (辺がなければ INF、d[i][i] = 0) を渡す
// 実行後、d[i][j] は i から j への最短距離 (届かなければ INF) になる
fun warshallFloyd(d: Array<LongArray>) {
    val n = d.size
    for (k in 0 until n) {      // k が一番外側
        for (i in 0 until n) {
            if (d[i][k] == INF) continue // i から k に行けないなら k 経由は無意味
            for (j in 0 until n) {
                if (d[k][j] == INF) continue
                val nd = d[i][k] + d[k][j]
                if (nd < d[i][j]) d[i][j] = nd
            }
        }
    }
}

// warshallFloyd のあとに呼ぶ。負閉路があれば true
fun hasNegativeCycle(d: Array<LongArray>): Boolean = d.indices.any { d[it][it] < 0 }
```

## 使い方

入力は `N M` のあとに有向辺 `A_i B_i C_i` (1-indexed、重み C_i) が M 行。
全頂点間の最短距離を表で出力します (届かなければ `INF`、負閉路があれば `NEGATIVE CYCLE`)。

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
    val d = Array(n) { LongArray(n) { INF } }
    for (i in 0 until n) d[i][i] = 0L
    repeat(m) {
        val a = next().toInt() - 1
        val b = next().toInt() - 1
        val c = next().toLong()
        d[a][b] = minOf(d[a][b], c) // 多重辺対策で小さいほうを残す
        // 無向グラフなら d[b][a] = minOf(d[b][a], c) も
    }

    warshallFloyd(d)
    if (hasNegativeCycle(d)) {
        println("NEGATIVE CYCLE")
        return
    }
    val sb = StringBuilder()
    for (i in 0 until n) {
        sb.appendLine((0 until n).joinToString(" ") { j -> if (d[i][j] == INF) "INF" else d[i][j].toString() })
    }
    print(sb)
}
```

入力例

```
4 6
1 2 1
1 3 5
2 3 2
3 4 1
2 4 6
4 2 -1
```

出力例

```
0 1 3 4
INF 0 2 3
INF 0 0 1
INF -1 1 0
```

## 例題

- [ABC 012 D - バスと避けられない運命](https://atcoder.jp/contests/abc012/tasks/abc012_4) — 全頂点間の距離を求め、「一番遠いバス停までの距離」が最小になる頂点を選ぶ
- [ABC 074 D - Restoring Road Network](https://atcoder.jp/contests/abc074/tasks/arc083_b) — 最短距離の表から、必要な道路だけを残す。ワーシャルフロイドの考え方 (k 経由) を使う
- [ABC 208 D - Shortest Path Queries 2](https://atcoder.jp/contests/abc208/tasks/abc208_d) — 「経由してよい頂点が 1〜k」の途中経過をそのまま使う。ループの意味が分かっていれば解ける

## 注意点・よくあるミス

- **k のループを一番外側に**。`for (i) for (j) for (k)` の順にすると間違った答えになります。
- `d[i][i] = 0` の初期化を忘れない。
- 多重辺があるときは `minOf` で小さいほうを残す。無向グラフなら `d[a][b]` と `d[b][a]` の両方を入れる。
- INF どうしを足すとオーバーフローするので、INF を `Long.MAX_VALUE / 4` にするか、上の実装のように INF なら飛ばす。
  負の辺があるときは「INF + 負の数」が INF より小さくなってしまうので、**INF なら飛ばす** 書き方が安全です。
- N = 500 で約 1.25×10⁸ 回のループ。Kotlin でも 1 秒程度で動きますが、N が 1000 を超えるなら別の方法を考える。
- メモリは N² 個の Long。N = 3000 でも 72MB ほど使うので注意。
