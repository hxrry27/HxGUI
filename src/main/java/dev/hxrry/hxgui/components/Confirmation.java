package dev.hxrry.hxgui.components;

import dev.hxrry.hxgui.builders.ItemBuilder;
import dev.hxrry.hxgui.core.MenuItem;
import dev.hxrry.hxgui.core.PersonalMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Confirmation {
    
    private final PersonalMenu menu;
    private Runnable onConfirm;
    private Runnable onCancel;
    
    // customizable slots
    private int confirmSlot = 11;
    private int cancelSlot = 15;
    private int infoSlot = 13;
    
    // customizable items
    private ItemStack confirmItem;
    private ItemStack cancelItem;
    private ItemStack infoItem;
    
    public Confirmation(String title) {
        this(Component.text(title));
    }
    
    public Confirmation(Component title) {
        // default 3 row inventory
        this.menu = new PersonalMenu(title, 3);
        
        // default items
        this.confirmItem = new ItemBuilder(Material.LIME_WOOL)
            .name("<green><bold>CONFIRM")
            .lore("<gray>Click to confirm this action")
            .glow()
            .build();
            
        this.cancelItem = new ItemBuilder(Material.RED_WOOL)
            .name("<red><bold>CANCEL")
            .lore("<gray>Click to cancel")
            .glow()
            .build();
            
        this.infoItem = new ItemBuilder(Material.PAPER)
            .name("<yellow>Are you sure?")
            .lore("<gray>This action cannot be undone!")
            .build();
        
        // fill background with gray glass
        ItemStack filler = ItemBuilder.filler(Material.GRAY_STAINED_GLASS_PANE);
        for (int i = 0; i < 27; i++) {
            menu.setItem(i, filler);
        }
    }
    
    // set what happens on confirm
    public Confirmation onConfirm(Runnable action) {
        this.onConfirm = action;
        return this;
    }
    
    // set what happens on cancel
    public Confirmation onCancel(Runnable action) {
        this.onCancel = action;
        return this;
    }
    
    // customize slots
    public Confirmation slots(int confirm, int cancel, int info) {
        this.confirmSlot = confirm;
        this.cancelSlot = cancel;
        this.infoSlot = info;
        return this;
    }
    
    // customize items
    public Confirmation confirmItem(ItemStack item) {
        this.confirmItem = item;
        return this;
    }
    
    public Confirmation cancelItem(ItemStack item) {
        this.cancelItem = item;
        return this;
    }
    
    public Confirmation infoItem(ItemStack item) {
        this.infoItem = item;
        return this;
    }
    
    // set the question/info item with builder
    public Confirmation question(String name, String... lore) {
        this.infoItem = new ItemBuilder(Material.PAPER)
            .name(name)
            .lore(lore)
            .build();
        return this;
    }
    
    // build and setup the menu
    private void setupMenu(Player player) {
        // confirm button
        menu.setItem(confirmSlot, new MenuItem(confirmItem, event -> {
            player.closeInventory();
            if (onConfirm != null) {
                onConfirm.run();
            }
        }));
        
        // cancel button
        menu.setItem(cancelSlot, new MenuItem(cancelItem, event -> {
            player.closeInventory();
            if (onCancel != null) {
                onCancel.run();
            }
        }));
        
        // info item
        if (infoItem != null) {
            menu.setItem(infoSlot, infoItem);
        }
    }
    
    // open for player
    public void open(Player player) {
        setupMenu(player);
        menu.open(player);
    }
    
    // static factory for quick confirmations
    public static void confirm(Player player, String question, Runnable onConfirm) {
        new Confirmation("<red>Confirmation Required")
            .question("<yellow>" + question)
            .onConfirm(onConfirm)
            .onCancel(() -> player.sendMessage("<red>Action cancelled"))
            .open(player);
    }
    
    // dangerous action confirmation with extra warning
    public static void dangerous(Player player, String action, Runnable onConfirm) {
        Confirmation conf = new Confirmation("<dark_red><bold>⚠ DANGEROUS ACTION ⚠");
        
        // scary looking items
        conf.confirmItem = new ItemBuilder(Material.TNT)
            .name("<red><bold>YES, I'M SURE")
            .lore(
                "<red>I understand this action is permanent",
                "<red>and cannot be reversed!"
            )
            .build();
            
        conf.cancelItem = new ItemBuilder(Material.LIME_WOOL)
            .name("<green><bold>NO, GO BACK")
            .lore("<green>Cancel this action")
            .glow()
            .build();
            
        conf.infoItem = new ItemBuilder(Material.BARRIER)
            .name("<red><bold>" + action)
            .lore(
                "",
                "<yellow>⚠ This action is PERMANENT",
                "<yellow>⚠ It cannot be undone",
                "<yellow>⚠ All data will be lost",
                "",
                "<gray>Click the TNT to confirm",
                "<gray>Click the green wool to cancel"
            )
            .build();
            
        conf.onConfirm(onConfirm)
            .onCancel(() -> player.sendMessage("<green>Action cancelled - nothing was changed"))
            .open(player);
    }
    
    // get the menu for advanced usage
    public PersonalMenu getMenu() {
        return menu;
    }
}