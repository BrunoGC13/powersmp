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

public class RecipeBookWeaponsMenu extends Menu {

    public RecipeBookWeaponsMenu(Main plugin) {
        this.setTitle("<red>Weapons & Tools");
        this.setSize(9 * 5);

        addRecipeEntry(10, "powersmp:unfinished_redstone_sword", List.of(
            "<gray>Craft with <red>Redstone Blocks + Sticks<gray>:",
            "<gray>  · R ·",
            "<gray>  · R ·",
            "<gray>  · S ·",
            "<gray>R = Redstone Block, S = Stick",
            "",
            "<yellow>Then combine with a <red>Redstone Battery",
            "<yellow>to get the charged version!"
        ));

        addRecipeEntry(11, "powersmp:unfinished_redstone_pickaxe", List.of(
            "<gray>Craft with <red>Redstone Blocks + Sticks<gray>:",
            "<gray>  R R R",
            "<gray>  · S ·",
            "<gray>  · S ·",
            "<gray>R = Redstone Block, S = Stick"
        ));

        addRecipeEntry(12, "powersmp:unfinished_redstone_axe", List.of(
            "<gray>Craft with <red>Redstone Blocks + Sticks<gray>:",
            "<gray>  R R ·",
            "<gray>  R S ·",
            "<gray>  · S ·"
        ));

        addRecipeEntry(13, "powersmp:unfinished_redstone_shovel", List.of(
            "<gray>Craft with <red>Redstone Blocks + Sticks<gray>:",
            "<gray>  · R ·",
            "<gray>  · S ·",
            "<gray>  · S ·"
        ));

        addRecipeEntry(14, "powersmp:unfinished_redstone_hoe", List.of(
            "<gray>Craft with <red>Redstone Blocks + Sticks<gray>:",
            "<gray>  R R ·",
            "<gray>  · S ·",
            "<gray>  · S ·"
        ));

        addRecipeEntry(19, "powersmp:coal_pickaxe", List.of(
            "<gray>Craft with <dark_gray>Coal + Sticks<gray>:",
            "<gray>  C C C",
            "<gray>  · S ·",
            "<gray>  · S ·",
            "<gray>C = Coal, S = Stick"
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