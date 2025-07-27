package com.camp3rcraft.coconutlib.config;

import com.camp3rcraft.coconutlib.CoconutLib;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigManager {
    
    private final CoconutLib plugin;
    private FileConfiguration config;
    private File configFile;
    
    public ConfigManager(CoconutLib plugin) {
        this.plugin = plugin;
        loadConfig();
    }
    
    public void loadConfig() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
        
        configFile = new File(plugin.getDataFolder(), "config.yml");
        
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }
        
        config = YamlConfiguration.loadConfiguration(configFile);
        setDefaults();
    }
    
    private void setDefaults() {
        config.addDefault("worlds.default_environment", "NORMAL");
        config.addDefault("worlds.seed", 0L);
        config.addDefault("worlds.generate_structures", false);
        
        config.addDefault("scoreboard.enabled", true);
        config.addDefault("scoreboard.title", "§6§lАРЕНА");
        config.addDefault("scoreboard.lobby", "§aЛобби");
        config.addDefault("scoreboard.waiting", "§eОжидание игроков");
        config.addDefault("scoreboard.countdown", "§6Ожидание начала");
        config.addDefault("scoreboard.game", "§cИгра");
        config.addDefault("scoreboard.end", "§4Конец");
        
        config.addDefault("tab.enabled", true);
        config.addDefault("tab.header", "§6§lАРЕНА");
        config.addDefault("tab.footer", "§7Игроков на арене: §e%players%");
        
        config.addDefault("inventory.clear_on_leave", true);
        config.addDefault("inventory.save_on_join", true);
        
        config.addDefault("messages.prefix", "§8[§6CoconutLib§8] §r");
        config.addDefault("messages.arena_created", "§aАрена §e%arena% §aсоздана!");
        config.addDefault("messages.arena_deleted", "§cАрена §e%arena% §cудалена!");
        config.addDefault("messages.arena_joined", "§aВы присоединились к арене §e%arena%");
        config.addDefault("messages.arena_left", "§cВы покинули арену §e%arena%");
        config.addDefault("messages.arena_not_found", "§cАрена §e%arena% §cне найдена!");
        config.addDefault("messages.arena_already_exists", "§cАрена §e%arena% §cуже существует!");
        config.addDefault("messages.arena_regenerated", "§aАрена §e%arena% §aрегенерирована!");
        
        config.options().copyDefaults(true);
        saveConfig();
    }
    
    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Не удалось сохранить конфигурацию: " + e.getMessage());
        }
    }
    
    public void reloadConfig() {
        loadConfig();
    }
    
    public FileConfiguration getConfig() {
        return config;
    }
    
    public String getString(String path) {
        return config.getString(path, "");
    }
    
    public String getString(String path, String def) {
        return config.getString(path, def);
    }
    
    public int getInt(String path) {
        return config.getInt(path, 0);
    }
    
    public int getInt(String path, int def) {
        return config.getInt(path, def);
    }
    
    public boolean getBoolean(String path) {
        return config.getBoolean(path, false);
    }
    
    public boolean getBoolean(String path, boolean def) {
        return config.getBoolean(path, def);
    }
    
    public long getLong(String path) {
        return config.getLong(path, 0L);
    }
    
    public long getLong(String path, long def) {
        return config.getLong(path, def);
    }
} 