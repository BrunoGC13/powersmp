package net.tgc.powersmp.item.armor;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

public class Armor {
    String name;
    ItemStack helmet;
    ItemStack chestplate;
    ItemStack leggings;
    ItemStack boots;
    NamespacedKey helmetKey;
    NamespacedKey chestplateKey;
    NamespacedKey leggingsKey;
    NamespacedKey bootsKey;

    public Armor(String name,
                 NamespacedKey helmetKey, ItemStack helmet,
                 NamespacedKey chestplateKey, ItemStack chestplate,
                 NamespacedKey leggingsKey, ItemStack leggings,
                 NamespacedKey bootsKey, ItemStack boots) {
        this.name = name;
        this.helmetKey = helmetKey;
        this.helmet = helmet;
        this.chestplateKey = chestplateKey;
        this.chestplate = chestplate;
        this.leggingsKey = leggingsKey;
        this.leggings = leggings;
        this.bootsKey = bootsKey;
        this.boots = boots;
    }

    public ItemStack getHelmet()    { return helmet; }
    public ItemStack getChestplate(){ return chestplate; }
    public ItemStack getLeggings()  { return leggings; }
    public ItemStack getBoots()     { return boots; }

    public NamespacedKey getHelmetKey()     { return helmetKey; }
    public NamespacedKey getChestplateKey() { return chestplateKey; }
    public NamespacedKey getLeggingsKey()   { return leggingsKey; }
    public NamespacedKey getBootsKey()      { return bootsKey; }
}
