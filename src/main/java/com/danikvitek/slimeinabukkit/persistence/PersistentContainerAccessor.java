package com.danikvitek.slimeinabukkit.persistence;

import com.danikvitek.slimeinabukkit.persistence.datatype.UUIDTagType;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PersistentContainerAccessor {
    private final @NotNull NamespacedKey slimeBucketUuidKey;

    public PersistentContainerAccessor(@NotNull NamespacedKey slimeBucketUuidKey) {
        this.slimeBucketUuidKey = slimeBucketUuidKey;
    }

    public void setSlimeBucketUUID(final @NotNull ItemStack itemStack, final @NotNull UUID uuid) {
        final ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.getPersistentDataContainer().set(slimeBucketUuidKey, UUIDTagType.INSTANCE, uuid);
        itemStack.setItemMeta(itemMeta);
    }

    public void removeSlimeBucketUUID(final @NotNull ItemStack itemStack) {
        final ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.getPersistentDataContainer().remove(slimeBucketUuidKey);
        itemStack.setItemMeta(itemMeta);
    }
}
