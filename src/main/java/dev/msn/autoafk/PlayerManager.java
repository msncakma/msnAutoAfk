package dev.msn.autoafk;

import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager {
    private final Map<UUID, PlayerData> playerData;
    private final AutoAfk plugin;

    public PlayerManager(AutoAfk plugin) {
        this.plugin = plugin;
        this.playerData = new HashMap<>();
    }

    public void initializePlayer(Player player) {
        if (!player.hasPermission("msnautoafk.use")) return;

        boolean shouldEnable = PlayerData.loadPlayerState(plugin, player.getUniqueId());
        if (plugin.getConfig().getBoolean("settings.auto-enable-on-join", true)) {
            playerData.put(player.getUniqueId(), new PlayerData(shouldEnable));
        } else {
            playerData.put(player.getUniqueId(), new PlayerData(false));
        }
        
        // Clear any existing AFK state
        clearAfkState(player);
    }

    public void clearAfkState(Player player) {
        String afkType = plugin.getConfig().getString("settings.afk-type", "suffix");
        
        // Execute commands in the global region to ensure they run on the main thread
        plugin.getServer().getGlobalRegionScheduler().execute(plugin, () -> {
            if (afkType.equalsIgnoreCase("group")) {
                String groupName = plugin.getConfig().getString("settings.afk-group", "afk");
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), 
                    "lp user " + player.getName() + " parent remove " + groupName);
            } else {
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), 
                    "lp user " + player.getName() + " meta clear");
            }
        });

        PlayerData data = playerData.get(player.getUniqueId());
        if (data != null) {
            data.setAfk(false);
        }
    }

    public void updatePlayerActivity(Player player) {
        if (!player.hasPermission("msnautoafk.use")) return;
        
        PlayerData data = playerData.get(player.getUniqueId());
        if (data == null || !data.isEnabled()) return;

        data.setLastActivity(System.currentTimeMillis());
        if (data.isAfk()) {
            setAfk(player, false);
        }
    }

    public void checkAfkStatus(Player player) {
        if (!player.hasPermission("msnautoafk.use")) return;

        PlayerData data = playerData.get(player.getUniqueId());
        if (data == null || !data.isEnabled()) return;

        long afkTime = plugin.getConfig().getLong("settings.afk-time") * 1000;
        if (!data.isAfk() && System.currentTimeMillis() - data.getLastActivity() >= afkTime) {
            setAfk(player, true);
        }
    }

    public void setAfk(Player player, boolean afk) {
        PlayerData data = playerData.get(player.getUniqueId());
        if (data == null) return;

        data.setAfk(afk);
        
        // Execute commands in the global region to ensure they run on the main thread
        plugin.getServer().getGlobalRegionScheduler().execute(plugin, () -> {
            String afkType = plugin.getConfig().getString("settings.afk-type", "suffix");
            
            if (afkType.equalsIgnoreCase("group")) {
                String groupName = plugin.getConfig().getString("settings.afk-group", "afk");
                if (afk) {
                    plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), 
                        "lp user " + player.getName() + " parent add " + groupName);
                } else {
                    plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), 
                        "lp user " + player.getName() + " parent remove " + groupName);
                }
            } else {
                // Default to suffix type
                String suffix = plugin.getConfig().getString("settings.afk-suffix", "&7[AFK]")
                        .replace("&", "§");
                if (afk) {
                    plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), 
                        "lp user " + player.getName() + " meta setsuffix 0 " + suffix);
                } else {
                    plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), 
                        "lp user " + player.getName() + " meta clear");
                }
            }
        });

        // Send message to player in their region
        player.getScheduler().run(plugin, (task) -> {
            player.sendMessage(plugin.getConfig().getString(
                afk ? "messages.now-afk" : "messages.no-longer-afk", "").replace("&", "§"));
        }, null);
    }

    public void toggleAutoAfk(Player player) {
        PlayerData data = playerData.get(player.getUniqueId());
        if (data == null) {
            data = new PlayerData(true);
            playerData.put(player.getUniqueId(), data);
        }

        data.setEnabled(!data.isEnabled());
        PlayerData.savePlayerState(plugin, player.getUniqueId(), data.isEnabled());
        
        if (!data.isEnabled() && data.isAfk()) {
            setAfk(player, false);
        }
        
        String message = data.isEnabled() ? 
            plugin.getConfig().getString("messages.afk-enabled", "") :
            plugin.getConfig().getString("messages.afk-disabled", "");
        player.sendMessage(message.replace("&", "§"));
    }

    public void cleanup(Player player) {
        if (player == null) return;
        clearAfkState(player);
        PlayerData data = playerData.get(player.getUniqueId());
        if (data != null && data.isEnabled()) {
            PlayerData.savePlayerState(plugin, player.getUniqueId(), data.isEnabled());
        }
        playerData.remove(player.getUniqueId());
    }
}