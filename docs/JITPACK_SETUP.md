# CoconutLib - Настройка Jitpack

## Обзор

Jitpack - это сервис, который автоматически собирает и публикует Java библиотеки из GitHub репозиториев. Это позволяет легко интегрировать CoconutLib в ваши проекты без необходимости ручной сборки.

## Настройка репозитория

### 1. Структура репозитория

Убедитесь, что ваш репозиторий имеет правильную структуру:

```
CoconutLib/
├── build.gradle.kts          # Конфигурация Gradle
├── jitpack.yml               # Конфигурация Jitpack
├── src/
│   └── main/
│       ├── java/
│       │   └── com/camp3rcraft/coconutlib/
│       └── resources/
│           ├── plugin.yml
│           └── config.yml
├── README.md
└── docs/
    ├── COMPLETE_GUIDE.md
    ├── TECHNICAL_REFERENCE.md
    └── JITPACK_SETUP.md
```

### 2. Конфигурация Jitpack

Создайте файл `jitpack.yml` в корне репозитория:

```yaml
jdk:
  - openjdk17

before_install:
  - ./gradlew clean

install:
  - ./gradlew build

build:
  - ./gradlew shadowJar
```

### 3. Настройка Gradle

Убедитесь, что ваш `build.gradle.kts` правильно настроен:

```kotlin
plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.camp3rcraft"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.codemc.org/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.5")

    // MiniMessage для цветов
    implementation("net.kyori:adventure-text-minimessage:4.14.0")

    // Scoreboard API
    implementation("net.kyori:adventure-text-serializer-legacy:4.14.0")

    // JSON для конфигурации
    implementation("com.google.code.gson:gson:2.10.1")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks {
    shadowJar {
        archiveClassifier.set("")
        relocate("net.kyori", "com.camp3rcraft.coconutlib.libs.kyori")
        relocate("com.google.gson", "com.camp3rcraft.coconutlib.libs.gson")
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}
```

### 4. Настройка plugin.yml

Убедитесь, что `plugin.yml` содержит правильную информацию:

```yaml
name: CoconutLib
version: '${version}'
main: com.camp3rcraft.coconutlib.CoconutLib
api-version: '1.21'
author: camp3rcraft
description: Библиотека для удобного создания арен в Minecraft плагинах

commands:
  arena:
    description: Команды для управления аренами
    usage: /arena <create|delete|list|join|leave|info|regenerate> [параметры]
    permission: coconutlib.arena.admin
    permission-message: §cУ вас нет прав для использования этой команды!

permissions:
  coconutlib.arena.admin:
    description: Полный доступ к управлению аренами
    default: op
  coconutlib.arena.join:
    description: Возможность присоединяться к аренам
    default: true
  coconutlib.arena.leave:
    description: Возможность покидать арены
    default: true
```

## Публикация на Jitpack

### 1. Подготовка репозитория

1. Убедитесь, что все файлы закоммичены в Git
2. Создайте тег для версии:
   ```bash
   git tag -a v1.0.0 -m "Release version 1.0.0"
   git push origin v1.0.0
   ```

### 2. Подключение к Jitpack

1. Перейдите на [jitpack.io](https://jitpack.io)
2. Войдите через GitHub
3. Найдите ваш репозиторий в списке
4. Нажмите "Look up" для сборки

### 3. Мониторинг сборки

Jitpack покажет статус сборки:
- ✅ **Success** - Библиотека успешно собрана
- ❌ **Failed** - Ошибка сборки
- ⏳ **Building** - Сборка в процессе

### 4. Получение ссылки

После успешной сборки вы получите ссылку вида:
```
https://jitpack.io/com/github/camp3rcraft/CoconutLib/v1.0.0
```

## Использование в проектах

### Gradle (Kotlin DSL)

```kotlin
repositories {
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("com.github.camp3rcraft:CoconutLib:1.0.0")
}
```

### Gradle (Groovy)

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.camp3rcraft:CoconutLib:1.0.0'
}
```

### Maven

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.camp3rcraft</groupId>
    <artifactId>CoconutLib</artifactId>
    <version>1.0.0</version>
</dependency>
```

### SBT

```scala
resolvers += "jitpack" at "https://jitpack.io"

libraryDependencies += "com.github.camp3rcraft" % "CoconutLib" % "1.0.0"
```

## Версионирование

### Семантическое версионирование

Используйте семантическое версионирование (SemVer):

```
MAJOR.MINOR.PATCH
```

- **MAJOR** - Несовместимые изменения API
- **MINOR** - Новые функции, совместимые с предыдущими версиями
- **PATCH** - Исправления багов, совместимые с предыдущими версиями

### Теги Git

Создавайте теги для каждой версии:

```bash
# Создание тега
git tag -a v1.0.0 -m "Release version 1.0.0"

# Публикация тега
git push origin v1.0.0

# Создание тега для SNAPSHOT
git tag -a v1.0.1-SNAPSHOT -m "Snapshot version 1.0.1"
git push origin v1.0.1-SNAPSHOT
```

### Использование разных версий

```kotlin
// Стабильная версия
implementation("com.github.camp3rcraft:CoconutLib:1.0.0")

// SNAPSHOT версия
implementation("com.github.camp3rcraft:CoconutLib:1.0.1-SNAPSHOT")

// Версия из конкретного коммита
implementation("com.github.camp3rcraft:CoconutLib:abc1234")

// Версия из ветки
implementation("com.github.camp3rcraft:CoconutLib:main-SNAPSHOT")
```

## Устранение неполадок

### Проблемы сборки

#### Ошибка: "Could not resolve dependencies"

**Решение:**
1. Проверьте, что все зависимости доступны
2. Убедитесь, что репозитории правильно настроены
3. Проверьте версии зависимостей

```kotlin
repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.codemc.org/repository/maven-public/")
}
```

#### Ошибка: "Java version not supported"

**Решение:**
1. Убедитесь, что в `jitpack.yml` указана правильная версия Java
2. Проверьте, что в `build.gradle.kts` указана совместимая версия Java

```yaml
jdk:
  - openjdk17
```

```kotlin
java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}
```

#### Ошибка: "Shadow plugin not found"

**Решение:**
1. Проверьте, что shadow plugin правильно подключен
2. Убедитесь, что версия плагина актуальна

```kotlin
plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}
```

### Проблемы интеграции

#### Ошибка: "Could not find CoconutLib"

**Решение:**
1. Проверьте, что репозиторий Jitpack добавлен
2. Убедитесь, что версия библиотеки правильная
3. Попробуйте очистить кэш Gradle

```bash
./gradlew clean build --refresh-dependencies
```

#### Ошибка: "Class not found"

**Решение:**
1. Проверьте, что зависимости правильно relocated
2. Убедитесь, что shadowJar настроен правильно

```kotlin
tasks {
    shadowJar {
        archiveClassifier.set("")
        relocate("net.kyori", "com.camp3rcraft.coconutlib.libs.kyori")
        relocate("com.google.gson", "com.camp3rcraft.coconutlib.libs.gson")
    }
}
```

## Мониторинг и аналитика

### Jitpack Analytics

Jitpack предоставляет аналитику использования:

- **Downloads** - Количество загрузок
- **Builds** - Количество сборок
- **Errors** - Ошибки сборки
- **Performance** - Время сборки

### GitHub Insights

Отслеживайте активность репозитория:

- **Traffic** - Просмотры и клоны
- **Contributors** - Участники проекта
- **Issues** - Проблемы и предложения
- **Pull Requests** - Вклад сообщества

## Лучшие практики

### 1. Регулярные релизы

```bash
# Создание релиза
git checkout -b release/v1.0.1
# Внесите изменения
git commit -m "Prepare release 1.0.1"
git tag -a v1.0.1 -m "Release version 1.0.1"
git push origin v1.0.1
```

### 2. Документация

Поддерживайте актуальную документацию:

- README.md с примерами использования
- API документация
- Руководства по интеграции
- Примеры кода

### 3. Тестирование

```kotlin
// Добавьте тесты в ваш проект
dependencies {
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:4.5.1")
}

tasks.test {
    useJUnit()
}
```

### 4. CI/CD

Настройте автоматическую сборку:

```yaml
# .github/workflows/build.yml
name: Build and Test

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
    
    - name: Build with Gradle
      run: ./gradlew build
    
    - name: Test with Gradle
      run: ./gradlew test
```

## Заключение

Jitpack значительно упрощает распространение и использование CoconutLib. Следуя этим рекомендациям, вы сможете:

- ✅ Легко публиковать новые версии
- ✅ Предоставлять простую интеграцию для пользователей
- ✅ Отслеживать использование библиотеки
- ✅ Поддерживать качество кода

### Полезные ссылки

- [Jitpack Documentation](https://jitpack.io/docs/)
- [Gradle Shadow Plugin](https://github.com/johnrengelman/shadow)
- [Semantic Versioning](https://semver.org/)
- [GitHub Releases](https://docs.github.com/en/repositories/releasing-projects-on-github)

---

*CoconutLib - Удобное создание арен для Minecraft плагинов* 