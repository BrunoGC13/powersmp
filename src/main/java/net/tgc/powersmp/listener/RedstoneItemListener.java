package net.tgc.powersmp.listener;

import net.tgc.powersmp.Main;
import net.tgc.powersmp.item.redstone.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

public class RedstoneItemListener implements Listener {

    private final Main plugin;

    public RedstoneItemListener(Main plugin) {
        this.plugin = plugin;
    }

    // ── Crafting: Unfinished + Battery = Charged ─────────────

    @EventHandler
    public void onCraftingClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;
        if (e.getSlotType() != InventoryType.SlotType.RESULT) return;
        if (e.getInventory().getType() != InventoryType.CRAFTING &&
                e.getInventory().getType() != InventoryType.WORKBENCH) return;

        ItemStack[] matrix = e.getInventory().getContents();
        // Bei CRAFTING (2x2): indices 1-4, bei WORKBENCH: indices 1-9
        ItemStack unfinished = null;
        ItemStack battery = null;

        for (ItemStack slot : matrix) {
            if (slot == null) continue;
            if (RedstoneWeapon.isUnfinishedRedstoneItem(slot)) unfinished = slot;
            if (RedstoneBattery.isBattery(slot)) battery = slot;
        }

        if (unfinished == null || battery == null) return;

        e.setCancelled(true);

        String id = RedstoneWeapon.getItemId(unfinished);
        ItemStack charged = buildChargedFromId(id);
        if (charged == null) return;
        charged.addUnsafeEnchantment(Enchantment.EFFICIENCY, 6);
        charged.addUnsafeEnchantment(Enchantment.SHARPNESS, 4);
        charged.addUnsafeEnchantment(Enchantment.UNBREAKING, 5);

        // Matrix leeren
        for (int i = 1; i < matrix.length; i++) {
            e.getInventory().setItem(i, null);
        }

        player.getInventory().addItem(charged);
        player.sendMessage("§4Redstone Item §7charged! §cEnergy: §4500§7/§c500");
    }

    // Vorschau im Crafting-Output anzeigen
    @EventHandler
    public void onPrepare(PrepareItemCraftEvent e) {
        ItemStack[] matrix = e.getInventory().getMatrix();
        ItemStack unfinished = null;
        ItemStack battery = null;

        for (ItemStack slot : matrix) {
            if (slot == null) continue;
            if (RedstoneWeapon.isUnfinishedRedstoneItem(slot)) unfinished = slot;
            if (RedstoneBattery.isBattery(slot)) battery = slot;
        }

        if (unfinished == null || battery == null) return;

        String id = RedstoneWeapon.getItemId(unfinished);
        ItemStack charged = buildChargedFromId(id);
        if (charged == null) return;

        e.getInventory().setResult(charged);
    }

    // ── Schwert: Energy beim Angriff verbrauchen ──────────────

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player player)) return;

        ItemStack item = player.getInventory().getItemInMainHand();
        if (!RedstoneWeapon.isChargedRedstoneItem(item)) return;

        String id = RedstoneWeapon.getItemId(item);
        if (id == null) return;

        int energy = RedstoneWeapon.getEnergy(item);
        if (energy <= 0) return;

        if (id.equals("redstone_sword") || id.equals("redstone_axe")) {
            // Bonus-Schaden basierend auf Energy
            double bonus = id.equals("redstone_sword")
                    ? RedstoneSword.getDamageBonus(energy)
                    : RedstoneAxe.getDamageBonus(energy);
            e.setDamage(e.getDamage() + bonus * (energy / (double) RedstoneWeapon.MAX_ENERGY));

            // Energy verbrauchen
            int newEnergy = energy - 2;
            String displayName = item.getItemMeta().getDisplayName();
            RedstoneWeapon.setEnergy(item, newEnergy, displayName);

            if (newEnergy <= 0) {
                player.sendMessage("§cYour " + displayName + " §cis out of energy!");
            }
        }
    }

    // ── Tools: Energy beim Abbauen verbrauchen ────────────────

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (!RedstoneWeapon.isChargedRedstoneItem(item)) return;

        String id = RedstoneWeapon.getItemId(item);
        if (id == null) return;

        int energy = RedstoneWeapon.getEnergy(item);
        if (energy <= 0) return;

        // Speed-Boost durch schnelleren Block-Break (Haste-Effekt)
        double multiplier = switch (id) {
            case "redstone_pickaxe" -> RedstonePickaxe.getSpeedMultiplier(energy);
            case "redstone_shovel"  -> RedstoneShovel.getSpeedMultiplier(energy);
            case "redstone_axe"     -> RedstoneAxe.getSpeedMultiplier(energy);
            default -> 1.0;
        };

        // Haste-Effekt proportional zur Energy
        int hasteLevel = (int) (multiplier * (energy / (double) RedstoneWeapon.MAX_ENERGY) * 3);
        if (hasteLevel > 0) {
            player.addPotionEffect(new org.bukkit.potion.PotionEffect(
                    org.bukkit.potion.PotionEffectType.HASTE,
                    40, // 2 Sekunden
                    hasteLevel - 1,
                    true, false
            ));
        }

        // Energy verbrauchen
        int newEnergy = energy - 1;
        RedstoneWeapon.setEnergy(item, newEnergy, item.getItemMeta().getDisplayName());

        if (newEnergy <= 0) {
            player.sendMessage("§cYour §4Redstone Tool §cis out of energy!");
        }
    }

    // ── Hilfsmethode ─────────────────────────────────────────

    private ItemStack buildChargedFromId(String id) {
        if (id == null) return null;
        return switch (id) {
            case "unfinished_redstone_sword"   -> new RedstoneSword(plugin).buildCharged();
            case "unfinished_redstone_pickaxe" -> new RedstonePickaxe(plugin).buildCharged();
            case "unfinished_redstone_shovel"  -> new RedstoneShovel(plugin).buildCharged();
            case "unfinished_redstone_axe"     -> new RedstoneAxe(plugin).buildCharged();
            default -> null;
        };
    }
}