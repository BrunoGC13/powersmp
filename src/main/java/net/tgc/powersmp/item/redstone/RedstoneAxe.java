package net.tgc.powersmp.item.redstone;

import net.tgc.powersmp.Main;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class RedstoneAxe extends RedstoneWeapon {
    public RedstoneAxe(Main plugin) { super(plugin); }
    @Override public String getItemId() { return "redstone_axe"; }
    @Override public ItemStack buildCharged() {
        ItemStack item = new ItemStack(Material.NETHERITE_AXE);
        ItemMeta meta = item.getItemMeta();
        meta.setCustomModelData(2040);
        applyBase(meta, "§4Redstone Axe", MAX_ENERGY, false);
        item.setItemMeta(meta); return item;
    }
    @Override public ItemStack buildUnfinished() {
        ItemStack item = new ItemStack(Material.IRON_AXE);
        ItemMeta meta = item.getItemMeta();
        meta.setCustomModelData(2041);
        applyBase(meta, "§8Unfinished Redstone Axe", 0, true);
        item.setItemMeta(meta); return item;
    }
    // Axe: Schaden + Speed beide skalieren
    public static double getDamageBonus(int energy) {
        return 7.0 + (energy / (double) MAX_ENERGY) * 9.0;
    }
    public static double getSpeedMultiplier(int energy) {
        return 1.0 + (energy / (double) MAX_ENERGY) * 1.5;
    }
}
