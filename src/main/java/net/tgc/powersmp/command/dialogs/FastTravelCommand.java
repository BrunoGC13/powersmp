package net.tgc.powersmp.command.dialogs;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.tgc.powersmp.menus.dialog.TravelDialog;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

public class FastTravelCommand implements BasicCommand {
    @Override
    public void execute(CommandSourceStack commandSourceStack, String[] args) {
        if (!(commandSourceStack.getExecutor() instanceof Player p)) {
            return;
        }

        p.showDialog(new TravelDialog(p).getDialog());
    }

    @Override
    public boolean canUse(CommandSender sender) {
        return sender.hasPermission(permission());
    }

    @Override
    public @Nullable String permission() {
        return "powersmp.commands.fast_travel";
    }
}
