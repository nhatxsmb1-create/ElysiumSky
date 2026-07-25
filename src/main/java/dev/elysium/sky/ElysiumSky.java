package dev.elysium.sky;

import dev.elysium.sky.api.SkyAPI;
import dev.elysium.sky.config.SkyConfig;
import dev.elysium.sky.island.IslandManager;
import dev.elysium.sky.listener.SkyListener;
import dev.elysium.sky.quest.QuestManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ElysiumSky extends JavaPlugin {

    private static ElysiumSky instance;

    private SkyConfig    skyConfig;
    private QuestManager questManager;
    private IslandManager islandManager;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        saveResource("quests.yml", false);

        skyConfig     = new SkyConfig(this);
        questManager  = new QuestManager(this);
        islandManager = new IslandManager(this);

        SkyAPI.init(this);

        getCommand("sky").setExecutor(new SkyCommand(this));
        getServer().getPluginManager().registerEvents(new SkyListener(this), this);

        getLogger().info("=== ElysiumSky v" + getDescription().getVersion() + " enabled! ===");
        getLogger().info("Quests loaded: " + questManager.getQuestCount());
        getLogger().info("Islands loaded: " + islandManager.getCount());
    }

    @Override
    public void onDisable() {
        if (islandManager != null) islandManager.saveAll();
        getLogger().info("ElysiumSky disabled.");
    }

    public static ElysiumSky getInstance()    { return instance; }
    public SkyConfig     getSkyConfig()       { return skyConfig; }
    public QuestManager  getQuestManager()    { return questManager; }
    public IslandManager getIslandManager()   { return islandManager; }
  }
