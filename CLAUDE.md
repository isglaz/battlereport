# BattleReport

## Project rules

### 1. English is the language of the codebase

Everything written *about* the code is in English, with no exceptions:

* **Comments** - line, block and KDoc.
* **Documentation** - `README.md`, `migrations/README.md`, and any new docs.
* **Commit messages** - both subject and body.
* **Pull request titles and descriptions.**
* **Identifiers** - names of classes, functions, variables, SQL objects, Gradle tasks.
* **Developer-facing strings** - log messages, exception texts, `check`/`require`
  messages, CLI output of the scripts in `deploy/`.

The single exception is **user-facing product content**: the `RU` dictionary in
`site/src/jsMain/kotlin/org/isglaz/battlereport/i18n/Strings.kt` and the language
label `RU("ru", "Русский")`. Those are translations shown in the UI - they stay
Russian, and every new interface string is added to **both** `EN` and `RU`.
