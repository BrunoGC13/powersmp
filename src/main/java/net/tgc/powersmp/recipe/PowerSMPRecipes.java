package net.tgc.powersmp.recipe;

import net.tgc.powersmp.Main;
import net.tgc.powersmp.api.PowerSMPAPI;
import net.tgc.powersmp.api.item.CustomItem;
import net.tgc.powersmp.api.item.CustomItemRegistry;
import net.tgc.powersmp.item.HeartItem;
import net.tgc.powersmp.item.armor.EnforcedDiamondArmor;
import net.tgc.powersmp.item.armor.KineticArmor;
import net.tgc.powersmp.item.gems.EscapeGem;
import net.tgc.powersmp.item.gems.FireballItem;
import net.tgc.powersmp.item.gems.NoCooldownGem;
import net.tgc.powersmp.item.gems.PowerDiamondItem;
import net.tgc.powersmp.item.gems.PowerEmeraldItem;
import net.tgc.powersmp.item.materials.PrimalEmber;
import net.tgc.powersmp.item.materials.Ruby;
import net.tgc.powersmp.item.materials.enforce.EnforcedDiamond;
import net.tgc.powersmp.item.materials.enforce.EnforcementShard;
import net.tgc.powersmp.item.materials.kinetic.HardKineticShard;
import net.tgc.powersmp.item.materials.kinetic.KineticShard;
import net.tgc.powersmp.item.materials.resonite.ResoniteCrystal;
import net.tgc.powersmp.item.materials.resonite.ResoniteCrystalFragment;
import net.tgc.powersmp.item.redstone.RedstoneRecipes;
import net.tgc.powersmp.item.tools.CoalPickaxe;
import net.tgc.powersmp.item.tools.PrimalEmberPickaxe;
import net.tgc.powersmp.item.weapons.EmberBlade;
import net.tgc.powersmp.item.weapons.RubySpear;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public final class PowerSMPRecipes {

    private final Main plugin;

    public PowerSMPRecipes(Main plugin) {
        this.plugin = plugin;
    }

    public void registerAll() {
        try {
            registerStrongholdRecipes();
            plugin.getLogger().info(
                    "[Recipes] Stronghold recipes registered."
            );
        } catch (Exception exception) {
            plugin.getLogger().log(
                    Level.SEVERE,
                    "[Recipes] Failed to register stronghold recipes.",
                    exception
            );
        }

        try {
            registerNetherRecipes();
            plugin.getLogger().info(
                    "[Recipes] Nether recipes registered."
            );
        } catch (Exception exception) {
            plugin.getLogger().log(
                    Level.SEVERE,
                    "[Recipes] Failed to register Nether recipes.",
                    exception
            );
        }

        try {
            registerRedstoneRecipes();
            plugin.getLogger().info(
                    "[Recipes] Redstone recipes registered."
            );
        } catch (Exception exception) {
            plugin.getLogger().log(
                    Level.SEVERE,
                    "[Recipes] Failed to register redstone recipes.",
                    exception
            );
        }

        try {
            registerHeartRecipe(
                    new HeartItem(plugin),
                    new PrimalEmber(plugin)
            );

            plugin.getLogger().info(
                    "[Recipes] Heart recipe registered."
            );
        } catch (Exception exception) {
            plugin.getLogger().log(
                    Level.SEVERE,
                    "[Recipes] Failed to register heart recipe.",
                    exception
            );
        }

        plugin.getLogger().info(
                "[Recipes] Recipe book entries registered: "
                        + RecipeBookRegistry.getAll().size()
        );
    }

    private void registerStrongholdRecipes() {
        KineticShard kineticShard =
                new KineticShard(plugin);

        HardKineticShard hardKineticShard =
                new HardKineticShard(plugin);

        ResoniteCrystalFragment resoniteFragment =
                new ResoniteCrystalFragment(plugin);

        ResoniteCrystal resoniteCrystal =
                new ResoniteCrystal(plugin);

        Ruby ruby =
                new Ruby(plugin);

        EnforcementShard enforcementShard =
                new EnforcementShard(plugin);

        EnforcedDiamond enforcedDiamond =
                new EnforcedDiamond(plugin);

        PowerEmeraldItem powerEmerald =
                new PowerEmeraldItem(plugin);

        PowerDiamondItem powerDiamond =
                new PowerDiamondItem(plugin);

        CoalPickaxe coalPickaxe =
                new CoalPickaxe(plugin);

        RubySpear rubySpear =
                new RubySpear(plugin);

        registerShaped(
                "resonite_crystal",
                customResult(resoniteCrystal),
                new String[]{
                        " F ",
                        "FEF",
                        " F "
                },
                RecType.MATERIAL,
                exact(resoniteFragment),
                'F',
                Material.ECHO_SHARD,
                'E'
        );

        registerShaped(
                "power_emerald",
                customResult(powerEmerald),
                new String[]{
                        " E ",
                        "EKE",
                        " E "
                },
                RecType.MATERIAL,
                Material.EMERALD,
                'E',
                exact(kineticShard),
                'K'
        );

        registerShaped(
                "power_diamond",
                customResult(powerDiamond),
                new String[]{
                        " D ",
                        "DRD",
                        " D "
                },
                RecType.MATERIAL,
                Material.DIAMOND,
                'D',
                exact(resoniteCrystal),
                'R'
        );

        registerShaped(
                "hard_kinetic_shard",
                customResult(hardKineticShard),
                new String[]{
                        " K ",
                        "KKK",
                        " K "
                },
                RecType.MATERIAL,
                exact(kineticShard),
                'K'
        );

        registerArmorSet(
                "kinetic",
                new KineticArmor().getKineticArmor(),
                exact(hardKineticShard),
                RecType.ARMOR
        );

        registerShaped(
                "enforced_diamond",
                customResult(enforcedDiamond),
                new String[]{
                        " E ",
                        "EDE",
                        " E "
                },
                RecType.MATERIAL,
                exact(enforcementShard),
                'E',
                Material.DIAMOND,
                'D'
        );

        registerShaped(
                "enforcement_shard",
                customResult(enforcementShard),
                new String[]{
                        "H H",
                        "H H",
                        "H H"
                },
                RecType.MATERIAL,
                exact(hardKineticShard),
                'H'
        );

        registerArmorSet(
                "enforced_diamond",
                new EnforcedDiamondArmor().getEnforcedDiamondArmor(),
                exact(enforcedDiamond),
                RecType.ARMOR
        );

        registerShaped(
                "coal_pickaxe",
                customResult(coalPickaxe),
                new String[]{
                        "CCC",
                        " S ",
                        " S "
                },
                RecType.WEAPON,
                Material.COAL,
                'C',
                Material.STICK,
                'S'
        );

        registerShaped(
                "ruby_spear",
                customResult(rubySpear),
                new String[]{
                        " R ",
                        " S ",
                        " S "
                },
                RecType.WEAPON,
                exact(ruby),
                'R',
                Material.STICK,
                'S'
        );
    }

    private void registerNetherRecipes() {
        PrimalEmber primalEmber =
                new PrimalEmber(plugin);

        EmberBlade emberBlade =
                new EmberBlade(plugin);

        FireballItem fireball =
                new FireballItem(plugin);

        EscapeGem escapeGem =
                new EscapeGem(plugin);

        NoCooldownGem noCooldown =
                new NoCooldownGem(plugin);

        PrimalEmberPickaxe primalPickaxe =
                new PrimalEmberPickaxe(plugin);

        registerShaped(
                "fireball_item",
                customResult(fireball),
                new String[]{
                        "PMP",
                        "MFM",
                        "PMP"
                },
                RecType.WEAPON,
                exact(primalEmber),
                'P',
                Material.MAGMA_BLOCK,
                'M',
                Material.FIRE_CHARGE,
                'F'
        );

        registerShaped(
                "primal_ember_pickaxe",
                customResult(primalPickaxe),
                new String[]{
                        "PPP",
                        " E ",
                        " E "
                },
                RecType.WEAPON,
                exact(primalEmber),
                'P',
                Material.ECHO_SHARD,
                'E'
        );
    }

    private void registerRedstoneRecipes() {
        RedstoneRecipes.register(plugin);
    }

    private void registerArmorSet(
            String id,
            net.tgc.powersmp.item.armor.Armor armor,
            RecipeIngredient ingredient,
            RecType type
    ) {
        registerShaped(
                id + "_helmet",
                armor.getHelmet(),
                new String[]{
                        "   ",
                        "HHH",
                        "H H"
                },
                type,
                ingredient,
                'H'
        );

        registerShaped(
                id + "_chestplate",
                armor.getChestplate(),
                new String[]{
                        "H H",
                        "HHH",
                        "HHH"
                },
                type,
                ingredient,
                'H'
        );

        registerShaped(
                id + "_leggings",
                armor.getLeggings(),
                new String[]{
                        "HHH",
                        "H H",
                        "H H"
                },
                type,
                ingredient,
                'H'
        );

        registerShaped(
                id + "_boots",
                armor.getBoots(),
                new String[]{
                        "   ",
                        "H H",
                        "H H"
                },
                type,
                ingredient,
                'H'
        );
    }

    private RecipeIngredient exact(CustomItem customItem) {
        ItemStack stack =
                CustomItemRegistry.getInstance()
                        .createItemStack(customItem.getKey());

        if (stack == null) {
            throw new IllegalStateException(
                    "Custom item is not registered: "
                            + customItem.getKey()
            );
        }

        return RecipeIngredient.custom(
                new RecipeChoice.ExactChoice(stack),
                stack,
                1
        );
    }

    private ItemStack customResult(CustomItem customItem) {
        ItemStack result =
                CustomItemRegistry.getInstance()
                        .createItemStack(customItem.getKey());

        if (result == null) {
            throw new IllegalStateException(
                    "Custom recipe result is not registered: "
                            + customItem.getKey()
            );
        }

        return result;
    }

    private void registerShaped(
            String id,
            ItemStack result,
            String[] shape,
            RecType type,
            Object... ingredients
    ) {
        if (ingredients.length % 2 != 0) {
            throw new IllegalArgumentException(
                    "Recipe '" + id
                            + "' must use ingredient/character pairs."
            );
        }

        NamespacedKey key =
                new NamespacedKey(plugin, id);

        ShapedRecipe recipe =
                new ShapedRecipe(key, result);

        recipe.shape(shape);

        List<RecipeIngredient> bookIngredients =
                new ArrayList<>();

        for (int index = 0; index < ingredients.length; index += 2) {
            Object ingredient =
                    ingredients[index];

            Object characterObject =
                    ingredients[index + 1];

            if (!(characterObject instanceof Character character)) {
                throw new IllegalArgumentException(
                        "Recipe '" + id
                                + "' has an invalid ingredient character."
                );
            }

            int amount =
                    countCharacter(shape, character);

            if (amount <= 0) {
                throw new IllegalArgumentException(
                        "Character '" + character
                                + "' does not occur in recipe '"
                                + id + "'."
                );
            }

            if (ingredient instanceof Material material) {
                recipe.setIngredient(character, material);

                bookIngredients.add(
                        RecipeIngredient.material(
                                material,
                                amount
                        )
                );
            } else if (ingredient instanceof RecipeIngredient recipeIngredient) {
                recipe.setIngredient(
                        character,
                        recipeIngredient.choice()
                );

                bookIngredients.add(
                        new RecipeIngredient(
                                recipeIngredient.choice(),
                                recipeIngredient.preview(),
                                amount
                        )
                );
            } else {
                throw new IllegalArgumentException(
                        "Unsupported ingredient in recipe '"
                                + id + "': "
                                + ingredient
                );
            }
        }

        validateShapeIngredients(
                id,
                shape,
                bookIngredients,
                ingredients
        );

        PowerSMPAPI.registerRecipe(recipe);

        RecipeBookRegistry.register(
                key,
                recipe,
                result,
                type,
                bookIngredients
        );
    }

    private void validateShapeIngredients(
            String id,
            String[] shape,
            List<RecipeIngredient> bookIngredients,
            Object[] ingredients
    ) {
        List<Character> registeredCharacters =
                new ArrayList<>();

        for (int index = 1; index < ingredients.length; index += 2) {
            registeredCharacters.add(
                    (Character) ingredients[index]
            );
        }

        for (String row : shape) {
            for (char character : row.toCharArray()) {
                if (character == ' ') {
                    continue;
                }

                if (!registeredCharacters.contains(character)) {
                    throw new IllegalArgumentException(
                            "Recipe '" + id
                                    + "' contains '"
                                    + character
                                    + "' in its shape, but no ingredient "
                                    + "was registered for it."
                    );
                }
            }
        }

        if (bookIngredients.isEmpty()) {
            throw new IllegalArgumentException(
                    "Recipe '" + id
                            + "' has no registered ingredients."
            );
        }
    }

    private int countCharacter(
            String[] shape,
            char character
    ) {
        int count = 0;

        for (String row : shape) {
            for (char current : row.toCharArray()) {
                if (current == character) {
                    count++;
                }
            }
        }

        return count;
    }

    private void registerHeartRecipe(
            HeartItem heart,
            PrimalEmber primalEmber
    ) {
        registerShaped(
                "heart_item",
                customResult(heart),
                new String[]{
                        "PRP",
                        "RTR",
                        "PRP"
                },
                RecType.MATERIAL,
                exact(primalEmber),
                'P',
                exact(new ResoniteCrystal(plugin)),
                'R',
                Material.TOTEM_OF_UNDYING,
                'T'
        );
    }
}