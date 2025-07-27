package com.camp3rcraft.coconutlib.commands;

import com.camp3rcraft.coconutlib.CoconutLib;
import com.camp3rcraft.coconutlib.arena.Arena;
import com.camp3rcraft.coconutlib.arena.ArenaStatus;
import com.camp3rcraft.coconutlib.utils.ColorUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ArenaCommand implements CommandExecutor, TabCompleter {
    
    private final CoconutLib plugin;
    
    public ArenaCommand(CoconutLib plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "create":
                return handleCreate(sender, args);
            case "delete":
                return handleDelete(sender, args);
            case "list":
                return handleList(sender);
            case "join":
                return handleJoin(sender, args);
            case "leave":
                return handleLeave(sender);
            case "info":
                return handleInfo(sender, args);
            case "regenerate":
                return handleRegenerate(sender, args);
            case "status":
                return handleStatus(sender, args);
            default:
                sendHelp(sender);
                return true;
        }
    }
    
    private boolean handleCreate(CommandSender sender, String[] args) {
        if (!sender.hasPermission("coconutlib.arena.admin")) {
            sender.sendMessage(ColorUtils.colorize("§cУ вас нет прав для создания арен!"));
            return true;
        }
        
        if (args.length < 2) {
            sender.sendMessage(ColorUtils.colorize("§cИспользование: /arena create <название> [макс_игроков] [мин_игроков]"));
            return true;
        }
        
        String arenaName = args[1];
        int maxPlayers = args.length > 2 ? parseInt(args[2], 10) : 10;
        int minPlayers = args.length > 3 ? parseInt(args[3], 2) : 2;
        
        if (plugin.getArenaManager().createArena(arenaName, maxPlayers, minPlayers)) {
            sender.sendMessage(ColorUtils.colorize(
                plugin.getConfigManager().getString("messages.arena_created").replace("%arena%", arenaName)
            ));
        } else {
            sender.sendMessage(ColorUtils.colorize(
                plugin.getConfigManager().getString("messages.arena_already_exists").replace("%arena%", arenaName)
            ));
        }
        
        return true;
    }
    
    private boolean handleDelete(CommandSender sender, String[] args) {
        if (!sender.hasPermission("coconutlib.arena.admin")) {
            sender.sendMessage(ColorUtils.colorize("§cУ вас нет прав для удаления арен!"));
            return true;
        }
        
        if (args.length < 2) {
            sender.sendMessage(ColorUtils.colorize("§cИспользование: /arena delete <название>"));
            return true;
        }
        
        String arenaName = args[1];
        
        if (plugin.getArenaManager().deleteArena(arenaName)) {
            sender.sendMessage(ColorUtils.colorize(
                plugin.getConfigManager().getString("messages.arena_deleted").replace("%arena%", arenaName)
            ));
        } else {
            sender.sendMessage(ColorUtils.colorize(
                plugin.getConfigManager().getString("messages.arena_not_found").replace("%arena%", arenaName)
            ));
        }
        
        return true;
    }
    
    private boolean handleList(CommandSender sender) {
        List<Arena> arenas = plugin.getArenaManager().getAllArenas();
        
        if (arenas.isEmpty()) {
            sender.sendMessage(ColorUtils.colorize("§7Нет созданных арен."));
            return true;
        }
        
        sender.sendMessage(ColorUtils.colorize("§6§lСписок арен:"));
        for (Arena arena : arenas) {
            String status = arena.getStatus().getDisplayName();
            String players = arena.getPlayerCount() + "/" + arena.getMaxPlayers();
            sender.sendMessage(ColorUtils.colorize(
                "§7- §e" + arena.getName() + " §7(" + status + ") §7Игроков: §e" + players
            ));
        }
        
        return true;
    }
    
    private boolean handleJoin(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ColorUtils.colorize("§cЭта команда только для игроков!"));
            return true;
        }
        
        if (!sender.hasPermission("coconutlib.arena.join")) {
            sender.sendMessage(ColorUtils.colorize("§cУ вас нет прав для присоединения к аренам!"));
            return true;
        }
        
        if (args.length < 2) {
            sender.sendMessage(ColorUtils.colorize("§cИспользование: /arena join <название>"));
            return true;
        }
        
        Player player = (Player) sender;
        String arenaName = args[1];
        
        if (plugin.getArenaManager().joinArena(player, arenaName)) {
            // Сообщение отправляется в ArenaManager
        } else {
            sender.sendMessage(ColorUtils.colorize(
                plugin.getConfigManager().getString("messages.arena_not_found").replace("%arena%", arenaName)
            ));
        }
        
        return true;
    }
    
    private boolean handleLeave(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ColorUtils.colorize("§cЭта команда только для игроков!"));
            return true;
        }
        
        if (!sender.hasPermission("coconutlib.arena.leave")) {
            sender.sendMessage(ColorUtils.colorize("§cУ вас нет прав для покидания арен!"));
            return true;
        }
        
        Player player = (Player) sender;
        Arena playerArena = plugin.getArenaManager().getPlayerArena(player.getUniqueId());
        
        if (playerArena == null) {
            sender.sendMessage(ColorUtils.colorize("§cВы не находитесь на арене!"));
            return true;
        }
        
        plugin.getArenaManager().leaveArena(player, playerArena.getName());
        return true;
    }
    
    private boolean handleInfo(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ColorUtils.colorize("§cИспользование: /arena info <название>"));
            return true;
        }
        
        String arenaName = args[1];
        Arena arena = plugin.getArenaManager().getArena(arenaName);
        
        if (arena == null) {
            sender.sendMessage(ColorUtils.colorize(
                plugin.getConfigManager().getString("messages.arena_not_found").replace("%arena%", arenaName)
            ));
            return true;
        }
        
        sender.sendMessage(ColorUtils.colorize("§6§lИнформация об арене §e" + arena.getName() + "§6:"));
        sender.sendMessage(ColorUtils.colorize("§7Мир: §e" + arena.getWorldName()));
        sender.sendMessage(ColorUtils.colorize("§7Статус: §e" + arena.getStatus().getDisplayName()));
        sender.sendMessage(ColorUtils.colorize("§7Игроки: §e" + arena.getPlayerCount() + "§7/§e" + arena.getMaxPlayers()));
        sender.sendMessage(ColorUtils.colorize("§7Минимум игроков: §e" + arena.getMinPlayers()));
        sender.sendMessage(ColorUtils.colorize("§7Создана: §e" + arena.getCreatedAt()));
        
        return true;
    }
    
    private boolean handleRegenerate(CommandSender sender, String[] args) {
        if (!sender.hasPermission("coconutlib.arena.admin")) {
            sender.sendMessage(ColorUtils.colorize("§cУ вас нет прав для регенерации арен!"));
            return true;
        }
        
        if (args.length < 2) {
            sender.sendMessage(ColorUtils.colorize("§cИспользование: /arena regenerate <название>"));
            return true;
        }
        
        String arenaName = args[1];
        
        if (plugin.getArenaManager().regenerateArena(arenaName)) {
            sender.sendMessage(ColorUtils.colorize(
                plugin.getConfigManager().getString("messages.arena_regenerated").replace("%arena%", arenaName)
            ));
        } else {
            sender.sendMessage(ColorUtils.colorize(
                plugin.getConfigManager().getString("messages.arena_not_found").replace("%arena%", arenaName)
            ));
        }
        
        return true;
    }
    
    private boolean handleStatus(CommandSender sender, String[] args) {
        if (!sender.hasPermission("coconutlib.arena.admin")) {
            sender.sendMessage(ColorUtils.colorize("§cУ вас нет прав для изменения статуса арен!"));
            return true;
        }
        
        if (args.length < 3) {
            sender.sendMessage(ColorUtils.colorize("§cИспользование: /arena status <название> <статус>"));
            return true;
        }
        
        String arenaName = args[1];
        String statusName = args[2].toUpperCase();
        
        try {
            ArenaStatus status = ArenaStatus.valueOf(statusName);
            plugin.getArenaManager().updateArenaStatus(arenaName, status);
            sender.sendMessage(ColorUtils.colorize("§aСтатус арены §e" + arenaName + " §aизменен на §e" + status.getDisplayName()));
        } catch (IllegalArgumentException e) {
            sender.sendMessage(ColorUtils.colorize("§cНеверный статус! Доступные статусы: LOBBY, WAITING, COUNTDOWN, GAME, END"));
        }
        
        return true;
    }
    
    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ColorUtils.colorize("§6§lCoconutLib - Команды арен:"));
        sender.sendMessage(ColorUtils.colorize("§7/arena create <название> [макс_игроков] [мин_игроков] §8- Создать арену"));
        sender.sendMessage(ColorUtils.colorize("§7/arena delete <название> §8- Удалить арену"));
        sender.sendMessage(ColorUtils.colorize("§7/arena list §8- Список арен"));
        sender.sendMessage(ColorUtils.colorize("§7/arena join <название> §8- Присоединиться к арене"));
        sender.sendMessage(ColorUtils.colorize("§7/arena leave §8- Покинуть арену"));
        sender.sendMessage(ColorUtils.colorize("§7/arena info <название> §8- Информация об арене"));
        sender.sendMessage(ColorUtils.colorize("§7/arena regenerate <название> §8- Регенерировать арену"));
        sender.sendMessage(ColorUtils.colorize("§7/arena status <название> <статус> §8- Изменить статус арены"));
    }
    
    private int parseInt(String str, int defaultValue) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> subCommands = Arrays.asList("create", "delete", "list", "join", "leave", "info", "regenerate", "status");
            return subCommands.stream()
                    .filter(cmd -> cmd.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        } else if (args.length == 2) {
            String subCommand = args[0].toLowerCase();
            if (subCommand.equals("delete") || subCommand.equals("info") || 
                subCommand.equals("regenerate") || subCommand.equals("status") || 
                subCommand.equals("join")) {
                return plugin.getArenaManager().getAllArenas().stream()
                        .map(Arena::getName)
                        .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                        .collect(Collectors.toList());
            }
        } else if (args.length == 3 && args[0].toLowerCase().equals("status")) {
            return Arrays.stream(ArenaStatus.values())
                    .map(ArenaStatus::name)
                    .filter(status -> status.toLowerCase().startsWith(args[2].toLowerCase()))
                    .collect(Collectors.toList());
        }
        
        return new ArrayList<>();
    }
} 