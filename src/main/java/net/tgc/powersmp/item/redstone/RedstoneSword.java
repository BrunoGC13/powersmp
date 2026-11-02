package net.tgc.powersmp.item.redstone;

import net.tgc.powersmp.Main;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public class RedstoneSword extends RedstoneWeapon {

    public RedstoneSword(Main plugin) { super(plugin); }

    @Override
    public String getItemId() { return "redstone_sword"; }

    @Override
    public ItemStack buildCharged() {
        ItemStack item = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.setCustomModelData(2010);
        applyBase(meta, "§4Redstone Sword", MAX_ENERGY, false);
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public ItemStack buildUnfinished() {
        ItemStack item = new ItemStack(Material.IRON_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.setCustomModelData(2011);
        applyBase(meta, "§8Unfinished Redstone Sword", 0, true);
        item.setItemMeta(meta);
        return item;
    }

    // Schaden basierend auf Energy berechnen: 8 bei 0 Energy, 18 bei 500
    public static double getDamageBonus(int energy) {
        return 8.0 + (energy / (double) MAX_ENERGY) * 10.0;
    }
}