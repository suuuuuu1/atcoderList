---
title: 尺取り法
category: 基本テクニック
acl: -
genres: 尺取り法
order: 2030
summary: 区間の左端と右端を少しずつ動かし、条件を満たす区間を O(N) で調べる
level: 緑
keywords: two pointers, しゃくとり, スライディングウィンドウ, sliding window, 連続部分列, 部分列の個数, 最長の区間, 区間の数え上げ
---

## 何ができる？

連続する区間 [l, r) のうち、条件を満たすものを **数える** / **一番長いもの (短いもの) を探す** のを、
O(N^2) ではなく **O(N)** で行います。

| よくある形 | 内容 | 計算量 |
| --- | --- | --- |
| `countSubarraysSumAtMost(a, k)` | 和が k 以下の連続部分列の個数 (a は 0 以上) | O(N) |
| `longestWithAtMostKKinds(a, k)` | 値の種類が k 種類以下の連続部分列の最大の長さ | O(N) (HashMap 込み) |
| 自分で書くテンプレート | 「l を 1 つ進めるたびに r を進められるだけ進める」 | O(N) |

## いつ使う？

- 「**連続する** 部分列 (区間) のうち、条件を満たすものは何個？」「最長は？」「最短は？」
- 条件に **単調性** がある:
  「区間 [l, r) が OK なら、それより短い [l', r') (l ≤ l' ≤ r' ≤ r) も OK」
  (例: 要素が 0 以上での「和が K 以下」、「種類数が K 以下」、「同じ値を含まない」)
- 逆向き「[l, r) が OK なら、それを含む長い区間も OK」(例: 「和が K 以上」) も同じように使える
- N ≤ 10^5 程度で二重ループ O(N^2) が間に合わない

二分探索 (累積和 + 各 l について r を二分探索) でも解けることが多いですが、尺取り法の方が速く、
「種類数」のように累積和で表せない条件にも使えます。

## しくみ

左端 l を 0, 1, 2, ... と 1 つずつ進めます。それぞれの l について、
右端 r を **条件を満たす限り** 右へ伸ばします。

ポイントは **l を進めても r を戻さない** こと。
単調性があるので、[l, r) が OK なら [l+1, r) も OK。だから r はその場所から続きを伸ばせばよいのです。

```
a = [2, 1, 3, 1, 1, 4],  和が 5 以下の区間

l=0: [2 1]       r=2 で止まる (2+1+3 = 6 > 5)  → [0,1),[0,2) の 2 個
l=1:   [1 3 1]   r=4 で止まる (1+3+1+1 = 6)    → 3 個
l=2:     [3 1 1] r=5 で止まる                  → 3 個
l=3:       [1 1] r=5 (1+1+4 = 6 > 5)           → 2 個
l=4:         [1 4]  r=6                        → 2 個
l=5:           [4]                             → 1 個
                                         合計 13 個
```

l も r も 0 から N まで **一方向にしか動かない** ので、全体で O(N) です。

## 実装 (Kotlin)

```kotlin
// a の要素はすべて 0 以上とする。
// 和が k 以下になる連続部分列 [l, r) (l < r) の個数を返す
fun countSubarraysSumAtMost(a: LongArray, k: Long): Long {
    val n = a.size
    var res = 0L
    var r = 0
    var sum = 0L // a[l] + ... + a[r-1]
    for (l in 0 until n) {
        // 右端を伸ばせるだけ伸ばす
        while (r < n && sum + a[r] <= k) {
            sum += a[r]
            r++
        }
        res += (r - l).toLong() // [l, l+1), ..., [l, r) の r-l 個が条件を満たす
        // 左端 a[l] を区間から外す
        if (r == l) r++ else sum -= a[l]
    }
    return res
}

// 値の種類数が k 種類以下である連続部分列の、最大の長さを返す
fun longestWithAtMostKKinds(a: IntArray, k: Int): Int {
    val n = a.size
    val count = HashMap<Int, Int>() // 区間 [l, r) に各値が何個あるか
    var best = 0
    var r = 0
    for (l in 0 until n) {
        // a[r] を入れても k 種類以下なら伸ばす
        while (r < n && (count.size < k || count.containsKey(a[r]))) {
            count[a[r]] = (count[a[r]] ?: 0) + 1
            r++
        }
        best = maxOf(best, r - l)
        if (r == l) {
            r++ // 空の区間 (k = 0 のとき)。何も外さずに進む
        } else {
            val c = count[a[l]]!! - 1
            if (c == 0) count.remove(a[l]) else count[a[l]] = c
        }
    }
    return best
}
```

自分で書くときのテンプレート (「区間 [l, r) が OK」の判定と、出し入れの処理だけ書き換える):

```
var r = 0
for (l in 0 until n) {
    while (r < n && (a[r] を入れても OK)) { a[r] を入れる; r++ }
    // ここで [l, r) が l を左端とする最長の OK 区間 → 答えを更新
    if (r == l) r++ else a[l] を外す
}
```

## 使い方

N 個の数と K を読み、和が K 以下の連続部分列の個数と、種類数が K 以下の連続部分列の最大の長さを出力します。

```kotlin
fun main() {
    val br = System.`in`.bufferedReader()
    var st = java.util.StringTokenizer("")
    fun next(): String {
        while (!st.hasMoreTokens()) st = java.util.StringTokenizer(br.readLine())
        return st.nextToken()
    }

    val n = next().toInt()
    val k = next().toInt()
    val a = IntArray(n) { next().toInt() }
    println(countSubarraysSumAtMost(LongArray(n) { a[it].toLong() }, k.toLong()))
    println(longestWithAtMostKKinds(a, k))
}
```

入力例

```
6 5
2 1 3 1 1 4
```

出力例

```
13
6
```

## 例題

- [ABC 032 C - 列](https://atcoder.jp/contests/abc032/tasks/abc032_c) — 積が K 以下の最長区間。0 が含まれるときは全体が答えになる点に注意
- [典型90 034 - There are few types of elements（★4）](https://atcoder.jp/contests/typical90/tasks/typical90_ah) — 種類数 K 以下の最長区間 (上の `longestWithAtMostKKinds` そのもの)
- [ABC 098 D - Xor Sum 2](https://atcoder.jp/contests/abc098/tasks/arc098_b) — 「XOR = 和」となる区間の個数。ビットが重ならない限り伸ばせる

## 注意点・よくあるミス

- **単調性があるか必ず確認**。負の数を含む「和が K 以下」は単調でないので尺取り法は使えません
  (累積和 + 別の方法を考える)。
- **r が l に追い越される** ケース (1 個だけでも条件を満たさない) を忘れがち。`if (r == l) r++` のような処理が必要です。
- 個数は最大 N(N+1)/2 ≈ 2×10^10 になるので `Long` で数える。
- 外す処理を忘れる / 入れる処理と外す処理が対応していない、というバグが多いです。
  「入れる」「外す」を関数にしておくと安全です。
- HashMap で種類数を数えるとき、個数が 0 になったキーは `remove` しないと `count.size` が種類数になりません。
