Codex, Claude, GitHub Copilot 등 각 Agent별 확장기능(skill, command.. 등)을 위한 파일 관리 기준을 정의한다.

## 기본원칙
- `.codex`, `.claude`, `.github` 같은 Agent별 폴더는 자동 발견을 위한 metadata 와 얇은 adapter만 둔다.
- 확장기능이 요구하는 front matter, manifest, display name, description 언제 해당 adapter를 사용할지에 대한 짧은 설명을 넣을 수 있다.
- Agent 별 특성상 필요한 얇은 실행 래퍼는 넣을 수 있다.
- 확장기능의 핵심 내용 및 절차는 Agent 중립 디렉토리인 `.AI` 내부의 적절한 위치를 선정하여 그곳에 작성한다. (동일 내용이 중복 작성되지 않도록 관리한다)
- `.AI/` 내부에 작성된 관련 파일을 참조하도록 연결한다.
