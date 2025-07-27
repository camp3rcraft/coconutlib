package com.camp3rcraft.coconutlib.scoreboard;

import com.camp3rcraft.coconutlib.CoconutLib;
import com.camp3rcraft.coconutlib.arena.Arena;
import com.camp3rcraft.coconutlib.arena.ArenaStatus;
import com.camp3rcraft.coconutlib.utils.ColorUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ScoreboardManager {
    
    private final CoconutLib plugin;
    private final Map<UUID, Scoreboard> playerScoreboards;
    private final MiniMessage miniMessage;
    
    public ScoreboardManager(CoconutLib plugin) {
        this.plugin = plugin;
        this.playerScoreboards = new HashMap<>();
        this.miniMessage = MiniMessage.miniMessage();
    }
    
    public void updatePlayerScoreboard(Player player, Arena arena) {
        if (!plugin.getConfigManager().getBoolean("scoreboard.enabled", true)) {
            return;
        }
        
        org.bukkit.scoreboard.ScoreboardManager bukkitManager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = bukkitManager.getNewScoreboard();
        
        // Создаем объектив
        Objective objective = scoreboard.registerNewObjective("arena", "dummy", 
            ColorUtils.colorize(plugin.getConfigManager().getString("scoreboard.title", "§6§lАРЕНА")));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        
        // Получаем статус арены
        ArenaStatus status = arena.getStatus();
        String statusText = getStatusText(status);
        
        // Добавляем строки скорборда
        int score = 15;
        
        // Пустая строка
        Score emptyLine1 = objective.getScore("§1");
        emptyLine1.setScore(score--);
        
        // Название арены
        Score arenaName = objective.getScore("§7Арена: §e" + arena.getName());
        arenaName.setScore(score--);
        
        // Статус
        Score statusScore = objective.getScore("§7Статус: " + statusText);
        statusScore.setScore(score--);
        
        // Пустая строка
        Score emptyLine2 = objective.getScore("§2");
        emptyLine2.setScore(score--);
        
        // Игроки
        Score players = objective.getScore("§7Игроки: §e" + arena.getPlayerCount() + "§7/§e" + arena.getMaxPlayers());
        players.setScore(score--);
        
        // Пустая строка
        Score emptyLine3 = objective.getScore("§3");
        emptyLine3.setScore(score--);
        
        // Дополнительная информация в зависимости от статуса
        switch (status) {
            case LOBBY:
                Score lobbyInfo = objective.getScore("§aОжидаем игроков...");
                lobbyInfo.setScore(score--);
                break;
            case WAITING:
                Score waitingInfo = objective.getScore("§eНужно игроков: §f" + (arena.getMinPlayers() - arena.getPlayerCount()));
                waitingInfo.setScore(score--);
                break;
            case COUNTDOWN:
                Score countdownInfo = objective.getScore("§6Игра начнется скоро!");
                countdownInfo.setScore(score--);
                break;
            case GAME:
                Score gameInfo = objective.getScore("§cИгра в процессе!");
                gameInfo.setScore(score--);
                break;
            case END:
                Score endInfo = objective.getScore("§4Игра завершена!");
                endInfo.setScore(score--);
                break;
        }
        
        // Время
        Score timeScore = objective.getScore("§7Время: §e" + getCurrentTime());
        timeScore.setScore(score--);
        
        // Пустая строка
        Score emptyLine4 = objective.getScore("§4");
        emptyLine4.setScore(score--);
        
        // Устанавливаем скорборд игроку
        player.setScoreboard(scoreboard);
        
        // Сохраняем скорборд
        playerScoreboards.put(player.getUniqueId(), scoreboard);
    }
    
    public void removePlayerScoreboard(Player player) {
        if (playerScoreboards.containsKey(player.getUniqueId())) {
            // Устанавливаем пустой скорборд
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            playerScoreboards.remove(player.getUniqueId());
        }
    }
    
    public void updateAllArenaScoreboards(Arena arena) {
        for (UUID playerUUID : arena.getPlayers()) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player != null) {
                updatePlayerScoreboard(player, arena);
            }
        }
    }
    
    private String getStatusText(ArenaStatus status) {
        switch (status) {
            case LOBBY:
                return plugin.getConfigManager().getString("scoreboard.lobby", "§aЛобби");
            case WAITING:
                return plugin.getConfigManager().getString("scoreboard.waiting", "§eОжидание игроков");
            case COUNTDOWN:
                return plugin.getConfigManager().getString("scoreboard.countdown", "§6Ожидание начала");
            case GAME:
                return plugin.getConfigManager().getString("scoreboard.game", "§cИгра");
            case END:
                return plugin.getConfigManager().getString("scoreboard.end", "§4Конец");
            default:
                return "§7Неизвестно";
        }
    }
    
    private String getCurrentTime() {
        return java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
    }
    
    public void clearAllScoreboards() {
        for (UUID playerUUID : playerScoreboards.keySet()) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player != null) {
                removePlayerScoreboard(player);
            }
        }
        playerScoreboards.clear();
    }
    
    public boolean hasScoreboard(UUID playerUUID) {
        return playerScoreboards.containsKey(playerUUID);
    }
} 