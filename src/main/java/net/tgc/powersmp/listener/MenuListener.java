package net.tgc.powersmp.listener;

import net.tgc.powersmp.Main;
import net.tgc.powersmp.menus.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class MenuListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;
        if (!p.hasMetadata("powersmp")) return;

        if (e.getClickedInventory() == null) return;

        Menu menu = (Menu) p.getMetadata("powersmp")
                .get(0)
                .value();

        /*
         * The delivery menu is a real storage inventory.
         * Players must be able to place and remove items normally.
         */
        if (menu instanceof OrderDeliveryMenu) {
            return;
        }

        // Only intercept clicks inside regular GUI menus.
        if (!e.getClickedInventory().equals(e.getView().getTopInventory())) {
            return;
        }

        e.setCancelled(true);

        int slot = e.getSlot();

        for (Button button : menu.getButtons()) {
            if (button.getSlot() == slot) {
                button.onClick(p);
                return;
            }
        }

        for (Item item : menu.getItems()) {
            if (item.getSlot() == slot) {
                return;
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (!(e.getPlayer() instanceof Player p)) {
            return;
        }

        if (!p.hasMetadata("powersmp")) {
            return;
        }

        Menu menu = (Menu) p.getMetadata("powersmp")
                .get(0)
                .value();

        if (menu instanceof OrderDeliveryMenu deliveryMenu) {
            deliveryMenu.handleClose(e);
        }

        if (menu instanceof MultiSellMenu multiSellMenu) {
            multiSellMenu.handleClose(e);
        }

        p.removeMetadata("powersmp", Main.getInstance());
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        e.getPlayer().removeMetadata(
                "powersmp",
                Main.getInstance()
        );
    }
}