package com.danikvitek.slimeinabukkit.util;

import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * Utility class for {@link org.bukkit.inventory.ItemStack}.
 */
public final class ISUtil {
    private ISUtil() {
    }

    public static void useDisplayName(
        final @NotNull ItemStack itemStack,
        final @NotNull Consumer<@NotNull Component> consumer
    ) {
        if (!itemStack.hasItemMeta()) {
            final ItemMeta itemMeta = itemStack.getItemMeta();
            useDisplayName(itemMeta, consumer);
        }
    }

    public static void useDisplayName(
        final @NotNull ItemMeta itemMeta,
        final @NotNull Consumer<@NotNull Component> consumer
    ) {
        if (itemMeta.hasDisplayName()) {
            final Component displayName = itemMeta.displayName();
            assert displayName != null;
            consumer.accept(displayName);
        }
    }
}
