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

## 브랜치 전략 (Trunk-based)

- 장기 브랜치는 `main` 하나다. `develop`은 사용하지 않는다.
- `main` ruleset: **PR 필수, squash/rebase merge만 허용, linear history, force push 금지, `build-debug` 체크 필수**.

### 기능 개발
- `main`에서 `feat/*`, `fix/*`, `refactor/*` 등을 만들고, 브랜치 안에서는 자유롭게 커밋한다.
- `main`으로 PR → 기본은 **Squash and merge**.
  - 커밋 제목은 PR 제목 + `(#PR번호)`, 본문은 브랜치의 커밋 메시지 목록으로 자동 작성된다.
- 브랜치의 커밋들이 각각 의미 있는 단위(각 커밋이 빌드되는 상태)로 정리되어 있으면 **Rebase and merge**로 커밋을 그대로 남긴다.
  - rebase 머지 전에 WIP/오타 수정 커밋은 fixup으로 정리한다.
- 기능 브랜치는 짧게 유지한다. 여러 PR에 걸친 큰 기능은 Remote Config 등 feature flag로 꺼 둔 채 합친다.

### 출시 (release 브랜치 + cherry-pick)
1. `main`에 versionCode 업 PR을 머지한다. (`app/build.gradle.kts`, 커밋 메시지: `build: versionCode 업데이트 (13 -> 14)`)
2. 그 커밋에서 `release/v1.0.0-alpha.<versionCode>`를 만들어 push → QA용 prod 빌드가 Firebase App Distribution으로 배포된다.
3. QA 버그는 **반드시 `main`에 먼저 PR로 수정**하고, 머지된 커밋을 release 브랜치로 `git cherry-pick` 한다.
   release 브랜치에서 직접 수정하지 않는다.
4. QA 통과 후 release 브랜치 끝에 `v1.0.0-alpha.<versionCode>` 태그를 push → Google Play(internal) 업로드 + GitHub Release 생성.
5. release 브랜치는 `main`으로 다시 머지하지 않는다. (백머지 없음)

### 핫픽스
- `main`에서 수정 → 출시된 release 브랜치에 cherry-pick + versionCode 업 → 태그 push로 출시.
- `main`에도 versionCode 업 PR을 올려 다음 출시 번호가 겹치지 않게 한다.

### CI (`.github/workflows`)
| 트리거 | 동작 |
|---|---|
| PR → `main`, `release/*` | ktlint / lint / unit test / debug 빌드 (`build-debug`) |
| `main` push | 위 검사 + devRelease를 Firebase App Distribution으로 배포 |
| `release/*` push | prodRelease 빌드 + Firebase App Distribution 배포 (QA) |
| `v*` 태그 push | prodRelease 빌드 + Google Play internal 업로드 + GitHub Release |
