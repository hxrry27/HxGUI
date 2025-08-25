package dev.hxrry.hxgui;

import org.bukkit.plugin.java.JavaPlugin;
import dev.hxrry.hxgui.core.MenuManager;

public class HxGUI extends JavaPlugin {
    
    private static HxGUI instance;
    private MenuManager menuManager;
    
    @Override
    public void onEnable() {
        instance = this;
        
        // initialize the menu manager and register events
        menuManager = new MenuManager(this);
        
        // register metrics if needed
        // todo: add bstats later if we want
        
        getLogger().info("HxGUI enabled - gui library ready for use");
    }
    
    @Override
    public void onDisable() {
        // close all open menus to prevent weirdness
        if (menuManager != null) {
            menuManager.closeAll();
        }
        
        getLogger().info("HxGUI disabled");
        instance = null;
    }
    
    // static access for other plugins
    public static HxGUI getInstance() {
        return instance;
    }
    
    // get the menu manager for internal use
    public MenuManager getMenuManager() {
        return menuManager;
    }
    
    // convenience method for other plugins
    public static void init(JavaPlugin plugin) {
        if (instance == null) {
            plugin.getLogger().warning("HxGUI is not loaded! make sure it's in your plugins folder");
        }
    }
}