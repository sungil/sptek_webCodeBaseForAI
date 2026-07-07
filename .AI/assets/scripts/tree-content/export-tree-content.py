#!/usr/bin/env python3
"""Export a directory tree and include content only for Markdown files."""

from __future__ import annotations

import argparse
from pathlib import Path


DEFAULT_OUTPUT = Path.home() / "AI-tree-content.md"


def sorted_children(path: Path) -> tuple[list[Path], list[Path]]:
    children = sorted(path.iterdir(), key=lambda item: (not item.is_dir(), item.name.casefold()))
    dirs = [item for item in children if item.is_dir()]
    files = [item for item in children if item.is_file()]
    return dirs, files


def default_root() -> Path:
    for candidate in (Path(".AI"), Path(".ai")):
        if candidate.is_dir():
            return candidate
    return Path(".AI")


def read_markdown(path: Path) -> str:
    with path.open("r", encoding="utf-8", newline="") as handle:
        return handle.read()


def emit_file(path: Path, root: Path, level: int, args: argparse.Namespace, lines: list[str]) -> None:
    indent = args.indent * level
    content_indent = args.indent * (level + 1)
    lines.append(f"{indent}{path.name}")

    if path.suffix.casefold() != ".md":
        return

    output_path = args.output.resolve()
    if path.resolve() == output_path:
        return

    content = read_markdown(path)
    normalized = content.replace("\r\n", "\n").replace("\r", "\n")
    for content_line in normalized.split("\n"):
        lines.append(f"{content_indent}{content_line}")


def emit_dir(path: Path, root: Path, level: int, args: argparse.Namespace, lines: list[str]) -> None:
    indent = args.indent * level
    lines.append(f"{indent}{path.name}/")
    dirs, files = sorted_children(path)
    for child_dir in dirs:
        emit_dir(child_dir, root, level + 1, args, lines)
    for child_file in files:
        emit_file(child_file, root, level + 1, args, lines)


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="Export a directory tree and indent Markdown file content below each .md file name."
    )
    parser.add_argument("root", nargs="?", type=Path, default=None, help="Directory to export. Default: .AI, then .ai")
    parser.add_argument(
        "-o",
        "--output",
        type=Path,
        default=DEFAULT_OUTPUT,
        help=f"Output file. Default: {DEFAULT_OUTPUT}",
    )
    parser.add_argument("--indent", default="  ", help="Indent string. Default: two spaces.")
    return parser.parse_args()


def main() -> int:
    args = parse_args()
    root = (args.root if args.root else default_root()).resolve()
    if not root.is_dir():
        raise SystemExit(f"Root directory does not exist: {root}")

    lines: list[str] = []
    emit_dir(root, root, 0, args, lines)
    output = "\n".join(lines) + "\n"

    args.output.parent.mkdir(parents=True, exist_ok=True)
    with args.output.open("w", encoding="utf-8", newline="\n") as handle:
        handle.write(output)
    print(f"Exported {root} to {args.output.resolve()}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
