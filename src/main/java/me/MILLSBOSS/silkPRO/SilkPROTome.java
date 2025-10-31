package me.MILLSBOSS.silkPRO;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory and validator for the "SilkPRO Tome" item traded by librarians.
 * When used, it applies the SilkPRO tag to a valid pickaxe.
 */
public final class SilkPROTome {

    private static final String TOME_TAG = "silkpro_tome";

    private SilkPROTome() {}

    public static ItemStack create(NamespacedKey pluginKey) {
        ItemStack tome = new ItemStack(Material.ENCHANTED_BOOK, 1);
        ItemMeta meta = tome.getItemMeta();
        if (meta != null) {
            // Keep default name "Enchanted Book" to match vanilla enchanted book trades
            List<String> lore = new ArrayList<>();
            // Show a single enchantment-style line so it looks like other enchanted books
            lore.add(ChatColor.LIGHT_PURPLE + "SilkPRO I");
            meta.setLore(lore);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);

            PersistentDataContainer pdc = meta.getPersistentDataContainer();
            // Mark the item as a SilkPRO Tome
            pdc.set(new NamespacedKey(pluginKey.getNamespace(), TOME_TAG), PersistentDataType.BOOLEAN, true);
            tome.setItemMeta(meta);
        }
        return tome;
    }

    public static boolean isTome(ItemStack stack, NamespacedKey pluginKey) {
        if (stack == null || stack.getType() == Material.AIR) return false;
        if (stack.getType() != Material.ENCHANTED_BOOK) return false;
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) return false;
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        Boolean flag = pdc.get(new NamespacedKey(pluginKey.getNamespace(), TOME_TAG), PersistentDataType.BOOLEAN);
        return flag != null && flag;
    }
}
