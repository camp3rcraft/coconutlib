 # CoconutLib API Documentation

## Обзор

CoconutLib предоставляет полный API для создания мини-игр на Minecraft серверах. Библиотека включает в себя все необходимые компоненты для управления аренами, игроками, скорбордами и табами.

## Основные компоненты

### CoconutLib
Главный класс библиотеки, предоставляющий доступ ко всем менеджерам.

```java
CoconutLib coconutLib = (CoconutLib) getServer().getPluginManager().getPlugin("CoconutLib");
```

### ArenaManager
Управление аренами и игроками.

```java
ArenaManager arenaManager = coconutLib.getArenaManager();

// Создание арены
arenaManager.createArena("my_arena", 10, 2);

// Получение арены
Arena arena = arenaManager.getArena("my_arena");

// Присоединение игрока
arenaManager.joinArena(player, "my_arena");

// Покидание арены
arenaManager.leaveArena(player, "my_arena");

// Изменение статуса
arenaManager.updateArenaStatus("my_arena", ArenaStatus.GAME);
```

### Arena
Класс, представляющий арену.

```java
Arena arena = arenaManager.getArena("my_arena");

// Основные свойства
String name = arena.getName();
String worldName = arena.getWorldName();
ArenaStatus status = arena.getStatus();
int maxPlayers = arena.getMaxPlayers();
int minPlayers = arena.getMinPlayers();
int playerCount = arena.getPlayerCount();

// Проверки
boolean isFull = arena.isFull();
boolean hasEnoughPlayers = arena.hasEnoughPlayers();
boolean isEmpty = arena.isEmpty();

// Управление игроками
List<UUID> players = arena.getPlayers();
boolean hasPlayer = arena.hasPlayer(playerUUID);
```

### ArenaStatus
Enum статусов арены.

```java
ArenaStatus.LOBBY      // Лобби
ArenaStatus.WAITING    // Ожидание игроков
ArenaStatus.COUNTDOWN  // Обратный отсчет
ArenaStatus.GAME       // Игра
ArenaStatus.END        // Конец игры
```

### WorldManager
Управление мирами арен.

```java
WorldManager worldManager = coconutLib.getWorldManager();

// Создание мира
World world = worldManager.createArenaWorld("arena_my_arena");

// Удаление мира
worldManager.deleteArenaWorld("arena_my_arena");

// Регенерация мира
worldManager.regenerateArenaWorld("arena_my_arena");

// Телепортация игрока
worldManager.teleportToArena(player, "arena_my_arena");
```

### ScoreboardManager
Управление скорбордами.

```java
ScoreboardManager scoreboardManager = coconutLib.getScoreboardManager();

// Обновление скорборда игрока
scoreboardManager.updatePlayerScoreboard(player, arena);

// Удаление скорборда
scoreboardManager.removePlayerScoreboard(player);

// Обновление всех скорбордов арены
scoreboardManager.updateAllArenaScoreboards(arena);
```

### TabManager
Управление табами.

```java
TabManager tabManager = coconutLib.getTabManager();

// Обновление таба игрока
tabManager.updatePlayerTab(player, arena);

// Удаление таба
tabManager.removePlayerTab(player);

// Обновление всех табов арены
tabManager.updateAllArenaTabs(arena);
```

### InventoryManager
Управление инвентарями игроков.

```java
InventoryManager inventoryManager = coconutLib.getInventoryManager();

// Сохранение инвентаря
inventoryManager.savePlayerInventory(player);

// Восстановление инвентаря
inventoryManager.restorePlayerInventory(player);

// Очистка инвентаря
inventoryManager.clearPlayerInventory(player);

// Проверка наличия сохраненного инвентаря
boolean hasSaved = inventoryManager.hasSavedInventory(playerUUID);
```

### DatabaseManager
Работа с базой данных.

```java
DatabaseManager databaseManager = coconutLib.getDatabaseManager();

// Создание арены в БД
databaseManager.createArena("my_arena", "world_name", 10, 2);

// Получение арены из БД
Arena arena = databaseManager.getArena("my_arena");

// Обновление статуса
databaseManager.updateArenaStatus("my_arena", ArenaStatus.GAME);

// Добавление игрока в арену
databaseManager.addPlayerToArena("my_arena", playerUUID, playerName);

// Удаление игрока из арены
databaseManager.removePlayerFromArena("my_arena", playerUUID);

// Получение списка игроков арены
List<String> players = databaseManager.getArenaPlayers("my_arena");
```

### ConfigManager
Управление конфигурацией.

```java
ConfigManager configManager = coconutLib.getConfigManager();

// Получение строковых значений
String value = configManager.getString("path.to.setting");
String defaultValue = configManager.getString("path.to.setting", "default");

// Получение числовых значений
int intValue = configManager.getInt("path.to.setting");
int intDefault = configManager.getInt("path.to.setting", 0);

// Получение булевых значений
boolean boolValue = configManager.getBoolean("path.to.setting");
boolean boolDefault = configManager.getBoolean("path.to.setting", false);

// Получение длинных значений
long longValue = configManager.getLong("path.to.setting");
long longDefault = configManager.getLong("path.to.setting", 0L);

// Перезагрузка конфигурации
configManager.reloadConfig();
```

## Утилиты

### ColorUtils
Утилиты для работы с цветами.

```java
// Преобразование цветовых кодов
String colored = ColorUtils.colorize("&aЗеленый текст &#FF0000Красный текст");

// Создание Component из MiniMessage
Component component = ColorUtils.miniMessage("<gradient:red:blue>Градиентный текст</gradient>");

// Создание Component из цветовых кодов
Component coloredComponent = ColorUtils.colorizeComponent("&aЗеленый текст");

// Удаление цветовых кодов
String plain = ColorUtils.stripColors("&aЦветной текст");

// Проверка наличия цветов
boolean hasColors = ColorUtils.hasColors("&aТекст");

// Конвертация RGB в HEX
String hex = ColorUtils.rgbToHex(255, 0, 0); // "#FF0000"

// Конвертация HEX в RGB
int[] rgb = ColorUtils.hexToRgb("FF0000"); // [255, 0, 0]
```

## Примеры использования

### Создание простой мини-игры

```java
public class MyMinigame extends JavaPlugin {
    
    private CoconutLib coconutLib;
    private String arenaName = "my_game";
    
    @Override
    public void onEnable() {
        coconutLib = (CoconutLib) getServer().getPluginManager().getPlugin("CoconutLib");
        
        // Создаем арену
        if (coconutLib.getArenaManager().getArena(arenaName) == null) {
            coconutLib.getArenaManager().createArena(arenaName, 8, 2);
        }
        
        // Запускаем игровой цикл
        startGameLoop();
    }
    
    private void startGameLoop() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Arena arena = coconutLib.getArenaManager().getArena(arenaName);
                if (arena == null) return;
                
                // Логика игры
                switch (arena.getStatus()) {
                    case LOBBY:
                        if (arena.getPlayerCount() >= arena.getMinPlayers()) {
                            coconutLib.getArenaManager().updateArenaStatus(arenaName, ArenaStatus.WAITING);
                        }
                        break;
                    case WAITING:
                        coconutLib.getArenaManager().updateArenaStatus(arenaName, ArenaStatus.COUNTDOWN);
                        startCountdown();
                        break;
                    case GAME:
                        // Игровая логика
                        break;
                }
            }
        }.runTaskTimer(this, 20L, 20L);
    }
    
    private void startCountdown() {
        new BukkitRunnable() {
            int countdown = 10;
            
            @Override
            public void run() {
                if (countdown > 0) {
                    // Отправляем сообщение всем игрокам арены
                    Arena arena = coconutLib.getArenaManager().getArena(arenaName);
                    for (UUID playerUUID : arena.getPlayers()) {
                        Player player = Bukkit.getPlayer(playerUUID);
                        if (player != null) {
                            player.sendMessage("§6Игра начнется через " + countdown + " секунд!");
                        }
                    }
                    countdown--;
                } else {
                    coconutLib.getArenaManager().updateArenaStatus(arenaName, ArenaStatus.GAME);
                }
            }
        }.runTaskTimer(this, 20L, 20L);
    }
}
```

### Обработка событий

```java
@EventHandler
public void onPlayerJoinArena(PlayerJoinEvent event) {
    Player player = event.getPlayer();
    
    // Проверяем, находится ли игрок на арене
    Arena playerArena = coconutLib.getArenaManager().getPlayerArena(player.getUniqueId());
    if (playerArena != null) {
        // Обновляем скорборд и таб
        coconutLib.getScoreboardManager().updatePlayerScoreboard(player, playerArena);
        coconutLib.getTabManager().updatePlayerTab(player, playerArena);
    }
}

@EventHandler
public void onPlayerQuit(PlayerQuitEvent event) {
    Player player = event.getPlayer();
    
    // Удаляем скорборд и таб
    coconutLib.getScoreboardManager().removePlayerScoreboard(player);
    coconutLib.getTabManager().removePlayerTab(player);
}
```

### Настройка конфигурации

```java
// В config.yml
scoreboard:
  enabled: true
  title: "§6§lМОЯ ИГРА"
  lobby: "§aЛобби"
  waiting: "§eОжидание"
  countdown: "§6Обратный отсчет"
  game: "§cИгра"
  end: "§4Конец"

// В коде
String title = coconutLib.getConfigManager().getString("scoreboard.title");
boolean enabled = coconutLib.getConfigManager().getBoolean("scoreboard.enabled");
```

## Лучшие практики

1. **Всегда проверяйте наличие CoconutLib** перед использованием API
2. **Используйте try-catch** для обработки ошибок при работе с БД
3. **Обновляйте скорборды и табы** при изменении статуса арены
4. **Сохраняйте инвентари** игроков при входе в арену
5. **Используйте ColorUtils** для работы с цветами
6. **Настраивайте конфигурацию** через config.yml
7. **Обрабатывайте события** для корректной работы с игроками

## Обработка ошибок

```java
try {
    coconutLib.getArenaManager().createArena("my_arena", 10, 2);
} catch (Exception e) {
    getLogger().severe("Ошибка при создании арены: " + e.getMessage());
}

try {
    coconutLib.getDatabaseManager().updateArenaStatus("my_arena", ArenaStatus.GAME);
} catch (Exception e) {
    getLogger().warning("Ошибка при обновлении статуса арены: " + e.getMessage());
}
```

## Производительность

- Используйте кэширование для часто запрашиваемых данных
- Обновляйте скорборды и табы только при необходимости
- Используйте асинхронные операции для работы с БД
- Ограничивайте количество одновременных операций