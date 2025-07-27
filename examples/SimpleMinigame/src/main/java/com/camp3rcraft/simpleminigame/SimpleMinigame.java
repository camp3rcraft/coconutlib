package com.camp3rcraft.simpleminigame;

import com.camp3rcraft.coconutlib.CoconutLib;
import com.camp3rcraft.coconutlib.arena.Arena;
import com.camp3rcraft.coconutlib.arena.ArenaStatus;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class SimpleMinigame extends JavaPlugin {
    
    private CoconutLib coconutLib;
    private String currentArena = "simple_game";
    
    @Override
    public void onEnable() {
        // Получаем экземпляр CoconutLib
        coconutLib = (CoconutLib) getServer().getPluginManager().getPlugin("CoconutLib");
        
        if (coconutLib == null) {
            getLogger().severe("CoconutLib не найден! Плагин будет отключен.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        // Создаем арену если её нет
        if (coconutLib.getArenaManager().getArena(currentArena) == null) {
            coconutLib.getArenaManager().createArena(currentArena, 8, 2);
            getLogger().info("Арена " + currentArena + " создана!");
        }
        
        // Запускаем игровой цикл
        startGameLoop();
        
        getLogger().info("SimpleMinigame успешно загружен!");
    }
    
    @Override
    public void onDisable() {
        getLogger().info("SimpleMinigame выключен!");
    }
    
    private void startGameLoop() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Arena arena = coconutLib.getArenaManager().getArena(currentArena);
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
            }
        }.runTaskTimer(this, 20L, 20L); // Каждую секунду
    }
    
    private void handleLobby(Arena arena) {
        if (arena.getPlayerCount() >= arena.getMinPlayers()) {
            coconutLib.getArenaManager().updateArenaStatus(currentArena, ArenaStatus.WAITING);
            broadcastToArena(arena, "§aДостаточно игроков! Игра начнется через 10 секунд!");
        }
    }
    
    private void handleWaiting(Arena arena) {
        if (arena.getPlayerCount() < arena.getMinPlayers()) {
            coconutLib.getArenaManager().updateArenaStatus(currentArena, ArenaStatus.LOBBY);
            broadcastToArena(arena, "§cНедостаточно игроков! Возвращаемся в лобби.");
        } else {
            coconutLib.getArenaManager().updateArenaStatus(currentArena, ArenaStatus.COUNTDOWN);
            broadcastToArena(arena, "§6Обратный отсчет начинается!");
            startCountdown(arena);
        }
    }
    
    private void handleCountdown(Arena arena) {
        // Обратный отсчет обрабатывается в startCountdown
    }
    
    private void handleGame(Arena arena) {
        // Простая игра - игроки должны выжить
        if (arena.getPlayerCount() <= 1) {
            coconutLib.getArenaManager().updateArenaStatus(currentArena, ArenaStatus.END);
            broadcastToArena(arena, "§4Игра завершена!");
        }
    }
    
    private void handleEnd(Arena arena) {
        // Через 5 секунд возвращаемся в лобби
        new BukkitRunnable() {
            @Override
            public void run() {
                coconutLib.getArenaManager().updateArenaStatus(currentArena, ArenaStatus.LOBBY);
                broadcastToArena(arena, "§aВозвращаемся в лобби!");
            }
        }.runTaskLater(this, 100L); // 5 секунд
    }
    
    private void startCountdown(Arena arena) {
        new BukkitRunnable() {
            int countdown = 10;
            
            @Override
            public void run() {
                if (countdown > 0) {
                    broadcastToArena(arena, "§6Игра начнется через §e" + countdown + " §6секунд!");
                    countdown--;
                } else {
                    coconutLib.getArenaManager().updateArenaStatus(currentArena, ArenaStatus.GAME);
                    broadcastToArena(arena, "§a§lИГРА НАЧАЛАСЬ!");
                    
                    // Даем игрокам инвентарь
                    for (UUID playerUUID : arena.getPlayers()) {
                        Player player = Bukkit.getPlayer(playerUUID);
                        if (player != null) {
                            giveGameInventory(player);
                        }
                    }
                }
            }
        }.runTaskTimer(this, 20L, 20L);
    }
    
    private void giveGameInventory(Player player) {
        player.getInventory().clear();
        // Здесь можно дать игрокам специальный инвентарь для игры
        player.sendMessage("§aВы получили игровой инвентарь!");
    }
    
    private void broadcastToArena(Arena arena, String message) {
        for (UUID playerUUID : arena.getPlayers()) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player != null) {
                player.sendMessage(message);
            }
        }
    }
} 