package net.tgc.powersmp.economy.networth;

import net.tgc.powersmp.Main;
import net.tgc.powersmp.economy.ItemOwnerShip;
import net.tgc.powersmp.economy.order.Order;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class NetWorthManager {
    private final Main plugin;
    private ItemOwnerShip ownerShip;

    public NetWorthManager(Main plugin, ItemOwnerShip ownerShip) {
        this.plugin = plugin;
        this.ownerShip = ownerShip;
    }

    public NetWorth getNetworth(Player p) {
        if (p == null) return null;

        return new NetWorth(p, plugin.getPlayerData().getDouble("players." + p.getUniqueId() + ".networth", 0));
    }

    public NetWorth getNetworth(UUID playerUUID) {
        if (playerUUID == null) return null;

        Player p = Bukkit.getPlayer(playerUUID);

        if (p == null) return null;

        return new NetWorth(p, plugin.getPlayerData().getDouble("players." + p.getUniqueId() + ".networth", 0));
    }

    private void savePlayerNetworth(NetWorth newNetWorth) {
        if (newNetWorth == null) return;

        Player p = newNetWorth.getPlayer();
        NetWorth oldNetWorth = getNetworth(p);
        if (oldNetWorth == null) return;

        double newNetworthAmount = newNetWorth.getAmount() + oldNetWorth.getAmount();

        plugin.getPlayerData().set("players." + p.getUniqueId() + ".networth", newNetworthAmount);
        plugin.savePlayerData();
    }

//    public void updatePlayerNetworthOld(Player p) {
//        if (p == null) return;
//        List<UUID> items = new ArrayList<>();
//
//        Map<UUID, UUID> itemOwnerMap = ownerShip.getItemPlayerMap();
//
//        for (Map.Entry<UUID, UUID> entry : itemOwnerMap.entrySet()) {
//            if (entry.getValue().equals(p.getUniqueId())) items.add(entry.getValue());
//        }
//
//        NetWorth netWorth = new NetWorth(p);
//
//        for (UUID uuid : items) {
//            int worth;
//            ItemStack item;
//
//            // some method to get the item and its price tag from order
//
//
//
//            Optional<Order> order = plugin.getOrderManager().getBestPayingOrder(item.getType());
//            if (order.isEmpty()) return;
//            worth = order.get().getPriceEach();
//
//            netWorth.addWorth(worth);
//        }
//
//        saveNetworth(netWorth);
//    }

    public void updatePlayerNetworth(Player p, ItemStack item) {
        if (p == null) return;
        int worth;

        Optional<Order> order = plugin.getOrderManager().getBestPayingOrder(item.getType());
        if (order.isEmpty()) return;

        worth = order.get().getPriceEach();
        NetWorth netWorth = new NetWorth(p, worth);

        savePlayerNetworth(netWorth);
    }
}
