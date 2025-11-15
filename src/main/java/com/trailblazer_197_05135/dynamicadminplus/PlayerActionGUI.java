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

public class PlayerActionGUI implements InventoryHolder {

    private final DynamicAdminPlus plugin;
    private final Player admin;
    private final Player target;
    private final Inventory inventory;

    public PlayerActionGUI(DynamicAdminPlus plugin, Player admin, Player target) {
        this.plugin = plugin;
        this.admin = admin;
        this.target = target;
        this.inventory = Bukkit.createInventory(this, 54, "Actions for " + target.getName());

        loadGUI();
    }

    private void loadGUI() {
        // Right side: Player stats
        ItemStack statsHead = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta statsMeta = (SkullMeta) statsHead.getItemMeta();
        statsMeta.setOwningPlayer(target);
        statsMeta.setDisplayName(target.getName() + "'s Stats");

        PlayerDataCache.PlayerData data = plugin.getPlayerDataCache().getPlayerData(target);
        List<String> lore = Arrays.asList(
            "Coordinates: " + (int) target.getLocation().getX() + ", " + (int) target.getLocation().getY() + ", " + (int) target.getLocation().getZ(),
            "Potion Effects: " + (target.getActivePotionEffects().isEmpty() ? "None" : target.getActivePotionEffects().size() + " active"),
            "Health: " + (int) target.getHealth() + "/" + (int) target.getMaxHealth(),
            "XP Level: " + target.getLevel(),
            "Armor: " + (target.getInventory().getArmorContents().length > 0 ? "Equipped" : "None"),
            "Kills: " + data.getKills(),
            "Deaths: " + data.getDeaths(),
            "Total Playtime: " + formatPlaytime(data.getPlaytime())
        );
        statsMeta.setLore(lore);
        statsHead.setItemMeta(statsMeta);

        for (int i = 9; i < 18; i++) {
            inventory.setItem(i, statsHead.clone());
        }

        // Left side: Action panes
        setActionPane(0, Material.RED_STAINED_GLASS_PANE, "Ban this player", ActionType.BAN);
        setActionPane(1, Material.ORANGE_STAINED_GLASS_PANE, "Kick this player", ActionType.KICK);
        setActionPane(2, Material.YELLOW_STAINED_GLASS_PANE, "Teleport to this player", ActionType.TP_ADMIN_TO_PLAYER);
        setActionPane(3, Material.BLUE_STAINED_GLASS_PANE, "Teleport this player to you", ActionType.TP_PLAYER_TO_ADMIN);
        setActionPane(4, Material.BLACK_STAINED_GLASS_PANE, "Kill this player", ActionType.KILL);
        setActionPane(5, Material.PURPLE_STAINED_GLASS_PANE, "Clear all potion effects", ActionType.REMOVE_POTIONS);
        setActionPane(6, Material.GREEN_STAINED_GLASS_PANE, "Increase/decrease max health", ActionType.MODIFY_HEALTH);
        setActionPane(7, Material.GRAY_STAINED_GLASS_PANE, "Freeze this player", ActionType.FREEZE);
        setActionPane(8, Material.LIGHT_GRAY_STAINED_GLASS_PANE, "Unfreeze this player", ActionType.UNFREEZE);
    }

    private void setActionPane(int slot, Material material, String tooltip, ActionType actionType) {
        ItemStack pane = new ItemStack(material);
        ItemMeta meta = pane.getItemMeta();
        meta.setDisplayName(actionType.name());
        meta.setLore(Arrays.asList(tooltip));
        pane.setItemMeta(meta);
        inventory.setItem(slot, pane);
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
        if (item == null || !item.getType().name().endsWith("_STAINED_GLASS_PANE")) return;

        ItemMeta meta = item.getItemMeta();
        ActionType actionType = ActionType.valueOf(meta.getDisplayName());

        if (actionType == ActionType.MODIFY_HEALTH) {
            new ModifyHealthGUI(plugin, admin, target).open();
        } else {
            new ConfirmationGUI(plugin, admin, target, actionType).open();
        }
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public enum ActionType {
        BAN, KICK, TP_ADMIN_TO_PLAYER, TP_PLAYER_TO_ADMIN, KILL, REMOVE_POTIONS, MODIFY_HEALTH, FREEZE, UNFREEZE
    }
}