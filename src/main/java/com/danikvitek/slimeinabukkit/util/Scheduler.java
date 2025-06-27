package com.danikvitek.slimeinabukkit.util;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class Scheduler {
    private final @NotNull Plugin plugin;
    private final @NotNull BukkitScheduler bukkitScheduler;

    public Scheduler(@NotNull Plugin plugin, @NotNull BukkitScheduler bukkitScheduler) {
        this.plugin = plugin;
        this.bukkitScheduler = bukkitScheduler;
    }

    public BukkitTask runTask(@NotNull Runnable runnable) {
        return bukkitScheduler.runTask(plugin, runnable);
    }

    public BukkitTask runTaskLater(@NotNull Runnable runnable, long delay) {
        return bukkitScheduler.runTaskLater(plugin, runnable, delay);
    }

    public void runTaskTimerAsynchronously(@NotNull Consumer<BukkitTask> task, long delay, long period) {
        bukkitScheduler.runTaskTimerAsynchronously(plugin, task, delay, period);
    }

    public void cancelAll() {
        bukkitScheduler.cancelTasks(plugin);
    }
}
