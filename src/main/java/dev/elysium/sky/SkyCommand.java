package dev.elysium.sky;

import dev.elysium.core.api.CoreAPI;
import dev.elysium.core.util.ColorUtil;
import dev.elysium.sky.island.IslandData;
import dev.elysium.sky.island.UpgradeType;
import dev.elysium.sky.quest.Quest;
import dev.elysium.sky.quest.PlayerQuestData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SkyCommand implements CommandExecutor {

    private final ElysiumSky plugin;

    public SkyCommand(ElysiumSky plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Chi player moi dung duoc lenh nay!");
            return true;
        }

        if (args.length == 0) { sendHelp(player); return true; }

        switch (args[0].toLowerCase()) {
            case "quest", "q"       -> handleQuest(player, args);
            case "level", "lv"      -> handleLevel(player);
            case "upgrade", "up"    -> handleUpgrade(player, args);
            default                 -> sendHelp(player);
        }
        return true;
    }

    // ── Quest ─────────────────────────────────────────────────────────────────

    private void handleQuest(Player player, String[] args) {
        PlayerQuestData data = plugin.getQuestManager().getOrCreatePlayerData(player.getUniqueId());

        if (args.length < 2) {
            player.sendMessage(ColorUtil.color("&b=== Danh Sach Quest ==="));
            for (Quest quest : plugin.getQuestManager().getAllQuests()) {
                boolean done = data.isCompleted(quest.getId());
                boolean locked = !plugin.getQuestManager().canAccess(player, quest);
                String icon = done ? "&a✓" : locked ? "&8✗" : "&e○";
                player.sendMessage(ColorUtil.color("  " + icon + " &f" + quest.getName()
                    + " &7- " + quest.getDescription()));
            }
            player.sendMessage(ColorUtil.color("  &7Dung: /sky quest info <id>"));
            return;
        }

        if (args[1].equalsIgnoreCase("info") && args.length >= 3) {
            Quest quest = plugin.getQuestManager().getQuest(args[2]);
            if (quest == null) {
                player.sendMessage(ColorUtil.color("&cKhong tim thay quest &e" + args[2] + "&c!"));
                return;
            }
            quest.displayInfo(player, data);
        }
    }

    // ── Level ─────────────────────────────────────────────────────────────────

    private void handleLevel(Player player) {
        IslandData island = plugin.getIslandManager().getIslandData(player.getUniqueId());
        if (island == null) {
            player.sendMessage(ColorUtil.color("&cBan chua co dao! Hay tao dao truoc."));
            return;
        }
        long next = island.getLevelRequired(island.getLevel() + 1);
        player.sendMessage(ColorUtil.color("&b=== Island Level ==="));
        player.sendMessage(ColorUtil.color("  &7Level: &e" + island.getLevel()));
        player.sendMessage(ColorUtil.color("  &7Points: &e" + island.getPoints() + " &7/ &e" + next));
        player.sendMessage(ColorUtil.color("  &7Progress: " + ColorUtil.progressBar(
            (int)(island.getPoints() % next), (int)next, 10, '■', '□', "&a", "&8")));
    }

    // ── Upgrade ───────────────────────────────────────────────────────────────

    private void handleUpgrade(Player player, String[] args) {
        IslandData island = plugin.getIslandManager().getOrCreate(
            player.getUniqueId(), player.getName());

        if (args.length < 2) {
            player.sendMessage(ColorUtil.color("&b=== Island Upgrades ==="));
            for (UpgradeType type : UpgradeType.values()) {
                int cur  = island.getUpgrade(type);
                boolean maxed = cur >= type.getMaxLevel();
                long cost = type.getCost(cur + 1);
                String status = maxed ? "&a[MAX]" : "&7Cost: &6" + cost;
                player.sendMessage(ColorUtil.color(
                    "  &e" + type.name().toLowerCase() +
                    " &7| &f" + type.getDisplayName() +
                    " &7Lv" + cur + "/" + type.getMaxLevel() +
                    " | " + status));
            }
            player.sendMessage(ColorUtil.color("  &7Dung: /sky upgrade <ten>"));
            return;
        }

        UpgradeType type;
        try {
            type = UpgradeType.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage(ColorUtil.color("&cUpgrade khong ton tai! Xem danh sach: /sky upgrade"));
            return;
        }

        int current = island.getUpgrade(type);
        if (current >= type.getMaxLevel()) {
            player.sendMessage(ColorUtil.color("&c" + type.getDisplayName() + " da dat MAX LEVEL!"));
            return;
        }

        long cost = type.getCost(current + 1);
        if (!CoreAPI.hasBalance(player, cost)) {
            player.sendMessage(ColorUtil.color("&cKhong du tien! Can &6" + cost
                + " &ccoin, ban co &6" + String.format("%.1f", CoreAPI.getBalance(player))));
            return;
        }

        CoreAPI.removeBalance(player, cost);
        island.setUpgrade(type, current + 1);
        plugin.getIslandManager().save(island);
        player.sendMessage(ColorUtil.color("&a[UPGRADE] &f" + type.getDisplayName()
            + " &anang cap len &eLv" + (current + 1) + " &athanh cong!"));
    }

    // ── Help ──────────────────────────────────────────────────────────────────

    private void sendHelp(Player player) {
        player.sendMessage(ColorUtil.color("&b=== ElysiumSky ==="));
        player.sendMessage(ColorUtil.color("  &7/sky quest &f- Xem danh sach quest"));
        player.sendMessage(ColorUtil.color("  &7/sky quest info <id> &f- Chi tiet quest"));
        player.sendMessage(ColorUtil.color("  &7/sky level &f- Xem level dao"));
        player.sendMessage(ColorUtil.color("  &7/sky upgrade &f- Nang cap dao"));
    }
              }
