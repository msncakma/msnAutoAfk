package dev.msn.autoafk;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class AutoAfk extends JavaPlugin {
    private PlayerManager playerManager;
    private final Set<UUID> debugPlayers = new HashSet<>();

    @Override
    public void onEnable() {
        // Save default config
        saveDefaultConfig();

        // Initialize PlayerManager
        playerManager = new PlayerManager(this);
        
        // Clear any leftover AFK states
        getServer().getOnlinePlayers().forEach(player -> 
            playerManager.clearAfkState(player));

        // Register events
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

        // Register command
        getCommand("autoafk").setExecutor(new AutoAfkCommand(this));

        // Start AFK checker task
        startAfkChecker();

        getLogger().info("AutoAfk plugin has been enabled!");
    }

    @Override
    public void onDisable() {
        // Clean up all online players
        getServer().getOnlinePlayers().forEach(player -> 
            playerManager.cleanup(player));
        getLogger().info("AutoAfk plugin has been disabled!");
    }

    private void startAfkChecker() {
        // For Folia compatibility, we check AFK status in the global region
        // Check every 5 seconds (100 ticks)
        Bukkit.getServer().getGlobalRegionScheduler().runAtFixedRate(this, (task) -> {
            Bukkit.getOnlinePlayers().forEach(player -> {
                // Schedule the AFK check in the player's region
                player.getScheduler().run(this, (scheduledTask) -> {
                    playerManager.checkAfkStatus(player);
                    sendDebugMessage("Checking AFK status for " + player.getName());
                }, null);
            });
        }, 20L, 100L);
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public void toggleDebug(UUID playerId) {
        if (debugPlayers.contains(playerId)) {
            debugPlayers.remove(playerId);
        } else {
            debugPlayers.add(playerId);
        }
    }

    public boolean isDebugEnabled(UUID playerId) {
        return debugPlayers.contains(playerId);
    }

    public void sendDebugMessage(String message) {
        if (!debugPlayers.isEmpty()) {
            for (UUID uuid : debugPlayers) {
                Player player = getServer().getPlayer(uuid);
                if (player != null && player.isOnline()) {
                    player.sendMessage("§7[Debug] §f" + message);
                }
            }
        }
    }
}