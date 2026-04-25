package net.tgc.powersmp.api.item;

import net.tgc.powersmp.api.ability.Ability;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Extend this class to create a custom item.
 * Register it via PowerSMPAPI.registerItem(item) or CustomItemRegistry.getInstance().register(item).
 *
 * Example:
 * <pre>
 * public class FireSword extends CustomItem {
 *     public FireSword(Plugin plugin) {
 *         super(new NamespacedKey(plugin, "fire_sword"), List.of(new FireSlashAbility()));
 *     }
 *
 *     {@literal @}Override
 *     public ItemStack buildItem() {
 *         ItemStack stack = new ItemStack(Material.DIAMOND_SWORD);
 *         ItemMeta meta = stack.getItemMeta();
 *         meta.displayName(Component.text("Fire Sword").color(NamedTextColor.RED));
 *         stack.setItemMeta(meta);
 *         return stack;
 *     }
 * }
 * </pre>
 */
public abstract class CustomItem {
    private final NamespacedKey key;
    private final List<Ability> abilities;

    protected CustomItem(NamespacedKey key, List<Ability> abilities) {
        this.key = key;
        this.abilities = List.copyOf(abilities);
    }

    protected CustomItem(NamespacedKey key) {
        this(key, List.of());
    }

    public NamespacedKey getKey() { return key; }
    public List<Ability> getAbilities() { return abilities; }

    /**
     * Build the base ItemStack. Do NOT apply PDC tags here; the registry handles that automatically.
     */
    public abstract ItemStack buildItem();
}
