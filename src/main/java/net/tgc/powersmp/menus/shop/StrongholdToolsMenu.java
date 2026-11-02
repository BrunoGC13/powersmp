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

public class StrongholdToolsMenu extends Menu {

    record ToolEntry(Material mat, String name, int price) {}

    private static final ToolEntry[] TOOLS = {
            new ToolEntry(Material.IRON_PICKAXE,    "<gray>Iron Pickaxe",      55),
            new ToolEntry(Material.IRON_SHOVEL,     "<gray>Iron Shovel",        35),
            new ToolEntry(Material.IRON_AXE,        "<gray>Iron Axe",           50),
            new ToolEntry(Material.IRON_HOE,        "<gray>Iron Hoe",           30),
            new ToolEntry(Material.GOLDEN_PICKAXE,  "<gold>Golden Pickaxe",    110),
            new ToolEntry(Material.GOLDEN_SHOVEL,   "<gold>Golden Shovel",      70),
            new ToolEntry(Material.GOLDEN_AXE,      "<gold>Golden Axe",        100),
            new ToolEntry(Material.GOLDEN_HOE,      "<gold>Golden Hoe",         60),
            new ToolEntry(Material.DIAMOND_PICKAXE, "<aqua>Diamond Pickaxe",   450),
            new ToolEntry(Material.DIAMOND_SHOVEL,  "<aqua>Diamond Shovel",    280),
            new ToolEntry(Material.DIAMOND_AXE,     "<aqua>Diamond Axe",       420),
            new ToolEntry(Material.DIAMOND_HOE,     "<aqua>Diamond Hoe",       250),
    };

    private static final int[] SLOTS = {1,2,3,4, 10,11,12,13, 19,20,21,22};

    public StrongholdToolsMenu(Main plugin, CoinsEconomy eco) {
        this.setTitle("<green>Tools Shop");
        this.setSize(9 * 4);

        for (int i = 0; i < TOOLS.length; i++) {
            ToolEntry entry = TOOLS[i];
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