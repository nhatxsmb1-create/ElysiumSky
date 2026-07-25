package dev.elysium.sky.quest;

import dev.elysium.core.util.ColorUtil;
import org.bukkit.entity.Player;

import java.util.List;

public class Quest {

    private final String id;
    private final String name;
    private final String description;
    private final List<QuestObjective> objectives;
    private final List<QuestReward>    rewards;
    private final int    requiredLevel;
    private final String requiredQuestId;

    public Quest(String id, String name, String description,
                 List<QuestObjective> objectives, List<QuestReward> rewards,
                 int requiredLevel, String requiredQuestId) {
        this.id              = id;
        this.name            = name;
        this.description     = description;
        this.objectives      = objectives;
        this.rewards         = rewards;
        this.requiredLevel   = requiredLevel;
        this.requiredQuestId = requiredQuestId;
    }

    public void displayInfo(Player player, PlayerQuestData data) {
        boolean done = data.isCompleted(id);
        player.sendMessage(ColorUtil.color("&b=== Quest: &f" + name + (done ? " &a[DONE]" : "") + " &b==="));
        player.sendMessage(ColorUtil.color("  &7" + description));
        player.sendMessage(ColorUtil.color("  &bMuc tieu:"));
        for (int i = 0; i < objectives.size(); i++) {
            QuestObjective obj = objectives.get(i);
            int cur = done ? obj.getRequired() : data.getProgress(id, i);
            String color = cur >= obj.getRequired() ? "&a" : "&7";
            player.sendMessage(ColorUtil.color("    " + color + "- " + obj.getDescription()
                + " &8(" + Math.min(cur, obj.getRequired()) + "/" + obj.getRequired() + ")"));
        }
        player.sendMessage(ColorUtil.color("  &bPhan thuong:"));
        for (QuestReward reward : rewards) {
            player.sendMessage(ColorUtil.color("    &7- " + reward.getDescription()));
        }
        if (requiredLevel > 0)
            player.sendMessage(ColorUtil.color("  &7Yeu cau level: &e" + requiredLevel));
        if (!requiredQuestId.isEmpty())
            player.sendMessage(ColorUtil.color("  &7Yeu cau quest: &e" + requiredQuestId));
    }

    public String               getId()             { return id; }
    public String               getName()           { return name; }
    public String               getDescription()    { return description; }
    public List<QuestObjective> getObjectives()     { return objectives; }
    public List<QuestReward>    getRewards()        { return rewards; }
    public int                  getRequiredLevel()  { return requiredLevel; }
    public String               getRequiredQuestId(){ return requiredQuestId; }
}
