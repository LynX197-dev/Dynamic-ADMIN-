package com.trailblazer_197_05135.dynamicadminplus;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PlayersGUI implements InventoryHolder {

    private final DynamicAdminPlus plugin;
    private final Player admin;
    private final Inventory inventory;

    public PlayersGUI(DynamicAdminPlus plugin, Player admin) {
        this.plugin = plugin;
        this.admin = admin;
        this.inventory = Bukkit.createInventory(this, 54, "Online Players");

        loadPlayers();
    }

    private void loadPlayers() {
        inventory.clear();
        List<Player> onlinePlayers = (List<Player>) Bukkit.getOnlinePlayers();
        int slot = 0;
        for (Player player : onlinePlayers) {
            if (slot >= 54) break;
            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            meta.setOwningPlayer(player);
            meta.setDisplayName(player.getName());

            PlayerDataCache.PlayerData data = plugin.getPlayerDataCache().getPlayerData(player);
            List<String> lore = Arrays.asList(
                "Coordinates: " + (int) player.getLocation().getX() + ", " + (int) player.getLocation().getY() + ", " + (int) player.getLocation().getZ(),
                "Potion Effects: " + (player.getActivePotionEffects().isEmpty() ? "None" : player.getActivePotionEffects().size() + " active"),
                "Health: " + (int) player.getHealth() + "/" + (int) player.getMaxHealth(),
                "XP Level: " + player.getLevel(),
                "Armor: " + (player.getInventory().getArmorContents().length > 0 ? "Equipped" : "None"),
                "Kills: " + data.getKills(),
                "Deaths: " + data.getDeaths(),
                "Total Playtime: " + formatPlaytime(data.getPlaytime())
            );
            meta.setLore(lore);
            head.setItemMeta(meta);
            inventory.setItem(slot++, head);
        }
    }

    private String formatPlaytime(long minutes) {
        long hours = TimeUnit.MINUTES.toHours(minutes);
        long mins = minutes % 60;
        return hours + "h " + mins + "m";
    }

    public void open() {
        admin.openInventory(inventory);
    }

    public void handleClick(org.bukkit.event.inventory.InventoryClickEvent event) {
        event.setCancelled(true);
        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType() != Material.PLAYER_HEAD) return;

        SkullMeta meta = (SkullMeta) item.getItemMeta();
        Player target = Bukkit.getPlayer(meta.getOwningPlayer().getUniqueId());
        if (target != null) {
            new PlayerActionGUI(plugin, admin, target).open();
        }
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}