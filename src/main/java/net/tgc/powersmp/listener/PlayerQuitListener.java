package net.tgc.powersmp.listener;

import net.tgc.powersmp.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {
    private Main plugin;
    public PlayerQuitListener(Main plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        plugin.getObjectiveManager().savePlayer(e.getPlayer());
        plugin.getObjectiveManager().clearPlayer(e.getPlayer().getUniqueId());
    }
}
