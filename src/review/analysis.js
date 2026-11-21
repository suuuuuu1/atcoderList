// 📊 分析タブ: 解いたコード (items)、genres.tsv のジャンル・要復習・メモ、git のコミット日 (bundleDates) を
// タブを開くたびにその場で集計する。解答やジャンル、コミットが増えれば結果も変わる。
// 「解いた日」は、そのファイルが初めて git にコミットされた日として扱う。
// index.html の $, esc, store, items, bundleDates, allGenres, vocab, pages, pagesForGenre, genre, open を使う。

const DAY_MS = 86400000;
const ymd = d => `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`;
const parseYmd = s => new Date(s + 'T00:00:00');
const daysBetween = (a, b) => Math.round((parseYmd(b) - parseYmd(a)) / DAY_MS);
const pct = (a, b) => b ? Math.round(a / b * 100) : 0;
const WEEKDAYS = ['日', '月', '火', '水', '木', '金', '土'];

// f(x) が配列を返すときは、その要素ごとに数える
function countBy(arr, f) {
  const m = new Map();
  for (const x of arr) for (const k of [].concat(f(x))) if (k != null) m.set(k, (m.get(k) || 0) + 1);
  return m;
}
const sortedEntries = m => [...m].sort((a, b) => b[1] - a[1] || String(a[0]).localeCompare(String(b[0])));

const solvedOn = it => (bundleDates[it.path] || [])[0] || null; // 初コミット日 = 解いた日
const editDays = it => bundleDates[it.path] || [];
const problemIndex = it => it.problem.length === 1 ? it.problem.charCodeAt(0) - 65 : -1; // A=0, B=1, ...
const itemLink = it => `<a data-open="${esc(it.path)}">${esc(it.contest)} ${esc(it.problem)}</a>`;
const genreLink = g => `<a data-genre="${esc(g)}">${esc(g)}</a>`;

// コードの書き方から推測できるテクニック。付けたジャンルと比べて「付け忘れ候補」を出すのに使う
const CODE_HINTS = [
  ['優先度付きキュー', /PriorityQueue/],
  ['スタック・キュー', /ArrayDeque|LinkedList|Stack<|Queue</, ['BFS', 'DFS', 'グラフ', '優先度付きキュー']], // BFS の中で使うことが多い
  ['bit全探索', /1\s+shl\s+\w+/],
  ['DP', /\bdp\s*(\[|=)/i],
  ['二分探索', /lowerBound|upperBound|binarySearch|while\s*\(\s*\w+\s*-\s*\w+\s*>\s*1\s*\)/],
  ['BFS', /fun\s+\w*bfs\w*|\bbfs\s*\(/i],
  ['DFS', /fun\s+dfs\b/i],
  ['Union-Find', /UnionFind|DSU|fun\s+find\s*\(/],
  ['ダイクストラ', /dijkstra/i],
  ['整数論', /\bgcd\b|\blcm\b/],
  ['累積和', /prefix|ruiseki|cum\[|sum\[\s*i\s*\+\s*1\s*\]/i],
  ['二次元グリッド', /Array\(\s*h\s*\)|\bgrid\[/, ['DP']],
];

// コードの書き方のクセ
const HABITS = [
  ['readln で入力', /readln\(/],
  ['Scanner / BufferedReader', /Scanner|BufferedReader/],
  ['StringBuilder でまとめて出力', /StringBuilder/],
  ['Long を使う', /\bLong\b|toLong\(\)|LongArray/],
  ['when 式', /\bwhen\s*[({]/],
  ['repeat', /\brepeat\s*\(/],
  ['while ループ', /\bwhile\s*\(/],
  ['data class', /data\s+class/],
  ['拡張関数', /fun\s+[A-Z]\w*(<[^>]*>)?\.\w+\s*\(/],
  ['ローカル関数', /\n[ \t]+fun\s+\w+\s*\(/],
  ['map / filter などのコレクション操作', /\.(map|filter|count|sumOf|any|all|groupBy)\s*\{/],
  ['再帰', null], // 下の isRecursive で判定
];

// 関数の { } の中で、その関数自身を呼んでいるか
function isRecursive(code) {
  for (const m of code.matchAll(/fun\s+(?:[\w<>?, ]+\.)?(\w+)\s*\(/g)) {
    if (m[1] === 'main') continue;
    let i = code.indexOf('{', m.index + m[0].length);
    if (i < 0 || /\n\s*(private\s+)?fun\s/.test(code.slice(m.index + m[0].length, i))) continue; // 式本体の関数は見ない
    const start = ++i;
    for (let depth = 1; i < code.length && depth > 0; i++) depth += code[i] === '{' ? 1 : code[i] === '}' ? -1 : 0;
    if (new RegExp(`\\b${m[1]}\\s*\\(`).test(code.slice(start, i - 1))) return true;
  }
  return false;
}

// ---------- 部品 ----------

function hbars(rows, { max, fmt = r => r.value } = {}) {
  if (!rows.length) return '<div class="hint">まだデータがありません</div>';
  const m = max ?? Math.max(1, ...rows.map(r => r.value));
  return rows.map(r => `<div class="hb"><span class="lab" title="${esc(String(r.title ?? r.label))}">${r.html ?? esc(String(r.label))}</span>
    <span class="track"><i style="width:${(r.value - (r.warn || 0)) / m * 100}%"></i>${r.warn ? `<i class="w" style="width:${r.warn / m * 100}%"></i>` : ''}</span>
    <span class="v">${fmt(r)}</span></div>`).join('');
}

function vbars(rows) {
  if (!rows.length) return '<div class="hint">まだデータがありません</div>';
  const m = Math.max(1, ...rows.map(r => r.value));
  return `<div class="vb">${rows.map(r => `<div title="${esc(r.title ?? `${r.label}: ${r.value}`)}">
    <span>${r.value || ''}</span><i style="height:${r.value / m * 85}%"></i><span class="x">${esc(r.label)}</span></div>`).join('')}</div>`;
}

const card = (title, body, wide) => body ? `<section class="card ${wide ? 'wide' : ''}"><h3>${title}</h3>${body}</section>` : '';
const kpi = (value, label) => `<div class="kpi"><b>${value}</b><span>${label}</span></div>`;

// ---------- 集計 ----------

// genres.tsv に「未実装」として載っているだけの問題 (main が空) は数えない。fun main() = ... の 1 行解答は数える
const isSolved = it => hasCode(it.code) || /fun\s+main\s*\([^)]*\)\s*=/.test(it.code);

function analysisTarget() {
  const kind = $('akind').value, period = Number($('aperiod').value);
  const since = period ? ymd(new Date(Date.now() - (period - 1) * DAY_MS)) : null;
  return items.filter(it => isSolved(it) && (!kind || it.type === kind) && (!since || (solvedOn(it) && solvedOn(it) >= since)));
}

function secSummary(list) {
  const dated = list.map(solvedOn).filter(Boolean).sort();
  const contests = new Set(list.map(it => `${it.type}/${it.contest}`));
  const classified = list.filter(it => allGenres(it).length).length;
  const lines = list.reduce((s, it) => s + it.lines, 0);
  const genres = new Set(list.flatMap(allGenres));
  const memo = list.filter(it => it.memo.trim()).length;
  return card('サマリー', `<div class="kpis">
    ${kpi(list.length, '解いた問題')}
    ${kpi(contests.size, 'コンテスト')}
    ${kpi(`${pct(classified, list.length)}%`, `ジャンル分類済み (${classified}件)`)}
    ${kpi(list.filter(it => it.review).length, '要復習')}
    ${kpi(genres.size, '使ったジャンル')}
    ${kpi(lines.toLocaleString(), `総行数 (平均 ${list.length ? Math.round(lines / list.length) : 0}行)`)}
    ${kpi(`${pct(memo, list.length)}%`, 'メモを書いた割合')}
    ${dated.length ? kpi(new Set(dated).size, `解いた日数 (${dated[0]} 〜 ${dated[dated.length - 1]})`) : ''}
  </div>`, true);
}

// 今日からさかのぼって、毎日つながっている日数 (今日まだでも昨日まで続いていれば数える)
function streaks(days) {
  const set = new Set(days);
  const sorted = [...set].sort();
  let longest = 0, run = 0, prev = null;
  for (const d of sorted) {
    run = prev && daysBetween(prev, d) === 1 ? run + 1 : 1;
    longest = Math.max(longest, run);
    prev = d;
  }
  let cur = 0, d = new Date();
  if (!set.has(ymd(d))) d = new Date(d - DAY_MS);
  while (set.has(ymd(d))) { cur++; d = new Date(d - DAY_MS); }
  return { cur, longest };
}

function secActivity(list) {
  const solved = list.map(solvedOn).filter(Boolean);
  if (!solved.length) return card('活動', '<div class="hint">git の日付データがありません。bundle を作り直すと表示されます（GitHub Pages は push すると更新）。</div>', true);
  const perDay = countBy(solved, d => d);
  const activeDays = list.flatMap(editDays);
  const { cur, longest } = streaks(activeDays);
  const today = new Date();
  const last = solved.slice().sort().pop();

  // ヒートマップ: 53週前の日曜日から今日まで
  const start = new Date(today - 52 * 7 * DAY_MS);
  start.setDate(start.getDate() - start.getDay());
  const cells = [];
  for (let d = new Date(start); d <= today || d.getDay() !== 0; d = new Date(d.getTime() + DAY_MS)) {
    const key = ymd(d), n = perDay.get(key) || 0;
    const lv = n === 0 ? 0 : n === 1 ? 1 : n <= 3 ? 2 : n <= 6 ? 3 : 4;
    cells.push(d > today ? '<i class="out"></i>' : `<i class="${lv ? 'l' + lv : ''}" title="${key} (${WEEKDAYS[d.getDay()]}) ${n}問"></i>`);
  }

  // 月ごと (直近12か月)
  const months = [];
  for (let i = 11; i >= 0; i--) {
    const d = new Date(today.getFullYear(), today.getMonth() - i, 1);
    const key = ymd(d).slice(0, 7);
    months.push({ label: `${d.getMonth() + 1}月`, title: `${key}: ${solved.filter(s => s.startsWith(key)).length}問`, value: solved.filter(s => s.startsWith(key)).length });
  }
  const byWeekday = countBy(solved, s => parseYmd(s).getDay());

  // 直近30日とその前の30日
  const ago = n => ymd(new Date(today - n * DAY_MS));
  const recent = solved.filter(s => s > ago(30)).length, before = solved.filter(s => s > ago(60) && s <= ago(30)).length;
  const diff = recent - before;

  return card('活動', `
    <div class="kpis">
      ${kpi(`${cur}日`, '連続で取り組んだ日数 (いま)')}
      ${kpi(`${longest}日`, '最長の連続日数')}
      ${kpi(recent, `直近30日に解いた問題 <span class="${diff >= 0 ? 'up' : 'down'}">(${diff >= 0 ? '+' : ''}${diff} / その前の30日比)</span>`)}
      ${kpi(`${daysBetween(last, ymd(today))}日前`, `最後に解いた日 (${last})`)}
      ${kpi((solved.length / Math.max(1, perDay.size)).toFixed(1), '解いた日の平均問題数')}
    </div>
    <h4>解いた日のカレンダー (直近1年)</h4>
    <div class="heat-wrap"><div class="heat">${cells.join('')}</div></div>
    <div class="an" style="margin-top:6px">
      <div><h4>月ごとの解いた数</h4>${vbars(months)}</div>
      <div><h4>曜日ごとの解いた数</h4>${vbars(WEEKDAYS.map((w, i) => ({ label: w, value: byWeekday.get(i) || 0 })))}</div>
    </div>`, true);
}

function genreStats(list) {
  const stats = new Map();
  for (const it of list) for (const g of allGenres(it)) {
    const s = stats.get(g) || { g, n: 0, review: 0, lines: 0, items: [] };
    s.n++; s.lines += it.lines; s.items.push(it);
    if (it.review) s.review++;
    stats.set(g, s);
  }
  return [...stats.values()].sort((a, b) => b.n - a.n || a.g.localeCompare(b.g));
}

function secGenres(list) {
  const stats = genreStats(list);
  const none = list.filter(it => !allGenres(it).length).length;
  return card('ジャンル別の解いた数', hbars(stats.map(s => ({ html: genreLink(s.g), label: s.g, value: s.n, warn: s.review })),
    { fmt: r => `${r.value}${r.warn ? ` <span class="down">(要復習 ${r.warn})</span>` : ''}` })
    + `<p class="hint">オレンジ = 要復習。${none ? `未分類が ${none} 件あります。` : ''}ジャンル名を押すと解答タブで絞り込みます</p>`);
}

function secStrength(list) {
  const stats = genreStats(list).filter(s => s.n >= 2);
  if (!stats.length) return '';
  // 苦手: 要復習の割合が高い順。得意: 数が多くて要復習が少ない順 (件数 × 復習できている割合)
  const weak = stats.filter(s => s.review).sort((a, b) => b.review / b.n - a.review / a.n || b.n - a.n).slice(0, 6);
  const strong = stats.slice().sort((a, b) => b.n * (1 - b.review / b.n) - a.n * (1 - a.review / a.n)).slice(0, 6);
  const row = s => `<li>${genreLink(s.g)} — ${s.n}問, 要復習 ${pct(s.review, s.n)}%, 平均 ${Math.round(s.lines / s.n)}行
    ${pagesForGenre(s.g).slice(0, 2).map(p => `<a class="tb-link" href="#textbook/${encodeURIComponent(p.slug)}">📘 ${esc(p.title)}</a>`).join('')}</li>`;
  return card('得意・苦手', `
    <h4>得意そうなジャンル (多く解けていて要復習が少ない)</h4><ol class="alist">${strong.map(row).join('')}</ol>
    <h4>苦手そうなジャンル (要復習の割合が高い)</h4>${weak.length ? `<ol class="alist">${weak.map(row).join('')}</ol>` : '<div class="hint">要復習の付いたジャンルはありません</div>'}
    <p class="hint">2問以上あるジャンルで計算しています</p>`);
}

function secTrend(list) {
  const today = ymd(new Date()), cut = ymd(new Date(Date.now() - 60 * DAY_MS));
  const dated = list.filter(solvedOn);
  const recent = dated.filter(it => solvedOn(it) > cut), older = dated.filter(it => solvedOn(it) <= cut);
  if (!recent.length || !older.length) return '';
  const share = arr => { const m = countBy(arr, allGenres); const total = arr.length; return g => (m.get(g) || 0) / total; };
  const r = share(recent), o = share(older);
  const all = [...new Set(dated.flatMap(allGenres))];
  const changes = all.map(g => ({ g, d: r(g) - o(g), now: Math.round(r(g) * 100), then: Math.round(o(g) * 100) }))
    .filter(c => c.d).sort((a, b) => b.d - a.d);
  const row = c => `<li>${genreLink(c.g)} ${c.then}% → <b class="${c.d > 0 ? 'up' : 'down'}">${c.now}%</b></li>`;
  // ジャンルを初めて解いた日
  const firstSeen = new Map();
  for (const it of dated.slice().sort((a, b) => solvedOn(a).localeCompare(solvedOn(b))))
    for (const g of allGenres(it)) if (!firstSeen.has(g)) firstSeen.set(g, { d: solvedOn(it), it });
  const newest = [...firstSeen].sort((a, b) => b[1].d.localeCompare(a[1].d)).slice(0, 6);
  return card('最近の傾向 (直近60日 vs それより前)', `
    <h4>増えたジャンル (全体に占める割合)</h4><ul class="alist">${changes.filter(c => c.d > 0).slice(0, 5).map(row).join('') || '<li class="hint">なし</li>'}</ul>
    <h4>減ったジャンル</h4><ul class="alist">${changes.filter(c => c.d < 0).slice(-5).reverse().map(row).join('') || '<li class="hint">なし</li>'}</ul>
    <h4>最近はじめて解いたジャンル</h4><ul class="alist">${newest.map(([g, v]) => `<li>${genreLink(g)} — ${v.d} (${itemLink(v.it)}, ${daysBetween(v.d, today)}日前)</li>`).join('')}</ul>`);
}

function secPairs(list) {
  const pairs = countBy(list, it => {
    const gs = allGenres(it).slice().sort(), out = [];
    for (let i = 0; i < gs.length; i++) for (let j = i + 1; j < gs.length; j++) out.push(`${gs[i]} × ${gs[j]}`);
    return out;
  });
  const top = sortedEntries(pairs).slice(0, 8);
  if (!top.length) return '';
  return card('よく一緒に使うジャンルの組み合わせ', hbars(top.map(([k, v]) => ({ label: k, value: v }))));
}

function secUntried(list) {
  const used = new Set(list.flatMap(allGenres));
  const candidates = [...new Set([...vocab, ...pages.flatMap(p => p.genres)])].filter(g => !used.has(g));
  if (!candidates.length) return card('まだ解いていないジャンル', '<div class="hint">すべてのジャンルを1問以上解いています 🎉</div>');
  return card(`まだ解いていないジャンル (${candidates.length})`, `<ul class="alist">${candidates.map(g => {
    const ps = pagesForGenre(g);
    return `<li>${esc(g)} ${ps.slice(0, 2).map(p => `<a class="tb-link" href="#textbook/${encodeURIComponent(p.slug)}">📘 ${esc(p.title)}</a>`).join('')}</li>`;
  }).join('')}</ul>`);
}

function secProblems(list) {
  const letters = [...new Set(list.map(it => it.problem))].filter(p => p.length === 1).sort();
  if (!letters.length) return '';
  const rows = letters.map(L => {
    const its = list.filter(it => it.problem === L);
    const top = sortedEntries(countBy(its, allGenres)).slice(0, 3).map(([g]) => genreLink(g)).join(', ');
    return `<tr><th>${esc(L)}</th><td class="num">${its.length}</td><td class="num">${Math.round(its.reduce((s, it) => s + it.lines, 0) / its.length)}</td>
      <td class="num">${pct(its.filter(it => it.review).length, its.length)}%</td><td>${top || '<span class="hint">未分類</span>'}</td></tr>`;
  });
  return card('問題 (A, B, C…) ごとの傾向', `<table class="atable"><thead><tr><th>問題</th><th>解いた数</th><th>平均行数</th><th>要復習</th><th>多いジャンル</th></tr></thead>
    <tbody>${rows.join('')}</tbody></table>`);
}

// ABC: 回ごとに何問目まで解けたか
function secAbc(list) {
  const abc = list.filter(it => it.type === 'abc' && problemIndex(it) >= 0);
  if (!abc.length) return '';
  const byContest = new Map();
  for (const it of abc) {
    const c = byContest.get(it.contest) || { contest: it.contest, num: contestNum(it.contest), solved: new Set() };
    c.solved.add(problemIndex(it));
    byContest.set(it.contest, c);
  }
  const contests = [...byContest.values()].sort((a, b) => a.num - b.num);
  const maxIdx = c => Math.max(...c.solved);
  const fromA = c => { let k = 0; while (c.solved.has(k)) k++; return k; }; // A から連続で解けた数
  const kan = countBy(contests, fromA);
  const recent = contests.slice(-20);
  const avgOf = arr => arr.length ? (arr.reduce((s, c) => s + c.solved.size, 0) / arr.length).toFixed(1) : '-';
  const half = Math.floor(contests.length / 2);
  return card('ABC の成績', `
    <div class="kpis">
      ${kpi(contests.length, '取り組んだ回')}
      ${kpi(avgOf(contests), '1回あたりの平均解答数')}
      ${kpi(`${avgOf(contests.slice(half))} <span class="hint">/ ${avgOf(contests.slice(0, half))}</span>`, '新しい半分 / 古い半分の平均')}
      ${kpi(String.fromCharCode(65 + Math.max(...contests.map(maxIdx))), '解いた最も後ろの問題')}
    </div>
    <h4>A から連続で何問解けたか (回数)</h4>
    ${hbars([...kan].sort((a, b) => b[0] - a[0]).map(([k, v]) => ({ label: k ? `${'ABCDEFG'.slice(0, k)} (${k}冠)` : 'A を解いていない回', value: v })))}
    <h4>直近 ${recent.length} 回の解いた数</h4>
    ${vbars(recent.map(c => ({ label: String(c.num), value: c.solved.size,
      title: `${c.contest}: ${[...c.solved].sort().map(i => String.fromCharCode(65 + i)).join(' ')}` })))}`, true);
}

function secCode(list) {
  if (!list.length) return '';
  const buckets = [['〜10行', 10], ['11〜20行', 20], ['21〜40行', 40], ['41〜80行', 80], ['81行〜', Infinity]];
  const hist = buckets.map(([label, hi], i) => ({ label, value: list.filter(it => it.lines <= hi && it.lines > (i ? buckets[i - 1][1] : 0)).length }));
  const longest = list.slice().sort((a, b) => b.lines - a.lines).slice(0, 5);
  const imports = countBy(list, it => [...new Set([...it.code.matchAll(/^import\s+([\w.]+\*?)/gm)].map(m => m[1].endsWith('*') ? m[1] : m[1].split('.').pop()))]);
  const habits = HABITS.map(([label, re]) => ({ label, value: list.filter(it => re ? re.test(it.code) : isRecursive(it.code)).length }))
    .sort((a, b) => b.value - a.value);
  return card('コードの分析', `
    <div class="an">
      <div><h4>行数の分布</h4>${vbars(hist)}</div>
      <div><h4>長いコード</h4><ol class="alist">${longest.map(it => `<li>${itemLink(it)} — ${it.lines}行</li>`).join('')}</ol></div>
      <div><h4>よく import するもの</h4>${hbars(sortedEntries(imports).slice(0, 10).map(([k, v]) => ({ label: k, value: v })))}</div>
      <div><h4>書き方のクセ (使っている問題の割合)</h4>${hbars(habits, { max: list.length, fmt: r => `${pct(r.value, list.length)}%` })}</div>
    </div>`, true);
}

function secMissingTags(list) {
  const out = [];
  for (const it of list) {
    const gs = new Set(allGenres(it));
    // 3つめの配列のジャンルが付いていれば、そのテクニックも使っていて当然なので候補にしない
    const guess = CODE_HINTS.filter(([g, re, implied = []]) => !gs.has(g) && !implied.some(x => gs.has(x)) && re.test(it.code))
      .map(([g]) => g);
    if (guess.length) out.push({ it, guess });
  }
  if (!out.length) return card('ジャンルの付け忘れ候補', '<div class="hint">コードから推測したテクニックは、すべてジャンルに入っています</div>');
  return card(`ジャンルの付け忘れ候補 (${out.length})`, `<ul class="alist">${out.slice(0, 15).map(({ it, guess }) =>
    `<li>${itemLink(it)} — ${guess.map(esc).join(', ')}</li>`).join('')}</ul>
    ${out.length > 15 ? `<p class="hint">ほか ${out.length - 15} 件</p>` : ''}
    <p class="hint">コードに PriorityQueue・dp[・lowerBound などがあるのに、そのジャンルが付いていない問題です（あくまで推測）</p>`);
}

function secReview(list) {
  const today = ymd(new Date());
  const rv = list.filter(it => it.review).map(it => ({ it, last: editDays(it).slice(-1)[0] }))
    .sort((a, b) => (a.last || '').localeCompare(b.last || ''));
  const memoed = list.filter(it => it.memo.trim());
  const avgMemo = memoed.length ? Math.round(memoed.reduce((s, it) => s + it.memo.length, 0) / memoed.length) : 0;
  const revisited = list.map(it => ({ it, n: editDays(it).length })).filter(x => x.n >= 2).sort((a, b) => b.n - a.n).slice(0, 5);
  return card('復習とメモ', `
    <div class="kpis">${kpi(rv.length, '要復習')}${kpi(`${pct(memoed.length, list.length)}%`, 'メモあり')}${kpi(`${avgMemo}字`, 'メモの平均の長さ')}</div>
    <h4>要復習 (最後に手を入れてから時間がたっている順)</h4>
    ${rv.length ? `<ol class="alist">${rv.slice(0, 10).map(({ it, last }) => `<li>${itemLink(it)}${last ? ` — ${daysBetween(last, today)}日前` : ''}${it.memo ? ` <span class="hint">${esc(it.memo.slice(0, 30))}</span>` : ''}</li>`).join('')}</ol>` : '<div class="hint">要復習の問題はありません</div>'}
    ${revisited.length ? `<h4>何度も書き直した問題 (コミットされた日数)</h4><ol class="alist">${revisited.map(({ it, n }) => `<li>${itemLink(it)} — ${n}日</li>`).join('')}</ol>` : ''}`);
}

// 上の集計から「次にやること」を組み立てる
function secAdvice(list) {
  const tips = [];
  const none = list.filter(it => !allGenres(it).length);
  if (none.length) tips.push(`未分類の問題が <b>${none.length}</b> 件あります（例: ${none.slice(0, 3).map(itemLink).join(', ')}）。ジャンルを付けると分析の精度が上がります。`);
  const weak = genreStats(list).filter(s => s.n >= 2 && s.review).sort((a, b) => b.review / b.n - a.review / a.n)[0];
  if (weak) {
    const p = pagesForGenre(weak.g)[0];
    tips.push(`${genreLink(weak.g)} は要復習の割合が <b>${pct(weak.review, weak.n)}%</b> と高めです。${p ? `<a class="tb-link" href="#textbook/${encodeURIComponent(p.slug)}">📘 ${esc(p.title)}</a> を読み直してみましょう。` : ''}`);
  }
  const used = new Set(list.flatMap(allGenres));
  const untried = vocab.filter(g => !used.has(g) && pagesForGenre(g).length).slice(0, 3);
  if (untried.length) tips.push(`まだ解いていないジャンル: ${untried.map(g => `<a class="tb-link" href="#textbook/${encodeURIComponent(pagesForGenre(g)[0].slug)}">📘 ${esc(g)}</a>`).join(' ')}`);
  const dates = list.map(solvedOn).filter(Boolean).sort();
  if (dates.length) {
    const gap = daysBetween(dates[dates.length - 1], ymd(new Date()));
    tips.push(gap === 0 ? '今日も解いています 👍 この調子で続けましょう。' : `最後に解いてから <b>${gap}日</b> たっています。${gap >= 3 ? '1問だけでも解いてみましょう。' : ''}`);
  }
  const abc = list.filter(it => it.type === 'abc' && problemIndex(it) >= 0);
  if (abc.length) {
    const contestsN = new Set(abc.map(it => it.contest)).size;
    // 半分以上の回で解けている一番後ろの問題の、次の問題を目標にする
    const rate = L => new Set(abc.filter(it => it.problem === L).map(it => it.contest)).size / contestsN;
    const letters = 'ABCDEFG'.split('');
    const reached = letters.map(rate).map((r, i) => r >= 0.5 ? i : -1).reduce((a, b) => Math.max(a, b), -1);
    const next = letters[reached + 1];
    if (reached >= 0 && next) tips.push(`ABC は ${letters[reached]} 問題まで半分以上の回で解けています。${next} 問題は <b>${Math.round(rate(next) * 100)}%</b> なので、次の目標は ${next} 問題です。`);
  }
  const rv = list.filter(it => it.review).length;
  if (rv) tips.push(`要復習が <b>${rv}</b> 件あります。解答タブの「要復習のみ」→「🎲 ランダム復習」で1問ずつ片付けましょう。`);
  return card('次にやること', tips.length ? `<ul class="alist">${tips.map(t => `<li>${t}</li>`).join('')}</ul>` : '', true);
}

function showAnalysis() {
  const list = analysisTarget();
  $('abody').innerHTML = list.length ? `<div class="an">
    ${secAdvice(list)}
    ${secSummary(list)}
    ${secActivity(list)}
    ${secGenres(list)}
    ${secStrength(list)}
    ${secTrend(list)}
    ${secPairs(list)}
    ${secUntried(list)}
    ${secProblems(list)}
    ${secReview(list)}
    ${secAbc(list)}
    ${secCode(list)}
    ${secMissingTags(list)}
  </div>` : '<div class="empty">この条件に当てはまる解答がありません</div>';

  $('abody').querySelectorAll('[data-open]').forEach(a => a.onclick = e => {
    e.preventDefault();
    location.hash = '';
    open(a.dataset.open);
  });
  $('abody').querySelectorAll('[data-genre]').forEach(a => a.onclick = e => {
    e.preventDefault();
    genre = store('genre', a.dataset.genre);
    location.hash = '';
  });
}

['akind', 'aperiod'].forEach(id => {
  $(id).value = store(id) ?? '';
  $(id).addEventListener('input', () => { store(id, $(id).value); showAnalysis(); });
});
