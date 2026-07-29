package dev.elysium.sky.quest;

import dev.elysium.core.achievement.AchievementType;
import dev.elysium.core.api.CoreAPI;
import dev.elysium.core.util.ColorUtil;
import dev.elysium.sky.ElysiumSky;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;

public class QuestManager {

    private final ElysiumSky plugin;
    private final Map<String, Quest>         quests     = new LinkedHashMap<>();
    private final Map<UUID, PlayerQuestData> playerData = new HashMap<>();

    public QuestManager(ElysiumSky plugin) {
        this.plugin = plugin;
        loadQuests();
    }

    private void loadQuests() {
        File file = new File(plugin.getDataFolder(), "quests.yml");
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection root = cfg.getConfigurationSection("quests");
        if (root == null) { plugin.getLogger().warning("quests.yml trong!"); return; }

        for (String id : root.getKeys(false)) {
            ConfigurationSection q = root.getConfigurationSection(id);
            if (q == null) continue;

            List<QuestObjective> objectives = new ArrayList<>();
            ConfigurationSection objSec = q.getConfigurationSection("objectives");
            if (objSec != null) {
                for (String key : objSec.getKeys(false)) {
                    ConfigurationSection o = objSec.getConfigurationSection(key);
                    if (o == null) continue;
                    try {
                        objectives.add(new QuestObjective(
                            QuestObjective.Type.valueOf(o.getString("type","BREAK_BLOCK").toUpperCase()),
                            o.getString("target","STONE"), o.getInt("amount",1)));
                    } catch (Exception e) {
                        plugin.getLogger().warning("Loi objective " + key + " quest " + id);
                    }
                }
            }

            List<QuestReward> rewards = new ArrayList<>();
            ConfigurationSection rewSec = q.getConfigurationSection("rewards");
            if (rewSec != null) {
                for (String key : rewSec.getKeys(false)) {
                    ConfigurationSection r = rewSec.getConfigurationSection(key);
                    if (r == null) continue;
                    try {
                        rewards.add(new QuestReward(
                            QuestReward.Type.valueOf(r.getString("type","MONEY").toUpperCase()),
                            r.getString("value",""), r.getDouble("amount",0)));
                    } catch (Exception e) {
                        plugin.getLogger().warning("Loi reward " + key + " quest " + id);
                    }
                }
            }

            quests.put(id, new Quest(id,
                q.getString("name",id), q.getString("description",""),
                objectives, rewards,
                q.getInt("required-level",0), q.getString("required-quest","")));
        }
        plugin.getLogger().info("Loaded " + quests.size() + " quests.");
    }

    // ── Progress ──────────────────────────────────────────────────────────────

    public void handleProgress(Player player, QuestObjective.Type type, String target, int amount) {
        PlayerQuestData data = getOrCreatePlayerData(player.getUniqueId());

        for (Quest quest : quests.values()) {
            if (data.isCompleted(quest.getId())) continue;
            if (!canAccess(player, quest)) continue;

            List<QuestObjective> objs = quest.getObjectives();
            for (int i = 0; i < objs.size(); i++) {
                QuestObjective obj = objs.get(i);
                if (obj.getType() != type) continue;

                // REACH_ISLAND_LEVEL: chi can dat moc, khong can khop exact
                if (type == QuestObjective.Type.REACH_ISLAND_LEVEL) {
                    try {
                        int currentLevel = Integer.parseInt(target);
                        if (currentLevel >= obj.getRequired()) {
                            // Dat du level, danh dau hoan thanh objective nay
                            data.addProgress(quest.getId(), i,
                                obj.getRequired() - data.getProgress(quest.getId(), i));
                        }
                    } catch (NumberFormatException ignored) {}
                } else {
                    if (!obj.getTarget().equalsIgnoreCase(target)) continue;
                    data.addProgress(quest.getId(), i, amount);
                }

                if (isQuestComplete(quest, data)) {
                    completeQuest(player, quest, data);
                    break;
                }
            }
        }
    }

    private boolean isQuestComplete(Quest quest, PlayerQuestData data) {
        List<QuestObjective> objs = quest.getObjectives();
        for (int i = 0; i < objs.size(); i++) {
            if (data.getProgress(quest.getId(), i) < objs.get(i).getRequired()) return false;
        }
        return true;
    }

    private void completeQuest(Player player, Quest quest, PlayerQuestData data) {
        data.complete(quest.getId());

        player.sendMessage(ColorUtil.color("&a&l[QUEST HOAN THANH] &f" + quest.getName() + "!"));
        player.sendMessage(ColorUtil.color("&7Phan thuong:"));

        for (QuestReward reward : quest.getRewards()) {
            switch (reward.getType()) {
                case MONEY -> {
                    CoreAPI.addBalance(player, reward.getAmount());
                    player.sendMessage(ColorUtil.color("  &7+ &6" + (long)reward.getAmount() + " coins"));
                }
                case EXP -> {
                    CoreAPI.addExp(player, (long)reward.getAmount()); // fires LevelUpEvent
                    player.sendMessage(ColorUtil.color("  &7+ &a" + (long)reward.getAmount() + " EXP"));
                }
                case ISLAND_POINTS -> {
                    var island = plugin.getIslandManager()
                        .getOrCreate(player.getUniqueId(), player.getName());
                    island.addPoints((long)reward.getAmount());
                    player.sendMessage(ColorUtil.color("  &7+ &b" + (long)reward.getAmount() + " Island Points"));
                }
            }
        }

        // Check achievements sau khi hoan thanh quest
        CoreAPI.checkAchievements(player);

        // Neu hoan thanh quest dau tien -> award ISLAND_CREATED
        long doneCount = quests.values().stream()
            .filter(q -> data.isCompleted(q.getId())).count();
        if (doneCount == 1) {
            CoreAPI.awardAchievement(player, AchievementType.ISLAND_CREATED);
        }
    }

    // ── Utils ─────────────────────────────────────────────────────────────────

    public boolean canAccess(Player player, Quest quest) {
        if (CoreAPI.getLevel(player) < quest.getRequiredLevel()) return false;
        if (!quest.getRequiredQuestId().isEmpty()) {
            PlayerQuestData data = getOrCreatePlayerData(player.getUniqueId());
            if (!data.isCompleted(quest.getRequiredQuestId())) return false;
        }
        return true;
    }

    public PlayerQuestData getOrCreatePlayerData(UUID uuid) {
        return playerData.computeIfAbsent(uuid, PlayerQuestData::new);
    }

    public Quest             getQuest(String id)    { return quests.get(id); }
    public Collection<Quest> getAllQuests()          { return quests.values(); }
    public PlayerQuestData   getPlayerData(UUID uuid){ return playerData.get(uuid); }
    public int               getQuestCount()        { return quests.size(); }
}
