package com.camp3rcraft.coconutlib.arena;

public enum ArenaStatus {
    LOBBY("Лобби"),
    WAITING("Ожидание игроков"),
    COUNTDOWN("Ожидание начала"),
    GAME("Игра"),
    END("Конец");
    
    private final String displayName;
    
    ArenaStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
} 