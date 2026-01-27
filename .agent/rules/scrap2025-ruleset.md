---
trigger: always_on
---

# Antigravity Rules - scrap2025

## 0. Interaction Protocol (CRITICAL - DO NOT VIOLATE)

### 0.1 Propose First Strategy
- For tasks involving code modification, file creation/deletion/movement, you **MUST first propose a plan or a Diff**, and **execute tools (`write_to_file`, `run_command`, etc.) only after receiving the user's explicit approval** ("Good", "Proceed", "Fix it").
- **No Blanket Approval**: Just because the user says "Fix the error" does not give you permission to arbitrarily delete or move files. You must first propose *how* you will fix it.

### 0.2 Internal Self-Correction Protocol (Tool Safety Check)
Before calling a tool, perform the following **Mental Check**:
1.  **"Has the user approved this specific file change (deletion/creation)?"**
    - YES -> Proceed
    - NO / Unsure -> Stop and propose first.
2.  **Separate Diagnosis and Action**:
    - In error situations, interpret the cause using only `read` tools.
    - Once the cause is identified, do not immediately fix (`write`) it. Instead, report: "The cause is ~. I will fix it by ~."

### 0.3 Exceptions
- If the user explicitly **orders specific changes** (e.g., "Change the NavHost package name to lowercase"), you may execute it immediately.
- **Non-destructive (Read-only)** tasks such as simple code queries or log checks can be performed without approval.

## 1. Environment & Tools

- **OS**: macOS

- **IDE**: Android Studio

- **Language**: Kotlin 100%

- **Linter**: Adhere to ktlint

- **Configuration**:
  - Use `libs.versions.toml` (Version Catalog) to centrally manage dependency versions.

- **Formatting**:
  - **Mandatory Formatting**: You **MUST run `./gradlew ktlintFormat`** after any code modification to automatically handle indentation, newlines, and import optimization. Do not rely on manual formatting.

## 2. Architecture & Patterns (Clean Architecture + MVVM)

- **Architecture**:
  - **Presentation Layer**: ViewModel + Compose UI
  - **Domain Layer**: UseCases (Optional) + Repository Interfaces
  - **Data Layer**: Repository Impl + Data Sources (Local/Remote)

- **Dependency Injection**: Use Hilt

- **Concurrency**: Use Coroutines & Flow
  - **Dispatcher Injection**: Do not call `Dispatchers.IO` etc. directly; inject them via DI (Ensure Testability).

- **Error Handling**:
  - The Repository layer does not throw Exceptions but returns a `Result<T>` wrapper.

- **Navigation**: Use Jetpack Navigation Compose (Type-safe navigation recommended)

## 3. UI Guidelines (Jetpack Compose)

- **Performance (Critical)**:
  - **Stability Annotation**: Actively attach **`@Immutable`** or **`@Stable`** annotations to UI State classes or frequently used data holder classes to prevent unnecessary recomposition.
  - **`key` parameter is mandatory** when using `LazyColumn`/`LazyRow`.
  - UI State classes should only use `val` properties.
  - Use `remember { }` or `derivedStateOf { }` for heavy computations.

- **Lifecycle Safety**:
  - Use `collectAsStateWithLifecycle()` when collecting Flows (Lifecycle-aware).

- **State Hoisting**:
  - Adhere to the `Route/Screen` (Stateful) vs `Content` (Stateless) separation pattern.

- **Previews**:
  - Writing `@Preview` is mandatory for all UI components.
  - Configure to verify both Light Mode and Dark Mode.
  - Separate data for Previews (`@PreviewParameter` or Sample Data).

- **Components**:
  - Separate reusable components into a separate file.
  - The first optional parameter of every Composable function must be `modifier: Modifier = Modifier`.

- **Resources**:
  - No hardcoded strings, colors, or dimensions. (Use `strings.xml`, `Theme`)

## 4. Testing Strategy

- **Target**:
  - ViewModel (Business Logic), Repository (Data Mapping), UseCase.

- **Unit Test**:
  - Target: ViewModel, Repository, UseCase, Utility Class.
  - Use JUnit5, Mockk.
  - ViewModel logic verification is mandatory.

- **Android Test (UI Test)**:
  - Verify core UI Flows.
  - Use Compose Test Rule.

- **Tools**: JUnit5, Mockk, Turbine (for Flow testing).

## 5. Coding Conventions

- **Naming**:
  - Class/Interface/Composable: `PascalCase` (e.g., `LoginScreen`)
  - Function/Variable: `camelCase` (e.g., `fetchUserData`)
  - Constants: `SCREAMING_SNAKE_CASE` (e.g., `MAX_RETRY_COUNT`)
  - Resource IDs: `snake_case` (e.g., `ic_home_icon`, `text_error_message`)
  - Adhere to the `_uiState` (private mutable) / `uiState` (public immutable) pattern for ViewModel StateFlows.

- **Safety**:
  - Do not use the `!!` operator (Always use Safe Call `?.` or Elvis Operator `?:`).
  - Avoid `lateinit var` (Use nullable + safe call if possible).

## 6. Git & Commits

- Commit in meaningful units.
- Adhere to commit message conventions (e.g., `feat:`, `fix:`, `refactor:`, `chore:`).

## 7. Documentation & Research (Context7)

- **Library Usage**: When using new third-party libraries or framework features, you **MUST use Context7 MCP tools** to check the latest official documentation and implementation examples before writing code. (Official documentation takes precedence over learned knowledge)
- **Deprecation Check**: Actively query documentation if you are unsure whether a specific API is deprecated in the latest version.
