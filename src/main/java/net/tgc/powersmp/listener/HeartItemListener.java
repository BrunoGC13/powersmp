package net.tgc.powersmp.listener;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.tgc.powersmp.Main;
import net.tgc.powersmp.player.PlayerDataManager;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class HeartItemListener implements Listener {

    private final Main plugin;
    private final String prefix;

    public HeartItemListener(Main plugin) {
        this.plugin = plugin;
        this.prefix = plugin.getConfig().getString("plugin-prefix");
    }

    @EventHandler
    public void onUse(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_AIR
            && e.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Player player = e.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || !item.hasItemMeta()) return;

        // Custom Item ID prüfen
        String id = item.getItemMeta().getPersistentDataContainer()
            .get(new NamespacedKey(plugin, "custom_item_id"), PersistentDataType.STRING);
        if (!"powersmp:heart_item".equals(id)) return;

        e.setCancelled(true);

        PlayerDataManager pdm = PlayerDataManager.getInstance();
        int newHearts = pdm.getHearts(player) + 1;
        if (pdm.applyNewHeartsExceedsLimit(player, newHearts)) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(prefix + "<red>You have the maximum amount of hearts a player can have!"));
            return;
        }

        // Max HP erhöhen
        pdm.setHearts(player, newHearts);

        // Item verbrauchen
        if (item.getAmount() > 1) {
            item.setAmount(item.getAmount() - 1);
        } else {
            player.getInventory().setItemInMainHand(null);
        }

        // Feedback
        player.sendMessage(net.kyori.adventure.text.minimessage.MiniMessage.miniMessage()
            .deserialize("<red>❤ +1 Heart! <gray>You now have <red>" + newHearts + " ❤ hearts<gray>."));
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1.5f);
        player.spawnParticle(org.bukkit.Particle.HEART,
            player.getLocation().add(0, 2, 0), 10, 0.5, 0.5, 0.5, 0.1);
    }
}