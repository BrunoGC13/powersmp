package net.tgc.powersmp.listener;

import net.tgc.powersmp.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {
    private final Main plugin;

    public BlockBreakListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();

        String dungeonId = plugin.getDungeonAreaManager().getDungeonIdForPlayer(player);
        if (dungeonId == null) return;

        String materialId = e.getBlock().getType().name().toLowerCase();

        plugin.getDungeonPlayerData().incrementMined(player, materialId, 1);
        plugin.getDungeonAreaManager().checkUnlock(player, dungeonId);
    }
}
