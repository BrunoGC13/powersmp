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
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ButcherMenu extends Menu {

    private final MiniMessage mm = MiniMessage.miniMessage();
    private final CoinsEconomy eco;
    private final String prefix;

    public ButcherMenu(Main plugin, CoinsEconomy eco) {
        this.eco = eco;
        this.prefix = plugin.getConfig().getString("plugin-prefix");

        this.setSize(54);
        this.setTitle("Butcher shop");

        addFood(Material.COOKED_CHICKEN, "chicken", 4, 10);
        addFood(Material.COOKED_BEEF, "cooked beef", 8, 19);
        addFood(Material.COOKED_PORKCHOP, "cooked porkchop", 7, 28);
        addFood(Material.COOKED_MUTTON, "cooked mutton", 6, 37);
    }

    private void addFood(Material material, String itemName, double pricePerItem, int startingSlot) {
        int[] amounts = {1, 4, 16, 32, 64};

        for (int index = 0; index < amounts.length; index++) {
            int amount = amounts[index];
            int slot = startingSlot + index;
            double price = pricePerItem * amount;

            this.addButton(new Button(slot) {
                @Override
                public ItemStack getItem() {
                    ItemStack item = new ItemStack(material, amount);
                    ItemMeta meta = item.getItemMeta();

                    List<Component> lore = new ArrayList<>();
                    lore.add(Component.empty());
                    lore.add(mm.deserialize("<yellow>Buy " + amount + "x " + itemName));
                    lore.add(mm.deserialize("<gray>Price: " + formatPrice(price) + " Coins")
                            .decorate(TextDecoration.ITALIC));

                    meta.lore(lore);
                    item.setItemMeta(meta);

                    return item;
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