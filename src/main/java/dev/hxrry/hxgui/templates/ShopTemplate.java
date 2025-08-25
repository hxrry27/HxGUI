package dev.hxrry.hxgui.templates;

import dev.hxrry.hxgui.builders.GUIBuilder;
import dev.hxrry.hxgui.builders.ItemBuilder;
import dev.hxrry.hxgui.core.Menu;
import dev.hxrry.hxgui.core.MenuItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ShopTemplate implements Template {
    
    private String title = "Shop";
    private final Map<Integer, MenuItem> items = new HashMap<>();
    private final Map<Integer, ShopItem> shopItems = new HashMap<>();
    
    // layout settings
    private boolean showCategories = true;
    private boolean showBalance = true;
    private boolean showBorder = true;
    
    // slots for special items
    private static final int BALANCE_SLOT = 4;
    private static final int[] CATEGORY_SLOTS = {0, 1, 2, 6, 7, 8};
    private static final int[] ITEM_SLOTS = {
        10, 11, 12, 13, 14, 15, 16,
        19, 20, 21, 22, 23, 24, 25,
        28, 29, 30, 31, 32, 33, 34,
        37, 38, 39, 40, 41, 42, 43
    };
    private static final int PREVIOUS_SLOT = 45;
    private static final int NEXT_SLOT = 53;
    private static final int CLOSE_SLOT = 49;
    
    public ShopTemplate() {
        setupDefaultLayout();
    }
    
    public ShopTemplate(String title) {
        this.title = title;
        setupDefaultLayout();
    }
    
    @Override
    public String getTitle() {
        return title;
    }
    
    @Override
    public int getRows() {
        return 6; // standard shop size
    }
    
    @Override
    public Map<Integer, ItemStack> getDefaultItems() {
        Map<Integer, ItemStack> defaults = new HashMap<>();
        
        if (showBorder) {
            // add border
            ItemStack border = ItemBuilder.filler(Material.GRAY_STAINED_GLASS_PANE);
            for (int i = 0; i < 9; i++) {
                defaults.put(i, border);
            }
            defaults.put(9, border);
            defaults.put(17, border);
            defaults.put(18, border);
            defaults.put(26, border);
            defaults.put(27, border);
            defaults.put(35, border);
            defaults.put(36, border);
            defaults.put(44, border);
            for (int i = 45; i < 54; i++) {
                defaults.put(i, border);
            }
        }
        
        return defaults;
    }
    
    @Override
    public void apply(Menu menu) {
        // apply default items
        for (Map.Entry<Integer, ItemStack> entry : getDefaultItems().entrySet()) {
            menu.setItem(entry.getKey(), entry.getValue());
        }
        
        // apply configured items
        for (Map.Entry<Integer, MenuItem> entry : items.entrySet()) {
            menu.setItem(entry.getKey(), entry.getValue());
        }
    }
    
    @Override
    public void open(Player player) {
        Menu menu = GUIBuilder.chest()
            .title(title)
            .rows(6)
            .build();
            
        apply(menu);
        menu.open(player);
    }
    
    @Override
    public Template clone() {
        ShopTemplate clone = new ShopTemplate(this.title);
        clone.items.putAll(this.items);
        clone.shopItems.putAll(this.shopItems);
        clone.showCategories = this.showCategories;
        clone.showBalance = this.showBalance;
        clone.showBorder = this.showBorder;
        return clone;
    }
    
    // setup default layout
    private void setupDefaultLayout() {
        // close button
        items.put(CLOSE_SLOT, MenuItem.closeButton(
            new ItemBuilder(Material.BARRIER)
                .name("<red>Close Shop")
                .build()
        ));
    }
    
    // add a shop item
    public ShopTemplate addShopItem(ItemStack display, double price, Consumer<Player> onPurchase) {
        // find next available slot
        for (int slot : ITEM_SLOTS) {
            if (!shopItems.containsKey(slot)) {
                ShopItem item = new ShopItem(display, price, onPurchase);
                shopItems.put(slot, item);
                
                // create display item with price
                ItemStack shopDisplay = ItemBuilder.from(display)
                    .addLore("")
                    .addLore("<gray>Price: <gold>$" + price)
                    .addLore("<gray>Click to purchase")
                    .build();
                    
                items.put(slot, new MenuItem(shopDisplay, event -> {
                    Player p = (Player) event.getWhoClicked();
                    // todo: check money here when economy is added
                    item.onPurchase.accept(p);
                }));
                
                break;
            }
        }
        return this;
    }
    
    // add category button
    public ShopTemplate addCategory(String name, Material icon, Runnable onClick) {
        for (int slot : CATEGORY_SLOTS) {
            if (!items.containsKey(slot)) {
                ItemStack categoryItem = new ItemBuilder(icon)
                    .name("<yellow>" + name)
                    .lore("<gray>Click to view category")
                    .build();
                    
                items.put(slot, new MenuItem(categoryItem, event -> onClick.run()));
                break;
            }
        }
        return this;
    }
    
    // set balance display
    public ShopTemplate setBalanceItem(Player player, double balance) {
        if (!showBalance) return this;
        
        ItemStack balanceItem = new ItemBuilder(Material.EMERALD)
            .name("<green>Your Balance")
            .lore("<gray>Money: <gold>$" + balance)
            .build();
            
        items.put(BALANCE_SLOT, new MenuItem(balanceItem));
        return this;
    }
    
    // configuration methods
    public ShopTemplate showCategories(boolean show) {
        this.showCategories = show;
        return this;
    }
    
    public ShopTemplate showBalance(boolean show) {
        this.showBalance = show;
        return this;
    }
    
    public ShopTemplate showBorder(boolean show) {
        this.showBorder = show;
        return this;
    }
    
    // inner class for shop items
    private static class ShopItem {
        final ItemStack display;
        final double price;
        final Consumer<Player> onPurchase;
        
        ShopItem(ItemStack display, double price, Consumer<Player> onPurchase) {
            this.display = display;
            this.price = price;
            this.onPurchase = onPurchase;
        }
    }
}