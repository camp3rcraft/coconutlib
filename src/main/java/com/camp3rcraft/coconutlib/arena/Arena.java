package com.camp3rcraft.coconutlib.arena;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Arena {
    
    private final String name;
    private final String worldName;
    private ArenaStatus status;
    private final int maxPlayers;
    private final int minPlayers;
    private final Timestamp createdAt;
    private final List<UUID> players;
    
    public Arena(String name, String worldName, ArenaStatus status, int maxPlayers, int minPlayers, Timestamp createdAt) {
        this.name = name;
        this.worldName = worldName;
        this.status = status;
        this.maxPlayers = maxPlayers;
        this.minPlayers = minPlayers;
        this.createdAt = createdAt;
        this.players = new ArrayList<>();
    }
    
    public String getName() {
        return name;
    }
    
    public String getWorldName() {
        return worldName;
    }
    
    public ArenaStatus getStatus() {
        return status;
    }
    
    public void setStatus(ArenaStatus status) {
        this.status = status;
    }
    
    public int getMaxPlayers() {
        return maxPlayers;
    }
    
    public int getMinPlayers() {
        return minPlayers;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public List<UUID> getPlayers() {
        return new ArrayList<>(players);
    }
    
    public void addPlayer(UUID playerUUID) {
        if (!players.contains(playerUUID) && players.size() < maxPlayers) {
            players.add(playerUUID);
        }
    }
    
    public void removePlayer(UUID playerUUID) {
        players.remove(playerUUID);
    }
    
    public boolean hasPlayer(UUID playerUUID) {
        return players.contains(playerUUID);
    }
    
    public int getPlayerCount() {
        return players.size();
    }
    
    public boolean isFull() {
        return players.size() >= maxPlayers;
    }
    
    public boolean hasEnoughPlayers() {
        return players.size() >= minPlayers;
    }
    
    public boolean isEmpty() {
        return players.isEmpty();
    }
    
    @Override
    public String toString() {
        return "Arena{" +
                "name='" + name + '\'' +
                ", worldName='" + worldName + '\'' +
                ", status=" + status +
                ", players=" + players.size() + "/" + maxPlayers +
                '}';
    }
} 