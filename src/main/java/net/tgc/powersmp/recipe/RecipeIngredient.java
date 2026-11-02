package net.tgc.powersmp.recipe;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

public record RecipeIngredient(
        RecipeChoice choice,
        ItemStack preview,
        int amount
) {
    public static RecipeIngredient material(Material material, int amount) {
        return new RecipeIngredient(
                new RecipeChoice.MaterialChoice(material),
                new ItemStack(material),
                amount
        );
    }

    public static RecipeIngredient custom(
            RecipeChoice choice,
            ItemStack preview,
            int amount
    ) {
        return new RecipeIngredient(
                choice,
                preview.clone(),
                amount
        );
    }
}