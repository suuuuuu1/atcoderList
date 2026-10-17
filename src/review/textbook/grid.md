---
title: グリッドの扱い方
category: 基本テクニック
acl: -
genres: 二次元グリッド
order: 2070
summary: マス目の入力、上下左右の移動、範囲チェック、1 次元化の書き方
level: 灰
keywords: grid, マス目, 迷路, 盤面, 4方向, 8方向, dx dy, 上下左右, 範囲外, 二次元配列
---

## 何ができる？

`#` と `.` でできた迷路などの **マス目 (グリッド)** を、ミスなく扱うための定番の書き方です。

| 道具 | 内容 | 計算量 |
| --- | --- | --- |
| `Array(h) { readLine()!!.toCharArray() }` | H 行の文字列を 2 次元の文字配列として読む | O(HW) |
| `DI4`, `DJ4` | 上下左右 4 方向への移動量 | - |
| `DI8`, `DJ8` | 斜めも含めた 8 方向への移動量 | - |
| `inGrid(i, j, h, w)` | (i, j) がマス目の中か (範囲チェック) | O(1) |
| `i * w + j` | マス (i, j) を 1 つの番号にする (1 次元化) | O(1) |
| `gridBfs(g, si, sj)` | (si, sj) から各マスへの最短手数 (壁 `#` は通れない) | O(HW) |

## いつ使う？

- 入力が `H W` のあとに `#` `.` の文字列が H 行並んでいる
- 「上下左右に隣接するマスへ移動できる」「周囲 8 マスの爆弾の数」
- 迷路の最短手数 → [BFS](bfs.md) (幅優先探索)。島 (連結成分) の個数 → [BFS](bfs.md) / [DFS](dfs.md) / [Union-Find](dsu.md)
- マスに番号を付けたい (Union-Find やグラフの頂点にしたい) → 1 次元化

## しくみ

### 座標の向き

このページでは **(i, j) = 上から i 行目、左から j 列目** (どちらも 0 始まり) とします。
`g[i][j]` で読めるので、数学の (x, y) より間違えにくいです。

```
        j=0 j=1 j=2 j=3
  i=0    .   .   #   .
  i=1    .   #   .   .
  i=2    .   .   .   #
```

### 4 方向・8 方向

移動量を配列にしておき、for 文で回します。上下左右を 4 回コピペするよりバグが減ります。

```
4 方向            8 方向
     (-1,0)        (-1,-1) (-1,0) (-1,1)
(0,-1)  ●  (0,1)   (0,-1)    ●    (0,1)
     (1,0)         (1,-1)  (1,0)  (1,1)
```

### 範囲チェック

移動先 (ni, nj) が `0 ≤ ni < H` かつ `0 ≤ nj < W` でないと配列外参照になります。
**先に範囲チェック、次に壁チェック** の順に書きます。
(外周がすべて `#` と保証されている問題でも、チェックしておくと安心です。)

### 1 次元化

マス (i, j) に番号 `id = i * W + j` を付けると、0 〜 HW-1 の整数で表せます。
戻すときは `i = id / W`, `j = id % W` です。Union-Find や、頂点番号が必要なグラフで使います。

## 実装 (Kotlin)

```kotlin
// 上, 右, 下, 左 (i は行、j は列の変化量)
val DI4 = intArrayOf(-1, 0, 1, 0)
val DJ4 = intArrayOf(0, 1, 0, -1)

// 8 方向 (斜めを含む)
val DI8 = intArrayOf(-1, -1, -1, 0, 0, 1, 1, 1)
val DJ8 = intArrayOf(-1, 0, 1, -1, 1, -1, 0, 1)

// (i, j) が h 行 w 列のマス目の中にあるか
fun inGrid(i: Int, j: Int, h: Int, w: Int): Boolean = i in 0 until h && j in 0 until w

// (si, sj) から各マスへの最短手数 (上下左右に 1 手で移動、'#' は通れない)。
// 行けないマスは -1
fun gridBfs(g: Array<CharArray>, si: Int, sj: Int): Array<IntArray> {
    val h = g.size
    val w = g[0].size
    val dist = Array(h) { IntArray(w) { -1 } }
    val queue = IntArray(h * w) // マスを 1 次元化した番号 i * w + j を入れる
    var head = 0
    var tail = 0
    dist[si][sj] = 0
    queue[tail++] = si * w + sj
    while (head < tail) {
        val id = queue[head++]
        val i = id / w
        val j = id % w
        for (d in 0 until 4) {
            val ni = i + DI4[d]
            val nj = j + DJ4[d]
            if (!inGrid(ni, nj, h, w)) continue // 先に範囲チェック
            if (g[ni][nj] == '#' || dist[ni][nj] != -1) continue
            dist[ni][nj] = dist[i][j] + 1
            queue[tail++] = ni * w + nj
        }
    }
    return dist
}
```

## 使い方

迷路の最短手数: [ABC 007 C - 幅優先探索](https://atcoder.jp/contests/abc007/tasks/abc007_3) の入力形式
(1 行目 `R C`、2 行目にスタート、3 行目にゴール (1-indexed)、続いて迷路) です。

```kotlin
fun main() {
    val (h, w) = readLine()!!.trim().split(" ").map { it.toInt() }
    val (si, sj) = readLine()!!.trim().split(" ").map { it.toInt() - 1 } // 0-indexed に
    val (gi, gj) = readLine()!!.trim().split(" ").map { it.toInt() - 1 }
    val g = Array(h) { readLine()!!.trim().toCharArray() }
    check(g.all { it.size == w })

    val dist = gridBfs(g, si, sj)
    println(dist[gi][gj])
}
```

入力例

```
4 5
1 1
3 4
.....
.###.
.#...
...#.
```

出力例

```
7
```

8 方向: [ABC 075 B - Minesweeper](https://atcoder.jp/contests/abc075/tasks/abc075_b) のように、
各 `.` を「周囲 8 マスにある `#` の個数」に置き換えます。1 次元化の例もつけています。

```kotlin
fun main() {
    val (h, w) = readLine()!!.trim().split(" ").map { it.toInt() }
    val g = Array(h) { readLine()!!.trim().toCharArray() }

    val sb = StringBuilder()
    for (i in 0 until h) {
        for (j in 0 until w) {
            if (g[i][j] == '#') {
                sb.append('#')
                continue
            }
            var cnt = 0
            for (d in 0 until 8) {
                val ni = i + DI8[d]
                val nj = j + DJ8[d]
                if (inGrid(ni, nj, h, w) && g[ni][nj] == '#') cnt++
            }
            sb.append(cnt)
        }
        sb.appendLine()
    }
    print(sb)

    // 1 次元化: マス (i, j) ⇔ 番号 i * w + j
    val id = 1 * w + 2
    println("${id / w} ${id % w}") // → 1 2
}
```

入力例

```
3 5
.....
.#.#.
.....
```

出力例

```
11211
1#2#1
11211
1 2
```

## 例題

- [ABC 007 C - 幅優先探索](https://atcoder.jp/contests/abc007/tasks/abc007_3) — グリッド BFS の基本 (上の使い方そのもの)
- [ABC 075 B - Minesweeper](https://atcoder.jp/contests/abc075/tasks/abc075_b) — 8 方向と範囲チェックの練習
- [ABC 151 D - Maze Master](https://atcoder.jp/contests/abc151/tasks/abc151_d) — 全マスからグリッド BFS をして最大の最短距離を求める

## 注意点・よくあるミス

- **範囲チェックを先に**。`g[ni][nj] == '#' && inGrid(...)` の順だと、範囲外で例外になります。
- **(i, j) と (x, y) を混ぜない**。問題文が「(x, y) = x 列目、y 行目」のこともあるので、読み込み時に (行, 列) へ直してから使う。
- 入力が **1-indexed** ならスタート・ゴールを `- 1` する。
- `H` と `W` を逆にしがち。`Array(h)` の各要素の長さが `w` です。1 次元化は `i * W + j` (H ではなく W を掛ける)。
- 行の読み込みは `readLine()!!.trim()`。Windows の改行 `\r` が残ると長さが W+1 になります。
- 再帰 DFS は H × W = 10^6 マスだとスタックオーバーフローしやすいので、BFS (キュー) か明示的なスタックで書く。
