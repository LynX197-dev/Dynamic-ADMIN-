package com.trailblazer_197_05135.dynamicadminplus;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

import java.util.Arrays;

public class ConfirmationGUI implements InventoryHolder {

    private final DynamicAdminPlus plugin;
    private final Player admin;
    private final Player target;
    private final PlayerActionGUI.ActionType actionType;
    private final Inventory inventory;

    public ConfirmationGUI(DynamicAdminPlus plugin, Player admin, Player target, PlayerActionGUI.ActionType actionType) {
        this.plugin = plugin;
        this.admin = admin;
        this.target = target;
        this.actionType = actionType;
        this.inventory = Bukkit.createInventory(this, 54, "Confirm " + actionType.name());

        loadGUI();
    }

    private void loadGUI() {
        ItemStack greenPane = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        ItemMeta greenMeta = greenPane.getItemMeta();
        greenMeta.setDisplayName("Confirm");
        greenMeta.setLore(Arrays.asList("Right-click to execute"));
        greenPane.setItemMeta(greenMeta);

        ItemStack redPane = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta redMeta = redPane.getItemMeta();
        redMeta.setDisplayName("Cancel");
        redMeta.setLore(Arrays.asList("Right-click to cancel"));
        redPane.setItemMeta(redMeta);

        for (int i = 0; i < 27; i++) {
            inventory.setItem(i, greenPane.clone());
        }
        for (int i = 27; i < 54; i++) {
            inventory.setItem(i, redPane.clone());
        }
    }

    public void open() {
        admin.openInventory(inventory);
    }

    public void handleClick(org.bukkit.event.inventory.InventoryClickEvent event) {
        event.setCancelled(true);
        ItemStack item = event.getCurrentItem();
        if (item == null) return;

        Material type = item.getType();
        if (type == Material.GREEN_STAINED_GLASS_PANE) {
            executeAction();
            admin.closeInventory();
            admin.sendMessage("Action executed: " + actionType.name());
        } else if (type == Material.RED_STAINED_GLASS_PANE) {
            new PlayerActionGUI(plugin, admin, target).open();
        }
    }

    private void executeAction() {
        switch (actionType) {
            case BAN:
                Bukkit.getBanList(BanList.Type.NAME).addBan(target.getName(), "Banned by admin", null, admin.getName());
                target.kickPlayer("You have been banned.");
                break;
            case KICK:
                target.kickPlayer("Kicked by admin.");
                break;
            case TP_ADMIN_TO_PLAYER:
                admin.teleport(target.getLocation());
                break;
            case TP_PLAYER_TO_ADMIN:
                target.teleport(admin.getLocation());
                break;
            case KILL:
                target.setHealth(0);
                break;
            case REMOVE_POTIONS:
                for (PotionEffect effect : target.getActivePotionEffects()) {
                    target.removePotionEffect(effect.getType());
                }
                break;
            case FREEZE:
                PlayerDataCache.frozenPlayers.add(target.getUniqueId());
                target.setWalkSpeed(0f);
                target.setFlySpeed(0f);
                target.sendMessage(plugin.getConfig().getString("messages.frozen-message", "&cYou have been frozen by an admin!"));
                break;
            case UNFREEZE:
                if (PlayerDataCache.frozenPlayers.remove(target.getUniqueId())) {
                    target.setWalkSpeed(0.2f);
                    target.setFlySpeed(0.1f);
                    target.sendMessage(plugin.getConfig().getString("messages.unfrozen-message", "&aYou have been unfrozen by an admin!"));
                }
                break;
        }
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}