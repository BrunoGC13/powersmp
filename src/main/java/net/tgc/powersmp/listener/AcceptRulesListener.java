package net.tgc.powersmp.listener;

import io.papermc.paper.connection.PlayerGameConnection;
import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.event.player.PlayerCustomClickEvent;
import net.kyori.adventure.key.Key;
import net.tgc.powersmp.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@SuppressWarnings("UnstableApiUsage")
public class AcceptRulesListener implements Listener {
    private Main plugin;

    public AcceptRulesListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onRulesAccept(PlayerCustomClickEvent e) {
        if (!e.getIdentifier().equals(Key.key("powersmp:rules/accept"))) {
            return;
        }
        if (!(e.getCommonConnection() instanceof PlayerGameConnection connection)) {
            return;
        }

        DialogResponseView view = e.getDialogResponseView();
        if (view == null) {
            return;
        }

        plugin.getPlayerData().set("players." + ((PlayerGameConnection) e.getCommonConnection()).getPlayer().getUniqueId() + ".acceptedRules", true);
        plugin.savePlayerData();
    }
}
