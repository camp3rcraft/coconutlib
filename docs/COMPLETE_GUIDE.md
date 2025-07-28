# CoconutLib - Полное руководство

## Содержание

1. [Введение](#введение)
2. [Архитектура библиотеки](#архитектура-библиотеки)
3. [Установка и настройка](#установка-и-настройка)
4. [Основные концепции](#основные-концепции)
5. [API документация](#api-документация)
6. [Примеры использования](#примеры-использования)
7. [Конфигурация](#конфигурация)
8. [Команды](#команды)
9. [События и хуки](#события-и-хуки)
10. [Производительность](#производительность)
11. [Отладка](#отладка)
12. [Часто задаваемые вопросы](#часто-задаваемые-вопросы)

---

## Введение

### Что такое CoconutLib?

CoconutLib - это Java библиотека для Minecraft серверов на базе Paper, которая предоставляет полный набор инструментов для создания и управления аренами в мини-играх. Библиотека разработана с целью упростить процесс создания плагинов для мини-игр, предоставляя готовые решения для самых распространенных задач.

### Основные возможности

- ✅ **Создание арен** - Каждая арена представляет собой отдельный мир
- ✅ **Управление игроками** - Присоединение, выход, телепортация
- ✅ **Система статусов** - Лобби, ожидание, обратный отсчет, игра, конец
- ✅ **Скорборды** - Динамические скорборды для каждого статуса
- ✅ **Табы** - Кастомные табы с информацией об арене
- ✅ **Управление инвентарями** - Сохранение и восстановление
- ✅ **Создание миров** - Void миры без рестарта сервера
- ✅ **Цвета** - Поддержка HEX и ванильных цветов
- ✅ **In-Memory хранилище** - Быстрая работа без внешних зависимостей

### Философия библиотеки

CoconutLib следует принципу "сделай одну вещь и сделай её хорошо". Библиотека фокусируется исключительно на предоставлении удобных инструментов для создания арен, не навязывая конкретную логику игры. Это позволяет разработчикам создавать любые типы мини-игр, используя предоставленные компоненты как строительные блоки.

---

## Архитектура библиотеки

### Общая структура

```
CoconutLib/
├── CoconutLib.java              # Главный класс плагина
├── arena/                       # Система управления аренами
│   ├── Arena.java              # Модель арены
│   ├── ArenaManager.java       # Менеджер арен
│   └── ArenaStatus.java        # Статусы арен
├── world/                      # Управление мирами
│   └── WorldManager.java       # Менеджер миров
├── scoreboard/                 # Система скорбордов
│   └── ScoreboardManager.java  # Менеджер скорбордов
├── tab/                        # Система табов
│   └── TabManager.java         # Менеджер табов
├── inventory/                  # Управление инвентарями
│   └── InventoryManager.java   # Менеджер инвентарей
├── config/                     # Конфигурация
│   └── ConfigManager.java      # Менеджер конфигурации
├── commands/                   # Команды
│   └── ArenaCommand.java       # Команды арен
├── utils/                      # Утилиты
│   └── ColorUtils.java         # Утилиты для цветов
└── resources/                  # Ресурсы
    ├── plugin.yml              # Метаданные плагина
    ├── config.yml              # Конфигурация по умолчанию
    └── inventories.yml         # Сохраненные инвентари
```

### Принципы архитектуры

1. **Модульность** - Каждый компонент отвечает за свою область
2. **Инверсия зависимостей** - Компоненты слабо связаны между собой
3. **Единая ответственность** - Каждый класс имеет одну четкую задачу
4. **Конфигурируемость** - Все настройки вынесены в конфигурацию
5. **Производительность** - Использование in-memory хранилища для скорости

### Поток данных

```
Игрок → ArenaManager → WorldManager → ScoreboardManager → TabManager
  ↓
InventoryManager ← ConfigManager ← CoconutLib
```

---

## Установка и настройка

### Требования

- **Java**: 17 или выше
- **Minecraft**: Paper 1.21.4+
- **Gradle**: 8.5+ (для сборки)

### Установка через Jitpack (рекомендуется)

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

### Первоначальная настройка

После установки библиотеки на сервер, создайте базовую конфигурацию:

```yaml
# plugins/CoconutLib/config.yml
worlds:
  default_environment: "NORMAL"
  seed: 0
  generate_structures: false

scoreboard:
  enabled: true
  title: "§6§lАРЕНА"

tab:
  enabled: true
  header: "§6§lАРЕНА"
  footer: "§7Игроков на арене: §e%players%"

inventory:
  clear_on_leave: true
  save_on_join: true
```

---

## Основные концепции

### Арена (Arena)

Арена - это центральная концепция библиотеки. Каждая арена представляет собой:

- **Отдельный мир** - Изолированное пространство для игры
- **Список игроков** - Участники арены
- **Статус** - Текущее состояние арены
- **Настройки** - Лимиты игроков, параметры

```java
Arena arena = new Arena(
    "my_arena",           // Название
    "arena_my_arena",     // Имя мира
    ArenaStatus.LOBBY,    // Статус
    10,                   // Максимум игроков
    2,                    // Минимум игроков
    System.currentTimeMillis() // Время создания
);
```

### Статусы арен

Библиотека поддерживает 5 основных статусов:

1. **LOBBY** - Лобби, ожидание игроков
2. **WAITING** - Ожидание достаточного количества игроков
3. **COUNTDOWN** - Обратный отсчет до начала игры
4. **GAME** - Игра в процессе
5. **END** - Игра завершена

```java
// Изменение статуса
arenaManager.updateArenaStatus("my_arena", ArenaStatus.GAME);

// Получение статуса
ArenaStatus status = arena.getStatus();
```

### Менеджеры

Библиотека использует паттерн "Manager" для организации кода:

- **ArenaManager** - Управление аренами
- **WorldManager** - Управление мирами
- **ScoreboardManager** - Управление скорбордами
- **TabManager** - Управление табами
- **InventoryManager** - Управление инвентарями
- **ConfigManager** - Управление конфигурацией

### In-Memory хранилище

CoconutLib использует in-memory хранилище для максимальной производительности:

- **Преимущества**: Быстрый доступ, простота
- **Недостатки**: Данные не сохраняются при перезапуске
- **Применение**: Подходит для временных арен и быстрых игр

---

## API документация

### CoconutLib (Главный класс)

```java
public class CoconutLib extends JavaPlugin {
    // Получение экземпляра
    public static CoconutLib getInstance();
    
    // Получение менеджеров
    public ArenaManager getArenaManager();
    public WorldManager getWorldManager();
    public ScoreboardManager getScoreboardManager();
    public TabManager getTabManager();
    public ConfigManager getConfigManager();
}
```

### ArenaManager

Основной класс для управления аренами.

#### Создание арен

```java
// Создание арены с параметрами по умолчанию
boolean success = arenaManager.createArena("arena1", 10, 2);

// Проверка существования арены
Arena arena = arenaManager.getArena("arena1");
if (arena != null) {
    // Арена существует
}
```

#### Управление игроками

```java
// Присоединение к арене
boolean joined = arenaManager.joinArena(player, "arena1");

// Покидание арены
boolean left = arenaManager.leaveArena(player, "arena1");

// Получение арены игрока
Arena playerArena = arenaManager.getPlayerArena(player.getUniqueId());
```

#### Управление статусами

```java
// Изменение статуса
arenaManager.updateArenaStatus("arena1", ArenaStatus.GAME);

// Получение всех арен
List<Arena> allArenas = arenaManager.getAllArenas();
```

#### Регенерация

```java
// Регенерация арены (удаление и создание заново)
boolean regenerated = arenaManager.regenerateArena("arena1");
```

### WorldManager

Управление мирами арен.

#### Создание миров

```java
// Создание void мира
World world = worldManager.createArenaWorld("arena_world");

// Проверка существования мира
World existingWorld = Bukkit.getWorld("arena_world");
```

#### Телепортация

```java
// Телепортация в арену
boolean teleported = worldManager.teleportToArena(player, "arena_world");
```

#### Удаление миров

```java
// Удаление мира арены
boolean deleted = worldManager.deleteArenaWorld("arena_world");
```

### ScoreboardManager

Управление скорбордами игроков.

#### Обновление скорбордов

```java
// Обновление скорборда для игрока
scoreboardManager.updatePlayerScoreboard(player, arena);

// Обновление скорбордов всех игроков арены
scoreboardManager.updateAllArenaScoreboards(arena);
```

#### Удаление скорбордов

```java
// Удаление скорборда игрока
scoreboardManager.removePlayerScoreboard(player);

// Очистка всех скорбордов
scoreboardManager.clearAllScoreboards();
```

### TabManager

Управление табами игроков.

#### Обновление табов

```java
// Обновление таба для игрока
tabManager.updatePlayerTab(player, arena);

// Обновление табов всех игроков арены
tabManager.updateAllArenaTabs(arena);
```

#### Удаление табов

```java
// Удаление таба игрока
tabManager.removePlayerTab(player);

// Очистка всех табов
tabManager.clearAllTabs();
```

### InventoryManager

Управление инвентарями игроков.

#### Сохранение и восстановление

```java
// Сохранение инвентаря
inventoryManager.savePlayerInventory(player);

// Восстановление инвентаря
inventoryManager.restorePlayerInventory(player);

// Очистка инвентаря
inventoryManager.clearPlayerInventory(player);
```

#### Проверки

```java
// Проверка наличия сохраненного инвентаря
boolean hasSaved = inventoryManager.hasSavedInventory(player);
```

### ConfigManager

Управление конфигурацией.

#### Получение значений

```java
// Строковые значения
String title = configManager.getString("scoreboard.title", "§6§lАРЕНА");

// Числовые значения
int maxPlayers = configManager.getInt("arena.max_players", 10);

// Логические значения
boolean enabled = configManager.getBoolean("scoreboard.enabled", true);
```

#### Перезагрузка

```java
// Перезагрузка конфигурации
configManager.reloadConfig();
```

### ColorUtils

Утилиты для работы с цветами.

#### Поддержка цветов

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

---

## Примеры использования

### Базовый пример

```java
public class MyMinigamePlugin extends JavaPlugin {
    
    private CoconutLib coconutLib;
    private ArenaManager arenaManager;
    
    @Override
    public void onEnable() {
        // Получаем экземпляр CoconutLib
        coconutLib = (CoconutLib) getServer().getPluginManager().getPlugin("CoconutLib");
        
        if (coconutLib == null) {
            getLogger().severe("CoconutLib не найден!");
            return;
        }
        
        // Получаем менеджеры
        arenaManager = coconutLib.getArenaManager();
        
        // Создаем арену
        arenaManager.createArena("my_arena", 10, 2);
        
        getLogger().info("MyMinigamePlugin загружен!");
    }
    
    @Override
    public void onDisable() {
        getLogger().info("MyMinigamePlugin выключен!");
    }
}
```

### Продвинутый пример с игровым циклом

```java
public class AdvancedMinigamePlugin extends JavaPlugin {
    
    private CoconutLib coconutLib;
    private ArenaManager arenaManager;
    private BukkitTask gameTask;
    
    @Override
    public void onEnable() {
        coconutLib = (CoconutLib) getServer().getPluginManager().getPlugin("CoconutLib");
        arenaManager = coconutLib.getArenaManager();
        
        // Создаем арену
        arenaManager.createArena("advanced_arena", 8, 2);
        
        // Запускаем игровой цикл
        startGameLoop();
    }
    
    private void startGameLoop() {
        gameTask = Bukkit.getScheduler().runTaskTimer(this, () -> {
            Arena arena = arenaManager.getArena("advanced_arena");
            if (arena == null) return;
            
            switch (arena.getStatus()) {
                case LOBBY:
                    handleLobby(arena);
                    break;
                case WAITING:
                    handleWaiting(arena);
                    break;
                case COUNTDOWN:
                    handleCountdown(arena);
                    break;
                case GAME:
                    handleGame(arena);
                    break;
                case END:
                    handleEnd(arena);
                    break;
            }
        }, 20L, 20L); // Каждую секунду
    }
    
    private void handleLobby(Arena arena) {
        if (arena.getPlayerCount() >= arena.getMinPlayers()) {
            arenaManager.updateArenaStatus("advanced_arena", ArenaStatus.WAITING);
        }
    }
    
    private void handleWaiting(Arena arena) {
        // Логика ожидания
        arenaManager.updateArenaStatus("advanced_arena", ArenaStatus.COUNTDOWN);
    }
    
    private void handleCountdown(Arena arena) {
        // Обратный отсчет
        arenaManager.updateArenaStatus("advanced_arena", ArenaStatus.GAME);
    }
    
    private void handleGame(Arena arena) {
        // Логика игры
        if (gameEnded(arena)) {
            arenaManager.updateArenaStatus("advanced_arena", ArenaStatus.END);
        }
    }
    
    private void handleEnd(Arena arena) {
        // Завершение игры
        arenaManager.updateArenaStatus("advanced_arena", ArenaStatus.LOBBY);
    }
    
    private boolean gameEnded(Arena arena) {
        // Ваша логика определения окончания игры
        return false;
    }
    
    @Override
    public void onDisable() {
        if (gameTask != null) {
            gameTask.cancel();
        }
    }
}
```

### Пример с кастомными скорбордами

```java
public class CustomScoreboardPlugin extends JavaPlugin {
    
    private CoconutLib coconutLib;
    private ScoreboardManager scoreboardManager;
    
    @Override
    public void onEnable() {
        coconutLib = (CoconutLib) getServer().getPluginManager().getPlugin("CoconutLib");
        scoreboardManager = coconutLib.getScoreboardManager();
        
        // Создаем арену
        ArenaManager arenaManager = coconutLib.getArenaManager();
        arenaManager.createArena("custom_arena", 6, 2);
        
        // Регистрируем события
        getServer().getPluginManager().registerEvents(new CustomScoreboardListener(), this);
    }
    
    private class CustomScoreboardListener implements Listener {
        
        @EventHandler
        public void onPlayerJoinArena(PlayerJoinEvent event) {
            Arena arena = coconutLib.getArenaManager().getPlayerArena(event.getPlayer().getUniqueId());
            if (arena != null) {
                updateCustomScoreboard(event.getPlayer(), arena);
            }
        }
        
        private void updateCustomScoreboard(Player player, Arena arena) {
            // Создаем кастомный скорборд
            org.bukkit.scoreboard.ScoreboardManager manager = Bukkit.getScoreboardManager();
            Scoreboard scoreboard = manager.getNewScoreboard();
            
            Objective objective = scoreboard.registerNewObjective("custom", "dummy", "§6§lКАСТОМНАЯ ИГРА");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            
            // Добавляем кастомную информацию
            objective.getScore("§7Игроки: §e" + arena.getPlayerCount()).setScore(10);
            objective.getScore("§7Статус: §a" + arena.getStatus().getDisplayName()).setScore(9);
            objective.getScore("§7Время: §e" + getCurrentTime()).setScore(8);
            
            player.setScoreboard(scoreboard);
        }
        
        private String getCurrentTime() {
            return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        }
    }
}
```

---

## Конфигурация

### Основные настройки

```yaml
# worlds - Настройки миров
worlds:
  default_environment: "NORMAL"  # Тип мира (NORMAL, NETHER, THE_END)
  seed: 0                       # Сид для генерации мира
  generate_structures: false     # Генерировать ли структуры

# scoreboard - Настройки скорбордов
scoreboard:
  enabled: true                 # Включить скорборды
  title: "§6§lАРЕНА"           # Заголовок скорборда
  lobby: "§aЛобби"             # Текст для статуса LOBBY
  waiting: "§eОжидание игроков" # Текст для статуса WAITING
  countdown: "§6Ожидание начала" # Текст для статуса COUNTDOWN
  game: "§cИгра"               # Текст для статуса GAME
  end: "§4Конец"               # Текст для статуса END

# tab - Настройки табов
tab:
  enabled: true                 # Включить табы
  header: "§6§lАРЕНА"          # Заголовок таба
  footer: "§7Игроков на арене: §e%players%" # Подвал таба

# inventory - Настройки инвентарей
inventory:
  clear_on_leave: true          # Очищать инвентарь при выходе
  save_on_join: true            # Сохранять инвентарь при входе

# messages - Настройки сообщений
messages:
  prefix: "§8[§6CoconutLib§8] §r" # Префикс сообщений
  arena_created: "§aАрена §e%arena% §aсоздана!"
  arena_deleted: "§cАрена §e%arena% §cудалена!"
  arena_joined: "§aВы присоединились к арене §e%arena%"
  arena_left: "§cВы покинули арену §e%arena%"
  arena_not_found: "§cАрена §e%arena% §cне найдена!"
  arena_already_exists: "§cАрена §e%arena% §cуже существует!"
  arena_regenerated: "§aАрена §e%arena% §aрегенерирована!"
```

### Переменные в сообщениях

В сообщениях можно использовать следующие переменные:

- `%arena%` - Название арены
- `%players%` - Количество игроков
- `%max_players%` - Максимальное количество игроков
- `%status%` - Статус арены
- `%time%` - Текущее время

### Кастомизация цветов

```yaml
scoreboard:
  title: "&#FF5733§lМОЯ ИГРА"  # HEX цвет + ванильный цвет
  game: "&c&#FF0000ИГРА"       # Комбинация цветов
```

---

## Команды

### Основные команды

| Команда | Описание | Права | Пример |
|---------|----------|-------|--------|
| `/arena create` | Создать арену | `coconutlib.arena.admin` | `/arena create my_arena 10 2` |
| `/arena delete` | Удалить арену | `coconutlib.arena.admin` | `/arena delete my_arena` |
| `/arena list` | Список арен | `coconutlib.arena.admin` | `/arena list` |
| `/arena join` | Присоединиться к арене | `coconutlib.arena.join` | `/arena join my_arena` |
| `/arena leave` | Покинуть арену | `coconutlib.arena.leave` | `/arena leave` |
| `/arena info` | Информация об арене | `coconutlib.arena.admin` | `/arena info my_arena` |
| `/arena regenerate` | Регенерировать арену | `coconutlib.arena.admin` | `/arena regenerate my_arena` |
| `/arena status` | Установить статус | `coconutlib.arena.admin` | `/arena status my_arena GAME` |

### Права доступа

```yaml
permissions:
  coconutlib.arena.admin:
    description: "Полный доступ к управлению аренами"
    default: op
  coconutlib.arena.join:
    description: "Возможность присоединяться к аренам"
    default: true
  coconutlib.arena.leave:
    description: "Возможность покидать арены"
    default: true
```

### Tab Completion

Все команды поддерживают автодополнение:

```bash
/arena create <название> [макс_игроков] [мин_игроков]
/arena status <название> <LOBBY|WAITING|COUNTDOWN|GAME|END>
```

---

## События и хуки

### Интеграция с другими плагинами

CoconutLib предоставляет несколько способов интеграции:

#### 1. Получение экземпляра

```java
CoconutLib coconutLib = (CoconutLib) getServer().getPluginManager().getPlugin("CoconutLib");
```

#### 2. Слушание событий

```java
@EventHandler
public void onPlayerJoinArena(PlayerJoinEvent event) {
    Arena arena = coconutLib.getArenaManager().getPlayerArena(event.getPlayer().getUniqueId());
    if (arena != null) {
        // Игрок находится на арене
    }
}
```

#### 3. Кастомные события

```java
public class ArenaJoinEvent extends Event {
    private final Player player;
    private final Arena arena;
    
    public ArenaJoinEvent(Player player, Arena arena) {
        this.player = player;
        this.arena = arena;
    }
    
    // Геттеры...
}
```

### PlaceholderAPI интеграция

```java
// Регистрация плейсхолдеров
if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
    new ArenaPlaceholder(coconutLib).register();
}

public class ArenaPlaceholder extends PlaceholderExpansion {
    private final CoconutLib plugin;
    
    public ArenaPlaceholder(CoconutLib plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (identifier.equals("arena_name")) {
            Arena arena = plugin.getArenaManager().getPlayerArena(player.getUniqueId());
            return arena != null ? arena.getName() : "Нет";
        }
        return null;
    }
}
```

---

## Производительность

### Оптимизация памяти

1. **In-Memory хранилище** - Быстрый доступ к данным
2. **Ленивая загрузка** - Компоненты создаются по требованию
3. **Кэширование** - Часто используемые данные кэшируются

### Мониторинг производительности

```java
// Измерение времени выполнения
long startTime = System.currentTimeMillis();
arenaManager.createArena("test", 10, 2);
long endTime = System.currentTimeMillis();
getLogger().info("Создание арены заняло: " + (endTime - startTime) + "ms");
```

### Рекомендации

1. **Ограничение количества арен** - Не создавайте слишком много арен одновременно
2. **Регулярная очистка** - Удаляйте неиспользуемые арены
3. **Мониторинг памяти** - Следите за использованием памяти

---

## Отладка

### Логирование

CoconutLib использует стандартную систему логирования Bukkit:

```java
// Включение подробного логирования
plugin.getLogger().setLevel(Level.FINE);

// Логирование событий
plugin.getLogger().info("Арена " + arenaName + " создана");
plugin.getLogger().warning("Предупреждение: " + message);
plugin.getLogger().severe("Ошибка: " + error);
```

### Отладочные команды

```java
// Добавление отладочной команды
@Override
public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (command.getName().equalsIgnoreCase("debug")) {
        ArenaManager arenaManager = coconutLib.getArenaManager();
        
        sender.sendMessage("§6=== Отладочная информация ===");
        sender.sendMessage("§7Арен: " + arenaManager.getAllArenas().size());
        
        for (Arena arena : arenaManager.getAllArenas()) {
            sender.sendMessage("§e- " + arena.getName() + " (" + arena.getStatus() + ")");
        }
        
        return true;
    }
    return false;
}
```

### Проверка состояния

```java
// Проверка корректности данных
public void validateArena(Arena arena) {
    if (arena == null) {
        getLogger().severe("Арена null!");
        return;
    }
    
    if (arena.getName() == null || arena.getName().isEmpty()) {
        getLogger().severe("Название арены пустое!");
        return;
    }
    
    if (arena.getMaxPlayers() < arena.getMinPlayers()) {
        getLogger().warning("Максимум игроков меньше минимума!");
    }
}
```

---

## Часто задаваемые вопросы

### Q: Как создать арену программно?

```java
ArenaManager arenaManager = coconutLib.getArenaManager();
boolean success = arenaManager.createArena("my_arena", 10, 2);
if (success) {
    getLogger().info("Арена создана успешно!");
} else {
    getLogger().warning("Не удалось создать арену!");
}
```

### Q: Как получить всех игроков на арене?

```java
Arena arena = arenaManager.getArena("my_arena");
if (arena != null) {
    List<UUID> playerUUIDs = arena.getPlayers();
    for (UUID uuid : playerUUIDs) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            // Работа с игроком
        }
    }
}
```

### Q: Как изменить статус арены?

```java
arenaManager.updateArenaStatus("my_arena", ArenaStatus.GAME);
```

### Q: Как телепортировать игрока на арену?

```java
Arena arena = arenaManager.getArena("my_arena");
if (arena != null) {
    WorldManager worldManager = coconutLib.getWorldManager();
    worldManager.teleportToArena(player, arena.getWorldName());
}
```

### Q: Как сохранить данные арены?

CoconutLib использует in-memory хранилище, поэтому данные не сохраняются между перезапусками. Для постоянного хранения используйте внешние решения:

```java
// Пример с JSON файлом
public void saveArenaData(Arena arena) {
    try {
        Gson gson = new Gson();
        String json = gson.toJson(arena);
        
        File file = new File(getDataFolder(), "arenas/" + arena.getName() + ".json");
        file.getParentFile().mkdirs();
        
        Files.write(file.toPath(), json.getBytes());
    } catch (IOException e) {
        getLogger().severe("Не удалось сохранить данные арены: " + e.getMessage());
    }
}
```

### Q: Как добавить кастомные поля в арену?

```java
// Создание расширенного класса арены
public class CustomArena extends Arena {
    private String gameType;
    private int score;
    
    public CustomArena(String name, String worldName, ArenaStatus status, 
                      int maxPlayers, int minPlayers, Long createdAt) {
        super(name, worldName, status, maxPlayers, minPlayers, createdAt);
    }
    
    // Геттеры и сеттеры для кастомных полей
    public String getGameType() { return gameType; }
    public void setGameType(String gameType) { this.gameType = gameType; }
    
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
}
```

### Q: Как интегрировать с другими плагинами?

```java
// Пример интеграции с Vault
public class VaultIntegration {
    private final CoconutLib coconutLib;
    private final Economy economy;
    
    public VaultIntegration(CoconutLib coconutLib) {
        this.coconutLib = coconutLib;
        this.economy = Bukkit.getServicesManager().getRegistration(Economy.class).getProvider();
    }
    
    public void giveReward(Player player, double amount) {
        if (economy != null) {
            economy.depositPlayer(player, amount);
            player.sendMessage("§aВы получили награду: §e$" + amount);
        }
    }
}
```

### Q: Как создать кастомный скорборд?

```java
public void createCustomScoreboard(Player player, Arena arena) {
    org.bukkit.scoreboard.ScoreboardManager manager = Bukkit.getScoreboardManager();
    Scoreboard scoreboard = manager.getNewScoreboard();
    
    Objective objective = scoreboard.registerNewObjective("custom", "dummy", "§6§lКАСТОМНЫЙ СКОРБОРД");
    objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    
    // Добавляем кастомные строки
    objective.getScore("§7Игроки: §e" + arena.getPlayerCount()).setScore(10);
    objective.getScore("§7Статус: §a" + arena.getStatus().getDisplayName()).setScore(9);
    objective.getScore("§7Время: §e" + getCurrentTime()).setScore(8);
    
    player.setScoreboard(scoreboard);
}
```

---

## Заключение

CoconutLib предоставляет мощный и гибкий API для создания арен в Minecraft плагинах. Библиотека следует принципам простоты и производительности, позволяя разработчикам сосредоточиться на логике игры, а не на технических деталях.

### Основные преимущества

- ✅ **Простота использования** - Интуитивный API
- ✅ **Производительность** - In-memory хранилище
- ✅ **Гибкость** - Легкая кастомизация
- ✅ **Надежность** - Протестированный код
- ✅ **Документация** - Подробные примеры

### Следующие шаги

1. Изучите примеры в папке `examples/`
2. Создайте свой первый плагин с CoconutLib
3. Присоединитесь к сообществу для обмена опытом
4. Внесите свой вклад в развитие библиотеки

### Поддержка

- **GitHub**: [https://github.com/camp3rcraft/CoconutLib](https://github.com/camp3rcraft/CoconutLib)
- **Issues**: Создавайте issues для багов и предложений
- **Discussions**: Обсуждайте идеи и делитесь опытом

---

*CoconutLib - Удобное создание арен для Minecraft плагинов* 