// 🏆 コンテストタブ: AtCoder Problems の API から自分の提出を取ってきて、コンテスト × 問題の表にする。
// データ: https://github.com/kenkoooo/AtCoderProblems/blob/master/doc/api.md
// index.html の $, esc, store, items, open を使う。

const AP = 'https://kenkoooo.com/atcoder';
let cx = null; // { contests, problems, models, cps } 一度だけ読む
let subsCache = {}; // user -> 提出の配列

const DIFF_COLORS = [[400, '#808080'], [800, '#804000'], [1200, '#008000'], [1600, '#00c0c0'], [2000, '#0000ff'], [2400, '#c0c000'], [2800, '#ff8000'], [Infinity, '#ff0000']];
// AtCoder Problems と同じく、400 未満の diff は 400 / exp((400 - d) / 400) に補正して表示
const clipDiff = d => Math.round(d >= 400 ? d : 400 / Math.exp((400 - d) / 400));
const diffColor = d => DIFF_COLORS.find(([lim]) => d < lim)[1];

// 直接取りに行き、だめなら PC のアプリ (ReviewApp.kt) 経由で取る
async function apFetch(path) {
  try {
    const res = await fetch(`${AP}/${path}`);
    if (res.ok) return res;
  } catch (e) { /* CORS などで失敗 → 中継を試す */ }
  const res = await fetch('/api/ap?path=' + encodeURIComponent(path));
  if (!res.ok) throw new Error(`AtCoder Problems からの取得に失敗しました (${res.status})`);
  return res;
}

async function loadContestData() {
  if (cx) return cx;
  const get = async name => (await apFetch(`resources/${name}`)).json();
  const [contests, problems, models, cps] = await Promise.all(
    ['contests.json', 'problems.json', 'problem-models.json', 'contest-problem.json'].map(get));
  cx = {
    contests: Object.fromEntries(contests.map(c => [c.id, c])),
    problems: Object.fromEntries(problems.map(p => [p.id, p])),
    models, cps,
  };
  return cx;
}

// 提出は 1 回 500 件まで。端末に保存しておき、前回の続きから取りに行く
async function loadSubmissions(user, onProgress) {
  const key = 'subs:' + user.toLowerCase();
  let saved = [];
  try { saved = JSON.parse(localStorage.getItem(key) || '[]'); } catch (e) {}
  const seen = new Set(saved.map(s => s[0]));
  let from = saved.reduce((m, s) => Math.max(m, s[3]), 0);
  for (;;) {
    const res = await apFetch(`atcoder-api/v3/user/submissions?user=${encodeURIComponent(user)}&from_second=${from}`);
    const page = await res.json();
    for (const s of page) {
      if (seen.has(s.id)) continue;
      seen.add(s.id);
      saved.push([s.id, s.contest_id, s.problem_id, s.epoch_second, s.result]);
    }
    onProgress?.(saved.length);
    if (page.length < 500) break;
    from = page[page.length - 1].epoch_second;
    await new Promise(r => setTimeout(r, 1000)); // API へのお願い: 連続アクセスは 1 秒あける
  }
  try { localStorage.setItem(key, JSON.stringify(saved)); } catch (e) {}
  return saved;
}

function contestKind(id) {
  if (/^abc\d+$/.test(id)) return 'abc';
  if (/^arc\d+$/.test(id)) return 'arc';
  if (/^agc\d+$/.test(id)) return 'agc';
  if (/^ahc\d+$/.test(id)) return 'ahc';
  return 'other';
}

async function showContests(refresh) {
  const user = $('cuser').value.trim();
  store('cuser', user);
  if (!user) {
    $('cbody').innerHTML = '<div class="empty">上の欄に AtCoder の ID を入れて「表示」を押してください</div>';
    return;
  }
  $('cbody').innerHTML = '<div class="empty">読み込み中…</div>';
  try {
    const data = await loadContestData();
    if (refresh || !subsCache[user]) {
      subsCache[user] = await loadSubmissions(user, n => $('cbody').innerHTML = `<div class="empty">提出を読み込み中… ${n} 件</div>`);
    }
    renderContests(user, data, subsCache[user]);
  } catch (e) {
    $('cbody').innerHTML = `<div class="empty">読み込みに失敗しました: ${esc(String(e.message || e))}</div>`;
  }
}

function renderContests(user, data, subs) {
  const kind = $('ckind').value, onlyJoined = $('cjoined').checked;

  // 問題ごとの状態: 2 = コンテスト中に AC, 1 = あとで AC, 0 = 提出したが AC なし
  const status = {}, joined = new Set(), tried = new Set();
  for (const [, cid, pid, t, result] of subs) {
    const c = data.contests[cid];
    const inContest = c && t >= c.start_epoch_second && t < c.start_epoch_second + c.duration_second;
    if (inContest && c.duration_second < 60 * 60 * 24 * 7) joined.add(cid); // 常設コンテストは除く
    tried.add(cid);
    const v = result === 'AC' ? (inContest ? 2 : 1) : 0;
    status[pid] = Math.max(status[pid] ?? -1, v);
  }

  const byContest = {};
  for (const cp of data.cps) (byContest[cp.contest_id] ||= []).push(cp);

  const rows = Object.values(data.contests)
    .filter(c => (kind === 'all' || contestKind(c.id) === kind) && byContest[c.id])
    .filter(c => onlyJoined ? joined.has(c.id) : (kind !== 'other' || tried.has(c.id)))
    .sort((a, b) => b.start_epoch_second - a.start_epoch_second);

  // 列: A, B, C, ... (Ex は H として扱う)
  const idxKey = i => i === 'Ex' ? 'H' : i;
  const cols = [...new Set(rows.flatMap(c => byContest[c.id].map(p => idxKey(p.problem_index))))]
    .sort((a, b) => a.length - b.length || a.localeCompare(b));

  // 参加した回での問題ごとの正答率
  const joinedRows = rows.filter(c => joined.has(c.id));
  const rate = cols.map(col => {
    const ps = joinedRows.flatMap(c => byContest[c.id].filter(p => idxKey(p.problem_index) === col));
    const ac = ps.filter(p => status[p.problem_id] === 2).length;
    return ps.length ? `${col}: <b>${Math.round(ac / ps.length * 100)}%</b> <span class="hint">(${ac}/${ps.length})</span>` : '';
  }).filter(Boolean);
  const acTotal = Object.values(status).filter(v => v >= 1).length;

  const localCode = new Map(items.map(it => [`${it.contest}/${it.problem}`, it.path]));

  const cell = (c, p) => {
    if (!p) return '<td></td>';
    const prob = data.problems[p.problem_id] || {};
    const model = data.models[p.problem_id];
    const d = model?.difficulty != null ? clipDiff(model.difficulty) : null;
    const st = status[p.problem_id];
    const local = localCode.get(`${c.id}/${idxKey(p.problem_index).toUpperCase()}`) || localCode.get(`${c.id}/${p.problem_index.toUpperCase()}`);
    return `<td class="st${st ?? ''}">
      ${d != null ? `<span class="dot" style="--c:${diffColor(d)}" title="difficulty ${d}"></span>` : ''}
      <a href="https://atcoder.jp/contests/${esc(c.id)}/tasks/${esc(p.problem_id)}" target="_blank" rel="noopener">${esc(prob.title || p.problem_index)}</a>
      ${local ? `<a class="mycode" href="#" data-open="${esc(local)}" title="自分のコードを見る">📝</a>` : ''}</td>`;
  };

  $('cbody').innerHTML = `
    <div class="cstats">
      <span><b>${esc(user)}</b></span>
      <span>参加 <b>${joined.size}</b> 回</span>
      <span>AC <b>${acTotal}</b> 問</span>
      <span>表示 <b>${rows.length}</b> 回</span>
    </div>
    ${rate.length ? `<div class="crate">参加した回の正答率 (コンテスト中に AC): ${rate.join(' ')}</div>` : ''}
    <div class="clegend"><span class="st2">コンテスト中に AC</span><span class="st1">あとで AC</span><span class="st0">提出したが未 AC</span></div>
    ${rows.length ? `<div class="ctable-wrap"><table class="ctable">
      <thead><tr><th>コンテスト</th>${cols.map(c => `<th>${esc(c)}</th>`).join('')}</tr></thead>
      <tbody>${rows.map(c => {
        const ps = Object.fromEntries(byContest[c.id].map(p => [idxKey(p.problem_index), p]));
        const date = new Date(c.start_epoch_second * 1000).toLocaleDateString();
        return `<tr><th><a href="https://atcoder.jp/contests/${esc(c.id)}" target="_blank" rel="noopener">${esc(c.id.toUpperCase())}</a>
          ${joined.has(c.id) ? '<span class="joined" title="コンテスト中に提出した回">参加</span>' : ''}<div class="hint">${date}</div></th>
          ${cols.map(col => cell(c, ps[col])).join('')}</tr>`;
      }).join('')}</tbody></table></div>`
      : `<div class="empty">${onlyJoined ? '参加した回がありません。「参加した回のみ」を外すと全部の回が出ます' : '該当するコンテストがありません'}</div>`}`;

  $('cbody').querySelectorAll('[data-open]').forEach(a => a.onclick = e => {
    e.preventDefault();
    location.hash = '';
    open(a.dataset.open);
  });
}

$('cuser').value = store('cuser') || '';
$('ckind').value = store('ckind') || 'abc';
$('cjoined').checked = store('cjoined') !== '0';
$('cshow').onclick = () => showContests(true);
$('cuser').addEventListener('keydown', e => { if (e.key === 'Enter') showContests(true); });
$('ckind').addEventListener('input', () => { store('ckind', $('ckind').value); showContests(false); });
$('cjoined').addEventListener('input', () => { store('cjoined', $('cjoined').checked ? '1' : '0'); showContests(false); });
