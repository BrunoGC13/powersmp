package net.tgc.powersmp.item.redstone;

import net.tgc.powersmp.Main;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class RedstoneBattery {

    public static final NamespacedKey BATTERY_KEY = new NamespacedKey("powersmp", "redstone_battery");
    private final Main plugin;

    public RedstoneBattery(Main plugin) {
        this.plugin = plugin;
    }

    public ItemStack build() {
        ItemStack item = new ItemStack(Material.REDSTONE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§4Redstone Battery");
        meta.setLore(List.of(
                "§8A concentrated redstone energy cell.",
                "",
                "§7Combine with an §cUnfinished Redstone Item",
                "§7in a crafting table to charge it.",
                "",
                "§cCharges to: §4500 Energy"
        ));
        meta.setCustomModelData(2001); // dein CustomModelData anpassen
        meta.getPersistentDataContainer().set(BATTERY_KEY, PersistentDataType.BYTE, (byte) 1);
        item.setItemMeta(meta);
        return item;
    }

    public static boolean isBattery(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer()
                .has(BATTERY_KEY, PersistentDataType.BYTE);
    }
}