package dev.msn.autoafk;

import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class PlayerData {
    private long lastActivity;
    private long lastCheck;
    private boolean isAfk;
    private boolean enabled;

    public PlayerData(boolean enabled) {
        this.lastActivity = System.currentTimeMillis();
        this.lastCheck = 0L;
        this.enabled = enabled;
        this.isAfk = false;
    }

    public long getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(long lastActivity) {
        this.lastActivity = lastActivity;
    }

    public long getLastCheck() {
        return lastCheck;
    }

    public void setLastCheck(long lastCheck) {
        this.lastCheck = lastCheck;
    }

    public boolean isAfk() {
        return isAfk;
    }

    public void setAfk(boolean afk) {
        isAfk = afk;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public static void savePlayerState(AutoAfk plugin, UUID uuid, boolean enabled) {
        if (!plugin.getConfig().getBoolean("settings.remember-toggle-state", false)) {
            return;
        }

        File dataFile = new File(plugin.getDataFolder(), "playerdata.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(dataFile);
        config.set("players." + uuid.toString(), enabled);
        
        try {
            config.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Could not save player state for " + uuid);
        }
    }

    public static boolean loadPlayerState(AutoAfk plugin, UUID uuid) {
        if (!plugin.getConfig().getBoolean("settings.remember-toggle-state", false)) {
            return plugin.getConfig().getBoolean("settings.default-enabled", true);
        }

        File dataFile = new File(plugin.getDataFolder(), "playerdata.yml");
        if (!dataFile.exists()) {
            return plugin.getConfig().getBoolean("settings.default-enabled", true);
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(dataFile);
        return config.getBoolean("players." + uuid.toString(), 
            plugin.getConfig().getBoolean("settings.default-enabled", true));
    }
}