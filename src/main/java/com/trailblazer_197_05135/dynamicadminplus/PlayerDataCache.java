package com.trailblazer_197_05135.dynamicadminplus;

import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PlayerDataCache {

    private final DynamicAdminPlus plugin;
    private final Map<UUID, PlayerData> cache = new HashMap<>();
    private final long refreshInterval;
    public static final Set<UUID> frozenPlayers = new HashSet<>();

    public PlayerDataCache(DynamicAdminPlus plugin) {
        this.plugin = plugin;
        this.refreshInterval = plugin.getConfig().getLong("refresh-interval-minutes", 1) * 60 * 20; // Convert to ticks

        new BukkitRunnable() {
            @Override
            public void run() {
                refreshCache();
            }
        }.runTaskTimer(plugin, 0, refreshInterval);
    }

    private void refreshCache() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            UUID uuid = player.getUniqueId();
            int kills = player.getStatistic(Statistic.PLAYER_KILLS);
            int deaths = player.getStatistic(Statistic.DEATHS);
            long playtimeMinutes = player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20 / 60; // Convert ticks to minutes

            cache.put(uuid, new PlayerData(kills, deaths, playtimeMinutes));
        }
    }

    public PlayerData getPlayerData(Player player) {
        return cache.getOrDefault(player.getUniqueId(), new PlayerData(0, 0, 0));
    }

    public static class PlayerData {
        private final int kills;
        private final int deaths;
        private final long playtime;

        public PlayerData(int kills, int deaths, long playtime) {
            this.kills = kills;
            this.deaths = deaths;
            this.playtime = playtime;
        }

        public int getKills() { return kills; }
        public int getDeaths() { return deaths; }
        public long getPlaytime() { return playtime; }
    }
}