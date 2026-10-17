---
title: 順序付き集合・連想配列 (TreeSet / TreeMap)
category: データ構造
acl: -
genres: 二分探索, 実装
order: 103
summary: 値を小さい順に保ち、x 以上で最小などを O(log N) で探す
level: 緑
keywords: TreeSet, TreeMap, 順序付き集合, multiset, 多重集合, ordered set, lower_bound, floor, ceiling, 平衡二分探索木
---

## 何ができる？

`java.util.TreeSet` (集合) と `java.util.TreeMap` (キー → 値) は、中身を **常に小さい順** に保ったまま、
追加・削除・「x 以上で最小の値」などの検索を O(log N) で行います。

| 操作 (TreeSet) | 内容 | 計算量 |
| --- | --- | --- |
| `add(x)` / `remove(x)` / `contains(x)` | 追加 / 削除 / 含むか | O(log N) |
| `first()` / `last()` | 最小 / 最大 (空だと例外) | O(log N) |
| `pollFirst()` / `pollLast()` | 最小 / 最大を取り出す (空なら null) | O(log N) |
| `floor(x)` | **x 以下** で最大 (無ければ null) | O(log N) |
| `ceiling(x)` | **x 以上** で最小 (無ければ null) | O(log N) |
| `lower(x)` | **x 未満** で最大 (無ければ null) | O(log N) |
| `higher(x)` | **x より大きい** 中で最小 (無ければ null) | O(log N) |

TreeMap では同じ検索が `floorKey(x)` / `ceilingKey(x)` / `lowerKey(x)` / `higherKey(x)` /
`firstKey()` / `lastKey()` (エントリごと欲しければ `floorEntry(x)` など) という名前になります。

TreeSet は **同じ値を 1 個しか持てない** ので、重複ありの集合 (多重集合) は
`TreeMap<値, 個数>` で作ります。それをまとめたのが下の `MultiSet` クラスです。

| 操作 (MultiSet) | 内容 | 計算量 |
| --- | --- | --- |
| `add(x, cnt = 1)` | x を cnt 個追加 | O(log N) |
| `remove(x, cnt = 1)` | x を最大 cnt 個削除し、消した個数を返す | O(log N) |
| `count(x)` / `x in ms` | x の個数 / 含むか | O(log N) |
| `min()` / `max()` | 最小 / 最大 (空なら null) | O(log N) |
| `floor(x)` / `ceiling(x)` / `lower(x)` / `higher(x)` | TreeSet と同じ意味 | O(log N) |
| `size` / `isEmpty()` | 全要素数 (重複も数える) / 空か | O(1) |

**HashSet / HashMap との違い**

| | HashSet / HashMap | TreeSet / TreeMap |
| --- | --- | --- |
| 追加・削除・検索 | 平均 O(1) (速い) | O(log N) |
| 順番 | バラバラ | 小さい順 |
| 最小・最大・x 以上で最小 | できない (全部見る O(N)) | O(log N) でできる |

「あるか・ないか」「個数」だけなら Hash の方が速いです。**大小関係を使う検索が必要なら Tree** を使います。

## いつ使う？

- 値の追加・削除がくり返されて、そのたびに **最小値・最大値** や「最大 - 最小」を答える
- 「x 以上で一番小さいもの」「x の一つ手前」を聞かれる (**動的な二分探索**)
  - ソート済み配列 + 二分探索は、途中で値が増減すると使えない → TreeSet
- 数直線や棒を「切る」問題で、x の左右の切れ目を探す
- 「空いている席 (番号) のうち x 以上で最小」を何度も探す
- 優先度付きキューで「任意の値を削除」したくなった

## しくみ

中身は **赤黒木** という平衡二分探索木です。「左の子孫 < 自分 < 右の子孫」というルールを守り、
木の高さが常に O(log N) になるように自動で形を整えます。

```
        5
      /   \
     2     8         ceiling(6) を探す: 5 より大きいので右へ → 8 は 6 以上なので候補 8、左へ
    / \   / \                          → 7 は 6 以上なので候補 7、左へ → 何もない → 答え 7
   1   3 7   9
```

根から「x と比べて左右どちらに進むか」を決めながら下りるだけなので、O(高さ) = O(log N) です。

**多重集合**: TreeMap に「値 → 個数」を持たせます。追加は個数 +1、削除は個数 -1 で、
**0 個になったらキーごと消す** のがポイントです (消さないと `min()` などに 0 個の値が出てきてしまう)。

## 実装 (Kotlin)

```kotlin
import java.util.TreeMap
import java.util.TreeSet

// 多重集合 (同じ値を何個でも入れられる、小さい順に並んだ集合)。中身は TreeMap<値, 個数>
class MultiSet {
    private val map = TreeMap<Long, Long>()

    // 全体の要素数 (重複も数える)
    var size = 0L
        private set

    fun isEmpty(): Boolean = size == 0L

    // x を cnt 個追加する
    fun add(x: Long, cnt: Long = 1L) {
        map[x] = (map[x] ?: 0L) + cnt
        size += cnt
    }

    // x を最大 cnt 個削除し、実際に削除した個数を返す
    fun remove(x: Long, cnt: Long = 1L): Long {
        val c = map[x] ?: return 0L
        val d = minOf(c, cnt)
        if (c == d) map.remove(x) else map[x] = c - d // 0 個になったらキーごと消す
        size -= d
        return d
    }

    fun count(x: Long): Long = map[x] ?: 0L
    operator fun contains(x: Long): Boolean = map.containsKey(x)

    // 見つからなければ null
    fun min(): Long? = map.firstEntry()?.key
    fun max(): Long? = map.lastEntry()?.key
    fun floor(x: Long): Long? = map.floorKey(x)     // x 以下で最大
    fun ceiling(x: Long): Long? = map.ceilingKey(x) // x 以上で最小
    fun lower(x: Long): Long? = map.lowerKey(x)     // x 未満で最大
    fun higher(x: Long): Long? = map.higherKey(x)   // x より大きい中で最小
}
```

## 使い方

**例 1: TreeSet** — [ABC 217 D - Cutting Woods](https://atcoder.jp/contests/abc217/tasks/abc217_d) と同じ形の問題です。
長さ L の木材に切れ目を入れていき、位置 x を含む木片の長さを答えます。

```kotlin
// 長さ L の木材。"1 x" で位置 x に切れ目を入れ、"2 x" で位置 x を含む木片の長さを答える
fun main() {
    val (l, q) = readln().trim().split(" ").map { it.toLong() }
    val cuts = TreeSet<Long>()
    cuts.add(0L) // 両端も「切れ目」として入れておくと null の場合分けが要らない
    cuts.add(l)
    val sb = StringBuilder()
    repeat(q.toInt()) {
        val (c, x) = readln().trim().split(" ").map { it.toLong() }
        if (c == 1L) {
            cuts.add(x)
        } else {
            val right = cuts.higher(x)!! // x より右で一番近い切れ目
            val left = cuts.lower(x)!!   // x より左で一番近い切れ目
            sb.appendLine(right - left)
        }
    }
    print(sb)
}
```

入力例

```
5 3
1 2
2 1
2 3
```

出力例

```
2
3
```

**例 2: MultiSet** — [ABC 253 C - Max - Min Query](https://atcoder.jp/contests/abc253/tasks/abc253_c) と同じ形の問題です。

```kotlin
// "1 x": x を 1 個追加 / "2 x c": x を min(c, 個数) 個削除 / "3": (最大値) - (最小値) を出力
fun main() {
    val q = readln().trim().toInt()
    val ms = MultiSet()
    val sb = StringBuilder()
    repeat(q) {
        val t = readln().trim().split(" ").map { it.toLong() }
        when (t[0]) {
            1L -> ms.add(t[1])
            2L -> ms.remove(t[1], t[2])
            else -> sb.appendLine(ms.max()!! - ms.min()!!)
        }
    }
    print(sb)
}
```

入力例

```
8
1 3
1 2
3
1 2
1 7
3
2 2 3
3
```

出力例

```
1
5
4
```

(どちらの例も、`import java.util.TreeMap` / `import java.util.TreeSet` は実装側の先頭にあります。)

## 例題

- [ABC 217 D - Cutting Woods](https://atcoder.jp/contests/abc217/tasks/abc217_d) — 上の例 1。切れ目の位置を TreeSet で管理
- [ABC 253 C - Max - Min Query](https://atcoder.jp/contests/abc253/tasks/abc253_c) — 上の例 2。多重集合の最大と最小
- [ABC 241 D - Sequence Query](https://atcoder.jp/contests/abc241/tasks/abc241_d) — 多重集合で「x 以下で大きい方から k 番目」。k ≤ 5 なので `floor` / `lower` を数回たどればよい

## 注意点・よくあるミス

- `floor` / `ceiling` / `lower` / `higher` は見つからないと **null** を返します。
  番兵 (例: 両端の 0 と L、`Long.MIN_VALUE` や `Long.MAX_VALUE`) を最初に入れておくと場合分けが減ります。
- TreeSet の `first()` / `last()` は空だと例外です。空かもしれないなら先に `isEmpty()` を確認するか、
  `pollFirst()` (取り出す。空なら null) や TreeMap の `firstEntry()` (空なら null) を使う。
- TreeSet は重複を持てません。同じ値が何個も入る問題で TreeSet を使うと、`add` が無視されて数が合わなくなります。
- 「小さい方から K 番目」は TreeSet では O(log N) で求められません (先頭から K 個たどる O(K) になる)。
  K が大きいなら座標圧縮 + [Fenwick Tree](#textbook/fenwicktree) や [セグメント木](#textbook/segtree) を使う。
- `TreeSet<Long>` に `Int` の変数を `add` するとコンパイルエラーです。`.toLong()` で型を合わせる。
- `for (x in set)` で回している途中に `set.remove(x)` すると ConcurrentModificationException。
  消したいものをリストに集めてから消す。
- O(log N) ですが定数が重めです。N = 2×10^5 で数回の操作なら問題ありませんが、10^6 回を超えるなら注意。
