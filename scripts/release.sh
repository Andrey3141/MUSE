#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

# Проверки
command -v gh >/dev/null 2>&1 || { echo "ERROR: GitHub CLI (gh) не установлен."; exit 1; }
gh auth status >/dev/null 2>&1 || { echo "ERROR: gh не авторизован. Выполните: gh auth login"; exit 1; }
[[ -f "./gradlew" ]] || { echo "ERROR: ./gradlew не найден."; exit 1; }
[[ -f "./CHANGELOG.md" ]] || { echo "ERROR: CHANGELOG.md не найден."; exit 1; }

# Версия из Gradle
VERSION_NAME="$(grep -m1 'versionName' app/build.gradle.kts | sed -E 's/.*versionName = "([^"]+)".*/\1/')"
VERSION_CODE="$(grep -m1 'versionCode' app/build.gradle.kts | sed -E 's/.*versionCode = ([0-9]+).*/\1/')"
[[ -n "$VERSION_NAME" ]] || { echo "ERROR: Не удалось извлечь versionName."; exit 1; }
echo "Версия: $VERSION_NAME ($VERSION_CODE)"

# Запись в CHANGELOG
if ! grep -q "## \[$VERSION_NAME\]" CHANGELOG.md; then
  echo "ERROR: В CHANGELOG.md нет записи для версии $VERSION_NAME"
  exit 1
fi

DATE_LINE="$(grep -A1 "## \[$VERSION_NAME\]" CHANGELOG.md | grep -oE '[0-9]{4}-[0-9]{2}-[0-9]{2}' | head -1)"
[[ -n "$DATE_LINE" ]] || { echo "ERROR: Не удалось извлечь дату для версии $VERSION_NAME"; exit 1; }
echo "Дата: $DATE_LINE"

# Release Notes: от заголовка версии до следующего заголовка версии
NOTES="$(awk -v ver="$VERSION_NAME" '
  $0 ~ "^## \\[" ver "\\]" { flag=1; next }
  flag && $0 ~ "^## \\[" { exit }
  flag { print }
' CHANGELOG.md)"
[[ -n "$NOTES" ]] || { echo "ERROR: Пустые Release Notes."; exit 1; }

# Сборка
echo "Сборка release APK..."
./gradlew :app:assembleRelease

APK_PATH="app/build/outputs/apk/release/app-release.apk"
[[ -f "$APK_PATH" ]] || { echo "ERROR: APK не найден: $APK_PATH"; exit 1; }

# Проверка тега
TAG="v$VERSION_NAME"
if git rev-parse "$TAG" >/dev/null 2>&1; then
  echo "ERROR: Тег $TAG уже существует."; exit 1
fi
if gh release view "$TAG" >/dev/null 2>&1; then
  echo "ERROR: Релиз $TAG уже существует."; exit 1
fi

# Публикация
echo "Публикация GitHub Release $TAG..."
gh release create "$TAG" "$APK_PATH" \
  --title "MUSE $TAG" \
  --notes "$NOTES"

echo "Готово: $TAG"
