package net.tgc.powersmp.menus.npcs;

import io.lumine.mythic.core.menus.items.ItemMenuContext;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.tgc.powersmp.api.item.CustomItemRegistry;
import net.tgc.powersmp.economy.CoinsEconomy;
import net.tgc.powersmp.menus.Button;
import net.tgc.powersmp.menus.Menu;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GemMasterMenu extends Menu {
    public GemMasterMenu(Player player, CoinsEconomy eco) {
        this.setTitle("<green>Gem Master Shop");
        this.setSize(9 * 3);
        MiniMessage mm = MiniMessage.miniMessage();

        int pricePowerEmerald = 15000;
        int priceEscapeGem = 30000;
        int pricePowerDiamond = 23000;

        this.addButton(new Button(10) {
            @Override
            public ItemStack getItem() {
                NamespacedKey key = new NamespacedKey("powersmp", "power_emerald");
                ItemStack stack = CustomItemRegistry.getInstance().createItemStack(key);
                ItemMeta meta = stack.getItemMeta();

                List<Component> lore = meta.lore() != null ? new ArrayList<>(meta.lore()) : new ArrayList<>();
                lore.add(Component.text("Price: " + pricePowerEmerald + " Coins", NamedTextColor.GOLD));
                meta.lore(lore);

                stack.setItemMeta(meta);
                return stack;
            }

            @Override
            public void onClick(Player player) {
                UUID playerUUID = player.getUniqueId();
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerUUID);
                if (eco.has(offlinePlayer, pricePowerEmerald)) {
                    eco.withdrawPlayer(offlinePlayer, pricePowerEmerald);
                    NamespacedKey key = new NamespacedKey("powersmp", "power_emerald");
                    ItemStack stack = CustomItemRegistry.getInstance().createItemStack(key);
                    player.getInventory().addItem(stack);
                } else  {
                    Component text = mm.deserialize("<red>You do not have enough money to buy this item!");
                    player.sendMessage(text);
                }
            }
        });
        this.addButton(new Button(13) {
            @Override
            public ItemStack getItem() {
                NamespacedKey key = new NamespacedKey("powersmp", "escape_gem");
                ItemStack stack = CustomItemRegistry.getInstance().createItemStack(key);
                ItemMeta meta = stack.getItemMeta();

                List<Component> lore = meta.lore() != null ? new ArrayList<>(meta.lore()) : new ArrayList<>();
                lore.add(Component.text("Price: " + priceEscapeGem + " Coins", NamedTextColor.GOLD));
                meta.lore(lore);

                stack.setItemMeta(meta);
                return stack;
            }

            @Override
            public void onClick(Player player) {
                UUID playerUUID = player.getUniqueId();
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerUUID);
                if (eco.has(offlinePlayer, priceEscapeGem)) {
                    eco.withdrawPlayer(offlinePlayer, priceEscapeGem);
                    NamespacedKey key = new NamespacedKey("powersmp", "escape_gem");
                    ItemStack stack = CustomItemRegistry.getInstance().createItemStack(key);
                    player.getInventory().addItem(stack);
                } else  {
                    Component text = mm.deserialize("<red>You do not have enough money to buy this item!");
                    player.sendMessage(text);
                }
            }
        });
        this.addButton(new Button(16) {
            @Override
            public ItemStack getItem() {
                NamespacedKey key = new NamespacedKey("powersmp", "power_diamond");
                ItemStack stack = CustomItemRegistry.getInstance().createItemStack(key);
                ItemMeta meta = stack.getItemMeta();

                List<Component> lore = meta.lore() != null ? new ArrayList<>(meta.lore()) : new ArrayList<>();
                lore.add(Component.text("Price: " + pricePowerDiamond + " Coins", NamedTextColor.GOLD));
                meta.lore(lore);

                stack.setItemMeta(meta);
                return stack;
            }

            @Override
            public void onClick(Player player) {
                UUID playerUUID = player.getUniqueId();
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerUUID);
                if (eco.has(offlinePlayer, pricePowerDiamond)) {
                    eco.withdrawPlayer(offlinePlayer, pricePowerDiamond);
                    NamespacedKey key = new NamespacedKey("powersmp", "power_diamond");
                    ItemStack stack = CustomItemRegistry.getInstance().createItemStack(key);
                    player.getInventory().addItem(stack);
                } else  {
                    Component text = mm.deserialize("<red>You do not have enough money to buy this item!");
                    player.sendMessage(text);
                }
            }
        });
    }
}
