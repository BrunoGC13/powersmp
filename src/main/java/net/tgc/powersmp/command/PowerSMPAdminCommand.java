package net.tgc.powersmp.command;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.tgc.powersmp.Main;
import net.tgc.powersmp.api.item.CustomItemRegistry;
import net.tgc.powersmp.dungeon.LootOrb;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@SuppressWarnings("UnstableApiUsage")
public class PowerSMPAdminCommand implements BasicCommand {

    private static final MiniMessage MM = MiniMessage.miniMessage();
    private static final String PREFIX = "<grey>[<gold>PowerSMP<grey>] <reset><white>";

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        CommandSender sender = source.getSender();

        if (args.length == 0) {
            sendUsage(sender);
            return;
        }

        switch (args[0].toLowerCase()) {
            case "giveitem"     -> handleGiveItem(sender, args);
            case "dungeoninfo"  -> handleDungeonInfo(sender);
            case "debugmode"    -> handleDebugMode(sender, args);
            case "reload"       -> handleReload(sender);
            case "player"       -> handlePlayer(sender, args);
            default             -> sender.sendMessage(MM.deserialize(PREFIX + "<red>Unknown subcommand."));
        }
    }

    // ── /powersmp dungeoninfo ────────────────────────────────────────────────

    private void handleDungeonInfo(CommandSender sender) {
        Main plugin = Main.getInstance();

        sender.sendMessage(MM.deserialize("<gold>═══════ Dungeon Debug Info ═══════"));

        // MythicMobs hook
        boolean mmHooked = plugin.getMythicMobs() != null;
        sender.sendMessage(MM.deserialize("<gray>MythicMobs hook: " + (mmHooked ? "<green>connected" : "<red>NULL (bosses disabled)")));
        sender.sendMessage(MM.deserialize("<gray>Debug mode: " + (plugin.isDebugMode() ? "<green>ON" : "<yellow>OFF")));

        // Dungeon config section
        ConfigurationSection dungeonsSection = plugin.getDungeonsData().getConfigurationSection("dungeons");
        if (dungeonsSection == null) {
            sender.sendMessage(MM.deserialize("<red>dungeons section in dungeons.yml is NULL!"));
            return;
        }

        Set<String> activeBosses = plugin.getDungeonManager() != null
                ? plugin.getDungeonManager().getActiveBosses()
                : Set.of();

        Map<String, List<LootOrb>> orbMap = plugin.getDungeonLootManager() != null
                ? plugin.getDungeonLootManager().getOrbs()
                : Map.of();

        for (String key : dungeonsSection.getKeys(false)) {
            ConfigurationSection dungeon = dungeonsSection.getConfigurationSection(key);
            if (dungeon == null) {
                sender.sendMessage(MM.deserialize("<red>[" + key + "] config section is NULL"));
                continue;
            }

            sender.sendMessage(MM.deserialize("<gold>── [" + key + "] ──────────────"));

            // Raw config values
            boolean active     = dungeon.getBoolean("active", true);
            String worldName   = dungeon.getString("world");
            double cx          = dungeon.getDouble("center.x");
            double cy          = dungeon.getDouble("center.y");
            double cz          = dungeon.getDouble("center.z");
            double trigRadius  = dungeon.getDouble("trigger_radius");
            double bossX       = dungeon.getDouble("boss_spawn.x");
            double bossY       = dungeon.getDouble("boss_spawn.y");
            double bossZ       = dungeon.getDouble("boss_spawn.z");
            String bossMob     = dungeon.getString("boss_mob");

            org.bukkit.World world = worldName != null ? Bukkit.getWorld(worldName) : null;

            sender.sendMessage(MM.deserialize(
                    "<gray>  active=<white>" + active
                    + " <gray>world=<white>" + worldName + (world == null ? " <red>(NULL!)" : " <green>(loaded)")
            ));
            sender.sendMessage(MM.deserialize(
                    "<gray>  center=<white>[" + cx + ", " + cy + ", " + cz + "]"
                    + "  <gray>triggerRadius=<white>" + trigRadius
            ));
            sender.sendMessage(MM.deserialize(
                    "<gray>  bossSpawn=<white>[" + bossX + ", " + bossY + ", " + bossZ + "]"
            ));
            sender.sendMessage(MM.deserialize(
                    "<gray>  bossMob=<white>" + (bossMob == null ? "<red>NULL!" : bossMob)
                    + "  <gray>bossActive=<white>" + (activeBosses.contains(key) ? "<green>YES" : "<yellow>NO")
            ));

            // Loot orbs
            List<LootOrb> orbs = orbMap.getOrDefault(key, List.of());
            sender.sendMessage(MM.deserialize("<gray>  lootOrbs=<white>" + orbs.size()));
            for (int i = 0; i < orbs.size(); i++) {
                LootOrb orb = orbs.get(i);
                Location loc = orb.getLocation();
                sender.sendMessage(MM.deserialize(
                        "<dark_gray>    #" + (i + 1) + " <white>["
                        + (int) loc.getX() + ", " + (int) loc.getY() + ", " + (int) loc.getZ() + "]"
                        + " <dark_gray>uuid=" + orb.getEntityId().toString().substring(0, 8) + "…"
                ));
            }
        }

        sender.sendMessage(MM.deserialize("<gold>═══════════════════════════════════"));
    }

    // ── /powersmp debugmode [on|off] ─────────────────────────────────────────

    private void handleDebugMode(CommandSender sender, String[] args) {
        Main plugin = Main.getInstance();
        if (args.length < 2) {
            sender.sendMessage(MM.deserialize(PREFIX + "Debug mode is currently <white>"
                    + (plugin.isDebugMode() ? "<green>ON" : "<yellow>OFF")
                    + "<white>. Use <gray>/powersmp debugmode <on|off>"));
            return;
        }
        boolean enable = args[1].equalsIgnoreCase("on");
        plugin.setDebugMode(enable);
        sender.sendMessage(MM.deserialize(PREFIX + "Debug mode " + (enable ? "<green>enabled" : "<yellow>disabled")
                + "<white>. Console will " + (enable ? "" : "no longer ") + "log dungeon trigger checks."));
    }

    // ── /powersmp reload ─────────────────────────────────────────────────────

    private void handleReload(CommandSender sender) {
        Main.getInstance().reload();
        sender.sendMessage(MM.deserialize(PREFIX
                + "<green>Reloaded <white>npc_menus.yml<gray>, <white>npc_assignments.yml<gray>, <white>dungeons.yml<gray>."));
    }

    // ── /powersmp giveitem ───────────────────────────────────────────────────

    private void handleGiveItem(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sendUsage(sender);
            return;
        }

        String rawKey = args[1];
        NamespacedKey key = NamespacedKey.fromString(rawKey);
        if (key == null) {
            sender.sendMessage(MM.deserialize(PREFIX + "<red>Invalid key format: <gray>" + rawKey
                    + " <dark_gray>(expected <white>namespace:key<dark_gray>)"));
            return;
        }

        int amount;
        try {
            amount = Math.clamp(Integer.parseInt(args[2]), 1, 64);
        } catch (NumberFormatException e) {
            sender.sendMessage(MM.deserialize(PREFIX + "<red>Amount must be a number between 1 and 64."));
            return;
        }

        Player target;
        if (args.length >= 4) {
            target = Bukkit.getPlayer(args[3]);
            if (target == null) {
                sender.sendMessage(MM.deserialize(PREFIX + "<red>Player not found: <gray>" + args[3]));
                return;
            }
        } else if (sender instanceof Player p) {
            target = p;
        } else {
            sender.sendMessage(MM.deserialize(PREFIX + "<red>Specify a target player when running from console."));
            return;
        }

        ItemStack stack = CustomItemRegistry.getInstance().createItemStack(key);
        if (stack == null) {
            sender.sendMessage(MM.deserialize(PREFIX + "<red>Unknown custom item: <gray>" + rawKey));
            return;
        }

        stack.setAmount(amount);
        target.getInventory().addItem(stack);

        sender.sendMessage(MM.deserialize(PREFIX
                + "Gave <yellow>" + amount + "x <white>" + rawKey
                + " <gray>to <white>" + target.getName()));
        if (!target.equals(sender)) {
            target.sendMessage(MM.deserialize(PREFIX
                    + "You received <yellow>" + amount + "x <white>" + rawKey));
        }
    }

    // ── /powersmp player ─────────────────────────────────────────────────────

    private void handlePlayer(CommandSender sender, String[] args) {
        // /powersmp player <name> reset [all|objectives|quests|level]
        // /powersmp player <name> set level|xp|coins <value>
        // /powersmp player <name> set objective <npcId> <step>
        if (args.length < 3) { sendPlayerUsage(sender); return; }

        String targetName = args[1];
        Player online = Bukkit.getPlayer(targetName);
        UUID uuid;
        if (online != null) {
            uuid = online.getUniqueId();
        } else {
            @SuppressWarnings("deprecation")
            org.bukkit.OfflinePlayer offline = Bukkit.getOfflinePlayer(targetName);
            if (!offline.hasPlayedBefore()) {
                sender.sendMessage(MM.deserialize(PREFIX + "<red>Player not found or has never joined: <gray>" + targetName));
                return;
            }
            uuid = offline.getUniqueId();
        }

        switch (args[2].toLowerCase()) {
            case "reset" -> handlePlayerReset(sender, uuid, targetName, online, args);
            case "set"   -> handlePlayerSet(sender, uuid, targetName, online, args);
            default      -> sendPlayerUsage(sender);
        }
    }

    private void handlePlayerReset(CommandSender sender, UUID uuid, String name, Player online, String[] args) {
        String section = args.length >= 4 ? args[3].toLowerCase() : "all";
        Main plugin = Main.getInstance();
        String base = "players." + uuid;

        switch (section) {
            case "objectives" -> {
                plugin.getObjectiveManager().resetObjectives(uuid);
                sender.sendMessage(MM.deserialize(PREFIX + "Reset <yellow>objectives <white>for <yellow>" + name + "<white>."));
            }
            case "quests" -> {
                plugin.getPlayerData().set(base + ".quests", null);
                plugin.savePlayerData();
                // Re-initialise quest trees for online players so they start fresh
                if (online != null) {
                    plugin.getQuestStorage().save(); // flush first
                    // QuestService.initPlayer would normally set the first quest; player can relog to trigger it
                }
                sender.sendMessage(MM.deserialize(PREFIX + "Reset <yellow>quests <white>for <yellow>" + name
                        + "<white>." + (online != null ? " <gray>(relog to reinitialise quest trees)" : "")));
            }
            case "level" -> {
                plugin.getPlayerData().set(base + ".level", 1);
                plugin.getPlayerData().set(base + ".xp", 0);
                plugin.savePlayerData();
                sender.sendMessage(MM.deserialize(PREFIX + "Reset <yellow>level & xp <white>for <yellow>" + name + "<white>."));
            }
            case "all" -> {
                plugin.getObjectiveManager().resetObjectives(uuid);
                plugin.getPlayerData().set(base + ".quests", null);
                plugin.getPlayerData().set(base + ".level", 1);
                plugin.getPlayerData().set(base + ".xp", 0);
                plugin.savePlayerData();
                sender.sendMessage(MM.deserialize(PREFIX + "Reset <red>all data <white>for <yellow>" + name + "<white>."));
            }
            default -> sender.sendMessage(MM.deserialize(PREFIX
                    + "<red>Unknown section. <gray>Use: <white>all, objectives, quests, level"));
        }
    }

    private void handlePlayerSet(CommandSender sender, UUID uuid, String name, Player online, String[] args) {
        // /powersmp player <name> set <field> [extra] <value>
        if (args.length < 5) { sendPlayerUsage(sender); return; }

        Main plugin = Main.getInstance();
        String base = "players." + uuid;
        String field = args[3].toLowerCase();

        switch (field) {
            case "level" -> {
                int val = parseIntArg(sender, args[4]); if (val < 0) return;
                plugin.getPlayerData().set(base + ".level", val);
                plugin.getPlayerData().set(base + ".xp", 0);
                plugin.savePlayerData();
                sender.sendMessage(MM.deserialize(PREFIX + "Set <yellow>level<white> of <yellow>" + name
                        + " <white>to <green>" + val + "<white>."));
            }
            case "xp" -> {
                int val = parseIntArg(sender, args[4]); if (val < 0) return;
                plugin.getPlayerData().set(base + ".xp", val);
                plugin.savePlayerData();
                sender.sendMessage(MM.deserialize(PREFIX + "Set <yellow>xp<white> of <yellow>" + name
                        + " <white>to <green>" + val + "<white>."));
            }
            case "coins" -> {
                double val;
                try { val = Double.parseDouble(args[4]); }
                catch (NumberFormatException e) {
                    sender.sendMessage(MM.deserialize(PREFIX + "<red>Invalid number: <gray>" + args[4])); return;
                }
                plugin.getPlayerData().set(base + ".coins", val);
                plugin.savePlayerData();
                sender.sendMessage(MM.deserialize(PREFIX + "Set <yellow>coins<white> of <yellow>" + name
                        + " <white>to <green>" + val + "<white>."));
            }
            case "objective" -> {
                // /powersmp player <name> set objective <npcId> <step>
                if (args.length < 6) { sendPlayerUsage(sender); return; }
                int npcId = parseIntArg(sender, args[4]); if (npcId < 0) return;
                int step  = parseIntArg(sender, args[5]); if (step  < 0) return;
                String questId = "npc_" + npcId;
                plugin.getObjectiveManager().setObjectiveStep(uuid, questId, step);
                sender.sendMessage(MM.deserialize(PREFIX + "Set <yellow>" + questId + "<white> step of <yellow>"
                        + name + " <white>to <green>" + step + "<white>."));
            }
            default -> sender.sendMessage(MM.deserialize(PREFIX
                    + "<red>Unknown field. <gray>Use: <white>level, xp, coins, objective"));
        }
    }

    private int parseIntArg(CommandSender sender, String raw) {
        try {
            int v = Integer.parseInt(raw);
            if (v < 0) throw new NumberFormatException();
            return v;
        } catch (NumberFormatException e) {
            sender.sendMessage(MM.deserialize(PREFIX + "<red>Expected a non-negative integer, got: <gray>" + raw));
            return -1;
        }
    }

    private void sendPlayerUsage(CommandSender sender) {
        sender.sendMessage(MM.deserialize("<gold>── Player data commands ──────────────────"));
        sender.sendMessage(MM.deserialize("<gray>  /powersmp player <name> reset [all|objectives|quests|level]"));
        sender.sendMessage(MM.deserialize("<gray>  /powersmp player <name> set level <value>"));
        sender.sendMessage(MM.deserialize("<gray>  /powersmp player <name> set xp <value>"));
        sender.sendMessage(MM.deserialize("<gray>  /powersmp player <name> set coins <value>"));
        sender.sendMessage(MM.deserialize("<gray>  /powersmp player <name> set objective <npcId> <step>"));
    }

    // ── Tab completion ────────────────────────────────────────────────────────

    @Override
    public Collection<String> suggest(CommandSourceStack source, String[] args) {
        if (args.length <= 1) {
            return filter(List.of("giveitem", "dungeoninfo", "debugmode", "reload", "player"), args.length == 1 ? args[0] : "");
        }

        String sub = args[0].toLowerCase();

        if (sub.equals("giveitem")) {
            if (args.length == 2) {
                List<String> keys = CustomItemRegistry.getInstance().getAll().stream()
                        .map(item -> item.getKey().toString())
                        .sorted()
                        .toList();
                return filter(keys, args[1]);
            }
            if (args.length == 3) return filter(List.of("1", "4", "8", "16", "32", "64"), args[2]);
            if (args.length == 4) {
                return filter(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList(), args[3]);
            }
        }

        if (sub.equals("debugmode") && args.length == 2) {
            return filter(List.of("on", "off"), args[1]);
        }

        if (sub.equals("player")) {
            if (args.length == 2) return filter(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList(), args[1]);
            if (args.length == 3) return filter(List.of("reset", "set"), args[2]);
            String action = args[2].toLowerCase();
            if (action.equals("reset") && args.length == 4)
                return filter(List.of("all", "objectives", "quests", "level"), args[3]);
            if (action.equals("set") && args.length == 4)
                return filter(List.of("level", "xp", "coins", "objective"), args[3]);
        }

        return List.of();
    }

    private static List<String> filter(List<String> options, String input) {
        if (input.isEmpty()) return options;
        String lower = input.toLowerCase();
        return options.stream().filter(s -> s.toLowerCase().startsWith(lower)).toList();
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(MM.deserialize(PREFIX + "Subcommands:"));
        sender.sendMessage(MM.deserialize("<gray>  /powersmp giveitem <item> <amount> [player]"));
        sender.sendMessage(MM.deserialize("<gray>  /powersmp dungeoninfo"));
        sender.sendMessage(MM.deserialize("<gray>  /powersmp debugmode <on|off>"));
        sender.sendMessage(MM.deserialize("<gray>  /powersmp reload"));
        sender.sendMessage(MM.deserialize("<gray>  /powersmp player <name> reset|set ..."));
    }

    @Override
    public @Nullable String permission() {
        return "powersmp.admin";
    }
}
