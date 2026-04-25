package net.tgc.powersmp.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.tgc.powersmp.player.PlayerDataManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /powerbar — toggles the power display in the action bar.
 */
public class PowerBarCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can use this command.", NamedTextColor.RED));
            return true;
        }
        PlayerDataManager data = PlayerDataManager.getInstance();
        boolean nowEnabled = !data.isPowerBarEnabled(player);
        data.setPowerBarEnabled(player, nowEnabled);
        sender.sendMessage(Component.text(
                "Power bar " + (nowEnabled ? "enabled" : "disabled") + ".", NamedTextColor.GREEN));
        return true;
    }
}
