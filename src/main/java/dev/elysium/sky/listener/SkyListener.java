package dev.elysium.sky.listener;

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
        // Tao island data neu chua co
        plugin.getIslandManager().getOrCreate(
            e.getPlayer().getUniqueId(), e.getPlayer().getName());
        // Tao quest data
        plugin.getQuestManager().getOrCreatePlayerData(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        String mat = e.getBlock().getType().name();

        // Quest progress
        plugin.getQuestManager().handleProgress(player, QuestObjective.Type.BREAK_BLOCK, mat, 1);

        // Island points
        int pts = plugin.getSkyConfig().getBlockPoints(mat);
        if (pts > 0) {
            IslandData island = plugin.getIslandManager().getOrCreate(
                player.getUniqueId(), player.getName());
            island.addPoints(pts);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        Player player = e.getPlayer();
        String mat = e.getBlock().getType().name();

        // Quest progress
        plugin.getQuestManager().handleProgress(player, QuestObjective.Type.PLACE_BLOCK, mat, 1);

        // Island points (placing cung tinh diem)
        int pts = plugin.getSkyConfig().getBlockPoints(mat);
        if (pts > 0) {
            IslandData island = plugin.getIslandManager().getOrCreate(
                player.getUniqueId(), player.getName());
            island.addPoints(pts);
        }
    }

    @EventHandler
    public void onKill(EntityDeathEvent e) {
        Player killer = e.getEntity().getKiller();
        if (killer == null) return;
        plugin.getQuestManager().handleProgress(
            killer, QuestObjective.Type.KILL_MOB, e.getEntity().getType().name(), 1);
    }
}
