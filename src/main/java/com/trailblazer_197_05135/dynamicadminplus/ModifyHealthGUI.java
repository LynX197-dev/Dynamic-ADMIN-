package com.trailblazer_197_05135.dynamicadminplus;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class ModifyHealthGUI implements InventoryHolder {

    private final DynamicAdminPlus plugin;
    private final Player admin;
    private final Player target;
    private final Inventory inventory;

    public ModifyHealthGUI(DynamicAdminPlus plugin, Player admin, Player target) {
        this.plugin = plugin;
        this.admin = admin;
        this.target = target;
        this.inventory = Bukkit.createInventory(this, 54, "Modify Health for " + target.getName());

        loadGUI();
    }

    private void loadGUI() {
        ItemStack greenPane = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        ItemMeta greenMeta = greenPane.getItemMeta();
        greenMeta.setDisplayName("Increase Max Health");
        greenMeta.setLore(Arrays.asList("Right-click to increase by 1"));
        greenPane.setItemMeta(greenMeta);

        ItemStack redPane = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta redMeta = redPane.getItemMeta();
        redMeta.setDisplayName("Decrease Max Health");
        redMeta.setLore(Arrays.asList("Right-click to decrease by 1"));
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
        double currentMax = target.getAttribute(Attribute.MAX_HEALTH).getBaseValue();

        if (type == Material.GREEN_STAINED_GLASS_PANE) {
            target.getAttribute(Attribute.MAX_HEALTH).setBaseValue(currentMax + 1);
            admin.sendMessage("Increased " + target.getName() + "'s max health to " + (currentMax + 1));
        } else if (type == Material.RED_STAINED_GLASS_PANE) {
            if (currentMax > 1) {
                target.getAttribute(Attribute.MAX_HEALTH).setBaseValue(currentMax - 1);
                admin.sendMessage("Decreased " + target.getName() + "'s max health to " + (currentMax - 1));
            } else {
                admin.sendMessage("Cannot decrease health below 1.");
            }
        }
        // Stay in the GUI for repeated actions
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}