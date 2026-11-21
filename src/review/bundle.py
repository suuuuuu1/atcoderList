# GitHub Pages 用に、ReviewApp.kt の /api/bundle と同じ形式の JSON を標準出力に書く。
# 使い方 (プロジェクト直下で): python3 src/review/bundle.py > _site/bundle.json
import json
import subprocess
import sys
from pathlib import Path

files = {}
for d in ["abc", "arc", "atc", "betu"]:
    for p in sorted(Path("src", d).rglob("*.kt")):
        files[p.relative_to("src").as_posix()] = p.read_text(encoding="utf-8")

# git の履歴から、ファイルごとにコミットされた日付 (古い順・重複なし)。分析タブで使う。
# GitHub Actions では checkout の fetch-depth: 0 で全履歴を取ってくる必要がある。
dates = {}
try:
    log = subprocess.run(["git", "-c", "core.quotepath=false", "log", "--format=@%ad", "--date=short", "--name-only", "--", "src"],
                         capture_output=True, encoding="utf-8", check=True).stdout
    day = None
    for line in log.splitlines():
        if line.startswith("@"):
            day = line[1:]
        elif line.startswith("src/") and line.endswith(".kt"):
            dates.setdefault(line[4:], set()).add(day)
except (OSError, subprocess.CalledProcessError):
    pass

tsv = Path("genres.tsv")
bundle = {
    "editable": False,
    "tsv": tsv.read_text(encoding="utf-8") if tsv.exists() else "",
    "files": files,
    "dates": {p: sorted(dates[p]) for p in files if p in dates},
    "textbook": {p.name: p.read_text(encoding="utf-8") for p in sorted(Path("src/review/textbook").glob("*.md"))},
}
sys.stdout.buffer.write(json.dumps(bundle, ensure_ascii=False).encode("utf-8"))
