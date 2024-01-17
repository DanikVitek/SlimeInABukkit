package com.danikvitek.slimeinabukkit.config;

import org.bukkit.configuration.file.FileConfiguration;

public interface ConfigAccessor {
    FileConfiguration getConfig();
    void saveConfig();
}
