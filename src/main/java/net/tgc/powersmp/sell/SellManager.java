package net.tgc.powersmp.sell;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.milkbowl.vault.economy.EconomyResponse;
import net.tgc.powersmp.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SellManager {
    private Main plugin;
    private String prefix;

    public SellManager(Main plugin) {
        this.plugin = plugin;
        this.prefix = plugin.getConfig().getString("plugin-prefix");
    }

    MiniMessage mm = MiniMessage.miniMessage();

    public SellResult sell(ItemStack item, Player p) {
        Material mat = item.getType();
        int amount = item.getAmount();
        String name = mat.name();
        double price = getPrice(item) * amount;

        OfflinePlayer offP = Bukkit.getOfflinePlayer(p.getName());
        EconomyResponse ecoRes = plugin.getCoinsEconomy().depositPlayer(offP, price);
        if (!(ecoRes.transactionSuccess())) {
            return new SellResult(item, amount, price, false, Component.text(ecoRes.errorMessage));
        }
        p.getInventory().setItemInMainHand(null);
        return new SellResult(item, amount, price, true);
    }
    public double getPrice(ItemStack item) {
        if (item == null) return -1;

        String materialName = item.getType().name();

        for (Map<?, ?> entry : plugin.getSellPricesData().getMapList("prices.items")) {
            String type = (String) entry.get("type");
            if (type == null) continue;

            if (type.equalsIgnoreCase(materialName)) {
                return ((Number) entry.get("price")).doubleValue();
            }
        }
        return -1;
    }

    public SellResult multiSell(List<ItemStack> items, Player p) {
        List<ItemStack> failedItems = new ArrayList<>();
        double money = 0;

        for (ItemStack item : items) {

            if (!(isSellable(item))) {
                failedItems.add(item);
                p.sendMessage(mm.deserialize(prefix + "<white>One item can not be sold: <green>" + item.getAmount() + " ").append(item.displayName()));
                continue;
            }

            money += getPrice(item) * item.getAmount();
        }

        OfflinePlayer offP = Bukkit.getOfflinePlayer(p.getUniqueId());
        EconomyResponse ecoRes = plugin.getCoinsEconomy().depositPlayer(offP, money);
        if (!(ecoRes.transactionSuccess())) return new SellResult(items, failedItems, money, false, mm.deserialize(prefix + "<white>" + ecoRes.errorMessage));

        return new SellResult(items, failedItems, money, true);
    }

    public boolean isSellable(ItemStack item) {
        if (item == null) return false;

        String materialName = item.getType().name();

        for (Map<?, ?> entry : plugin.getSellPricesData().getMapList("prices.items")) {
            String type = (String) entry.get("type");
            if (type == null) continue;

            if (type.equalsIgnoreCase(materialName)) {
                return true;
            }
        }
        return false;

    }
}
