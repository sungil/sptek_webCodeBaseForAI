# -*- coding: utf-8 -*-
from __future__ import annotations

import argparse
from pathlib import Path


DEFAULT_EXCLUDED_DIRS = {
    ".git",
    "__pycache__",
    "_cache",
}

DEFAULT_INCLUDED_SUFFIXES = {
    ".md",
    ".py",
    ".ps1",
    ".sql",
    ".yml",
    ".yaml",
    ".json",
}


def repo_root_from_script() -> Path:
    return Path(__file__).resolve().parents[4]


def should_include(path: Path, ai_root: Path, *, include_scripts: bool) -> bool:
    if any(part in DEFAULT_EXCLUDED_DIRS for part in path.relative_to(ai_root).parts):
        return False

    if path.is_dir():
        return True

    if path.name == "README.md":
        return True

    if path.suffix.lower() == ".md":
        return True

    return include_scripts and path.suffix.lower() in DEFAULT_INCLUDED_SUFFIXES


def visible_children(path: Path, ai_root: Path, *, include_scripts: bool) -> list[Path]:
    children = [child for child in path.iterdir() if should_include(child, ai_root, include_scripts=include_scripts)]
    return sorted(children, key=lambda child: (not child.is_dir(), child.name.lower()))


def print_tree(path: Path, ai_root: Path, *, prefix: str, include_scripts: bool) -> None:
    children = visible_children(path, ai_root, include_scripts=include_scripts)

    for index, child in enumerate(children):
        is_last = index == len(children) - 1
        connector = "`-- " if is_last else "|-- "
        name = child.name + ("/" if child.is_dir() else "")
        print(f"{prefix}{connector}{name}")

        if child.is_dir():
            next_prefix = prefix + ("    " if is_last else "|   ")
            print_tree(child, ai_root, prefix=next_prefix, include_scripts=include_scripts)


def main() -> int:
    parser = argparse.ArgumentParser(
        description="_AI 하위 지침 문서 구조를 세션 컨텍스트용 트리로 출력합니다."
    )
    parser.add_argument(
        "--repo-root",
        default=str(repo_root_from_script()),
        help="저장소 루트 경로",
    )
    parser.add_argument(
        "--include-scripts",
        action="store_true",
        help="Markdown 외에 _AI 하위 보조 스크립트와 설정 파일도 함께 표시합니다.",
    )
    args = parser.parse_args()

    ai_root = Path(args.repo_root).resolve() / "_AI"
    if not ai_root.is_dir():
        print("_AI 디렉터리를 찾을 수 없습니다.")
        return 1

    print("[_AI 지침 문서 구조]")
    print("_AI/")
    print_tree(ai_root, ai_root, prefix="", include_scripts=args.include_scripts)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
