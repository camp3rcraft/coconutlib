# CoconutLib - Примеры использования

## Содержание

1. [Базовые примеры](#базовые-примеры)
2. [Продвинутые примеры](#продвинутые-примеры)
3. [Интеграция с другими плагинами](#интеграция-с-другими-плагинами)
4. [Кастомизация](#кастомизация)
5. [Игровые механики](#игровые-механики)
6. [Отладка и тестирование](#отладка-и-тестирование)

---

## Базовые примеры

### Пример 1: Простая мини-игра

```java
public class SimpleMinigamePlugin extends JavaPlugin {
    
    private CoconutLib coconutLib;
    private ArenaManager arenaManager;
    private BukkitTask gameTask;
    
    @Override
    public void onEnable() {
        // Получаем CoconutLib
        coconutLib = (CoconutLib) getServer().getPluginManager().getPlugin("CoconutLib");
        if (coconutLib == null) {
            getLogger().severe("CoconutLib не найден!");
            return;
        }
        
        arenaManager = coconutLib.getArenaManager();
        
        // Создаем арену
        arenaManager.createArena("simple_game", 8, 2);
        
        // Запускаем игровой цикл
        startGameLoop();
        
        getLogger().info("SimpleMinigamePlugin загружен!");
    }
    
    private void startGameLoop() {
        gameTask = Bukkit.getScheduler().runTaskTimer(this, () -> {
            Arena arena = arenaManager.getArena("simple_game");
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
            arenaManager.updateArenaStatus("simple_game", ArenaStatus.WAITING);
        }
    }
    
    private void handleWaiting(Arena arena) {
        // Ждем 10 секунд перед началом игры
        Bukkit.getScheduler().runTaskLater(this, () -> {
            arenaManager.updateArenaStatus("simple_game", ArenaStatus.COUNTDOWN);
        }, 200L); // 10 секунд
    }
    
    private void handleCountdown(Arena arena) {
        // Обратный отсчет 5 секунд
        for (int i = 5; i > 0; i--) {
            final int count = i;
            Bukkit.getScheduler().runTaskLater(this, () -> {
                for (UUID playerUUID : arena.getPlayers()) {
                    Player player = Bukkit.getPlayer(playerUUID);
                    if (player != null) {
                        player.sendMessage("§6Игра начнется через §e" + count + "§6 секунд!");
                    }
                }
            }, (5 - i) * 20L);
        }
        
        Bukkit.getScheduler().runTaskLater(this, () -> {
            arenaManager.updateArenaStatus("simple_game", ArenaStatus.GAME);
        }, 100L); // 5 секунд
    }
    
    private void handleGame(Arena arena) {
        // Простая логика игры - побеждает последний выживший
        if (arena.getPlayerCount() <= 1) {
            arenaManager.updateArenaStatus("simple_game", ArenaStatus.END);
        }
    }
    
    private void handleEnd(Arena arena) {
        // Объявляем победителя
        if (arena.getPlayerCount() == 1) {
            UUID winnerUUID = arena.getPlayers().get(0);
            Player winner = Bukkit.getPlayer(winnerUUID);
            if (winner != null) {
                winner.sendMessage("§a§lПоздравляем! Вы победили!");
            }
        }
        
        // Возвращаемся в лобби через 5 секунд
        Bukkit.getScheduler().runTaskLater(this, () -> {
            arenaManager.updateArenaStatus("simple_game", ArenaStatus.LOBBY);
        }, 100L);
    }
    
    @Override
    public void onDisable() {
        if (gameTask != null) {
            gameTask.cancel();
        }
        getLogger().info("SimpleMinigamePlugin выключен!");
    }
}
```

### Пример 2: Команды для управления

```java
public class ArenaCommandsPlugin extends JavaPlugin {
    
    private CoconutLib coconutLib;
    private ArenaManager arenaManager;
    
    @Override
    public void onEnable() {
        coconutLib = (CoconutLib) getServer().getPluginManager().getPlugin("CoconutLib");
        arenaManager = coconutLib.getArenaManager();
        
        // Регистрируем команды
        getCommand("game").setExecutor(new GameCommand());
        
        getLogger().info("ArenaCommandsPlugin загружен!");
    }
    
    private class GameCommand implements CommandExecutor {
        
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cЭта команда только для игроков!");
                return true;
            }
            
            Player player = (Player) sender;
            
            if (args.length == 0) {
                showHelp(player);
                return true;
            }
            
            switch (args[0].toLowerCase()) {
                case "join":
                    if (args.length < 2) {
                        player.sendMessage("§cИспользование: /game join <арена>");
                        return true;
                    }
                    handleJoin(player, args[1]);
                    break;
                    
                case "leave":
                    handleLeave(player);
                    break;
                    
                case "list":
                    handleList(player);
                    break;
                    
                case "info":
                    if (args.length < 2) {
                        player.sendMessage("§cИспользование: /game info <арена>");
                        return true;
                    }
                    handleInfo(player, args[1]);
                    break;
                    
                default:
                    showHelp(player);
                    break;
            }
            
            return true;
        }
        
        private void showHelp(Player player) {
            player.sendMessage("§6=== Команды игры ===");
            player.sendMessage("§e/game join <арена> §7- Присоединиться к арене");
            player.sendMessage("§e/game leave §7- Покинуть арену");
            player.sendMessage("§e/game list §7- Список арен");
            player.sendMessage("§e/game info <арена> §7- Информация об арене");
        }
        
        private void handleJoin(Player player, String arenaName) {
            Arena arena = arenaManager.getArena(arenaName);
            if (arena == null) {
                player.sendMessage("§cАрена §e" + arenaName + " §cне найдена!");
                return;
            }
            
            if (arena.isFull()) {
                player.sendMessage("§cАрена полная!");
                return;
            }
            
            boolean success = arenaManager.joinArena(player, arenaName);
            if (success) {
                player.sendMessage("§aВы присоединились к арене §e" + arenaName);
            } else {
                player.sendMessage("§cНе удалось присоединиться к арене!");
            }
        }
        
        private void handleLeave(Player player) {
            Arena playerArena = arenaManager.getPlayerArena(player.getUniqueId());
            if (playerArena == null) {
                player.sendMessage("§cВы не находитесь на арене!");
                return;
            }
            
            boolean success = arenaManager.leaveArena(player, playerArena.getName());
            if (success) {
                player.sendMessage("§aВы покинули арену §e" + playerArena.getName());
            } else {
                player.sendMessage("§cНе удалось покинуть арену!");
            }
        }
        
        private void handleList(Player player) {
            List<Arena> arenas = arenaManager.getAllArenas();
            if (arenas.isEmpty()) {
                player.sendMessage("§7Нет доступных арен.");
                return;
            }
            
            player.sendMessage("§6=== Доступные арены ===");
            for (Arena arena : arenas) {
                String status = arena.getStatus().getDisplayName();
                String players = arena.getPlayerCount() + "/" + arena.getMaxPlayers();
                player.sendMessage("§e" + arena.getName() + " §7(" + status + ") §7- " + players);
            }
        }
        
        private void handleInfo(Player player, String arenaName) {
            Arena arena = arenaManager.getArena(arenaName);
            if (arena == null) {
                player.sendMessage("§cАрена §e" + arenaName + " §cне найдена!");
                return;
            }
            
            player.sendMessage("§6=== Информация об арене ===");
            player.sendMessage("§7Название: §e" + arena.getName());
            player.sendMessage("§7Статус: §e" + arena.getStatus().getDisplayName());
            player.sendMessage("§7Игроки: §e" + arena.getPlayerCount() + "/" + arena.getMaxPlayers());
            player.sendMessage("§7Мир: §e" + arena.getWorldName());
            
            if (!arena.getPlayers().isEmpty()) {
                player.sendMessage("§7Игроки на арене:");
                for (UUID playerUUID : arena.getPlayers()) {
                    Player arenaPlayer = Bukkit.getPlayer(playerUUID);
                    if (arenaPlayer != null) {
                        player.sendMessage("§e- " + arenaPlayer.getName());
                    }
                }
            }
        }
    }
}
```

---

## Продвинутые примеры

### Пример 3: Кастомные скорборды

```java
public class CustomScoreboardPlugin extends JavaPlugin {
    
    private CoconutLib coconutLib;
    private ScoreboardManager scoreboardManager;
    
    @Override
    public void onEnable() {
        coconutLib = (CoconutLib) getServer().getPluginManager().getPlugin("CoconutLib");
        scoreboardManager = coconutLib.getScoreboardManager();
        
        // Регистрируем события
        getServer().getPluginManager().registerEvents(new CustomScoreboardListener(), this);
        
        getLogger().info("CustomScoreboardPlugin загружен!");
    }
    
    private class CustomScoreboardListener implements Listener {
        
        @EventHandler
        public void onPlayerJoinArena(PlayerJoinEvent event) {
            Arena arena = coconutLib.getArenaManager().getPlayerArena(event.getPlayer().getUniqueId());
            if (arena != null) {
                updateCustomScoreboard(event.getPlayer(), arena);
            }
        }
        
        @EventHandler
        public void onPlayerQuit(PlayerQuitEvent event) {
            // Удаляем кастомный скорборд при выходе
            scoreboardManager.removePlayerScoreboard(event.getPlayer());
        }
        
        private void updateCustomScoreboard(Player player, Arena arena) {
            org.bukkit.scoreboard.ScoreboardManager manager = Bukkit.getScoreboardManager();
            Scoreboard scoreboard = manager.getNewScoreboard();
            
            Objective objective = scoreboard.registerNewObjective("custom", "dummy", "§6§lКАСТОМНАЯ ИГРА");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            
            // Добавляем кастомную информацию
            int score = 15;
            
            objective.getScore("§7Игроки: §e" + arena.getPlayerCount() + "/" + arena.getMaxPlayers()).setScore(score--);
            objective.getScore("§7Статус: §a" + arena.getStatus().getDisplayName()).setScore(score--);
            objective.getScore("§7Время: §e" + getCurrentTime()).setScore(score--);
            objective.getScore("§7Арена: §e" + arena.getName()).setScore(score--);
            
            // Добавляем кастомную информацию в зависимости от статуса
            switch (arena.getStatus()) {
                case LOBBY:
                    objective.getScore("§aОжидаем игроков...").setScore(score--);
                    break;
                case WAITING:
                    objective.getScore("§eНужно игроков: §f" + (arena.getMinPlayers() - arena.getPlayerCount())).setScore(score--);
                    break;
                case COUNTDOWN:
                    objective.getScore("§6Игра начнется скоро!").setScore(score--);
                    break;
                case GAME:
                    objective.getScore("§cИгра в процессе!").setScore(score--);
                    break;
                case END:
                    objective.getScore("§4Игра завершена!").setScore(score--);
                    break;
            }
            
            player.setScoreboard(scoreboard);
        }
        
        private String getCurrentTime() {
            return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        }
    }
}
```

### Пример 4: Система достижений

```java
public class AchievementPlugin extends JavaPlugin {
    
    private CoconutLib coconutLib;
    private Map<UUID, Set<String>> playerAchievements = new HashMap<>();
    
    @Override
    public void onEnable() {
        coconutLib = (CoconutLib) getServer().getPluginManager().getPlugin("CoconutLib");
        
        // Регистрируем события
        getServer().getPluginManager().registerEvents(new AchievementListener(), this);
        
        getLogger().info("AchievementPlugin загружен!");
    }
    
    private class AchievementListener implements Listener {
        
        @EventHandler
        public void onPlayerJoinArena(PlayerJoinEvent event) {
            Arena arena = coconutLib.getArenaManager().getPlayerArena(event.getPlayer().getUniqueId());
            if (arena != null) {
                checkFirstJoinAchievement(event.getPlayer());
            }
        }
        
        @EventHandler
        public void onPlayerDeath(PlayerDeathEvent event) {
            Arena arena = coconutLib.getArenaManager().getPlayerArena(event.getEntity().getUniqueId());
            if (arena != null) {
                checkSurvivalAchievement(event.getEntity());
            }
        }
        
        private void checkFirstJoinAchievement(Player player) {
            String achievement = "FIRST_JOIN";
            if (!hasAchievement(player, achievement)) {
                giveAchievement(player, achievement, "§aДостижение: §eПервое присоединение к арене");
            }
        }
        
        private void checkSurvivalAchievement(Player player) {
            String achievement = "SURVIVOR";
            if (!hasAchievement(player, achievement)) {
                giveAchievement(player, achievement, "§aДостижение: §eВыживший");
            }
        }
        
        private boolean hasAchievement(Player player, String achievement) {
            Set<String> achievements = playerAchievements.get(player.getUniqueId());
            return achievements != null && achievements.contains(achievement);
        }
        
        private void giveAchievement(Player player, String achievement, String message) {
            playerAchievements.computeIfAbsent(player.getUniqueId(), k -> new HashSet<>()).add(achievement);
            player.sendMessage(message);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        }
    }
}
```

---

## Интеграция с другими плагинами

### Пример 5: Интеграция с Vault

```java
public class VaultIntegrationPlugin extends JavaPlugin {
    
    private CoconutLib coconutLib;
    private Economy economy;
    private Permission permissions;
    
    @Override
    public void onEnable() {
        coconutLib = (CoconutLib) getServer().getPluginManager().getPlugin("CoconutLib");
        
        // Подключаем Vault
        if (setupEconomy() && setupPermissions()) {
            getLogger().info("Vault интеграция успешно подключена!");
        } else {
            getLogger().severe("Vault интеграция не найдена!");
            return;
        }
        
        // Регистрируем команды
        getCommand("reward").setExecutor(new RewardCommand());
        
        getLogger().info("VaultIntegrationPlugin загружен!");
    }
    
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }
    
    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        if (rsp == null) {
            return false;
        }
        permissions = rsp.getProvider();
        return permissions != null;
    }
    
    private class RewardCommand implements CommandExecutor {
        
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cЭта команда только для игроков!");
                return true;
            }
            
            Player player = (Player) sender;
            Arena arena = coconutLib.getArenaManager().getPlayerArena(player.getUniqueId());
            
            if (arena == null) {
                player.sendMessage("§cВы не находитесь на арене!");
                return true;
            }
            
            // Даем награду за участие
            double reward = 100.0;
            economy.depositPlayer(player, reward);
            player.sendMessage("§aВы получили награду: §e$" + reward);
            
            return true;
        }
    }
}
```

### Пример 6: Интеграция с PlaceholderAPI

```java
public class PlaceholderIntegrationPlugin extends JavaPlugin {
    
    private CoconutLib coconutLib;
    
    @Override
    public void onEnable() {
        coconutLib = (CoconutLib) getServer().getPluginManager().getPlugin("CoconutLib");
        
        // Регистрируем плейсхолдеры
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new ArenaPlaceholder(coconutLib).register();
            getLogger().info("PlaceholderAPI интеграция подключена!");
        } else {
            getLogger().warning("PlaceholderAPI не найден!");
        }
        
        getLogger().info("PlaceholderIntegrationPlugin загружен!");
    }
    
    public class ArenaPlaceholder extends PlaceholderExpansion {
        
        private final CoconutLib plugin;
        
        public ArenaPlaceholder(CoconutLib plugin) {
            this.plugin = plugin;
        }
        
        @Override
        public String getIdentifier() {
            return "coconutlib";
        }
        
        @Override
        public String getAuthor() {
            return "camp3rcraft";
        }
        
        @Override
        public String getVersion() {
            return "1.0.0";
        }
        
        @Override
        public String onPlaceholderRequest(Player player, String identifier) {
            if (player == null) {
                return "";
            }
            
            Arena arena = plugin.getArenaManager().getPlayerArena(player.getUniqueId());
            
            switch (identifier.toLowerCase()) {
                case "arena_name":
                    return arena != null ? arena.getName() : "Нет";
                case "arena_status":
                    return arena != null ? arena.getStatus().getDisplayName() : "Нет";
                case "arena_players":
                    return arena != null ? String.valueOf(arena.getPlayerCount()) : "0";
                case "arena_max_players":
                    return arena != null ? String.valueOf(arena.getMaxPlayers()) : "0";
                case "arena_min_players":
                    return arena != null ? String.valueOf(arena.getMinPlayers()) : "0";
                default:
                    return null;
            }
        }
    }
}
```

---

## Кастомизация

### Пример 7: Кастомная арена

```java
public class CustomArena extends Arena {
    private String gameType;
    private int score;
    private long gameStartTime;
    
    public CustomArena(String name, String worldName, ArenaStatus status, 
                      int maxPlayers, int minPlayers, Long createdAt) {
        super(name, worldName, status, maxPlayers, minPlayers, createdAt);
        this.gameType = "default";
        this.score = 0;
        this.gameStartTime = 0;
    }
    
    public String getGameType() {
        return gameType;
    }
    
    public void setGameType(String gameType) {
        this.gameType = gameType;
    }
    
    public int getScore() {
        return score;
    }
    
    public void setScore(int score) {
        this.score = score;
    }
    
    public long getGameStartTime() {
        return gameStartTime;
    }
    
    public void setGameStartTime(long gameStartTime) {
        this.gameStartTime = gameStartTime;
    }
    
    public long getGameDuration() {
        if (gameStartTime == 0) {
            return 0;
        }
        return System.currentTimeMillis() - gameStartTime;
    }
}
```

### Пример 8: Кастомный менеджер

```java
public class CustomArenaManager {
    
    private final CoconutLib coconutLib;
    private final ArenaManager arenaManager;
    private final Map<String, CustomArena> customArenas = new HashMap<>();
    
    public CustomArenaManager(CoconutLib coconutLib) {
        this.coconutLib = coconutLib;
        this.arenaManager = coconutLib.getArenaManager();
    }
    
    public boolean createCustomArena(String name, int maxPlayers, int minPlayers, String gameType) {
        // Создаем обычную арену
        boolean success = arenaManager.createArena(name, maxPlayers, minPlayers);
        if (!success) {
            return false;
        }
        
        // Создаем кастомную арену
        Arena baseArena = arenaManager.getArena(name);
        CustomArena customArena = new CustomArena(
            baseArena.getName(),
            baseArena.getWorldName(),
            baseArena.getStatus(),
            baseArena.getMaxPlayers(),
            baseArena.getMinPlayers(),
            baseArena.getCreatedAt()
        );
        customArena.setGameType(gameType);
        
        customArenas.put(name, customArena);
        return true;
    }
    
    public CustomArena getCustomArena(String name) {
        return customArenas.get(name);
    }
    
    public void startGame(String arenaName) {
        CustomArena arena = getCustomArena(arenaName);
        if (arena != null) {
            arena.setGameStartTime(System.currentTimeMillis());
            arenaManager.updateArenaStatus(arenaName, ArenaStatus.GAME);
        }
    }
    
    public void addScore(String arenaName, int points) {
        CustomArena arena = getCustomArena(arenaName);
        if (arena != null) {
            arena.setScore(arena.getScore() + points);
        }
    }
}
```

---

## Игровые механики

### Пример 9: Система команд

```java
public class TeamSystemPlugin extends JavaPlugin {
    
    private CoconutLib coconutLib;
    private Map<String, List<UUID>> teams = new HashMap<>();
    
    @Override
    public void onEnable() {
        coconutLib = (CoconutLib) getServer().getPluginManager().getPlugin("CoconutLib");
        
        // Регистрируем команды
        getCommand("team").setExecutor(new TeamCommand());
        
        getLogger().info("TeamSystemPlugin загружен!");
    }
    
    private class TeamCommand implements CommandExecutor {
        
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cЭта команда только для игроков!");
                return true;
            }
            
            Player player = (Player) sender;
            
            if (args.length == 0) {
                showHelp(player);
                return true;
            }
            
            switch (args[0].toLowerCase()) {
                case "create":
                    if (args.length < 2) {
                        player.sendMessage("§cИспользование: /team create <название>");
                        return true;
                    }
                    createTeam(player, args[1]);
                    break;
                    
                case "join":
                    if (args.length < 2) {
                        player.sendMessage("§cИспользование: /team join <название>");
                        return true;
                    }
                    joinTeam(player, args[1]);
                    break;
                    
                case "leave":
                    leaveTeam(player);
                    break;
                    
                case "list":
                    listTeams(player);
                    break;
                    
                default:
                    showHelp(player);
                    break;
            }
            
            return true;
        }
        
        private void showHelp(Player player) {
            player.sendMessage("§6=== Команды команд ===");
            player.sendMessage("§e/team create <название> §7- Создать команду");
            player.sendMessage("§e/team join <название> §7- Присоединиться к команде");
            player.sendMessage("§e/team leave §7- Покинуть команду");
            player.sendMessage("§e/team list §7- Список команд");
        }
        
        private void createTeam(Player player, String teamName) {
            if (teams.containsKey(teamName)) {
                player.sendMessage("§cКоманда §e" + teamName + " §cуже существует!");
                return;
            }
            
            teams.put(teamName, new ArrayList<>());
            teams.get(teamName).add(player.getUniqueId());
            
            player.sendMessage("§aКоманда §e" + teamName + " §aсоздана!");
        }
        
        private void joinTeam(Player player, String teamName) {
            if (!teams.containsKey(teamName)) {
                player.sendMessage("§cКоманда §e" + teamName + " §cне найдена!");
                return;
            }
            
            // Удаляем из других команд
            leaveTeam(player);
            
            // Добавляем в команду
            teams.get(teamName).add(player.getUniqueId());
            
            player.sendMessage("§aВы присоединились к команде §e" + teamName);
        }
        
        private void leaveTeam(Player player) {
            for (Map.Entry<String, List<UUID>> entry : teams.entrySet()) {
                if (entry.getValue().remove(player.getUniqueId())) {
                    player.sendMessage("§aВы покинули команду §e" + entry.getKey());
                    break;
                }
            }
        }
        
        private void listTeams(Player player) {
            if (teams.isEmpty()) {
                player.sendMessage("§7Нет созданных команд.");
                return;
            }
            
            player.sendMessage("§6=== Команды ===");
            for (Map.Entry<String, List<UUID>> entry : teams.entrySet()) {
                String teamName = entry.getKey();
                int playerCount = entry.getValue().size();
                player.sendMessage("§e" + teamName + " §7- " + playerCount + " игроков");
            }
        }
    }
}
```

### Пример 10: Система очков

```java
public class ScoreSystemPlugin extends JavaPlugin {
    
    private CoconutLib coconutLib;
    private Map<UUID, Integer> playerScores = new HashMap<>();
    private Map<String, Integer> arenaScores = new HashMap<>();
    
    @Override
    public void onEnable() {
        coconutLib = (CoconutLib) getServer().getPluginManager().getPlugin("CoconutLib");
        
        // Регистрируем события
        getServer().getPluginManager().registerEvents(new ScoreListener(), this);
        
        getLogger().info("ScoreSystemPlugin загружен!");
    }
    
    private class ScoreListener implements Listener {
        
        @EventHandler
        public void onPlayerKill(PlayerDeathEvent event) {
            Player victim = event.getEntity();
            Player killer = victim.getKiller();
            
            if (killer != null) {
                Arena arena = coconutLib.getArenaManager().getPlayerArena(killer.getUniqueId());
                if (arena != null) {
                    addScore(killer, 10);
                    killer.sendMessage("§a+10 очков за убийство!");
                }
            }
        }
        
        @EventHandler
        public void onPlayerJoinArena(PlayerJoinEvent event) {
            Arena arena = coconutLib.getArenaManager().getPlayerArena(event.getPlayer().getUniqueId());
            if (arena != null) {
                addScore(event.getPlayer(), 5);
                event.getPlayer().sendMessage("§a+5 очков за присоединение к арене!");
            }
        }
        
        private void addScore(Player player, int points) {
            UUID playerUUID = player.getUniqueId();
            int currentScore = playerScores.getOrDefault(playerUUID, 0);
            playerScores.put(playerUUID, currentScore + points);
            
            // Обновляем счет арены
            Arena arena = coconutLib.getArenaManager().getPlayerArena(playerUUID);
            if (arena != null) {
                String arenaName = arena.getName();
                int arenaScore = arenaScores.getOrDefault(arenaName, 0);
                arenaScores.put(arenaName, arenaScore + points);
            }
        }
    }
    
    public int getPlayerScore(UUID playerUUID) {
        return playerScores.getOrDefault(playerUUID, 0);
    }
    
    public int getArenaScore(String arenaName) {
        return arenaScores.getOrDefault(arenaName, 0);
    }
    
    public void resetScores() {
        playerScores.clear();
        arenaScores.clear();
    }
}
```

---

## Отладка и тестирование

### Пример 11: Отладочный плагин

```java
public class DebugPlugin extends JavaPlugin {
    
    private CoconutLib coconutLib;
    
    @Override
    public void onEnable() {
        coconutLib = (CoconutLib) getServer().getPluginManager().getPlugin("CoconutLib");
        
        // Регистрируем команды
        getCommand("debug").setExecutor(new DebugCommand());
        
        getLogger().info("DebugPlugin загружен!");
    }
    
    private class DebugCommand implements CommandExecutor {
        
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (args.length == 0) {
                showDebugInfo(sender);
                return true;
            }
            
            switch (args[0].toLowerCase()) {
                case "arenas":
                    showArenasInfo(sender);
                    break;
                case "players":
                    showPlayersInfo(sender);
                    break;
                case "memory":
                    showMemoryInfo(sender);
                    break;
                case "performance":
                    showPerformanceInfo(sender);
                    break;
                default:
                    showDebugInfo(sender);
                    break;
            }
            
            return true;
        }
        
        private void showDebugInfo(CommandSender sender) {
            sender.sendMessage("§6=== Отладочная информация ===");
            sender.sendMessage("§e/debug arenas §7- Информация об аренах");
            sender.sendMessage("§e/debug players §7- Информация об игроках");
            sender.sendMessage("§e/debug memory §7- Использование памяти");
            sender.sendMessage("§e/debug performance §7- Производительность");
        }
        
        private void showArenasInfo(CommandSender sender) {
            ArenaManager arenaManager = coconutLib.getArenaManager();
            List<Arena> arenas = arenaManager.getAllArenas();
            
            sender.sendMessage("§6=== Информация об аренах ===");
            sender.sendMessage("§7Всего арен: §e" + arenas.size());
            
            for (Arena arena : arenas) {
                sender.sendMessage("§e" + arena.getName() + ":");
                sender.sendMessage("  §7Статус: §e" + arena.getStatus());
                sender.sendMessage("  §7Игроки: §e" + arena.getPlayerCount() + "/" + arena.getMaxPlayers());
                sender.sendMessage("  §7Мир: §e" + arena.getWorldName());
            }
        }
        
        private void showPlayersInfo(CommandSender sender) {
            ArenaManager arenaManager = coconutLib.getArenaManager();
            List<Arena> arenas = arenaManager.getAllArenas();
            
            sender.sendMessage("§6=== Информация об игроках ===");
            
            int totalPlayers = 0;
            for (Arena arena : arenas) {
                totalPlayers += arena.getPlayerCount();
                sender.sendMessage("§e" + arena.getName() + " §7- §e" + arena.getPlayerCount() + " §7игроков");
            }
            
            sender.sendMessage("§7Всего игроков на аренах: §e" + totalPlayers);
        }
        
        private void showMemoryInfo(CommandSender sender) {
            Runtime runtime = Runtime.getRuntime();
            long totalMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();
            long usedMemory = totalMemory - freeMemory;
            
            sender.sendMessage("§6=== Использование памяти ===");
            sender.sendMessage("§7Использовано: §e" + formatBytes(usedMemory));
            sender.sendMessage("§7Свободно: §e" + formatBytes(freeMemory));
            sender.sendMessage("§7Всего: §e" + formatBytes(totalMemory));
            sender.sendMessage("§7Максимум: §e" + formatBytes(runtime.maxMemory()));
        }
        
        private void showPerformanceInfo(CommandSender sender) {
            sender.sendMessage("§6=== Производительность ===");
            sender.sendMessage("§7TPS: §e" + getTPS());
            sender.sendMessage("§7Загруженные чанки: §e" + getLoadedChunks());
            sender.sendMessage("§7Онлайн игроков: §e" + Bukkit.getOnlinePlayers().size());
        }
        
        private String formatBytes(long bytes) {
            if (bytes < 1024) return bytes + " B";
            int exp = (int) (Math.log(bytes) / Math.log(1024));
            String pre = "KMGTPE".charAt(exp-1) + "";
            return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
        }
        
        private String getTPS() {
            // Упрощенная реализация получения TPS
            return "20.0";
        }
        
        private int getLoadedChunks() {
            int total = 0;
            for (World world : Bukkit.getWorlds()) {
                total += world.getLoadedChunks().length;
            }
            return total;
        }
    }
}
```

### Пример 12: Тестовый плагин

```java
public class TestPlugin extends JavaPlugin {
    
    private CoconutLib coconutLib;
    private ArenaManager arenaManager;
    
    @Override
    public void onEnable() {
        coconutLib = (CoconutLib) getServer().getPluginManager().getPlugin("CoconutLib");
        arenaManager = coconutLib.getArenaManager();
        
        // Запускаем тесты
        runTests();
        
        getLogger().info("TestPlugin загружен!");
    }
    
    private void runTests() {
        getLogger().info("Запуск тестов CoconutLib...");
        
        // Тест 1: Создание арены
        testCreateArena();
        
        // Тест 2: Присоединение игрока
        testJoinArena();
        
        // Тест 3: Изменение статуса
        testStatusChange();
        
        // Тест 4: Удаление арены
        testDeleteArena();
        
        getLogger().info("Тесты завершены!");
    }
    
    private void testCreateArena() {
        getLogger().info("Тест: Создание арены");
        
        boolean result = arenaManager.createArena("test_arena", 10, 2);
        
        if (result) {
            Arena arena = arenaManager.getArena("test_arena");
            if (arena != null) {
                getLogger().info("✅ Арена создана успешно: " + arena.getName());
            } else {
                getLogger().warning("❌ Арена не найдена после создания");
            }
        } else {
            getLogger().warning("❌ Не удалось создать арену");
        }
    }
    
    private void testJoinArena() {
        getLogger().info("Тест: Присоединение к арене");
        
        // Создаем тестового игрока
        Player testPlayer = createTestPlayer();
        
        if (testPlayer != null) {
            boolean result = arenaManager.joinArena(testPlayer, "test_arena");
            
            if (result) {
                Arena arena = arenaManager.getPlayerArena(testPlayer.getUniqueId());
                if (arena != null) {
                    getLogger().info("✅ Игрок успешно присоединился к арене: " + arena.getName());
                } else {
                    getLogger().warning("❌ Игрок не найден на арене");
                }
            } else {
                getLogger().warning("❌ Не удалось присоединить игрока к арене");
            }
        } else {
            getLogger().warning("❌ Не удалось создать тестового игрока");
        }
    }
    
    private void testStatusChange() {
        getLogger().info("Тест: Изменение статуса арены");
        
        Arena arena = arenaManager.getArena("test_arena");
        if (arena != null) {
            ArenaStatus oldStatus = arena.getStatus();
            arenaManager.updateArenaStatus("test_arena", ArenaStatus.GAME);
            
            Arena updatedArena = arenaManager.getArena("test_arena");
            if (updatedArena != null && updatedArena.getStatus() == ArenaStatus.GAME) {
                getLogger().info("✅ Статус арены изменен: " + oldStatus + " -> " + updatedArena.getStatus());
            } else {
                getLogger().warning("❌ Статус арены не изменился");
            }
        } else {
            getLogger().warning("❌ Арена не найдена для теста статуса");
        }
    }
    
    private void testDeleteArena() {
        getLogger().info("Тест: Удаление арены");
        
        boolean result = arenaManager.deleteArena("test_arena");
        
        if (result) {
            Arena arena = arenaManager.getArena("test_arena");
            if (arena == null) {
                getLogger().info("✅ Арена успешно удалена");
            } else {
                getLogger().warning("❌ Арена все еще существует после удаления");
            }
        } else {
            getLogger().warning("❌ Не удалось удалить арену");
        }
    }
    
    private Player createTestPlayer() {
        // Создаем мок игрока для тестирования
        // В реальном коде здесь была бы более сложная логика
        return null;
    }
}
```

---

## Заключение

Эти примеры демонстрируют различные способы использования CoconutLib для создания сложных мини-игр и плагинов. Библиотека предоставляет гибкий API, который можно легко расширять и кастомизировать под конкретные потребности.

### Основные принципы

1. **Модульность** - Разделяйте функциональность на отдельные компоненты
2. **Переиспользование** - Используйте существующие менеджеры CoconutLib
3. **Расширяемость** - Создавайте кастомные классы для специфичной логики
4. **Тестирование** - Всегда тестируйте функциональность перед релизом
5. **Документация** - Поддерживайте актуальную документацию

### Следующие шаги

1. Изучите API документацию
2. Создайте свой первый плагин с CoconutLib
3. Экспериментируйте с кастомизацией
4. Поделитесь своими примерами с сообществом

---

*CoconutLib - Удобное создание арен для Minecraft плагинов* 