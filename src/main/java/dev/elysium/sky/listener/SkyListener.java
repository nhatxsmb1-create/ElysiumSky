package dev.elysium.sky.listener;

import dev.elysium.core.api.CoreAPI;
import dev.elysium.core.event.ElysiumLevelUpEvent;
import dev.elysium.sky.ElysiumSky;
import dev.elysium.sky.island.IslandData;
import dev.elysium.sky.quest.QuestObjective;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class SkyListener implements Listener {

    private final ElysiumSky plugin;
    public SkyListener(ElysiumSky plugin) { this.plugin = plugin; }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        // Tao island data + quest data khi join
        plugin.getIslandManager().getOrCreate(
            e.getPlayer().getUniqueId(), e.getPlayer().getName());
        plugin.getQuestManager().getOrCreatePlayerData(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        String mat = e.getBlock().getType().name();

        plugin.getQuestManager().handleProgress(
            player, QuestObjective.Type.BREAK_BLOCK, mat, 1);

        int pts = plugin.getSkyConfig().getBlockPoints(mat);
        if (pts > 0) {
            IslandData island = plugin.getIslandManager()
                .getOrCreate(player.getUniqueId(), player.getName());
            island.addPoints(pts);
            // Check island level quest
            plugin.getQuestManager().handleProgress(
                player, QuestObjective.Type.REACH_ISLAND_LEVEL,
                String.valueOf(island.getLevel()), 1);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        Player player = e.getPlayer();
        String mat = e.getBlock().getType().name();

        plugin.getQuestManager().handleProgress(
            player, QuestObjective.Type.PLACE_BLOCK, mat, 1);

        int pts = plugin.getSkyConfig().getBlockPoints(mat);
        if (pts > 0) {
            IslandData island = plugin.getIslandManager()
                .getOrCreate(player.getUniqueId(), player.getName());
            island.addPoints(pts);
        }
    }

    @EventHandler
    public void onKill(EntityDeathEvent e) {
        Player killer = e.getEntity().getKiller();
        if (killer == null) return;
        plugin.getQuestManager().handleProgress(
            killer, QuestObjective.Type.KILL_MOB,
            e.getEntity().getType().name(), 1);
    }

    /**
     * Khi player len level (tu ElysiumCore):
     * Check quest REACH_ISLAND_LEVEL va check achievement
     */
    @EventHandler
    public void onLevelUp(ElysiumLevelUpEvent e) {
        // Check achievements
        CoreAPI.checkAchievements(e.getPlayer());
        // Thong bao sky bonus
        if (e.getNewLevel() % 10 == 0) {
            e.getPlayer().sendMessage(
                dev.elysium.core.util.ColorUtil.color(
                    "&b[Sky] &7Dat Level &e" + e.getNewLevel() +
                    "&7! Island bonus co the duoc mo khoa."));
        }
    }
}
