package net.tgc.powersmp.item;

import net.tgc.powersmp.Main;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class NukeShotItem {
    private Main plugin;
    public NukeShotItem(Main plugin) {
        this.plugin = plugin;
    }

    public ItemStack getStabShot(String type, double x, double y, double z) {
        Material material = type.equals("chunkeater") ? Material.ARMOR_STAND : Material.FISHING_ROD;
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        String var10000;
        switch (type) {
            case "nuke" -> var10000 = "Nuke shot";
            default -> var10000 = "Orbital Strike Rod";
        }

        String displayName = var10000;

        assert meta != null;

        meta.setDisplayName(displayName);
        meta.setCustomModelData(12345);
        if (type.equals("stasis")) {
            meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "stasis_x"), PersistentDataType.DOUBLE, x);
            meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "stasis_y"), PersistentDataType.DOUBLE, y);
            meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "stasis_z"), PersistentDataType.DOUBLE, z);
        }

        item.setItemMeta(meta);
        if (material == Material.FISHING_ROD) {
            item.setDurability((short)63);
        }

        return item;
    }
}
