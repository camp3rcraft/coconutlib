# CoconutLib

[![](https://jitpack.io/v/camp3rcraft/coconutlib.svg)](https://jitpack.io/#camp3rcraft/coconutlib)
Библиотека для удобного создания арен в Minecraft плагинах.

## 📖 Документация

- **[Полное руководство](docs/COMPLETE_GUIDE.md)** - Гипер подробная документация с объяснением всего и вся
- **[Техническая документация](docs/TECHNICAL_REFERENCE.md)** - Детальная техническая информация
- **[Примеры использования](docs/EXAMPLES.md)** - Практические примеры и кейсы
- **[Настройка Jitpack](docs/JITPACK_SETUP.md)** - Инструкции по интеграции через Jitpack

## 🚀 Возможности

- ✅ **Создание арен** - Каждая арена представляет собой отдельный мир
- ✅ **Управление игроками** - Присоединение, выход, телепортация
- ✅ **Система статусов** - Лобби, ожидание, обратный отсчет, игра, конец
- ✅ **Скорборды** - Динамические скорборды для каждого статуса
- ✅ **Табы** - Кастомные табы с информацией об арене
- ✅ **Управление инвентарями** - Сохранение и восстановление
- ✅ **Создание миров** - Void миры без рестарта сервера
- ✅ **Цвета** - Поддержка HEX и ванильных цветов
- ✅ **In-Memory хранилище** - Быстрая работа без внешних зависимостей

## 📦 Установка

### Через Jitpack (рекомендуется)

#### Gradle (Kotlin DSL)
```kotlin
repositories {
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    // Последняя версия из ветки main:
    implementation("com.github.camp3rcraft:CoconutLib:main-SNAPSHOT")
    // Или стабильный релиз (если появится тег v1.0.0):
    // implementation("com.github.camp3rcraft:CoconutLib:1.0.0")
}
```

#### Gradle (Groovy)
```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.camp3rcraft:CoconutLib:main-SNAPSHOT'
    // Или стабильный релиз:
    // implementation 'com.github.camp3rcraft:CoconutLib:1.0.0'
}
```

#### Maven
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
    <version>main-SNAPSHOT</version>
    <!-- Или <version>1.0.0</version> если появится тег -->
</dependency>
```

### Ручная установка

1. Перейдите на [Releases](https://github.com/camp3rcraft/coconutlib/releases)
2. Скачайте последний jar-файл CoconutLib
3. Поместите его в папку `plugins/` вашего сервера
4. Перезапустите сервер

---

## 🎯 Быстрый старт

```java
public class MyPlugin extends JavaPlugin {
    
    private CoconutLib coconutLib;
    private ArenaManager arenaManager;
    
    @Override
    public void onEnable() {
        // Получаем CoconutLib
        coconutLib = (CoconutLib) getServer().getPluginManager().getPlugin("CoconutLib");
        
        if (coconutLib == null) {
            getLogger().severe("CoconutLib не найден!");
            return;
        }
        
        // Получаем менеджеры
        arenaManager = coconutLib.getArenaManager();
        
        // Создаем арену
        arenaManager.createArena("my_arena", 10, 2);
        
        getLogger().info("MyPlugin загружен!");
    }
}
```

## 📋 Команды

| Команда | Описание | Права |
|---------|----------|-------|
| `/arena create <название> [макс] [мин]` | Создать арену | `coconutlib.arena.admin` |
| `/arena delete <название>` | Удалить арену | `coconutlib.arena.admin` |
| `/arena list` | Список арен | `coconutlib.arena.admin` |
| `/arena join <название>` | Присоединиться к арене | `coconutlib.arena.join` |
| `/arena leave` | Покинуть арену | `coconutlib.arena.leave` |
| `/arena info <название>` | Информация об арене | `coconutlib.arena.admin` |
| `/arena regenerate <название>` | Регенерировать арену | `coconutlib.arena.admin` |
| `/arena status <название> <статус>` | Установить статус | `coconutlib.arena.admin` |

## ⚙️ Конфигурация

```yaml
# worlds - Настройки миров
worlds:
  default_environment: "NORMAL"
  seed: 0
  generate_structures: false

# scoreboard - Настройки скорбордов
scoreboard:
  enabled: true
  title: "§6§lАРЕНА"
  lobby: "§aЛобби"
  waiting: "§eОжидание игроков"
  countdown: "§6Ожидание начала"
  game: "§cИгра"
  end: "§4Конец"

# tab - Настройки табов
tab:
  enabled: true
  header: "§6§lАРЕНА"
  footer: "§7Игроков на арене: §e%players%"

# inventory - Настройки инвентарей
inventory:
  clear_on_leave: true
  save_on_join: true
```

## 🔧 API для разработчиков

### ArenaManager
```java
// Создание арены
boolean success = arenaManager.createArena("arena1", 10, 2);

// Присоединение к арене
boolean joined = arenaManager.joinArena(player, "arena1");

// Изменение статуса
arenaManager.updateArenaStatus("arena1", ArenaStatus.GAME);

// Получение арены
Arena arena = arenaManager.getArena("arena1");
```

### WorldManager
```java
// Телепортация в арену
boolean teleported = worldManager.teleportToArena(player, "arena_world");

// Создание мира
World world = worldManager.createArenaWorld("arena_world");
```

### ScoreboardManager
```java
// Обновление скорборда
scoreboardManager.updatePlayerScoreboard(player, arena);

// Удаление скорборда
scoreboardManager.removePlayerScoreboard(player);
```

### TabManager
```java
// Обновление таба
tabManager.updatePlayerTab(player, arena);

// Удаление таба
tabManager.removePlayerTab(player);
```

### InventoryManager
```java
// Сохранение инвентаря
inventoryManager.savePlayerInventory(player);

// Восстановление инвентаря
inventoryManager.restorePlayerInventory(player);

// Очистка инвентаря
inventoryManager.clearPlayerInventory(player);
```

## 🎮 Статусы арен

- **LOBBY** - Лобби, ожидание игроков
- **WAITING** - Ожидание достаточного количества игроков
- **COUNTDOWN** - Обратный отсчет до начала игры
- **GAME** - Игра в процессе
- **END** - Игра завершена

## 🌈 Поддержка цветов

CoconutLib поддерживает различные форматы цветов:

```java
// HEX цвета
String hexColor = "&#FF5733";

// Ванильные цвета
String vanillaColor = "&cКрасный текст";

// MiniMessage
String miniMessage = "<gradient:red:blue>Градиентный текст</gradient>";

// Применение цветов
String colored = ColorUtils.colorize("&cКрасный &aзеленый &bсиний");
```

## 📁 Структура проекта

```
CoconutLib/
├── src/main/java/com/camp3rcraft/coconutlib/
│   ├── CoconutLib.java              # Главный класс
│   ├── arena/                       # Система арен
│   │   ├── Arena.java
│   │   ├── ArenaManager.java
│   │   └── ArenaStatus.java
│   ├── world/                       # Управление мирами
│   │   └── WorldManager.java
│   ├── scoreboard/                  # Скорборды
│   │   └── ScoreboardManager.java
│   ├── tab/                         # Табы
│   │   └── TabManager.java
│   ├── inventory/                   # Инвентари
│   │   └── InventoryManager.java
│   ├── config/                      # Конфигурация
│   │   └── ConfigManager.java
│   ├── commands/                    # Команды
│   │   └── ArenaCommand.java
│   └── utils/                       # Утилиты
│       └── ColorUtils.java
├── src/main/resources/
│   ├── plugin.yml
│   └── config.yml
├── docs/                            # Документация
│   ├── COMPLETE_GUIDE.md
│   ├── TECHNICAL_REFERENCE.md
│   ├── EXAMPLES.md
│   └── JITPACK_SETUP.md
├── examples/                        # Примеры
│   └── SimpleMinigame/
├── build.gradle.kts
├── jitpack.yml
└── README.md
```

## 🛠️ Требования

- **Java**: 17 или выше
- **Minecraft**: Paper 1.21.4+
- **Gradle**: 8.5+ (для сборки)

## 📝 Лицензия

Этот проект лицензирован под MIT License - см. файл [LICENSE](LICENSE) для деталей.

## 🤝 Поддержка

- **GitHub Issues**: [Создать issue](https://github.com/camp3rcraft/CoconutLib/issues)
- **Discussions**: [Обсуждения](https://github.com/camp3rcraft/CoconutLib/discussions)
- **Wiki**: [Документация](https://github.com/camp3rcraft/CoconutLib/wiki)

## ⭐ Особенности

- **Простота использования** - Интуитивный API
- **Производительность** - In-memory хранилище
- **Гибкость** - Легкая кастомизация
- **Надежность** - Протестированный код
- **Документация** - Подробные примеры и руководства

---

*CoconutLib - Удобное создание арен для Minecraft плагинов* 