package net.tgc.powersmp.listener;

import org.bukkit.entity.Item;
import net.tgc.powersmp.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class GetLorePage implements Listener {
    private Main plugin;
    public GetLorePage(Main plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void onInteract(PlayerInteractEntityEvent e) {
        if (!(e.getRightClicked() instanceof Item item)) return;
        if (!plugin.getLorePagesManager().isLorePage(item.getItemStack())) return;

        e.setCancelled(true);
        e.getPlayer().getInventory().addItem(item.getItemStack().clone());
        e.getPlayer().sendMessage("§e✦ §6You found: " +
                item.getItemStack().getItemMeta().getDisplayName());
    }
}
