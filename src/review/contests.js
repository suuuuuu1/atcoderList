// 🏆 コンテストタブ: リポジトリにあるコンテストのフォルダ (src/abc/abc480 など) を
// AtCoder Problems 風の「コンテスト × 問題」の表に並べる。
// index.html の $, esc, store, items, bundleFiles, hasCode, allGenres, open を使う。

// abc480 → [abc, 480] のように分けて、番号の大きい (新しい) 順に並べる
const contestNum = c => Number((c.match(/\d+/) || ['0'])[0]);

function showContests() {
  const kind = $('ckind').value;
  const solvedOnly = $('csolved').checked;
  const byPath = new Map(items.map(it => [it.path, it]));

  // フォルダごとに問題ファイルをまとめる
  const contests = new Map();
  for (const path of Object.keys(bundleFiles)) {
    const [type, contest, file] = path.split('/');
    if (!file || (kind && type !== kind)) continue;
    const key = `${type}/${contest}`;
    if (!contests.has(key)) contests.set(key, { type, contest, probs: {} });
    const prob = file.replace(/\.kt$/, '').toUpperCase();
    contests.get(key).probs[prob] = byPath.get(path) || null; // null = テンプレートのまま
  }

  let rows = [...contests.values()];
  if (solvedOnly) rows = rows.filter(r => Object.values(r.probs).some(Boolean));
  rows.sort((a, b) => a.type.localeCompare(b.type) || contestNum(b.contest) - contestNum(a.contest) || a.contest.localeCompare(b.contest));

  const cols = [...new Set(rows.flatMap(r => Object.keys(r.probs)))]
    .sort((a, b) => a.length - b.length || a.localeCompare(b));

  // 問題ごとの「解いた数 / フォルダ数」
  const rate = cols.map(col => {
    const has = rows.filter(r => col in r.probs);
    const solved = has.filter(r => r.probs[col]).length;
    return has.length ? `${col}: <b>${solved}</b><span class="den">/${has.length}</span>` : '';
  }).filter(Boolean);
  const solvedTotal = rows.reduce((s, r) => s + Object.values(r.probs).filter(Boolean).length, 0);
  const reviewTotal = rows.reduce((s, r) => s + Object.values(r.probs).filter(it => it?.review).length, 0);

  const cell = (r, col) => {
    if (!(col in r.probs)) return '<td class="none"></td>';
    const it = r.probs[col];
    if (!it) return `<td class="todo"><span class="pl">${esc(col)}</span></td>`;
    const gs = allGenres(it);
    return `<td class="${it.review ? 'rv' : 'ok'}" data-open="${esc(it.path)}" title="${esc(it.memo || '')}">
      <span class="pl">${esc(col)}</span>${it.review ? '<span class="flag">●</span>' : ''}
      <div class="cg">${gs.length ? gs.map(g => `<span>${esc(g)}</span>`).join('') : '<span class="none">未分類</span>'}</div></td>`;
  };

  const atcoderUrl = r => r.type === 'betu' ? '' : `https://atcoder.jp/contests/${encodeURIComponent(r.contest)}`;

  $('cbody').innerHTML = `
    <div class="cstats">
      <span>コンテスト <b>${rows.length}</b> 回</span>
      <span>解いた問題 <b>${solvedTotal}</b></span>
      <span>要復習 <b>${reviewTotal}</b></span>
    </div>
    ${rate.length ? `<div class="crate">解いた数: ${rate.join(' ')}</div>` : ''}
    <div class="clegend"><span class="ok">解いた</span><span class="rv">要復習</span><span class="todo">未着手 (テンプレートのみ)</span></div>
    ${rows.length ? `<div class="ctable-wrap"><table class="ctable">
      <thead><tr><th>コンテスト</th>${cols.map(c => `<th>${esc(c)}</th>`).join('')}</tr></thead>
      <tbody>${rows.map(r => `<tr><th>${atcoderUrl(r)
          ? `<a href="${atcoderUrl(r)}" target="_blank" rel="noopener">${esc(r.contest.toUpperCase())}</a>`
          : esc(r.contest.toUpperCase())}</th>${cols.map(col => cell(r, col)).join('')}</tr>`).join('')}</tbody>
    </table></div>` : '<div class="empty">該当するコンテストがありません</div>'}`;

  $('cbody').querySelectorAll('td[data-open]').forEach(td => td.onclick = () => {
    location.hash = '';
    open(td.dataset.open);
  });
}

$('ckind').value = store('ckind') ?? 'abc';
$('csolved').checked = store('csolved') !== '0';
$('ckind').addEventListener('input', () => { store('ckind', $('ckind').value); showContests(); });
$('csolved').addEventListener('input', () => { store('csolved', $('csolved').checked ? '1' : '0'); showContests(); });
