---
title: スタック・キュー・両端キュー
category: データ構造
acl: -
genres: スタック・キュー
order: 101
summary: 末尾や先頭への出し入れを O(1) で行う。括弧列・単調スタックにも
level: 茶
keywords: stack, queue, deque, ArrayDeque, 括弧列, 単調スタック, LIFO, FIFO, 両端キュー, monotonic stack
---

## 何ができる？

Kotlin では **`ArrayDeque`** (import 不要の `kotlin.collections.ArrayDeque`) 1 つで、スタックにもキューにも両端キューにもなります。

| 使い方 | 入れる | 取り出す | 見るだけ |
| --- | --- | --- | --- |
| スタック (後に入れたものが先に出る / LIFO) | `addLast(x)` | `removeLast()` | `last()` |
| キュー (先に入れたものが先に出る / FIFO) | `addLast(x)` | `removeFirst()` | `first()` |
| 両端キュー (両側から出し入れ) | `addFirst(x)` / `addLast(x)` | `removeFirst()` / `removeLast()` | `first()` / `last()` |

| 操作 | 計算量 |
| --- | --- |
| `addFirst` / `addLast` / `removeFirst` / `removeLast` | O(1) (償却) |
| `first()` / `last()` / `size` / `isEmpty()` | O(1) |
| `dq[i]` (i 番目を見る) | O(1) |

このページでは応用として、次の 2 つの関数も用意しています。

| 関数 | 内容 | 計算量 |
| --- | --- | --- |
| `isBalanced(s)` | 括弧列 `()[]{}` が正しく対応しているか | O(N) |
| `prevGreater(a)` | 各 i について、左側で a[i] より大きい最も近い位置 (無ければ -1) | O(N) |
| `nextGreater(a)` | 各 i について、右側で a[i] より大きい最も近い位置 (無ければ N) | O(N) |

## いつ使う？

- **スタック**
  - 括弧列の対応チェック、「`ABC` を見つけたら消す」をくり返す問題
  - 「直前の状態に戻す (Undo)」操作がある
  - DFS を再帰なしで書きたい
- **キュー**
  - BFS (幅優先探索)。迷路の最短手数など
  - 「列に並ぶ」「先に来た順に処理」というシミュレーション
- **両端キュー**
  - 先頭にも末尾にも文字を追加する、文字列の「反転」が何度も来る (反転フラグ + 両端キュー)
  - 0-1 BFS (コスト 0 の辺は先頭に、コスト 1 の辺は末尾に入れる)
- **単調スタック**
  - 「左 (右) 側で自分より大きい (小さい) 最初の要素」を全部の i について求めたい
  - 「間に自分より高いビルがない」「見えるビルの数」「ヒストグラムの最大長方形」

## しくみ

`ArrayDeque` は配列をリング状 (輪っか) に使っていて、先頭と末尾の位置を覚えています。
配列が一杯になったら 2 倍の大きさに作り直すので、どの操作も平均 O(1) です。

```
スタック:  addLast で積んで、removeLast で上から取る
  [1, 2, 3] ← ここから出し入れ

キュー:    addLast で後ろに並び、removeFirst で前から抜ける
  出る ← [1, 2, 3] ← 入る
```

**括弧列の判定**: 開き括弧はスタックに積み、閉じ括弧が来たらスタックの一番上と種類が合うか確認して取り除きます。
最後にスタックが空なら正しい括弧列です。

**単調スタック (prevGreater)**: 左から順に見ながら、「まだ右側の誰かの答えになりうる添字」だけをスタックに残します。

```
a = [3, 1, 4, 1, 5, 2]

i=0 (3): スタック空 → 答え -1。積む        スタック: [3]
i=1 (1): 一番上 3 > 1 → 答え 0。積む       スタック: [3, 1]
i=2 (4): 1, 3 は 4 以下なので捨てる → -1   スタック: [4]
i=3 (1): 一番上 4 > 1 → 答え 2            スタック: [4, 1]
i=4 (5): 全部捨てる → -1                  スタック: [5]
i=5 (2): 一番上 5 > 2 → 答え 4            スタック: [5, 2]
```

a[i] 以下の値は、a[i] より右にいる誰にとっても「a[i] のほうが近くて大きい」ので、もう答えになりません。だから捨ててよいのです。
各添字は 1 回積まれて 1 回捨てられるだけなので、全体で O(N) です。

## 実装 (Kotlin)

```kotlin
// 括弧列が正しく対応しているか判定する ("()", "[]", "{}" の 3 種類に対応)
fun isBalanced(s: String): Boolean {
    val open = mapOf(')' to '(', ']' to '[', '}' to '{')
    val stack = ArrayDeque<Char>()
    for (c in s) {
        when (c) {
            '(', '[', '{' -> stack.addLast(c)
            ')', ']', '}' -> {
                // 閉じ括弧: 直前の開き括弧と種類が合っていなければダメ
                if (stack.isEmpty() || stack.last() != open[c]) return false
                stack.removeLast()
            }
        }
    }
    return stack.isEmpty() // 閉じられていない開き括弧が残っていたらダメ
}

// 単調スタック: 各 i について「左側にある a[i] より大きい値のうち、最も近い位置」を返す (無ければ -1)
fun prevGreater(a: IntArray): IntArray {
    val res = IntArray(a.size)
    val stack = ArrayDeque<Int>() // 添字を積む。a[添字] は底から上に向かって狭義単調減少
    for (i in a.indices) {
        // a[i] 以下の値は、今後「より大きい値」として使われることがないので捨てる
        while (stack.isNotEmpty() && a[stack.last()] <= a[i]) stack.removeLast()
        res[i] = if (stack.isEmpty()) -1 else stack.last()
        stack.addLast(i)
    }
    return res
}

// 単調スタック: 各 i について「右側にある a[i] より大きい値のうち、最も近い位置」を返す (無ければ n)
fun nextGreater(a: IntArray): IntArray {
    val n = a.size
    val res = IntArray(n)
    val stack = ArrayDeque<Int>()
    for (i in n - 1 downTo 0) {
        while (stack.isNotEmpty() && a[stack.last()] <= a[i]) stack.removeLast()
        res[i] = if (stack.isEmpty()) n else stack.last()
        stack.addLast(i)
    }
    return res
}
```

## 使い方

ArrayDeque の基本操作のあと、1 行目の括弧列を判定し、2〜3 行目の数列に単調スタックを使います。

```kotlin
fun main() {
    // ---- 1. ArrayDeque の基本 (スタックにもキューにもなる) ----
    val dq = ArrayDeque<Int>()
    dq.addLast(1)
    dq.addLast(2)
    dq.addFirst(0)
    println(dq)               // [0, 1, 2]
    println(dq.removeFirst()) // 0  ← キューとして使う (先頭から取り出す)
    println(dq.removeLast())  // 2  ← スタックとして使う (末尾から取り出す)
    println(dq.first())       // 1  ← 見るだけ (取り出さない)
    println(dq.size)          // 1

    // ---- 2. 括弧列の判定 ----
    val s = readln()
    println(if (isBalanced(s)) "Yes" else "No")

    // ---- 3. 単調スタック ----
    val n = readln().trim().toInt()
    val a = readln().trim().split(" ").map { it.toInt() }.toIntArray()
    println(prevGreater(a).joinToString(" "))
    println(nextGreater(a).joinToString(" "))
}
```

入力例

```
([]{})
6
3 1 4 1 5 2
```

出力例

```
[0, 1, 2]
0
2
1
1
Yes
-1 0 -1 2 -1 4
2 2 4 4 6 6
```

## 例題

- [ABC 328 D - Take ABC](https://atcoder.jp/contests/abc328/tasks/abc328_d) — 文字をスタックに積み、上 3 つが `ABC` になったら取り除く
- [ABC 158 D - String Formation](https://atcoder.jp/contests/abc158/tasks/abc158_d) — 「反転したか」のフラグを持ち、両端キューの先頭か末尾に追加する
- [典型90 061 - Deck](https://atcoder.jp/contests/typical90/tasks/typical90_bi) — 山札の上と下にカードを足していく。両端キューそのもの
- [ABC 372 D - Buildings](https://atcoder.jp/contests/abc372/tasks/abc372_d) — 右から単調スタックを使う。スタックの大きさがそのまま答えになる

## 注意点・よくあるミス

- 空の `ArrayDeque` に `removeFirst()` / `removeLast()` / `first()` / `last()` をすると例外になります。
  先に `isEmpty()` を確認するか、`removeFirstOrNull()` / `lastOrNull()` などを使う。
- キューの代わりに `MutableList` の `removeAt(0)` を使うと **1 回 O(N)** で TLE します。必ず `ArrayDeque` を使う。
- `java.util.Stack` は古いクラスなので使わなくて OK。`java.util.ArrayDeque` でも動きますが、
  Kotlin の `ArrayDeque` のほうが `dq[i]` で添字アクセスもできて便利です。
- 単調スタックでは **`<` と `<=` の違い** に注意。「より大きい」なら `a[top] <= a[i]` で捨て、
  「以上」なら `a[top] < a[i]` で捨てます。
- 単調スタックには **値ではなく添字** を積んでおくと、位置も値 (`a[添字]`) も分かって便利です。
- 括弧が 1 種類 `()` だけなら、スタックを使わず「`(` で +1、`)` で -1、途中で負にならず最後に 0」で判定できます。
