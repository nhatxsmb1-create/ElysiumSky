package dev.elysium.sky.gui;

import dev.elysium.core.api.CoreAPI;
import dev.elysium.core.gui.ElysiumGui;
import dev.elysium.core.gui.GuiButton;
import dev.elysium.core.gui.ItemBuilder;
import dev.elysium.core.util.ColorUtil;
import dev.elysium.sky.ElysiumSky;
import dev.elysium.sky.island.IslandData;
import dev.elysium.sky.island.UpgradeType;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * GUI nang cap dao.
 * Mo tu SkyMenuGui hoac /sky island
 */
public class IslandGui extends ElysiumGui {

    private final ElysiumSky sky;
    private final Player     owner;

    public IslandGui(ElysiumSky sky, Player owner) {
        super("&b🏝 Island: &f" + owner.getName(), 36);
        this.sky   = sky;
        this.owner = owner;
    }

    @Override
    public void build(Player viewer) {
        fill(ItemBuilder.filler());

        IslandData island = sky.getIslandManager().getOrCreate(
            owner.getUniqueId(), owner.getName());

        long nextLevelPts = island.getLevelRequired(island.getLevel() + 1);

        // ── Header: Island info ───────────────────────────────────────────────
        fill(4, new ItemBuilder(Material.BEACON)
            .name("&b🏝 Island Level &e" + island.getLevel())
            .lore(
                "&7Points: &b" + island.getPoints() + " &7/ &b" + nextLevelPts,
                "&7Progress: " + ColorUtil.progressBar(
                    (int)(island.getPoints() % Math.max(1, nextLevelPts)),
                    (int)nextLevelPts, 10, '■', '□', "&a", "&8"),
                "",
                "&8Dat block co gia tri de tang diem"
            ).glow().build());

        // ── 4 Upgrade buttons ─────────────────────────────────────────────────
        int[] upgradeSlots = {10, 12, 14, 16};
        UpgradeType[] types = UpgradeType.values();

        for (int i = 0; i < Math.min(types.length, upgradeSlots.length); i++) {
            UpgradeType type = types[i];
            int cur   = island.getUpgrade(type);
            int max   = type.getMaxLevel();
            long cost = type.getCost(cur + 1);
            boolean maxed  = cur >= max;
            boolean afford = !maxed && CoreAPI.hasBalance(owner, cost);

            Material mat = switch (type) {
                case SIZE          -> Material.GRASS_BLOCK;
                case SPAWNER_LIMIT -> Material.SPAWNER;
                case MEMBER_LIMIT  -> Material.PLAYER_HEAD;
                case GENERATOR_TIER -> Material.COBBLESTONE;
            };

            List<String> lore = new ArrayList<>();
            lore.add(ColorUtil.color("&7" + getUpgradeDesc(type)));
            lore.add("");
            lore.add(ColorUtil.color("&7Level: &e" + cur + "&7/" + max));
            if (maxed) {
                lore.add(ColorUtil.color("&a✔ MAX LEVEL"));
            } else {
                lore.add(ColorUtil.color("&7Chi phi: &6" + cost + " coins"));
                lore.add(ColorUtil.color(afford ? "&eClick de nang cap!" : "&cKhong du tien!"));
            }

            final UpgradeType finalType = type;
            setButton(upgradeSlots[i], new GuiButton(
                new ItemBuilder(mat)
                    .name((maxed ? "&a" : "&b") + type.getDisplayName()
                        + " &7[Lv" + cur + "/" + max + "]")
                    .lore(lore)
                    .build(),
                e -> {
                    if (maxed) { owner.sendMessage(ColorUtil.color("&cDa dat MAX!")); return; }
                    if (!CoreAPI.hasBalance(owner, cost)) {
                        owner.sendMessage(ColorUtil.color("&cKhong du tien! Can: &6" + cost));
                        return;
                    }
                    CoreAPI.removeBalance(owner, cost);
                    island.setUpgrade(finalType, cur + 1);
                    sky.getIslandManager().save(island);
                    owner.sendMessage(ColorUtil.color(
                        "&a[UPGRADE] &f" + finalType.getDisplayName() +
                        " &anang len &eLv" + (cur + 1) + "!"));
                    // Rebuild GUI
                    CoreAPI.getCore().getGuiManager().open((Player) e.getWhoClicked(),
                        new IslandGui(sky, owner));
                }
            ));
        }

        // ── Back button ───────────────────────────────────────────────────────
        setButton(31, new GuiButton(
            new ItemBuilder(Material.ARROW).name("&7← Quay lai").build(),
            e -> CoreAPI.getCore().getGuiManager().open(
                (Player) e.getWhoClicked(), new SkyMenuGui(sky))
        ));
    }

    private String getUpgradeDesc(UpgradeType type) {
        return switch (type) {
            case SIZE           -> "Tang kich thuoc bien gioi dao";
            case SPAWNER_LIMIT  -> "Tang so spawner toi da tren dao";
            case MEMBER_LIMIT   -> "Tang so thanh vien toi da";
            case GENERATOR_TIER -> "Nang cap may phat khoi tot hon";
        };
    }
}
