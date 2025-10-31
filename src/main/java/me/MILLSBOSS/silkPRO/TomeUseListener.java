package me.MILLSBOSS.silkPRO;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class TomeUseListener implements Listener {

    private final NamespacedKey silkKey;

    public TomeUseListener(NamespacedKey silkKey) {
        this.silkKey = silkKey;
    }

    @EventHandler(ignoreCancelled = true)
    public void onUse(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();

        // Determine which hand holds the tome
        ItemStack handItem = player.getInventory().getItemInMainHand();
        EquipmentSlot tomeHand = EquipmentSlot.HAND;
        if (!SilkPROTome.isTome(handItem, silkKey)) {
            ItemStack off = player.getInventory().getItemInOffHand();
            if (SilkPROTome.isTome(off, silkKey)) {
                handItem = off;
                tomeHand = EquipmentSlot.OFF_HAND;
            } else {
                return; // Not using a tome
            }
        }

        // Require a supported pickaxe in main hand
        ItemStack pick = player.getInventory().getItemInMainHand();
        if (pick == null || pick.getType() == Material.AIR || !SilkPROCommand.isSupportedPickaxe(pick.getType())) {
            player.sendMessage(ChatColor.RED + "Hold a Netherite pickaxe in your main hand to apply SilkPRO.");
            return;
        }

        ItemMeta meta = pick.getItemMeta();
        if (meta == null) return;
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        if (Boolean.TRUE.equals(pdc.get(silkKey, PersistentDataType.BOOLEAN))) {
            player.sendMessage(ChatColor.YELLOW + "This pickaxe already has SilkPRO.");
            return;
        }

        // Apply tag and lore
        pdc.set(silkKey, PersistentDataType.BOOLEAN, true);
        List<String> lore = meta.getLore();
        if (lore == null) lore = new ArrayList<>();
        lore.add(ChatColor.AQUA + "SilkPRO I");
        meta.setLore(lore);
        pick.setItemMeta(meta);

        // Consume one tome from the hand used
        ItemStack tomeStack = (tomeHand == EquipmentSlot.HAND) ? player.getInventory().getItemInMainHand() : player.getInventory().getItemInOffHand();
        tomeStack.setAmount(tomeStack.getAmount() - 1);
        if (tomeHand == EquipmentSlot.HAND) {
            player.getInventory().setItemInMainHand(tomeStack.getAmount() > 0 ? tomeStack : new ItemStack(Material.AIR));
        } else {
            player.getInventory().setItemInOffHand(tomeStack.getAmount() > 0 ? tomeStack : new ItemStack(Material.AIR));
        }

        player.sendMessage(ChatColor.GREEN + "Your pickaxe has been imbued with " + ChatColor.AQUA + "SilkPRO" + ChatColor.GREEN + "!");
        player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0f, 1.2f);

        // Prevent further processing (like opening lecterns etc.) when right-clicking blocks
        event.setCancelled(true);
    }
}
