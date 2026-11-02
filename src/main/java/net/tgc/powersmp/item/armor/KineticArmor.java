package net.tgc.powersmp.item.armor;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.tgc.powersmp.other.FontIcon;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class KineticArmor {
    private final Armor kineticArmor;

    public KineticArmor() {
        // Erst hier aufrufen, wenn der Konstruktor explizit läuft
        this.kineticArmor = new Armor(
                "kinetic_armor",
                new NamespacedKey("powersmp", "kinetic_helmet"),     buildHelmet(),
                new NamespacedKey("powersmp", "kinetic_chestplate"), buildChestplate(),
                new NamespacedKey("powersmp", "kinetic_leggings"),   buildLeggings(),
                new NamespacedKey("powersmp", "kinetic_boots"),      buildBoots()
        );
    }
    MiniMessage mm = MiniMessage.miniMessage();

    public ItemStack buildHelmet() {
        ItemStack helmet = new ItemStack(Material.DIAMOND_HELMET);
        helmet.addEnchantment(Enchantment.RESPIRATION,1);
        helmet.addEnchantment(Enchantment.PROTECTION, 3);
        helmet.addEnchantment(Enchantment.UNBREAKING, 2);

        ItemMeta meta = helmet.getItemMeta();
        List<Component> lore = new ArrayList<>();
        lore.add(Component.empty());
        lore.add(Component.text(FontIcon.EPIC.get() + " " + FontIcon.ARMOR.get(), NamedTextColor.WHITE)
                .decoration(TextDecoration.ITALIC, false));
        meta.lore(lore);
        helmet.setItemMeta(meta);

        Component parsed = mm.deserialize("<green><bold>Kinetic Helmet");
        helmet.setData(DataComponentTypes.ITEM_NAME, parsed);
        return helmet;
    }

    public ItemStack buildChestplate() {
        ItemStack helmet = new ItemStack(Material.DIAMOND_CHESTPLATE);
        helmet.addEnchantment(Enchantment.PROTECTION, 3);
        helmet.addEnchantment(Enchantment.UNBREAKING, 2);
        helmet.addEnchantment(Enchantment.THORNS, 1);

        ItemMeta meta = helmet.getItemMeta();
        List<Component> lore = new ArrayList<>();
        lore.add(Component.empty());
        lore.add(Component.text(FontIcon.EPIC.get() + " " + FontIcon.ARMOR.get(), NamedTextColor.WHITE)
                .decoration(TextDecoration.ITALIC, false));
        meta.lore(lore);
        helmet.setItemMeta(meta);

        Component parsed = mm.deserialize("<green><bold>Kinetic Chestplate");
        helmet.setData(DataComponentTypes.ITEM_NAME, parsed);
        return helmet;
    }

    public ItemStack buildLeggings() {
        ItemStack helmet = new ItemStack(Material.DIAMOND_LEGGINGS);
        helmet.addEnchantment(Enchantment.PROTECTION, 2);
        helmet.addEnchantment(Enchantment.UNBREAKING, 2);

        ItemMeta meta = helmet.getItemMeta();
        List<Component> lore = new ArrayList<>();
        lore.add(Component.empty());
        lore.add(Component.text(FontIcon.EPIC.get() + " " + FontIcon.ARMOR.get(), NamedTextColor.WHITE)
                .decoration(TextDecoration.ITALIC, false));
        meta.lore(lore);
        helmet.setItemMeta(meta);

        Component parsed = mm.deserialize("<green><bold>Kinetic Leggings");
        helmet.setData(DataComponentTypes.ITEM_NAME, parsed);
        return helmet;
    }

    public ItemStack buildBoots() {
        ItemStack helmet = new ItemStack(Material.DIAMOND_BOOTS);
        helmet.addEnchantment(Enchantment.PROTECTION, 2);
        helmet.addEnchantment(Enchantment.UNBREAKING, 2);
        helmet.addEnchantment(Enchantment.FEATHER_FALLING, 2);
        helmet.addEnchantment(Enchantment.DEPTH_STRIDER, 2);

        ItemMeta meta = helmet.getItemMeta();
        List<Component> lore = new ArrayList<>();
        lore.add(Component.empty());
        lore.add(Component.text(FontIcon.EPIC.get() + " " + FontIcon.ARMOR.get(), NamedTextColor.WHITE)
                .decoration(TextDecoration.ITALIC, false));
        meta.lore(lore);
        helmet.setItemMeta(meta);

        Component parsed = mm.deserialize("<green><bold>Kinetic Boots");
        helmet.setData(DataComponentTypes.ITEM_NAME, parsed);
        return helmet;
    }

    public Armor getKineticArmor() {
        return kineticArmor;
    }
}
