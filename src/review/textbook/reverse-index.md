---
title: 逆引き — こんなときは？
category: はじめに
acl: -
genres:
order: 5
summary: 問題文の特徴や「やりたいこと」から、使うアルゴリズムを探す
keywords: 逆引き, 索引, 目次, どれを使う, 判断, 見分け方, 典型
---

問題文を読んで「これ何を使うんだろう？」と思ったら、ここから探してください。
左上の検索欄に言葉を入れて探すこともできます (例: `最短`, `区間`, `mod`)。

## 制約から当たりをつける

| 制約 | 使えそうな計算量 | 候補 |
|---|---|---|
| N ≤ 8〜10 | O(N!) | [順列全探索](#textbook/permutation) |
| N ≤ 16〜20 | O(2^N) / O(2^N · N) | [bit全探索](#textbook/bit-brute-force), [bit DP](#textbook/bit-dp) |
| N ≤ 40 | O(2^(N/2)) | [半分全列挙](#textbook/meet-in-the-middle) |
| N ≤ 400〜500 | O(N^3) | [ワーシャルフロイド](#textbook/warshall-floyd), [区間 DP](#textbook/interval-dp) |
| N ≤ 3000〜5000 | O(N^2) | [DP の基本](#textbook/dp-basics), 二重ループの[全探索](#textbook/brute-force) |
| N ≤ 2×10^5 | O(N log N) | [ソート](#textbook/greedy), [二分探索](#textbook/binary-search), [セグメント木](#textbook/segtree), [優先度付きキュー](#textbook/priority-queue) |
| N ≤ 10^6〜10^7 | O(N) | [累積和](#textbook/prefix-sum), [尺取り法](#textbook/two-pointers), [エラトステネスの篩](#textbook/sieve) |
| N ≤ 10^12 | O(√N) | [素因数分解・約数列挙](#textbook/prime-factorization) |
| N ≤ 10^18 | O(log N) | [繰り返し二乗法](#textbook/pow-inv-mod), [行列累乗](#textbook/matrix-power), [ダブリング](#textbook/doubling), [答えで二分探索](#textbook/binary-search-answer) |

1 秒で回せるのはだいたい **10^8 回** の計算です。詳しくは [全探索](#textbook/brute-force) と [Kotlin 競プロの基本](#textbook/kotlin-basics)。

## 全部試したい

- 全パターンを試す → [全探索](#textbook/brute-force)
- 「選ぶ / 選ばない」を全部試す → [bit全探索](#textbook/bit-brute-force)
- 並べ方を全部試す → [順列全探索](#textbook/permutation)
- 条件付きで組み合わせを列挙、途中で打ち切りたい → [再帰による全列挙](#textbook/backtracking)
- N ≤ 40 で全探索したい → [半分全列挙](#textbook/meet-in-the-middle)

## 探したい・数えたい

- ソート済み配列で「x 以上の最初の位置」「x 未満の個数」 → [二分探索](#textbook/binary-search)
- 「最小の〇〇を求めよ」で、答えを決めれば判定が簡単 → [答えで二分探索](#textbook/binary-search-answer)
- 「x 以下で最大の値」を追加・削除しながら何度も聞かれる → [TreeSet / TreeMap](#textbook/treeset-treemap)
- 条件を満たす区間の個数・最長の区間 → [尺取り法](#textbook/two-pointers)
- 値が大きい (10^9) けど種類は少ない → [座標圧縮](#textbook/coordinate-compression)

## 区間の和・最小値

- 区間の和を何度も聞かれる (更新なし) → [累積和](#textbook/prefix-sum)
- 区間にまとめて足す操作をたくさん → [いもす法](#textbook/imos)
- 1 点を更新しながら区間の和 → [Fenwick Tree](#textbook/fenwicktree)
- 1 点を更新しながら区間の最小 / 最大 → [セグメント木](#textbook/segtree)
- 区間をまとめて更新しながら区間の和・最小 → [遅延セグメント木](#textbook/lazysegtree)
- 転倒数 (バブルソートの交換回数) → [Fenwick Tree](#textbook/fenwicktree)

## 最適化 (最大・最小)

- 「〇〇から選んで価値の最大」「〜通りの数」 → [DP の基本](#textbook/dp-basics)
- 一番長い増加する部分列 → [LIS](#textbook/lis)
- 2 つの文字列の共通部分・何回の操作で一致するか → [LCS・編集距離](#textbook/lcs-edit-distance)
- 「隣り合うものを合体」「区間を消す」 → [区間 DP](#textbook/interval-dp)
- 全部の点を 1 回ずつ回る (N ≤ 16) → [bit DP](#textbook/bit-dp)
- 「N 以下の整数で条件を満たすものの個数」(N が巨大) → [桁 DP](#textbook/digit-dp)
- 期待値・確率 → [確率・期待値 DP](#textbook/probability-dp)
- 締め切り・区間の選択・「なるべく早く終わるものから」 → [貪欲法](#textbook/greedy)
- 大きい方から K 個を取り出し続ける → [優先度付きキュー](#textbook/priority-queue)

## グラフ・迷路

- グラフの入力の受け取り方 → [グラフの表し方](#textbook/graph-basics)
- 迷路の最短手数 / 辺の重みがすべて同じ → [BFS](#textbook/bfs)
- 辺の重みが 0 か 1 だけ → [01-BFS](#textbook/zero-one-bfs)
- 辺に重み (正) がある最短経路 → [ダイクストラ](#textbook/dijkstra)
- 負の重みがある / 負閉路の検出 → [ベルマンフォード](#textbook/bellman-ford)
- 全ての 2 点間の最短距離 (N ≤ 400) → [ワーシャルフロイド](#textbook/warshall-floyd)
- 連結かどうか・連結成分 → [DFS](#textbook/dfs), [Union-Find](#textbook/dsu)
- 辺を追加しながら「同じグループか」 → [Union-Find](#textbook/dsu)
- 「A は B より先に」という順番を決める → [トポロジカルソート](#textbook/topological-sort)
- 全部をつなぐ最小コスト → [最小全域木](#textbook/mst)
- 2 色に塗り分けられるか・2 チームに分ける → [二部グラフ判定](#textbook/bipartite)
- 有向グラフのループ (サイクル) をまとめる → [強連結成分分解](#textbook/scc)
- マッチング・最大で何本流せるか → [最大流](#textbook/maxflow), [最小費用流](#textbook/mincostflow)
- 「A なら B」という条件がたくさん (2 択) → [2-SAT](#textbook/two-sat)
- グリッドの入力・上下左右への移動 → [グリッドの扱い方](#textbook/grid)

## 木

- 部分木の大きさ・木の上での DP → [木 DP](#textbook/tree-dp)
- 2 頂点の共通の祖先・木の上の距離 → [LCA](#textbook/lca)
- 木で一番遠い 2 点 → [木の直径](#textbook/tree-diameter)

## 数学

- 「998244353 で割った余り」 → [modint](#textbook/modint)
- nCr (組み合わせの数) → [modint](#textbook/modint)
- 巨大な累乗 a^b mod m・割り算の mod → [pow_mod / inv_mod](#textbook/pow-inv-mod)
- 素数判定・素因数分解・約数 → [素因数分解・約数列挙](#textbook/prime-factorization)
- たくさんの数の素数判定・素因数分解 → [エラトステネスの篩](#textbook/sieve)
- 最大公約数・最小公倍数 → [GCD / LCM](#textbook/gcd-lcm)
- 漸化式の 10^18 項目 → [行列累乗](#textbook/matrix-power)
- K 回 (K は巨大) 操作した後の状態 → [ダブリング](#textbook/doubling), [周期検出](#textbook/cycle-detection)
- 「〜で割ると〜余る」が複数 → [中国剰余定理](#textbook/crt)
- 点・線分・三角形 → [幾何の基本](#textbook/geometry)
- 切り上げ・負の数の割り算・sqrt の誤差 → [整数計算の小技](#textbook/integer-tricks)
- 2 つの数列の「和が k になる組」の個数を全部の k で → [畳み込み](#textbook/convolution)

## 文字列

- 文字列の基本操作 (反転・数え上げ・回文) → [文字列操作の基本](#textbook/string-basics)
- 同じ文字の連続をまとめる → [ランレングス圧縮](#textbook/run-length)
- 部分文字列が一致するか高速に判定 → [ローリングハッシュ](#textbook/rolling-hash)
- パターンが出てくる位置を全部 → [Z アルゴリズム](#textbook/z-algorithm)
- 異なる部分文字列の数・辞書順 → [接尾辞配列](#textbook/suffix-array)
- 括弧列が正しいか → [スタック・キュー](#textbook/stack-queue)
