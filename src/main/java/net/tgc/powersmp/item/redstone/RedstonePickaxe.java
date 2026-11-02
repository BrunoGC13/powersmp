package net.tgc.powersmp.item.redstone;

import net.tgc.powersmp.Main;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class RedstonePickaxe extends RedstoneWeapon {

    public RedstonePickaxe(Main plugin) { super(plugin); }

    @Override
    public String getItemId() { return "redstone_pickaxe"; }

    @Override
    public ItemStack buildCharged() {
        ItemStack item = new ItemStack(Material.NETHERITE_PICKAXE);
        ItemMeta meta = item.getItemMeta();
        meta.setCustomModelData(2020);
        applyBase(meta, "§4Redstone Pickaxe", MAX_ENERGY, false);
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public ItemStack buildUnfinished() {
        ItemStack item = new ItemStack(Material.IRON_PICKAXE);
        ItemMeta meta = item.getItemMeta();
        meta.setCustomModelData(2021);
        applyBase(meta, "§8Unfinished Redstone Pickaxe", 0, true);
        item.setItemMeta(meta);
        return item;
    }

    // Mining Speed Multiplikator: 1.0x bei 0, 3.0x bei 500
    public static double getSpeedMultiplier(int energy) {
        return 1.0 + (energy / (double) MAX_ENERGY) * 2.0;
    }
}