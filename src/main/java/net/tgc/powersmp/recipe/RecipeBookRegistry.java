package net.tgc.powersmp.recipe;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class RecipeBookRegistry {

    private static final Map<RecType, List<RegisteredRecipe>> RECIPES =
            new EnumMap<>(RecType.class);

    private RecipeBookRegistry() {
    }

    public static void register(
            NamespacedKey key,
            Recipe recipe,
            ItemStack result,
            RecType type,
            List<RecipeIngredient> ingredients
    ) {
        List<RegisteredRecipe> recipes =
                RECIPES.computeIfAbsent(type, ignored -> new ArrayList<>());

        recipes.removeIf(existing -> existing.key().equals(key));

        recipes.add(new RegisteredRecipe(
                key,
                recipe,
                result.clone(),
                type,
                List.copyOf(ingredients)
        ));
    }

    public static List<RegisteredRecipe> get(RecType type) {
        return RECIPES
                .getOrDefault(type, List.of())
                .stream()
                .sorted(Comparator.comparing(recipe ->
                        recipe.key().getKey()))
                .toList();
    }

    public static Collection<RegisteredRecipe> getAll() {
        return RECIPES.values()
                .stream()
                .flatMap(Collection::stream)
                .toList();
    }

    public static ItemStack decorate(
            ItemStack original,
            List<RecipeIngredient> ingredients
    ) {
        if (original == null) {
            return null;
        }

        ItemStack result = original.clone();
        ItemMeta meta = result.getItemMeta();

        if (meta == null) {
            return result;
        }

        List<Component> lore = new ArrayList<>();

        if (meta.lore() != null) {
            lore.addAll(meta.lore());
        }

        if (!lore.isEmpty()) {
            lore.add(Component.empty());
        }

        lore.add(Component.text(
                "Recipe",
                NamedTextColor.GOLD
        ).decoration(TextDecoration.ITALIC, false));

        for (RecipeIngredient ingredient : ingredients) {
            lore.add(Component.text(
                    "• " + ingredient.amount() + "x ",
                    NamedTextColor.GRAY
            ).append(displayName(ingredient.preview())));
        }

        meta.lore(lore);
        result.setItemMeta(meta);

        return result;
    }

    private static Component displayName(ItemStack item) {
        ItemMeta meta = item.getItemMeta();

        if (meta != null && meta.hasDisplayName()) {
            return meta.displayName();
        }

        return Component.translatable(
                item.getType().translationKey(),
                NamedTextColor.WHITE
        );
    }

    public record RegisteredRecipe(
            NamespacedKey key,
            Recipe recipe,
            ItemStack result,
            RecType type,
            List<RecipeIngredient> ingredients
    ) {
    }
}