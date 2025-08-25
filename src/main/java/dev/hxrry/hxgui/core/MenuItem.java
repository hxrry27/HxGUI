package dev.hxrry.hxgui.core;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class MenuItem {
    
    private final ItemStack item;
    private final Consumer<InventoryClickEvent> clickHandler;
    
    // basic constructor
    public MenuItem(ItemStack item, Consumer<InventoryClickEvent> clickHandler) {
        this.item = item;
        this.clickHandler = clickHandler;
    }
    
    // constructor for non-clickable items
    public MenuItem(ItemStack item) {
        this(item, null);
    }
    
    // get the item
    public ItemStack getItem() {
        return item;
    }
    
    // get the click handler
    public Consumer<InventoryClickEvent> getClickHandler() {
        return clickHandler;
    }
    
    // check if clickable
    public boolean isClickable() {
        return clickHandler != null;
    }
    
    // handle click if handler exists
    public void handleClick(InventoryClickEvent event) {
        if (clickHandler != null) {
            clickHandler.accept(event);
        }
    }
    
    // create a copy with different handler
    public MenuItem withHandler(Consumer<InventoryClickEvent> newHandler) {
        return new MenuItem(item.clone(), newHandler);
    }
    
    // create a copy with different item
    public MenuItem withItem(ItemStack newItem) {
        return new MenuItem(newItem, clickHandler);
    }
    
    // static factory methods for common items
    public static MenuItem empty() {
        return new MenuItem(null, null);
    }
    
    public static MenuItem filler(ItemStack item) {
        // filler items don't do anything when clicked
        return new MenuItem(item, event -> {
            // just cancel the event, no action
            event.setCancelled(true);
        });
    }
    
    public static MenuItem closeButton(ItemStack item) {
        return new MenuItem(item, event -> {
            event.setCancelled(true);
            event.getWhoClicked().closeInventory();
        });
    }
}