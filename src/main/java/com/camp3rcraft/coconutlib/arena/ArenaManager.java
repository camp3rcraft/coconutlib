package com.camp3rcraft.coconutlib.arena;

import com.camp3rcraft.coconutlib.CoconutLib;
import com.camp3rcraft.coconutlib.inventory.InventoryManager;
import com.camp3rcraft.coconutlib.scoreboard.ScoreboardManager;
import com.camp3rcraft.coconutlib.tab.TabManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ArenaManager {
    
    private final CoconutLib plugin;
    private final Map<String, Arena> arenas;
    private final InventoryManager inventoryManager;
    
    public ArenaManager(CoconutLib plugin) {
        this.plugin = plugin;
        this.arenas = new HashMap<>();
        this.inventoryManager = new InventoryManager(plugin);
        plugin.getLogger().info("ArenaManager инициализирован");
    }
    
    public boolean createArena(String name, int maxPlayers, int minPlayers) {
        if (arenas.containsKey(name)) {
            return false;
        }
        
        String worldName = "arena_" + name.toLowerCase();
        
        // Создаем мир
        if (plugin.getWorldManager().createArenaWorld(worldName) == null) {
            return false;
        }
        
        // Создаем объект арены
        Arena arena = new Arena(name, worldName, ArenaStatus.LOBBY, maxPlayers, minPlayers, null);
        arenas.put(name, arena);
        
        plugin.getLogger().info("Арена " + name + " создана успешно!");
        return true;
    }
    
    public boolean deleteArena(String name) {
        Arena arena = arenas.get(name);
        if (arena == null) {
            return false;
        }
        
        // Удаляем мир
        plugin.getWorldManager().deleteArenaWorld(arena.getWorldName());
        
        // Удаляем из памяти
        arenas.remove(name);
        
        plugin.getLogger().info("Арена " + name + " удалена успешно!");
        return true;
    }
    
    public boolean joinArena(Player player, String arenaName) {
        Arena arena = arenas.get(arenaName);
        if (arena == null) {
            return false;
        }
        
        if (arena.isFull()) {
            player.sendMessage(plugin.getConfigManager().getString("messages.prefix") + 
                            "§cАрена полная!");
            return false;
        }
        
        // Сохраняем инвентарь игрока
        if (plugin.getConfigManager().getBoolean("inventory.save_on_join", true)) {
            inventoryManager.savePlayerInventory(player);
        }
        
        // Очищаем инвентарь
        player.getInventory().clear();
        
        // Добавляем игрока в арену
        arena.addPlayer(player.getUniqueId());
        
        // Телепортируем в мир арены
        plugin.getWorldManager().teleportToArena(player, arena.getWorldName());
        
        // Обновляем скорборд и таб
        updateArenaDisplays(arena);
        
        player.sendMessage(plugin.getConfigManager().getString("messages.arena_joined")
                         .replace("%arena%", arenaName));
        
        return true;
    }
    
    public boolean leaveArena(Player player, String arenaName) {
        Arena arena = arenas.get(arenaName);
        if (arena == null) {
            return false;
        }
        
        if (!arena.hasPlayer(player.getUniqueId())) {
            return false;
        }
        
        // Удаляем игрока из арены
        arena.removePlayer(player.getUniqueId());
        
        // Очищаем инвентарь
        if (plugin.getConfigManager().getBoolean("inventory.clear_on_leave", true)) {
            player.getInventory().clear();
        }
        
        // Восстанавливаем инвентарь
        inventoryManager.restorePlayerInventory(player);
        
        // Телепортируем в лобби
        org.bukkit.World lobby = Bukkit.getWorlds().get(0);
        player.teleport(lobby.getSpawnLocation());
        
        // Обновляем скорборд и таб
        updateArenaDisplays(arena);
        
        player.sendMessage(plugin.getConfigManager().getString("messages.arena_left")
                         .replace("%arena%", arenaName));
        
        return true;
    }
    
    public boolean regenerateArena(String name) {
        Arena arena = arenas.get(name);
        if (arena == null) {
            return false;
        }
        
        // Регенерируем мир
        if (!plugin.getWorldManager().regenerateArenaWorld(arena.getWorldName())) {
            return false;
        }
        
        // Очищаем список игроков
        arena.getPlayers().clear();
        
        // Обновляем статус
        arena.setStatus(ArenaStatus.LOBBY);
        
        plugin.getLogger().info("Арена " + name + " регенерирована!");
        return true;
    }
    
    public void updateArenaStatus(String arenaName, ArenaStatus status) {
        Arena arena = arenas.get(arenaName);
        if (arena != null) {
            arena.setStatus(status);
            updateArenaDisplays(arena);
        }
    }
    
    private void updateArenaDisplays(Arena arena) {
        // Обновляем скорборд для всех игроков арены
        if (plugin.getConfigManager().getBoolean("scoreboard.enabled", true)) {
            for (UUID playerUUID : arena.getPlayers()) {
                Player player = Bukkit.getPlayer(playerUUID);
                if (player != null) {
                    plugin.getScoreboardManager().updatePlayerScoreboard(player, arena);
                }
            }
        }
        
        // Обновляем таб для всех игроков арены
        if (plugin.getConfigManager().getBoolean("tab.enabled", true)) {
            for (UUID playerUUID : arena.getPlayers()) {
                Player player = Bukkit.getPlayer(playerUUID);
                if (player != null) {
                    plugin.getTabManager().updatePlayerTab(player, arena);
                }
            }
        }
    }
    
    public Arena getArena(String name) {
        return arenas.get(name);
    }
    
    public List<Arena> getAllArenas() {
        return List.copyOf(arenas.values());
    }
    
    public Arena getPlayerArena(UUID playerUUID) {
        for (Arena arena : arenas.values()) {
            if (arena.hasPlayer(playerUUID)) {
                return arena;
            }
        }
        return null;
    }
    
    public void shutdown() {
        // Очищаем все арены при выключении
        arenas.clear();
    }
} 