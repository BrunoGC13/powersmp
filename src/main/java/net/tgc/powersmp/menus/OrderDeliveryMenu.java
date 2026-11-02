package net.tgc.powersmp.menus;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.tgc.powersmp.Main;
import net.tgc.powersmp.economy.order.Order;
import net.tgc.powersmp.economy.order.OrderManager;
import net.tgc.powersmp.economy.order.OrderResult;
import net.tgc.powersmp.economy.order.OrderResultType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrderDeliveryMenu extends Menu {

    private static final int MENU_SIZE = 54;

    private final Main plugin;
    private final OrderManager orderManager;
    private final Order order;
    private String prefix;

    private final MiniMessage mm = MiniMessage.miniMessage();

    public OrderDeliveryMenu(
            Main plugin,
            OrderManager orderManager,
            Order order
    ) {
        this.plugin = plugin;
        this.orderManager = orderManager;
        this.order = order;
        this.prefix = plugin.getConfig().getString("plugin-prefix");

        this.setSize(MENU_SIZE);
        this.setTitle("Deliver Items");
    }

    public Order getOrder() {
        return order;
    }

    public void handleClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) {
            return;
        }

        Inventory inventory = event.getInventory();

        List<ItemStack> contents = new ArrayList<>();

        for (ItemStack item : inventory.getContents()) {
            if (item == null || item.getType().isAir()) {
                continue;
            }

            contents.add(item.clone());
        }

        if (contents.isEmpty()) {
            return;
        }

        for (ItemStack item : contents) {
            deliverStack(player, item);
        }
    }

    private void deliverStack(Player player, ItemStack item) {
        OrderResult result = orderManager.sendItems(
                order,
                player,
                item
        );

        if (result.getType() == OrderResultType.SUCCESS) {
            player.sendMessage(mm.deserialize(
                    prefix +
                    "<white>Delivered <green>"
                            + item.getAmount()
                            + "x "
                            + item.getType().name().toLowerCase()
                            + "<green>."
            ));
            return;
        }

        // sendItems rejects materials that do not match the order.
        // Return the complete rejected stack to the player.
        giveOrDrop(player, item);

        player.sendMessage(mm.deserialize(
                prefix +
                "<red>" + result.getMsg()
                        + " <gray>Your items were returned."
        ));
    }

    private void giveOrDrop(Player player, ItemStack item) {
        Map<Integer, ItemStack> leftovers =
                player.getInventory().addItem(item);

        leftovers.values().forEach(leftover ->
                player.getWorld().dropItemNaturally(
                        player.getLocation(),
                        leftover
                )
        );
    }
}