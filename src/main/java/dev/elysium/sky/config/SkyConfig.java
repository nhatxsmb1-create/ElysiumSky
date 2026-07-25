package dev.elysium.sky.config;

import dev.elysium.sky.ElysiumSky;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

public class SkyConfig {

    private final ElysiumSky plugin;
    private FileConfiguration cfg;
    private final Map<String, Integer> blockPoints = new HashMap<>();

    public SkyConfig(ElysiumSky plugin) {
        this.plugin = plugin;
        this.cfg    = plugin.getConfig();
        loadBlockPoints();
    }

    public void reload() {
        plugin.reloadConfig();
        this.cfg = plugin.getConfig();
        blockPoints.clear();
        loadBlockPoints();
    }

    private void loadBlockPoints() {
        var section = cfg.getConfigurationSection("island.block-points");
        if (section == null) return;
        for (String key : section.getKeys(false)) {
            blockPoints.put(key.toUpperCase(), section.getInt(key, 0));
        }
        plugin.getLogger().info("Loaded " + blockPoints.size() + " block point entries.");
    }

    public int    getBlockPoints(String mat) { return blockPoints.getOrDefault(mat.toUpperCase(), 0); }
    public String getPrefix()               { return cfg.getString("prefix", "&b[Sky] &r"); }
}
