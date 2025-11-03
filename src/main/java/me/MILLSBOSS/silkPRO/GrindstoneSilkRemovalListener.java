package me.MILLSBOSS.silkPRO;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareGrindstoneEvent;
import org.bukkit.inventory.GrindstoneInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

/**
 * Allows removing the custom SilkPRO tag using a grindstone like normal enchantments.
 *
 * Behavior implemented minimally to match vanilla UX:
 * - If exactly one item is placed in the grindstone and it has SilkPRO, the result will be a copy
 *   of that item with the SilkPRO tag and lore removed.
 */
public class GrindstoneSilkRemovalListener implements Listener {

    private final NamespacedKey silkKey;

    public GrindstoneSilkRemovalListener(NamespacedKey silkKey) {
        this.silkKey = silkKey;
    }

    @EventHandler
    public void onPrepareGrindstone(PrepareGrindstoneEvent event) {
        GrindstoneInventory inv = event.getInventory();
        ItemStack upper = inv.getUpperItem();
        ItemStack lower = inv.getLowerItem();

        // Exactly one item present behaves like vanilla enchant removal
        boolean hasUpper = upper != null && upper.getType() != Material.AIR;
        boolean hasLower = lower != null && lower.getType() != Material.AIR;
        if (hasUpper == hasLower) {
            // Either both empty or both filled (combine/repair case) —
            // we do not alter vanilla behavior here to keep logic minimal.
            return;
        }

        ItemStack src = hasUpper ? upper : lower;
        if (!hasSilkPRO(src)) return;

        ItemStack result = src.clone();
        removeSilkPRO(result);
        event.setResult(result);
    }

    private boolean hasSilkPRO(ItemStack item) {
        if (item == null) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        Boolean tagged = pdc.get(silkKey, PersistentDataType.BOOLEAN);
        if (Boolean.TRUE.equals(tagged)) return true;
        List<String> lore = meta.getLore();
        String line = ChatColor.AQUA + "SilkPRO I";
        return lore != null && lore.contains(line);
    }

    private void removeSilkPRO(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.remove(silkKey);
        List<String> lore = meta.getLore();
        if (lore != null && !lore.isEmpty()) {
            String line = ChatColor.AQUA + "SilkPRO I";
            List<String> updated = new ArrayList<>(lore);
            updated.removeIf(l -> l != null && l.equals(line));
            // Only set lore if something changed; otherwise leave as-is
            if (updated.size() != lore.size()) {
                meta.setLore(updated.isEmpty() ? null : updated);
            }
        }
        item.setItemMeta(meta);
    }
}
