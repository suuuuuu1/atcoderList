---
title: DP の基本 (Frog・ナップサック・部分和)
category: DP
acl: -
genres: DP
order: 3010
summary: 状態と遷移を決め、小さい問題の答えから順に表を埋める
level: 茶
keywords: 動的計画法, dynamic programming, ナップサック, knapsack, 部分和, Frog, 配るDP, 貰うDP, 漸化式, 最大価値
---

## 何ができる？

DP (動的計画法) は「**小さい問題の答えを表 (配列) に覚えておき、それを使って大きい問題の答えを作る**」方法です。
全探索だと 2^N 通りになる問題が、O(N × 何か) で解けるようになります。

このページの実装でできること:

| 関数 | 内容 | 計算量 |
| --- | --- | --- |
| `frog1(h)` | 1 段か 2 段ずつ進むカエルの最小コスト (貰う DP) | O(N) |
| `frog2(h, k)` | 1〜k 段ずつ進むカエルの最小コスト (配る DP) | O(NK) |
| `knapsackByWeight(w, v, cap)` | 重さの合計 cap 以下での価値の最大値 (**重さ** が小さいとき) | O(N × cap) |
| `knapsackByValue(w, v, cap)` | 同じ問題を **価値** の合計で表を作って解く (価値が小さいとき) | O(N × Σv) |
| `subsetSum(a, s)` | いくつか選んで和を 0, 1, ..., s にできるか | O(N × s) |

## いつ使う？

- 「N 個の中からいくつか選ぶ」で、選び方が 2^N 通りあって全探索できない (N ≦ 100 など)
- 「i 番目まで見たとき」の情報が **少しの数** (今の重さ、今の位置、今の和…) で表せる
- 「最小コスト」「最大価値」「何通り (mod 10^9+7)」「できるか？(Yes/No)」を聞かれる
- 制約の中に **小さめの数** (W ≦ 10^5、和 ≦ 10^4 など) があり、それを配列の添字に使えそう
- ナップサックの見分け方:
  - 重さの上限 W が 10^5 程度 → `dp[重さ] = 価値の最大` (重さ版)
  - W が 10^9 と大きいが、価値の合計が 10^5 程度 → `dp[価値] = 重さの最小` (価値版)

## しくみ

DP を作るときは、次の 3 つを決めます。

1. **状態**: `dp[i][j]` が何を表すか、日本語で言えるようにする
   (例: 「i 個目まで見て、重さの合計が j のときの価値の最大値」)
2. **遷移**: `dp[i][j]` から次の状態へどう移るか (「i 個目を選ぶ / 選ばない」など)
3. **初期値と答え**: `dp[0][0] = 0`、それ以外は「ありえない」値 (-∞ や +∞)。答えはどこを見るか

### 貰う DP と 配る DP

Frog 1 (足場 i に着くまでの最小コスト) で比べます。

```
貰う DP: dp[i] を「前の状態から貰って」決める
  dp[i] = min(dp[i-1] + |h[i]-h[i-1]|, dp[i-2] + |h[i]-h[i-2]|)

配る DP: dp[i] が決まったら「後ろの状態へ配る」
  dp[i+1] = min(dp[i+1], dp[i] + |h[i+1]-h[i]|)
  dp[i+2] = min(dp[i+2], dp[i] + |h[i+2]-h[i]|)

  i:    0    1    2    3
  h:   10   30   40   20
  dp:   0   20   30   30   ← 答えは dp[3] = 30
```

どちらで書いても答えは同じです。書きやすい方を選べば OK。
配る DP は「最初は全部 +∞ にしておき、min で更新する」のがポイントです。

### ナップサック (重さ版)

`dp[j]` = 重さの合計がちょうど j のときの価値の最大 (ありえなければ -∞)。
品物 (w, v) を 1 つずつ見て、

```
dp[j] = max(dp[j], dp[j - w] + v)      (選ぶ場合)
```

と更新します。1 次元配列で書くときは **j を大きい方から小さい方へ** 回すのがコツです
(小さい方から回すと、同じ品物を何回も選んでしまう)。

### ナップサック (価値版)

W が 10^9 だと配列が作れません。代わりに価値の合計 (≦ N × max v = 10^5 程度) を添字にします。

`dp[x]` = 価値の合計がちょうど x になるときの **重さの最小**。
最後に `dp[x] <= W` を満たす最大の x が答えです。

### 部分和

`dp[j]` = 和をちょうど j にできるか (true/false)。ナップサックの「価値なし版」です。

## 実装 (Kotlin)

```kotlin
// Frog 1 (貰う DP): 足場 i から i+1 か i+2 へ跳ぶ。コストは高さの差の絶対値
fun frog1(h: LongArray): Long {
    val n = h.size
    val dp = LongArray(n)  // dp[i] = 足場 i に着くまでの最小コスト
    for (i in 1 until n) {
        dp[i] = dp[i - 1] + Math.abs(h[i] - h[i - 1])
        if (i >= 2) dp[i] = minOf(dp[i], dp[i - 2] + Math.abs(h[i] - h[i - 2]))
    }
    return dp[n - 1]
}

// Frog 2 (配る DP): 足場 i から i+1, ..., i+k へ跳べる
fun frog2(h: LongArray, k: Int): Long {
    val n = h.size
    val dp = LongArray(n) { Long.MAX_VALUE }
    dp[0] = 0
    for (i in 0 until n) {
        for (j in i + 1..minOf(i + k, n - 1)) {
            dp[j] = minOf(dp[j], dp[i] + Math.abs(h[j] - h[i]))
        }
    }
    return dp[n - 1]
}

// ナップサック (重さ版): 重さの合計 cap 以下で価値の合計の最大値。O(N * cap)
fun knapsackByWeight(w: IntArray, v: LongArray, cap: Int): Long {
    val NG = Long.MIN_VALUE / 4
    val dp = LongArray(cap + 1) { NG }  // dp[j] = 重さちょうど j での価値の最大
    dp[0] = 0
    for (i in w.indices) {
        for (j in cap downTo w[i]) {  // 大きい方から回す (同じ品物を 2 回使わない)
            if (dp[j - w[i]] != NG) dp[j] = maxOf(dp[j], dp[j - w[i]] + v[i])
        }
    }
    return dp.max()
}

// ナップサック (価値版): 重さが大きく、価値の合計が小さいとき。O(N * Σv)
fun knapsackByValue(w: LongArray, v: IntArray, cap: Long): Int {
    val sumV = v.sum()
    val INF = Long.MAX_VALUE / 4
    val dp = LongArray(sumV + 1) { INF }  // dp[x] = 価値ちょうど x での重さの最小
    dp[0] = 0
    for (i in w.indices) {
        for (x in sumV downTo v[i]) {
            if (dp[x - v[i]] != INF) dp[x] = minOf(dp[x], dp[x - v[i]] + w[i])
        }
    }
    for (x in sumV downTo 0) if (dp[x] <= cap) return x
    return 0
}

// 部分和: a からいくつか選んで (各 1 回まで) 和を j にできるか。j = 0..s
fun subsetSum(a: IntArray, s: Int): BooleanArray {
    val dp = BooleanArray(s + 1)
    dp[0] = true
    for (x in a) {
        for (j in s downTo x) {
            if (dp[j - x]) dp[j] = true
        }
    }
    return dp
}
```

## 使い方

[EDPC D - Knapsack 1](https://atcoder.jp/contests/dp/tasks/dp_d) を解くコードです
(N 個の品物 (重さ w, 価値 v) から、重さの合計 W 以下で価値の合計を最大化)。

```kotlin
fun main() {
    val br = System.`in`.bufferedReader()
    var st = java.util.StringTokenizer("")
    fun next(): String {
        while (!st.hasMoreTokens()) st = java.util.StringTokenizer(br.readLine())
        return st.nextToken()
    }

    val n = next().toInt()
    val cap = next().toInt()
    val w = IntArray(n)
    val v = LongArray(n)
    for (i in 0 until n) {
        w[i] = next().toInt()
        v[i] = next().toLong()
    }
    println(knapsackByWeight(w, v, cap))

    // EDPC E - Knapsack 2 (W ≦ 10^9, v ≦ 10^3) なら価値版を使う:
    // println(knapsackByValue(wLong, vInt, capLong))
}
```

入力例

```
3 8
3 30
4 50
5 60
```

出力例

```
90
```

## 例題

- [EDPC A - Frog 1](https://atcoder.jp/contests/dp/tasks/dp_a) — `frog1` そのもの。[EDPC B - Frog 2](https://atcoder.jp/contests/dp/tasks/dp_b) は `frog2`
- [EDPC D - Knapsack 1](https://atcoder.jp/contests/dp/tasks/dp_d) — 重さ版ナップサック。答えは最大で 10^11 程度なので Long
- [EDPC E - Knapsack 2](https://atcoder.jp/contests/dp/tasks/dp_e) — W ≦ 10^9 なので価値版ナップサック
- [EDPC C - Vacation](https://atcoder.jp/contests/dp/tasks/dp_c) — 状態に「前日にした活動」を持たせる練習

## 注意点・よくあるミス

- **dp の意味を先に日本語で書く**。「dp[j] は重さ **ちょうど** j」か「重さ j **以下**」かで初期値と答えの取り方が変わります
  (上の実装は「ちょうど」なので、答えは `dp.max()`)。
- 1 次元配列のナップサックで **j を小さい方から回すと、同じ品物を何回でも選ぶ** 計算になります。
  各品物 1 個まで (0-1 ナップサック) なら大きい方から。何個でも選べる (個数制限なし) なら逆に小さい方から回せば OK です。
- 2 次元 `dp[i][j]` で書くときは、「選ばない」遷移 `dp[i+1][j] = dp[i][j]` を忘れがち。
- 「ありえない状態」(-∞ / +∞) に足し算すると、`Long.MAX_VALUE + 1` がオーバーフローして負の数になります。
  INF は `Long.MAX_VALUE / 4` などにするか、上のように `!= INF` をチェックしてから足す。
- 価値や重さの合計は Int を超えやすい (10^9 × 100 など)。迷ったら Long。
- 「何通り」を数える DP は、足すたびに `% MOD` を取る。
