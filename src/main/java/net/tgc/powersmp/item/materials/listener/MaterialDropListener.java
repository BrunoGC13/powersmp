package net.tgc.powersmp.item.materials.listener;

import net.tgc.powersmp.api.item.CustomItemRegistry;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.concurrent.ThreadLocalRandom;

public class MaterialDropListener implements Listener {

    private final NamespacedKey kineticShardKey;
    private final NamespacedKey resoniteCrystalFragmentKey;
    private final NamespacedKey enforcementShardKey;
    private final NamespacedKey rubyKey;
    private final NamespacedKey primalEmberKey;

    public MaterialDropListener(Plugin plugin) {
        this.kineticShardKey =
                new NamespacedKey(plugin, "kinetic_shard");

        this.resoniteCrystalFragmentKey =
                new NamespacedKey(plugin, "resonite_crystal_fragment");

        this.enforcementShardKey =
                new NamespacedKey(plugin, "enforcement_shard");

        this.rubyKey =
                new NamespacedKey(plugin, "ruby");

        this.primalEmberKey =
                new NamespacedKey(plugin, "primal_ember");
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) {
            return;
        }

        PlayerData data = inspect(event);

        dropKineticShard(event, data);
        dropResoniteFragment(event, data);
        dropEnforcementShard(event, data);
        dropRuby(event, data);
        dropPrimalEmber(event, data);
    }

    private PlayerData inspect(BlockBreakEvent event) {
        ItemStack tool =
                event.getPlayer()
                        .getInventory()
                        .getItemInMainHand();

        return new PlayerData(
                event.getBlock().getType(),
                tool.getType(),
                tool.getEnchantmentLevel(Enchantment.FORTUNE),
                tool.containsEnchantment(Enchantment.SILK_TOUCH)
        );
    }

    private void dropKineticShard(
            BlockBreakEvent event,
            PlayerData data
    ) {
        if (!isEmeraldOre(data.blockType())) {
            return;
        }

        if (!isDiamondOrNetheritePickaxe(data.toolType())) {
            return;
        }

        /*
         * Fortune III is intentionally required.
         * Fortune I should not immediately unlock the Kinetic economy.
         */
        if (data.fortuneLevel() < 3) {
            return;
        }

        if (roll(0.25)) {
            drop(event, kineticShardKey);
        }
    }

    private void dropResoniteFragment(
            BlockBreakEvent event,
            PlayerData data
    ) {
        if (!isResoniteBlock(data.blockType())) {
            return;
        }

        if (!isPickaxe(data.toolType())) {
            return;
        }

        if (data.fortuneLevel() < 1) {
            return;
        }

        if (roll(0.20)) {
            drop(event, resoniteCrystalFragmentKey);
        }
    }

    private void dropEnforcementShard(
            BlockBreakEvent event,
            PlayerData data
    ) {
        if (!isObsidian(data.blockType())) {
            return;
        }

        /*
         * Requiring an iron pickaxe and Silk Touch makes this a deliberate
         * Stronghold progression activity instead of a random early drop.
         */
        if (data.toolType() != Material.IRON_PICKAXE) {
            return;
        }

        if (!data.silkTouch()) {
            return;
        }

        if (roll(0.20)) {
            drop(event, enforcementShardKey);
        }
    }

    private void dropRuby(
            BlockBreakEvent event,
            PlayerData data
    ) {
        if (!isRubyBlock(data.blockType())) {
            return;
        }

        if (!isPickaxe(data.toolType())) {
            return;
        }

        if (roll(0.30)) {
            drop(event, rubyKey);
        }
    }

    private void dropPrimalEmber(
            BlockBreakEvent event,
            PlayerData data
    ) {
        if (data.blockType() != Material.ANCIENT_DEBRIS) {
            return;
        }

        if (data.toolType() != Material.NETHERITE_PICKAXE) {
            return;
        }

        if (data.fortuneLevel() < 3) {
            return;
        }

        if (roll(0.20)) {
            drop(event, primalEmberKey);
        }
    }

    private boolean isEmeraldOre(Material material) {
        return material == Material.EMERALD_ORE
                || material == Material.DEEPSLATE_EMERALD_ORE;
    }

    private boolean isResoniteBlock(Material material) {
        return material == Material.AMETHYST_BLOCK
                || material == Material.BUDDING_AMETHYST
                || material == Material.AMETHYST_CLUSTER;
    }

    private boolean isObsidian(Material material) {
        return material == Material.OBSIDIAN
                || material == Material.CRYING_OBSIDIAN;
    }

    private boolean isRubyBlock(Material material) {
        return material == Material.OXIDIZED_COPPER
                || material == Material.OXIDIZED_CUT_COPPER;
    }

    private boolean isDiamondOrNetheritePickaxe(Material material) {
        return material == Material.DIAMOND_PICKAXE
                || material == Material.NETHERITE_PICKAXE;
    }

    private boolean isPickaxe(Material material) {
        return material.name().endsWith("_PICKAXE");
    }

    private void drop(
            BlockBreakEvent event,
            NamespacedKey key
    ) {
        ItemStack item =
                CustomItemRegistry.getInstance()
                        .createItemStack(key);

        if (item == null) {
            return;
        }

        event.getPlayer()
                .getInventory()
                .addItem(item)
                .forEach((slot, leftover) ->
                        event.getBlock()
                                .getWorld()
                                .dropItemNaturally(
                                        event.getBlock().getLocation(),
                                        leftover
                                )
                );
    }

    private boolean roll(double chance) {
        return ThreadLocalRandom.current().nextDouble() < chance;
    }

    private record PlayerData(
            Material blockType,
            Material toolType,
            int fortuneLevel,
            boolean silkTouch
    ) {
    }
}