package dev.elysium.sky.gui;

import dev.elysium.core.api.CoreAPI;
import dev.elysium.core.gui.ElysiumGui;
import dev.elysium.core.gui.GuiButton;
import dev.elysium.core.gui.ItemBuilder;
import dev.elysium.core.util.ColorUtil;
import dev.elysium.sky.ElysiumSky;
import dev.elysium.sky.quest.Quest;
import dev.elysium.sky.quest.QuestObjective;
import dev.elysium.sky.quest.PlayerQuestData;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * GUI danh sach quest.
 * Xanh la = hoan thanh
 * Vang = dang lam / san sang
 * Xam = bi khoa (chua du level hoac chua hoan thanh quest truoc)
 */
public class QuestGui extends ElysiumGui {

    private final ElysiumSky sky;

    public QuestGui(ElysiumSky sky) {
        super("&a📋 Quests", 54);
        this.sky = sky;
    }

    @Override
    public void build(Player viewer) {
        fill(ItemBuilder.filler());

        PlayerQuestData data = sky.getQuestManager().getOrCreatePlayerData(viewer.getUniqueId());
        Collection<Quest> quests = sky.getQuestManager().getAllQuests();

        // Header
        long done = quests.stream().filter(q -> data.isCompleted(q.getId())).count();
        fill(4, new ItemBuilder(Material.WRITABLE_BOOK)
            .name("&a📋 Quests")
            .lore(
                "&7Hoan thanh: &a" + done + "&7/" + quests.size(),
                "",
                "&a■ &7Hoan thanh",
                "&e■ &7Dang lam / San sang",
                "&8■ &7Bi khoa"
            ).build());

        // Quest slots bat dau tu slot 9
        int slot = 9;
        for (Quest quest : quests) {
            if (slot >= 45) break; // Max 36 quests hien thi

            boolean completed = data.isCompleted(quest.getId());
            boolean locked    = !sky.getQuestManager().canAccess(viewer, quest);
            boolean canAccess = !locked && !completed;

            Material mat;
            String nameColor;
            if (completed)    { mat = Material.LIME_DYE;    nameColor = "&a"; }
            else if (locked)  { mat = Material.GRAY_DYE;    nameColor = "&8"; }
            else              { mat = Material.YELLOW_DYE;  nameColor = "&e"; }

            List<String> lore = new ArrayList<>();
            lore.add(ColorUtil.color("&7" + quest.getDescription()));
            lore.add("");

            if (completed) {
                lore.add(ColorUtil.color("&a✔ Da hoan thanh!"));
            } else if (locked) {
                lore.add(ColorUtil.color("&8✗ Bi khoa"));
                if (quest.getRequiredLevel() > 0)
                    lore.add(ColorUtil.color("  &8Yeu cau Level " + quest.getRequiredLevel()));
                if (!quest.getRequiredQuestId().isEmpty())
                    lore.add(ColorUtil.color("  &8Hoan thanh: " + quest.getRequiredQuestId()));
            } else {
                // Hien thi tien do
                lore.add(ColorUtil.color("&bTien do:"));
                List<dev.elysium.sky.quest.QuestObjective> objs = quest.getObjectives();
                for (int i = 0; i < objs.size(); i++) {
                    dev.elysium.sky.quest.QuestObjective obj = objs.get(i);
                    int cur = data.getProgress(quest.getId(), i);
                    int req = obj.getRequired();
                    String prog = "&7[" + Math.min(cur, req) + "/" + req + "]";
                    lore.add(ColorUtil.color("  " + prog + " &f" + obj.getDescription()));
                }
                lore.add("");
                lore.add(ColorUtil.color("&bPhan thuong:"));
                for (dev.elysium.sky.quest.QuestReward r : quest.getRewards()) {
                    lore.add(ColorUtil.color("  &7+ " + r.getDescription()));
                }
            }

            setButton(slot, new GuiButton(
                new ItemBuilder(mat)
                    .name(nameColor + quest.getName())
                    .lore(lore)
                    .build()
            ));
            slot++;
            // Skip border slots
            if (slot % 9 == 8) slot += 2;
        }

        // Back button
        setButton(49, new GuiButton(
            new ItemBuilder(Material.ARROW).name("&7← Quay lai").build(),
            e -> CoreAPI.getCore().getGuiManager().open(
                (Player) e.getWhoClicked(), new SkyMenuGui(sky))
        ));
    }
}
