package net.tgc.powersmp.item.materials;

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

/**
 * Drops custom material items when players mine specific blocks with the right tool.
 *
 * Drop table:
 *   Kinetic Shard   — Emerald Ore (normal or deepslate) + Diamond/Netherite Pickaxe Fortune III → 50%
 *   Void Fragment   — End Stone + Netherite Pickaxe Silk Touch                                  → 20%
 *   Primal Ember    — Ancient Debris + Netherite Pickaxe Fortune III                            → 30%
 *
 * Drops are ADDITIONAL to normal vanilla drops (nothing is replaced or cancelled).
 */
public class MaterialDropListener implements Listener {

    private final NamespacedKey kineticShardKey;
    private final NamespacedKey voidFragmentKey;
    private final NamespacedKey primalEmberKey;

    public MaterialDropListener(Plugin plugin) {
        this.kineticShardKey = new NamespacedKey(plugin, "kinetic_shard");
        this.voidFragmentKey = new NamespacedKey(plugin, "void_fragment");
        this.primalEmberKey  = new NamespacedKey(plugin, "primal_ember");
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) return;

        Material blockType = event.getBlock().getType();
        ItemStack tool     = event.getPlayer().getInventory().getItemInMainHand();
        Material toolType  = tool.getType();

        // ── Kinetic Shard ──────────────────────────────────────────────────────
        // Emerald Ore (normal or deepslate) + Diamond or Netherite pickaxe + Fortune III
        if ((blockType == Material.EMERALD_ORE || blockType == Material.DEEPSLATE_EMERALD_ORE)
                && (toolType == Material.DIAMOND_PICKAXE || toolType == Material.NETHERITE_PICKAXE)
                && tool.getEnchantmentLevel(Enchantment.FORTUNE) >= 3
                && roll(0.50)) {
            drop(event, kineticShardKey);
        }

        // ── Void Fragment ──────────────────────────────────────────────────────
        // End Stone + Netherite Pickaxe + Silk Touch (20% chance)
        if (blockType == Material.END_STONE
                && toolType == Material.NETHERITE_PICKAXE
                && tool.containsEnchantment(Enchantment.SILK_TOUCH)
                && roll(0.20)) {
            drop(event, voidFragmentKey);
        }

        // ── Primal Ember ───────────────────────────────────────────────────────
        // Ancient Debris + Netherite Pickaxe + Fortune III (30% chance)
        if (blockType == Material.ANCIENT_DEBRIS
                && toolType == Material.NETHERITE_PICKAXE
                && tool.getEnchantmentLevel(Enchantment.FORTUNE) >= 3
                && roll(0.30)) {
            drop(event, primalEmberKey);
        }
    }

    // ──────────────────────────────────────────────────────────────────────────

    private void drop(BlockBreakEvent event, NamespacedKey key) {
        ItemStack item = CustomItemRegistry.getInstance().createItemStack(key);
        if (item != null) {
            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), item);
        }
    }

    private boolean roll(double chance) {
        return ThreadLocalRandom.current().nextDouble() < chance;
    }
}
