package net.tgc.powersmp.menus.shop;

import net.tgc.powersmp.Main;
import net.tgc.powersmp.economy.CoinsEconomy;
import net.tgc.powersmp.menus.Button;
import net.tgc.powersmp.menus.Menu;
import net.tgc.powersmp.other.ItemStackCreator;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class StrongholdShopMenu extends Menu {

    public StrongholdShopMenu(Main plugin, CoinsEconomy eco) {
        this.setTitle("<dark_red>⚔ Secret Stronghold Shop");
        this.setSize(9 * 3);

        // ── Materials ────────────────────────────────────────
        this.addButton(new Button(10) {
            @Override
            public ItemStack getItem() {
                return ItemStackCreator.buildItem(Material.AMETHYST_SHARD, "<light_purple>Materials",
                        java.util.List.of("", "<gray>Buy Resonite Crystals, Kinetic Shards,",
                                "<gray>Emeralds, Redstone & more.",
                                "", "<yellow>Click to browse"));
            }
            @Override
            public void onClick(Player player) {
                new StrongholdMaterialsMenu(plugin, eco).displayTo(player);
            }
        });

        // ── Armor ────────────────────────────────────────────
        this.addButton(new Button(12) {
            @Override
            public ItemStack getItem() {
                return ItemStackCreator.buildItem(Material.DIAMOND_CHESTPLATE, "<aqua>Armor",
                        java.util.List.of("", "<gray>Iron, Gold and Diamond armor sets.",
                                "", "<yellow>Click to browse"));
            }
            @Override
            public void onClick(Player player) {
                new StrongholdArmorMenu(plugin, eco).displayTo(player);
            }
        });

        // ── Weapons ──────────────────────────────────────────
        this.addButton(new Button(14) {
            @Override
            public ItemStack getItem() {
                return ItemStackCreator.buildItem(Material.DIAMOND_SWORD, "<red>Weapons",
                        java.util.List.of("", "<gray>Iron, Gold and Diamond swords & axes.",
                                "", "<yellow>Click to browse"));
            }
            @Override
            public void onClick(Player player) {
                new StrongholdWeaponsMenu(plugin, eco).displayTo(player);
            }
        });

        // ── Tools ────────────────────────────────────────────
        this.addButton(new Button(16) {
            @Override
            public ItemStack getItem() {
                return ItemStackCreator.buildItem(Material.DIAMOND_PICKAXE, "<green>Tools",
                        java.util.List.of("", "<gray>Iron, Gold and Diamond pickaxes,",
                                "<gray>shovels, axes and hoes.",
                                "", "<yellow>Click to browse"));
            }
            @Override
            public void onClick(Player player) {
                new StrongholdToolsMenu(plugin, eco).displayTo(player);
            }
        });
    }
}