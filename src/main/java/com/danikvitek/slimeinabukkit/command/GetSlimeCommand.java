package com.danikvitek.slimeinabukkit.command;

import com.danikvitek.slimeinabukkit.config.PluginConfig;
import de.tr7zw.changeme.nbtapi.NBTItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static com.danikvitek.slimeinabukkit.SlimeInABukkitPlugin.SLIME_BUCKET_MATERIAL;
import static com.danikvitek.slimeinabukkit.SlimeInABukkitPlugin.SLIME_BUCKET_UUID_KEY;

public class GetSlimeCommand implements CommandExecutor {
    private final @NotNull PluginConfig config;

    public GetSlimeCommand(@NotNull PluginConfig config) {
        this.config = config;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull Command command,
                             @NotNull String label,
                             @NotNull String[] args) {
        getSlimeImpl(sender);
        return true;
    }

    private void getSlimeImpl(@NotNull CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Command can only be used by a player", NamedTextColor.RED));
            return;
        }

        final ItemStack slimeBucket = new ItemStack(SLIME_BUCKET_MATERIAL);
        final ItemMeta slimeBucketMeta = slimeBucket.getItemMeta();
        assert slimeBucketMeta != null;
        final Location location = player.getLocation();
        slimeBucketMeta.setCustomModelData(
            location.getChunk().isSlimeChunk()
                ? config.getActiveSlimeCmd()
                : config.getCalmSlimeCmd()
        );

        slimeBucketMeta.displayName(config.getSlimeBucketTitle());
        slimeBucket.setItemMeta(slimeBucketMeta);
        final NBTItem nbtItem = new NBTItem(slimeBucket);
        nbtItem.setUUID(SLIME_BUCKET_UUID_KEY, UUID.randomUUID()); // for it to be not stackable
        nbtItem.applyNBT(slimeBucket);

        final World world = player.getWorld();
        world.playSound(location, Sound.ENTITY_ITEM_PICKUP, 1f, 1f);
        if (!player.getInventory().addItem(slimeBucket).isEmpty())
            world.dropItem(player.getEyeLocation(), slimeBucket);
    }
}
