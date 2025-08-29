package dev.hxrry.hxgui;

import org.bukkit.plugin.java.JavaPlugin;
import dev.hxrry.hxgui.core.MenuManager;

public class HxGUI {
    
    private static HxGUI instance;
    private final JavaPlugin hostPlugin;
    private MenuManager menuManager;
    
    // Private constructor - use init() to create
    private HxGUI(JavaPlugin plugin) {
        this.hostPlugin = plugin;
        this.menuManager = new MenuManager(plugin);
        
        plugin.getLogger().info("HxGUI library initialized");
    }
    
    /**
     * Initialize the HxGUI library with your plugin
     * Call this in your plugin's onEnable()
     * 
     * @param plugin Your plugin instance
     * @return The HxGUI instance
     */
    public static HxGUI init(JavaPlugin plugin) {
        if (instance != null) {
            plugin.getLogger().warning("HxGUI already initialized!");
            return instance;
        }
        
        instance = new HxGUI(plugin);
        return instance;
    }
    
    /**
     * Shutdown the library
     * Call this in your plugin's onDisable()
     */
    public static void shutdown() {
        if (instance != null && instance.menuManager != null) {
            instance.menuManager.closeAll();
            instance = null;
        }
    }
    
    /**
     * Get the instance (will be null if not initialized)
     */
    public static HxGUI getInstance() {
        return instance;
    }
    
    /**
     * Get the menu manager
     */
    public MenuManager getMenuManager() {
        return menuManager;
    }
    
    /**
     * Get the host plugin
     */
    public JavaPlugin getPlugin() {
        return hostPlugin;
    }
    
    /**
     * Check if the library is initialized
     */
    public static boolean isInitialized() {
        return instance != null;
    }
}