# GitHub Pages 用に、ReviewApp.kt の /api/bundle と同じ形式の JSON を標準出力に書く。
# 使い方 (プロジェクト直下で): python3 src/review/bundle.py > _site/bundle.json
import json
import sys
from pathlib import Path

files = {}
for d in ["abc", "arc", "atc", "betu"]:
    for p in sorted(Path("src", d).rglob("*.kt")):
        files[p.relative_to("src").as_posix()] = p.read_text(encoding="utf-8")

tsv = Path("genres.tsv")
bundle = {
    "editable": False,
    "tsv": tsv.read_text(encoding="utf-8") if tsv.exists() else "",
    "files": files,
}
sys.stdout.buffer.write(json.dumps(bundle, ensure_ascii=False).encode("utf-8"))
