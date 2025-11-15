package com.trailblazer_197_05135.dynamicadminplus;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

public class GUIListener implements Listener {

    private final DynamicAdminPlus plugin;

    public GUIListener(DynamicAdminPlus plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getInventory();

        if (inventory.getHolder() instanceof PlayersGUI) {
            ((PlayersGUI) inventory.getHolder()).handleClick(event);
        } else if (inventory.getHolder() instanceof PlayerActionGUI) {
            ((PlayerActionGUI) inventory.getHolder()).handleClick(event);
        } else if (inventory.getHolder() instanceof ConfirmationGUI) {
            ((ConfirmationGUI) inventory.getHolder()).handleClick(event);
        } else if (inventory.getHolder() instanceof ModifyHealthGUI) {
            ((ModifyHealthGUI) inventory.getHolder()).handleClick(event);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        // Handle any cleanup if needed
    }
}