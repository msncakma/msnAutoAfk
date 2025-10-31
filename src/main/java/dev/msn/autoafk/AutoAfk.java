package dev.msn.autoafk;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class AutoAfk extends JavaPlugin {
    private PlayerManager playerManager;
    private UpdateChecker updateChecker;
    private final Set<UUID> debugPlayers = new HashSet<>();

    @Override
    public void onEnable() {
        // Save default config
        saveDefaultConfig();

        // Initialize PlayerManager
        playerManager = new PlayerManager(this);
        
        // Initialize UpdateChecker
        updateChecker = new UpdateChecker(this);
        
        // Clear any leftover AFK states
        getServer().getOnlinePlayers().forEach(player -> 
            playerManager.clearAfkState(player));

        // Register events
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

        // Register command
        getCommand("autoafk").setExecutor(new AutoAfkCommand(this));

        // Start AFK checker task
        startAfkChecker();
        
        // Check for updates
        if (getConfig().getBoolean("settings.check-updates", true)) {
            updateChecker.checkForUpdates();
        }

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
                // Only check players with permission
                if (!player.hasPermission("msnautoafk.use")) {
                    return;
                }
                
                // Check if player has AFK detection enabled before scheduling
                PlayerData data = playerManager.getPlayerData(player.getUniqueId());
                if (data == null || !data.isEnabled()) {
                    return;
                }
                
                // Schedule the AFK check in the player's region
                player.getScheduler().run(this, (scheduledTask) -> {
                    playerManager.checkAfkStatus(player);
                }, null);
            });
        }, 20L, 100L);
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }
    
    public UpdateChecker getUpdateChecker() {
        return updateChecker;
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