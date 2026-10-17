---
title: 接尾辞配列 (Suffix Array) と LCP 配列
category: 文字列
acl: suffix_array/lcp_array
genres: 文字列, ソート
order: 410
summary: 全ての接尾辞を辞書順に並べ、部分文字列の問題を解く
---

## 何ができる？

文字列 `S` (長さ N) の「接尾辞 (suffix)」= `S[i..]` を全部辞書順に並べた配列を作ります。

| 操作 | 内容 | 計算量 |
|---|---|---|
| `suffixArray(s)` | `sa[k]` = 辞書順で k 番目に小さい接尾辞の開始位置 | O(N log N) |
| `suffixArray(a, upper)` | 整数列版 (要素は 0 以上 upper 以下) | O(N log N) |
| `lcpArray(s, sa)` | `lcp[k]` = 接尾辞 `sa[k]` と `sa[k+1]` の最長共通接頭辞の長さ (長さ N-1) | O(N) |

ACL の `suffix_array` / `lcp_array` と同じ意味の値を返します (ACL は SA-IS で O(N) ですが、ここでは分かりやすいダブリング法で書いています。N = 5×10^5 でも 0.1 秒程度です)。

## いつ使う？

- 「**異なる部分文字列**の個数」を数えたい
- 「部分文字列全部」についての和や個数 (N^2 個あるので愚直では間に合わない)
- 2 つの接尾辞の**最長共通接頭辞 (LCP)** をたくさん聞かれる (LCP 配列の区間最小値で求まる)
- 2 つの文字列 S, T の共通部分文字列を扱う (`S + "#" + T` のようにつなげて作る)
- 文字列の中にパターンが現れるか? を何度も聞かれる (sa 上で二分探索)

## しくみ

### 接尾辞配列

`S = "abcab"` の接尾辞を辞書順に並べると次のようになります。

```
 k | sa[k] | 接尾辞   | lcp[k] (次の行との共通接頭辞)
---+-------+----------+-----
 0 |   3   | ab       | 2  ("ab")
 1 |   0   | abcab    | 0
 2 |   4   | b        | 1  ("b")
 3 |   1   | bcab     | 0
 4 |   2   | cab      |
```

辞書順に並べると、**似ている接尾辞が隣同士に集まる** のがポイントです。

### ダブリング (作り方)

1. まず先頭 1 文字だけで並べ、順位 `rank[i]` をつけます。
2. 「先頭 2k 文字での順位」は、ペア `(rank[i], rank[i+k])` (先頭 k 文字の順位, その続き k 文字の順位) で比べれば決まります。
3. k = 1, 2, 4, 8, ... と倍々にしていき、順位が全部バラバラになったら終わり。

```
S = "abcab"
k=1 の順位 :  a b c a b  ->  0 1 2 0 1
ペア(k=1)  : (0,1) (1,2) (2,0) (0,1) (1,-)    ※ はみ出しは -1 (最小)
2 文字順位 :   0     2     3     0     1    ... まだ同順位あり → k=2 へ
```

ペアのソートを計数ソート (2 回の安定ソート) で行うと 1 段階 O(N)、段階数は log N なので全体 O(N log N) です。

### LCP 配列 (Kasai 法)

元の文字列の位置 i = 0, 1, 2, ... の順に「辞書順で 1 つ前の接尾辞との LCP」を求めます。
`S[i..]` の LCP が h なら `S[i+1..]` の LCP は **h-1 以上** になる、という性質があるので、h を毎回 0 から数え直さず 1 減らすだけで続きから伸ばせます。h が増える回数は合計 2N 以下なので O(N) です。

### 異なる部分文字列の個数

部分文字列 = 「どれかの接尾辞の接頭辞」です。接尾辞 `sa[k]` の接頭辞は (N - sa[k]) 個ありますが、そのうち `lcp[k-1]` 個は 1 つ前の接尾辞の接頭辞と重複します。よって

```
答え = N(N+1)/2 - (lcp の総和)
```

## 実装 (Kotlin)

```kotlin
// 接尾辞配列 (ACL の suffix_array と同じ挙動)
// s の各要素は 0 以上 upper 以下。戻り値 sa[i] = 辞書順で i 番目の接尾辞の開始位置
fun suffixArray(s: IntArray, upper: Int): IntArray {
    val n = s.size
    if (n == 0) return IntArray(0)
    val sa = IntArray(n)
    val rank = IntArray(n)
    val tmp = IntArray(n)
    val sa2 = IntArray(n)
    val cnt = IntArray(maxOf(upper + 1, n) + 1)

    // 1 文字目だけで計数ソート
    for (x in s) cnt[x]++
    for (i in 1 until cnt.size) cnt[i] += cnt[i - 1]
    for (i in n - 1 downTo 0) sa[--cnt[s[i]]] = i
    rank[sa[0]] = 0
    for (i in 1 until n) {
        rank[sa[i]] = rank[sa[i - 1]] + if (s[sa[i]] != s[sa[i - 1]]) 1 else 0
    }

    // ダブリング: 先頭 k 文字の順位から先頭 2k 文字の順位を作る
    var k = 1
    while (rank[sa[n - 1]] < n - 1) { // 順位が全部バラバラになったら終了
        // (1) 第2キー rank[i+k] で並べる (i+k >= n は「空」で最小)
        var p = 0
        for (i in n - k until n) sa2[p++] = i
        for (i in 0 until n) if (sa[i] >= k) sa2[p++] = sa[i] - k
        // (2) 第1キー rank[i] で安定な計数ソート
        cnt.fill(0, 0, n)
        for (i in 0 until n) cnt[rank[i]]++
        for (i in 1 until n) cnt[i] += cnt[i - 1]
        for (i in n - 1 downTo 0) sa[--cnt[rank[sa2[i]]]] = sa2[i]
        // (3) 新しい順位を振り直す
        tmp[sa[0]] = 0
        for (i in 1 until n) {
            val a = sa[i - 1]
            val b = sa[i]
            val ra = if (a + k < n) rank[a + k] else -1
            val rb = if (b + k < n) rank[b + k] else -1
            tmp[b] = tmp[a] + if (rank[a] == rank[b] && ra == rb) 0 else 1
        }
        tmp.copyInto(rank)
        k *= 2
    }
    return sa
}

fun suffixArray(s: String): IntArray = suffixArray(IntArray(s.length) { s[it].code }, 65535)

// LCP 配列 (Kasai 法, O(N))。長さ n-1
// lcp[i] = sa[i] から始まる接尾辞と sa[i+1] から始まる接尾辞の最長共通接頭辞の長さ
fun lcpArray(s: IntArray, sa: IntArray): IntArray {
    val n = s.size
    if (n == 0) return IntArray(0)
    val rnk = IntArray(n)
    for (i in 0 until n) rnk[sa[i]] = i
    val lcp = IntArray(n - 1)
    var h = 0
    for (i in 0 until n) {
        if (h > 0) h--
        if (rnk[i] == 0) continue
        val j = sa[rnk[i] - 1] // 辞書順で 1 つ前の接尾辞
        while (j + h < n && i + h < n && s[j + h] == s[i + h]) h++
        lcp[rnk[i] - 1] = h
    }
    return lcp
}

fun lcpArray(s: String, sa: IntArray): IntArray = lcpArray(IntArray(s.length) { s[it].code }, sa)
```

## 使い方

```kotlin
// practice2_i: 異なる部分文字列の個数
fun main() {
    val s = readLine()!!.trim()
    val n = s.length
    val sa = suffixArray(s)
    val lcp = lcpArray(s, sa)
    // 部分文字列の総数 n(n+1)/2 から、隣り合う接尾辞で重複する分 (lcp) を引く
    var ans = n.toLong() * (n + 1) / 2
    for (x in lcp) ans -= x
    println(ans)
}
```

入力 `abcbcba` → 出力 `21`、入力 `mississippi` → 出力 `53` (practice2_i の入力例と一致)。

## 例題

- [AtCoder Library Practice Contest I - Number of Substrings](https://atcoder.jp/contests/practice2/tasks/practice2_i) … 上の「使い方」そのもの
- [ABC 213 F - Common Prefixes](https://atcoder.jp/contests/abc213/tasks/abc213_f) … LCP 配列の区間最小値の和をスタックで計算
- [ABC 272 F - Two Strings](https://atcoder.jp/contests/abc272/tasks/abc272_f) … `S+S` と `T+T` をつなげた文字列の接尾辞配列で順位を比べる

## 注意点・よくあるミス

- 答えが N(N+1)/2 程度になるので **Long** を使う (N = 5×10^5 で約 1.25×10^11)。
- `lcpArray` の長さは **N-1** です (N ではない)。`lcp[k]` は `sa[k]` と `sa[k+1]` の比較。
- 2 つの文字列をつなげるときは、どちらにも出てこない区切り文字 (`#`, `$` など) を挟む。挟まないと境界をまたいだ共通部分が数えられてしまう。
- 任意の 2 接尾辞 `sa[a]`, `sa[b]` (a < b) の LCP は `min(lcp[a], ..., lcp[b-1])`。たくさん聞かれるならセグメント木や Sparse Table と組み合わせる。
- 整数列版 `suffixArray(a, upper)` は要素が 0 以上 upper 以下である必要があります。大きい値は座標圧縮してから渡す。
- 文字列の比較を `sortedWith` などで直接行う方法は O(N^2 log N) になり TLE します。
