package me.MILLSBOSS.silkPRO;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

/**
 * Allows applying SilkPRO to supported pickaxes using an anvil by combining
 * a SilkPRO Tome (from librarians) with an Iron/Diamond/Netherite pickaxe.
 *
 * Cost: 8 levels (displayed in the anvil UI).
 */
public class AnvilSilkListener implements Listener {

    private final NamespacedKey silkKey;

    public AnvilSilkListener(NamespacedKey silkKey) {
        this.silkKey = silkKey;
    }

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        AnvilInventory inv = event.getInventory();
        ItemStack left = inv.getFirstItem();
        ItemStack right = inv.getSecondItem();

        // Must have exactly a supported pickaxe + a valid SilkPRO Tome
        if (left == null || left.getType() == Material.AIR) return;
        if (right == null || right.getType() == Material.AIR) return;
        if (!SilkPROCommand.isSupportedPickaxe(left.getType())) return;
        if (!SilkPROTome.isTome(right, silkKey)) return;

        // Left item must be modifiable and not already have SilkPRO
        ItemMeta meta = left.getItemMeta();
        if (meta == null) return;
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        if (Boolean.TRUE.equals(pdc.get(silkKey, PersistentDataType.BOOLEAN))) {
            // Already has SilkPRO -> no special result
            return;
        }

        // Build the resulting item: clone left, add tag + lore, keep durability, enchants, etc.
        ItemStack result = left.clone();
        ItemMeta rMeta = result.getItemMeta();
        if (rMeta == null) return;

        PersistentDataContainer rPdc = rMeta.getPersistentDataContainer();
        rPdc.set(silkKey, PersistentDataType.BOOLEAN, true);

        List<String> lore = rMeta.getLore();
        if (lore == null) lore = new ArrayList<>();
        // Avoid duplicate lore if some other path already added it
        String line = ChatColor.AQUA + "SilkPRO I";
        if (!lore.contains(line)) lore.add(line);
        rMeta.setLore(lore);

        // Preserve rename text entered in the anvil UI
        String rename = inv.getRenameText();
        if (rename != null && !rename.isBlank()) {
            rMeta.setDisplayName(rename);
        }

        result.setItemMeta(rMeta);

        event.setResult(result);
        // Show the level cost in the anvil
        try {
            inv.setRepairCost(8);
        } catch (Throwable ignored) {
            // Fallback for API variance; not critical for functionality
        }
    }
}
