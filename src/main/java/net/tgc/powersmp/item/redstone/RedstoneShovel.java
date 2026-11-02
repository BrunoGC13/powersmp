package net.tgc.powersmp.item.redstone;

import net.tgc.powersmp.Main;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class RedstoneShovel extends RedstoneWeapon {
    public RedstoneShovel(Main plugin) { super(plugin); }
    @Override public String getItemId() { return "redstone_shovel"; }
    @Override public ItemStack buildCharged() {
        ItemStack item = new ItemStack(Material.NETHERITE_SHOVEL);
        ItemMeta meta = item.getItemMeta();
        meta.setCustomModelData(2030);
        applyBase(meta, "§4Redstone Shovel", MAX_ENERGY, false);
        item.setItemMeta(meta); return item;
    }
    @Override public ItemStack buildUnfinished() {
        ItemStack item = new ItemStack(Material.IRON_SHOVEL);
        ItemMeta meta = item.getItemMeta();
        meta.setCustomModelData(2031);
        applyBase(meta, "§8Unfinished Redstone Shovel", 0, true);
        item.setItemMeta(meta); return item;
    }
    public static double getSpeedMultiplier(int energy) {
        return 1.0 + (energy / (double) MAX_ENERGY) * 2.0;
    }
}
