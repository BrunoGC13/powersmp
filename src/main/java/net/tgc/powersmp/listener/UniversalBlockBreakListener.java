package net.tgc.powersmp.listener;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import net.tgc.powersmp.Main;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.HashMap;

public class UniversalBlockBreakListener implements Listener {
    private Main plugin;
    public UniversalBlockBreakListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {

        Player player = e.getPlayer();
        Block block = e.getBlock();

        if (player.getGameMode() == GameMode.CREATIVE) return;

        Collection<ItemStack> drops =
                block.getDrops(player.getInventory().getItemInMainHand());

        e.setDropItems(false);

        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        Location loc = BukkitAdapter.adapt(block.getLocation());
        RegionContainer regionContainer =
                WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = regionContainer.createQuery();

        if (!query.testState(loc, localPlayer, Flags.BUILD)) {
            return;
        }

        for (ItemStack drop : drops) {
            HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(drop);

            leftover.values().forEach(item ->
                    player.getWorld().dropItemNaturally(block.getLocation(), item));
        }
    }
}
