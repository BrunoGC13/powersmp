package net.tgc.powersmp.listener;

import net.tgc.powersmp.Main;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class AnvilListener implements Listener {
    private final Main plugin;

    public AnvilListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onAnvil(PrepareAnvilEvent event) {
        ItemStack left = event.getInventory().getItem(0);
        ItemStack right = event.getInventory().getItem(1);

        if (left == null || right == null) return;

        ItemStack result = left.clone();
        Map<Enchantment, Integer> resultEnchants = new HashMap<>(left.getEnchantments());

        for (Enchantment ench : right.getEnchantments().keySet()) {

            // ❗ Nur bearbeiten, wenn in Config
            if (!plugin.overrideEnchants.containsKey(ench)) continue;

            int maxLevel = plugin.overrideEnchants.get(ench);

            int leftLevel = resultEnchants.getOrDefault(ench, 0);
            int rightLevel = right.getEnchantments().get(ench);

            int newLevel;

            if (leftLevel == rightLevel) {
                newLevel = leftLevel + 1;
            } else {
                newLevel = Math.max(leftLevel, rightLevel);
            }

            if (newLevel > maxLevel) {
                newLevel = maxLevel;
            }

            result.addUnsafeEnchantment(ench, newLevel);
        }

        event.setResult(result);
    }
}
