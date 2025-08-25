package dev.hxrry.hxgui.builders;

import dev.hxrry.hxgui.core.Menu;
import dev.hxrry.hxgui.core.MenuItem;
import dev.hxrry.hxgui.utils.SlotPattern;
import dev.hxrry.hxgui.core.SharedMenu;
import dev.hxrry.hxgui.core.PersonalMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class GUIBuilder {
    
    private Component title = Component.text("GUI");
    private int rows = 3;
    private InventoryType type = null;
    private final Map<Integer, MenuItem> items = new HashMap<>();
    private final Map<Character, MenuItem> patternItems = new HashMap<>();
    private SlotPattern pattern = null;
    private boolean shared = false;
    
    // private constructor - use static methods
    private GUIBuilder() {}
    
    // factory methods for different inventory types
    public static GUIBuilder chest() {
        return new GUIBuilder();
    }
    
    public static GUIBuilder hopper() {
        GUIBuilder builder = new GUIBuilder();
        builder.type = InventoryType.HOPPER;
        return builder;
    }
    
    public static GUIBuilder dispenser() {
        GUIBuilder builder = new GUIBuilder();
        builder.type = InventoryType.DISPENSER;
        return builder;
    }
    
    // title setting
    public GUIBuilder title(String title) {
        this.title = Component.text(title);
        return this;
    }
    
    public GUIBuilder title(Component title) {
        this.title = title;
        return this;
    }
    
    // size setting
    public GUIBuilder rows(int rows) {
        if (rows < 1 || rows > 6) {
            throw new IllegalArgumentException("rows must be between 1 and 6");
        }
        this.rows = rows;
        return this;
    }
    
    public GUIBuilder size(int size) {
        // convert size to rows
        this.rows = (int) Math.ceil(size / 9.0);
        return this;
    }
    
    // memory optimization
    public GUIBuilder shared(boolean shared) {
        this.shared = shared;
        return this;
    }
    
    // add items
    public GUIBuilder item(int slot, ItemStack item) {
        items.put(slot, new MenuItem(item, null));
        return this;
    }
    
    public GUIBuilder item(int slot, ItemStack item, Consumer<InventoryClickEvent> clickHandler) {
        items.put(slot, new MenuItem(item, clickHandler));
        return this;
    }
    
    public GUIBuilder item(int slot, ItemBuilder item) {
        items.put(slot, new MenuItem(item.build(), null));
        return this;
    }
    
    public GUIBuilder item(int slot, ItemBuilder item, Consumer<InventoryClickEvent> clickHandler) {
        items.put(slot, new MenuItem(item.build(), clickHandler));
        return this;
    }
    
    // pattern support
    public GUIBuilder pattern(SlotPattern pattern) {
        this.pattern = pattern;
        return this;
    }
    
    public GUIBuilder pattern(String... rows) {
        this.pattern = SlotPattern.fromVisual(rows);
        this.rows = rows.length;
        return this;
    }
    
    // pattern items
    public GUIBuilder item(char key, ItemStack item) {
        patternItems.put(key, new MenuItem(item, null));
        return this;
    }
    
    public GUIBuilder item(char key, ItemStack item, Consumer<InventoryClickEvent> clickHandler) {
        patternItems.put(key, new MenuItem(item, clickHandler));
        return this;
    }
    
    public GUIBuilder item(char key, ItemBuilder item) {
        patternItems.put(key, new MenuItem(item.build(), null));
        return this;
    }
    
    public GUIBuilder item(char key, ItemBuilder item, Consumer<InventoryClickEvent> clickHandler) {
        patternItems.put(key, new MenuItem(item.build(), clickHandler));
        return this;
    }
    
    // fill remaining slots
    public GUIBuilder fill(ItemStack item) {
        // fill all empty slots with item
        MenuItem filler = new MenuItem(item, null);
        int size = rows * 9;
        for (int i = 0; i < size; i++) {
            items.putIfAbsent(i, filler);
        }
        return this;
    }
    
    public GUIBuilder fill(Material material) {
        return fill(new ItemStack(material));
    }
    
    // build the final menu
    public Menu build() {
        // merge pattern items if pattern exists
        if (pattern != null) {
            Map<Integer, Character> slots = pattern.getSlotMap();
            for (Map.Entry<Integer, Character> entry : slots.entrySet()) {
                MenuItem item = patternItems.get(entry.getValue());
                if (item != null) {
                    items.put(entry.getKey(), item);
                }
            }
        }
        
        // create the menu
        Menu menu;
        if (type != null) {
        // special scenarios
            menu = shared ? 
                new SharedMenu(title, type) : 
                new PersonalMenu(title, type);
        } else {
            // for chest inventories
            menu = shared ? 
                new SharedMenu(title, rows) : 
                new PersonalMenu(title, rows);
        }
        
        // add all items
        for (Map.Entry<Integer, MenuItem> entry : items.entrySet()) {
            menu.setItem(entry.getKey(), entry.getValue());
        }
        
        return menu;
    }
    
    // build and open immediately
    public Menu open(Player player) {
        Menu menu = build();
        menu.open(player);
        return menu;
    }
}