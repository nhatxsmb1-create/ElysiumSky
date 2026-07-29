package dev.elysium.sky;

import dev.elysium.core.api.CoreAPI;
import dev.elysium.core.util.ColorUtil;
import dev.elysium.sky.gui.IslandGui;
import dev.elysium.sky.gui.QuestGui;
import dev.elysium.sky.gui.SkyMenuGui;
import dev.elysium.sky.island.IslandData;
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
            sender.sendMessage("Chi player dung duoc!"); return true;
        }

        // Mac dinh mo main menu
        if (args.length == 0) {
            CoreAPI.getCore().getGuiManager().open(player, new SkyMenuGui(plugin));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "menu", "m"         -> CoreAPI.getCore().getGuiManager().open(player, new SkyMenuGui(plugin));
            case "quest", "q"        -> CoreAPI.getCore().getGuiManager().open(player, new QuestGui(plugin));
            case "island", "level", "lv" -> CoreAPI.getCore().getGuiManager().open(player, new IslandGui(plugin, player));
            case "upgrade", "up"     -> CoreAPI.getCore().getGuiManager().open(player, new IslandGui(plugin, player));
            default                  -> CoreAPI.getCore().getGuiManager().open(player, new SkyMenuGui(plugin));
        }
        return true;
    }
}
