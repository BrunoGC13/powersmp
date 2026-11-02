package net.tgc.powersmp.item.weapons;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.tgc.powersmp.Main;
import net.tgc.powersmp.ability.DiamondDashAbility;
import net.tgc.powersmp.api.ability.AbilityLoreHelper;
import net.tgc.powersmp.api.item.CustomItem;
import net.tgc.powersmp.other.FontIcon;
import net.tgc.powersmp.other.Helper;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class AspectOfTheEnd extends CustomItem {
    private final Main plugin;

    public AspectOfTheEnd(Main plugin) {
        super(new NamespacedKey(plugin, "aspect_of_the_end"), List.of(new DiamondDashAbility()));
        this.plugin = plugin;
    }

    @Override
    public ItemStack buildItem() {
        Helper helper = new Helper();

        ItemStack stack = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = stack.getItemMeta();

        meta.displayName(Component.text("⛤ Aspect Of The End ⛤", NamedTextColor.YELLOW)
                .decoration(TextDecoration.ITALIC, false)
                .decoration(TextDecoration.BOLD, true));

        meta.addEnchant(Enchantment.SHARPNESS, 6, true);
        meta.addEnchant(Enchantment.UNBREAKING, 4, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("A blade forged from the void itself.", NamedTextColor.GRAY)
                .decoration(TextDecoration.ITALIC, false));
        lore.add(Component.empty());
        lore.add(Component.text(helper.getLevelIcon(6, "red") + " Sharpness", NamedTextColor.GRAY)
                .decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text(helper.getLevelIcon(4, "blue") + " Unbreaking", NamedTextColor.GRAY)
                .decoration(TextDecoration.ITALIC, false));
        lore.add(Component.empty());

        for (var ability : getAbilities()) {
            lore.addAll(AbilityLoreHelper.build(ability));
            lore.add(Component.empty());
        }

        lore.add(Component.text("Model/Texture by ", NamedTextColor.DARK_GRAY)
                .decoration(TextDecoration.ITALIC, false)
                .append(Component.text("@Eftann", NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false)));
        lore.add(Component.empty());
        lore.add(Component.text(FontIcon.LEGENDARY.get() + " " + FontIcon.ARTIFACT.get(), NamedTextColor.WHITE)
                .decoration(TextDecoration.ITALIC, false));

        meta.lore(lore);
        stack.setItemMeta(meta);
        stack.setData(DataComponentTypes.ITEM_MODEL, Key.key("powersmp", "aspect_of_the_end"));
        return stack;
    }
}
