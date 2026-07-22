#!/usr/bin/env python3
"""Recreate Markdown files and move existing non-Markdown files from an AI context export."""

from __future__ import annotations

import argparse
from dataclasses import dataclass
from pathlib import Path


DEFAULT_INPUT = Path.home() / "AI-context.md"
DEFAULT_TARGET_PARENT = Path.home() / "ai-context-restore"


@dataclass
class StackEntry:
    indent: int
    path: Path


@dataclass
class PendingFile:
    indent: int
    path: Path
    content_lines: list[str]


@dataclass
class ParsedTree:
    dirs: list[Path]
    md_files: dict[Path, list[str]]
    non_md_files: list[Path]


def leading_spaces(line: str) -> int:
    return len(line) - len(line.lstrip(" "))


def safe_child(parent: Path, name: str) -> Path:
    if not name or name in {".", ".."} or "/" in name or "\\" in name:
        raise ValueError(f"Invalid tree entry name: {name!r}")
    return parent / name


def normalize(path: Path) -> Path:
    return path.resolve()


def parse_tree(source: list[str], target_parent: Path) -> ParsedTree:
    stack: list[StackEntry] = []
    pending: PendingFile | None = None
    dirs: list[Path] = []
    md_files: dict[Path, list[str]] = {}
    non_md_files: list[Path] = []

    def finish_pending() -> None:
        nonlocal pending
        if not pending:
            return
        if pending.path.suffix.casefold() == ".md":
            md_files[pending.path] = pending.content_lines
        else:
            non_md_files.append(pending.path)
        pending = None

    for raw_line in source:
        indent = leading_spaces(raw_line)

        if pending and pending.path.suffix.casefold() == ".md" and indent > pending.indent:
            content_prefix_len = pending.indent + 2
            if len(raw_line) >= content_prefix_len:
                pending.content_lines.append(raw_line[content_prefix_len:])
            else:
                pending.content_lines.append("")
            continue

        finish_pending()

        text = raw_line[indent:]
        if not text:
            continue

        while stack and stack[-1].indent >= indent:
            stack.pop()
        parent = stack[-1].path if stack else target_parent

        if text.endswith("/"):
            path = safe_child(parent, text[:-1])
            dirs.append(path)
            stack.append(StackEntry(indent=indent, path=path))
        else:
            pending = PendingFile(indent=indent, path=safe_child(parent, text), content_lines=[])

    finish_pending()
    return ParsedTree(dirs=dirs, md_files=md_files, non_md_files=non_md_files)


def existing_files(root_dirs: list[Path]) -> list[Path]:
    found: list[Path] = []
    for root_dir in root_dirs:
        if root_dir.is_dir():
            found.extend(path for path in root_dir.rglob("*") if path.is_file())
    return found


def planned_non_markdown_moves(non_md_files: list[Path], existing: list[Path], desired: set[Path]) -> dict[Path, Path]:
    planned: dict[Path, Path] = {}
    for path in non_md_files:
        if path.exists():
            continue
        candidates = [
            existing_path
            for existing_path in existing
            if existing_path.name == path.name and normalize(existing_path) not in desired and existing_path.exists()
        ]
        if len(candidates) == 1:
            planned[path] = candidates[0]
    return planned


def print_disappearing_files(existing: list[Path], desired: set[Path], moved_sources: set[Path]) -> None:
    disappearing = [
        path
        for path in existing
        if normalize(path) not in desired and normalize(path) not in moved_sources
    ]
    if not disappearing:
        print("No existing files disappear from the new tree.")
        return
    print("Existing files not present in the new tree. They will not be deleted:")
    for path in sorted(disappearing):
        print(f"  {path}")


def write_markdown(path: Path, content_lines: list[str], dry_run: bool) -> bool:
    if dry_run:
        return True
    path.parent.mkdir(parents=True, exist_ok=True)
    with path.open("w", encoding="utf-8", newline="\n") as handle:
        handle.write("\n".join(content_lines))
    return True


def move_non_markdown(path: Path, planned_moves: dict[Path, Path], dry_run: bool) -> tuple[bool, str]:
    if path.exists():
        return True, "already exists"

    source = planned_moves.get(path)
    if not source:
        return False, "not found or ambiguous source"

    if not dry_run:
        path.parent.mkdir(parents=True, exist_ok=True)
        source.replace(path)
    return True, f"moved from {source}"


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="Import an AI context export into directories and files.")
    parser.add_argument(
        "input",
        nargs="?",
        type=Path,
        default=DEFAULT_INPUT,
        help=f"AI context file to import. Default: {DEFAULT_INPUT}",
    )
    parser.add_argument(
        "-t",
        "--target-parent",
        type=Path,
        default=DEFAULT_TARGET_PARENT,
        help=f"Directory under which the top-level exported directory is created. Default: {DEFAULT_TARGET_PARENT}",
    )
    parser.add_argument("--dry-run", action="store_true", help="Parse and report without writing files.")
    return parser.parse_args()


def main() -> int:
    args = parse_args()
    source = args.input.read_text(encoding="utf-8").splitlines()
    target_parent = args.target_parent.resolve()
    tree = parse_tree(source, target_parent)
    root_dirs = [path for path in tree.dirs if path.parent == target_parent]
    desired = {normalize(path) for path in tree.md_files}
    desired.update(normalize(path) for path in tree.non_md_files)
    existing = existing_files(root_dirs)
    planned_moves = planned_non_markdown_moves(tree.non_md_files, existing, desired)
    moved_sources = {normalize(path) for path in planned_moves.values()}

    print_disappearing_files(existing, desired, moved_sources)

    if not args.dry_run:
        for path in tree.dirs:
            path.mkdir(parents=True, exist_ok=True)

    written_md = 0
    moved_non_md = 0
    unresolved_non_md = 0
    for path, content_lines in tree.md_files.items():
        if write_markdown(path, content_lines, args.dry_run):
            written_md += 1

    for path in tree.non_md_files:
        ok, _message = move_non_markdown(path, planned_moves, args.dry_run)
        if ok:
            moved_non_md += 1
        else:
            unresolved_non_md += 1

    action = "Would create/update" if args.dry_run else "Created/updated"
    print(
        f"{action} {len(tree.dirs)} directories and {written_md} Markdown files. "
        f"Moved/found {moved_non_md} non-Markdown files. "
        f"Unresolved {unresolved_non_md} non-Markdown files."
    )
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
