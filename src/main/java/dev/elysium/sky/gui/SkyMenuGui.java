package dev.elysium.sky.gui;

import dev.elysium.core.api.CoreAPI;
import dev.elysium.core.gui.ElysiumGui;
import dev.elysium.core.gui.GuiButton;
import dev.elysium.core.gui.ItemBuilder;
import dev.elysium.core.player.ElysiumPlayer;
import dev.elysium.core.util.ColorUtil;
import dev.elysium.sky.ElysiumSky;
import dev.elysium.sky.island.IslandData;
import dev.elysium.sky.quest.PlayerQuestData;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Menu chinh ElysiumSky.
 * Mo bang: /sky
 * Cac nut: Quest | Island | Leaderboard
 */
public class SkyMenuGui extends ElysiumGui {

    private final ElysiumSky sky;

    public SkyMenuGui(ElysiumSky sky) {
        super("&b✦ Sky Menu", 27);
        this.sky = sky;
    }

    @Override
    public void build(Player viewer) {
        fill(ItemBuilder.filler());

        ElysiumPlayer ep  = CoreAPI.getPlayer(viewer);
        IslandData island = sky.getIslandManager().getIslandData(viewer.getUniqueId());
        PlayerQuestData qd = sky.getQuestManager().getOrCreatePlayerData(viewer.getUniqueId());
        int doneQuests    = (int) sky.getQuestManager().getAllQuests().stream()
            .filter(q -> qd.isCompleted(q.getId())).count();
        int totalQuests   = sky.getQuestManager().getQuestCount();

        // Header
        fill(4, new ItemBuilder(Material.NETHER_STAR)
            .name("&b✦ ElysiumSky")
            .lore(
                "&7Island Level: &e" + (island != null ? island.getLevel() : 0),
                "&7Quests: &a" + doneQuests + "&7/" + totalQuests,
                "&7Mana: &b" + (ep != null ? ep.getMana() + "/" + ep.getMaxMana() : "N/A")
            ).glow().build());

        // Quest button
        setButton(11, new GuiButton(
            new ItemBuilder(Material.WRITABLE_BOOK)
                .name("&a📋 Quests")
                .lore(
                    "&7Xem va theo doi quest",
                    "",
                    "&aHoan thanh: &f" + doneQuests + "&7/" + totalQuests,
                    "",
                    "&eClick de mo!"
                ).build(),
            e -> CoreAPI.getCore().getGuiManager().open(viewer, new QuestGui(sky))
        ));

        // Island button
        setButton(13, new GuiButton(
            new ItemBuilder(Material.GRASS_BLOCK)
                .name("&b🏝 Island")
                .lore(
                    "&7Quan ly va nang cap dao",
                    "",
                    "&7Level: &e" + (island != null ? island.getLevel() : 0),
                    "&7Points: &b" + (island != null ? island.getPoints() : 0),
                    "",
                    "&eClick de mo!"
                ).build(),
            e -> CoreAPI.getCore().getGuiManager().open(viewer, new IslandGui(sky, viewer))
        ));

        // Stats button
        setButton(15, new GuiButton(
            new ItemBuilder(Material.GOLD_INGOT)
                .name("&6📊 Chi So")
                .lore(
                    "&7Balance: &6" + String.format("%.1f", CoreAPI.getBalance(viewer)),
                    "&7Level: &e" + (ep != null ? ep.getLevel() : 0),
                    "&7Class: &d" + (ep != null ? ep.getPlayerClass() : "N/A"),
                    "",
                    "&7/is home &8- Teleport ve dao",
                    "&7/is create &8- Tao dao"
                ).build()
        ));
    }
}
