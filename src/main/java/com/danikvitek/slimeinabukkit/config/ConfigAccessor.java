package com.danikvitek.slimeinabukkit.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

public interface ConfigAccessor {
    @NotNull FileConfiguration getConfig();

    void saveConfig();
}
