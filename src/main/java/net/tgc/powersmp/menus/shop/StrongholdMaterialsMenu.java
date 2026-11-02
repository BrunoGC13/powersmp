package net.tgc.powersmp.menus.shop;

import net.tgc.powersmp.Main;
import net.tgc.powersmp.economy.CoinsEconomy;
import net.tgc.powersmp.menus.Button;
import net.tgc.powersmp.menus.Menu;
import net.tgc.powersmp.other.ItemStackCreator;
import net.tgc.powersmp.api.item.CustomItemRegistry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class StrongholdMaterialsMenu extends Menu {

    // Preise pro Stück
    private static final int PRICE_RESONITE       = 120;
    private static final int PRICE_KINETIC         = 80;
    private static final int PRICE_EMERALD         = 15;
    private static final int PRICE_REDSTONE_DUST   = 3;
    private static final int PRICE_REDSTONE_BLOCK  = 25;

    public StrongholdMaterialsMenu(Main plugin, CoinsEconomy eco) {
        this.setTitle("<light_purple>Materials Shop");
        this.setSize(9 * 6);

        // Row headers (display only)
        addDisplayItem(0,  Material.AMETHYST_SHARD,   "<light_purple>Resonite Crystal",  "x1 · x16 · x32 · x64");
        addDisplayItem(9,  Material.EMERALD,           "<green>Kinetic Shard",             "x1 · x16 · x32 · x64");
        addDisplayItem(18, Material.EMERALD,           "<green>Emerald",                   "x1 · x16 · x32 · x64");
        addDisplayItem(27, Material.REDSTONE,          "<red>Redstone Dust",               "x1 · x16 · x32 · x64");
        addDisplayItem(36, Material.REDSTONE_BLOCK,    "<dark_red>Redstone Block",         "x1 · x16 · x32 · x64");

        // Back button
        this.addButton(new Button(49) {
            @Override public ItemStack getItem() {
                return ItemStackCreator.buildItem(Material.ARROW, "<gray>← Back", List.of());
            }
            @Override public void onClick(Player player) {
                new StrongholdShopMenu(plugin, eco).displayTo(player);
            }
        });

        // ── Resonite Crystal ─────────────────────────────────
        addCustomItemButtons(eco, "powersmp:resonite_crystal", PRICE_RESONITE,
                new int[]{1, 2, 3, 4}, Material.AMETHYST_SHARD);

        // ── Kinetic Shard ─────────────────────────────────────
        addCustomItemButtons(eco, "powersmp:kinetic_shard", PRICE_KINETIC,
                new int[]{10, 11, 12, 13}, Material.EMERALD);

        // ── Emerald ───────────────────────────────────────────
        addVanillaButtons(eco, Material.EMERALD, PRICE_EMERALD,
                new int[]{19, 20, 21, 22});

        // ── Redstone Dust ─────────────────────────────────────
        addVanillaButtons(eco, Material.REDSTONE, PRICE_REDSTONE_DUST,
                new int[]{28, 29, 30, 31});

        // ── Redstone Block ────────────────────────────────────
        addVanillaButtons(eco, Material.REDSTONE_BLOCK, PRICE_REDSTONE_BLOCK,
                new int[]{37, 38, 39, 40});
    }

    private void addDisplayItem(int slot, Material mat, String name, String sub) {
        this.addButton(new Button(slot) {
            @Override public ItemStack getItem() {
                return ItemStackCreator.buildItem(mat, name, List.of("<gray>" + sub));
            }
            @Override public void onClick(Player player) {}
        });
    }

    private static final int[] AMOUNTS = {1, 16, 32, 64};

    private void addCustomItemButtons(CoinsEconomy eco, String keyStr, int priceEach, int[] slots, Material icon) {
        for (int i = 0; i < 4; i++) {
            int amount = AMOUNTS[i];
            int slot = slots[i];
            int totalPrice = priceEach * amount;

            this.addButton(new Button(slot) {
                @Override
                public ItemStack getItem() {
                    NamespacedKey key = NamespacedKey.fromString(keyStr);
                    ItemStack stack = CustomItemRegistry.getInstance().createItemStack(key);
                    if (stack == null) stack = new ItemStack(icon);
                    stack.setAmount(1);
                    ItemMeta meta = stack.getItemMeta();
                    List<Component> lore = meta.lore() != null ? new ArrayList<>(meta.lore()) : new ArrayList<>();
                    lore.add(Component.empty());
                    lore.add(Component.text("Amount: x" + amount, NamedTextColor.WHITE));
                    lore.add(Component.text("Price: " + totalPrice + " Coins", NamedTextColor.GOLD));
                    meta.lore(lore);
                    stack.setItemMeta(meta);
                    return stack;
                }

                @Override
                public void onClick(Player player) {
                    OfflinePlayer op = Bukkit.getOfflinePlayer(player.getUniqueId());
                    if (!eco.has(op, totalPrice)) {
                        player.sendMessage(net.kyori.adventure.text.minimessage.MiniMessage.miniMessage()
                                .deserialize("<red>Not enough Coins! You need <gold>" + totalPrice + " Coins<red>."));
                        return;
                    }
                    eco.withdrawPlayer(op, totalPrice);
                    NamespacedKey key = NamespacedKey.fromString(keyStr);
                    ItemStack reward = CustomItemRegistry.getInstance().createItemStack(key);
                    if (reward == null) return;
                    reward.setAmount(amount);
                    player.getInventory().addItem(reward);
                    player.sendMessage(net.kyori.adventure.text.minimessage.MiniMessage.miniMessage()
                            .deserialize("<green>Purchased <white>x" + amount + " <green>for <gold>" + totalPrice + " Coins<green>!"));
                }
            });
        }
    }

    private void addVanillaButtons(CoinsEconomy eco, Material mat, int priceEach, int[] slots) {
        for (int i = 0; i < 4; i++) {
            int amount = AMOUNTS[i];
            int slot = slots[i];
            int totalPrice = priceEach * amount;

            this.addButton(new Button(slot) {
                @Override
                public ItemStack getItem() {
                    ItemStack stack = new ItemStack(mat);
                    ItemMeta meta = stack.getItemMeta();
                    List<Component> lore = new ArrayList<>();
                    lore.add(Component.empty());
                    lore.add(Component.text("Amount: x" + amount, NamedTextColor.WHITE));
                    lore.add(Component.text("Price: " + totalPrice + " Coins", NamedTextColor.GOLD));
                    meta.lore(lore);
                    stack.setItemMeta(meta);
                    return stack;
                }

                @Override
                public void onClick(Player player) {
                    OfflinePlayer op = Bukkit.getOfflinePlayer(player.getUniqueId());
                    if (!eco.has(op, totalPrice)) {
                        player.sendMessage(net.kyori.adventure.text.minimessage.MiniMessage.miniMessage()
                                .deserialize("<red>Not enough Coins! You need <gold>" + totalPrice + " Coins<red>."));
                        return;
                    }
                    eco.withdrawPlayer(op, totalPrice);
                    player.getInventory().addItem(new ItemStack(mat, amount));
                    player.sendMessage(net.kyori.adventure.text.minimessage.MiniMessage.miniMessage()
                            .deserialize("<green>Purchased <white>x" + amount + " <green>for <gold>" + totalPrice + " Coins<green>!"));
                }
            });
        }
    }
}