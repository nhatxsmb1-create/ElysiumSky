package dev.elysium.sky.api;

import dev.elysium.sky.ElysiumSky;
import dev.elysium.sky.island.IslandData;
import dev.elysium.sky.quest.Quest;
import dev.elysium.sky.quest.QuestManager;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Public API cho ElysiumSky.
 * Cac plugin khac (ElysiumCombat, ElysiumWar...) goi class nay.
 *
 * Vi du:
 *   IslandData island = SkyAPI.getIsland(player);
 *   island.addPoints(100);
 */
public final class SkyAPI {

    private static ElysiumSky sky;
    public static void init(ElysiumSky plugin) { sky = plugin; }

    public static IslandData  getIsland(Player p)   { return sky.getIslandManager().getIslandData(p.getUniqueId()); }
    public static IslandData  getIsland(UUID uuid)  { return sky.getIslandManager().getIslandData(uuid); }
    public static QuestManager getQuestManager()    { return sky.getQuestManager(); }
    public static Quest        getQuest(String id)  { return sky.getQuestManager().getQuest(id); }
    public static ElysiumSky   getSky()             { return sky; }
}
