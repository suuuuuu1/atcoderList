// 問題ごとのコメント欄。giscus (https://giscus.app) を使い、コメントは GitHub Discussions に保存される。
// 書き込むには GitHub でログインが必要。読むのはログインなしでも OK。
// repo / repoId / categoryId が空のあいだはコメント欄を出さない。
// index.html の esc を使う。

const GISCUS = {
  repo: 'suuuuuu1/atcoderList-comments',
  // https://giscus.app/api/discussions/categories?repo=suuuuuu1/atcoderList-comments で調べた値
  repoId: 'R_kgDOVOO_IQ',
  category: 'Announcements', // スレッドを新しく作れるのは giscus (と管理者) だけのカテゴリ
  categoryId: 'DIC_kwDOVOO_Ic4DI3ec',
};

// 問題 (it) のコメント欄を container の中に作る。問題ごとに別のスレッドになる
function showComments(container, it) {
  if (!GISCUS.repo || !GISCUS.repoId || !GISCUS.categoryId) {
    container.innerHTML = '';
    return;
  }
  // スレッドのタイトル。例: "abc480 C"、"edpc C"
  const term = `${it.contest} ${it.problem}`;
  container.innerHTML = `<h3 class="cm-title">💬 みんなのコメント <span class="hint">${esc(term)}・GitHub でログインすると書き込めます</span></h3>`;

  const s = document.createElement('script');
  s.src = 'https://giscus.app/client.js';
  Object.entries({
    'data-repo': GISCUS.repo,
    'data-repo-id': GISCUS.repoId,
    'data-category': GISCUS.category,
    'data-category-id': GISCUS.categoryId,
    'data-mapping': 'specific',      // term と完全に同じタイトルのスレッドを使う
    'data-term': term,
    'data-strict': '1',              // "abc48 C" と "abc480 C" を取り違えない
    'data-reactions-enabled': '1',
    'data-emit-metadata': '0',
    'data-input-position': 'top',
    'data-theme': 'preferred_color_scheme',
    'data-lang': 'ja',
    'data-loading': 'lazy',
  }).forEach(([k, v]) => s.setAttribute(k, v));
  s.crossOrigin = 'anonymous';
  s.async = true;
  container.appendChild(s);
}
