package net.tgc.powersmp.command;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.tgc.powersmp.Main;
import net.tgc.powersmp.other.tpa.TPA;
import net.tgc.powersmp.other.tpa.TPAManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

public class TPAResponseCommand implements BasicCommand {
    private final boolean accept;
    private final String permission;
    private final String prefix = Main.getInstance().getConfig().getString("plugin-prefix");

    public TPAResponseCommand(boolean accept) {
        this.accept = accept;
        this.permission = accept
                ? "powersmp.commands.tpaccept"
                : "powersmp.commands.tpdeny";
    }

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        if (!(source.getExecutor() instanceof Player player)) {
            source.getSender().sendRichMessage(
                    "<red>Only players can use this command.</red>"
            );
            return;
        }

        TPAManager tpaManager = Main.getInstance().getTpaManager();
        TPA request = tpaManager.getPendingRequest(player);

        if (request == null) {
            player.sendRichMessage(prefix + "<red>You do not have any pending teleport requests.</red>");
            return;
        }

        boolean successful;

        if (accept) {
            request = tpaManager.acceptRequest(request);
        } else {
            request = tpaManager.denyRequest(request);
        }

        if (!request.isAccepted()) {
            player.sendRichMessage(
                    prefix + "<red>That teleport request is no longer available.</red>"
            );
        }
    }

    @Override
    public boolean canUse(CommandSender sender) {
        return sender.hasPermission(permission) || sender.isOp();
    }

    @Override
    public @Nullable String permission() {
        return permission;
    }
}