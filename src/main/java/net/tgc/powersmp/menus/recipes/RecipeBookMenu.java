package net.tgc.powersmp.menus.recipes;

import net.tgc.powersmp.Main;
import net.tgc.powersmp.menus.Button;
import net.tgc.powersmp.menus.Menu;
import net.tgc.powersmp.other.ItemStackCreator;
import net.tgc.powersmp.recipe.RecType;
import net.tgc.powersmp.recipe.RecipeBookRegistry;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class RecipeBookMenu extends Menu {

    private final Main plugin;

    public RecipeBookMenu(Main plugin) {
        this.plugin = plugin;

        setTitle("<gold>✦ Recipe Book");
        setSize(9 * 4);

        addCategory(
                10,
                RecType.WEAPON,
                Material.DIAMOND_SWORD,
                "<red>Weapons & Tools"
        );

        addCategory(
                12,
                RecType.ARMOR,
                Material.DIAMOND_CHESTPLATE,
                "<aqua>Armor"
        );

        addCategory(
                14,
                RecType.MATERIAL,
                Material.AMETHYST_SHARD,
                "<light_purple>Materials"
        );
    }

    private void addCategory(
            int slot,
            RecType type,
            Material icon,
            String title
    ) {
        addButton(new Button(slot) {
            @Override
            public ItemStack getItem() {
                int count = RecipeBookRegistry.get(type).size();

                return ItemStackCreator.buildItem(
                        icon,
                        title,
                        List.of(
                                "",
                                "<gray>Recipes: <white>" + count,
                                "",
                                "<yellow>Click to view"
                        )
                );
            }

            @Override
            public void onClick(Player player) {
                new RecipeCategoryMenu(plugin, type).displayTo(player);
            }
        });
    }
}