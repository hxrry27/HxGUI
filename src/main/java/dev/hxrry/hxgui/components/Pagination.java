package dev.hxrry.hxgui.components;

import dev.hxrry.hxgui.builders.ItemBuilder;
import dev.hxrry.hxgui.core.MenuItem;
import dev.hxrry.hxgui.core.PersonalMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Pagination {
    
    private final PersonalMenu menu;
    private final List<MenuItem> allItems = new ArrayList<>();
    private final Map<UUID, Integer> playerPages = new HashMap<>();
    
    // slots for content (default: entire inventory minus bottom row)
    private int startSlot = 0;
    private int endSlot = 44;
    
    // navigation button slots
    private int previousSlot = 45;
    private int nextSlot = 53;
    private int infoSlot = 49;
    
    // navigation items
    private ItemStack previousItem;
    private ItemStack nextItem;
    private ItemStack noMorePagesItem;
    
    // items per page
    private int itemsPerPage;
    
    public Pagination(String title, int rows) {
        this(Component.text(title), rows);
    }
    
    public Pagination(Component title, int rows) {
        this.menu = new PersonalMenu(title, rows);
        this.itemsPerPage = (rows - 1) * 9; // leave bottom row for navigation
        
        // default navigation items
        this.previousItem = new ItemBuilder(Material.ARROW)
            .name("<green>Previous Page")
            .lore("<gray>Click to go back")
            .build();
            
        this.nextItem = new ItemBuilder(Material.ARROW)
            .name("<green>Next Page")
            .lore("<gray>Click to continue")
            .build();
            
        this.noMorePagesItem = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
            .name("<gray>No more pages")
            .build();
    }
    
    // configure content area
    public Pagination contentArea(int startSlot, int endSlot) {
        this.startSlot = startSlot;
        this.endSlot = endSlot;
        this.itemsPerPage = endSlot - startSlot + 1;
        return this;
    }
    
    // configure navigation slots
    public Pagination navigationSlots(int previous, int next, int info) {
        this.previousSlot = previous;
        this.nextSlot = next;
        this.infoSlot = info;
        return this;
    }
    
    // set custom navigation items
    public Pagination previousItem(ItemStack item) {
        this.previousItem = item;
        return this;
    }
    
    public Pagination nextItem(ItemStack item) {
        this.nextItem = item;
        return this;
    }
    
    public Pagination noMorePagesItem(ItemStack item) {
        this.noMorePagesItem = item;
        return this;
    }
    
    // add items to paginate
    public Pagination addItem(MenuItem item) {
        allItems.add(item);
        return this;
    }
    
    public Pagination addItem(ItemStack item) {
        allItems.add(new MenuItem(item, null));
        return this;
    }
    
    public Pagination addItems(List<MenuItem> items) {
        allItems.addAll(items);
        return this;
    }
    
    // set all items at once
    public Pagination setItems(List<MenuItem> items) {
        allItems.clear();
        allItems.addAll(items);
        return this;
    }
    
    // get current page for player
    public int getPage(Player player) {
        return playerPages.getOrDefault(player.getUniqueId(), 0);
    }
    
    // get total pages
    public int getTotalPages() {
        return (int) Math.ceil((double) allItems.size() / itemsPerPage);
    }
    
    // set page for player
    public void setPage(Player player, int page) {
        // clamp to valid range
        page = Math.max(0, Math.min(page, getTotalPages() - 1));
        playerPages.put(player.getUniqueId(), page);
        updatePage(player);
    }
    
    // update the display for a player
    private void updatePage(Player player) {
        int page = getPage(player);
        int totalPages = getTotalPages();
        
        // clear content area
        for (int slot = startSlot; slot <= endSlot; slot++) {
            menu.setItemFor(player, slot, MenuItem.empty());
        }
        
        // add items for current page
        int startIndex = page * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, allItems.size());
        
        int slot = startSlot;
        for (int i = startIndex; i < endIndex && slot <= endSlot; i++) {
            menu.setItemFor(player, slot, allItems.get(i));
            slot++;
        }
        
        // add navigation buttons
        if (page > 0) {
            // previous button
            menu.setItemFor(player, previousSlot, new MenuItem(previousItem, event -> {
                setPage(player, page - 1);
            }));
        } else {
            // no previous pages
            menu.setItemFor(player, previousSlot, new MenuItem(noMorePagesItem));
        }
        
        if (page < totalPages - 1) {
            // next button
            menu.setItemFor(player, nextSlot, new MenuItem(nextItem, event -> {
                setPage(player, page + 1);
            }));
        } else {
            // no more pages
            menu.setItemFor(player, nextSlot, new MenuItem(noMorePagesItem));
        }
        
        // page info
        if (infoSlot >= 0) {
            ItemStack infoItem = new ItemBuilder(Material.BOOK)
                .name("<yellow>Page " + (page + 1) + " of " + totalPages)
                .lore("<gray>Total items: " + allItems.size())
                .build();
            menu.setItemFor(player, infoSlot, new MenuItem(infoItem));
        }
    }
    
    // open for player
    public void open(Player player) {
        // initialize page if needed
        if (!playerPages.containsKey(player.getUniqueId())) {
            playerPages.put(player.getUniqueId(), 0);
        }
        
        // update and open
        updatePage(player);
        menu.open(player);
    }
    
    // next page for player
    public void nextPage(Player player) {
        setPage(player, getPage(player) + 1);
    }
    
    // previous page for player
    public void previousPage(Player player) {
        setPage(player, getPage(player) - 1);
    }
    
    // cleanup when player closes
    public void onClose(Player player) {
        playerPages.remove(player.getUniqueId());
    }
    
    // get the underlying menu
    public PersonalMenu getMenu() {
        return menu;
    }
}