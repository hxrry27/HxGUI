package dev.hxrry.hxgui.core;

import dev.hxrry.hxgui.HxGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MenuManager implements Listener {
    
    private final HxGUI plugin;
    private final Map<UUID, Menu> openMenus = new HashMap<>();
    
    public MenuManager(HxGUI plugin) {
        this.plugin = plugin;
        // register events
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    // register a menu as open
    public void registerMenu(Player player, Menu menu) {
        openMenus.put(player.getUniqueId(), menu);
    }
    
    // unregister a menu
    public void unregisterMenu(Player player) {
        openMenus.remove(player.getUniqueId());
    }
    
    // get open menu for player
    public Menu getOpenMenu(Player player) {
        return openMenus.get(player.getUniqueId());
    }
    
    // check if player has menu open
    public boolean hasMenuOpen(Player player) {
        return openMenus.containsKey(player.getUniqueId());
    }
    
    // close all menus
    public void closeAll() {
        // copy to avoid concurrent modification
        for (UUID uuid : new HashMap<>(openMenus).keySet()) {
            Player player = plugin.getServer().getPlayer(uuid);
            if (player != null) {
                player.closeInventory();
            }
        }
        openMenus.clear();
    }
    
    // handle inventory clicks
    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        
        Menu menu = openMenus.get(player.getUniqueId());
        if (menu == null) {
            return;
        }
        
        // check if click is in the menu inventory
        if (event.getInventory().getHolder() != menu) {
            return;
        }
        
        // let the menu handle it
        menu.handleClick(event);
    }
    
    // prevent dragging items
    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        
        Menu menu = openMenus.get(player.getUniqueId());
        if (menu == null) {
            return;
        }
        
        // check if drag is in the menu
        if (event.getInventory().getHolder() != menu) {
            return;
        }
        
        // cancel all drags in menus
        event.setCancelled(true);
    }
    
    // handle inventory close
    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) {
            return;
        }
        
        Menu menu = openMenus.get(player.getUniqueId());
        if (menu == null) {
            return;
        }
        
        // check if closing the menu inventory
        if (event.getInventory().getHolder() != menu) {
            return;
        }
        
        // clean up
        menu.onClose(player);
    }
    
    // handle player quit
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Menu menu = openMenus.get(player.getUniqueId());
        
        if (menu != null) {
            // clean up without closing inventory
            menu.onClose(player);
        }
    }
    
    // handle plugin disable
    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        // close all menus if any plugin disables
        // this prevents issues with inter-plugin dependencies
        if (!openMenus.isEmpty()) {
            closeAll();
        }
    }
    
    // get count of open menus
    public int getOpenMenuCount() {
        return openMenus.size();
    }
    
    // get all open menus
    public Map<UUID, Menu> getOpenMenus() {
        return new HashMap<>(openMenus);
    }
}