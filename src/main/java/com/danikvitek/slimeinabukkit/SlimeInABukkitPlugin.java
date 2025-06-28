package com.danikvitek.slimeinabukkit;

import com.danikvitek.slimeinabukkit.command.GetSlimeCommand;
import com.danikvitek.slimeinabukkit.command.SlimeChunkCommand;
import com.danikvitek.slimeinabukkit.config.ChunkMessageResolver;
import com.danikvitek.slimeinabukkit.config.ConfigAccessor;
import com.danikvitek.slimeinabukkit.config.PluginConfig;
import com.danikvitek.slimeinabukkit.listener.SlimeListener;
import com.danikvitek.slimeinabukkit.persistence.PersistentContainerAccessor;
import com.danikvitek.slimeinabukkit.util.Scheduler;
import org.bstats.bukkit.Metrics;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

public final class SlimeInABukkitPlugin extends JavaPlugin implements ConfigAccessor {
    public static final Material SLIME_BUCKET_MATERIAL = Material.SLIME_BALL;
    private static final int PLUGIN_ID = 14716;

    private final Scheduler scheduler = new Scheduler(this, getServer().getScheduler());
    private final PluginConfig pluginConfig = new PluginConfig(this);

    @Override
    public void onEnable() {
        this.getConfig().options().configuration();
        this.saveDefaultConfig();

        final var persistentContainerAccessor = new PersistentContainerAccessor(new NamespacedKey(this, "slime-uuid"));
        final var messageResolver = new ChunkMessageResolver(
            pluginConfig.getSlimeChunkMessage(),
            pluginConfig.getChunkStatusTrue(),
            pluginConfig.getChunkStatusFalse()
        );

        getServer().getCommandMap()
            .registerAll(
                getName().toLowerCase(Locale.ROOT),
                List.of(
                    new GetSlimeCommand(pluginConfig, persistentContainerAccessor),
                    new SlimeChunkCommand(messageResolver)
                )
            );

        new Metrics(this, PLUGIN_ID);

        getServer().getPluginManager()
            .registerEvents(
                new SlimeListener(pluginConfig, this::debugLog, scheduler, persistentContainerAccessor),
                this
            );
    }

    @Override
    public void onDisable() {
        scheduler.cancelAll();
    }

    public void debugLog(final @NotNull String message) {
        if (pluginConfig.isDebug()) this.getLogger().info(message);
    }
}
