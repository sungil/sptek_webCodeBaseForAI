# Agent별 확장기능 관리 정책

- Codex, Claude Code, GitHub Copilot 등 Agent별 확장기능의 관리 기준을 정의한다.
- 여기서 확장기능은 Skill, Command, Instruction 등 각 Agent가 자체적으로 발견하고 실행하는 기능을 의미한다.

- 확장기능과 관련한 정책 및 수행절차는 Agent 중립 디렉터리인 _AI/ 하위 각 영역에 작성한다.
- .codex/, .claude/, .github/에는 각 Agent가 기능을 발견하고 실행하는 데 필요한 최소한의 Adapter만 둔다.
- Adapter에는 metadata, 간단한 사용 조건, _AI/ 하위에 작성된 핵심 내용 참조 경로, Agent별 실행 래퍼만 포함한다.
- 동일한 정책이나 절차를 Agent별 파일에 중복 작성하지 않는다.
- 공통 내용이 변경되면 _AI/ 원본을 수정하고, 관련 Adapter의 참조 경로와 연결 정보도 함께 업데이트한다.
- Agent 고유 기능으로 공통화 할 수 없는 설정만 Agent별 디렉터리에 별도로 작성한다.
