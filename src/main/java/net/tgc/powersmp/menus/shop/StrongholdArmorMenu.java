package net.tgc.powersmp.menus.shop;

import net.tgc.powersmp.Main;
import net.tgc.powersmp.economy.CoinsEconomy;
import net.tgc.powersmp.menus.Button;
import net.tgc.powersmp.menus.Menu;
import net.tgc.powersmp.other.ItemStackCreator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class StrongholdArmorMenu extends Menu {

    record ArmorEntry(Material mat, String name, int price) {}

    private static final ArmorEntry[] ARMOR = {
            new ArmorEntry(Material.IRON_HELMET,          "<gray>Iron Helmet",           40),
            new ArmorEntry(Material.IRON_CHESTPLATE,      "<gray>Iron Chestplate",        60),
            new ArmorEntry(Material.IRON_LEGGINGS,        "<gray>Iron Leggings",          55),
            new ArmorEntry(Material.IRON_BOOTS,           "<gray>Iron Boots",             35),
            new ArmorEntry(Material.GOLDEN_HELMET,        "<gold>Golden Helmet",          80),
            new ArmorEntry(Material.GOLDEN_CHESTPLATE,    "<gold>Golden Chestplate",     120),
            new ArmorEntry(Material.GOLDEN_LEGGINGS,      "<gold>Golden Leggings",       110),
            new ArmorEntry(Material.GOLDEN_BOOTS,         "<gold>Golden Boots",           70),
            new ArmorEntry(Material.DIAMOND_HELMET,       "<aqua>Diamond Helmet",        300),
            new ArmorEntry(Material.DIAMOND_CHESTPLATE,   "<aqua>Diamond Chestplate",    450),
            new ArmorEntry(Material.DIAMOND_LEGGINGS,     "<aqua>Diamond Leggings",      400),
            new ArmorEntry(Material.DIAMOND_BOOTS,        "<aqua>Diamond Boots",         250),
    };

    private static final int[] SLOTS = {1,2,3,4, 10,11,12,13, 19,20,21,22};

    public StrongholdArmorMenu(Main plugin, CoinsEconomy eco) {
        this.setTitle("<aqua>Armor Shop");
        this.setSize(9 * 4);

        for (int i = 0; i < ARMOR.length; i++) {
            ArmorEntry entry = ARMOR[i];
            int slot = SLOTS[i];

            this.addButton(new Button(slot) {
                @Override
                public ItemStack getItem() {
                    ItemStack stack = new ItemStack(entry.mat());
                    ItemMeta meta = stack.getItemMeta();
                    List<Component> lore = new ArrayList<>();
                    lore.add(Component.empty());
                    lore.add(Component.text("Price: " + entry.price() + " Coins", NamedTextColor.GOLD));
                    meta.lore(lore);
                    stack.setItemMeta(meta);
                    return stack;
                }

                @Override
                public void onClick(Player player) {
                    OfflinePlayer op = Bukkit.getOfflinePlayer(player.getUniqueId());
                    if (!eco.has(op, entry.price())) {
                        player.sendMessage(net.kyori.adventure.text.minimessage.MiniMessage.miniMessage()
                                .deserialize("<red>Not enough Coins! Need <gold>" + entry.price() + " Coins<red>."));
                        return;
                    }
                    eco.withdrawPlayer(op, entry.price());
                    player.getInventory().addItem(new ItemStack(entry.mat()));
                    player.sendMessage(net.kyori.adventure.text.minimessage.MiniMessage.miniMessage()
                            .deserialize("<green>Purchased for <gold>" + entry.price() + " Coins<green>!"));
                }
            });
        }

        this.addButton(new Button(31) {
            @Override public ItemStack getItem() {
                return ItemStackCreator.buildItem(Material.ARROW, "<gray>← Back", List.of());
            }
            @Override public void onClick(Player player) {
                new StrongholdShopMenu(plugin, eco).displayTo(player);
            }
        });
    }
}