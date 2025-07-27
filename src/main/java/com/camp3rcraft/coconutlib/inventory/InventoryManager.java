package com.camp3rcraft.coconutlib.inventory;

import com.camp3rcraft.coconutlib.CoconutLib;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InventoryManager {
    
    private final CoconutLib plugin;
    private final Map<UUID, SavedInventory> savedInventories;
    private final File inventoryFile;
    private FileConfiguration inventoryConfig;
    
    public InventoryManager(CoconutLib plugin) {
        this.plugin = plugin;
        this.savedInventories = new HashMap<>();
        this.inventoryFile = new File(plugin.getDataFolder(), "inventories.yml");
        loadInventories();
    }
    
    private void loadInventories() {
        if (!inventoryFile.exists()) {
            plugin.saveResource("inventories.yml", false);
        }
        inventoryConfig = YamlConfiguration.loadConfiguration(inventoryFile);
    }
    
    public void savePlayerInventory(Player player) {
        UUID playerUUID = player.getUniqueId();
        PlayerInventory inventory = player.getInventory();
        
        SavedInventory savedInventory = new SavedInventory();
        
        // Сохраняем основной инвентарь
        savedInventory.setContents(inventory.getContents());
        
        // Сохраняем броню
        savedInventory.setArmorContents(inventory.getArmorContents());
        
        // Сохраняем оффхенд
        savedInventory.setOffHand(inventory.getItemInOffHand());
        
        // Сохраняем в память
        savedInventories.put(playerUUID, savedInventory);
        
        // Сохраняем в файл
        saveToFile(playerUUID, savedInventory);
    }
    
    public void restorePlayerInventory(Player player) {
        UUID playerUUID = player.getUniqueId();
        SavedInventory savedInventory = savedInventories.get(playerUUID);
        
        if (savedInventory == null) {
            // Пытаемся загрузить из файла
            savedInventory = loadFromFile(playerUUID);
        }
        
        if (savedInventory != null) {
            PlayerInventory inventory = player.getInventory();
            
            // Восстанавливаем основной инвентарь
            inventory.setContents(savedInventory.getContents());
            
            // Восстанавливаем броню
            inventory.setArmorContents(savedInventory.getArmorContents());
            
            // Восстанавливаем оффхенд
            inventory.setItemInOffHand(savedInventory.getOffHand());
            
            // Удаляем из памяти
            savedInventories.remove(playerUUID);
            
            // Удаляем из файла
            removeFromFile(playerUUID);
        }
    }
    
    private void saveToFile(UUID playerUUID, SavedInventory savedInventory) {
        String path = "inventories." + playerUUID.toString();
        
        // Сохраняем основной инвентарь
        for (int i = 0; i < savedInventory.getContents().length; i++) {
            ItemStack item = savedInventory.getContents()[i];
            if (item != null) {
                inventoryConfig.set(path + ".contents." + i, item);
            }
        }
        
        // Сохраняем броню
        for (int i = 0; i < savedInventory.getArmorContents().length; i++) {
            ItemStack item = savedInventory.getArmorContents()[i];
            if (item != null) {
                inventoryConfig.set(path + ".armor." + i, item);
            }
        }
        
        // Сохраняем оффхенд
        if (savedInventory.getOffHand() != null) {
            inventoryConfig.set(path + ".offhand", savedInventory.getOffHand());
        }
        
        try {
            inventoryConfig.save(inventoryFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Не удалось сохранить инвентарь игрока: " + e.getMessage());
        }
    }
    
    private SavedInventory loadFromFile(UUID playerUUID) {
        String path = "inventories." + playerUUID.toString();
        
        if (!inventoryConfig.contains(path)) {
            return null;
        }
        
        SavedInventory savedInventory = new SavedInventory();
        
        // Загружаем основной инвентарь
        ItemStack[] contents = new ItemStack[36];
        for (int i = 0; i < 36; i++) {
            if (inventoryConfig.contains(path + ".contents." + i)) {
                contents[i] = inventoryConfig.getItemStack(path + ".contents." + i);
            }
        }
        savedInventory.setContents(contents);
        
        // Загружаем броню
        ItemStack[] armorContents = new ItemStack[4];
        for (int i = 0; i < 4; i++) {
            if (inventoryConfig.contains(path + ".armor." + i)) {
                armorContents[i] = inventoryConfig.getItemStack(path + ".armor." + i);
            }
        }
        savedInventory.setArmorContents(armorContents);
        
        // Загружаем оффхенд
        if (inventoryConfig.contains(path + ".offhand")) {
            savedInventory.setOffHand(inventoryConfig.getItemStack(path + ".offhand"));
        }
        
        return savedInventory;
    }
    
    private void removeFromFile(UUID playerUUID) {
        String path = "inventories." + playerUUID.toString();
        inventoryConfig.set(path, null);
        
        try {
            inventoryConfig.save(inventoryFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Не удалось удалить инвентарь игрока: " + e.getMessage());
        }
    }
    
    public void clearPlayerInventory(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.getInventory().setItemInOffHand(null);
    }
    
    public boolean hasSavedInventory(UUID playerUUID) {
        return savedInventories.containsKey(playerUUID) || 
               inventoryConfig.contains("inventories." + playerUUID.toString());
    }
    
    public void clearAllInventories() {
        savedInventories.clear();
        inventoryConfig.set("inventories", null);
        
        try {
            inventoryConfig.save(inventoryFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Не удалось очистить все инвентари: " + e.getMessage());
        }
    }
    
    /**
     * Класс для хранения сохраненного инвентаря
     */
    private static class SavedInventory {
        private ItemStack[] contents;
        private ItemStack[] armorContents;
        private ItemStack offHand;
        
        public ItemStack[] getContents() {
            return contents;
        }
        
        public void setContents(ItemStack[] contents) {
            this.contents = contents;
        }
        
        public ItemStack[] getArmorContents() {
            return armorContents;
        }
        
        public void setArmorContents(ItemStack[] armorContents) {
            this.armorContents = armorContents;
        }
        
        public ItemStack getOffHand() {
            return offHand;
        }
        
        public void setOffHand(ItemStack offHand) {
            this.offHand = offHand;
        }
    }
} 