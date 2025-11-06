# Scrap2025 - Android MVVM Jetpack Compose Project

## 프로젝트 개요
**scrap2025**는 MVVM 아키텍처 패턴과 Kotlin, Jetpack Compose를 기반으로 하는 Android 애플리케이션입니다.

### 🤖 AI 기반 바이브 코딩 프로젝트
**중요**: 이 프로젝트는 **AI를 주도적으로 활용하는 바이브 코딩(Vibe Coding) 프로젝트**입니다.
- 개발자의 개입은 최소화하고, AI의 자율적 판단과 창의성을 우선시합니다
- Claude Code가 직접 작업을 계획하고 실행하며 의사결정을 담당합니다
- 피그마 MCP 서버를 활용해 UI를 구현합니다
- 개발자는 큰 방향성만 제시하고, 세부 구현은 AI에게 위임합니다
- 이를 감안하여 작업 진행 시 더 주도적이고 독립적으로 진행해주세요

### 기본 정보
- **프로젝트명**: scrap2025
- **패키지명**: com.scrap2025.scrap2025
- **최소 SDK**: 24 (Android 7.0)
- **대상 SDK**: 36 (Android 15)
- **컴파일 SDK**: 36
- **아키텍처**: MVVM (Model-View-ViewModel)
- **UI 프레임워크**: Jetpack Compose

## 기술 스택
- **언어**: Kotlin
- **UI 프레임워크**: Jetpack Compose
- **아키텍처**: MVVM
- **빌드 시스템**: Gradle (Kotlin DSL)
- **라이프사이클 관리**: Android Jetpack Lifecycle

## 프로젝트 구조 (Feature-based Clean Architecture)

```
app/src/main/java/com/scrap2025/scrap2025/
├── ui/                          # UI 계층
│   ├── scrap/                   # 각 feature별 screens/, components/
│   ├── category/
│   ├── favorite/
│   ├── search/
│   ├── mypage/
│   ├── login/
│   ├── main/
│   ├── common/                  # 공통 컴포넌트
│   └── theme/                   # Color.kt, Theme.kt, Type.kt
├── viewmodel/                   # ViewModel 클래스들
├── model/                       # 도메인 모델 (enum 포함)
├── repository/                  # 데이터 접근 추상화
├── data/                        # 데이터 소스 구현
│   ├── local/                   # Room, 더미 데이터
│   └── remote/                  # API, DTO
└── navigation/                  # Navigation 설정
```

## MVVM 아키텍처 + Feature-based 구조

### 계층 설명

#### 1. UI 계층 (`ui/`) - Feature-based 구조
**책임**: 사용자 인터페이스 표시

이 프로젝트는 **기능 기반(Feature-based)** 구조를 채택하여, 관련 화면과 컴포넌트를 기능별로 그룹화합니다.

**Feature 폴더 구조:**
- **`{feature}/screens/`**: 해당 기능의 모든 화면
  - 예: `category/screens/CategoryScreen.kt`, `category/screens/AddCategoryScreen.kt`
  - 관련된 모든 화면이 한 곳에 모여 있어 유지보수 용이

- **`{feature}/components/`**: 해당 기능 전용 컴포넌트
  - 예: `scrap/components/ScrapItemCard.kt`
  - Feature-specific 컴포넌트만 포함

**주요 Feature 목록:**
- `scrap/`: 스크랩 관련 화면 및 컴포넌트
- `category/`: 카테고리 관련 화면 및 컴포넌트
- `favorite/`: 즐겨찾기 관련 화면 및 컴포넌트
- `search/`: 검색 관련 화면 및 컴포넌트
- `mypage/`: 마이페이지 관련 화면 및 컴포넌트
- `login/`: 로그인 관련 화면 및 컴포넌트
- `main/`: 메인 쉘 (MainScreen, BottomNavigationBar)

**공통 요소:**
- **`common/`**: 여러 기능에서 공유되는 컴포넌트
  - `buttons/`: 공통 버튼 컴포넌트들
  - `cards/`: 공통 카드 컴포넌트들

- **`theme/`**: 디자인 시스템
  - `Color.kt`: 앱 전체 색상 팔레트
  - `Theme.kt`: Material Design 3 테마
  - `Type.kt`: 타이포그래피 정의

#### 2. ViewModel 계층 (`viewmodel/`)
**책임**: UI 상태 관리 및 비즈니스 로직

- `androidx.lifecycle.ViewModel` 상속
- `StateFlow` 또는 `LiveData`로 UI 상태 노출
- View(Composable)에 의존하지 않음
- Repository를 통해 데이터 접근
- 예: `LoginViewModel.kt`, `ScrapViewModel.kt`

#### 3. Model 계층 (`model/`)
**책임**: 도메인 모델 및 UI 상태 모델 정의
- 도메인 모델: `CategoryItem.kt`, `ScrapItem.kt`
- UI 상태 enum: `ViewMode.kt` 등

#### 4. Repository/Data/Navigation 계층
- **Repository**: 데이터 접근 추상화 (인터페이스)
- **Data**: 실제 구현 (local/remote)
- **Navigation**: 화면 간 네비게이션

### 계층 간 데이터 흐름

```
UI (Screens/Components)
    ↓ (상태 구독, 콜백 호출)
ViewModel
    ↓ (데이터 요청)
Repository (인터페이스)
    ↓ (구현)
Data Layer (Local/Remote)
    ↓ (데이터 반환)
ViewModel (상태 업데이트)
    ↓ (상태 변경)
UI (리컴포지션)
```

### 아키텍처 원칙 및 Best Practices

#### 타입 및 Enum 배치 원칙

**Enum 위치 결정 기준:**

| 타입 | 위치 | 예시 |
|------|------|------|
| **UI 상태 모델** | `model/` | `ViewMode.kt` (LIST, GRID) |
| **도메인 모델** | `model/` | `ScrapItem.kt`, `CategoryItem.kt` |
| **여러 feature 공유 enum** | `model/` | `SortOrder.kt`, `FilterType.kt` |
| **공통 UI enum** | `ui/common/` | 필요 시 (현재 미사용) |
| **Feature 전용 enum** | 해당 feature 내부 | 특수한 경우만 |

**원칙:**
- ✅ **재사용 가능성 우선**: 여러 feature에서 사용 가능하면 `model/`
- ✅ **일관성 유지**: 기존 도메인 모델과 같은 위치
- ❌ **화면 파일에 enum 정의 금지**: 재사용 불가, 아키텍처 위반

**예시:**
```kotlin
// ✅ 올바른 위치: model/ViewMode.kt
package com.scrap2025.scrap2025.model

enum class ViewMode {
    LIST,
    GRID
}

// ❌ 잘못된 위치: ui/scrap/screens/ScrapScreen.kt 내부
enum class ViewMode { ... }  // 재사용 불가!
```

#### 상태 관리 원칙

**핵심 규칙: 모든 비즈니스 상태는 ViewModel에서 관리**

| 구분 | ViewModel에서 관리 ✅ | View에서 관리 ⚠️ |
|------|----------------------|-----------------|
| **비즈니스 상태** | ViewMode, 데이터 목록, 필터 | 절대 금지 |
| **UI 임시 상태** | - | 다이얼로그 표시, 애니메이션 |
| **도구** | StateFlow, LiveData | remember { mutableStateOf() } |

**ViewModel에서 관리해야 하는 것:**
- ✅ UI 상태 (viewMode, selectedTab, sortOrder 등)
- ✅ 비즈니스 데이터 (스크랩 목록, 카테고리 등)
- ✅ 비즈니스 로직 결과 (검색 결과, 필터링 등)
- ✅ 화면 회전 시 유지되어야 할 상태

**View에서만 관리 가능한 것:**
- ⚠️ 순수 UI 임시 상태 (다이얼로그 열림/닫힘, 애니메이션 진행)
- ⚠️ Focus 상태, Scroll 상태 등 Composition 수명주기와 밀접

**잘못된 예시 vs 올바른 예시:**

```kotlin
// ❌ 잘못된 예시: View에서 비즈니스 상태 관리
@Composable
fun ScrapScreen() {
    // 문제: viewMode는 비즈니스 상태인데 View에서 관리
    var viewMode by remember { mutableStateOf(ViewMode.LIST) }

    SortBar(
        viewMode = viewMode,
        onViewModeToggle = {
            viewMode = if (viewMode == ViewMode.LIST) ViewMode.GRID else ViewMode.LIST
        }
    )
    // 문제: 화면 회전 시 상태 손실, 테스트 어려움
}

// ✅ 올바른 예시: ViewModel에서 상태 관리
class ScrapViewModel : ViewModel() {
    private val _viewMode = MutableStateFlow(ViewMode.LIST)
    val viewMode: StateFlow<ViewMode> = _viewMode.asStateFlow()

    fun toggleViewMode() {
        _viewMode.value = if (_viewMode.value == ViewMode.LIST) {
            ViewMode.GRID
        } else {
            ViewMode.LIST
        }
    }
}

@Composable
fun ScrapScreen(viewModel: ScrapViewModel = viewModel()) {
    // View는 상태를 구독만 함
    val viewMode by viewModel.viewMode.collectAsState()

    SortBar(
        viewMode = viewMode,
        onViewModeToggle = { viewModel.toggleViewMode() }  // 이벤트만 전달
    )
    // 장점: 화면 회전 시 상태 유지, 테스트 용이, 아키텍처 준수
}
```

#### 구현 전 체크리스트

**코드 작성 전 필수 확인사항:**

```
[ ] 1. 어느 계층에 속하는가?
    - Model: 도메인 모델, enum, 상수
    - ViewModel: 상태 관리, 비즈니스 로직
    - View: UI 표현, 상태 구독, 이벤트 전달

[ ] 2. 상태 관리가 필요한가?
    - YES → ViewModel에서 StateFlow 사용
    - NO → 단순 파라미터 전달

[ ] 3. 재사용 가능한 타입인가?
    - YES → model/ 패키지에 별도 파일
    - NO → 사용처 근처 정의 (최소화)

[ ] 4. MVVM 원칙을 위배하지 않는가?
    - View가 비즈니스 상태 관리? → ❌
    - ViewModel이 View 참조? → ❌
    - 상태가 화면 회전 시 손실? → ❌

[ ] 5. "작동"보다 "올바른 아키텍처" 우선
    - 빠른 구현 < 유지보수 가능한 구조
    - 임시방편 < 확장 가능한 설계
```

**구현 순서:**
1. 타입/Enum 필요 → `model/` 패키지에 생성
2. 상태 관리 필요 → `ViewModel` 생성 및 StateFlow 정의
3. UI 구현 → `Screen` Composable에서 상태 구독
4. 이벤트 처리 → ViewModel 함수 호출로 처리

#### 일반적인 안티패턴 및 해결책

**안티패턴 1: View에서 상태 관리**
```kotlin
// ❌ 안티패턴
@Composable
fun MyScreen() {
    var state by remember { mutableStateOf(초기값) }
    // ...
}

// ✅ 해결책
class MyViewModel : ViewModel() {
    private val _state = MutableStateFlow(초기값)
    val state = _state.asStateFlow()
}
```

**안티패턴 2: 화면 파일에 enum 정의**
```kotlin
// ❌ 안티패턴: ui/screens/MyScreen.kt
enum class ViewMode { ... }
@Composable fun MyScreen() { ... }

// ✅ 해결책: model/ViewMode.kt
enum class ViewMode { LIST, GRID }
```

**안티패턴 3: 계층 간 의존성 역전**
```kotlin
// ❌ 안티패턴: ViewModel이 UI 참조
class MyViewModel(private val screen: MyScreen) { ... }

// ✅ 해결책: 콜백 또는 이벤트 사용
class MyViewModel {
    fun onEvent(event: UiEvent) { ... }
}
```


## 코딩 컨벤션

### 네이밍 규칙
- **클래스**: PascalCase (예: `MainActivity`, `LoginViewModel`)
- **함수/변수**: camelCase (예: `loadData()`, `userName`)
- **상수**: CONSTANT_CASE
- **ViewModel**: `{Screen}ViewModel` (예: `HomeViewModel`)
- **State**: `{Screen}UiState` (예: `HomeUiState`)

### Composable 함수 규칙
```kotlin
@Composable
fun MyComponent(
    modifier: Modifier = Modifier,
    title: String = "Default",
    onClickButton: () -> Unit = {}
) {
    // UI 구현
}
```
- 파라미터 순서: Modifier → 데이터 → 콜백

## 개발 가이드

### Context7 라이브러리 검증
- 모든 코드 생성 전에 `mcp__context7__resolve-library-id`로 라이브러리 ID 찾기
- `mcp__context7__get-library-docs`로 최신 API 확인
- deprecated API 사용 금지

### Import 정렬 순서
1. `android.*`, `androidx.*`
2. `androidx.compose.*`
3. `androidx.compose.material3.*`
4. 기타 라이브러리
5. 프로젝트 내부 (`com.scrap2025.*`)

## 빌드

빌드 명령어:
```bash
./gradlew assembleDebug --no-daemon
```
빌드 전에는 clean

BUILD SUCCESSFUL이면 성공, BUILD FAILED면 오류 확인.


## 주의사항

- **Compose 호환성**: Material3, Compose UI, Lifecycle 버전 일치
- **라이프사이클**: `viewModelScope` 사용으로 메모리 누수 방지
- **상태 관리**: `StateFlow` 또는 `LiveData` 일관성 유지
- **실제 동작**: 모킹 없이 실제 동작하는 코드만 작성
- **Recomposition 고려**: 상태 범위 최소화, 람다 안정화, LazyList 사용
- **색상 관리**: 하드코딩된 색상값(Color(0xFF...)) 사용 금지, `Color.kt`에 정의된 색상 상수 사용 필수

## 작업 완료 시 보고 형식

모든 작업 완료 후 다음 형식으로 보고합니다:

### ✅ 작업 완료

**수정된 파일:**
- `app/src/main/java/.../FileName1.kt` - 수정 내용 요약
- `app/src/main/java/.../FileName2.kt` - 수정 내용 요약

**주요 변경사항:**
- 변경사항 1
- 변경사항 2

**빌드 상태:** ✅ BUILD SUCCESSFUL

## 향후 개선 계획

- [ ] 데이터 계층 구현 (Repository, DataSource)
- [ ] 네트워킹 라이브러리 통합 (Retrofit, OkHttp)
- [ ] 로컬 데이터베이스 (Room)
- [ ] 의존성 주입 (Hilt)
- [ ] 이미지 로딩 라이브러리 (Coil)
- [ ] 로깅 및 분석
- [ ] 유닛 테스트 및 UI 테스트 확대
- [ ] 각 화면의 실제 기능 구현
- [ ] 화면 간 데이터 전달 로직
