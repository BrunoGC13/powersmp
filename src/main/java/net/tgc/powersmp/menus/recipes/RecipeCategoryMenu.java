package net.tgc.powersmp.menus.recipes;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.tgc.powersmp.Main;
import net.tgc.powersmp.menus.Button;
import net.tgc.powersmp.menus.Menu;
import net.tgc.powersmp.other.ItemStackCreator;
import net.tgc.powersmp.recipe.RecType;
import net.tgc.powersmp.recipe.RecipeBookRegistry;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class RecipeCategoryMenu extends Menu {

    private static final MiniMessage MM = MiniMessage.miniMessage();

    private final Main plugin;
    private final RecType type;

    public RecipeCategoryMenu(Main plugin, RecType type) {
        this.plugin = plugin;
        this.type = type;

        setTitle("<gold>✦ " + type.displayName() + " Recipes");
        setSize(9 * 6);

        addRecipes();
        addBackButton();
    }

    private void addRecipes() {
        List<RecipeBookRegistry.RegisteredRecipe> recipes =
                RecipeBookRegistry.get(type);

        for (int index = 0; index < recipes.size(); index++) {
            int slot = index;

            if (slot >= 45) {
                break;
            }

            RecipeBookRegistry.RegisteredRecipe registeredRecipe =
                    recipes.get(index);

            addButton(new Button(slot) {
                @Override
                public ItemStack getItem() {
                    ItemStack item =
                            RecipeBookRegistry.decorate(
                                    registeredRecipe.result(),
                                    registeredRecipe.ingredients()
                            );

                    ItemMeta meta = item.getItemMeta();

                    if (meta == null) {
                        return item;
                    }

                    List<Component> lore = new ArrayList<>();

                    if (meta.lore() != null) {
                        lore.addAll(meta.lore());
                    }

                    if (!lore.isEmpty()) {
                        lore.add(Component.empty());
                    }

                    lore.add(MM.deserialize(
                            "<yellow>Recipe ID: <gray>"
                                    + registeredRecipe.key().getKey()
                    ));

                    meta.lore(lore);
                    item.setItemMeta(meta);

                    return item;
                }

                @Override
                public void onClick(Player player) {
                    // Recipe entries are informational.
                }
            });
        }
    }

    private void addBackButton() {
        addButton(new Button(49) {
            @Override
            public ItemStack getItem() {
                return ItemStackCreator.buildItem(
                        Material.ARROW,
                        "<gray>← Back",
                        List.of()
                );
            }

            @Override
            public void onClick(Player player) {
                new RecipeBookMenu(plugin).displayTo(player);
            }
        });
    }
}