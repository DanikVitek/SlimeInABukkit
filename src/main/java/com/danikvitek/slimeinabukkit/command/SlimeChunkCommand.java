package com.danikvitek.slimeinabukkit.command;

import com.danikvitek.slimeinabukkit.config.ChunkMessageResolver;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SlimeChunkCommand implements CommandExecutor {
    private final @NotNull ChunkMessageResolver messageResolver;

    public SlimeChunkCommand(@NotNull ChunkMessageResolver messageResolver) {
        this.messageResolver = messageResolver;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender,
                             @NotNull Command command,
                             @NotNull String s,
                             @NotNull String[] strings) {
        slimeChunkImpl(commandSender);
        return true;
    }

    private void slimeChunkImpl(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Command can only be used by a player");
            return;
        }

        final boolean isSlimeChunk = player.getLocation().getChunk().isSlimeChunk();
        player.sendMessage(messageResolver.resolve(isSlimeChunk));
    }
}
