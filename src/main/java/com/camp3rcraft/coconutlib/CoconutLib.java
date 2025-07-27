package com.camp3rcraft.coconutlib;

import com.camp3rcraft.coconutlib.arena.ArenaManager;
import com.camp3rcraft.coconutlib.commands.ArenaCommand;
import com.camp3rcraft.coconutlib.config.ConfigManager;
import com.camp3rcraft.coconutlib.scoreboard.ScoreboardManager;
import com.camp3rcraft.coconutlib.tab.TabManager;
import com.camp3rcraft.coconutlib.world.WorldManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CoconutLib extends JavaPlugin {

    private static CoconutLib instance;
    private ArenaManager arenaManager;
    private WorldManager worldManager;
    private ScoreboardManager scoreboardManager;
    private TabManager tabManager;
    private ConfigManager configManager;

    @Override
    public void onEnable() {
        instance = this;

        // Инициализация менеджеров
        this.configManager = new ConfigManager(this);
        this.worldManager = new WorldManager(this);
        this.arenaManager = new ArenaManager(this);
        this.scoreboardManager = new ScoreboardManager(this);
        this.tabManager = new TabManager(this);

        // Регистрация команд
        getCommand("arena").setExecutor(new ArenaCommand(this));

        getLogger().info("CoconutLib успешно загружен!");
    }

    @Override
    public void onDisable() {
        if (arenaManager != null) {
            arenaManager.shutdown();
        }
        getLogger().info("CoconutLib выключен!");
    }

    public static CoconutLib getInstance() {
        return instance;
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public WorldManager getWorldManager() {
        return worldManager;
    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    public TabManager getTabManager() {
        return tabManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }
} 