package dev.hxrry.hxgui.core;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.util.UUID;

public class PersonalMenu extends Menu {
    
    // constructor for chest inventories
    public PersonalMenu(Component title, int rows) {
        super(title, rows);
    }
    
    // constructor for other inventory types
    public PersonalMenu(Component title, InventoryType type) {
        super(title, type);
    }
    
    // legacy string title support
    public PersonalMenu(String title, int rows) {
        super(title, rows);
    }
    
    @Override
    public Inventory getInventory(Player player) {
        UUID uuid = player.getUniqueId();
        
        // check if player already has an inventory
        Inventory inv = inventories.get(uuid);
        
        if (inv == null) {
            // create a new personal inventory for this player
            inv = createInventory();
            updateInventory(inv);
            inventories.put(uuid, inv);
        }
        
        return inv;
    }
    
    @Override
    public void updateAll() {
        // update each player's personal inventory
        for (Inventory inv : inventories.values()) {
            updateInventory(inv);
        }
    }
    
    // update specific player's inventory
    public void updateFor(Player player) {
        Inventory inv = inventories.get(player.getUniqueId());
        if (inv != null) {
            updateInventory(inv);
        }
    }
    
    // set item for specific player only
    public void setItemFor(Player player, int slot, MenuItem item) {
        Inventory inv = inventories.get(player.getUniqueId());
        if (inv != null) {
            inv.setItem(slot, item.getItem());
        }
    }
    
    // check if specific player has this menu open
    public boolean isViewedBy(Player player) {
        return inventories.containsKey(player.getUniqueId());
    }
    
    // get all viewers
    public int getViewerCount() {
        return inventories.size();
    }
    
    // create a personalized copy for a player
    public PersonalMenu copy() {
        PersonalMenu copy = type != null ? 
            new PersonalMenu(title, type) : 
            new PersonalMenu(title, rows);
        
        // copy all items
        copy.items.putAll(this.items);
        
        return copy;
    }
}