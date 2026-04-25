package net.tgc.powersmp.api.recipe;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Recipe;

public class RecipeRegistry {
    /**
     * Registers any Bukkit Recipe (ShapedRecipe, ShapelessRecipe, SmithingRecipe, etc.).
     *
     * Example:
     * <pre>
     * NamespacedKey key = new NamespacedKey(plugin, "my_sword");
     * ShapedRecipe recipe = new ShapedRecipe(key, PowerSMPAPI.createItem(key));
     * recipe.shape("E", "E", "S");
     * recipe.setIngredient('E', Material.EMERALD);
     * recipe.setIngredient('S', Material.STICK);
     * PowerSMPAPI.registerRecipe(recipe);
     * </pre>
     */
    public static void register(Recipe recipe) {
        Bukkit.addRecipe(recipe);
    }
}
