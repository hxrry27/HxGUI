package dev.hxrry.hxgui.serialization;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import io.papermc.paper.registry.RegistryAccess;  // use paper's registry
import io.papermc.paper.registry.RegistryKey;     // add this too

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class ItemSerializer {
    
    private final Plugin plugin;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    
    public ItemSerializer(Plugin plugin) {
        this.plugin = plugin;
    }
    
    // load item from config
    public ItemStack load(ConfigurationSection section) {
        if (section == null) return null;
        
        // get material
        String materialName = section.getString("material", "STONE");
        Material material;
        try {
            material = Material.valueOf(materialName.toUpperCase());
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("invalid material: " + materialName);
            material = Material.STONE;
        }
        
        // create item
        ItemStack item = new ItemStack(material);
        
        // set amount
        int amount = section.getInt("amount", 1);
        item.setAmount(amount);
        
        // get meta
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        
        // set display name
        if (section.contains("name")) {
            String name = section.getString("name");
            Component component = miniMessage.deserialize(name);
            meta.displayName(component);
        }
        
        // set lore
        if (section.contains("lore")) {
            List<String> loreStrings = section.getStringList("lore");
            List<Component> lore = new ArrayList<>();
            for (String line : loreStrings) {
                lore.add(miniMessage.deserialize(line));
            }
            meta.lore(lore);
        }
        
        // add enchantments
        if (section.contains("enchantments")) {
            ConfigurationSection enchants = section.getConfigurationSection("enchantments");
            if (enchants != null) {
                for (String key : enchants.getKeys(false)) {
                    try {
                        // use paper's registry access for 1.21+
                        Enchantment enchant = RegistryAccess.registryAccess()
                            .getRegistry(RegistryKey.ENCHANTMENT)
                            .get(NamespacedKey.minecraft(key.toLowerCase()));
                        
                        if (enchant != null) {
                            int level = enchants.getInt(key);
                            meta.addEnchant(enchant, level, true);
                        } else {
                            plugin.getLogger().warning("enchantment not found: " + key);
                        }
                    } catch (Exception e) {
                        plugin.getLogger().warning("invalid enchantment: " + key);
                    }
                }
            }
        }
        
        // add flags
        if (section.contains("flags")) {
            List<String> flags = section.getStringList("flags");
            for (String flag : flags) {
                try {
                    ItemFlag itemFlag = ItemFlag.valueOf(flag.toUpperCase());
                    meta.addItemFlags(itemFlag);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("invalid item flag: " + flag);
                }
            }
        }
        
        // set unbreakable
        if (section.contains("unbreakable")) {
            meta.setUnbreakable(section.getBoolean("unbreakable"));
        }
        
        // set custom model data
        if (section.contains("custom-model-data")) {
            meta.setCustomModelData(section.getInt("custom-model-data"));
        }
        
        // add glow effect
        if (section.getBoolean("glow", false)) {
            meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        
        // apply meta
        item.setItemMeta(meta);
        
        return item;
    }
    
    // save item to config
    public void save(ItemStack item, ConfigurationSection section) {
        if (item == null || section == null) return;
        
        // save material
        section.set("material", item.getType().name());
        
        // save amount if not 1
        if (item.getAmount() != 1) {
            section.set("amount", item.getAmount());
        }
        
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        
        // save display name
        if (meta.hasDisplayName()) {
            Component name = meta.displayName();
            if (name != null) {
                section.set("name", miniMessage.serialize(name));
            }
        }
        
        // save lore
        if (meta.hasLore()) {
            List<Component> lore = meta.lore();
            if (lore != null && !lore.isEmpty()) {
                List<String> loreStrings = new ArrayList<>();
                for (Component line : lore) {
                    loreStrings.add(miniMessage.serialize(line));
                }
                section.set("lore", loreStrings);
            }
        }
        
        // save enchantments
        if (meta.hasEnchants()) {
            ConfigurationSection enchants = section.createSection("enchantments");
            for (Enchantment enchant : meta.getEnchants().keySet()) {
                String key = enchant.getKey().getKey();
                int level = meta.getEnchantLevel(enchant);
                enchants.set(key, level);
            }
        }
        
        // save flags
        if (!meta.getItemFlags().isEmpty()) {
            List<String> flags = new ArrayList<>();
            for (ItemFlag flag : meta.getItemFlags()) {
                flags.add(flag.name());
            }
            section.set("flags", flags);
        }
        
        // save unbreakable
        if (meta.isUnbreakable()) {
            section.set("unbreakable", true);
        }
        
        // save custom model data
        if (meta.hasCustomModelData()) {
            section.set("custom-model-data", meta.getCustomModelData());
        }
    }
    
    // quick load from string format
    public ItemStack quickLoad(String data) {
        // format: "MATERIAL:amount:name"
        String[] parts = data.split(":");
        if (parts.length == 0) return null;
        
        try {
            Material material = Material.valueOf(parts[0].toUpperCase());
            ItemStack item = new ItemStack(material);
            
            if (parts.length > 1) {
                item.setAmount(Integer.parseInt(parts[1]));
            }
            
            if (parts.length > 2) {
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    meta.displayName(miniMessage.deserialize(parts[2]));
                    item.setItemMeta(meta);
                }
            }
            
            return item;
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "failed to quick load item: " + data, e);
            return null;
        }
    }
    
    // quick save to string format
    public String quickSave(ItemStack item) {
        if (item == null) return "AIR:1:";
        
        StringBuilder data = new StringBuilder();
        data.append(item.getType().name());
        data.append(":");
        data.append(item.getAmount());
        
        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.hasDisplayName()) {
            data.append(":");
            data.append(miniMessage.serialize(meta.displayName()));
        }
        
        return data.toString();
    }
}