package net.tgc.powersmp.economy;

import io.papermc.paper.persistence.PersistentDataContainerView;
import net.tgc.powersmp.Main;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ItemOwnerShip {

    private static final PersistentDataType<Byte, Byte> BYTE_TYPE =
            PersistentDataType.BYTE;

    private final NamespacedKey itemKey;
    private final NamespacedKey itemOwnerKey;
    private final Main plugin;

    private final Map<UUID, UUID> itemPlayerMap;

    public ItemOwnerShip(Main plugin) {
        this.plugin = plugin;

        this.itemKey = new NamespacedKey(plugin, "item_uuid");
        this.itemOwnerKey = new NamespacedKey(plugin, "item_owner");

        this.itemPlayerMap = loadItemOwnerShipData();
    }

    public Map<UUID, UUID> getItemPlayerMap() {
        return itemPlayerMap;
    }

    /**
     * Applies ownership only to items that cannot stack.
     *
     * Stackable items are deliberately cleaned of old ownership data so
     * previously-created items can stack again.
     */
    public void changeItemOwnerShip(ItemStack item, Player player) {
        if (item == null || item.getType().isAir()) {
            return;
        }

        if (item.getMaxStackSize() > 1) {
            removeOwnershipData(item);
            return;
        }

        if (player == null) {
            return;
        }

        UUID itemUuid = getItemUuid(item);

        if (itemUuid == null) {
            itemUuid = createItemUuid(item);
        }

        UUID oldOwner = getOwnerUuid(item);

        if (oldOwner == null) {
            setOwner(item, player, itemUuid);
            return;
        }

        if (oldOwner.equals(player.getUniqueId())) {
            return;
        }

        setOwner(item, player, itemUuid);
        plugin.getLogger().info("Changed Ownership of " + player.getName() + " for item " + item.getType().name() + " --- UUID: " + itemUuid);
        plugin.getNetWorthManager().updatePlayerNetworth(player, item);
    }

    /**
     * Returns true when the item is intended to remain an individual item.
     *
     * This uses the item's actual maximum stack size, so future custom
     * items require no changes: stackable items stay stackable and
     * max-stack-size-1 items receive ownership tracking.
     */
    public boolean requiresIndividualOwnership(ItemStack item) {
        return item != null
                && !item.getType().isAir()
                && item.getMaxStackSize() <= 1;
    }

    /**
     * Removes ownership PDC from an item.
     *
     * This also repairs old stackable items that already received a unique
     * UUID and owner.
     */
    public void removeOwnershipData(ItemStack item) {
        if (item == null || item.getType().isAir()) {
            return;
        }

        UUID itemUuid = getItemUuid(item);

        if (itemUuid != null) {
            itemPlayerMap.remove(itemUuid);
        }

        item.editPersistentDataContainer(pdc -> {
            pdc.remove(itemKey);
            pdc.remove(itemOwnerKey);
        });

        if (itemUuid != null) {
            saveItemOwnerShip(itemPlayerMap);
        }
    }

    private UUID getItemUuid(ItemStack item) {
        PersistentDataContainerView pdc =
                item.getPersistentDataContainer();

        String value = pdc.get(itemKey, PersistentDataType.STRING);

        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException exception) {
            removeInvalidItemUuid(item);
            return null;
        }
    }

    private UUID createItemUuid(ItemStack item) {
        UUID itemUuid = UUID.randomUUID();

        item.editPersistentDataContainer(pdc ->
                pdc.set(
                        itemKey,
                        PersistentDataType.STRING,
                        itemUuid.toString()
                )
        );

        return itemUuid;
    }

    private UUID getOwnerUuid(ItemStack item) {
        PersistentDataContainerView pdc =
                item.getPersistentDataContainer();

        String value = pdc.get(itemOwnerKey, PersistentDataType.STRING);

        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException exception) {
            removeInvalidOwnerUuid(item);
            return null;
        }
    }

    private void setOwner(ItemStack item, Player player, UUID itemUuid) {
        UUID ownerUuid = player.getUniqueId();

        item.editPersistentDataContainer(pdc ->
                pdc.set(
                        itemOwnerKey,
                        PersistentDataType.STRING,
                        ownerUuid.toString()
                )
        );

        itemPlayerMap.put(itemUuid, ownerUuid);
        saveItemOwnerShip(itemPlayerMap);
    }

    private void removeInvalidItemUuid(ItemStack item) {
        item.editPersistentDataContainer(pdc ->
                pdc.remove(itemKey)
        );
    }

    private void removeInvalidOwnerUuid(ItemStack item) {
        item.editPersistentDataContainer(pdc ->
                pdc.remove(itemOwnerKey)
        );
    }

    public void saveItemOwnerShip(Map<UUID, UUID> ownership) {
        plugin.getItemOwnerShipData().set("items", null);

        for (Map.Entry<UUID, UUID> entry : ownership.entrySet()) {
            plugin.getItemOwnerShipData().set(
                    "items." + entry.getKey(),
                    entry.getValue().toString()
            );
        }

        plugin.saveItemOwnerShipData();
    }

    public Map<UUID, UUID> loadItemOwnerShipData() {
        ConfigurationSection section =
                plugin.getItemOwnerShipData()
                        .getConfigurationSection("items");

        Map<UUID, UUID> loaded = new HashMap<>();

        if (section == null) {
            return loaded;
        }

        for (String itemUuidString : section.getKeys(false)) {
            try {
                UUID itemUuid = UUID.fromString(itemUuidString);
                String ownerUuidString = section.getString(itemUuidString);

                if (ownerUuidString == null || ownerUuidString.isBlank()) {
                    continue;
                }

                UUID ownerUuid = UUID.fromString(ownerUuidString);
                loaded.put(itemUuid, ownerUuid);
            } catch (IllegalArgumentException exception) {
                plugin.getLogger().warning(
                        "[Ownership] Ignoring invalid ownership entry: "
                                + itemUuidString
                );
            }
        }

        return loaded;
    }
}