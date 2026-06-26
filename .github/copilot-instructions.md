# Project specific instructions
본 지침은 SPTek 에서 개발한 SPT Framework 프로젝트를 AI를 활용해 개발자가 쉽게 이해하고 잘 활용할 수 있도록 하는 것에 목적을 두고 있음


1. 모든 답변의 기본 지침
* 모든 답변은 현재 프로젝트 내 파일을 참고해서 답변을 해야하는게 가장 중요해, 이경우 답변의 시작은 "SPT Framework 를 기준으로한 설명입니다" 라고 언급하고 답변해줘
* 현재 프로젝트 내 파일로 설명할 수 없는 경우는 "일반적인 사항에 대한 설명 입니다" 라고 언급하고 답변 해줘


2. 코드 예시와 관련되 지침
* 기존 코드를 언급해서 보여줄때는 import 선언과 주석을 제거하고 간결하게 보여줘


3. 코드 설명과 관련한 답변 지침
* 프로젝트 내 클레스명과 동일한 특정단어에 대한 질문은 반드시 해당 클레스 파일일 실시간으로 참고해서 답변해야함 (너의 기억의 코드를 활용해서는 안되고 실시간으로 파일 참조해 답변할것)
* 클레스 참조 위치는 [_frameworkWebCore](../src/main/java/com/sptek/_frameworkWebCore) 와 [_projectCommon](../src/main/java/com/sptek/_projectCommon)를 참조할 것
* 특정 클레스와 관련된 설정 내용이 있는 경우 해당 설정 내용의 의미에 대해서도 설명해줘
* 설정 참조 위치는 [_frameworkApplicationProperties](../src/main/resources/_frameworkWebCoreResources/_frameworkApplicationProperties) 와 [_projectApplicationProperties](../src/main/resources/_projectCommonResources/_projectApplicationProperties)를 참조할 것


4. 기타 특정 질문 지침
* 로그백 설정과 관련한 질문은 [logbackConfig](../src/main/resources/_frameworkWebCoreResources/logbackConfig) 을 참조해 설명할것
* 다국어 설정과 관련한 질문은 [i18n](../src/main/resources/_projectCommonResources/i18n) 을 참조해 설명할것
* HandlerMethodArgumentResolver, Aspect, ResponseBodyAdvice, EventListener, Filter, OncePerRequestFilter, HandlerInterceptor, SecurityFilterChain 
  과 관련된 예시 코드를 원할때는 [_projectCommon](../src/main/java/com/sptek/_projectCommon) 내부 파일을 실시간으로 참조해서 답변해줘


5. 커밋 메시지 생성 지침
* 커밋 메시지를 생성할 때는 현재 변경 diff를 기준으로 한글로 작성해줘
* 제목은 변경 의도가 드러나도록 간결하게 작성하고, 단순 변경은 제목만 작성해줘
* 본문이 필요한 경우 주요 변경점과 검증 결과만 짧게 포함해줘
* 민감값, 개인 경로, 임시 파일, 생성 파일 내용은 커밋 메시지에 포함하지 말아줘
