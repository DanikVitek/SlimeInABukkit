package com.danikvitek.slimeinabukkit;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class PluginConfig {
    public static final String CHUNK_STATUS_PLACEHOLDER = "<chunk-status>";
    private static final int DEFAULT_CALM_SLIME_CMD = 404;
    private static final int DEFAULT_ACTIVE_SLIME_CMD = 200;
    private static final String DEFAULT_SLIME_CHUNK_MESSAGE = "<gray>This chunk </gray>" + CHUNK_STATUS_PLACEHOLDER + "<gray> a Slime chunk";
    private static final String DEFAULT_BUCKET_TITLE = "<!italic><lang:item.slimeinabukkit.slime_bucket>";
    private static final String DEFAULT_CHUNK_STATUS_TRUE = "<green>is";
    private static final String DEFAULT_CHUNK_STATUS_FALSE = "<red>is not";
    private static final boolean DEFAULT_CAN_PICKUP_SLIME = true;
    private static final boolean DEFAULT_DEBUG = false;
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private final @NotNull Plugin configAccessor;
    private int calmSlimeCmd;
    private int activeSlimeCmd;
    private Component slimeBucketTitle;
    private String slimeChunkMessage;
    private Component chunkStatusTrue;
    private Component chunkStatusFalse;
    private boolean canPickupSlime;
    private boolean debug;

    @Contract(pure = false)
    public PluginConfig(@NotNull Plugin configAccessor) {
        this.configAccessor = configAccessor;
        read();
        update();
    }

    public void read() {
        final var config = configAccessor.getConfig();

        calmSlimeCmd = config.getInt("custom-model-data.calm-slime", DEFAULT_CALM_SLIME_CMD);
        activeSlimeCmd = config.getInt("custom-model-data.active-slime", DEFAULT_ACTIVE_SLIME_CMD);
        slimeBucketTitle = MINI_MESSAGE.deserialize(config.getString("bucket-title", DEFAULT_BUCKET_TITLE));
        slimeChunkMessage = config.getString("slime-chunk-message", DEFAULT_SLIME_CHUNK_MESSAGE);
        chunkStatusTrue = MINI_MESSAGE.deserialize(config.getString("chunk-status.true", DEFAULT_CHUNK_STATUS_TRUE));
        chunkStatusFalse = MINI_MESSAGE.deserialize(config.getString("chunk-status.false", DEFAULT_CHUNK_STATUS_FALSE));
        canPickupSlime = config.getBoolean("can-pickup-slime", DEFAULT_CAN_PICKUP_SLIME);
        debug = config.getBoolean("debug", DEFAULT_DEBUG);
    }

    public void update() {
        final var config = configAccessor.getConfig();
        config.set("custom-model-data.calm-slime", calmSlimeCmd);
        config.set("custom-model-data.active-slime", activeSlimeCmd);
        config.set("bucket-title", MINI_MESSAGE.serialize(slimeBucketTitle));
        config.set("slime-chunk-message", slimeChunkMessage);
        config.set("chunk-status.true", MINI_MESSAGE.serialize(chunkStatusTrue));
        config.set("chunk-status.false", MINI_MESSAGE.serialize(chunkStatusFalse));
        config.set("can-pickup-slime", canPickupSlime);
        config.set("debug", debug);
        configAccessor.saveConfig();
    }

    public int getCalmSlimeCmd() {
        return calmSlimeCmd;
    }

    public int getActiveSlimeCmd() {
        return activeSlimeCmd;
    }

    public Component getSlimeBucketTitle() {
        return slimeBucketTitle;
    }

    public boolean canPickupSlime() {
        return canPickupSlime;
    }

    public @NotNull String getSlimeChunkMessage() {
        return slimeChunkMessage;
    }

    public @NotNull Component getChunkStatusTrue() {
        return chunkStatusTrue;
    }

    public @NotNull Component getChunkStatusFalse() {
        return chunkStatusFalse;
    }

    public boolean isDebug() {
        return debug;
    }
}
