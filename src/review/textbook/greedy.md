---
title: 貪欲法の典型
category: 基本テクニック
acl: -
genres: 貪欲, ソート
order: 2040
summary: 「その場で一番よいもの」を選び続ける。区間スケジューリングなど
level: 茶
keywords: greedy, 区間スケジューリング, interval scheduling, 締め切り, 交換議論, 終わる時間でソート, 重ならないように, できるだけ多く
---

## 何ができる？

「ある順番に並べて、前から **その場で一番よい選択** をしていく」だけで最適解が得られる問題を解きます。
ほとんどは **ソート + 1 回のループ** で O(N log N) です。

| 典型パターン | 並べ方 | 計算量 |
| --- | --- | --- |
| 区間スケジューリング (重ならない区間を最大何個選べるか) | **終わる時刻 (右端)** の早い順 | O(N log N) |
| 区間を全部突き刺す点の最小個数 | 右端の早い順 (区間スケジューリングと同じ答え) | O(N log N) |
| 締め切りつきの仕事を全部こなせるか | **締め切り** の早い順 | O(N log N) |
| 大きい方 / 小さい方から使う (お釣り、K 個選んで和を最大) | 値の大きい順 | O(N log N) |

## いつ使う？

- 「できるだけ多く選ぶ」「最小の回数で」「全部できるか判定」などで、DP にするには N や値が大きすぎる
- 「区間が重ならないように」「時刻 s から t まで」→ 区間スケジューリングを疑う
- 「締め切り」「〜までに終わらせる」→ 締め切り順を疑う
- 順番を入れ替えても損しない (下の **交換議論** が言える) 並べ方が見つかる

貪欲法は **うまくいかない問題も多い** ので、小さいケースで全探索と比べて確かめる癖をつけると安全です。

## しくみ

### 区間スケジューリング

「**終わるのが一番早い区間** を選び、それと重なるものを捨てる」を繰り返します。

```
時刻   0 1 2 3 4 5 6 7 8
A      [-----)                 [0,3)
B        [---------------)     [1,8)
C          [---)               [2,4) ← ✕ (A と重なる)
D              [-----)         [4,7)
E                [-----)       [5,8) ← ✕ (D と重なる)

右端でソート: A(3) C(4) D(7) B(8) E(8)
A を選ぶ → C は A と重なる → D を選ぶ → B, E は D と重なる → 答え 2 個
```

なぜ「終わるのが早い順」でよいのか: 最初に選ぶ区間を、ある最適解の 1 個目から
「一番早く終わる区間」に **取り替えても**、残りの区間とは重ならない (むしろ空き時間が増える)。
だから取り替えても損しない、と言えるからです。

### 交換議論 (並べ方を見つける考え方)

「隣り合う 2 つ i, j を入れ替えたら、悪くならないのはどんなときか？」を考えます。

- 締め切りつき仕事 (時間 a_i、締め切り b_i): 締め切りが遅い仕事を先にやっている所があれば、
  入れ替えても締め切りに間に合わなくなる仕事は増えない。だから **締め切り順** が最善。
- この「入れ替えても損しない」が言えたら、その順にソートして前から貪欲が正解です。

迷ったら「開始時刻順」「長さ順」「終了時刻順」など候補を並べて、反例 (小さな例) を探してみましょう。
区間スケジューリングで **開始時刻順** や **短い順** は反例があります。

## 実装 (Kotlin)

```kotlin
// 区間スケジューリング: 半開区間 [ls[i], rs[i]) の中から、
// 互いに重ならないように最大何個選べるかを返す
fun maxDisjointIntervals(ls: LongArray, rs: LongArray): Int {
    val n = ls.size
    val idx = (0 until n).sortedBy { rs[it] } // 右端 (終わる時刻) の早い順
    var count = 0
    var lastEnd = Long.MIN_VALUE // 最後に選んだ区間の右端
    for (i in idx) {
        if (ls[i] >= lastEnd) { // 前に選んだ区間と重ならない
            count++
            lastEnd = rs[i]
        }
    }
    return count
}

// 締め切りつき仕事: 仕事 i は a[i] 時間かかり、時刻 b[i] までに終える必要がある。
// 時刻 0 から 1 つずつこなして、全部間に合うなら true
fun canFinishAll(a: LongArray, b: LongArray): Boolean {
    val n = a.size
    val idx = (0 until n).sortedBy { b[it] } // 締め切りの早い順
    var time = 0L
    for (i in idx) {
        time += a[i]
        if (time > b[i]) return false
    }
    return true
}
```

## 使い方

N 個の区間 `L R` (L 以上 R 未満) から、重ならないように最大何個選べるかを出力します。
続けて、M 個の仕事 `A B` (A 時間かかり、締め切り B) がすべて間に合うかを出力します。

```kotlin
fun main() {
    val br = System.`in`.bufferedReader()
    var st = java.util.StringTokenizer("")
    fun next(): String {
        while (!st.hasMoreTokens()) st = java.util.StringTokenizer(br.readLine())
        return st.nextToken()
    }

    val n = next().toInt()
    val ls = LongArray(n)
    val rs = LongArray(n)
    for (i in 0 until n) {
        ls[i] = next().toLong()
        rs[i] = next().toLong()
    }
    println(maxDisjointIntervals(ls, rs))

    val m = next().toInt()
    val a = LongArray(m)
    val b = LongArray(m)
    for (i in 0 until m) {
        a[i] = next().toLong()
        b[i] = next().toLong()
    }
    println(if (canFinishAll(a, b)) "Yes" else "No")
}
```

入力例

```
5
0 3
1 8
2 4
4 7
5 8
3
2 4
1 9
4 7
```

出力例

```
2
Yes
```

## 例題

- [キーエンス プログラミング コンテスト 2020 B - Robot Arms](https://atcoder.jp/contests/keyence2020/tasks/keyence2020_b) — 区間 [X-L, X+L) に直して区間スケジューリング
- [ABC 103 D - Islands War](https://atcoder.jp/contests/abc103/tasks/abc103_d) — 全部の区間を「突き刺す」最小本数。右端でソートして貪欲
- [ABC 131 D - Megalomania](https://atcoder.jp/contests/abc131/tasks/abc131_d) — 締め切りつき仕事。締め切り順にこなせるか判定 (`canFinishAll` そのもの)

## 注意点・よくあるミス

- **区間の端** が「含む」か「含まない」かを確認する。閉区間 [L, R] で端点の共有も重なりとみなすなら、
  条件を `ls[i] > lastEnd` に変えるか、R に 1 を足して半開区間にします。
- ソートのキーを間違えやすい。区間スケジューリングは **右端** で並べる (左端や長さではない)。
- 「なんとなく正しそう」な貪欲は外れることも多いです。小さい N で全探索 (bit 全探索など) と比べてみましょう。
- 貪欲で解けないと分かったら DP を考える (例: 重さつきナップサック、価値つきの区間選択)。
- Kotlin の `sortedBy` は安定ソート。同じキーの並びは元の順番のままです (タイブレークが必要なら `sortedWith(compareBy(...).thenBy(...))`)。
