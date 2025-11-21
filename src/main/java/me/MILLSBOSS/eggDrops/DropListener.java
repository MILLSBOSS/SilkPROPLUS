package me.MILLSBOSS.eggDrops;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Locale;
import java.util.Random;

public class DropListener implements Listener {

    private final JavaPlugin plugin;
    private final Random random;

    public DropListener(JavaPlugin plugin, Random random) {
        this.plugin = plugin;
        this.random = random;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof LivingEntity living)) return;
        Player killer = living.getKiller();
        boolean spawnerSpawned = living.hasMetadata(SpawnerSpawnListener.META_SPAWNER_TAG);
        // Preserve original behavior (player kills) but also allow equal chance for spawner-spawned mobs
        // even if they die without a direct player killer (e.g., farm mechanics).
        if (killer == null && !spawnerSpawned) return;

        EntityType type = living.getType();
        Material eggMat = resolveEggMaterial(type);
        if (eggMat == null) return; // no spawn egg exists for this entity

        String category = PluginConfig.categoryFor(type);
        String key = category + "." + type.name();
        FileConfiguration cfg = plugin.getConfig();
        double percent = cfg.getDouble(key, 0.0);
        // Clamp: allow 0 to disable; otherwise range [0.01, 100.0]
        if (percent == 0.0) return;
        if (percent < 0.01) percent = 0.01;
        if (percent > 100.0) percent = 100.0;

        double roll = random.nextDouble() * 100.0; // 0.0 inclusive to 100.0 exclusive
        if (roll < percent) {
            ItemStack egg = new ItemStack(eggMat, 1);
            event.getDrops().add(egg);
        }
    }

    private Material resolveEggMaterial(EntityType type) {
        String name = type.name().toUpperCase(Locale.ROOT) + "_SPAWN_EGG";
        try {
            return Material.valueOf(name);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
