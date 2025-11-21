package me.MILLSBOSS.eggDrops;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Tags mobs that originate from spawners so they can be handled with the same
 * spawn egg drop chance as naturally spawned ones, even if killed indirectly.
 */
public class SpawnerSpawnListener implements Listener {

    public static final String META_SPAWNER_TAG = "silkproplus-spawner";

    private final JavaPlugin plugin;

    public SpawnerSpawnListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER) {
            event.getEntity().setMetadata(META_SPAWNER_TAG, new FixedMetadataValue(plugin, true));
        }
    }
}
