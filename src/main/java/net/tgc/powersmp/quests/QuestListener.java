package net.tgc.powersmp.quests;

import io.papermc.paper.event.inventory.ItemCraftedEvent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Map;

public class QuestListener implements Listener {
    private final QuestService service;

    public QuestListener(QuestService service) {
        this.service = service;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        service.initPlayer(e.getPlayer());
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        String material = e.getBlock().getType().name();

        for (Map.Entry<String, ConfigurationSection> entry : service.getActiveQuestsPerTree(player).entrySet()) {
            service.handleProgress(player, entry.getKey(), entry.getValue(), "BREAK", material);
        }
    }

    @EventHandler
    public void onItemCraft(ItemCraftedEvent e) {
        Player player = e.getPlayer();
        String material = e.getCraftedItem().getType().name();

        for (Map.Entry<String, ConfigurationSection> entry : service.getActiveQuestsPerTree(player).entrySet()) {
            service.handleProgress(player, entry.getKey(), entry.getValue(), "CRAFT", material);
        }
    }

    @EventHandler
    public void onMobKill(EntityDeathEvent e) {
        if (e.getEntity().getKiller() == null) return;
        Player player = e.getEntity().getKiller();
        String entityType = resolveMobName(e.getEntity());

        for (Map.Entry<String, ConfigurationSection> entry : service.getActiveQuestsPerTree(player).entrySet()) {
            service.handleProgress(player, entry.getKey(), entry.getValue(), "KILL", entityType);
        }
    }

    private String resolveMobName(org.bukkit.entity.Entity entity) {
        try {
            var opt = io.lumine.mythic.bukkit.MythicBukkit.inst()
                    .getMobManager().getActiveMob(entity.getUniqueId());
            if (opt.isPresent()) return opt.get().getMobType();
        } catch (Exception | NoClassDefFoundError ignored) {}
        return entity.getType().name();
    }
}