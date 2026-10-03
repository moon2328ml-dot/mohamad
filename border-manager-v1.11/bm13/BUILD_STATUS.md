# Build status

The project source and assets are complete in this package.

Local validation performed in the generation environment:
- Jalali conversion utility compiled and tested for 1405/07/05 ↔ 2026-09-27.
- Leap-year behavior checked for 1402–1405.
- Kotlin sources were parser-checked; no syntax-style errors were detected before Android dependency resolution.
- Launcher icon resources exist for mdpi/hdpi/xhdpi/xxhdpi/xxxhdpi.
- AndroidManifest, FileProvider, backup rules and database schema are included.

A final Android APK could not be compiled in the generation container because that container does not contain the Android SDK/Maven dependency cache and has no direct package-network access. A GitHub Actions workflow is included at `.github/workflows/android-build.yml`; on a normal Android Studio machine or GitHub runner it installs SDK 37 and builds the debug APK.
