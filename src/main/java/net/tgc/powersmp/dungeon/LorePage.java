package net.tgc.powersmp.dungeon;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class LorePage {
    private String name;
    private String itemModel;
    private Material mat;

    public LorePage(String name, String itemModel, Material mat) {
        this.name = name;
        this.itemModel = itemModel;
        this.mat = mat;
    }
}
