package net.tgc.powersmp.listener;

import net.tgc.powersmp.Main;
import net.tgc.powersmp.api.item.CustomItem;
import net.tgc.powersmp.api.item.CustomItemRegistry;
import net.tgc.powersmp.item.tools.PrimalEmberPickaxe;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class PrimalEmberPickaxeListener implements Listener {
    private Main plugin;
    public PrimalEmberPickaxeListener(Main plugin) {
        this.plugin = plugin;
    }

    private boolean internalBreak = false;

    private enum MiningPlane {
        XY,
        XZ,
        YZ
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {

        if (internalBreak) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        CustomItem customItem = CustomItemRegistry.getInstance()
                .fromItemStack(item)
                .orElse(null);

        if (!(customItem instanceof PrimalEmberPickaxe)) {
            return;
        }

        Block center = event.getBlock();

        internalBreak = true;

        try {

            for (Block block : get3x3Blocks(center, player)) {

                if (block.getType().isAir()) {
                    continue;
                }

                BlockBreakEvent breakEvent =
                        new BlockBreakEvent(block, player);

                Bukkit.getPluginManager().callEvent(breakEvent);

                if (!breakEvent.isCancelled()) {
                    block.setType(Material.AIR);
                }
            }

        } finally {
            internalBreak = false;
        }
    }

    private List<Block> get3x3Blocks(Block center, Player player) {

        List<Block> blocks = new ArrayList<>();

        MiningPlane plane = getMiningPlane(player);

        for (int a = -1; a <= 1; a++) {

            for (int b = -1; b <= 1; b++) {

                if (a == 0 && b == 0) {
                    continue;
                }

                Block block;

                switch (plane) {

                    case XY ->
                            block = center.getRelative(a, b, 0);

                    case XZ ->
                            block = center.getRelative(a, 0, b);

                    case YZ ->
                            block = center.getRelative(0, a, b);

                    default -> {
                        continue;
                    }
                }

                blocks.add(block);
            }
        }

        return blocks;
    }

    private MiningPlane getMiningPlane(Player player) {

        Vector direction = player.getLocation().getDirection();

        double x = Math.abs(direction.getX());
        double y = Math.abs(direction.getY());
        double z = Math.abs(direction.getZ());

        if (y > x && y > z) {
            return MiningPlane.XZ;
        }

        if (x > z) {
            return MiningPlane.YZ;
        }

        return MiningPlane.XY;
    }
}