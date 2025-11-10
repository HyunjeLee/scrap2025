# Scrap2025

<img width="1033" height="960" alt="figma to android" src="https://github.com/user-attachments/assets/d83e56f9-64e4-4cf2-8efa-e8e82c6e8b97" />

> AI 기반 바이브 코딩으로 개발된 Android 애플리케이션


## 프로젝트 소개

**Scrap2025**는 안드로이드 내장 기능인 공유하기를 통해 생성된 링크를 보관하고 정리할 수 있는 Android 애플리케이션입니다.<br>
사용자는 웹 콘텐츠, 이미지, 링크 등을 카테고리별로 정리하고 관리할 수 있습니다.

### 주요 특징

이 프로젝트는 AI를 활용한 실험적 개발 프로젝트입니다. <br>
Claude Code가 주도적으로 아키텍처 설계와 구현을 담당하며, 개발자는 방향성 제시와 검토 역할을 수행합니다.
Claude Code와 함께 MCP(Model Context Protocol) 서버를 활용하여 개발됩니다.


## 활성 MCP 서버 + 플러그인

### Figma Dev Mode MCP

Figma 디자인을 직접 코드로 변환합니다.

**기능:**
- Figma 디자인 스크린샷 추출 (`get_screenshot`)
- 디자인 컨텍스트 조회 (`get_design_context`)
- 디자인 변수 정의 가져오기 (`get_variable_defs`)

### Context7 MCP

최신 라이브러리 문서와 API 정보를 실시간으로 제공합니다.

**기능:**
- 라이브러리 ID 자동 검색 (`resolve-library-id`)
- 최신 공식 문서 조회 (`get-library-docs`)
- Deprecated API 사전 감지
- 코드 예제 및 베스트 프랙티스 제공

### IDE 플러그인

Android Studio/IntelliJ IDEA와의 통합을 제공합니다.

**기능:**
- 실시간 코드 진단 (`getDiagnostics`)
- 컴파일 에러 및 경고 감지
- 코드 품질 개선 제안
- **GUI 기반 코드/파일 선택**


## 기술 스택

| 카테고리 | 기술 |
|---------|------|
| 언어 | Kotlin |
| UI | Jetpack Compose, Material Design 3 |
| 아키텍처 | MVVM + Clean Architecture |
| 비동기 처리 | Coroutines, Flow |
| 상태 관리 | StateFlow, ViewModel |
| 빌드 시스템 | Gradle (Kotlin DSL) |


## 시스템 요구사항

- **최소 SDK**: 26 (Android 8.0)
- **대상 SDK**: 36 (Android 15)
- **컴파일 SDK**: 36
- **Kotlin**: 1.9+
- **Gradle**: 8.x


## 프로젝트 구조

```
app/src/main/java/com/scrap2025/scrap2025/
├── ui/                      # UI 계층 (Feature-based)
│   ├── scrap/              # 스크랩 기능
│   ├── category/           # 카테고리 관리
│   ├── favorite/           # 즐겨찾기
│   ├── search/             # 검색
│   ├── mypage/             # 마이페이지
│   ├── login/              # 로그인
│   ├── main/               # 메인 화면
│   ├── common/             # 공통 컴포넌트
│   └── theme/              # 디자인 시스템
├── viewmodel/              # ViewModel 계층
├── model/                  # 도메인 모델
├── repository/             # 데이터 저장소 인터페이스
├── data/                   # 데이터 소스 구현
│   ├── local/             # 로컬 데이터
│   └── remote/            # 원격 API
└── navigation/             # 네비게이션 설정
```


## 주요 기능

### 구현된 기능



### 개발 예정 기능

- [ ] 사용자 인증 (로그인/회원가입)
- [ ] 클라우드 동기화
- [ ] 이미지 OCR 텍스트 추출
- [ ] 태그 기반 분류


## 아키텍처

### MVVM 패턴

```
┌─────────────┐
│     View    │  (Composable)
│  (UI Layer) │
└──────┬──────┘
       │ observes state
       │ sends events
┌──────▼──────┐
│  ViewModel  │  (StateFlow)
└──────┬──────┘
       │ requests data
┌──────▼──────┐
│ Repository  │  (Interface)
└──────┬──────┘
       │ implements
┌──────▼──────┐
│  Data Layer │  (Local/Remote)
└─────────────┘
```


## 코딩 규칙

### 네이밍 컨벤션

- **클래스**: `PascalCase` (예: `ScrapViewModel`)
- **함수/변수**: `camelCase` (예: `loadData()`)
- **상수**: `CONSTANT_CASE` (예: `MAX_ITEMS`)
- **Composable**: `PascalCase` (예: `ScrapScreen()`)

### Composable 함수 규칙

```kotlin
@Composable
fun MyComponent(
    modifier: Modifier = Modifier,      // 1. Modifier
    data: String,                       // 2. 데이터
    onEvent: () -> Unit                 // 3. 콜백
) {
    // 구현
}
```

---

**Note**: 이 프로젝트는 AI를 활용한 실험적 개발 프로젝트입니다. Claude Code가 주도적으로 아키텍처 설계와 구현을 담당하며, 개발자는 방향성 제시와 검토 역할을 수행합니다. Claude Code와 함께 MCP(Model Context Protocol) 서버를 활용하여 개발됩니다.
