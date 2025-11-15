package com.trailblazer_197_05135.dynamicadminplus;

import org.bukkit.plugin.java.JavaPlugin;

public class DynamicAdminPlus extends JavaPlugin {

    private PlayerDataCache playerDataCache;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        playerDataCache = new PlayerDataCache(this);
        getCommand("da").setExecutor(new AdminCommand(this));
        getServer().getPluginManager().registerEvents(new GUIListener(this), this);
        getLogger().info("Dynamic ADMIN+ enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Dynamic ADMIN+ disabled!");
    }

    public PlayerDataCache getPlayerDataCache() {
        return playerDataCache;
    }
}