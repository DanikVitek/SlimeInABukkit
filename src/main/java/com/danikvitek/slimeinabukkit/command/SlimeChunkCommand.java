package com.danikvitek.slimeinabukkit.command;

import com.danikvitek.slimeinabukkit.config.ChunkMessageResolver;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SlimeChunkCommand extends BukkitCommand {
    private final @NotNull ChunkMessageResolver messageResolver;

    public SlimeChunkCommand(@NotNull ChunkMessageResolver messageResolver) {
        super(
            "slime_chunk",
            "Tells you, if the chunk you are in is a slime chunk",
            "/slime_chunk",
            List.of()
        );
        this.messageResolver = messageResolver;
    }

    @Override
    public boolean execute(
        @NotNull CommandSender sender,
        @NotNull String commandLabel,
        @NotNull String[] args
    ) {
        slimeChunkImpl(sender);
        return true;
    }

    private void slimeChunkImpl(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text(
                "Command can only be used by a player",
                NamedTextColor.RED
            ));
            return;
        }

        final boolean isSlimeChunk = player.getLocation().getChunk().isSlimeChunk();
        player.sendMessage(messageResolver.resolve(isSlimeChunk));
    }
}
