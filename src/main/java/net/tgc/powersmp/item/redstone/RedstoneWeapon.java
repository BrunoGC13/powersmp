package net.tgc.powersmp.item.redstone;

import net.tgc.powersmp.Main;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public abstract class RedstoneWeapon {

    protected final Main plugin;
    public static final int MAX_ENERGY = 500;
    public static final NamespacedKey ENERGY_KEY = new NamespacedKey("powersmp", "redstone_energy");
    public static final NamespacedKey ITEM_ID_KEY = new NamespacedKey("powersmp", "redstone_item_id");

    public RedstoneWeapon(Main plugin) {
        this.plugin = plugin;
    }

    // Jede Subklasse gibt ihre ID zurück z.B. "redstone_sword"
    public abstract String getItemId();

    // Fertiges Item (mit Battery kombiniert)
    public abstract ItemStack buildCharged();

    // Unfinished Variante
    public abstract ItemStack buildUnfinished();

    // Lore mit Energy-Anzeige
    protected List<String> buildLore(int energy) {
        List<String> lore = new ArrayList<>();
        lore.add("§8Redstone-infused equipment");
        lore.add("");
        lore.add(energyBar(energy));
        lore.add("§7Energy: §c" + energy + "§7/§c" + MAX_ENERGY);
        lore.add("");
        lore.add("§7Energy drains on use.");
        lore.add("§7Higher energy = better performance.");
        return lore;
    }

    protected List<String> buildUnfinishedLore() {
        List<String> lore = new ArrayList<>();
        lore.add("§8Redstone-infused equipment");
        lore.add("");
        lore.add("§cNeeds a §4Redstone Battery §cto be charged!");
        lore.add("§7Combine in a crafting table.");
        return lore;
    }

    // Energie aus Item lesen
    public static int getEnergy(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return 0;
        return item.getItemMeta().getPersistentDataContainer()
                .getOrDefault(ENERGY_KEY, PersistentDataType.INTEGER, 0);
    }

    // Energie setzen + Lore updaten
    public static void setEnergy(ItemStack item, int energy, String displayName) {
        if (item == null || !item.hasItemMeta()) return;
        int clamped = Math.max(0, Math.min(MAX_ENERGY, energy));
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(ENERGY_KEY, PersistentDataType.INTEGER, clamped);

        List<String> lore = new ArrayList<>();
        lore.add("§8Redstone-infused equipment");
        lore.add("");
        lore.add(energyBar(clamped));
        lore.add("§7Energy: §c" + clamped + "§7/§c" + MAX_ENERGY);
        lore.add("");
        lore.add("§7Energy drains on use.");
        lore.add("§7Higher energy = better performance.");
        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    // Prüft ob ein Item ein geladenes Redstone-Item ist
    public static boolean isChargedRedstoneItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        String id = item.getItemMeta().getPersistentDataContainer()
                .get(ITEM_ID_KEY, PersistentDataType.STRING);
        return id != null && !id.startsWith("unfinished_");
    }

    public static boolean isUnfinishedRedstoneItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        String id = item.getItemMeta().getPersistentDataContainer()
                .get(ITEM_ID_KEY, PersistentDataType.STRING);
        return id != null && id.startsWith("unfinished_");
    }

    public static String getItemId(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;
        return item.getItemMeta().getPersistentDataContainer()
                .get(ITEM_ID_KEY, PersistentDataType.STRING);
    }

    private static String energyBar(int energy) {
        int filled = (int) ((energy / (double) MAX_ENERGY) * 20);
        StringBuilder bar = new StringBuilder("§c[");
        for (int i = 0; i < 20; i++) {
            bar.append(i < filled ? "§4|" : "§8|");
        }
        bar.append("§c]");
        return bar.toString();
    }

    protected void applyBase(ItemMeta meta, String displayName, int energy, boolean unfinished) {
        meta.setDisplayName(displayName);
        meta.setLore(unfinished ? buildUnfinishedLore() : buildLore(energy));
        meta.getPersistentDataContainer().set(
                ITEM_ID_KEY, PersistentDataType.STRING,
                unfinished ? "unfinished_" + getItemId() : getItemId()
        );
        if (!unfinished) {
            meta.getPersistentDataContainer().set(ENERGY_KEY, PersistentDataType.INTEGER, energy);
        }
    }
}