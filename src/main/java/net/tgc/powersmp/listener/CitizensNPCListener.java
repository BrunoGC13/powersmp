package net.tgc.powersmp.listener;

import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.tgc.powersmp.Main;
import net.tgc.powersmp.menus.MenuRegistry;
import net.tgc.powersmp.objectives.Objective;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles Citizens NPC right-clicks configured in npc_menus.yml.
 *
 * Simple format  (menu only):
 *   npcs:
 *     0: profile
 *
 * Extended format (dialogue + optional menu):
 *   npcs:
 *     1:
 *       messages:
 *         - "<gold>[Bob]</gold> Hello there, adventurer!"
 *         - "<gold>[Bob]</gold> Did you know about the quest system?"
 *       delay: 2          # seconds between each action
 *       menu: quests      # optional — opened after the last message
 *       show_name: false  # set true to auto-prefix every message with [NpcName]
 */
public class CitizensNPCListener implements Listener {

    private static final MiniMessage MM = MiniMessage.miniMessage();

    private final Main plugin;
    // Players who are currently mid-sequence — blocks re-triggering
    private final Set<UUID> inConversation = ConcurrentHashMap.newKeySet();

    public CitizensNPCListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onNPCRightClick(NPCRightClickEvent event) {
        Player player = event.getClicker();

        // Always cancel so Citizens traits don't fire on top of us
        event.setCancelled(true);

        boolean debug = plugin.isDebugMode();

        // Don't re-trigger if already mid-conversation
        if (inConversation.contains(player.getUniqueId())) {
            if (debug) plugin.getLogger().info("[NPCMenus] " + player.getName() + " already in conversation — ignoring click");
            return;
        }

        if (!player.hasPermission("powersmp.menu.open")) {
            if (debug) plugin.getLogger().info("[NPCMenus] " + player.getName() + " lacks powersmp.menu.open — ignoring click");
            return;
        }

        int npcId = event.getNPC().getId();
        String npcName = event.getNPC().getName();

        if (debug) plugin.getLogger().info("[NPCMenus] " + player.getName() + " right-clicked NPC id=" + npcId + " name=" + npcName);

        // Assignment system takes priority — if this NPC has configured steps for the player, handle them.
        if (plugin.getObjectiveManager().handleNpcClick(player, npcId, npcName)) return;

        ConfigurationSection npcsSection = plugin.getNpcMenusConfig().getConfigurationSection("npcs");
        if (npcsSection == null) {
            if (debug) plugin.getLogger().warning("[NPCMenus] 'npcs' section is NULL in npc_menus.yml — check for YAML syntax errors");
            return;
        }

        String key = String.valueOf(npcId);
        Object raw = npcsSection.get(key);
        if (raw == null) {
            if (debug) plugin.getLogger().info("[NPCMenus] No entry for NPC id=" + npcId + " in npc_menus.yml");
            return;
        }

        if (debug) plugin.getLogger().info("[NPCMenus] Found entry for NPC id=" + npcId + ": " + raw);

        // ── Parse config entry ────────────────────────────────────────────────

        List<String> messages = new ArrayList<>();
        String menuId = null;
        long delayTicks = 40; // default 2 s

        if (raw instanceof String simpleMenu) {
            // Simple format: just open a menu
            menuId = simpleMenu;

        } else {
            // Extended format
            ConfigurationSection section = npcsSection.getConfigurationSection(key);
            if (section == null) return;

            messages = section.getStringList("messages");
            menuId   = section.getString("menu");
            delayTicks = (long) (section.getDouble("delay", 2.0) * 20);

            // Auto-prefix with NPC name if requested
            if (section.getBoolean("show_name", false) && !messages.isEmpty()) {
                String prefix = "<gold>[" + npcName + "]<reset> ";
                messages = messages.stream().map(m -> prefix + m).toList();
            }
        }

        if (messages.isEmpty() && menuId == null) return;

        // ── Build action list ─────────────────────────────────────────────────

        List<Runnable> actions = new ArrayList<>();
        for (String line : messages) {
            final String msg = line;
            actions.add(() -> player.sendMessage(MM.deserialize(msg)));
        }
        if (menuId != null) {
            final String finalMenu = menuId;
            actions.add(() -> {
                boolean opened = MenuRegistry.open(finalMenu, player);
                if (!opened) {
                    plugin.getLogger().warning("[NPCMenus] NPC " + npcId
                            + " references unknown menu '" + finalMenu + "' — check npc_menus.yml");
                }
            });
        }

        // Objectives
        ConfigurationSection section = npcsSection.getConfigurationSection(key);
        if (section == null) return;
        String objectiveId = section.getString("objective");

        if (!plugin.getObjectiveManager().has(player, objectiveId)) {
            if (objectiveId != null) {
                actions.add(() ->
                        plugin.getObjectiveManager().give(player, Objective.of(objectiveId))
                );
            }
        }

        // ── Execute sequence ──────────────────────────────────────────────────

        inConversation.add(player.getUniqueId());
        runSequence(player, actions, delayTicks, 0);
    }

    private void runSequence(Player player, List<Runnable> actions, long delayTicks, int index) {
        if (index >= actions.size()) {
            inConversation.remove(player.getUniqueId());
            return;
        }

        // First action fires immediately; the rest wait delayTicks
        long wait = (index == 0) ? 0L : delayTicks;

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (!player.isOnline()) {
                inConversation.remove(player.getUniqueId());
                return;
            }
            actions.get(index).run();
            runSequence(player, actions, delayTicks, index + 1);
        }, wait);
    }
}
