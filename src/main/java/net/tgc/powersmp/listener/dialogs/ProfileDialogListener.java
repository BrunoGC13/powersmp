package net.tgc.powersmp.listener.dialogs;

import io.papermc.paper.connection.PlayerGameConnection;
import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.event.player.PlayerCustomClickEvent;
import net.kyori.adventure.key.Key;
import net.tgc.powersmp.player.PlayerDataManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@SuppressWarnings("UnstableApiUsage")
public class ProfileDialogListener implements Listener {
    @EventHandler
    public void onProfileMenu(PlayerCustomClickEvent e) {
        if (!e.getIdentifier().equals(Key.key("powersmp:profile_menu"))) {
            return;
        }
        if (!(e.getCommonConnection() instanceof PlayerGameConnection connection)) {
            return;
        }

        PlayerDataManager pdm = PlayerDataManager.getInstance();
        DialogResponseView view = e.getDialogResponseView();
        if (view == null) {
            return;
        }

        boolean showPowerbar = Boolean.TRUE.equals(view.getBoolean("powerbar"));
        pdm.setPowerBarEnabled(connection.getPlayer(), showPowerbar);
    }

}
