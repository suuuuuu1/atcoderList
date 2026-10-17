---
title: Kotlin 競プロの基本
category: はじめに
acl: -
genres: 実装
order: 20
summary: 入出力・Long・配列・ソート・再帰対策など Kotlin で解く基本
level: 灰
keywords: Kotlin, 入力, 出力, readln, BufferedReader, StringTokenizer, オーバーフロー, ソート, スタックオーバーフロー, TLE
---

## 何ができる？

Kotlin で AtCoder を解くときに毎回使う「型 (テンプレート)」をまとめたページです。

| やりたいこと | 使うもの | ポイント |
| --- | --- | --- |
| 入力を読む (少量) | `readln()` + `split(" ")` | 手軽。10^5 行以上なら遅いことがある |
| 入力を読む (大量) | `FastReader` (BufferedReader + StringTokenizer) | 改行・空白を気にせず順に読める |
| 大量に出力する | `StringBuilder` か `PrintWriter` で一括出力 | `println` を 10^5 回呼ぶと遅い |
| 大きい数を扱う | `Long` | `Int` は約 ±2.1×10^9 まで |
| 配列 | `IntArray(n)`, `LongArray(n)`, `Array(h) { IntArray(w) }` | `Array<Int>` より速い |
| ソート | `sort()`, `sortBy`, `sortedWith(compareBy(...))` | 複数キーは `compareBy` |
| 深い再帰 | `Thread(null, ::solve, "", 1L shl 28)` | スタックを広げて StackOverflow を防ぐ |

## いつ使う？

- 入力が 10^5 個を超える → `FastReader` で読む
- 出力が 10^5 行を超える → `StringBuilder` や `PrintWriter` でまとめて出す
- 答えや途中の値が 2×10^9 を超えそう (掛け算・合計・距離) → `Long` を使う
- 再帰 DFS で深さが 10^4 を超えそう (一直線の木やグラフ) → スタックを拡張する
- 「なぜか TLE / WA / RE」のときに、まずこのページの「注意点」を確認する

## しくみ

### 入力: readln と split

AtCoder の入力は「空白区切り・改行区切り」です。少ないときは `readln()` で 1 行ずつ読みます。

```kotlin
val n = readln().toInt()                                   // 「5」
val (a, b) = readln().split(" ").map { it.toInt() }        // 「3 4」→ a=3, b=4
val xs = readln().split(" ").map { it.toLong() }           // 「1 2 3 4 5」→ List<Long>
val arr = readln().split(" ").map { it.toInt() }.toIntArray() // IntArray にしたいとき
val s = readln()                                           // 文字列をそのまま 1 行
```

- 分解宣言 `val (a, b) = ...` は 5 個まで使えます (`component1`〜`component5`)。
- `readln()` は Kotlin 1.6 以降。古い記事の `readLine()!!` と同じ意味です。

### 入力: FastReader (高速入力)

`readln().split(" ")` は 1 行ごとに List を作るので、入力が 10^6 個くらいになると遅くなります。
`BufferedReader` でまとめて読み、`StringTokenizer` で空白ごとに切り出すと速くなります。
下の「実装」の `FastReader` は **改行を気にせず「次の数」を順に読める** のも便利な点です。

### 出力: まとめて出す

`println` は呼ぶたびに出力処理が走るので、10^5 回以上呼ぶと遅くなります。

```kotlin
val sb = StringBuilder()
for (i in 1..3) sb.append(i).append('\n')     // sb.appendLine(i) でも同じ
print(sb)                                       // 最後に 1 回だけ出力

val out = java.io.PrintWriter(System.out)      // PrintWriter でも OK
for (i in 1..3) out.println(i * 10)
out.flush()                                     // ★ flush を忘れると何も出ない
```

1 行に空白区切りで並べるなら `joinToString(" ")` が便利です。

```kotlin
val ans = listOf(1, 2, 3)
println(ans.joinToString(" "))   // 1 2 3
```

### Int と Long (オーバーフロー)

`Int` は約 ±2.1×10^9 (2^31 - 1 = 2147483647) までしか表せません。
超えると **エラーにならず、おかしな値になります**。

```kotlin
val x = 100000
val y = 100000
println(x * y)            // 1410065408  ← オーバーフロー！
println(x.toLong() * y)   // 10000000000 ← 片方を Long にしてから掛ける
var sum = 0L              // 合計は最初から Long で (0L と書く)
```

目安: **10^9 × 10^9 = 10^18 も Long (約 9.2×10^18 まで) なら入る**。

### 配列とリスト

```kotlin
val n = 3
val h = 2
val w = 3
val a = IntArray(n)                       // [0, 0, 0]
val b = LongArray(n) { it.toLong() * 2 }  // [0, 2, 4] (it は添字)
val grid = Array(h) { IntArray(w) }       // h 行 w 列の 2 次元配列。grid[i][j]
val dist = IntArray(n) { Int.MAX_VALUE }  // 「無限大」で初期化
val list = mutableListOf<Int>()           // 可変長リスト (C++ の vector)
list.add(5)
val adj = List(n) { mutableListOf<Int>() } // グラフの隣接リスト
adj[0].add(1)
```

- 数値の配列は `Array<Int>` ではなく **`IntArray` / `LongArray`** を使う (速くてメモリも少ない)。
- 文字のグリッドは `val g = Array(h) { readln() }` で `g[i][j]` が `Char` になります。

### ソート

```kotlin
val a = intArrayOf(3, 1, 2)
a.sort()                 // a 自体を昇順に [1, 2, 3]
a.sortDescending()       // a 自体を降順に [3, 2, 1]
val s = a.sorted()       // 新しい List を返す (a は変わらない)

data class P(val name: String, val age: Int)
val ps = mutableListOf(P("bob", 20), P("alice", 20), P("carol", 18))
ps.sortBy { it.age }                                     // 年齢の昇順
val r1 = ps.sortedWith(compareBy({ it.age }, { it.name })) // 年齢 → 名前 の順
val r2 = ps.sortedWith(compareByDescending<P> { it.age }.thenBy { it.name }) // 年齢は降順
```

`sortBy` / `sortedBy` は **安定ソート** (同じキーの要素は元の順番のまま) です。

### よく使う標準関数

```kotlin
val v = listOf(5, 3, 8, 3)
v.sum()                      // 19
v.max()                      // 8 (空なら例外。空かも → maxOrNull())
v.count { it == 3 }          // 2
v.filter { it > 4 }          // [5, 8]
v.indexOf(8)                 // 2 (無ければ -1)
v.distinct()                 // [5, 3, 8]
v.groupingBy { it }.eachCount() // {5=1, 3=2, 8=1} 個数を数える
v.withIndex()                // (index, value) の組で回せる
maxOf(3, 7)                  // 7
kotlin.math.abs(-4)          // 4
repeat(3) { /* 3 回くり返す */ }
for (i in 0 until 5) {}      // 0,1,2,3,4
for (i in 4 downTo 0) {}     // 4,3,2,1,0
for (i in 0 until 10 step 3) {} // 0,3,6,9
```

### 再帰の深さ対策 (スタック拡張)

Kotlin (JVM) は標準のスタックが小さく、再帰が 1 万段くらいで `StackOverflowError` になることがあります。
**大きなスタックを持つ別スレッドで解く** と回避できます。

```kotlin
fun main() {
    // 第 4 引数がスタックサイズ (バイト)。1L shl 28 = 256MB
    Thread(null, ::solve, "solve", 1L shl 28).start()
}

fun solve() {
    val n = 1_000_000
    fun depth(x: Int): Int = if (x == 0) 0 else depth(x - 1) + 1
    println(depth(n))   // 100 万段の再帰でも OK
}
```

## 実装 (Kotlin)

```kotlin
// 高速入力: 空白・改行区切りのトークンを順に読む
class FastReader {
    private val br = System.`in`.bufferedReader()
    private var st = java.util.StringTokenizer("")

    fun next(): String {
        while (!st.hasMoreTokens()) st = java.util.StringTokenizer(br.readLine())
        return st.nextToken()
    }

    fun nextInt(): Int = next().toInt()
    fun nextLong(): Long = next().toLong()
    fun nextDouble(): Double = next().toDouble()
    fun nextIntArray(n: Int): IntArray = IntArray(n) { nextInt() }
    fun nextLongArray(n: Int): LongArray = LongArray(n) { nextLong() }
}
```

## 使い方

[ABC 088 B - Card Game for Two](https://atcoder.jp/contests/abc088/tasks/abc088_b) を解くコードです。
カードを大きい順に並べ、Alice と Bob が交互に取ったときの (Alice の合計) - (Bob の合計) を出力します。

```kotlin
fun main() {
    val fr = FastReader()
    val out = java.io.PrintWriter(System.out)

    val n = fr.nextInt()
    val a = fr.nextIntArray(n)
    a.sortDescending()               // 大きい順
    var diff = 0L
    for (i in 0 until n) {
        if (i % 2 == 0) diff += a[i] else diff -= a[i]  // 偶数番目が Alice
    }
    out.println(diff)
    out.flush()                      // 忘れずに！
}
```

入力例

```
3
2 7 4
```

出力例

```
5
```

## 例題

- [AtCoder Beginners Selection PracticeA - Welcome to AtCoder](https://atcoder.jp/contests/abs/tasks/practice_1) — 数と文字列を読んで出力するだけ。まずはここから
- [ABC 088 B - Card Game for Two](https://atcoder.jp/contests/abc088/tasks/abc088_b) — ソート (降順) の練習
- [ABC 081 B - Shift only](https://atcoder.jp/contests/abc081/tasks/abc081_b) — 配列とループの練習
- [AtCoder Beginners Selection](https://atcoder.jp/contests/abs) — 基本の 10 問。全部 Kotlin で解いてみよう

## 注意点・よくあるミス

- **`Int` のオーバーフロー**: 掛け算・合計・累積和は `Long` に。`val sum = 0` ではなく `var sum = 0L`。
  `a * b` は **掛ける前に** `a.toLong() * b` とする (`(a * b).toLong()` では手遅れ)。
- `PrintWriter` を使ったら最後に **`flush()`** (または `close()`)。忘れると何も出力されず WA。
- `readln().split(" ")` は空白が 2 つ続くと空文字が混ざります。AtCoder では普通起きませんが、
  不安なら `split(" ").filter { it.isNotEmpty() }` か `FastReader` を使う。
- 数値の配列に `Array<Int>` や `List<Int>` を大量に使うと遅い・メモリを食う。`IntArray` / `LongArray` を使う。
- `Int.MAX_VALUE` を「無限大」にして `+ 1` するとオーバーフローで負になります。
  足し算する可能性があるなら `Long` で `1L shl 60` などを使う。
- 再帰 DFS で `StackOverflowError` (RE) が出たら、`Thread(null, ::solve, "", 1L shl 28)` で包む。
- `list.remove(x)` は `MutableList<Int>` だと「値 x を消す」のか「添字 x を消す」のか紛らわしい。
  添字なら `removeAt(i)` を使う。
- 整数の割り算は切り捨て (負の数は 0 方向へ切り捨て: `-7 / 2 == -3`)。`%` も負になることがある (`-7 % 2 == -1`)。
  正の余りが欲しいなら `Math.floorMod(a, m)` か `((a % m) + m) % m`。
