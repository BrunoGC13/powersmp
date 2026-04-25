package net.tgc.powersmp.api;

import net.tgc.powersmp.api.ability.Ability;
import net.tgc.powersmp.api.ability.AbilityRegistry;
import net.tgc.powersmp.api.command.CommandRegistry;
import net.tgc.powersmp.api.item.CustomItem;
import net.tgc.powersmp.api.item.CustomItemRegistry;
import net.tgc.powersmp.api.recipe.RecipeRegistry;
import net.tgc.powersmp.player.HeartManager;
import net.tgc.powersmp.player.PowerManager;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

/**
 * Main entry point for the PowerSMP API.
 *
 * Usage from another plugin:
 * <pre>
 *   // Register a custom item
 *   PowerSMPAPI.registerItem(new MySword(this));
 *
 *   // Register an ability
 *   PowerSMPAPI.registerAbility(new FireballAbility());
 *
 *   // Append an ability to a vanilla item
 *   ItemStack sword = new ItemStack(Material.IRON_SWORD);
 *   sword = PowerSMPAPI.appendAbility(sword, new FireballAbility());
 *
 *   // Register a command
 *   PowerSMPAPI.registerCommand(this, "mycmd", myExecutor, myTabCompleter);
 *
 *   // Power / hearts
 *   PowerSMPAPI.consumePower(player, 10);
 *   PowerSMPAPI.addHearts(player, 1);
 * </pre>
 */
public final class PowerSMPAPI {
    private PowerSMPAPI() {}

    // -------------------------------------------------------------------------
    // Items
    // -------------------------------------------------------------------------

    public static void registerItem(CustomItem item) {
        CustomItemRegistry.getInstance().register(item);
    }

    /** Creates a ready-to-use ItemStack with PDC tags applied. */
    public static ItemStack createItem(NamespacedKey key) {
        return CustomItemRegistry.getInstance().createItemStack(key);
    }

    /** Appends an ability to any ItemStack (custom or vanilla). Returns modified copy. */
    public static ItemStack appendAbility(ItemStack stack, Ability ability) {
        return CustomItemRegistry.getInstance().appendAbility(stack, ability);
    }

    // -------------------------------------------------------------------------
    // Recipes
    // -------------------------------------------------------------------------

    /** Registers any Bukkit Recipe (ShapedRecipe, ShapelessRecipe, SmithingRecipe, …). */
    public static void registerRecipe(Recipe recipe) {
        RecipeRegistry.register(recipe);
    }

    // -------------------------------------------------------------------------
    // Abilities
    // -------------------------------------------------------------------------

    public static void registerAbility(Ability ability) {
        AbilityRegistry.getInstance().register(ability);
    }

    // -------------------------------------------------------------------------
    // Commands
    // -------------------------------------------------------------------------

    public static void registerCommand(Plugin plugin, String name, CommandExecutor executor) {
        CommandRegistry.register(plugin, name, executor);
    }

    public static void registerCommand(Plugin plugin, String name, CommandExecutor executor, TabCompleter completer) {
        CommandRegistry.register(plugin, name, executor, completer);
    }

    // -------------------------------------------------------------------------
    // Hearts
    // -------------------------------------------------------------------------

    public static int getHearts(Player player) { return HeartManager.getHearts(player); }
    public static void setHearts(Player player, int hearts) { HeartManager.setHearts(player, hearts); }
    public static void addHearts(Player player, int amount) { HeartManager.addHearts(player, amount); }
    public static void removeHearts(Player player, int amount) { HeartManager.removeHearts(player, amount); }

    // -------------------------------------------------------------------------
    // Power
    // -------------------------------------------------------------------------

    public static double getPower(Player player) { return PowerManager.getPower(player); }
    public static double getMaxPower(Player player) { return PowerManager.getMaxPower(player); }
    public static void setPower(Player player, double power) { PowerManager.setPower(player, power); }
    public static void addPower(Player player, double amount) { PowerManager.addPower(player, amount); }
    public static void removePower(Player player, double amount) { PowerManager.removePower(player, amount); }
    public static void setMaxPower(Player player, double max) { PowerManager.setMaxPower(player, max); }

    /**
     * Atomically checks and consumes power.
     *
     * @return true if the player had enough power (and it was consumed), false otherwise
     */
    public static boolean consumePower(Player player, double amount) {
        return PowerManager.consumePower(player, amount);
    }
}
