package dev.hxrry.hxgui.templates;

import dev.hxrry.hxgui.builders.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Theme {
    
    // valesmp color scheme
    public static final String PRIMARY_COLOR = "<#00ff88>";   // mint green
    public static final String SECONDARY_COLOR = "<#0088ff>"; // sky blue
    public static final String ACCENT_COLOR = "<gold>";        // gold
    public static final String ERROR_COLOR = "<red>";          // red
    public static final String SUCCESS_COLOR = "<green>";      // green
    public static final String TEXT_COLOR = "<gray>";          // gray
    public static final String HIGHLIGHT_COLOR = "<yellow>";   // yellow
    
    // default materials
    public static final Material BORDER_MATERIAL = Material.GRAY_STAINED_GLASS_PANE;
    public static final Material FILLER_MATERIAL = Material.BLACK_STAINED_GLASS_PANE;
    public static final Material CONFIRM_MATERIAL = Material.LIME_WOOL;
    public static final Material CANCEL_MATERIAL = Material.RED_WOOL;
    public static final Material INFO_MATERIAL = Material.PAPER;
    public static final Material NEXT_MATERIAL = Material.ARROW;
    public static final Material BACK_MATERIAL = Material.ARROW;
    public static final Material CLOSE_MATERIAL = Material.BARRIER;
    
    // pre-built themed items
    
    public static ItemStack border() {
        return new ItemBuilder(BORDER_MATERIAL)
            .name(" ")
            .build();
    }
    
    public static ItemStack filler() {
        return new ItemBuilder(FILLER_MATERIAL)
            .name(" ")
            .build();
    }
    
    public static ItemStack nextButton() {
        return new ItemBuilder(NEXT_MATERIAL)
            .name(PRIMARY_COLOR + "Next Page →")
            .lore(TEXT_COLOR + "Click to go forward")
            .build();
    }
    
    public static ItemStack backButton() {
        return new ItemBuilder(BACK_MATERIAL)
            .name(PRIMARY_COLOR + "← Previous Page")
            .lore(TEXT_COLOR + "Click to go back")
            .build();
    }
    
    public static ItemStack closeButton() {
        return new ItemBuilder(CLOSE_MATERIAL)
            .name(ERROR_COLOR + "Close")
            .lore(TEXT_COLOR + "Click to exit")
            .build();
    }
    
    public static ItemStack confirmButton() {
        return new ItemBuilder(CONFIRM_MATERIAL)
            .name(SUCCESS_COLOR + "✓ Confirm")
            .lore(TEXT_COLOR + "Click to confirm")
            .glow()
            .build();
    }
    
    public static ItemStack cancelButton() {
        return new ItemBuilder(CANCEL_MATERIAL)
            .name(ERROR_COLOR + "✗ Cancel")
            .lore(TEXT_COLOR + "Click to cancel")
            .build();
    }
    
    public static ItemStack infoItem(String title, String... lore) {
        return new ItemBuilder(INFO_MATERIAL)
            .name(HIGHLIGHT_COLOR + title)
            .lore(lore)
            .build();
    }
    
    // themed title formatting
    public static String formatTitle(String title) {
        return PRIMARY_COLOR + "» " + SECONDARY_COLOR + title + PRIMARY_COLOR + " «";
    }
    
    // category formatting
    public static String formatCategory(String category) {
        return ACCENT_COLOR + "▶ " + category;
    }
    
    // price formatting
    public static String formatPrice(double price) {
        return TEXT_COLOR + "Price: " + ACCENT_COLOR + "$" + String.format("%.2f", price);
    }
    
    // status formatting
    public static String formatEnabled(boolean enabled) {
        return enabled ? 
            SUCCESS_COLOR + "● Enabled" : 
            ERROR_COLOR + "● Disabled";
    }
    
    // selection formatting
    public static String formatSelected(boolean selected) {
        return selected ?
            SUCCESS_COLOR + "✓ Selected" :
            TEXT_COLOR + "Click to select";
    }
    
    // progress bar
    public static String progressBar(int current, int max, int length) {
        int filled = (int) ((double) current / max * length);
        StringBuilder bar = new StringBuilder(TEXT_COLOR + "[" + SUCCESS_COLOR);
        
        for (int i = 0; i < length; i++) {
            if (i < filled) {
                bar.append("■");
            } else {
                bar.append(TEXT_COLOR).append("■");
            }
        }
        
        bar.append(TEXT_COLOR).append("]");
        return bar.toString();
    }
    
    // apply theme to item name
    public static ItemBuilder themed(Material material, String name) {
        return new ItemBuilder(material)
            .name(PRIMARY_COLOR + name);
    }
    
    // create themed slot pattern for borders
    public static String[] borderPattern() {
        return new String[] {
            "BBBBBBBBB",
            "B       B",
            "B       B",
            "B       B",
            "B       B",
            "BBBBBBBBB"
        };
    }
    
    // create themed slot pattern for compact menu
    public static String[] compactPattern() {
        return new String[] {
            "BBBBBBBBB",
            "B       B",
            "B       B",
            "BBBBBBBBB"
        };
    }
}