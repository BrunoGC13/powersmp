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

public class FarmerShop extends Menu {
    MiniMessage mm = MiniMessage.miniMessage();
    private Main plugin;
    private CoinsEconomy eco;
    String prefix;

    public FarmerShop(Main plugin, CoinsEconomy eco) {
        this.plugin = plugin;
        this.eco = eco;
        this.prefix = plugin.getConfig().getString("plugin-prefix");

        this.setSize(54);
        this.setTitle("Food shop");

        // Bread x1
        this.addButton(new Button(1) {
            @Override
            public ItemStack getItem() {
                ItemStack bread = new ItemStack(Material.BREAD);
                ItemMeta meta = bread.getItemMeta();

                List<Component> lore = new ArrayList<>();
                lore.add(Component.empty());
                lore.add(mm.deserialize("<yellow>Buy 1x bread"));
                lore.add(mm.deserialize("<gray>Price: 8 Coins").decorate(TextDecoration.ITALIC));

                meta.lore(lore);
                bread.setItemMeta(meta);

                return bread;
            }

            @Override
            public void onClick(Player player) {
                buy(player, new ItemStack(Material.BREAD, 1), 1, 8);
            }
        });

        // Bread x8
        this.addButton(new Button(2) {
            @Override
            public ItemStack getItem() {
                ItemStack bread = new ItemStack(Material.BREAD,8);
                ItemMeta meta = bread.getItemMeta();

                List<Component> lore = new ArrayList<>();
                lore.add(Component.empty());
                lore.add(mm.deserialize("<yellow>Buy 8x bread"));
                lore.add(mm.deserialize("<gray>Price: 64 Coins").decorate(TextDecoration.ITALIC));

                meta.lore(lore);
                bread.setItemMeta(meta);

                return bread;
            }

            @Override
            public void onClick(Player player) {
                buy(player, new ItemStack(Material.BREAD, 8), 8, 64);
            }
        });

        // Bread x16
        this.addButton(new Button(3) {
            @Override
            public ItemStack getItem() {
                ItemStack bread = new ItemStack(Material.BREAD, 16);
                ItemMeta meta = bread.getItemMeta();

                List<Component> lore = new ArrayList<>();
                lore.add(Component.empty());
                lore.add(mm.deserialize("<yellow>Buy 16x bread"));
                lore.add(mm.deserialize("<gray>Price: 128 Coins").decorate(TextDecoration.ITALIC));

                meta.lore(lore);
                bread.setItemMeta(meta);

                return bread;
            }

            @Override
            public void onClick(Player player) {
                buy(player, new ItemStack(Material.BREAD, 16), 16, 128);
            }
        });


        // Carrot x1
        this.addButton(new Button(10) {
            @Override
            public ItemStack getItem() {
                ItemStack bread = new ItemStack(Material.CARROT);
                ItemMeta meta = bread.getItemMeta();

                List<Component> lore = new ArrayList<>();
                lore.add(Component.empty());
                lore.add(mm.deserialize("<yellow>Buy 1x carrot"));
                lore.add(mm.deserialize("<gray>Price: 6 Coins").decorate(TextDecoration.ITALIC));

                meta.lore(lore);
                bread.setItemMeta(meta);

                return bread;
            }

            @Override
            public void onClick(Player player) {
                buy(player, new ItemStack(Material.CARROT, 1), 1, 6);
            }
        });

        // Carrot x8
        this.addButton(new Button(11) {
            @Override
            public ItemStack getItem() {
                ItemStack bread = new ItemStack(Material.CARROT, 8);
                ItemMeta meta = bread.getItemMeta();

                List<Component> lore = new ArrayList<>();
                lore.add(Component.empty());
                lore.add(mm.deserialize("<yellow>Buy 8x carrots"));
                lore.add(mm.deserialize("<gray>Price: 48 Coins").decorate(TextDecoration.ITALIC));

                meta.lore(lore);
                bread.setItemMeta(meta);

                return bread;
            }

            @Override
            public void onClick(Player player) {
                buy(player, new ItemStack(Material.CARROT, 8), 8, 48);
            }
        });

        // Carrot x16
        this.addButton(new Button(12) {
            @Override
            public ItemStack getItem() {
                ItemStack bread = new ItemStack(Material.CARROT, 16);
                ItemMeta meta = bread.getItemMeta();

                List<Component> lore = new ArrayList<>();
                lore.add(Component.empty());
                lore.add(mm.deserialize("<yellow>Buy 16x carrots"));
                lore.add(mm.deserialize("<gray>Price: 96 Coins").decorate(TextDecoration.ITALIC));

                meta.lore(lore);
                bread.setItemMeta(meta);

                return bread;
            }

            @Override
            public void onClick(Player player) {
                buy(player, new ItemStack(Material.CARROT, 16), 16, 96);
            }
        });


        // Potato x1
        this.addButton(new Button(19) {
            @Override
            public ItemStack getItem() {
                ItemStack bread = new ItemStack(Material.POTATO);
                ItemMeta meta = bread.getItemMeta();

                List<Component> lore = new ArrayList<>();
                lore.add(Component.empty());
                lore.add(mm.deserialize("<yellow>Buy 1x potato"));
                lore.add(mm.deserialize("<gray>Price: 4 Coins").decorate(TextDecoration.ITALIC));

                meta.lore(lore);
                bread.setItemMeta(meta);

                return bread;
            }

            @Override
            public void onClick(Player player) {
                buy(player, new ItemStack(Material.POTATO, 1), 1, 4);
            }
        });

        // Potato x8
        this.addButton(new Button(20) {
            @Override
            public ItemStack getItem() {
                ItemStack bread = new ItemStack(Material.POTATO, 8);
                ItemMeta meta = bread.getItemMeta();

                List<Component> lore = new ArrayList<>();
                lore.add(Component.empty());
                lore.add(mm.deserialize("<yellow>Buy 8x potatoes"));
                lore.add(mm.deserialize("<gray>Price: 32 Coins").decorate(TextDecoration.ITALIC));

                meta.lore(lore);
                bread.setItemMeta(meta);

                return bread;
            }

            @Override
            public void onClick(Player player) {
                buy(player, new ItemStack(Material.POTATO, 8), 8, 32);
            }
        });

        // Potato x16
        this.addButton(new Button(21) {
            @Override
            public ItemStack getItem() {
                ItemStack bread = new ItemStack(Material.POTATO, 16);
                ItemMeta meta = bread.getItemMeta();

                List<Component> lore = new ArrayList<>();
                lore.add(Component.empty());
                lore.add(mm.deserialize("<yellow>Buy 16x potatoes"));
                lore.add(mm.deserialize("<gray>Price: 64 Coins").decorate(TextDecoration.ITALIC));

                meta.lore(lore);
                bread.setItemMeta(meta);

                return bread;
            }

            @Override
            public void onClick(Player player) {
                buy(player, new ItemStack(Material.POTATO, 16), 16, 64);
            }
        });


        // White Wool x1
        this.addButton(new Button(28) {
            @Override
            public ItemStack getItem() {
                ItemStack bread = new ItemStack(Material.WHITE_WOOL);
                ItemMeta meta = bread.getItemMeta();

                List<Component> lore = new ArrayList<>();
                lore.add(Component.empty());
                lore.add(mm.deserialize("<yellow>Buy 1x white wool"));
                lore.add(mm.deserialize("<gray>Price: 16 Coins").decorate(TextDecoration.ITALIC));

                meta.lore(lore);
                bread.setItemMeta(meta);

                return bread;
            }

            @Override
            public void onClick(Player player) {
                buy(player, new ItemStack(Material.WHITE_WOOL, 1), 1, 16);
            }
        });

        // White Wool x4
        this.addButton(new Button(29) {
            @Override
            public ItemStack getItem() {
                ItemStack bread = new ItemStack(Material.WHITE_WOOL, 4);
                ItemMeta meta = bread.getItemMeta();

                List<Component> lore = new ArrayList<>();
                lore.add(Component.empty());
                lore.add(mm.deserialize("<yellow>Buy 4x white wool"));
                lore.add(mm.deserialize("<gray>Price: 64 Coins").decorate(TextDecoration.ITALIC));

                meta.lore(lore);
                bread.setItemMeta(meta);

                return bread;
            }

            @Override
            public void onClick(Player player) {
                buy(player, new ItemStack(Material.WHITE_WOOL, 4), 4, 64);
            }
        });


        // Milk Bucket x1
        this.addButton(new Button(37) {
            @Override
            public ItemStack getItem() {
                ItemStack bread = new ItemStack(Material.MILK_BUCKET);
                ItemMeta meta = bread.getItemMeta();

                List<Component> lore = new ArrayList<>();
                lore.add(Component.empty());
                lore.add(mm.deserialize("<yellow>Buy 1x milk bucket"));
                lore.add(mm.deserialize("<gray>Price: 8 Coins").decorate(TextDecoration.ITALIC));

                meta.lore(lore);
                bread.setItemMeta(meta);

                return bread;
            }

            @Override
            public void onClick(Player player) {
                buy(player, new ItemStack(Material.MILK_BUCKET, 1), 1, 8);
            }
        });
    }

    public void buy(Player p, ItemStack item, int amount, double price) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(p.getUniqueId());
        EconomyResponse ecoRes = eco.withdrawPlayer(offlinePlayer, price);

        if (!(ecoRes.transactionSuccess())) {
            p.sendMessage(mm.deserialize(prefix + "<red>" + ecoRes.errorMessage));
            return;
        }

        p.getInventory().addItem(item);
        p.sendMessage(mm.deserialize(prefix + "<white>You bought <green>" + amount + "x " + item.getType().name().toLowerCase() + " <white>for <yellow>" + price + " coins"));
    }
}
