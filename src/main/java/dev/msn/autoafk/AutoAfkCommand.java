package dev.msn.autoafk;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AutoAfkCommand implements CommandExecutor {
    private final AutoAfk plugin;

    public AutoAfkCommand(AutoAfk plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }

        if (!player.hasPermission("msnautoafk.use")) {
            player.sendMessage(plugin.getConfig().getString("messages.no-permission", "").replace("&", "§"));
            return true;
        }

        if (args.length == 0) {
            plugin.getPlayerManager().toggleAutoAfk(player);
            return true;
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reload") && player.hasPermission("msnautoafk.reload")) {
                plugin.reloadConfig();
                player.sendMessage(plugin.getConfig().getString("messages.config-reloaded", "").replace("&", "§"));
                return true;
            } else if (args[0].equalsIgnoreCase("debug") && player.hasPermission("msnautoafk.debug")) {
                plugin.toggleDebug(player.getUniqueId());
                player.sendMessage("§6Debug mode " + (plugin.isDebugEnabled(player.getUniqueId()) ? "enabled" : "disabled") + " for you.");
                return true;
            }
        }

        return false;
    }
}