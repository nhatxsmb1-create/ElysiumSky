package dev.elysium.sky.island;

import dev.elysium.sky.ElysiumSky;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class IslandManager {

    private final ElysiumSky plugin;
    private final Map<UUID, IslandData> cache = new HashMap<>();
    private File              dataFile;
    private YamlConfiguration dataConfig;

    public IslandManager(ElysiumSky plugin) {
        this.plugin = plugin;
        load();
    }

    private void load() {
        dataFile = new File(plugin.getDataFolder(), "islands.yml");
        if (!dataFile.exists()) {
            try { dataFile.createNewFile(); }
            catch (IOException e) { plugin.getLogger().severe("Khong the tao islands.yml!"); return; }
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);

        if (dataConfig.getConfigurationSection("islands") == null) return;

        for (String key : dataConfig.getConfigurationSection("islands").getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                String path = "islands." + key;

                IslandData data = new IslandData(uuid, dataConfig.getString(path + ".name","Unknown"));
                data.setPoints(dataConfig.getLong(path + ".points", 0));
                for (UpgradeType t : UpgradeType.values()) {
                    data.setUpgrade(t, dataConfig.getInt(path + ".upgrades." + t.name(), 0));
                }
                cache.put(uuid, data);
            } catch (Exception e) {
                plugin.getLogger().warning("Loi load island " + key + ": " + e.getMessage());
            }
        }
    }

    public void save(IslandData data) {
        String path = "islands." + data.getOwnerUuid();
        dataConfig.set(path + ".name",   data.getOwnerName());
        dataConfig.set(path + ".points", data.getPoints());
        for (UpgradeType t : UpgradeType.values()) {
            dataConfig.set(path + ".upgrades." + t.name(), data.getUpgrade(t));
        }
        try { dataConfig.save(dataFile); }
        catch (IOException e) { plugin.getLogger().severe("Loi save island!"); }
    }

    public void saveAll() {
        cache.values().forEach(this::save);
        plugin.getLogger().info("Saved " + cache.size() + " islands.");
    }

    public IslandData getIslandData(UUID uuid) { return cache.get(uuid); }

    public IslandData getOrCreate(UUID uuid, String name) {
        return cache.computeIfAbsent(uuid, k -> {
            IslandData d = new IslandData(k, name);
            save(d);
            return d;
        });
    }

    public int getCount() { return cache.size(); }
}
