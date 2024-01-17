package com.danikvitek.slimeinabukkit;

import com.danikvitek.slimeinabukkit.command.GetSlimeCommand;
import com.danikvitek.slimeinabukkit.command.SlimeChunkCommand;
import com.danikvitek.slimeinabukkit.config.ChunkMessageResolver;
import com.danikvitek.slimeinabukkit.config.ConfigAccessor;
import com.danikvitek.slimeinabukkit.config.PluginConfig;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class SlimeInABukkitPlugin extends JavaPlugin implements ConfigAccessor {
    public static final Material SLIME_BUCKET_MATERIAL = Material.SLIME_BALL;
    public static final String SLIME_BUCKET_UUID_KEY = "SLIME_UUID";
    private static final int PLUGIN_ID = 14716;

    private final Scheduler scheduler = new Scheduler(this, getServer().getScheduler());
    private final PluginConfig pluginConfig = new PluginConfig(this);

    @Override
    public void onEnable() {
        this.getConfig().options().configuration();
        this.saveDefaultConfig();

        Objects.requireNonNull(getCommand("get_slime")).setExecutor(new GetSlimeCommand(pluginConfig));

        final var messageResolver = new ChunkMessageResolver(
            pluginConfig.getSlimeChunkMessage(),
            pluginConfig.getChunkStatusTrue(),
            pluginConfig.getChunkStatusFalse()
        );
        Objects.requireNonNull(getCommand("slime_chunk")).setExecutor(new SlimeChunkCommand(messageResolver));

        new Metrics(this, PLUGIN_ID);

        Bukkit.getPluginManager().registerEvents(new SlimeListener(pluginConfig, this::debugLog, scheduler), this);
    }

    @Override
    public void onDisable() {
        scheduler.cancelAll();
    }

    public void debugLog(final @NotNull String message) {
        if (pluginConfig.isDebug()) this.getLogger().info(message);
    }
}
