# ラムダ式の練習 解答例

自分で解いてから見てください。書き方は 1 つではないので、✅ が出れば別の書き方でも正解です。

## Lv1

```kotlin
fun q1(a: List<Int>): List<Int> = a.map { it * 2 }
fun q2(a: List<Int>): List<Int> = a.filter { it % 2 == 0 }
fun q3(a: List<Int>): Int = a.sumOf { it * it }
fun q4(line: String): List<Int> = line.split(" ").map { it.toInt() }
fun q5(a: List<Int>, k: Int): Int = a.count { it > k }
```

- `map` は「全部を変換」、`filter` は「条件に合うものだけ残す」、`count` は「条件に合う個数」。
- Q4 は AtCoder の入力でいつも書いている `readln().split(" ").map { it.toInt() }` と同じ形。

## Lv2

```kotlin
fun q6(n: Int): IntArray = IntArray(n) { i -> i * i }
fun q7(scores: List<Pair<String, Int>>): String = scores.maxBy { it.second }.first
fun q8(words: List<String>): List<String> = words.sortedWith(compareBy({ it.length }, { it }))
fun q9(a: List<Int>): Boolean = a.all { it > 0 }
fun q10(s: String): Map<Char, Int> = s.groupingBy { it }.eachCount()
```

- Q6 の `IntArray(n) { i -> ... }` は、`{ it * it }` と書いても同じ。
- Q7 の `maxBy { it.second }` は「second が最大の要素そのもの」を返す。値だけ欲しいなら `maxOf { it.second }`。
- Q8 の `compareBy({ 1番目の基準 }, { 2番目の基準 })` は、1 番目が同じとき 2 番目で比べる。
- Q9 の仲間: `any { }` (1 つでも)、`none { }` (1 つもない)。

## Lv3

```kotlin
fun q11(): (Int) -> Int = { x -> x * x + 1 }
fun applyTwice(x: Int, f: (Int) -> Int): Int = f(f(x))
fun q13(a: List<Int>): List<Int> = a.map { if (it % 2 == 0) it / 2 else 3 * it + 1 }
fun q14(prev: IntArray, a: IntArray): IntArray =
    IntArray(3) { j -> (0 until 3).filter { it != j }.maxOf { prev[it] } + a[j] }
```

- Q11: `(Int) -> Int` は「Int を受け取って Int を返す関数」の型。ラムダもこの型の値として変数に入れたり返したりできる。
- Q12: ラムダが最後の引数のときは `applyTwice(3) { it + 10 }` のように `( )` の外に書ける。`repeat(n) { }` や `IntArray(n) { }` も同じ仕組み。
- Q13 は複数行で書いてもよい:

```kotlin
fun q13(a: List<Int>): List<Int> = a.map { x ->
    val half = x / 2
    if (x % 2 == 0) half else 3 * x + 1   // 最後の行の値が返る
}
```

- Q14 は `{ j -> ... }` の中に `{ it != j }` と `{ prev[it] }` があり、ラムダの中にラムダが入っている。
  外側の引数には `j` と名前を付けているので、内側の `it` (0, 1, 2) と区別できる。
