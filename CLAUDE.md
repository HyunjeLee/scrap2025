# CLAUDE.md

## 코드 스타일 (ktlint)

- ktlint는 Gradle 플러그인(`org.jlleitschuh.gradle.ktlint`)으로 적용되어 있고, CI(`android_ci_debug.yml`)에서 `./gradlew ktlintCheck`로 검사한다.
- 로컬 pre-commit 훅(`.git/hooks/pre-commit`)은 git으로 공유되지 않으므로 클라우드/다른 환경에서는 동작하지 않는다.
  **Kotlin 파일을 수정했다면 커밋 전에 반드시 아래를 실행해 통과시킨다.**
  - `./gradlew ktlintFormat` — 자동 수정 가능한 위반 수정
  - `./gradlew ktlintCheck` — 남은 위반 확인 (실패 시 직접 수정)
- 규칙은 `.editorconfig`를 따른다. (`android_studio` 코드 스타일)
  - 와일드카드 import(`import foo.*`) 금지 — 자동 수정이 안 되므로 처음부터 명시적으로 import한다.
  - Composable 함수는 PascalCase 허용, trailing comma 규칙은 비활성화.
