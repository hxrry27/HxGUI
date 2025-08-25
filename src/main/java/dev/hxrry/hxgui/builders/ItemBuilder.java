package dev.hxrry.hxgui.builders;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ItemBuilder {
    
    private final ItemStack item;
    private final ItemMeta meta;
    
    // start with material
    public ItemBuilder(Material material) {
        this.item = new ItemStack(material);
        this.meta = item.getItemMeta();
    }
    
    // start with existing item
    public ItemBuilder(ItemStack item) {
        this.item = item.clone();
        this.meta = this.item.getItemMeta();
    }
    
    // static factory methods
    public static ItemBuilder of(Material material) {
        return new ItemBuilder(material);
    }
    
    public static ItemBuilder from(ItemStack item) {
        return new ItemBuilder(item);
    }
    
    // amount
    public ItemBuilder amount(int amount) {
        item.setAmount(amount);
        return this;
    }
    
    // display name with component
    public ItemBuilder name(Component name) {
        meta.displayName(name.decoration(TextDecoration.ITALIC, false));
        return this;
    }
    
    // display name with string
    public ItemBuilder name(String name) {
        // parse color codes automatically
        Component component = MiniMessage.miniMessage().deserialize(name)
            .decoration(TextDecoration.ITALIC, false);
        meta.displayName(component);
        return this;
    }
    
    // single lore line
    public ItemBuilder lore(String line) {
        List<Component> lore = meta.lore();
        if (lore == null) lore = new ArrayList<>();
        lore.add(MiniMessage.miniMessage().deserialize(line)
            .decoration(TextDecoration.ITALIC, false));
        meta.lore(lore);
        return this;
    }
    
    // multiple lore lines
    public ItemBuilder lore(String... lines) {
        List<Component> lore = new ArrayList<>();
        for (String line : lines) {
            lore.add(MiniMessage.miniMessage().deserialize(line)
                .decoration(TextDecoration.ITALIC, false));
        }
        meta.lore(lore);
        return this;
    }
    
    // lore with components
    public ItemBuilder lore(List<Component> lore) {
        // remove italic from all lines
        List<Component> formatted = new ArrayList<>();
        for (Component line : lore) {
            formatted.add(line.decoration(TextDecoration.ITALIC, false));
        }
        meta.lore(formatted);
        return this;
    }
    
    // add single lore line
    public ItemBuilder addLore(String line) {
        List<Component> lore = meta.lore();
        if (lore == null) lore = new ArrayList<>();
        lore.add(MiniMessage.miniMessage().deserialize(line)
            .decoration(TextDecoration.ITALIC, false));
        meta.lore(lore);
        return this;
    }
    
    // enchantments
    public ItemBuilder enchant(Enchantment enchantment, int level) {
        meta.addEnchant(enchantment, level, true);
        return this;
    }
    
    // item flags
    public ItemBuilder flags(ItemFlag... flags) {
        meta.addItemFlags(flags);
        return this;
    }
    
    // hide all attributes
    public ItemBuilder hideAttributes() {
        meta.addItemFlags(ItemFlag.values());
        return this;
    }
    
    // make it glow without showing enchants
    public ItemBuilder glow() {
        meta.addEnchant(Enchantment.UNBREAKING, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        return this;
    }
    
    // unbreakable
    public ItemBuilder unbreakable(boolean unbreakable) {
        meta.setUnbreakable(unbreakable);
        return this;
    }
    
    // custom model data for resource packs
    public ItemBuilder modelData(int data) {
        meta.setCustomModelData(data);
        return this;
    }
    
    // persistent data container for storing custom data
    public <T> ItemBuilder data(NamespacedKey key, PersistentDataType<T, T> type, T value) {
        meta.getPersistentDataContainer().set(key, type, value);
        return this;
    }
    
    // apply custom meta modifications
    public ItemBuilder meta(Consumer<ItemMeta> metaConsumer) {
        metaConsumer.accept(meta);
        return this;
    }
    
    // build the final item
    public ItemStack build() {
        item.setItemMeta(meta);
        return item;
    }
    
    // quick builder for common items
    public static ItemStack filler(Material material) {
        return new ItemBuilder(material)
            .name(" ")
            .build();
    }
    
    // quick builder for back button
    public static ItemStack backButton() {
        return new ItemBuilder(Material.ARROW)
            .name("<red>Go Back")
            .lore("<gray>Click to return to previous menu")
            .build();
    }
    
    // quick builder for close button
    public static ItemStack closeButton() {
        return new ItemBuilder(Material.BARRIER)
            .name("<red>Close")
            .lore("<gray>Click to close this menu")
            .build();
    }
}