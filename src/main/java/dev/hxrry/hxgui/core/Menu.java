package dev.hxrry.hxgui.core;

import dev.hxrry.hxgui.HxGUI;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public abstract class Menu implements InventoryHolder {
    
    protected final Component title;
    protected final int rows;
    protected final InventoryType type;
    protected final Map<Integer, MenuItem> items = new HashMap<>();
    protected final Map<UUID, Inventory> inventories = new HashMap<>();
    
    // constructor for chest inventories
    public Menu(Component title, int rows) {
        this.title = title;
        this.rows = rows;
        this.type = null;
    }
    
    // constructor for other inventory types
    public Menu(Component title, InventoryType type) {
        this.title = title;
        this.rows = 0;
        this.type = type;
    }
    
    // legacy string title support
    public Menu(String title, int rows) {
        this(Component.text(title), rows);
    }
    
    // create the inventory for a player
    protected Inventory createInventory() {
        if (type != null) {
            return Bukkit.createInventory(this, type, title);
        } else {
            return Bukkit.createInventory(this, rows * 9, title);
        }
    }
    
    // get inventory for specific player
    public abstract Inventory getInventory(Player player);
    
    // base inventory holder implementation
    @Override
    public Inventory getInventory() {
        // return first inventory or create new one
        if (inventories.isEmpty()) {
            return createInventory();
        }
        return inventories.values().iterator().next();
    }
    
    // set an item in the menu
    public void setItem(int slot, MenuItem item) {
        items.put(slot, item);
        // update all open inventories
        updateAll();
    }
    
    public void setItem(int slot, ItemStack item) {
        setItem(slot, new MenuItem(item, null));
    }
    
    public void setItem(int slot, ItemStack item, Consumer<InventoryClickEvent> clickHandler) {
        setItem(slot, new MenuItem(item, clickHandler));
    }
    
    // remove an item
    public void removeItem(int slot) {
        items.remove(slot);
        updateAll();
    }
    
    // get item at slot
    public MenuItem getItem(int slot) {
        return items.get(slot);
    }
    
    // open for player
    public void open(Player player) {
        Inventory inv = getInventory(player);
        
        // populate inventory with items
        for (Map.Entry<Integer, MenuItem> entry : items.entrySet()) {
            inv.setItem(entry.getKey(), entry.getValue().getItem());
        }
        
        // register with manager
        HxGUI.getInstance().getMenuManager().registerMenu(player, this);
        
        // open the inventory
        player.openInventory(inv);
    }
    
    // close for player
    public void close(Player player) {
        player.closeInventory();
        onClose(player);
    }
    
    // close for all players
    public void closeAll() {
        // make a copy to avoid concurrent modification
        for (UUID uuid : new HashMap<>(inventories).keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                close(player);
            }
        }
    }
    
    // update all open inventories
    public void updateAll() {
        for (Map.Entry<UUID, Inventory> entry : inventories.entrySet()) {
            updateInventory(entry.getValue());
        }
    }
    
    // update specific inventory
    protected void updateInventory(Inventory inv) {
        // clear then repopulate
        inv.clear();
        for (Map.Entry<Integer, MenuItem> entry : items.entrySet()) {
            inv.setItem(entry.getKey(), entry.getValue().getItem());
        }
    }
    
    // handle click
    public void handleClick(InventoryClickEvent event) {
        // cancel by default to prevent item taking
        event.setCancelled(true);
        
        int slot = event.getRawSlot();
        
        // check if click is in our inventory
        if (slot < 0 || slot >= event.getInventory().getSize()) {
            return;
        }
        
        MenuItem item = items.get(slot);
        if (item != null && item.getClickHandler() != null) {
            // run the click handler
            item.getClickHandler().accept(event);
        }
    }
    
    // cleanup when closed
    public void onClose(Player player) {
        inventories.remove(player.getUniqueId());
        HxGUI.getInstance().getMenuManager().unregisterMenu(player);
    }
    
    // getters
    public Component getTitle() {
        return title;
    }
    
    public int getRows() {
        return rows;
    }
    
    public int getSize() {
        return type != null ? type.getDefaultSize() : rows * 9;
    }
    
    public Map<Integer, MenuItem> getItems() {
        return new HashMap<>(items);
    }
}