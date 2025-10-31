package me.MILLSBOSS.silkPRO;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class SpawnerSilkListener implements Listener {

    private final NamespacedKey key;

    public SpawnerSilkListener(NamespacedKey key) {
        this.key = key;
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getType() != Material.SPAWNER) return;

        Player player = event.getPlayer();
        ItemStack tool = player.getInventory().getItem(EquipmentSlot.HAND);
        if (tool == null || tool.getType() == Material.AIR) return;
        if (!SilkPROCommand.isSupportedPickaxe(tool.getType())) return;

        ItemMeta toolMeta = tool.getItemMeta();
        if (toolMeta == null) return;

        PersistentDataContainer pdc = toolMeta.getPersistentDataContainer();
        Boolean has = pdc.get(key, PersistentDataType.BOOLEAN);
        if (has == null || !has) return; // Not a SilkPRO pickaxe

        // Now perform custom drop
        BlockState state = block.getState();
        if (!(state instanceof CreatureSpawner spawner)) return;

        // Prepare spawner item preserving full state using BlockStateMeta
        ItemStack drop = new ItemStack(Material.SPAWNER, 1);
        ItemMeta meta = drop.getItemMeta();
        if (meta instanceof BlockStateMeta bsm && player.isSneaking()) {
            BlockState bs = bsm.getBlockState();
            if (bs instanceof CreatureSpawner is) {
                is.setSpawnedType(spawner.getSpawnedType());
                // Copy key parameters for fidelity
                // Only preserve the spawned entity type to keep NBT minimal and allow stacking
                // of spawners that have the same preserved entity. Dynamic timing/count parameters
                // vary per spawner and would prevent stacking if stored.
                bsm.setBlockState(is);
                drop.setItemMeta(bsm);
            }
        }

        // Cancel normal event handling and drop our item
        event.setDropItems(false);
        event.setExpToDrop(0);
        block.setType(Material.AIR);
        block.getWorld().dropItemNaturally(block.getLocation(), drop);

        if (player.isSneaking()) {
            player.sendMessage(ChatColor.GRAY + "You mined a spawner with " + ChatColor.AQUA + "SilkPRO" + ChatColor.GRAY + " (preserved type).");
        } else {
            player.sendMessage(ChatColor.GRAY + "You mined a spawner with " + ChatColor.AQUA + "SilkPRO" + ChatColor.GRAY + " (plain spawner). Crouch while mining to preserve the entity type.");
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        // Only care about placing spawners
        if (event.getBlockPlaced().getType() != Material.SPAWNER) return;

        ItemStack inHand = event.getItemInHand();
        if (inHand == null) return;
        ItemMeta meta = inHand.getItemMeta();
        if (!(meta instanceof BlockStateMeta bsm)) return;

        BlockState stored = bsm.getBlockState();
        if (!(stored instanceof CreatureSpawner storedSpawner)) return; // plain spawner

        // Apply to placed block
        Block placedBlock = event.getBlockPlaced();
        BlockState placedState = placedBlock.getState();
        if (!(placedState instanceof CreatureSpawner placedSpawner)) return;

        try {
            // Only apply the preserved entity type. Other parameters are left to server defaults
            // to avoid inconsistent NBT and to keep items stackable.
            placedSpawner.setSpawnedType(storedSpawner.getSpawnedType());
        } catch (Throwable ignored) {
            // If API throws for some reason, ignore and let defaults stand
        }

        placedSpawner.update(true, false);
    }
}
