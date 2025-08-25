package dev.hxrry.hxgui.templates;

import dev.hxrry.hxgui.core.Menu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public interface Template {
    
    // get the title for this template
    String getTitle();
    
    // get the size in rows
    int getRows();
    
    // get the default items for this template
    Map<Integer, ItemStack> getDefaultItems();
    
    // apply this template to a menu
    void apply(Menu menu);
    
    // open this template for a player
    void open(Player player);
    
    // validate if this template can be used
    default boolean isValid() {
        return getRows() > 0 && getRows() <= 6;
    }
    
    // get template type name
    default String getType() {
        return this.getClass().getSimpleName();
    }
    
    // clone this template
    Template clone();
}