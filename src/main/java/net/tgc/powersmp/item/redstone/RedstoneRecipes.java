package net.tgc.powersmp.item.redstone;

import net.kyori.adventure.key.Key;
import net.tgc.powersmp.Main;
import net.tgc.powersmp.api.item.CustomItemRegistry;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

public class RedstoneRecipes {

    public static void register(Main plugin) {

        // ── Unfinished Redstone Sword ─────────────────────────
        ShapedRecipe sword = new ShapedRecipe(
                new NamespacedKey(plugin, "unfinished_redstone_sword"),
                new RedstoneSword(plugin).buildUnfinished()
        );
        sword.shape(" R ", " R ", " S ");
        sword.setIngredient('R', Material.REDSTONE_BLOCK);
        sword.setIngredient('S', Material.STICK);
        plugin.getServer().addRecipe(sword);

        // ── Unfinished Redstone Pickaxe ───────────────────────
        ShapedRecipe pickaxe = new ShapedRecipe(
                new NamespacedKey(plugin, "unfinished_redstone_pickaxe"),
                new RedstonePickaxe(plugin).buildUnfinished()
        );
        pickaxe.shape("RRR", " S ", " S ");
        pickaxe.setIngredient('R', Material.REDSTONE_BLOCK);
        pickaxe.setIngredient('S', Material.STICK);
        plugin.getServer().addRecipe(pickaxe);

        // ── Unfinished Redstone Shovel ────────────────────────
        ShapedRecipe shovel = new ShapedRecipe(
                new NamespacedKey(plugin, "unfinished_redstone_shovel"),
                new RedstoneShovel(plugin).buildUnfinished()
        );
        shovel.shape(" R ", " S ", " S ");
        shovel.setIngredient('R', Material.REDSTONE_BLOCK);
        shovel.setIngredient('S', Material.STICK);
        plugin.getServer().addRecipe(shovel);

        // ── Unfinished Redstone Axe ───────────────────────────
        ShapedRecipe axe = new ShapedRecipe(
                new NamespacedKey(plugin, "unfinished_redstone_axe"),
                new RedstoneAxe(plugin).buildUnfinished()
        );
        axe.shape("RR ", "RS ", " S ");
        axe.setIngredient('R', Material.REDSTONE_BLOCK);
        axe.setIngredient('S', Material.STICK);
        plugin.getServer().addRecipe(axe);


        // ── Redstone Battery ──────────────────────────────────
        ShapedRecipe battery = new ShapedRecipe(
                new NamespacedKey(plugin, "redstone_battery"),
                new RedstoneBattery(plugin).build()
        );

        ItemStack primalEmber = CustomItemRegistry.getInstance().createItemStack(new NamespacedKey(plugin, "primal_ember"));
        RecipeChoice recipeChoice = new RecipeChoice.ExactChoice(primalEmber);

        battery.shape("RCR", "PNP", "RCR");
        battery.setIngredient('R', Material.REDSTONE_BLOCK);
        battery.setIngredient('C', Material.COMPARATOR);
        battery.setIngredient('N', Material.NETHERITE_INGOT);
        battery.setIngredient('P', recipeChoice);
        plugin.getServer().addRecipe(battery);
    }
}