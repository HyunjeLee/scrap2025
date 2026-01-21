---
trigger: always_on
---

# Antigravity Rules - scrap2025

## 0. Interaction Protocol (CRITICAL)
 
- **Propose First**: 코드 수정이 필요한 작업의 경우, **반드시 변경할 코드의 전체 내용이나 diff를 먼저 보여주고 사용자의 명시적인 승인("수정해줘", "적용해줘" 등)을 받은 후**에 `write_to_file`이나 `replace_file_content` 도구를 실행한다.

- **Exception**: 사용자가 처음부터 "수정해줘"와 같은 명시적 요청의 경우에는 즉시 적용할 수 있다.

## 1. Environment & Tools

- **OS**: macOS

- **IDE**: Android Studio

- **Language**: Kotlin 100%

- **Linter**: ktlint 준수

- **Configuration**:
  - `libs.versions.toml` (Version Catalog) 사용하여 의존성 버전을 중앙 관리.

- **Formatting**:
  - Indent: **4 spaces** (Tab 사용 금지)
  - 파일 마지막에 Newline 추가
  - Import 최적화 필수 (Unused import 제거)

## 2. Architecture & Patterns (Clean Architecture + MVVM)

- **Architecture**:
  - **Presentation Layer**: ViewModel + Compose UI
  - **Domain Layer**: UseCases (Optional) + Repository Interfaces
  - **Data Layer**: Repository Impl + Data Sources (Local/Remote)

- **Dependency Injection**: Hilt 사용

- **Concurrency**: Coroutines & Flow 사용
  - **Dispatcher Injection**: `Dispatchers.IO` 등을 직접 호출하지 말고, DI로 주입받아 사용 (Testability 확보).

- **Error Handling**:
  - Repository 계층은 Exception을 throw하지 않고 `Result<T> 래퍼로 반환.

- **Navigation**: Jetpack Navigation Compose 사용 (Type-safe navigation 권장)

## 3. UI Guidelines (Jetpack Compose)

- **Performance (Critical)**:
  - **Stability Annotation**: UI State 클래스나 자주 사용되는 데이터 홀더 클래스에는 불필요한 리컴포지션을 방지하기 위해 적극적으로 **`@Immutable`** 또는 **`@Stable`** 어노테이션을 부착.
  - `LazyColumn`/`LazyRow` 사용 시 **`key` 파라미터 필수 제공**.
  - UI State 클래스는 `val` 프로퍼티만 사용.
  - 무거운 연산은 `remember { }` 또는 `derivedStateOf { }` 사용.

- **Lifecycle Safety**:
  - Flow 수집 시 `collectAsStateWithLifecycle()` 사용 (Lifecycle-aware).

- **State Hoisting**:
  - `Route/Screen` (Stateful) vs `Content` (Stateless) 분리 패턴 준수.

- **Previews**:
  - 모든 UI 컴포넌트는 `@Preview` 작성 필수.
  - Light Mode / Dark Mode 모두 확인 가능하도록 구성.
  - Preview용 데이터(`@PreviewParameter` 또는 Sample Data) 분리.

- **Components**:
  - 재사용 가능한 컴포넌트는 별도 파일로 분리.
  - 모든 Composable 함수의 첫 번째 선택적 파라미터는 `modifier: Modifier = Modifier`여야 함.

- **Resources**:
  - 하드코딩된 문자열, 색상, 치수 금지. (`strings.xml`, `Theme` 사용)

## 4. Testing Strategy

- **Target**:
  - ViewModel (Business Logic), Repository (Data Mapping), UseCase.

- **Unit Test**:
  - ViewModel, Repository, UseCase, Utility Class 대상.
  - JUnit5, Mockk 사용.
  - ViewModel 로직 검증 필수.

- **Android Test (UI Test)**:
  - 핵심 UI Flow 검증.
  - Compose Test Rule 사용.

- **Tools**: JUnit5, Mockk, Turbine (Flow 테스트용).

## 5. Coding Conventions

- **Naming**:
  - Class/Interface/Composable: `PascalCase` (e.g., `LoginScreen`)
  - Function/Variable: `camelCase` (e.g., `fetchUserData`)
  - Constants: `SCREAMING_SNAKE_CASE` (e.g., `MAX_RETRY_COUNT`)
  - Resource IDs: `snake_case` (e.g., `ic_home_icon`, `text_error_message`)
  - ViewModel의 StateFlow는 `_uiState` (private mutable) / `uiState` (public immutable) 패턴 준수.

- **Safety**:
  - `!!` 연산자 사용 금지 (항상 Safe Call `?.` 또는 Elvis Operator `?:` 사용).
  - `lateinit var` 사용 지양 (가능하면 nullable + safe call 사용).

## 6. Git & Commits

- 의미 있는 단위로 분리하여 커밋.
- 커밋 메시지 컨벤션 준수 (e.g., `feat:`, `fix:`, `refactor:`, `chore:`).

## 7. Documentation & Research (Context7)

- **Library Usage**: 새로운 서드파티 라이브러리나 프레임워크 기능을 사용할 때는 반드시 **Context7 MCP 도구**를 사용하여 최신 공식 문서와 구현 예제를 확인한 후 코드를 작성한다. (학습된 지식보다 공식 문서 우선)
- **Deprecation Check**: 특정 API가 최신 버전에서 Deprecated 되었는지 불확실할 경우 적극적으로 문서를 조회한다.
