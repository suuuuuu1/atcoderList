---
title: ランレングス圧縮
category: 基本テクニック
acl: -
genres: 文字列, 実装
order: 2060
summary: 同じ文字が続く部分を (文字, 個数) の組にまとめる
level: 灰
keywords: run length encoding, RLE, 連続する文字, 同じ文字が続く, 連長圧縮, 塊, ブロック, グループ分け
---

## 何ができる？

`"aaabbc"` → `[(a, 3), (b, 2), (c, 1)]` のように、**同じものが連続している塊** を 1 つにまとめます。

| 操作 | 内容 | 計算量 |
| --- | --- | --- |
| `runLength(s: String)` | 文字列を (文字, 連続する個数) のリストにする | O(N) |
| `runLength(a: List<T>)` | 配列 (List) 版。数値の列などにも使える | O(N) |
| `runLengthDecode(runs)` | (文字, 個数) のリストから元の文字列に戻す | O(元の長さ) |

## いつ使う？

- 「同じ文字が **連続している** 部分」「**隣り合う** 同じ色のスライムは合体する」
- 「一番長く続いている部分の長さは？」「塊はいくつある？」
- 「文字列を `a3b2c1` のように圧縮して出力せよ」
- 「0 と 1 の列で、1 が連続している区間ごとに考える」
- 塊ごとの個数が分かれば、「長さ k の塊から取れる部分文字列の数は k(k+1)/2」のように数え上げにも使える

## しくみ

先頭から順に見て、**1 つ前と同じ文字なら個数を +1**、違う文字なら新しい塊を始めます。

```
s = a a a b b c
    ^         (a, 1)
      ^       (a, 2)
        ^     (a, 3)
          ^   b は a と違う → (a, 3) を確定、(b, 1) を開始
            ^ (b, 2)
              ^ c → (b, 2) を確定、(c, 1) を開始
最後に (c, 1) を確定 → [(a,3), (b,2), (c,1)]
```

**最後の塊を確定し忘れない** のが一番のポイントです。下の実装は「塊の終わりを先読みで探す」書き方なので、
最後の塊の処理を書き忘れる心配がありません。

## 実装 (Kotlin)

```kotlin
// 文字列のランレングス圧縮。"aaabbc" → [(a, 3), (b, 2), (c, 1)]
fun runLength(s: String): List<Pair<Char, Int>> {
    val res = ArrayList<Pair<Char, Int>>()
    var i = 0
    while (i < s.length) {
        var j = i
        while (j < s.length && s[j] == s[i]) j++ // s[i] と同じ文字が続く間進む
        res.add(s[i] to (j - i))
        i = j
    }
    return res
}

// List 版 (数値の列などに使う)
fun <T> runLength(a: List<T>): List<Pair<T, Int>> {
    val res = ArrayList<Pair<T, Int>>()
    var i = 0
    while (i < a.size) {
        var j = i
        while (j < a.size && a[j] == a[i]) j++
        res.add(a[i] to (j - i))
        i = j
    }
    return res
}

// 元に戻す。[(a, 3), (b, 2)] → "aaabb"
fun runLengthDecode(runs: List<Pair<Char, Int>>): String {
    val sb = StringBuilder()
    for ((c, k) in runs) repeat(k) { sb.append(c) }
    return sb.toString()
}
```

## 使い方

文字列 S を読み、`文字 個数` をつなげた形 (例: `a3b2c1`) で出力します。
続けて、塊の個数・一番長い塊の長さ・数値の列の圧縮・元に戻した文字列を出力します。

```kotlin
fun main() {
    val s = readLine()!!.trim()
    val runs = runLength(s)

    // 圧縮した形で出力
    println(runs.joinToString("") { (c, k) -> "$c$k" })

    // 塊の個数 (隣り合う同じものが合体した後の個数)
    println(runs.size)

    // 一番長く続く部分の長さ
    println(runs.maxOf { it.second })

    // 数値の列でも使える
    val a = listOf(1, 1, 0, 0, 0, 1)
    println(runLength(a))

    // 元に戻す
    println(runLengthDecode(runs))
}
```

入力例

```
aaabbcaa
```

出力例

```
a3b2c1a2
4
3
[(1, 2), (0, 3), (1, 1)]
aaabbcaa
```

## 例題

- [ABC 019 B - 高橋くんと文字列圧縮](https://atcoder.jp/contests/abc019/tasks/abc019_2) — ランレングス圧縮して出力するだけ
- [ABC 143 C - Slimes](https://atcoder.jp/contests/abc143/tasks/abc143_c) — 隣り合う同じ色は合体 → 答えは塊の個数
- [ABC 329 C - Count xxx](https://atcoder.jp/contests/abc329/tasks/abc329_c) — 文字ごとに「一番長い塊の長さ」を求めて足す

## 注意点・よくあるミス

- 「1 つ前と比べる」書き方では、ループの後に **最後の塊を追加し忘れる** ミスが多いです。
- 同じ文字でも **離れている塊は別** です (`aaabbcaa` の a は 2 つの塊)。文字ごとの合計個数が欲しいなら、
  ランレングスではなく単純に数えます。
- `readLine()` の末尾に空白や `\r` が残ることがあるので `trim()` しておくと安全。
- 個数を文字列にするとき、2 桁以上 (`a12`) になることもあります。`"$c$k"` ならそのまま書けます。
