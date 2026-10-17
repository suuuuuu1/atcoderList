---
title: Z アルゴリズム
category: 文字列
acl: z_algorithm
genres: 文字列
order: 420
summary: 各位置から始まる部分と先頭との一致長を O(N) で求める
level: 水
keywords: Z algorithm, 最長共通接頭辞, パターン検索, 文字列検索, prefix
---

## 何ができる？

文字列 `S` (長さ N) について、次の配列 `z` を **O(N)** で求めます。

| 操作 | 内容 | 計算量 |
|---|---|---|
| `zAlgorithm(s)` | `z[i]` = `S` と `S[i..]` の最長共通接頭辞の長さ (`z[0] = N`) | O(N) |
| `zAlgorithm(a)` | 整数列 (`IntArray`) 版 | O(N) |

ACL の `z_algorithm` と同じ値を返します (`z[0] = N` も ACL と同じ)。

```
S    = a b a a b a b
i    = 0 1 2 3 4 5 6
z[i] = 7 0 1 3 0 2 0
```

例えば `z[3] = 3` は「`S[3..] = "abab"` と `S = "abaabab"` は先頭 3 文字 `"aba"` まで一致する」という意味です。

## いつ使う？

- 文字列 S の中に**パターン P が現れる位置を全部**知りたい (P + "$" + S の Z 配列)
- 「先頭と何文字一致するか」を**全ての位置**について知りたい
- 「接頭辞 = 接尾辞」になる長さ、周期性を調べたい
- 数列の一致判定 (差分列などを作ってから Z アルゴリズム、のような使い方も)
- 愚直に全位置から比べると O(N^2) で間に合わない (N ≥ 10^5 程度)

## しくみ

左から順に z[i] を求めていきます。そのとき「今まで見つけた中で一番右まで伸びた一致区間」`[l, r)` を覚えておきます。
この区間では `S[l..r)` = `S[0..r-l)` (先頭と一致) であることが分かっています。

```
        l         i       r
S :     [=========|=======)........
           同じ文字列
先頭:   [=========|=======)
        0        i-l     r-l
```

- `i < r` なら、`S[i..r)` は `S[i-l..r-l)` と同じ文字列。なので **z[i] は少なくとも min(r - i, z[i-l])** と分かる (過去の結果を再利用)。
- そこから先 (r より右) だけ 1 文字ずつ愚直に比べて伸ばす。
- 伸ばした結果 `i + z[i] > r` なら、`[l, r) = [i, i + z[i])` に更新。

r は右にしか動かず、愚直に比べて成功するたびに r が 1 増えるので、全体で O(N) になります。

### パターン検索

S の中から P を探すには、`T = P + "$" + S` (`$` は P にも S にも出てこない文字) の Z 配列を作ります。
`$` があるので z の値は |P| を超えません。`z[|P| + 1 + i] == |P|` なら、`S` の位置 i から P が現れています。

```
P = "ab", S = "abaabab"
T = a b $ a b a a b a b
z = 10 0 0 2 0 1 2 0 2 0
          ↑     ↑   ↑      z = 2 = |P| の所 → S の位置 0, 3, 5
```

## 実装 (Kotlin)

```kotlin
// Z アルゴリズム (ACL の z_algorithm と同じ挙動)
// z[i] = s と s[i..] の最長共通接頭辞の長さ (z[0] = n)。O(N)
fun zAlgorithm(s: IntArray): IntArray {
    val n = s.size
    if (n == 0) return IntArray(0)
    val z = IntArray(n)
    z[0] = n
    var l = 0
    var r = 0 // [l, r) は「s の先頭と一致していると分かっている区間」
    for (i in 1 until n) {
        var k = if (i < r) minOf(r - i, z[i - l]) else 0 // 過去の結果を再利用
        while (i + k < n && s[k] == s[i + k]) k++          // はみ出した分だけ愚直に伸ばす
        z[i] = k
        if (i + k > r) { l = i; r = i + k }
    }
    return z
}

fun zAlgorithm(s: String): IntArray = zAlgorithm(IntArray(s.length) { s[it].code })
```

## 使い方

```kotlin
// 入力: 1 行目に文字列 S, 2 行目にパターン P
fun main() {
    val s = readLine()!!.trim()
    val p = readLine()!!.trim()

    // 例1: 各位置の最長共通接頭辞 (S と S[i..])
    val z = zAlgorithm(s)
    println(z.joinToString(" "))

    // 例2: パターン検索。S にも P にも出てこない文字 '$' で区切ってつなげる
    val t = p + "$" + s
    val zt = zAlgorithm(t)
    val pos = mutableListOf<Int>()
    for (i in s.indices) {
        if (zt[p.length + 1 + i] >= p.length) pos.add(i) // S[i..] が P で始まる
    }
    println(pos.joinToString(" ")) // P が現れる開始位置 (0-indexed)
}
```

入力
```
abaabab
ab
```
出力
```
7 0 1 3 0 2 0
0 3 5
```

## 例題

- [ABC 141 E - Who Says a Pun?](https://atcoder.jp/contests/abc141/tasks/abc141_e) … 各開始位置 i について `S[i..]` の Z 配列を作り、重ならない一致の最大長を求める (O(N^2))
- [ABC 150 F - Xor Shift](https://atcoder.jp/contests/abc150/tasks/abc150_f) … 隣同士の XOR を取った数列でパターン検索 (整数列版を使う)
- [ABC 284 F - ABCBAC](https://atcoder.jp/contests/abc284/tasks/abc284_f) … 先頭・末尾との一致長を Z 配列で求めて全ての分割位置を判定
- ※ AtCoder Library Practice Contest には Z アルゴリズム単体の問題はありません。

## 注意点・よくあるミス

- ACL と同じく **`z[0] = N`** です (0 にする流儀の解説もあるので混同に注意)。
- パターン検索で区切り文字を入れ忘れると、z の値が |P| を超えて S と P をまたいだ一致になる。`>= p.length` で判定すれば区切りなしでも動く場合はあるが、区切りを入れるのが安全。
- 区切り文字は S にも P にも現れない文字を選ぶ。整数列なら -1 や「最大値 + 1」など。
- 求まるのは「**先頭**との一致長」だけ。任意の 2 位置の一致長が必要なら接尾辞配列 + LCP 配列やローリングハッシュを使う。
- `S` と `S[i..]` の一致長は最大 N - i なので、`i + z[i]` が N を超えることはない。
