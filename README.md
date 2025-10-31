# 🎮 MSNAutoAFK v1.0.0 - Advanced AFK Management System

Hey there! 👋 Welcome to MSNAutoAFK, a powerful and user-friendly AFK management plugin for Paper/Folia servers. This plugin makes it super easy to manage AFK players with automatic detection and customizable appearance settings.

## ✨ Features

- 🎯 **Multiple Detection Methods** - Movement, mouse, and jump detection
- 🎨 **Flexible Display Options** - Choose between suffix or group-based AFK marking
- 🔒 **Permission-Based System** - Granular control with detailed permissions
- ⚡ **Performance Optimized** - Only tracks players with permission, per-player cooldown system
- 🌟 **Folia Compatible** - Full support for Folia server software
- 🔄 **State Persistence** - Remember player preferences between restarts
- 📊 **Debug System** - Built-in debugging for troubleshooting
- ⚙️ **Highly Configurable** - Customize all aspects through config.yml
- 🔔 **Update Checker** - Automatic GitHub release notifications

## 🎯 Features in v1.0.0 (Initial Release)

- ✅ **Automatic AFK Detection** - Based on player activity
- ✅ **Configurable Timers** - Set custom AFK timeout duration
- ✅ **Multiple Display Methods** - LuckPerms groups or suffixes
- ✅ **Activity Monitoring** - Track movement, mouse, and jumping
- ✅ **Command System** - Easy to use commands for players and staff
- ✅ **Debug Mode** - In-game debugging for administrators
- ✅ **State Management** - Proper handling of player states
- ✅ **Folia Support** - Full regional scheduler compatibility
- ✅ **Smart Performance** - Per-player check cooldown (5s default)
- ✅ **Permission-Only Tracking** - Only monitors players with permission
- ✅ **Zero Processing When Disabled** - No event processing for disabled players
- ✅ **Safe Meta Management** - Only removes AFK suffix, preserves other meta
- ✅ **Auto Update Checking** - Notifies admins of new releases from GitHub

## 📋 Commands

### For Players
- `/autoafk` - Toggle AFK detection on/off
  - Permission: `msnautoafk.use`

### For Staff
- `/msnautoafk` (alias: `/autoafk`) - Main command
  - `/msnautoafk reload` - Reload plugin configuration
  - `/msnautoafk debug` - Toggle debug messages
  - Permission: `msnautoafk.reload` (for reload command)
  - Permission: `msnautoafk.debug` (for debug command)

## 🚀 Installation

1. Download the latest release
2. Place the .jar file in your server's `plugins` folder
3. Restart your server
4. Configure settings in `config.yml`
5. Make sure LuckPerms is installed for suffix/group functionality

## ⚙️ Configuration

### Basic Setup
```yaml
settings:
  # Time in seconds before a player is marked as AFK
  afk-time: 300
  
  # How often to check each player's AFK status (in seconds)
  # After player activity detected, waits this long before checking again
  # Lower = more responsive, Higher = more efficient (default: 5)
  check-cooldown: 5
  
  # Detection settings
  check-movement: true
  check-mouse: true
  check-jump: true
  
  # AFK marking type (suffix/group)
  afk-type: 'suffix'
  
  # Group name if using group type
  afk-group: 'afk'
  
  # Suffix if using suffix type (only this suffix is removed, not all meta)
  afk-suffix: '&7[AFK]'
  
  # Default state settings
  default-enabled: true
  auto-enable-on-join: true
  remember-toggle-state: false
  
  # Check for plugin updates on startup
  check-updates: true

messages:
  no-permission: '&cYou don''t have permission to use this command!'
  config-reloaded: '&aConfiguration has been reloaded!'
  afk-enabled: '&aAuto AFK detection has been enabled!'
  afk-disabled: '&cAuto AFK detection has been disabled!'
  now-afk: '&7You are now AFK!'
  no-longer-afk: '&7You are no longer AFK!'
```

## 🔑 Permissions

### Basic Permissions
- `msnautoafk.use` - Allow use of AFK features (default: op)
- `msnautoafk.reload` - Allow reloading configuration (default: op)
- `msnautoafk.debug` - Allow using debug mode (default: op)
- `msnautoafk.updatenotify` - Receive update notifications when joining (default: op)

## 🔧 Troubleshooting

### Common Issues

**Plugin not working with Folia:**
- Make sure you're using the latest version
- Check console for any error messages
- Verify LuckPerms is properly configured

**AFK Detection Issues:**
- Check if player has proper permissions (`msnautoafk.use`)
- Verify detection settings in config.yml
- Use debug mode to track activity detection
- Make sure player hasn't toggled `/autoafk` off

**Performance Concerns:**
- Plugin only tracks players with `msnautoafk.use` permission
- Players with `/autoafk` disabled have zero performance impact
- Adjust `check-cooldown` to balance responsiveness vs performance
- Disable unused detection methods (e.g., `check-mouse: false`)

### Debug Mode

Enable debug mode to see detailed information about:
- Player movement detection
- AFK state changes
- Command execution
- Player initialization
- Check cooldown status
- When players are skipped (disabled or no permission)

Use `/msnautoafk debug` to toggle debug messages.

### Performance Optimization Tips

**For Best Performance:**
1. Only give `msnautoafk.use` to players who need AFK detection
2. Increase `check-cooldown` to 10-15 seconds for large servers
3. Disable `check-mouse` if not needed (most frequent event)
4. Set `check-jump: false` if movement detection is sufficient

**How It Works:**
- After detecting player activity, plugin waits `check-cooldown` seconds before checking again
- Players without permission are never processed
- Players who disabled `/autoafk` are completely ignored
- Uses LuckPerms `removesuffix` to safely remove only AFK suffix

## 🔔 Update Notifications

The plugin automatically checks for updates on GitHub:
- Server console shows update info on startup
- OPs/Admins get in-game notifications when joining
- Can be disabled with `check-updates: false` in config.yml
- Only checks the latest release from [GitHub Releases](https://github.com/msncakma/msnAutoAfk/releases)

## 🤝 Contributing

Want to make this plugin even better? Here's how you can help:

1. Fork the repo
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 License

This project is under the MIT License - see the [LICENSE](LICENSE) file for details.

## 💖 Support

If you found this plugin helpful, don't forget to give it a star ⭐ on GitHub! Got questions? Feel free to:

- Open an [issue](https://github.com/msncakma/msnAutoAfk/issues)
- Contact me on Discord: msncakma
- Visit our community for support and updates

## 🙏 Credits

Made with ❤️ by [msncakma](https://github.com/msncakma)

Special thanks to:
- Paper/Folia team for their excellent APIs
- LuckPerms for permission management support

---
*Keep your server organized with smart AFK management! 🎮*