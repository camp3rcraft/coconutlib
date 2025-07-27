package com.camp3rcraft.coconutlib.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.regex.Pattern;

public class ColorUtils {
    
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();
    
    /**
     * Преобразует строку с цветовыми кодами в цветную строку
     * Поддерживает HEX цвета и ванильные цвета
     * @param text Исходный текст
     * @return Цветной текст
     */
    public static String colorize(String text) {
        if (text == null) {
            return "";
        }
        
        // Заменяем HEX цвета
        text = HEX_PATTERN.matcher(text).replaceAll("§x§$1");
        
        // Заменяем ванильные цвета
        text = text.replace("&", "§");
        
        return text;
    }
    
    /**
     * Преобразует строку с MiniMessage в Component
     * @param text Текст с MiniMessage
     * @return Component
     */
    public static Component miniMessage(String text) {
        if (text == null) {
            return Component.empty();
        }
        
        return miniMessage.deserialize(text);
    }
    
    /**
     * Преобразует строку с цветовыми кодами в Component
     * @param text Текст с цветовыми кодами
     * @return Component
     */
    public static Component colorizeComponent(String text) {
        return Component.text(colorize(text));
    }
    
    /**
     * Удаляет все цветовые коды из строки
     * @param text Исходный текст
     * @return Текст без цветовых кодов
     */
    public static String stripColors(String text) {
        if (text == null) {
            return "";
        }
        
        return text.replaceAll("§[0-9a-fk-or]", "")
                  .replaceAll("&[0-9a-fk-or]", "")
                  .replaceAll("&#[A-Fa-f0-9]{6}", "");
    }
    
    /**
     * Проверяет, содержит ли строка цветовые коды
     * @param text Текст для проверки
     * @return true если содержит цветовые коды
     */
    public static boolean hasColors(String text) {
        if (text == null) {
            return false;
        }
        
        return text.contains("§") || text.contains("&") || HEX_PATTERN.matcher(text).find();
    }
    
    /**
     * Получает префикс для HEX цвета
     * @param hex HEX цвет (без #)
     * @return Префикс для HEX цвета
     */
    public static String getHexPrefix(String hex) {
        if (hex == null || hex.length() != 6) {
            return "";
        }
        
        StringBuilder prefix = new StringBuilder("§x");
        for (char c : hex.toCharArray()) {
            prefix.append("§").append(c);
        }
        return prefix.toString();
    }
    
    /**
     * Конвертирует RGB в HEX
     * @param r Красный (0-255)
     * @param g Зеленый (0-255)
     * @param b Синий (0-255)
     * @return HEX строка
     */
    public static String rgbToHex(int r, int g, int b) {
        return String.format("%02x%02x%02x", r, g, b);
    }
    
    /**
     * Конвертирует HEX в RGB
     * @param hex HEX строка
     * @return Массив RGB [r, g, b]
     */
    public static int[] hexToRgb(String hex) {
        if (hex == null || hex.length() != 6) {
            return new int[]{0, 0, 0};
        }
        
        try {
            int r = Integer.parseInt(hex.substring(0, 2), 16);
            int g = Integer.parseInt(hex.substring(2, 4), 16);
            int b = Integer.parseInt(hex.substring(4, 6), 16);
            return new int[]{r, g, b};
        } catch (NumberFormatException e) {
            return new int[]{0, 0, 0};
        }
    }
} 