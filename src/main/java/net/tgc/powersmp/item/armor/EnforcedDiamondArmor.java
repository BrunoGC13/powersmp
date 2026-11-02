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
public class EnforcedDiamondArmor {
    private final Armor enforcedDiamondArmor;

    public EnforcedDiamondArmor() {
        this.enforcedDiamondArmor = new Armor(
                "enforced_diamond_armor",
                new NamespacedKey("powersmp", "enforced_diamond_helmet"),     buildHelmet(),
                new NamespacedKey("powersmp", "enforced_diamond_chestplate"), buildChestplate(),
                new NamespacedKey("powersmp", "enforced_diamond_leggings"),   buildLeggings(),
                new NamespacedKey("powersmp", "enforced_diamond_boots"),      buildBoots()
        );
    }

    MiniMessage mm = MiniMessage.miniMessage();

    public ItemStack buildHelmet() {
        ItemStack helmet = new ItemStack(Material.DIAMOND_HELMET);
        helmet.addEnchantment(Enchantment.PROTECTION, 1);
        helmet.addUnsafeEnchantment(Enchantment.UNBREAKING, 6);

        ItemMeta meta = helmet.getItemMeta();
        List<Component> lore = new ArrayList<>();
        lore.add(Component.empty());
        lore.add(Component.text(FontIcon.LEGENDARY.get() + " " + FontIcon.ARMOR.get(), NamedTextColor.WHITE)
                .decoration(TextDecoration.ITALIC, false));
        meta.lore(lore);
        helmet.setItemMeta(meta);

        Component parsed = mm.deserialize("<gold><bold>Enforced Diamond Helmet");
        helmet.setData(DataComponentTypes.ITEM_NAME, parsed);
        return helmet;
    }

    public ItemStack buildChestplate() {
        ItemStack chestplate = new ItemStack(Material.DIAMOND_CHESTPLATE);
        chestplate.addEnchantment(Enchantment.PROTECTION, 1);
        chestplate.addUnsafeEnchantment(Enchantment.UNBREAKING, 6);

        ItemMeta meta = chestplate.getItemMeta();
        List<Component> lore = new ArrayList<>();
        lore.add(Component.empty());
        lore.add(Component.text(FontIcon.LEGENDARY.get() + " " + FontIcon.ARMOR.get(), NamedTextColor.WHITE)
                .decoration(TextDecoration.ITALIC, false));
        meta.lore(lore);
        chestplate.setItemMeta(meta);

        Component parsed = mm.deserialize("<gold><bold>Enforced Diamond Chestplate");
        chestplate.setData(DataComponentTypes.ITEM_NAME, parsed);
        return chestplate;
    }

    public ItemStack buildLeggings() {
        ItemStack leggings = new ItemStack(Material.DIAMOND_LEGGINGS);
        leggings.addEnchantment(Enchantment.PROTECTION, 1);
        leggings.addUnsafeEnchantment(Enchantment.UNBREAKING, 6);

        ItemMeta meta = leggings.getItemMeta();
        List<Component> lore = new ArrayList<>();
        lore.add(Component.empty());
        lore.add(Component.text(FontIcon.LEGENDARY.get() + " " + FontIcon.ARMOR.get(), NamedTextColor.WHITE)
                .decoration(TextDecoration.ITALIC, false));
        meta.lore(lore);
        leggings.setItemMeta(meta);

        Component parsed = mm.deserialize("<gold><bold>Enforced Diamond Leggings");
        leggings.setData(DataComponentTypes.ITEM_NAME, parsed);
        return leggings;
    }

    public ItemStack buildBoots() {
        ItemStack boots = new ItemStack(Material.DIAMOND_BOOTS);
        boots.addEnchantment(Enchantment.PROTECTION, 1);
        boots.addUnsafeEnchantment(Enchantment.UNBREAKING, 6);

        ItemMeta meta = boots.getItemMeta();
        List<Component> lore = new ArrayList<>();
        lore.add(Component.empty());
        lore.add(Component.text(FontIcon.LEGENDARY.get() + " " + FontIcon.ARMOR.get(), NamedTextColor.WHITE)
                .decoration(TextDecoration.ITALIC, false));
        meta.lore(lore);
        boots.setItemMeta(meta);

        Component parsed = mm.deserialize("<gold><bold>Enforced Diamond Boots");
        boots.setData(DataComponentTypes.ITEM_NAME, parsed);
        return boots;
    }

    public Armor getEnforcedDiamondArmor() {
        return enforcedDiamondArmor;
    }
}