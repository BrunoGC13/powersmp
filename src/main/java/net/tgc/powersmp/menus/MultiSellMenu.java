package net.tgc.powersmp.menus;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.tgc.powersmp.Main;
import net.tgc.powersmp.economy.order.OrderResult;
import net.tgc.powersmp.economy.order.OrderResultType;
import net.tgc.powersmp.sell.SellManager;
import net.tgc.powersmp.sell.SellResult;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MultiSellMenu extends Menu {
    private static final int SIZE = 54;
    MiniMessage mm = MiniMessage.miniMessage();

    private final Main plugin;
    private final SellManager sellManager;
    private String prefix;

    public MultiSellMenu(Main plugin) {
        this.plugin = plugin;
        this.prefix = plugin.getConfig().getString("plugin-prefix");
        this.sellManager = plugin.getSellManager();

        this.setSize(SIZE);
        this.setTitle("Multi Sell");
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

        SellResult result = sellManager.multiSell(contents, player);

        if (!(result.isSuccess())) {
            player.sendMessage(result.getMessage());
            return;
        }

        for (ItemStack item : result.getFailedItems()) {
            giveOrDrop(player, item);
        }

        player.sendMessage(mm.deserialize(prefix + "<white>Sold for<green> " + result.getPrice() + " coins."));
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
