package com.danikvitek.slimeinabukkit;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

import static com.danikvitek.slimeinabukkit.PluginConfig.CHUNK_STATUS_PLACEHOLDER;

public class ChunkMessageResolver {
    private final @NotNull Function<ComponentLike, Component> messageFactory;
    private final @NotNull ComponentLike chunkStatusTrue;
    private final @NotNull ComponentLike chunkStatusFalse;

    public ChunkMessageResolver(final @NotNull String message,
                                final @NotNull Component chunkStatusTrue,
                                final @NotNull Component chunkStatusFalse) {
        messageFactory = c -> MiniMessage.miniMessage().deserialize(
            message,
            Placeholder.component(CHUNK_STATUS_PLACEHOLDER, c)
        );
        this.chunkStatusTrue = chunkStatusTrue;
        this.chunkStatusFalse = chunkStatusFalse;
    }

    public @NotNull Component resolve(final boolean chunkStatus) {
        return messageFactory.apply(chunkStatus ? chunkStatusTrue : chunkStatusFalse);
    }
}