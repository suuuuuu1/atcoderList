---
title: bit全探索
category: 全探索・探索
acl: -
genres: bit全探索, 全探索
order: 1020
summary: N 個から「選ぶ/選ばない」の 2^N 通りを整数の bit で全部試す
level: 茶
keywords: bit全探索, 部分集合, subset, 選ぶ選ばない, 2のN乗, ビット演算, mask, 組み合わせ全列挙
---

## 何ができる？

N 個のものそれぞれについて「選ぶ / 選ばない」(または「+ / -」「ON / OFF」など 2 択) を決める
**2^N 通りすべて** を、0 〜 2^N - 1 の整数で表して順番に試します。

| 操作 | 書き方 | 計算量 |
| --- | --- | --- |
| すべての選び方を回す | `for (mask in 0 until (1 shl n))` | 2^N 通り |
| i 番目を選んでいるか | `(mask shr i) and 1 == 1` | O(1) |
| 選んだ個数 | `Integer.bitCount(mask)` | O(1) |
| 1 つの選び方を調べる | `for (i in 0 until n)` で各 bit を見る | O(N) |
| 全体 | | O(2^N × N) |

目安: **N ≦ 20 くらいまで** (2^20 × 20 ≒ 2×10^7)。

## いつ使う？

- **N ≦ 15〜20 くらいと小さい** のに、N 個それぞれについて 2 択がある
- 「いくつか選んで合計を〜にできるか」「どの組み合わせが最適か」(部分集合の全列挙)
- 「スイッチの ON/OFF」「正直者か嘘つきか」「+ か - か」のように、各要素の状態が 2 通り
- N が 40 くらいなら → [半分全列挙](#textbook/meet-in-the-middle) を検討

## しくみ

整数 `mask` を 2 進数で書いたとき、**下から i 桁目 (i ビット目) が 1 なら i 番目を選ぶ** と約束します。
N = 3 のとき、0 〜 7 がちょうど 8 通りの選び方に対応します。

```
mask  2進数   選ぶもの (i = 0, 1, 2)
 0    000     なし
 1    001     0
 2    010     1
 3    011     0, 1
 4    100     2
 5    101     0, 2
 6    110     1, 2
 7    111     0, 1, 2
```

- `1 shl n` は 2^N (1 を n 桁左にずらす)。
- `(mask shr i) and 1` は「mask を i 桁右にずらして、一番下の桁だけ取り出す」= i ビット目の値。
- `mask and (1 shl i) != 0` と書いても同じ意味です。

## 実装 (Kotlin)

部分集合の和を調べる形のテンプレートです。`sum` の計算と最後の判定を問題に合わせて書き換えます。

```kotlin
// a の部分集合 (2^n 通り) のうち、和がちょうど target になるものの個数
fun countSubsetsWithSum(a: LongArray, target: Long): Int {
    val n = a.size
    var cnt = 0
    for (mask in 0 until (1 shl n)) {          // mask が 1 つの「選び方」
        var sum = 0L
        for (i in 0 until n) {
            if ((mask shr i) and 1 == 1) {     // i 番目を選んでいる？
                sum += a[i]
            }
        }
        if (sum == target) cnt++
    }
    return cnt
}
```

## 使い方

[ABC 079 C - Train Ticket](https://atcoder.jp/contests/abc079/tasks/abc079_c) を解くコードです。
4 桁の数字 ABCD の間の 3 か所に `+` か `-` を入れて、答えが 7 になる式を 1 つ出力します。
3 か所 × 2 択 = 2^3 = 8 通りを bit全探索します (bit が 1 なら `+`、0 なら `-`)。

```kotlin
fun main() {
    val d = readln().map { it - '0' }         // "1222" → [1, 2, 2, 2]
    for (mask in 0 until (1 shl 3)) {
        var sum = d[0]
        val sb = StringBuilder().append(d[0])
        for (i in 0 until 3) {
            if ((mask shr i) and 1 == 1) {
                sum += d[i + 1]
                sb.append('+')
            } else {
                sum -= d[i + 1]
                sb.append('-')
            }
            sb.append(d[i + 1])
        }
        if (sum == 7) {
            println("$sb=7")
            return
        }
    }
}
```

入力例

```
1222
```

出力例

```
1+2+2+2=7
```

`countSubsetsWithSum` の使用例: `countSubsetsWithSum(longArrayOf(1, 2, 3, 4), 5)` は
{1, 4} と {2, 3} の 2 通りなので `2` を返します。

## 例題

- [ABC 079 C - Train Ticket](https://atcoder.jp/contests/abc079/tasks/abc079_c) — 上の使い方の問題。3 か所の `+` / `-` を試す
- [ABC 128 C - Switches](https://atcoder.jp/contests/abc128/tasks/abc128_c) — N 個のスイッチの ON/OFF を全部試し、各電球の点灯条件を調べる
- [ABC 147 C - HonestOrUnkind2](https://atcoder.jp/contests/abc147/tasks/abc147_c) — 各人が正直者か不親切か 2^N 通りを仮定し、証言と矛盾しないか確かめる

## 注意点・よくあるミス

- **N が大きすぎないか確認**。N = 30 だと 2^30 ≒ 10^9 で TLE。N ≦ 20 が目安。
- `1 shl n` を `1 shl 31` 以上にすると Int をはみ出す。N ≧ 31 なら `1L shl n` (ただしその規模は全探索では無理)。
- `mask shr i and 1 == 1` は Kotlin では `(mask shr i) and 1 == 1` と同じ意味になりますが、
  読みやすさのため **括弧を付ける** のがおすすめ。
- 「何も選ばない」(mask = 0) も含まれます。問題が「1 つ以上選ぶ」なら `mask in 1 until (1 shl n)`。
- 選んだ個数がちょうど K 個のものだけ見たいときは `if (Integer.bitCount(mask) != k) continue`。
- 答えの組み合わせを出力するときは、何番目のビットがどの要素か (0-indexed) を取り違えないように。
