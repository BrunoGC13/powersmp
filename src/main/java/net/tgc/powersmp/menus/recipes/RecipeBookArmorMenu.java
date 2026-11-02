package net.tgc.powersmp.menus.recipes;

import net.tgc.powersmp.Main;
import net.tgc.powersmp.menus.Button;
import net.tgc.powersmp.menus.Menu;
import net.tgc.powersmp.other.ItemStackCreator;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class RecipeBookArmorMenu extends Menu {

    public RecipeBookArmorMenu(Main plugin) {
        this.setTitle("<aqua>Armor Recipes");
        this.setSize(9 * 3);

        addInfoEntry(10, Material.DIAMOND_HELMET, "<green><bold>Kinetic Helmet", List.of(
            "<gray>Craft with <green>Hard Kinetic Shards<gray>:",
            "<gray>  · · ·",
            "<gray>  H H H",
            "<gray>  H · H",
            "<gray>H = Hard Kinetic Shard"
        ));
        addInfoEntry(11, Material.DIAMOND_CHESTPLATE, "<green><bold>Kinetic Chestplate", List.of(
            "<gray>  H · H",
            "<gray>  H H H",
            "<gray>  H H H"
        ));
        addInfoEntry(12, Material.DIAMOND_LEGGINGS, "<green><bold>Kinetic Leggings", List.of(
            "<gray>  H H H",
            "<gray>  H · H",
            "<gray>  H · H"
        ));
        addInfoEntry(13, Material.DIAMOND_BOOTS, "<green><bold>Kinetic Boots", List.of(
            "<gray>  · · ·",
            "<gray>  H · H",
            "<gray>  H · H"
        ));

        this.addButton(new Button(22) {
            @Override public ItemStack getItem() {
                return ItemStackCreator.buildItem(Material.ARROW, "<gray>← Back", List.of());
            }
            @Override public void onClick(Player player) {
                new net.tgc.powersmp.menus.recipes.RecipeBookMenu(plugin).displayTo(player);
            }
        });
    }

    private void addInfoEntry(int slot, Material mat, String name, List<String> info) {
        this.addButton(new Button(slot) {
            @Override
            public ItemStack getItem() {
                ItemStack stack = new ItemStack(mat);
                ItemMeta meta = stack.getItemMeta();
                meta.displayName(net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(name));
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