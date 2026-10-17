---
title: いもす法
category: 基本テクニック
acl: -
genres: いもす法, 累積和
order: 2020
summary: 区間に数を足す操作をまとめて行い、最後に一気に結果を出す
level: 緑
keywords: imos, 差分配列, difference array, 区間加算, 重なり, 同時に何人, 塗り重ね, 二次元いもす
---

## 何ができる？

「区間 [l, r) に x を足す」という操作を **たくさん** 行ったあと、各点の値を **まとめて** 求めます。
(累積和の逆向きの使い方です。)

| 操作 | 内容 | 計算量 |
| --- | --- | --- |
| `Imos(n)` | 長さ n の 0 の配列を用意する | O(n) |
| `add(l, r, x)` | 区間 [l, r) に x を足す (予約するだけ) | O(1) |
| `build()` | すべての add を反映した長さ n の配列を返す | O(n) |
| `Imos2D(h, w)` | h × w の 0 の 2 次元配列を用意する | O(hw) |
| `add(r1, c1, r2, c2, x)` | 行 [r1, r2) × 列 [c1, c2) に x を足す | O(1) |
| `build()` | すべての add を反映した h × w の配列を返す | O(hw) |

**できないこと**: 「足す」と「値を聞く」が交互に来る問題 (オンライン) には向きません。
その場合は [Fenwick Tree (BIT)](fenwicktree.md) や [遅延セグメント木](lazysegtree.md) を使います。

## いつ使う？

- 「l 日目から r 日目まで x 円かかる」「時刻 s から t まで店にいる」が N 件あって、
  **各日 (各時刻) の合計** や **同時にいる人数の最大** を求める
- 「長方形の紙を何枚も重ねて置く。k 枚重なっている部分の面積は？」(2 次元いもす)
- 区間に足す処理を毎回 for 文で書くと O(N × 区間の長さ) で間に合わないとき
- 座標が 10^9 のように大きいときは、[座標圧縮](coordinate-compression.md) と組み合わせるか、
  「(座標, 変化量)」のイベントをソートして順に足す

## しくみ

区間 [l, r) に x を足すとき、**入口 l に +x、出口 r に -x** を書いておくだけにします。
最後に左から累積和をとると、各点の値になります。

```
[1, 4) に +2、[3, 6) に +1 (長さ 6)

差分 d:   index  0   1   2   3   4   5   6
               0  +2   0  +1  -2   0  -1
累積和:         0   2   2   3   1   1   (最後の 6 は使わない)
```

1 回の add は 2 か所に書くだけなので O(1)。最後の累積和で O(n) です。

**2 次元** では長方形の 4 隅に書き込みます。

```
       c1    c2
r1    +x    -x
r2    -x    +x
```

この後「横方向に累積和」→「縦方向に累積和」の順にとると、長方形の中だけが +x になります。

## 実装 (Kotlin)

```kotlin
// 1 次元いもす法。区間 [l, r) への加算をまとめて行う
class Imos(private val n: Int) {
    private val d = LongArray(n + 1)

    // 区間 [l, r) に x を足す
    fun add(l: Int, r: Int, x: Long) {
        require(0 <= l && l <= r && r <= n)
        d[l] += x
        d[r] -= x
    }

    // すべての add を反映した長さ n の配列を返す
    fun build(): LongArray {
        val res = LongArray(n)
        var cur = 0L
        for (i in 0 until n) {
            cur += d[i]
            res[i] = cur
        }
        return res
    }
}

// 2 次元いもす法。長方形 (行 [r1, r2) × 列 [c1, c2)) への加算をまとめて行う
class Imos2D(private val h: Int, private val w: Int) {
    private val d = Array(h + 1) { LongArray(w + 1) }

    fun add(r1: Int, c1: Int, r2: Int, c2: Int, x: Long) {
        require(0 <= r1 && r1 <= r2 && r2 <= h && 0 <= c1 && c1 <= c2 && c2 <= w)
        d[r1][c1] += x
        d[r1][c2] -= x
        d[r2][c1] -= x
        d[r2][c2] += x
    }

    // すべての add を反映した h × w の配列を返す
    fun build(): Array<LongArray> {
        val s = Array(h + 1) { d[it].copyOf() }
        for (i in 0..h) for (j in 1..w) s[i][j] += s[i][j - 1] // 横に累積和
        for (i in 1..h) for (j in 0..w) s[i][j] += s[i - 1][j] // 縦に累積和
        return Array(h) { s[it].copyOf(w) }
    }
}
```

## 使い方

1 次元: N 日間で、M 件の予定 `L R X` (L 日目〜R 日目に X 人来る。1-indexed、両端を含む) があります。
各日の人数と、人数の最大値を出力します。

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
    val imos = Imos(n)
    repeat(m) {
        val l = next().toInt() - 1 // 0-indexed に
        val r = next().toInt()     // R 日目を含む → 半開区間の右端は R
        val x = next().toLong()
        imos.add(l, r, x)
    }
    val people = imos.build()
    println(people.joinToString(" "))
    println(people.max())
}
```

入力例

```
6 3
2 4 2
4 6 1
1 1 5
```

出力例

```
5 2 2 3 1 1
5
```

2 次元: [典型90 028 - Cluttered Paper](https://atcoder.jp/contests/typical90/tasks/typical90_ab) の解き方です。
N 枚の紙 `lx ly rx ry` (0 ≤ 座標 ≤ 1000) が置かれ、k = 1, ..., N について「ちょうど k 枚重なっている面積」を出力します。

```kotlin
fun main() {
    val br = System.`in`.bufferedReader()
    var st = java.util.StringTokenizer("")
    fun next(): String {
        while (!st.hasMoreTokens()) st = java.util.StringTokenizer(br.readLine())
        return st.nextToken()
    }

    val n = next().toInt()
    val size = 1000
    val imos = Imos2D(size, size)
    repeat(n) {
        val lx = next().toInt()
        val ly = next().toInt()
        val rx = next().toInt()
        val ry = next().toInt()
        imos.add(lx, ly, rx, ry, 1L) // マス (x, y) は lx ≤ x < rx, ly ≤ y < ry
    }
    val cnt = imos.build()
    val ans = LongArray(n + 1)
    for (x in 0 until size) for (y in 0 until size) ans[cnt[x][y].toInt()]++
    val sb = StringBuilder()
    for (k in 1..n) sb.appendLine(ans[k])
    print(sb)
}
```

入力例

```
2
1 1 3 4
2 2 4 3
```

出力例

```
6
1
```

## 例題

- [ABC 035 C - オセロ](https://atcoder.jp/contests/abc035/tasks/abc035_c) — 区間を何回ひっくり返したかをいもす法で数え、偶奇を見る
- [典型90 028 - Cluttered Paper（★4）](https://atcoder.jp/contests/typical90/tasks/typical90_ab) — 2 次元いもす法そのもの (上の使い方)
- [ABC 188 D - Snuke Prime](https://atcoder.jp/contests/abc188/tasks/abc188_d) — 日付が 10^9 まであるので、(日付, 変化量) のイベントをソートして順に足す

## 注意点・よくあるミス

- **右端の扱い**: 「L〜R (両端含む)」なら `-x` を書くのは **R+1** (0-indexed で R)。ここを間違えると 1 つずれます。
  そのため差分配列は長さ n+1 にしておきます。
- `build()` は **すべての add が終わってから 1 回だけ** 呼ぶ。add のたびに累積和をとると O(N^2) になります。
- 2 次元は「4 隅に書く → 横に累積 → 縦に累積」。符号 (+ - - +) を間違えやすいので注意。
- 座標が大きい (10^9 など) と配列が作れません。座標圧縮するか、イベントソート (`(位置, +x)`, `(位置, -x)` をソートして走査) にします。
- 2 次元配列 1001 × 1001 の Long は約 8MB。何枚も作るとメモリが厳しくなることがあります。
