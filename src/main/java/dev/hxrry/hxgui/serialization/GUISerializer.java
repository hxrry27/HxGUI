package dev.hxrry.hxgui.serialization;

import dev.hxrry.hxgui.builders.GUIBuilder;
import dev.hxrry.hxgui.core.Menu;
import dev.hxrry.hxgui.core.MenuItem;
import dev.hxrry.hxgui.utils.SlotPattern;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;

public class GUISerializer {
    
    private final Plugin plugin;
    private final ItemSerializer itemSerializer;
    private final Map<String, Consumer<Player>> actions = new HashMap<>();
    
    public GUISerializer(Plugin plugin) {
        this.plugin = plugin;
        this.itemSerializer = new ItemSerializer(plugin);
        
        // register default actions
        registerDefaultActions();
    }
    
    // load gui from file
    public Menu load(File file) {
        if (!file.exists()) {
            plugin.getLogger().warning("gui file not found: " + file.getName());
            return null;
        }
        
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        return load(config);
    }
    
    // load gui from config section
    public Menu load(ConfigurationSection config) {
        // get basic settings
        String title = config.getString("title", "GUI");
        int rows = config.getInt("rows", 3);
        boolean shared = config.getBoolean("shared", false);
        
        // start building
        GUIBuilder builder = GUIBuilder.chest()
            .title(title)
            .rows(rows)
            .shared(shared);
        
        // load pattern if exists
        if (config.contains("pattern")) {
            loadPattern(builder, config);
        }
        
        // load individual slots
        if (config.contains("slots")) {
            loadSlots(builder, config.getConfigurationSection("slots"));
        }
        
        // load items for pattern
        if (config.contains("items")) {
            loadPatternItems(builder, config.getConfigurationSection("items"));
        }
        
        return builder.build();
    }
    
    // load pattern from config
    private void loadPattern(GUIBuilder builder, ConfigurationSection config) {
        if (config.isList("pattern")) {
            // visual pattern as list of strings
            List<String> lines = config.getStringList("pattern");
            builder.pattern(lines.toArray(new String[0]));
            
        } else if (config.isConfigurationSection("pattern")) {
            ConfigurationSection patternSection = config.getConfigurationSection("pattern");
            String mode = patternSection.getString("mode", "visual");
            
            switch (mode.toLowerCase()) {
                case "visual" -> {
                    List<String> lines = patternSection.getStringList("lines");
                    builder.pattern(SlotPattern.fromVisual(lines.toArray(new String[0])));
                }
                case "slots" -> {
                    Map<Integer, Character> slots = new HashMap<>();
                    ConfigurationSection slotsSection = patternSection.getConfigurationSection("slots");
                    if (slotsSection != null) {
                        for (String key : slotsSection.getKeys(false)) {
                            int slot = Integer.parseInt(key);
                            char c = slotsSection.getString(key).charAt(0);
                            slots.put(slot, c);
                        }
                    }
                    builder.pattern(SlotPattern.fromSlots(slots));
                }
                case "regions" -> {
                    Map<String, List<Integer>> regions = new HashMap<>();
                    ConfigurationSection regionsSection = patternSection.getConfigurationSection("regions");
                    if (regionsSection != null) {
                        for (String key : regionsSection.getKeys(false)) {
                            regions.put(key, regionsSection.getIntegerList(key));
                        }
                    }
                    builder.pattern(SlotPattern.fromRegions(regions));
                }
            }
        }
    }
    
    // load items for pattern
    private void loadPatternItems(GUIBuilder builder, ConfigurationSection items) {
        for (String key : items.getKeys(false)) {
            if (key.length() != 1) continue; // must be single char
            
            char c = key.charAt(0);
            ConfigurationSection itemSection = items.getConfigurationSection(key);
            
            if (itemSection != null) {
                ItemStack item = itemSerializer.load(itemSection);
                String action = itemSection.getString("action");
                
                if (action != null && actions.containsKey(action)) {
                    builder.item(c, item, event -> {
                        actions.get(action).accept((Player) event.getWhoClicked());
                    });
                } else {
                    builder.item(c, item);
                }
            }
        }
    }
    
    // load individual slot items
    private void loadSlots(GUIBuilder builder, ConfigurationSection slots) {
        for (String key : slots.getKeys(false)) {
            try {
                int slot = Integer.parseInt(key);
                ConfigurationSection itemSection = slots.getConfigurationSection(key);
                
                if (itemSection != null) {
                    ItemStack item = itemSerializer.load(itemSection);
                    String action = itemSection.getString("action");
                    
                    if (action != null && actions.containsKey(action)) {
                        builder.item(slot, item, event -> {
                            actions.get(action).accept((Player) event.getWhoClicked());
                        });
                    } else {
                        builder.item(slot, item);
                    }
                }
            } catch (NumberFormatException e) {
                plugin.getLogger().warning("invalid slot number: " + key);
            }
        }
    }
    
    // save gui to file
    public void save(Menu menu, File file) {
        YamlConfiguration config = new YamlConfiguration();
        save(menu, config);
        
        try {
            config.save(file);
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "failed to save gui", e);
        }
    }
    
    // save gui to config
    public void save(Menu menu, ConfigurationSection config) {
        config.set("title", menu.getTitle().toString());
        config.set("rows", menu.getRows());
        
        // save items
        ConfigurationSection slots = config.createSection("slots");
        for (Map.Entry<Integer, MenuItem> entry : menu.getItems().entrySet()) {
            ConfigurationSection slot = slots.createSection(String.valueOf(entry.getKey()));
            itemSerializer.save(entry.getValue().getItem(), slot);
        }
    }
    
    // register custom action
    public void registerAction(String name, Consumer<Player> action) {
        actions.put(name, action);
    }
    
    // register default actions
    private void registerDefaultActions() {
        actions.put("close", Player::closeInventory);
        actions.put("back", player -> player.performCommand("back"));
        actions.put("menu", player -> player.performCommand("menu"));
    }
    
    // load all guis from directory
    public Map<String, Menu> loadDirectory(File directory) {
        Map<String, Menu> guis = new HashMap<>();
        
        if (!directory.exists() || !directory.isDirectory()) {
            return guis;
        }
        
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return guis;
        
        for (File file : files) {
            String name = file.getName().replace(".yml", "");
            Menu menu = load(file);
            if (menu != null) {
                guis.put(name, menu);
                plugin.getLogger().info("loaded gui: " + name);
            }
        }
        
        return guis;
    }
}