package dev.hxrry.hxgui.core;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public class SharedMenu extends Menu {
    
    private Inventory sharedInventory;
    
    // constructor for chest inventories
    public SharedMenu(Component title, int rows) {
        super(title, rows);
    }
    
    // constructor for other inventory types
    public SharedMenu(Component title, InventoryType type) {
        super(title, type);
    }
    
    // legacy string title support
    public SharedMenu(String title, int rows) {
        super(title, rows);
    }
    
    @Override
    public Inventory getInventory(Player player) {
        // create the shared inventory if it doesn't exist
        if (sharedInventory == null) {
            sharedInventory = createInventory();
            updateInventory(sharedInventory);
        }
        
        // everyone gets the same inventory instance
        inventories.put(player.getUniqueId(), sharedInventory);
        return sharedInventory;
    }
    
    @Override
    public void updateAll() {
        // only need to update one inventory since its shared
        if (sharedInventory != null) {
            updateInventory(sharedInventory);
        }
    }
    
    @Override
    public void onClose(Player player) {
        // remove player from tracking
        inventories.remove(player.getUniqueId());
        
        // if no one is viewing, clear the shared inventory
        if (inventories.isEmpty()) {
            sharedInventory = null;
        }
        
        // unregister from manager
        super.onClose(player);
    }
    
    // check if anyone is viewing
    public boolean hasViewers() {
        return !inventories.isEmpty();
    }
    
    // get viewer count
    public int getViewerCount() {
        return inventories.size();
    }
    
    // force refresh the shared inventory
    public void refresh() {
        if (sharedInventory != null) {
            updateInventory(sharedInventory);
        }
    }
}