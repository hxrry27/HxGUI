package dev.hxrry.hxgui.components;

import dev.hxrry.hxgui.builders.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PageIndicator {
    
    private final int currentPage;
    private final int totalPages;
    private Material material = Material.BOOK;
    private String nameFormat = "<yellow>Page %current% of %total%";
    private boolean showProgressBar = false;
    private boolean showItemCount = false;
    private int totalItems = 0;
    
    public PageIndicator(int currentPage, int totalPages) {
        // pages are 0-indexed internally but show as 1-indexed
        this.currentPage = currentPage;
        this.totalPages = totalPages;
    }
    
    // set the material
    public PageIndicator material(Material material) {
        this.material = material;
        return this;
    }
    
    // set custom name format
    public PageIndicator nameFormat(String format) {
        this.nameFormat = format;
        return this;
    }
    
    // enable progress bar in lore
    public PageIndicator showProgressBar(boolean show) {
        this.showProgressBar = show;
        return this;
    }
    
    // show total item count
    public PageIndicator showItemCount(int totalItems) {
        this.showItemCount = true;
        this.totalItems = totalItems;
        return this;
    }
    
    // build the indicator item
    public ItemStack build() {
        // format the name
        String name = nameFormat
            .replace("%current%", String.valueOf(currentPage + 1))
            .replace("%total%", String.valueOf(totalPages));
        
        ItemBuilder builder = new ItemBuilder(material).name(name);
        
        List<String> lore = new ArrayList<>();
        
        // add progress bar if enabled
        if (showProgressBar) {
            lore.add(createProgressBar());
        }
        
        // add item count if enabled
        if (showItemCount) {
            lore.add("<gray>Total items: <white>" + totalItems);
        }
        
        // add navigation hint
        if (currentPage > 0) {
            lore.add("<gray>← Previous page available");
        }
        if (currentPage < totalPages - 1) {
            lore.add("<gray>Next page available →");
        }
        
        if (!lore.isEmpty()) {
            builder.lore(lore.toArray(new String[0]));
        }
        
        return builder.build();
    }
    
    // create a visual progress bar
    private String createProgressBar() {
        int barLength = 20;
        int filled = (int) ((double) (currentPage + 1) / totalPages * barLength);
        
        StringBuilder bar = new StringBuilder("<gray>[<green>");
        
        for (int i = 0; i < barLength; i++) {
            if (i < filled) {
                bar.append("■");
            } else {
                bar.append("<gray>■");
            }
        }
        
        bar.append("<gray>]");
        return bar.toString();
    }
    
    // static factory methods for common styles
    public static ItemStack simple(int currentPage, int totalPages) {
        return new PageIndicator(currentPage, totalPages).build();
    }
    
    public static ItemStack withProgress(int currentPage, int totalPages) {
        return new PageIndicator(currentPage, totalPages)
            .showProgressBar(true)
            .build();
    }
    
    public static ItemStack detailed(int currentPage, int totalPages, int totalItems) {
        return new PageIndicator(currentPage, totalPages)
            .showProgressBar(true)
            .showItemCount(totalItems)
            .build();
    }
    
    // create a compass style indicator
    public static ItemStack compass(int currentPage, int totalPages) {
        return new PageIndicator(currentPage, totalPages)
            .material(Material.COMPASS)
            .nameFormat("<gold>Navigation <gray>(<yellow>%current%<gray>/<yellow>%total%<gray>)")
            .build();
    }
    
    // create a paper style indicator
    public static ItemStack paper(int currentPage, int totalPages) {
        return new PageIndicator(currentPage, totalPages)
            .material(Material.PAPER)
            .nameFormat("<white>Page %current% / %total%")
            .build();
    }
}