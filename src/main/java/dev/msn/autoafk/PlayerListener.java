package dev.msn.autoafk;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerListener implements Listener {
    private final AutoAfk plugin;

    public PlayerListener(AutoAfk plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        
        // Early permission check to avoid unnecessary processing
        if (!player.hasPermission("msnautoafk.use")) return;
        
        // Check if player has AFK detection enabled
        if (!plugin.getPlayerManager().isPlayerTrackingEnabled(player)) return;
        
        if (!plugin.getConfig().getBoolean("settings.check-movement", true)) return;

        if (event.getTo() != null && event.getFrom() != null) {
            // Check if the player actually moved blocks or just looked around
            boolean movedBlocks = event.getTo().getBlockX() != event.getFrom().getBlockX() 
                || event.getTo().getBlockY() != event.getFrom().getBlockY() 
                || event.getTo().getBlockZ() != event.getFrom().getBlockZ();

            boolean movedMouse = false;
            if (plugin.getConfig().getBoolean("settings.check-mouse", true)) {
                movedMouse = event.getTo().getPitch() != event.getFrom().getPitch() 
                    || event.getTo().getYaw() != event.getFrom().getYaw();
            }

            boolean jumping = false;
            if (plugin.getConfig().getBoolean("settings.check-jump", true)) {
                jumping = event.getTo().getY() > event.getFrom().getY() 
                    && !player.isFlying();
            }

            if (movedBlocks || movedMouse || jumping) {
                plugin.sendDebugMessage("Movement detected for " + player.getName());
                plugin.getPlayerManager().updatePlayerActivity(player);
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        plugin.sendDebugMessage("Player joined: " + player.getName());
        plugin.getPlayerManager().initializePlayer(player);
        plugin.getPlayerManager().updatePlayerActivity(player);
        
        // Notify ops/admins about updates
        if (player.hasPermission("msnautoafk.updatenotify") && plugin.getUpdateChecker().isUpdateAvailable()) {
            plugin.getUpdateChecker().notifyPlayer(player);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        plugin.getPlayerManager().cleanup(player);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        // Early permission check
        if (!player.hasPermission("msnautoafk.use")) return;
        // Check if player has AFK detection enabled
        if (!plugin.getPlayerManager().isPlayerTrackingEnabled(player)) return;
        plugin.getPlayerManager().updatePlayerActivity(player);
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        // Early permission check
        if (!player.hasPermission("msnautoafk.use")) return;
        // Check if player has AFK detection enabled
        if (!plugin.getPlayerManager().isPlayerTrackingEnabled(player)) return;
        plugin.getPlayerManager().updatePlayerActivity(player);
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        // Early permission check
        if (!player.hasPermission("msnautoafk.use")) return;
        // Check if player has AFK detection enabled
        if (!plugin.getPlayerManager().isPlayerTrackingEnabled(player)) return;
        
        // Schedule in player's region since this is an async event
        player.getScheduler().run(plugin, (task) -> 
            plugin.getPlayerManager().updatePlayerActivity(player), 
        null);
    }
}