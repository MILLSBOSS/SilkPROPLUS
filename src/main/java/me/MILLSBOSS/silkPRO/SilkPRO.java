package me.MILLSBOSS.silkPRO;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public final class SilkPRO extends JavaPlugin {

    private NamespacedKey silkKey;

    @Override
    public void onEnable() {
        silkKey = new NamespacedKey(this, SilkPROCommand.SILKPRO_KEY);
        // Register listeners (anvil-only application)
        getServer().getPluginManager().registerEvents(new SpawnerSilkListener(silkKey), this);
        getServer().getPluginManager().registerEvents(new LibrarianTradeListener(silkKey), this);
        getServer().getPluginManager().registerEvents(new AnvilSilkListener(silkKey), this);
    }

    @Override
    public void onDisable() {
        // Nothing to cleanup
    }
}
