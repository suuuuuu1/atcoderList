---
title: 最長増加部分列 (LIS)
category: DP
acl: -
genres: DP, 二分探索
order: 3020
summary: 数列から増加する部分列の最長の長さを O(N log N) で求める
level: 緑
keywords: LIS, longest increasing subsequence, 増加部分列, 単調増加, 広義単調増加, 入れ子, 箱, 部分列
---

## 何ができる？

数列 A から **いくつか取り出して (順番はそのまま)**、値が増えていくようにしたときの最長の長さを求めます。

```
A = 3 1 4 1 5 9 2 6
増加部分列の例: 1 4 5 9  /  3 4 5 6  /  1 4 5 6   → 最長は 4
```

| 関数 | 内容 | 計算量 |
| --- | --- | --- |
| `lis(a)` | **狭義** (a[i] < a[j]) の LIS の長さ | O(N log N) |
| `lis(a, strict = false)` | **広義** (a[i] ≦ a[j]) の LIS の長さ | O(N log N) |
| `lisIndices(a)` | 狭義 LIS を 1 つ復元して、その **添字のリスト** を返す | O(N log N) |

## いつ使う？

- 「部分列」「順番を変えずにいくつか選ぶ」「増加」「最長」という言葉が出てくる
- 「最小でいくつ消せば単調増加になる？」→ N - LIS
- 箱の入れ子・封筒の入れ子のような **2 次元の入れ子** (一方でソートして、もう一方で LIS)
- 「単調減少列の最長」→ 符号を反転 (`-a[i]`) して LIS
- 普通の DP `dp[i] = max(dp[j] + 1)` (O(N^2)) では N = 2×10^5 に間に合わないとき

## しくみ

配列 `tail` を持ちます。

`tail[k]` = 「長さ k+1 の増加部分列」の **末尾として考えられる最小の値**

末尾が小さいほど、後ろに続けやすいので得です。`tail` は常に **単調増加** になっています。
A の要素 x を左から 1 つずつ見て、

- x が `tail` のどれよりも大きい → 末尾に追加 (LIS が 1 伸びる)
- そうでない → 「x 以上の最初の位置」を **二分探索** で見つけて x で上書き

```
A = 3 1 4 1 5 9 2 6

x=3: tail = [3]
x=1: tail = [1]           (3 を 1 で上書き: 長さ 1 の末尾は 1 が最小)
x=4: tail = [1, 4]
x=1: tail = [1, 4]        (1 以上の最初は tail[0]=1 → 1 で上書き、変化なし)
x=5: tail = [1, 4, 5]
x=9: tail = [1, 4, 5, 9]
x=2: tail = [1, 2, 5, 9]  (4 を 2 で上書き)
x=6: tail = [1, 2, 5, 6]  (9 を 6 で上書き)
→ LIS の長さ = tail のサイズ = 4
```

注意: 最後の `tail` (1 2 5 6) は LIS そのものではありません (2 は 5 より後ろにある)。
LIS そのものが欲しいときは「各要素を置いたときの 1 つ前の要素」を覚えておいて復元します (`lisIndices`)。

### 狭義と広義の違い

| 種類 | 条件 | 二分探索 |
| --- | --- | --- |
| 狭義 (strictly) | a[i] **<** a[j] | x **以上** の最初の位置 (lower_bound) |
| 広義 (non-strictly) | a[i] **≦** a[j] | x **より大きい** 最初の位置 (upper_bound) |

例: A = 2 2 2 なら、狭義 LIS = 1、広義 LIS = 3 です。

## 実装 (Kotlin)

```kotlin
// a の最長増加部分列の長さ。strict = true なら狭義 (<)、false なら広義 (<=)
fun lis(a: LongArray, strict: Boolean = true): Int {
    val tail = LongArray(a.size)  // tail[k] = 長さ k+1 の増加部分列の末尾の最小値
    var len = 0
    for (x in a) {
        // 狭義: x 以上の最初の位置 / 広義: x より大きい最初の位置
        var lo = 0
        var hi = len
        while (lo < hi) {
            val mid = (lo + hi) / 2
            if (if (strict) tail[mid] < x else tail[mid] <= x) lo = mid + 1 else hi = mid
        }
        tail[lo] = x
        if (lo == len) len++
    }
    return len
}

// 狭義 LIS を 1 つ復元し、その添字 (昇順) のリストを返す
fun lisIndices(a: LongArray): List<Int> {
    val n = a.size
    val tailIdx = IntArray(n)       // tailIdx[k] = 長さ k+1 の末尾 (最小) の添字
    val prev = IntArray(n) { -1 }   // prev[i] = LIS で i の 1 つ前の添字
    var len = 0
    for (i in 0 until n) {
        var lo = 0
        var hi = len
        while (lo < hi) {
            val mid = (lo + hi) / 2
            if (a[tailIdx[mid]] < a[i]) lo = mid + 1 else hi = mid
        }
        if (lo > 0) prev[i] = tailIdx[lo - 1]
        tailIdx[lo] = i
        if (lo == len) len++
    }
    val res = ArrayList<Int>()
    var cur = if (len == 0) -1 else tailIdx[len - 1]
    while (cur != -1) {
        res.add(cur)
        cur = prev[cur]
    }
    res.reverse()
    return res
}
```

## 使い方

N と数列 A を読み、狭義 LIS の長さ・広義 LIS の長さ・狭義 LIS の一例を出力します。

```kotlin
fun main() {
    val br = System.`in`.bufferedReader()
    var st = java.util.StringTokenizer("")
    fun next(): String {
        while (!st.hasMoreTokens()) st = java.util.StringTokenizer(br.readLine())
        return st.nextToken()
    }

    val n = next().toInt()
    val a = LongArray(n) { next().toLong() }
    println(lis(a))                  // 狭義
    println(lis(a, strict = false))  // 広義
    val idx = lisIndices(a)
    println(idx.joinToString(" ") { a[it].toString() })

    // 単調減少列の最長なら: lis(LongArray(n) { -a[it] })
}
```

入力例

```
8
3 1 4 1 5 9 2 6
```

出力例

```
4
4
1 4 5 6
```

入力例2

```
5
2 2 1 2 2
```

出力例2

```
2
4
1 2
```

## 例題

- [ABC 038 D - プレゼント](https://atcoder.jp/contests/abc038/tasks/abc038_d) — 箱の入れ子。幅で昇順、**幅が同じなら高さで降順** にソートしてから高さの狭義 LIS
- [典型90 060 - Chimera](https://atcoder.jp/contests/typical90/tasks/typical90_bh) — 「i で終わる LIS」と「i から始まる LDS」を両側から求めて組み合わせる
- [EDPC Q - Flowers](https://atcoder.jp/contests/dp/tasks/dp_q) — 重み付きの LIS (発展)。`dp[高さ] = 美しさの最大` をセグメント木 / BIT で max 取得して更新する

## 注意点・よくあるミス

- **狭義か広義か** を問題文でよく確認する (「真に大きい」「以上」)。二分探索の `<` と `<=` が 1 文字違うだけです。
- `tail` の中身は LIS そのものではありません。列が必要なら `lisIndices` のように復元用の情報を持つ。
- 2 次元の入れ子 (ABC 038 D など) で、片方が等しいものを同時に入れられないときは、
  **同じ幅の中では高さを降順に並べる** と、同じ幅の箱を 2 つ選ぶことがなくなります。
- 「i 番目で終わる LIS の長さ」が各 i について欲しいときは、`lo + 1` (置いた位置 + 1) を記録すれば求まります。
- 部分列 (飛び飛びで OK) と部分文字列・連続部分列 (連続) を取り違えない。連続なら尺取りや単純なループで十分です。
