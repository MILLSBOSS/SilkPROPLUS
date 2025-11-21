package me.MILLSBOSS.silkPROPLUS;

import me.MILLSBOSS.eggDrops.DropListener;
import me.MILLSBOSS.eggDrops.PluginConfig;
import me.MILLSBOSS.eggDrops.SpawnerSpawnListener;
import me.MILLSBOSS.silkPRO.AnvilSilkListener;
import me.MILLSBOSS.silkPRO.LibrarianTradeListener;
import me.MILLSBOSS.silkPRO.SilkPROCommand;
import me.MILLSBOSS.silkPRO.SpawnerSilkListener;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public final class SilkPROPLUS extends JavaPlugin {

    private NamespacedKey silkKey;
    private final Random random = new Random();

    @Override
    public void onEnable() {
        // Key for SilkPRO item tagging
        silkKey = new NamespacedKey(this, SilkPROCommand.SILKPRO_KEY);

        // Register SilkPRO listeners (existing functionality)
        getServer().getPluginManager().registerEvents(new SpawnerSilkListener(silkKey), this);
        getServer().getPluginManager().registerEvents(new LibrarianTradeListener(silkKey), this);
        getServer().getPluginManager().registerEvents(new AnvilSilkListener(silkKey), this);
        // Allow removing SilkPRO via grindstone like normal enchantments
        getServer().getPluginManager().registerEvents(new me.MILLSBOSS.silkPRO.GrindstoneSilkRemovalListener(silkKey), this);

        // Initialize and register EggDrops functionality
        saveDefaultConfig();
        PluginConfig.initDefaults(this);
        getServer().getPluginManager().registerEvents(new DropListener(this, random), this);
        // Tag mobs that originate from spawners so egg drop chance applies equally
        getServer().getPluginManager().registerEvents(new SpawnerSpawnListener(this), this);
    }

    @Override
    public void onDisable() {
        // Nothing to cleanup
    }
}
