package dev.msn.autoafk;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

public class UpdateChecker {
    private final AutoAfk plugin;
    private final String currentVersion;
    private final String githubRepo = "msncakma/msnAutoAfk";
    private String latestVersion = null;
    private String downloadUrl = null;
    private boolean updateAvailable = false;

    public UpdateChecker(AutoAfk plugin) {
        this.plugin = plugin;
        this.currentVersion = plugin.getDescription().getVersion();
    }

    public CompletableFuture<Boolean> checkForUpdates() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                URL url = new URL("https://api.github.com/repos/" + githubRepo + "/releases/latest");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "application/vnd.github.v3+json");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                if (connection.getResponseCode() == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    JsonObject jsonObject = JsonParser.parseString(response.toString()).getAsJsonObject();
                    latestVersion = jsonObject.get("tag_name").getAsString();
                    
                    // Remove 'v' prefix if present
                    String cleanLatestVersion = latestVersion.startsWith("v") ? latestVersion.substring(1) : latestVersion;
                    String cleanCurrentVersion = currentVersion.startsWith("v") ? currentVersion.substring(1) : currentVersion;

                    if (jsonObject.has("html_url")) {
                        downloadUrl = jsonObject.get("html_url").getAsString();
                    }

                    updateAvailable = isNewerVersion(cleanCurrentVersion, cleanLatestVersion);
                    
                    if (updateAvailable) {
                        plugin.getLogger().warning("========================================");
                        plugin.getLogger().warning("A new version of msnAutoAfk is available!");
                        plugin.getLogger().warning("Current version: " + currentVersion);
                        plugin.getLogger().warning("Latest version: " + latestVersion);
                        plugin.getLogger().warning("Download: " + downloadUrl);
                        plugin.getLogger().warning("========================================");
                    } else {
                        plugin.getLogger().info("You are running the latest version of msnAutoAfk!");
                    }
                    
                    return updateAvailable;
                } else {
                    plugin.getLogger().warning("Failed to check for updates. HTTP Status: " + connection.getResponseCode());
                    return false;
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to check for updates: " + e.getMessage());
                return false;
            }
        });
    }

    private boolean isNewerVersion(String current, String latest) {
        try {
            String[] currentParts = current.split("\\.");
            String[] latestParts = latest.split("\\.");

            int length = Math.max(currentParts.length, latestParts.length);
            for (int i = 0; i < length; i++) {
                int currentPart = i < currentParts.length ? parseVersionPart(currentParts[i]) : 0;
                int latestPart = i < latestParts.length ? parseVersionPart(latestParts[i]) : 0;

                if (latestPart > currentPart) {
                    return true;
                } else if (latestPart < currentPart) {
                    return false;
                }
            }
            return false;
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to compare versions: " + e.getMessage());
            return false;
        }
    }

    private int parseVersionPart(String part) {
        // Remove any non-numeric suffix (like -SNAPSHOT, -BETA, etc.)
        return Integer.parseInt(part.replaceAll("[^0-9].*", ""));
    }

    public void notifyPlayer(Player player) {
        if (!updateAvailable) return;
        
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            player.sendMessage("§8§m                                                ");
            player.sendMessage("§6§lmsnAutoAfk Update Available!");
            player.sendMessage("§7Current: §c" + currentVersion + " §7→ Latest: §a" + latestVersion);
            player.sendMessage("§7Download: §b" + downloadUrl);
            player.sendMessage("§8§m                                                ");
        }, 40L); // 2 seconds after join
    }

    public boolean isUpdateAvailable() {
        return updateAvailable;
    }

    public String getLatestVersion() {
        return latestVersion;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }
}
