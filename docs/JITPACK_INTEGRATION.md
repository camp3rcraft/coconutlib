# Jitpack Integration

CoconutLib доступен через Jitpack для легкой интеграции в ваши проекты.

## Установка через Jitpack

### Gradle (Kotlin DSL)

Добавьте в ваш `build.gradle.kts`:

```kotlin
repositories {
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("com.github.camp3rcraft:CoconutLib:1.0.0")
}
```

### Gradle (Groovy)

Добавьте в ваш `build.gradle`:

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.camp3rcraft:CoconutLib:1.0.0'
}
```

### Maven

Добавьте в ваш `pom.xml`:

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

## Использование в плагине

После добавления зависимости, вы можете использовать CoconutLib в вашем плагине:

```java
public class MyPlugin extends JavaPlugin {
    
    private CoconutLib coconutLib;
    
    @Override
    public void onEnable() {
        // Получаем экземпляр CoconutLib
        coconutLib = (CoconutLib) getServer().getPluginManager().getPlugin("CoconutLib");
        
        if (coconutLib == null) {
            getLogger().severe("CoconutLib не найден! Убедитесь, что плагин установлен на сервере.");
            return;
        }
        
        // Теперь вы можете использовать API CoconutLib
        ArenaManager arenaManager = coconutLib.getArenaManager();
        arenaManager.createArena("my_arena", 10, 2);
    }
}
```

## Версии

- `1.0.0` - Текущая стабильная версия
- `main` - Последняя версия из главной ветки (может быть нестабильной)

## Требования

- Java 17+
- Paper 1.21.4+
- CoconutLib должен быть установлен на сервере

## Примечания

- CoconutLib использует in-memory хранилище, поэтому данные не сохраняются между перезапусками сервера
- Все зависимости уже включены в JAR файл (relocated)
- Библиотека совместима с Paper API 1.21.4 