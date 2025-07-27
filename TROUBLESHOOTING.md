# 🔧 Решение проблем сборки

## ❌ Ошибка: Duplicate class org.intellij.lang.annotations

**Проблема:** Конфликт между различными версиями аннотаций JetBrains/IntelliJ.

### ✅ **РЕШЕНИЕ ПРИМЕНЕНО В ПРОЕКТЕ:**

В файле `app/build.gradle` добавлено:

```gradle
// Exclude conflicting annotations globally
configurations.all {
    resolutionStrategy {
        force 'org.jetbrains:annotations:23.0.0'
    }
    exclude group: 'com.intellij', module: 'annotations'
}

dependencies {
    // ... другие зависимости ...
    
    // Use only the newer JetBrains annotations
    implementation 'org.jetbrains:annotations:23.0.0'
}
```

### 🚀 **Если ошибка все еще возникает:**

1. **В Android Studio:**
   - `Build` → `Clean Project`
   - `Build` → `Rebuild Project`

2. **Из командной строки:**
   ```bash
   ./gradlew clean
   ./gradlew build
   ```

3. **Очистить кэш Gradle:**
   ```bash
   ./gradlew --stop
   rm -rf ~/.gradle/caches/
   ./gradlew build
   ```

### 🎯 **Альтернативное решение:**

Если проблема остается, добавьте в `app/build.gradle`:

```gradle
android {
    packagingOptions {
        pickFirst '**/annotations-*.jar'
    }
}
```

## ✨ **Результат:**
Приложение должно собираться без ошибок дублирования классов аннотаций!

---
*Приложение "Напоминалочка" готово к использованию! 💕*