package net.tgc.powersmp.api.item;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.tgc.powersmp.Main;
import net.tgc.powersmp.api.ability.Ability;
import net.tgc.powersmp.recipe.RecipeBookRegistry;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class CustomItemRegistry {
    private static final String ITEM_ID_KEY = "custom_item_id";
    private static final String ABILITY_IDS_KEY = "ability_ids";

    private final Map<String, CustomItem> items = new HashMap<>();
    private static CustomItemRegistry instance;
    private Plugin plugin;

    private CustomItemRegistry() {}

    public static CustomItemRegistry getInstance() {
        if (instance == null) instance = new CustomItemRegistry();
        return instance;
    }

    public void setPlugin(Plugin plugin) {
        this.plugin = plugin;
    }

    public void register(CustomItem item) {
        items.put(item.getKey().toString(), item);
    }

    public Optional<CustomItem> get(NamespacedKey key) {
        return Optional.ofNullable(items.get(key.toString()));
    }

    public Collection<CustomItem> getAll() {
        return Collections.unmodifiableCollection(items.values());
    }

    /**
     * Creates an ItemStack with all necessary PDC tags applied (item ID + ability IDs).
     */
    public ItemStack createItemStack(NamespacedKey key) {
        CustomItem item = items.get(key.toString());
        if (item == null) return null;

        ItemStack stack = item.buildItem();
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) return stack;

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(new NamespacedKey(plugin, ITEM_ID_KEY), PersistentDataType.STRING, key.toString());

        if (!item.getAbilities().isEmpty()) {
            String abilityIds = item.getAbilities().stream()
                    .map(Ability::getId)
                    .reduce((a, b) -> a + "," + b)
                    .orElse("");

            pdc.set(
                    new NamespacedKey(plugin, ABILITY_IDS_KEY),
                    PersistentDataType.STRING,
                    abilityIds
            );
        }

        stack.setItemMeta(meta);

        return stack;
    }

    /**
     * Returns the CustomItem linked to this ItemStack, if any.
     */
    public Optional<CustomItem> fromItemStack(ItemStack stack) {
        if (stack == null || !stack.hasItemMeta()) return Optional.empty();
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) return Optional.empty();
        String id = meta.getPersistentDataContainer()
                .get(new NamespacedKey(plugin, ITEM_ID_KEY), PersistentDataType.STRING);
        if (id == null) return Optional.empty();
        return Optional.ofNullable(items.get(id));
    }

    /**
     * Returns ability IDs stored on any ItemStack (custom or vanilla with appended abilities).
     */
    public List<String> getAbilityIds(ItemStack stack) {
        if (stack == null || !stack.hasItemMeta()) return List.of();
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) return List.of();
        String ids = meta.getPersistentDataContainer()
                .get(new NamespacedKey(plugin, ABILITY_IDS_KEY), PersistentDataType.STRING);
        if (ids == null || ids.isEmpty()) return List.of();
        return Arrays.asList(ids.split(","));
    }

    /**
     * Appends an ability to any ItemStack (vanilla or custom). Returns a modified copy.
     */
    public ItemStack appendAbility(ItemStack stack, Ability ability) {
        ItemStack copy = stack.clone();
        ItemMeta meta = copy.getItemMeta();
        if (meta == null) return copy;

        NamespacedKey abilityKey = new NamespacedKey(plugin, ABILITY_IDS_KEY);
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        String existing = pdc.getOrDefault(abilityKey, PersistentDataType.STRING, "");
        String updated = existing.isEmpty() ? ability.getId() : existing + "," + ability.getId();
        pdc.set(abilityKey, PersistentDataType.STRING, updated);

        copy.setItemMeta(meta);
        return copy;
    }
}
