---
title: LCS と編集距離
category: DP
acl: -
genres: DP, 文字列
order: 3030
summary: 2 つの文字列の最長共通部分列と編集距離を 2 次元 DP で求める
level: 緑
keywords: LCS, longest common subsequence, 最長共通部分列, 編集距離, edit distance, レーベンシュタイン距離, 文字列の比較, 挿入 削除 置換
---

## 何ができる？

2 つの文字列 S (長さ N)、T (長さ M) を比べる DP です。

| 関数 | 内容 | 計算量 |
| --- | --- | --- |
| `lcs(s, t)` | 最長共通部分列 (S にも T にも部分列として含まれる最長の文字列) を **1 つ復元して** 返す | O(NM) |
| `editDistance(s, t)` | S を T に変える最小の操作回数 (1 文字の **挿入・削除・置換**) | O(NM) |

```
LCS:      S = "axyb", T = "abyxb"  → "axb" (長さ 3。"ayb" も正解)
編集距離: "kitten" → "sitting" は 3 回 (k→s 置換, e→i 置換, g を挿入)
```

## いつ使う？

- 2 つの列 (文字列・数列) が与えられ、「共通の部分列」「一致させる」「最小の変更回数」を聞かれる
- 「文字を消す / 挿入する / 書き換える」操作で 2 つを同じにしたい
- N, M ≦ 3000 程度 (N × M ≦ 10^7 くらい) なら 2 次元 DP が間に合う
- 「消すとコスト 1、一致しない位置ごとにコスト 1」のように **コストが少し違う** 問題も、同じ形の DP で解ける

## しくみ

`dp[i][j]` = 「S の先頭 i 文字」と「T の先頭 j 文字」についての答え、とします。
最後の文字 S[i-1] と T[j-1] に注目して場合分けします。

### LCS

```
dp[i][j] = dp[i-1][j-1] + 1                 (S[i-1] == T[j-1] のとき。その文字を使う)
dp[i][j] = max(dp[i-1][j], dp[i][j-1])      (違うとき。どちらかの最後の文字を捨てる)
初期値: dp[0][j] = dp[i][0] = 0
```

```
        ""  a  b  y  x  b      (T)
   ""    0  0  0  0  0  0
   a     0  1  1  1  1  1
   x     0  1  1  1  2  2
   y     0  1  1  2  2  2
   b     0  1  2  2  2  3   ← dp[4][5] = 3
  (S)
```

**復元**: 右下 (N, M) からスタートし、

- S[i-1] == T[j-1] なら、その文字を答えに入れて左上 (i-1, j-1) へ
- そうでなければ、dp の値が同じ方 (上か左) へ進む

を (0 になるまで) 繰り返し、集めた文字を逆順にします。

### 編集距離

```
dp[i][j] = min( dp[i-1][j] + 1,                         (S[i-1] を削除)
                dp[i][j-1] + 1,                         (T[j-1] を挿入)
                dp[i-1][j-1] + (S[i-1] == T[j-1] ? 0 : 1) ) (そのまま / 置換)
初期値: dp[i][0] = i (全部削除), dp[0][j] = j (全部挿入)
```

## 実装 (Kotlin)

```kotlin
// 最長共通部分列を 1 つ返す (長さだけなら .length)
fun lcs(s: String, t: String): String {
    val n = s.length
    val m = t.length
    // dp[i][j] = s の先頭 i 文字と t の先頭 j 文字の LCS の長さ
    val dp = Array(n + 1) { IntArray(m + 1) }
    for (i in 1..n) {
        for (j in 1..m) {
            dp[i][j] = if (s[i - 1] == t[j - 1]) dp[i - 1][j - 1] + 1
                       else maxOf(dp[i - 1][j], dp[i][j - 1])
        }
    }
    // 復元: (n, m) から逆にたどる
    val sb = StringBuilder()
    var i = n
    var j = m
    while (i > 0 && j > 0) {
        if (s[i - 1] == t[j - 1]) {
            sb.append(s[i - 1])
            i--
            j--
        } else if (dp[i - 1][j] >= dp[i][j - 1]) {
            i--
        } else {
            j--
        }
    }
    return sb.reverse().toString()
}

// 編集距離 (1 文字の挿入・削除・置換を各コスト 1 として、s を t にする最小回数)
fun editDistance(s: String, t: String): Int {
    val n = s.length
    val m = t.length
    val dp = Array(n + 1) { IntArray(m + 1) }
    for (i in 0..n) dp[i][0] = i
    for (j in 0..m) dp[0][j] = j
    for (i in 1..n) {
        for (j in 1..m) {
            val replace = dp[i - 1][j - 1] + if (s[i - 1] == t[j - 1]) 0 else 1
            dp[i][j] = minOf(replace, dp[i - 1][j] + 1, dp[i][j - 1] + 1)
        }
    }
    return dp[n][m]
}
```

## 使い方

[EDPC F - LCS](https://atcoder.jp/contests/dp/tasks/dp_f) を解くコードです (LCS を 1 つ出力。複数あればどれでも正解)。
おまけで編集距離も出力しています (EDPC F の提出では 2 行目は消してください)。

```kotlin
fun main() {
    val br = System.`in`.bufferedReader()
    val s = br.readLine().trim()
    val t = br.readLine().trim()
    println(lcs(s, t))
    println(editDistance(s, t))  // おまけ
}
```

入力例

```
axyb
abyxb
```

出力例

```
axb
2
```

入力例2

```
kitten
sitting
```

出力例2

```
ittn
3
```

## 例題

- [EDPC F - LCS](https://atcoder.jp/contests/dp/tasks/dp_f) — LCS の復元まで。`lcs` そのもの
- [ABC 185 E - Sequence Matching](https://atcoder.jp/contests/abc185/tasks/abc185_e) — 編集距離の変形 (削除コスト 1、残した位置で値が違えばコスト 1)。
  遷移は `dp[i-1][j] + 1`, `dp[i][j-1] + 1`, `dp[i-1][j-1] + (A≠B なら 1)` で、`editDistance` とまったく同じ形

## 注意点・よくあるミス

- 添字ずれ: `dp[i][j]` は「先頭 i 文字」なので、見る文字は `s[i - 1]`。dp 配列のサイズは `(n + 1) × (m + 1)`。
- LCS の復元で **一致した文字を取る条件は `s[i-1] == t[j-1]`** です。「dp[i][j] が増えたら取る」と書くと間違えやすい。
- N = M = 3000 だと dp は 900 万要素。`Array(n + 1) { IntArray(m + 1) }` なら約 36MB で OK。
  `Array<Array<Int>>` や `List` を使うと遅く・重くなるので避ける。
- 文字列の連結 `ans = c + ans` を復元のループで繰り返すと O(N^2)。`StringBuilder` に足して最後に `reverse()`。
- 編集距離の操作手順も知りたいときは、LCS と同じように (N, M) から「どの遷移で最小になったか」を逆にたどれば復元できます。
- 「部分列」(飛び飛び OK) と「部分文字列」(連続) は別物。最長共通 **部分文字列** なら
  `dp[i][j] = (一致なら dp[i-1][j-1] + 1、違えば 0)` の最大値です。
