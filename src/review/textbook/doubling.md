---
title: ダブリング
category: 基本テクニック
acl: -
genres: 数学
order: 2080
summary: K 回 (10^18 回でも) 移動した先を O(log K) で求める
level: 緑
keywords: doubling, 2 の累乗, K 回後, 10^18 回, テレポーター, functional graph, 繰り返し二乗, 高速化, ダブリングテーブル
---

## 何ができる？

各状態 x (0 〜 N-1) について「1 回操作すると次は `next[x]`」と決まっているとき、
**x から K 回操作した後の状態** を、K が 10^18 でも高速に求めます。

| 操作 | 内容 | 計算量 |
| --- | --- | --- |
| `Doubling(next, maxK)` | 表 `table[j][x]` = x から 2^j 回進んだ先 を作る | O(N log maxK) |
| `jump(x, k)` | x から k 回進んだ先 (0 ≤ k ≤ maxK) | O(log maxK) |
| `DoublingSum(next, weight, maxK)` | 上に加えて、通った各状態の weight の合計も記録する | O(N log maxK) |
| `query(x, k)` | (k 回進んだ先, その間に通った状態の weight の合計) | O(log maxK) |

## いつ使う？

- 「K 回 (K ≤ 10^18) 操作した後はどこ？」で、**次の状態が今の状態だけで決まる**
- 状態の種類数 N が 10^5〜2×10^5 程度で、N × 60 の表が作れる
- 「各 x から K 回後」を **いろいろな開始点やいろいろな K で何度も** 聞かれる
  (開始点が 1 つだけなら [周期検出](cycle-detection.md) でも解ける)
- 木の LCA (最小共通祖先) を求めるときの「親を 2^j 回たどる」もダブリングです

## しくみ

K を 2 進数で表すと、K 回の移動は「2^j 回の移動」の組み合わせになります。

```
K = 13 = 8 + 4 + 1 (2 進数で 1101)
→ 「1 回進む」→「4 回進む」→「8 回進む」の順にジャンプすれば 13 回進んだことになる
```

そこで「2^j 回進んだ先」の表を前もって作ります。

```
table[0][x] = next[x]                       (1 回進む)
table[1][x] = table[0][ table[0][x] ]       (2 回 = 1 回 + 1 回)
table[2][x] = table[1][ table[1][x] ]       (4 回 = 2 回 + 2 回)
  ...
table[j][x] = table[j-1][ table[j-1][x] ]   (2^j 回 = 2^(j-1) 回 + 2^(j-1) 回)
```

10^18 < 2^60 なので、j は 0〜59 の 60 段あれば足ります。
`jump(x, k)` は k のビットが立っている j について `x = table[j][x]` とするだけです。

合計 (通った状態の重みの和) が欲しいときは、`sum[j][x]` = x から 2^j 回進む間に足される重み の表も一緒に作ります。

## 実装 (Kotlin)

```kotlin
// ダブリング。状態 0..n-1 について next[x] = 1 回操作した後の状態
class Doubling(next: IntArray, maxK: Long) {
    private val n = next.size
    private val log: Int
    private val table: Array<IntArray> // table[j][x] = x から 2^j 回進んだ先

    init {
        require(maxK >= 0)
        var lg = 1
        while (lg < 63 && (1L shl lg) <= maxK) lg++ // 2^lg > maxK となる最小の lg
        log = lg
        table = Array(log) { IntArray(n) }
        for (x in 0 until n) table[0][x] = next[x]
        for (j in 1 until log) {
            val prev = table[j - 1]
            val cur = table[j]
            for (x in 0 until n) cur[x] = prev[prev[x]]
        }
    }

    // x から k 回進んだ先
    fun jump(x: Int, k: Long): Int {
        require(k >= 0 && (k shr log) == 0L)
        var cur = x
        for (j in 0 until log) {
            if ((k shr j) and 1L == 1L) cur = table[j][cur]
        }
        return cur
    }
}

// 重みの合計つきダブリング。状態 x にいるとき weight[x] を足してから next[x] へ進む
class DoublingSum(next: IntArray, weight: LongArray, maxK: Long) {
    private val n = next.size
    private val log: Int
    private val table: Array<IntArray> // x から 2^j 回進んだ先
    private val sum: Array<LongArray>  // その間に足される weight の合計

    init {
        require(maxK >= 0)
        var lg = 1
        while (lg < 63 && (1L shl lg) <= maxK) lg++
        log = lg
        table = Array(log) { IntArray(n) }
        sum = Array(log) { LongArray(n) }
        for (x in 0 until n) {
            table[0][x] = next[x]
            sum[0][x] = weight[x]
        }
        for (j in 1 until log) {
            for (x in 0 until n) {
                val mid = table[j - 1][x]
                table[j][x] = table[j - 1][mid]
                sum[j][x] = sum[j - 1][x] + sum[j - 1][mid]
            }
        }
    }

    // (x から k 回進んだ先, その間に足された weight の合計)
    fun query(x: Int, k: Long): Pair<Int, Long> {
        require(k >= 0 && (k shr log) == 0L)
        var cur = x
        var total = 0L
        for (j in 0 until log) {
            if ((k shr j) and 1L == 1L) {
                total += sum[j][cur]
                cur = table[j][cur]
            }
        }
        return cur to total
    }
}
```

## 使い方

[ABC 167 D - Teleporter](https://atcoder.jp/contests/abc167/tasks/abc167_d): 町 i のテレポーターは町 A_i へ飛ぶ。
町 1 から K 回 (K ≤ 10^18) テレポーターを使った後の町を出力します。

```kotlin
fun main() {
    val br = System.`in`.bufferedReader()
    var st = java.util.StringTokenizer("")
    fun next(): String {
        while (!st.hasMoreTokens()) st = java.util.StringTokenizer(br.readLine())
        return st.nextToken()
    }

    val n = next().toInt()
    val k = next().toLong()
    val a = IntArray(n) { next().toInt() - 1 } // 0-indexed に
    val db = Doubling(a, k)
    println(db.jump(0, k) + 1)
}
```

入力例

```
4 5
3 2 4 1
```

出力例

```
4
```

重みつき: [ABC 241 E - Putting Candies](https://atcoder.jp/contests/abc241/tasks/abc241_e)。
最初 X = 0 で、「X に A[X mod N] を足す」を K 回 (K ≤ 10^12) 行った後の X を出力します。
状態を `X mod N` と考えると、次の状態は `(x + A[x]) mod N`、足される量は `A[x]` です。

```kotlin
fun main() {
    val br = System.`in`.bufferedReader()
    var st = java.util.StringTokenizer("")
    fun next(): String {
        while (!st.hasMoreTokens()) st = java.util.StringTokenizer(br.readLine())
        return st.nextToken()
    }

    val n = next().toInt()
    val k = next().toLong()
    val a = LongArray(n) { next().toLong() }
    val nxt = IntArray(n) { ((it + a[it]) % n).toInt() }
    val db = DoublingSum(nxt, a, k)
    val (_, total) = db.query(0, k)
    println(total)
}
```

入力例

```
5 3
2 1 6 3 1
```

出力例

```
11
```

## 例題

- [ABC 167 D - Teleporter](https://atcoder.jp/contests/abc167/tasks/abc167_d) — K ≤ 10^18 回のテレポート。ダブリングでも周期検出でも解ける
- [ABC 241 E - Putting Candies](https://atcoder.jp/contests/abc241/tasks/abc241_e) — 合計つきダブリング (上の使い方)
- [典型90 058 - Original Calculator（★4）](https://atcoder.jp/contests/typical90/tasks/typical90_bf) — 状態は 0〜99999。K ≤ 10^18 回の操作

## 注意点・よくあるミス

- 表の段数が足りないと答えがずれます。`maxK` には **実際に使う最大の k** を渡す (上の実装は自動で段数を決めます)。
- メモリは `N × 段数` 個。N = 2×10^5、60 段の IntArray で約 48MB。Long の表も持つと倍になるので注意。
  開始点が 1 つだけなら [周期検出](cycle-detection.md) の方が軽いです。
- `1 shl 40` (Int) はオーバーフローします。ビット演算は `1L shl j`、`(k shr j) and 1L` と Long で書く。
- 合計つきは、合計が Long に収まるか確認する (10^18 回 × 10^9 は収まらない)。mod を取るなら表の段階で mod を取る。
- 表を作るときは **段 j-1 が全部できてから段 j を作る** (x のループを外側にすると壊れます)。
