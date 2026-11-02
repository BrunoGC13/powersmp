package net.tgc.powersmp.command.menus.shop;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.tgc.powersmp.Main;
import net.tgc.powersmp.economy.CoinsEconomy;
import net.tgc.powersmp.menus.Button;
import net.tgc.powersmp.menus.Menu;
import net.tgc.powersmp.other.ItemStackCreator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class MagicianShop extends Menu {

    private static final MiniMessage MM = MiniMessage.miniMessage();

    private static final int[] XP_AMOUNTS = {1, 16, 32, 64};
    private static final int XP_BOTTLE_PRICE = 96;

    private static final Material BORDER_MATERIAL =
            Material.PURPLE_STAINED_GLASS_PANE;

    private final Main plugin;
    private final CoinsEconomy economy;

    private record EnchantEntry(
            Enchantment enchantment,
            int level,
            String displayName,
            String description,
            int price
    ) {
    }

    /*
     * Progression prices:
     *
     * Fortune III is intentionally expensive because it:
     * - increases emerald income;
     * - improves resource mining;
     * - allows effective Kinetic Shard farming.
     */
    private static final List<EnchantEntry> ENCHANTMENTS = List.of(
            // Mining
            new EnchantEntry(
                    Enchantment.EFFICIENCY,
                    1,
                    "<aqua>Efficiency I",
                    "A small improvement to mining speed.",
                    250
            ),
            new EnchantEntry(
                    Enchantment.FORTUNE,
                    1,
                    "<light_purple>Fortune I",
                    "Slightly increases valuable block drops.",
                    750
            ),
            new EnchantEntry(
                    Enchantment.SILK_TOUCH,
                    1,
                    "<dark_purple>Silk Touch",
                    "Mines blocks in their original form.",
                    1_200
            ),

            // Durability
            new EnchantEntry(
                    Enchantment.UNBREAKING,
                    1,
                    "<gray>Unbreaking I",
                    "Slightly reduces item durability loss.",
                    375
            ),
            new EnchantEntry(
                    Enchantment.MENDING,
                    1,
                    "<green>Mending",
                    "Uses experience to repair damaged equipment.",
                    2_500
            ),

            // Armor
            new EnchantEntry(
                    Enchantment.PROTECTION,
                    1,
                    "<aqua>Protection I",
                    "Provides basic protection from damage.",
                    55
            ),
            new EnchantEntry(
                    Enchantment.FEATHER_FALLING,
                    1,
                    "<yellow>Feather Falling I",
                    "Slightly reduces damage from falling.",
                    125
            ),
            new EnchantEntry(
                    Enchantment.RESPIRATION,
                    1,
                    "<blue>Respiration I",
                    "Allows you to breathe underwater for longer.",
                    125
            ),
            new EnchantEntry(
                    Enchantment.THORNS,
                    1,
                    "<dark_green>Thorns I",
                    "Deals a small amount of damage to attackers.",
                    225
            ),

            // Combat
            new EnchantEntry(
                    Enchantment.SHARPNESS,
                    1,
                    "<red>Sharpness I",
                    "Adds a small amount of weapon damage.",
                    312
            ),
            new EnchantEntry(
                    Enchantment.FIRE_ASPECT,
                    1,
                    "<gold>Fire Aspect I",
                    "Sets enemies on fire when struck.",
                    500
            ),
            new EnchantEntry(
                    Enchantment.KNOCKBACK,
                    1,
                    "<white>Knockback I",
                    "Pushes enemies away when struck.",
                    300
            ),
            new EnchantEntry(
                    Enchantment.LOOTING,
                    1,
                    "<light_purple>Looting I",
                    "Slightly improves mob drop quantities.",
                    500
            )
    );

    public MagicianShop(Main plugin, CoinsEconomy economy) {
        this.plugin = plugin;
        this.economy = economy;

        setTitle("<dark_purple>✦ Magician's Shop");
        setSize(9 * 6);

        addBackground();
        addHeader();
        addExperienceBottleButtons();
        addEnchantmentButtons();
        addFooter();
    }

    private void addBackground() {
        /*
         * Do not place decorative buttons in slots 45, 49, or 53.
         * Those slots are used by the footer buttons.
         */
        int[] borderSlots = {
                0, 1, 2, 3, 4, 5, 6, 7, 8,
                46, 47, 48, 50, 51, 52
        };

        for (int slot : borderSlots) {
            addButton(new Button(slot) {
                @Override
                public ItemStack getItem() {
                    return ItemStackCreator.buildItem(
                            BORDER_MATERIAL,
                            "<dark_purple> ",
                            List.of()
                    );
                }

                @Override
                public void onClick(Player player) {
                    // Decorative button.
                }
            });
        }
    }

    private void addHeader() {
        addButton(new Button(4) {
            @Override
            public ItemStack getItem() {
                return ItemStackCreator.buildItem(
                        Material.ENCHANTING_TABLE,
                        "<light_purple>Magician's Enchantments",
                        List.of(
                                "<gray>Purchase Level I enchanted books.",
                                "<gray>Combine books to create stronger levels.",
                                "",
                                "<yellow>Important:",
                                "<gray>Higher-level books are not sold here.",
                                "<gray>Reach later progression areas for advanced enchantments."
                        )
                );
            }

            @Override
            public void onClick(Player player) {
                player.sendMessage(MM.deserialize(
                        "<light_purple>The Magician sells enchanted books."
                ));
            }
        });

        addButton(new Button(7) {
            @Override
            public ItemStack getItem() {
                return ItemStackCreator.buildItem(
                        Material.GOLD_NUGGET,
                        "<gold>Your Coins",
                        List.of(
                                "<gray>Click to view your current balance."
                        )
                );
            }

            @Override
            public void onClick(Player player) {
                sendBalance(player);
            }
        });
    }

    private void addExperienceBottleButtons() {
        addButton(new Button(9) {
            @Override
            public ItemStack getItem() {
                return ItemStackCreator.buildItem(
                        Material.EXPERIENCE_BOTTLE,
                        "<green>Experience Bottles",
                        List.of(
                                "<gray>Useful for applying books",
                                "<gray>and repairing equipment.",
                                "",
                                "<gold>" + XP_BOTTLE_PRICE
                                        + " Coins <gray>per bottle"
                        )
                );
            }

            @Override
            public void onClick(Player player) {
                player.sendMessage(MM.deserialize(
                        "<green>Choose an amount of XP bottles to purchase."
                ));
            }
        });

        for (int i = 0; i < XP_AMOUNTS.length; i++) {
            int slot = 10 + i;
            int amount = XP_AMOUNTS[i];
            int price = amount * XP_BOTTLE_PRICE;

            addButton(new Button(slot) {
                @Override
                public ItemStack getItem() {
                    ItemStack item = new ItemStack(
                            Material.EXPERIENCE_BOTTLE,
                            amount
                    );

                    ItemMeta meta = item.getItemMeta();
                    meta.displayName(MM.deserialize(
                            "<green>Experience Bottles <gray>x" + amount
                    ));
                    meta.lore(List.of(
                            Component.empty(),
                            MM.deserialize(
                                    "<gray>Amount: <white>" + amount
                            ),
                            MM.deserialize(
                                    "<gray>Price: <gold>" + price + " Coins"
                            ),
                            Component.empty(),
                            MM.deserialize("<yellow>Click to purchase")
                    ));
                    item.setItemMeta(meta);

                    return item;
                }

                @Override
                public void onClick(Player player) {
                    purchaseExperienceBottles(player, amount, price);
                }
            });
        }
    }

    private void addEnchantmentButtons() {
        /*
         * Slots 14 through 42 provide 29 enchantment slots.
         * The current enchantment list fits on this single page.
         */
        for (int i = 0; i < ENCHANTMENTS.size(); i++) {
            int slot = 14 + i;

            if (slot >= 45) {
                break;
            }

            addEnchantButton(slot, ENCHANTMENTS.get(i));
        }
    }

    private void addEnchantButton(int slot, EnchantEntry entry) {
        addButton(new Button(slot) {
            @Override
            public ItemStack getItem() {
                return createEnchantmentBook(entry, true);
            }

            @Override
            public void onClick(Player player) {
                purchaseEnchantment(player, entry);
            }
        });
    }

    private void addFooter() {
        addButton(new Button(45) {
            @Override
            public ItemStack getItem() {
                return ItemStackCreator.buildItem(
                        Material.BOOK,
                        "<light_purple>How the Shop Works",
                        List.of(
                                "<gray>1. Choose an enchanted book.",
                                "<gray>2. Pay with Coins.",
                                "<gray>3. Apply it to your equipment.",
                                "",
                                "<yellow>Higher-tier enchantments cost more."
                        )
                );
            }

            @Override
            public void onClick(Player player) {
                player.sendMessage(MM.deserialize(
                        "<light_purple>Choose an enchanted book and click "
                                + "it to purchase it."
                ));
            }
        });

        addButton(new Button(49) {
            @Override
            public ItemStack getItem() {
                return ItemStackCreator.buildItem(
                        Material.BARRIER,
                        "<red>Close Shop",
                        List.of("<gray>Click to close the menu.")
                );
            }

            @Override
            public void onClick(Player player) {
                player.closeInventory();
            }
        });

        addButton(new Button(53) {
            @Override
            public ItemStack getItem() {
                return ItemStackCreator.buildItem(
                        Material.SUNFLOWER,
                        "<gold>Refresh Balance",
                        List.of(
                                "<gray>Click to view your current",
                                "<gray>coin balance."
                        )
                );
            }

            @Override
            public void onClick(Player player) {
                sendBalance(player);
            }
        });
    }

    private void purchaseExperienceBottles(
            Player player,
            int amount,
            int price
    ) {
        OfflinePlayer offlinePlayer =
                Bukkit.getOfflinePlayer(player.getUniqueId());

        if (!economy.has(offlinePlayer, price)) {
            sendInsufficientFundsMessage(player, price);
            return;
        }

        ItemStack bottles = new ItemStack(
                Material.EXPERIENCE_BOTTLE,
                amount
        );

        if (!hasInventorySpace(player, bottles)) {
            player.sendMessage(MM.deserialize(
                    "<red>Your inventory does not have enough space."
            ));
            return;
        }

        if (!withdraw(player, price)) {
            player.sendMessage(MM.deserialize(
                    "<red>The purchase could not be completed."
            ));
            return;
        }

        player.getInventory().addItem(bottles);

        player.sendMessage(MM.deserialize(
                "<green>Purchased <white>x" + amount
                        + " Experience Bottles <green>for <gold>"
                        + price
                        + " Coins<green>."
        ));
    }

    private void purchaseEnchantment(
            Player player,
            EnchantEntry entry
    ) {
        ItemStack book = createEnchantmentBook(entry, false);

        if (!hasInventorySpace(player, book)) {
            player.sendMessage(MM.deserialize(
                    "<red>Your inventory does not have enough space."
            ));
            return;
        }

        OfflinePlayer offlinePlayer =
                Bukkit.getOfflinePlayer(player.getUniqueId());

        if (!economy.has(offlinePlayer, entry.price())) {
            sendInsufficientFundsMessage(player, entry.price());
            return;
        }

        if (!withdraw(player, entry.price())) {
            player.sendMessage(MM.deserialize(
                    "<red>The purchase could not be completed."
            ));
            return;
        }

        player.getInventory().addItem(book);

        player.sendMessage(MM.deserialize(
                "<green>Purchased "
                        + entry.displayName()
                        + " <green>for <gold>"
                        + entry.price()
                        + " Coins<green>."
        ));
    }

    private ItemStack createEnchantmentBook(
            EnchantEntry entry,
            boolean includeShopLore
    ) {
        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);

        EnchantmentStorageMeta meta =
                (EnchantmentStorageMeta) book.getItemMeta();

        meta.addStoredEnchant(
                entry.enchantment(),
                entry.level(),
                true
        );

        meta.displayName(
                MM.deserialize(entry.displayName())
                        .decoration(TextDecoration.ITALIC, false)
        );

        List<Component> lore = new ArrayList<>();

        lore.add(MM.deserialize(
                "<gray>" + entry.description()
        ));
        lore.add(Component.empty());

        if (includeShopLore) {
            lore.add(MM.deserialize(
                    "<gray>Price: <gold>"
                            + entry.price()
                            + " Coins"
            ));
            lore.add(Component.empty());
            lore.add(MM.deserialize(
                    "<yellow>Click to purchase"
            ));
        }

        meta.lore(lore);
        book.setItemMeta(meta);

        return book;
    }

    private boolean hasInventorySpace(
            Player player,
            ItemStack item
    ) {
        int remaining = item.getAmount();

        for (ItemStack content :
                player.getInventory().getStorageContents()) {

            if (content == null || content.getType().isAir()) {
                remaining -= item.getMaxStackSize();
            } else if (content.isSimilar(item)) {
                remaining -= item.getMaxStackSize() - content.getAmount();
            }

            if (remaining <= 0) {
                return true;
            }
        }

        return remaining <= 0;
    }

    private boolean withdraw(Player player, int price) {
        OfflinePlayer offlinePlayer =
                Bukkit.getOfflinePlayer(player.getUniqueId());

        return economy.withdrawPlayer(
                offlinePlayer,
                price
        ).transactionSuccess();
    }

    private void sendBalance(Player player) {
        OfflinePlayer offlinePlayer =
                Bukkit.getOfflinePlayer(player.getUniqueId());

        int balance = (int) economy.getBalance(offlinePlayer);

        player.sendMessage(MM.deserialize(
                "<gold>Your balance: <yellow>"
                        + balance
                        + " Coins"
        ));
    }

    private void sendInsufficientFundsMessage(
            Player player,
            int price
    ) {
        OfflinePlayer offlinePlayer =
                Bukkit.getOfflinePlayer(player.getUniqueId());

        int balance = (int) economy.getBalance(offlinePlayer);
        int missing = price - balance;

        player.sendMessage(MM.deserialize(
                "<red>You cannot afford this enchantment. "
                        + "You need <gold>"
                        + price
                        + " Coins<red>, but you only have <gold>"
                        + balance
                        + " Coins<red>."
        ));

        if (missing > 0) {
            player.sendMessage(MM.deserialize(
                    "<gray>You are missing <gold>"
                            + missing
                            + " Coins<gray>."
            ));
        }
    }
}