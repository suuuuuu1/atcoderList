---
title: Kotlin の文字列操作の基本
category: 文字列
acl: -
genres: 文字列
order: 401
summary: CharArray・StringBuilder・文字と数字の変換・回文・文字数の数え方
level: 灰
keywords: String, CharArray, StringBuilder, 回文, palindrome, 文字コード, 部分文字列, substring, ランレングス圧縮, 文字の種類
---

## 何ができる？

Kotlin の `String` は **書き換えできない (イミュータブル)** ので、目的に合わせて道具を使い分けます。

| やりたいこと | 書き方 | 計算量 |
| --- | --- | --- |
| i 文字目を見る | `s[i]` (Char 型) | O(1) |
| 長さ | `s.length` | O(1) |
| 部分文字列 s[l, r) | `s.substring(l, r)` (r 文字目は含まない) | O(r - l) |
| 一部を書き換える | `val a = s.toCharArray()` → `a[i] = 'x'` → `String(a)` | O(N) で変換、書き換えは O(1) |
| 少しずつ組み立てる | `StringBuilder` の `append` → `toString()` | append は O(1) (償却) |
| 反転 | `s.reversed()` / `sb.reverse()` | O(N) |
| 中身が同じか | `s == t` (Kotlin の `==` は中身を比べる) | O(N) |
| t で始まるか / i 文字目から t か | `s.startsWith(t)` / `s.startsWith(t, i)` | O(\|t\|) |

**文字 ↔ 数字の変換**

| 変換 | 書き方 | 例 |
| --- | --- | --- |
| 数字の文字 → Int | `c - '0'` | `'7' - '0'` = 7 |
| Int (0〜9) → 数字の文字 | `'0' + d` | `'0' + 3` = `'3'` |
| 英小文字 → 0〜25 | `c - 'a'` | `'c' - 'a'` = 2 |
| 0〜25 → 英小文字 | `'a' + i` | `'a' + 25` = `'z'` |
| 文字列 → 数 | `s.toInt()` / `s.toLong()` | `"123".toInt()` = 123 |
| 大文字 ↔ 小文字 | `c.uppercaseChar()` / `c.lowercaseChar()` | |
| 判定 | `c.isDigit()` / `c.isLowerCase()` / `c.isUpperCase()` / `c.isLetter()` | |

このページで用意している関数:

| 関数 | 内容 | 計算量 |
| --- | --- | --- |
| `isPalindrome(s)` | 回文か | O(N) |
| `countLower(s)` | 'a'〜'z' それぞれの個数 (`IntArray(26)`) | O(N) |
| `countOccurrences(s, t)` | s の中に t が何回出てくるか (重なりも数える) | O(\|s\| \|t\|) |
| `runLength(s)` | ランレングス圧縮 (同じ文字の連続をまとめる) | O(N) |

## いつ使う？

- 文字列の問題は (ほぼ) 全部。特に A・B・C 問題
- 「文字を 1 つ変える」「i 文字目と j 文字目を入れ替える」→ **CharArray**
- 答えの文字列を作る、たくさんの行を出力する → **StringBuilder**
- 「各文字が何回出てくるか」「アナグラム (並べ替えて同じ) か」→ **IntArray(26)** で数える
- 「同じ文字が続く区間」「圧縮」→ **ランレングス圧縮**
- 数字の各桁を使う (桁の和など) → `s` のまま読んで `c - '0'`

## しくみ

**String と CharArray と StringBuilder**

```
String        "abc"   … 書き換え不可。s += "d" は毎回まるごとコピー (遅い)
CharArray     [a,b,c] … 長さ固定の配列。a[1] = 'x' で書き換えられる
StringBuilder "abc"   … 末尾に追加できる可変長の文字列。最後に toString()
```

**Char は「文字コードの番号」** です。`'a'` は 97、`'b'` は 98、…と連続しているので、
`c - 'a'` で 0〜25 の番号になります。同じく `'0'`〜`'9'` も連続しているので `c - '0'` で数字になります。

**部分文字列の数**: 長さ N の文字列の (空でない) 部分文字列 s[l, r) は、l と r の選び方で
N(N+1)/2 個あります。N が数百なら全部列挙できますが、N = 2×10^5 では無理です。

**回文判定**: 左端と右端から 1 文字ずつ比べながら内側へ進みます。`s == s.reversed()` でも同じ結果です。

**ランレングス圧縮**

```
"aaabccdd" → (a,3) (b,1) (c,2) (d,2)
 i を先頭に、同じ文字が続く限り j を進める → (s[i], j - i) を記録 → i = j
```

## 実装 (Kotlin)

```kotlin
// 回文 (前から読んでも後ろから読んでも同じ) か判定する
fun isPalindrome(s: String): Boolean {
    var l = 0
    var r = s.length - 1
    while (l < r) {
        if (s[l] != s[r]) return false
        l++
        r--
    }
    return true
}

// 英小文字の出現回数: cnt[0] が 'a' の個数, cnt[1] が 'b' の個数, ..., cnt[25] が 'z' の個数
fun countLower(s: String): IntArray {
    val cnt = IntArray(26)
    for (c in s) cnt[c - 'a']++
    return cnt
}

// s の中に t が何回現れるか (重なりも数える)。O(|s| |t|)
fun countOccurrences(s: String, t: String): Int {
    var res = 0
    for (i in 0..s.length - t.length) {
        if (s.startsWith(t, i)) res++
    }
    return res
}

// ランレングス圧縮: "aaabcc" -> [(a, 3), (b, 1), (c, 2)]
fun runLength(s: String): List<Pair<Char, Int>> {
    val res = mutableListOf<Pair<Char, Int>>()
    var i = 0
    while (i < s.length) {
        var j = i
        while (j < s.length && s[j] == s[i]) j++
        res.add(s[i] to (j - i))
        i = j
    }
    return res
}
```

## 使い方

1 行で文字列 S を読み、いろいろな操作をしてみる例です。

```kotlin
fun main() {
    val s = readln().trim()

    // ---- 1. 文字 <-> 数字 ----
    val d = '7' - '0'        // 7     (数字の文字 -> Int)
    val c = '0' + 3          // '3'   (Int -> 数字の文字)
    val idx = 'c' - 'a'      // 2     (英小文字 -> 0〜25)
    val ch = 'a' + 25        // 'z'   (0〜25 -> 英小文字)
    val num = "123".toInt()  // 123   (文字列 -> Int)
    println("$d $c $idx $ch $num")
    println("${'a'.uppercaseChar()} ${'Q'.lowercaseChar()} ${'5'.isDigit()} ${'x'.isLowerCase()}") // A q true true

    // ---- 2. CharArray: 文字列の一部を書き換えたいとき ----
    val arr = s.toCharArray()
    arr[0] = 'X'
    println(String(arr))     // 先頭だけ X に変わる (s 自体は変わらない)

    // ---- 3. StringBuilder: 文字列を少しずつ組み立てるとき ----
    val sb = StringBuilder()
    for (i in 0 until 5) sb.append(i)
    sb.reverse()
    println(sb)              // 43210

    // ---- 4. 部分文字列 s[l, r) と、全部の部分文字列の列挙 (O(N^2) 個) ----
    println(s.substring(1, 3))
    val distinct = HashSet<String>()
    for (l in 0 until s.length) {
        for (r in l + 1..s.length) distinct.add(s.substring(l, r))
    }
    println(distinct.size)   // 異なる部分文字列の個数 (N が数百まで)

    // ---- 5. 上の関数を使う ----
    println(if (isPalindrome(s)) "Yes" else "No")
    println(countLower(s).count { it > 0 })                                   // 文字の種類数
    println(countOccurrences(s, "ab"))
    println(runLength(s).joinToString(" ") { "${it.first}${it.second}" })
}
```

入力例

```
abaabba
```

出力例

```
7 3 2 z 123
A q true true
Xbaabba
43210
ba
21
No
2
2
a1 b1 a2 b2 a1
```

## 例題

- [ABC 146 B - ROT N](https://atcoder.jp/contests/abc146/tasks/abc146_b) — 文字を N 個後ろにずらす。`'A' + (c - 'A' + n) % 26`
- [ABC 198 B - Palindrome with leading zeros](https://atcoder.jp/contests/abc198/tasks/abc198_b) — 末尾の 0 を消してから回文判定
- [ABC 049 C - 白昼夢](https://atcoder.jp/contests/abc049/tasks/arc065_a) — 文字列を反転すると、前から貪欲に単語を当てはめられる

## 注意点・よくあるミス

- ループの中で `s += c` をすると、毎回コピーが起きて **O(N^2)** になり TLE します。`StringBuilder` を使う。
- 出力が多いときも `println` を何度も呼ばず、`StringBuilder` にためて最後に 1 回 `print` する。
- `c.code` は文字コードそのもの (`'7'.code` は 55) です。数字が欲しいなら `c - '0'` (= 7)。
  `c.toInt()` は非推奨なので使わない。
- `'0' + 3` は `'3'` (Char) ですが、`"0" + 3` は `"03"` (String) です。シングルとダブルのクォートに注意。
- `substring(l, r)` の r は **含まない** (s[l, r) )。長さは r - l です。
- `substring` は新しい文字列を作るので O(長さ) かかります。ループの中で大量に作ると遅くなります。
- 入力の文字列は `readln()` で 1 行読めます。数値と同じ行にあるときは `split(" ")` で区切る。
- 文字列の比較に `===` を使わない。Kotlin では `==` で中身を比べます。
