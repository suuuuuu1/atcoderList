package practice.lambda

/*
 * ラムダ式の練習
 *
 * 使い方
 *   1. 各問題の TODO() を消して、答えを書く (1 行で書けるものが多いです)
 *   2. このファイルの main を実行 (Ctrl + Shift + F10)
 *   3. ✅ になれば正解、❌ なら期待値と自分の答えが出る、⬜ はまだ TODO のまま
 *   解答例は同じフォルダの answers.md
 *
 * 思い出す用
 *   { x -> x * 2 }   … x を受け取って x * 2 を返す関数。-> の左が引数、右が返す値
 *   { it * 2 }       … 引数が 1 つなら x -> を省略でき、引数の名前が it になる
 *   { a, b -> a + b } … 引数が 2 つ
 *   複数行のラムダは「最後の行の値」が返る
 */

// ===== Lv1: map / filter / sumOf / count =====

/** Q1. すべての要素を 2 倍にしたリストを返す。 例: [1, 2, 3] → [2, 4, 6]   ヒント: map */
fun q1(a: List<Int>): List<Int> = a.map{it*2}

/** Q2. 偶数だけを残したリストを返す。 例: [1, 2, 3, 4] → [2, 4]   ヒント: filter */
fun q2(a: List<Int>): List<Int> = a.filter{it % 2 == 0}

/** Q3. 各要素を 2 乗した合計を返す。 例: [1, 2, 3] → 14   ヒント: sumOf */
fun q3(a: List<Int>): Int = a.sumOf { it * it }

/** Q4. "3 1 4" のような文字列を数のリストにする。 例: "3 1 4" → [3, 1, 4]   ヒント: split と map */
fun q4(line: String): List<Int> = line.split(" ").map { it.toInt() }

/** Q5. k より大きい要素の個数を返す。 例: a=[5, 1, 8, 3], k=3 → 2   ヒント: count */
fun q5(a: List<Int>, k: Int): Int = a.count { it > k}

// ===== Lv2: IntArray の初期化 / max・sort 系 =====

/** Q6. 長さ n で、i 番目が i * i の IntArray を作る。 例: n=4 → [0, 1, 4, 9]   ヒント: IntArray(n) { i -> ... } */
fun q6(n: Int): IntArray = IntArray(n){i -> i*i}

/** Q7. (名前, 点数) のリストから、点数が一番高い人の名前を返す。 例: [("a",3), ("b",7), ("c",5)] → "b"   ヒント: maxBy */
fun q7(scores: List<Pair<String, Int>>): String = scores.maxBy { it.second }.first

/** Q8. 文字列を「短い順、同じ長さなら辞書順」に並べる。 例: ["bb", "a", "ab", "c"] → ["a", "c", "ab", "bb"]   ヒント: sortedWith(compareBy(...)) */
fun q8(words: List<String>): List<String> = words.sortedWith(compareBy ({ it.length } , {it}))

/** Q9. すべての要素が正なら true。 例: [1, 2, 3] → true, [1, -2, 3] → false   ヒント: all */
fun q9(a: List<Int>): Boolean = a.all{it > 0}

/** Q10. 文字列の中の各文字の個数を Map で返す。 例: "abca" → {a=2, b=1, c=1}   ヒント: groupingBy { }.eachCount() */
fun q10(s: String): Map<Char, Int> = s.groupingBy { it }.eachCount()

// ===== Lv3: ラムダを作る・渡す =====

/** Q11. 「x を受け取って x * x + 1 を返す」ラムダを返す。 例: q11()(5) → 26   ヒント: 型は (Int) -> Int */
fun q11(): (Int) -> Int = {x -> x * x +1}

/** Q12. 関数 f を x に 2 回使った結果 f(f(x)) を返す。 例: applyTwice(3) { it + 10 } → 23 */
fun applyTwice(x: Int, f: (Int) -> Int): Int = f(f(x))

/** Q13. 各要素 x を「x が偶数なら x / 2、奇数なら 3 * x + 1」に変える。 例: [4, 5] → [2, 16]
 *  ヒント: map の中で if を使う。複数行のラムダにしてもよい (最後の行の値が返る) */
fun q13(a: List<Int>): List<Int> = a.map{if (it % 2 == 0) it / 2 else 3 * it + 1}

/** Q14. EDPC C の 1 日分。prev[j] = 昨日 j をした場合の最大、a[j] = 今日 j の幸福度。
 *  今日 j をした場合の最大を 3 つ並べた IntArray を返す (昨日と同じ行動はできない)。
 *  例: prev=[10, 40, 70], a=[20, 50, 80] → [90, 120, 120]
 *  ヒント: IntArray(3) { j -> ... } の中で (0 until 3).filter { ... }.maxOf { ... } */
fun q14(prev: IntArray, a: IntArray): IntArray = IntArray(3){ j -> (0 until 3).filter{it != j}
    .maxOf{prev[it]}+ a[j]}

// ===== 答え合わせ (ここは変えなくて OK) =====

fun main() {
    var ok = 0
    var total = 0
    // ラムダを受け取る関数: actual は「答えを計算するラムダ」
    fun check(no: Int, expected: Any?, actual: () -> Any?) {
        total++
        try {
            val got = actual()
            if (got == expected) {
                ok++
                println("✅ Q$no")
            } else {
                println("❌ Q$no  期待: $expected  あなた: $got")
            }
        } catch (e: NotImplementedError) {
            println("⬜ Q$no  (まだ TODO)")
        } catch (e: Exception) {
            println("💥 Q$no  エラー: $e")
        }
    }

    check(1, listOf(2, 4, 6)) { q1(listOf(1, 2, 3)) }
    check(2, listOf(2, 4)) { q2(listOf(1, 2, 3, 4)) }
    check(3, 14) { q3(listOf(1, 2, 3)) }
    check(4, listOf(3, 1, 4)) { q4("3 1 4") }
    check(5, 2) { q5(listOf(5, 1, 8, 3), 3) }
    check(6, listOf(0, 1, 4, 9)) { q6(4).toList() }
    check(7, "b") { q7(listOf("a" to 3, "b" to 7, "c" to 5)) }
    check(8, listOf("a", "c", "ab", "bb")) { q8(listOf("bb", "a", "ab", "c")) }
    check(9, listOf(true, false)) { listOf(q9(listOf(1, 2, 3)), q9(listOf(1, -2, 3))) }
    check(10, mapOf('a' to 2, 'b' to 1, 'c' to 1)) { q10("abca") }
    check(11, 26) { q11()(5) }
    check(12, 23) { applyTwice(3) { it + 10 } }
    check(13, listOf(2, 16)) { q13(listOf(4, 5)) }
    check(14, listOf(90, 120, 120)) { q14(intArrayOf(10, 40, 70), intArrayOf(20, 50, 80)).toList() }

    println()
    println("$ok / $total 問正解")
}
