package net.tgc.powersmp.registry;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.event.RegistryEvents;
import io.papermc.paper.registry.keys.EnchantmentKeys;
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.tgc.powersmp.Main;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.EquipmentSlotGroup;

@SuppressWarnings("UnstableApiUsage")
public class Bootstrapper implements PluginBootstrap {
    MiniMessage mm = MiniMessage.miniMessage();
    private final Main plugin = Main.getInstance();

    @Override
    public void bootstrap(final BootstrapContext ctx) {
        ctx.getLifecycleManager().registerEventHandler(RegistryEvents.ENCHANTMENT.compose(), event -> {
            event.registry().register(
                    EnchantmentKeys.create(Key.key("powersmp:test")),
                    b -> b.description(mm.deserialize("test"))
                            .supportedItems(event.getOrCreateTag(ItemTypeTagKeys.SWORDS))
                            .anvilCost(10)
                            .maxLevel(5)
                            .weight(10)
                            .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 1))
                            .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(3, 1))
                            .activeSlots(EquipmentSlotGroup.ANY)
            );
        });

        ctx.getLifecycleManager().registerEventHandler(RegistryEvents.ENCHANTMENT.compose(), event -> {
            event.registry().register(
                    EnchantmentKeys.create(Key.key("powersmp:fast_walk")),
                    b -> b.description(mm.deserialize("<red>Fast Walk"))
                            .supportedItems(event.getOrCreateTag(ItemTypeTagKeys.FOOT_ARMOR))
                            .anvilCost(18)
                            .maxLevel(3)
                            .weight(10)
                            .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 1))
                            .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(3, 1))
                            .activeSlots(EquipmentSlotGroup.FEET)
            );
        });

        ctx.getLifecycleManager().registerEventHandler(RegistryEvents.ENCHANTMENT.entryAdd()
                // Increase the max level to 20
                .newHandler(event -> event.builder().maxLevel(8))
                // Configure the handler to only be called for the Vanilla sharpness enchantment.
                .filter(EnchantmentKeys.SHARPNESS)
        );

        ConfigurationSection enchants = plugin.getEnchantsData().getConfigurationSection("enchants");
        assert enchants != null;
        for (String key : enchants.getKeys(false)) {
            TypedKey enchant = EnchantmentKeys.create(Key.key("minecraft", enchants.getString(key + ".name")));
            ctx.getLifecycleManager().registerEventHandler(RegistryEvents.ENCHANTMENT.entryAdd()
                    .newHandler(event -> event.builder().maxLevel(enchants.getInt(key + ".max-level")))
                    .filter(enchant)
            );
        }
    }
}
