package net.tgc.powersmp.menus.recipes;

import net.tgc.powersmp.Main;
import net.tgc.powersmp.api.item.CustomItemRegistry;
import net.tgc.powersmp.menus.Button;
import net.tgc.powersmp.menus.Menu;
import net.tgc.powersmp.other.ItemStackCreator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class RecipeBookMaterialsMenu extends Menu {

    public RecipeBookMaterialsMenu(Main plugin) {
        this.setTitle("<light_purple>Materials & Gems");
        this.setSize(9 * 5);

        // ── Kinetic Shard ─────────────────────────────────────
        addRecipeEntry(10,
                "powersmp:kinetic_shard",
                List.of(
                        "<gray>Obtained by mining <green>Emerald Ore",
                        "<gray>with a <yellow>Diamond Pickaxe + Fortune III<gray>.",
                        "<gray>Also found in <yellow>Loot Orbs <gray>in the dungeon.",
                        "",
                        "<gold>Drop chance: <white>~50%"
                )
        );

        // ── Hard Kinetic Shard ────────────────────────────────
        addRecipeEntry(12,
                "powersmp:hard_kinetic_shard",
                List.of(
                        "<gray>Craft with <green>5x Kinetic Shards",
                        "<gray>in a <yellow>star pattern<gray>:",
                        "",
                        "<gray>  · K ·",
                        "<gray>  K K K",
                        "<gray>  · K ·",
                        "",
                        "<gray>K = Kinetic Shard"
                )
        );

        // ── Resonite Crystal Fragment ─────────────────────────
        addRecipeEntry(14,
                "powersmp:resonite_crystal_fragment",
                List.of(
                        "<gray>Obtained by mining <light_purple>Amethyst Clusters",
                        "<gray>or <light_purple>Amethyst Blocks <gray>with any pickaxe.",
                        "",
                        "<gold>Drop chance: <white>~25%"
                )
        );

        // ── Resonite Crystal ──────────────────────────────────
        addRecipeEntry(16,
                "powersmp:resonite_crystal",
                List.of(
                        "<gray>Craft with <light_purple>4x Resonite Crystal Fragment",
                        "<gray>+ <aqua>1x Echo Shard<gray>:",
                        "",
                        "<gray>  · A ·",
                        "<gray>  A E A",
                        "<gray>  · A ·",
                        "",
                        "<gray>A = Fragment, E = Echo Shard"
                )
        );

        // ── Redstone Battery ──────────────────────────────────
        addRecipeEntry(28,
                "powersmp:redstone_battery",
                List.of(
                        "<gray>Craft with <red>9x Redstone Dust",
                        "<gray>in a full <yellow>3x3 grid<gray>.",
                        "",
                        "<gray>Used to charge <red>Unfinished Redstone Items<gray>."
                )
        );

        // ── Back button ───────────────────────────────────────
        this.addButton(new Button(40) {
            @Override public ItemStack getItem() {
                return ItemStackCreator.buildItem(Material.ARROW, "<gray>← Back", List.of());
            }
            @Override public void onClick(Player player) {
                new net.tgc.powersmp.menus.recipes.RecipeBookMenu(plugin).displayTo(player);
            }
        });
    }

    private void addRecipeEntry(int slot, String keyStr, List<String> info) {
        this.addButton(new Button(slot) {
            @Override
            public ItemStack getItem() {
                NamespacedKey key = NamespacedKey.fromString(keyStr);
                ItemStack stack = CustomItemRegistry.getInstance().createItemStack(key);
                if (stack == null) stack = new ItemStack(Material.PAPER);
                ItemMeta meta = stack.getItemMeta();

                List<Component> lore = new ArrayList<>();
                lore.add(Component.empty());
                for (String line : info) {
                    lore.add(net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(line));
                }
                meta.lore(lore);
                stack.setItemMeta(meta);
                return stack;
            }
            @Override public void onClick(Player player) {}
        });
    }
}