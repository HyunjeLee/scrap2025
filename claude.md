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

## 프로젝트 구조 (Layer-based Clean Architecture)

```
scrap2025/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/scrap2025/scrap2025/
│   │   │   │   ├── MainActivity.kt                    # 메인 액티비티
│   │   │   │   ├── ui/                               # UI 계층
│   │   │   │   │   ├── screens/                      # 화면 컴포저블들 (LoginScreen.kt, etc)
│   │   │   │   │   ├── components/                   # 재사용 가능한 UI 컴포넌트
│   │   │   │   │   │   ├── buttons/                  # 버튼 컴포넌트
│   │   │   │   │   │   └── cards/                    # 카드 컴포넌트
│   │   │   │   │   └── theme/
│   │   │   │   │       ├── Color.kt                  # 색상 정의
│   │   │   │   │       ├── Theme.kt                  # 테마 정의
│   │   │   │   │       └── Type.kt                   # 타이포그래피 정의
│   │   │   │   ├── viewmodel/                        # ViewModel 클래스들
│   │   │   │   │   ├── LoginViewModel.kt
│   │   │   │   │   ├── ScrapViewModel.kt
│   │   │   │   │   └── ...
│   │   │   │   ├── model/                            # Data 모델
│   │   │   │   │   ├── dto/                          # Data Transfer Objects
│   │   │   │   │   └── entity/                       # Entity 클래스
│   │   │   │   ├── repository/                       # 데이터 접근 계층 (추상화)
│   │   │   │   │   ├── LoginRepository.kt
│   │   │   │   │   └── ScrapRepository.kt
│   │   │   │   ├── data/                             # 데이터 소스 (구현)
│   │   │   │   │   ├── local/                        # 로컬 데이터 (Room DB, SharedPref)
│   │   │   │   │   └── remote/                       # 원격 데이터 (API, Retrofit)
│   │   │   │   └── navigation/                       # Navigation 설정 (NavGraph.kt)
│   │   │   ├── res/                                  # 리소스 파일
│   │   │   └── AndroidManifest.xml
│   │   ├── test/                                     # 단위 테스트
│   │   └── androidTest/                              # UI 테스트
│   └── build.gradle.kts
├── build.gradle.kts                                  # 프로젝트 레벨 빌드 파일
├── settings.gradle.kts                               # 프로젝트 설정
├── gradle/                                           # Gradle 버전 카탈로그
└── claude.md                                         # 이 파일
```

## MVVM 아키텍처 + Layer-based 구조

### 계층 설명

#### 1. UI 계층 (`ui/`)
**책임**: 사용자 인터페이스 표시

- **`screens/`**: 전체 화면을 구성하는 Composable 함수
  - 예: `LoginScreen.kt`, `ScrapScreen.kt`
  - ViewModel과 상호작용
  - 여러 컴포넌트를 조합하여 전체 화면 구성

- **`components/`**: 재사용 가능한 UI 컴포넌트
  - `buttons/`: 버튼 컴포넌트들
  - `cards/`: 카드 컴포넌트들
  - 다른 화면에서도 사용 가능

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
**책임**: 데이터 구조 정의

- **`dto/`** (Data Transfer Object)
  - API 응답 데이터 구조
  - 네트워크 통신용 모델

- **`entity/`**
  - 로컬 DB 엔티티
  - 비즈니스 로직에 사용되는 데이터 모델

#### 4. Repository 계층 (`repository/`)
**책임**: 데이터 접근의 추상화

- 여러 데이터 소스(로컬, 원격)를 통합
- ViewModel에 단일 인터페이스 제공
- 예: `LoginRepository.kt`, `ScrapRepository.kt`

```kotlin
interface LoginRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun logout(): Result<Unit>
}

class LoginRepositoryImpl(
    private val remoteDataSource: LoginRemoteDataSource,
    private val localDataSource: LoginLocalDataSource
) : LoginRepository {
    override suspend fun login(email: String, password: String) =
        remoteDataSource.login(email, password)
}
```

#### 5. Data 계층 (`data/`)
**책임**: 실제 데이터 접근 구현

- **`local/`**: 로컬 데이터 소스
  - Room Database
  - SharedPreferences
  - 로컬 캐싱

- **`remote/`**: 원격 데이터 소스
  - Retrofit API 호출
  - HTTP 통신

#### 6. Navigation 계층 (`navigation/`)
**책임**: 화면 간 네비게이션 관리

- `NavGraph.kt`: Compose Navigation 정의
- 라우팅 경로 설정
- Deep Link 처리

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

### MVVM 패턴 구현 예시

**ViewModel**
```kotlin
class MyViewModel(private val repository: MyRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = try {
                UiState.Success(repository.getData())
            } catch (e: Exception) {
                UiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

sealed class UiState {
    object Loading : UiState()
    data class Success(val data: Data) : UiState()
    data class Error(val message: String) : UiState()
}
```

**View (Composable)**
```kotlin
@Composable
fun MyScreen(viewModel: MyViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    when (uiState) {
        is UiState.Loading -> LoadingIndicator()
        is UiState.Success -> Content(data = (uiState as UiState.Success).data)
        is UiState.Error -> ErrorMessage(message = (uiState as UiState.Error).message)
    }
}

@Preview(showBackground = true)
@Composable
fun MyScreenPreview() {
    Scrap2025Theme {
        MyScreen()
    }
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

## 코드 생성 체크리스트

모든 코드 생성 작업 시 다음을 반드시 따릅니다:

1. **Context7 라이브러리 검증** - 사용할 API의 최신 버전 확인
2. **Import 정리** - Unused import 제거, 정렬 순서 준수
3. **Preview 함수** - 모든 Composable에 @Preview 작성
4. **빌드 검증** - `./gradlew assembleDebug`로 컴파일 확인
5. **디자인 정확성** - Figma 스펙과 정확히 일치
6. **실제 동작** - 모킹 없이 실제 동작하는 코드만 작성

## 주의사항

- **Compose 호환성**: Material3, Compose UI, Lifecycle 버전 일치
- **라이프사이클**: `viewModelScope` 사용으로 메모리 누수 방지
- **상태 관리**: `StateFlow` 또는 `LiveData` 일관성 유지
- **실제 동작**: 모킹 없이 실제 동작하는 코드만 작성
- **Recomposition 고려**: 상태 범위 최소화, 람다 안정화, LazyList 사용

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
