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

public class StrongholdWeaponsMenu extends Menu {

    record WeaponEntry(Material mat, String name, int price) {}

    private static final WeaponEntry[] WEAPONS = {
            new WeaponEntry(Material.IRON_SWORD,    "<gray>Iron Sword",      50),
            new WeaponEntry(Material.IRON_AXE,      "<gray>Iron Axe",        55),
            new WeaponEntry(Material.GOLDEN_SWORD,  "<gold>Golden Sword",   100),
            new WeaponEntry(Material.GOLDEN_AXE,    "<gold>Golden Axe",     110),
            new WeaponEntry(Material.DIAMOND_SWORD, "<aqua>Diamond Sword",  400),
            new WeaponEntry(Material.DIAMOND_AXE,   "<aqua>Diamond Axe",    420),
    };

    private static final int[] SLOTS = {10, 12, 13, 15, 16, 22};

    public StrongholdWeaponsMenu(Main plugin, CoinsEconomy eco) {
        this.setTitle("<red>Weapons Shop");
        this.setSize(9 * 4);

        for (int i = 0; i < WEAPONS.length; i++) {
            WeaponEntry entry = WEAPONS[i];
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