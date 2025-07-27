package com.camp3rcraft.coconutlib.tab;

import com.camp3rcraft.coconutlib.CoconutLib;
import com.camp3rcraft.coconutlib.arena.Arena;
import com.camp3rcraft.coconutlib.utils.ColorUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TabManager {
    
    private final CoconutLib plugin;
    private final Map<UUID, TabInfo> playerTabs;
    private final MiniMessage miniMessage;
    
    public TabManager(CoconutLib plugin) {
        this.plugin = plugin;
        this.playerTabs = new HashMap<>();
        this.miniMessage = MiniMessage.miniMessage();
    }
    
    public void updatePlayerTab(Player player, Arena arena) {
        if (!plugin.getConfigManager().getBoolean("tab.enabled", true)) {
            return;
        }
        
        String header = plugin.getConfigManager().getString("tab.header", "§6§lАРЕНА");
        String footer = plugin.getConfigManager().getString("tab.footer", "§7Игроков на арене: §e%players%")
                           .replace("%players%", String.valueOf(arena.getPlayerCount()));
        
        // Получаем список игроков на арене из объекта арены
        List<UUID> arenaPlayers = arena.getPlayers();
        
        // Формируем список игроков для таба
        StringBuilder playerList = new StringBuilder();
        for (int i = 0; i < arenaPlayers.size(); i++) {
            if (i > 0) {
                playerList.append("\n");
            }
            Player arenaPlayer = Bukkit.getPlayer(arenaPlayers.get(i));
            if (arenaPlayer != null) {
                playerList.append("§e").append(arenaPlayer.getName());
            }
        }
        
        // Создаем компоненты для header и footer
        Component headerComponent = miniMessage.deserialize(ColorUtils.colorize(header));
        Component footerComponent = miniMessage.deserialize(ColorUtils.colorize(footer + "\n\n" + playerList.toString()));
        
        // Устанавливаем таб
        player.sendPlayerListHeader(headerComponent);
        player.sendPlayerListFooter(footerComponent);
        
        // Сохраняем информацию о табе
        playerTabs.put(player.getUniqueId(), new TabInfo(arena.getName(), header, footer));
    }
    
    public void removePlayerTab(Player player) {
        if (playerTabs.containsKey(player.getUniqueId())) {
            // Устанавливаем пустой таб
            Component emptyComponent = Component.empty();
            player.sendPlayerListHeader(emptyComponent);
            player.sendPlayerListFooter(emptyComponent);
            
            playerTabs.remove(player.getUniqueId());
        }
    }
    
    public void updateAllArenaTabs(Arena arena) {
        for (UUID playerUUID : arena.getPlayers()) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player != null) {
                updatePlayerTab(player, arena);
            }
        }
    }
    
    public void clearAllTabs() {
        for (UUID playerUUID : playerTabs.keySet()) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player != null) {
                removePlayerTab(player);
            }
        }
        playerTabs.clear();
    }
    
    public boolean hasTab(UUID playerUUID) {
        return playerTabs.containsKey(playerUUID);
    }
    
    public TabInfo getPlayerTabInfo(UUID playerUUID) {
        return playerTabs.get(playerUUID);
    }
    
    /**
     * Класс для хранения информации о табе игрока
     */
    private static class TabInfo {
        private final String arenaName;
        private final String header;
        private final String footer;
        
        public TabInfo(String arenaName, String header, String footer) {
            this.arenaName = arenaName;
            this.header = header;
            this.footer = footer;
        }
        
        public String getArenaName() {
            return arenaName;
        }
        
        public String getHeader() {
            return header;
        }
        
        public String getFooter() {
            return footer;
        }
    }
} 