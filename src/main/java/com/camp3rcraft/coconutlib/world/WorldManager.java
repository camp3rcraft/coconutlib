package com.camp3rcraft.coconutlib.world;

import com.camp3rcraft.coconutlib.CoconutLib;
import org.bukkit.*;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;

import java.io.File;
import java.util.Random;

public class WorldManager {
    
    private final CoconutLib plugin;
    
    public WorldManager(CoconutLib plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Создает новый мир для арены
     * @param worldName Имя мира
     * @return Созданный мир или null если ошибка
     */
    public World createArenaWorld(String worldName) {
        if (Bukkit.getWorld(worldName) != null) {
            plugin.getLogger().warning("Мир " + worldName + " уже существует!");
            return null;
        }
        
        WorldCreator creator = new WorldCreator(worldName);
        creator.environment(World.Environment.valueOf(
            plugin.getConfigManager().getString("worlds.default_environment", "NORMAL")
        ));
        creator.seed(plugin.getConfigManager().getLong("worlds.seed", 0L));
        creator.generateStructures(plugin.getConfigManager().getBoolean("worlds.generate_structures", false));
        creator.generator(new VoidGenerator());
        
        try {
            World world = creator.createWorld();
            if (world != null) {
                world.setAutoSave(false);
                world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
                world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
                world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
                world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
                world.setGameRule(GameRule.DO_FIRE_TICK, false);
                world.setGameRule(GameRule.DO_TILE_DROPS, false);
                world.setGameRule(GameRule.KEEP_INVENTORY, true);
                world.setGameRule(GameRule.NATURAL_REGENERATION, false);
                
                plugin.getLogger().info("Мир " + worldName + " создан успешно!");
                return world;
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Ошибка при создании мира " + worldName + ": " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Удаляет мир арены
     * @param worldName Имя мира
     * @return true если мир удален успешно
     */
    public boolean deleteArenaWorld(String worldName) {
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            plugin.getLogger().warning("Мир " + worldName + " не найден!");
            return false;
        }
        
        // Телепортируем всех игроков в лобби
        world.getPlayers().forEach(player -> {
            World lobby = Bukkit.getWorlds().get(0);
            player.teleport(lobby.getSpawnLocation());
        });
        
        // Выгружаем мир
        if (!Bukkit.unloadWorld(world, false)) {
            plugin.getLogger().severe("Не удалось выгрузить мир " + worldName);
            return false;
        }
        
        // Удаляем файлы мира
        File worldFolder = new File(Bukkit.getWorldContainer(), worldName);
        if (worldFolder.exists()) {
            deleteDirectory(worldFolder);
            plugin.getLogger().info("Мир " + worldName + " удален успешно!");
            return true;
        }
        
        return false;
    }
    
    /**
     * Регенерирует мир арены
     * @param worldName Имя мира
     * @return true если мир регенерирован успешно
     */
    public boolean regenerateArenaWorld(String worldName) {
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            plugin.getLogger().warning("Мир " + worldName + " не найден!");
            return false;
        }
        
        // Телепортируем всех игроков в лобби
        world.getPlayers().forEach(player -> {
            World lobby = Bukkit.getWorlds().get(0);
            player.teleport(lobby.getSpawnLocation());
        });
        
        // Выгружаем мир
        if (!Bukkit.unloadWorld(world, false)) {
            plugin.getLogger().severe("Не удалось выгрузить мир " + worldName);
            return false;
        }
        
        // Удаляем файлы мира
        File worldFolder = new File(Bukkit.getWorldContainer(), worldName);
        if (worldFolder.exists()) {
            deleteDirectory(worldFolder);
        }
        
        // Создаем мир заново
        World newWorld = createArenaWorld(worldName);
        return newWorld != null;
    }
    
    /**
     * Телепортирует игрока в мир арены
     * @param player Игрок
     * @param worldName Имя мира
     * @return true если телепортация успешна
     */
    public boolean teleportToArena(org.bukkit.entity.Player player, String worldName) {
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            plugin.getLogger().warning("Мир " + worldName + " не найден!");
            return false;
        }
        
        Location spawnLocation = world.getSpawnLocation();
        if (spawnLocation.getBlock().getType().isSolid()) {
            // Ищем безопасное место для спавна
            spawnLocation = findSafeLocation(world);
        }
        
        player.teleport(spawnLocation);
        return true;
    }
    
    /**
     * Находит безопасное место для спавна в мире
     * @param world Мир
     * @return Безопасная локация
     */
    private Location findSafeLocation(World world) {
        Location spawn = world.getSpawnLocation();
        int x = spawn.getBlockX();
        int z = spawn.getBlockZ();
        
        for (int y = 0; y < world.getMaxHeight(); y++) {
            Location loc = new Location(world, x, y, z);
            if (loc.getBlock().getType().isAir() && 
                loc.clone().add(0, 1, 0).getBlock().getType().isAir()) {
                return loc;
            }
        }
        
        return new Location(world, x, 64, z);
    }
    
    /**
     * Рекурсивно удаляет директорию
     * @param directory Директория для удаления
     */
    private void deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
            directory.delete();
        }
    }
    
    /**
     * Генератор пустого мира
     */
    private static class VoidGenerator extends ChunkGenerator {
        public void generateSurface(WorldInfo worldInfo, Random random, int x, int z, ChunkData chunkData) {
            // Пустой мир
        }
        
        public void buildSurface(WorldInfo worldInfo, Random random, int x, int z, ChunkData chunkData) {
            // Пустой мир
        }
    }
} 