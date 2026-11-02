package net.tgc.powersmp.menus.recipes;

import net.tgc.powersmp.Main;
import net.tgc.powersmp.api.item.CustomItemRegistry;
import net.tgc.powersmp.menus.Button;
import net.tgc.powersmp.menus.Menu;
import net.tgc.powersmp.other.ItemStackCreator;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class RecipeBookDropsMenu extends Menu {

    public RecipeBookDropsMenu(Main plugin) {
        this.setTitle("<green>How to get Materials");
        this.setSize(9 * 5);

        addDropEntry(10, "powersmp:kinetic_shard", List.of(
            "<yellow>Where to find:",
            "<gray>Mine <green>Emerald Ore <gray>or <green>Deepslate Emerald Ore",
            "<gray>with a <yellow>Diamond Pickaxe + Fortune III<gray>.",
            "<gray>Found in the <yellow>Emerald Mine <gray>inside the dungeon.",
            "",
            "<gold>Drop chance: <white>~50%",
            "<gray>Also obtainable from <yellow>Loot Orbs"
        ));

        addDropEntry(12, "powersmp:resonite_crystal_fragment", List.of(
            "<yellow>Where to find:",
            "<gray>Mine <light_purple>Amethyst Cluster<gray>, <light_purple>Amethyst Block",
            "<gray>or <light_purple>Budding Amethyst <gray>with any pickaxe.",
            "<gray>Found in the <yellow>Amethyst Caverns <gray>inside the dungeon.",
            "",
            "<gold>Drop chance: <white>~25%"
        ));

        addDropEntry(14, "powersmp:resonite_crystal", List.of(
            "<yellow>Where to find:",
            "<gray>Craft from <light_purple>4x Resonite Crystal Fragment",
            "<gray>+ <aqua>1x Echo Shard <gray>in crafting table.",
            "",
            "<gray>Echo Shards found in <aqua>Ancient Cities"
        ));

        addDropEntry(16, "powersmp:primal_ember", List.of(
            "<yellow>Where to find:",
            "<gray>Mine <gold>Ancient Debris <gray>with a",
            "<gray><yellow>Netherite Pickaxe + Fortune III<gray>.",
            "",
            "<gold>Drop chance: <white>~30%"
        ));

        addDropEntry(28, "powersmp:void_fragment", List.of(
            "<yellow>Where to find:",
            "<gray>Mine <white>End Stone <gray>with a",
            "<gray><yellow>Netherite Pickaxe + Silk Touch<gray>.",
            "",
            "<gold>Drop chance: <white>~20%"
        ));

        addDropEntry(30, "powersmp:storm_essence", List.of(
            "<yellow>Where to find:",
            "<gray>Mine <light_purple>Amethyst Cluster <gray>with a",
            "<gray><yellow>Diamond/Netherite Pickaxe + Fortune II+<gray>.",
            "",
            "<gold>Drop chance: <white>~15%"
        ));

        addDropEntry(32, "powersmp:soul_dust", List.of(
            "<yellow>Where to find:",
            "<gray>Mine <gray>Soul Sand <gray>or <gray>Soul Soil <gray>with a",
            "<gray><yellow>Netherite Pickaxe + Fortune II+<gray>.",
            "",
            "<gold>Drop chance: <white>~25%"
        ));

        addDropEntry(34, "powersmp:redstone_battery", List.of(
            "<yellow>How to craft:",
            "<gray>9x <red>Redstone Block <gray>in a full 3x3 grid.",
            "",
            "<gray>Used to charge <red>Unfinished Redstone Items<gray>.",
            "<gray>Combine in crafting table with any",
            "<gray>Unfinished Redstone Tool/Weapon."
        ));

        this.addButton(new Button(40) {
            @Override public ItemStack getItem() {
                return ItemStackCreator.buildItem(Material.ARROW, "<gray>← Back", List.of());
            }
            @Override public void onClick(Player player) {
                new net.tgc.powersmp.menus.recipes.RecipeBookMenu(plugin).displayTo(player);
            }
        });
    }

    private void addDropEntry(int slot, String keyStr, List<String> info) {
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