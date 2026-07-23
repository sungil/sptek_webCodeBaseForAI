# -*- coding: utf-8 -*-
from __future__ import annotations

import argparse
import sys
from pathlib import Path


SESSION_CONTEXT_COLOR = "\033[1;36m"
RESET_COLOR = "\033[0m"

ROLES = {
    "1": ("비즈영역 설계", "business_design"),
    "2": ("비즈영역 기획", "business_planning"),
    "3": ("비즈영역 개발", "business_development"),
    "4": ("Framework 개발", "framework_development"),
}

FRAMEWORK_COMPANY_NAMES = {"_sptek"}


def should_use_color(color_mode: str) -> bool:
    if color_mode == "always":
        return True
    if color_mode == "never":
        return False
    return sys.stdout.isatty()


def session_context_text(role: str, work_area: str, *, color: bool) -> str:
    text = f"[현재 세션은 {role}, {work_area} 관점에서 답변합니다]"
    if not color:
        return text
    return f"{SESSION_CONTEXT_COLOR}{text}{RESET_COLOR}"


def repo_root_from_script() -> Path:
    return Path(__file__).resolve().parents[4]


def print_role_question() -> int:
    print("먼저 사용자 역할을 선택해 주세요.")
    print()
    for role_number, (role_name, _) in ROLES.items():
        print(f"{role_number}. {role_name}")
    return 0


def role_name(role_number: str) -> str | None:
    role = ROLES.get(role_number)
    return role[0] if role else None


def print_unknown_role() -> int:
    print("알 수 없는 역할 번호입니다.")
    print("1, 2, 3, 4 중 하나를 선택해 주세요.")
    return 1


def print_role_selected(role_number: str, *, color: bool) -> int:
    selected_role_name = role_name(role_number)
    if selected_role_name is None:
        return print_unknown_role()

    if role_number == "4":
        print("Framework 개발은 모든 영역에서 조사와 작업이 가능합니다.")
        print("별도의 업무범위를 선택하지 않습니다.")
        print()
        print(session_context_text("Framework 개발", "전체 영역", color=color))
        print("이 세션이 종료될 때까지 최종 답변의 첫 줄에 위 세션 작업 기준을 표시합니다.")
        return 0

    print(f"선택한 역할: {selected_role_name}")
    print("다음 단계로 업무 도메인을 선택해야 합니다.")
    return 0


def find_business_work_areas(repo_root: Path) -> list[str]:
    java_com_dir = repo_root / "src" / "main" / "java" / "com"
    if not java_com_dir.exists():
        return []

    areas: list[str] = []

    for company_dir in sorted(java_com_dir.iterdir(), key=lambda path: path.name):
        if not company_dir.is_dir():
            continue

        company_name = company_dir.name
        if company_name in FRAMEWORK_COMPANY_NAMES:
            continue

        for area_dir in sorted(company_dir.iterdir(), key=lambda path: path.name):
            if not area_dir.is_dir():
                continue

            area_name = area_dir.name
            is_business_domain = area_name.startswith("_") and not area_name.startswith("__")

            if is_business_domain:
                areas.append(f"com.{company_name}.{area_name}")

    return areas


def print_area_question(repo_root: Path) -> int:
    areas = find_business_work_areas(repo_root)

    print("업무 도메인을 선택해 주세요.")
    print()

    for index, area_package in enumerate(areas, start=1):
        print(f"{index}. {area_package}")

    print(f"{len(areas) + 1}. 새 작업영역 추가")
    return 0


def common_package_for(area_package: str) -> str | None:
    parts = area_package.split(".")
    if len(parts) < 3 or parts[0] != "com":
        return None

    company_name = parts[1]
    return f"com.{company_name}.__{company_name}Common"


def print_area_selected(role_number: str, area_number: str, repo_root: Path, *, color: bool) -> int:
    selected_role_name = role_name(role_number)
    if selected_role_name is None:
        return print_unknown_role()

    if role_number == "4":
        print("Framework 개발은 업무범위를 별도로 선택하지 않습니다.")
        print(session_context_text("Framework 개발", "전체 영역", color=color))
        return 0

    areas = find_business_work_areas(repo_root)

    try:
        selected_index = int(area_number)
    except ValueError:
        print("알 수 없는 업무 도메인 번호입니다.")
        print("업무 도메인 선택지의 번호 중 하나를 선택해 주세요.")
        return 1

    new_area_index = len(areas) + 1

    if selected_index == new_area_index:
        print("companyName.domainName 형식으로 업무 영역을 입력해 주세요.")
        return 0

    if selected_index < 1 or selected_index > len(areas):
        print("알 수 없는 업무 도메인 번호입니다.")
        print("업무 도메인 선택지의 번호 중 하나를 선택해 주세요.")
        return 1

    selected_area = areas[selected_index - 1]
    common_package = common_package_for(selected_area)

    print(session_context_text(selected_role_name, selected_area, color=color))
    if common_package and not selected_area.endswith(f".__{selected_area.split('.')[1]}Common"):
        print(f"회사 공통 영역: {common_package}")
    print("이 세션이 종료될 때까지 최종 답변의 첫 줄에 위 세션 작업 기준을 표시합니다.")
    return 0


def main(argv: list[str]) -> int:
    parser = argparse.ArgumentParser(
        description="사용자 역할과 업무범위 확인을 위한 고정 질문과 결과 문구를 출력합니다."
    )
    parser.add_argument(
        "--repo-root",
        default=str(repo_root_from_script()),
        help="업무 도메인 패키지를 탐색할 저장소 루트 경로",
    )
    parser.add_argument(
        "--color",
        choices=["auto", "always", "never"],
        default="auto",
        help="세션 작업 기준 표시의 ANSI 색상 출력 여부",
    )

    subparsers = parser.add_subparsers(dest="command", required=True)

    subparsers.add_parser("role-question", help="사용자 역할 선택 질문을 출력합니다.")

    role_selected_parser = subparsers.add_parser("role-selected", help="선택한 사용자 역할 결과를 출력합니다.")
    role_selected_parser.add_argument("role_number", help="사용자 역할 번호")

    subparsers.add_parser("area-question", help="현재 패키지 구조 기준 업무 도메인 선택 질문을 출력합니다.")

    area_selected_parser = subparsers.add_parser("area-selected", help="선택한 업무 도메인 결과를 출력합니다.")
    area_selected_parser.add_argument("role_number", help="사용자 역할 번호")
    area_selected_parser.add_argument("area_number", help="업무 도메인 번호")

    args = parser.parse_args(argv)
    repo_root = Path(args.repo_root).resolve()
    color = should_use_color(args.color)

    if args.command == "role-question":
        return print_role_question()

    if args.command == "role-selected":
        return print_role_selected(args.role_number, color=color)

    if args.command == "area-question":
        return print_area_question(repo_root)

    if args.command == "area-selected":
        return print_area_selected(args.role_number, args.area_number, repo_root, color=color)

    return 1


if __name__ == "__main__":
    raise SystemExit(main(sys.argv[1:]))
