package com.danikvitek.slimeinabukkit.command;

import com.danikvitek.slimeinabukkit.config.PluginConfig;
import com.danikvitek.slimeinabukkit.persistence.PersistentContainerAccessor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

import static com.danikvitek.slimeinabukkit.SlimeInABukkitPlugin.SLIME_BUCKET_MATERIAL;

public class GetSlimeCommand extends BukkitCommand {
    private final @NotNull PluginConfig config;
    private final @NotNull PersistentContainerAccessor persistentContainerAccessor;

    public GetSlimeCommand(
        @NotNull PluginConfig config,
        @NotNull PersistentContainerAccessor persistentContainerAccessor
    ) {
        super(
            "get_slime",
            "Gives a bucket of slime to the player",
            "/get_slime",
            List.of()
        );
        this.config = config;
        this.persistentContainerAccessor = persistentContainerAccessor;
    }

    @Override
    public boolean execute(
        @NotNull CommandSender sender,
        @NotNull String commandLabel,
        @NotNull String[] args
    ) {
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
        slimeBucketMeta.setCustomModelData(location.getChunk()
            .isSlimeChunk() ? config.getActiveSlimeCmd() : config.getCalmSlimeCmd());

        slimeBucketMeta.displayName(config.getSlimeBucketTitle());
        persistentContainerAccessor.setSlimeBucketUUID(
            slimeBucketMeta,
            UUID.randomUUID()
        ); // for it to be not stackable
        slimeBucket.setItemMeta(slimeBucketMeta);

        final World world = player.getWorld();
        world.playSound(location, Sound.ENTITY_ITEM_PICKUP, 1f, 1f);
        if (!player.getInventory().addItem(slimeBucket).isEmpty()) {
            world.dropItem(player.getEyeLocation(), slimeBucket);
        }
    }
}
