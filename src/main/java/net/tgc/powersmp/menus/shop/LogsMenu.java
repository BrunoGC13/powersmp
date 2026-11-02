package net.tgc.powersmp.menus.shop;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.milkbowl.vault.economy.EconomyResponse;
import net.tgc.powersmp.Main;
import net.tgc.powersmp.economy.CoinsEconomy;
import net.tgc.powersmp.menus.Button;
import net.tgc.powersmp.menus.Menu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class LogsMenu extends Menu {

    private final MiniMessage mm = MiniMessage.miniMessage();
    private final CoinsEconomy eco;
    private final String prefix;

    public LogsMenu(Main plugin, CoinsEconomy eco) {
        this.eco = eco;
        this.prefix = plugin.getConfig().getString("plugin-prefix");

        this.setSize(54);
        this.setTitle("Logs shop");

        // Iron Axe
        this.addButton(new Button(1) {
            @Override
            public ItemStack getItem() {
                return createIronAxe();
            }

            @Override
            public void onClick(Player player) {
                buy(player, createIronAxe(), 1, 64);
            }
        });

        addLog(Material.OAK_LOG, "oak log", 4, 10);
        addLog(Material.SPRUCE_LOG, "spruce log", 4, 19);
        addLog(Material.BIRCH_LOG, "birch log", 4, 28);
        addLog(Material.JUNGLE_LOG, "jungle log", 4, 37);
        addLog(Material.DARK_OAK_LOG, "dark oak log", 4, 46);
    }

    private ItemStack createIronAxe() {
        ItemStack axe = new ItemStack(Material.IRON_AXE);
        ItemMeta meta = axe.getItemMeta();

        meta.addEnchant(Enchantment.UNBREAKING, 1, true);
        meta.addEnchant(Enchantment.EFFICIENCY, 1, true);

        List<Component> lore = new ArrayList<>();
        lore.add(Component.empty());
        lore.add(mm.deserialize("<yellow>Buy iron axe"));
        lore.add(mm.deserialize("<gray>Price: 64 Coins").decorate(TextDecoration.ITALIC));

        meta.lore(lore);
        axe.setItemMeta(meta);

        return axe;
    }

    private void addLog(Material material, String logName, double pricePerLog, int startingSlot) {
        int[] amounts = {1, 4, 16, 32, 64};

        for (int index = 0; index < amounts.length; index++) {
            int amount = amounts[index];
            int slot = startingSlot + index;
            double price = pricePerLog * amount;

            this.addButton(new Button(slot) {
                @Override
                public ItemStack getItem() {
                    ItemStack logs = new ItemStack(material, amount);
                    ItemMeta meta = logs.getItemMeta();

                    List<Component> lore = new ArrayList<>();
                    lore.add(Component.empty());
                    lore.add(mm.deserialize("<yellow>Buy " + amount + "x " + logName));
                    lore.add(mm.deserialize("<gray>Price: " + formatPrice(price) + " Coins")
                            .decorate(TextDecoration.ITALIC));

                    meta.lore(lore);
                    logs.setItemMeta(meta);

                    return logs;
                }

                @Override
                public void onClick(Player player) {
                    buy(player, new ItemStack(material, amount), amount, price);
                }
            });
        }
    }

    private String formatPrice(double price) {
        if (price == Math.floor(price)) {
            return String.valueOf((int) price);
        }

        return String.valueOf(price);
    }

    private void buy(Player player, ItemStack item, int amount, double price) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player.getUniqueId());
        EconomyResponse economyResponse = eco.withdrawPlayer(offlinePlayer, price);

        if (!economyResponse.transactionSuccess()) {
            player.sendMessage(mm.deserialize(prefix + "<red>" + economyResponse.errorMessage));
            return;
        }

        player.getInventory().addItem(item);
        player.sendMessage(mm.deserialize(
                prefix
                        + "<white>You bought <green>"
                        + amount
                        + "x "
                        + item.getType().name().toLowerCase()
                        + " <white>for <yellow>"
                        + formatPrice(price)
                        + " coins"
        ));
    }
}