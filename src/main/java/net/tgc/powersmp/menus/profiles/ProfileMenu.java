package net.tgc.powersmp.menus.profiles;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.tgc.powersmp.Main;
import net.tgc.powersmp.menus.Button;
import net.tgc.powersmp.menus.Menu;
import net.tgc.powersmp.player.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@SuppressWarnings("UnstableApiUsage")
public class ProfileMenu extends Menu {

    public ProfileMenu(Player player) {
        this.setTitle("<yellow>✦ " + player.getName() + "'s Profile");
        this.setSize(9 * 6);

        PlayerDataManager pdm = PlayerDataManager.getInstance();
        var lm = Main.getInstance().getLevelManager();

        int level      = lm.getLevel(player);
        int xp         = lm.getXp(player);
        int hearts     = pdm.getHearts(player);
        double power   = pdm.getPower(player);
        double maxPower = pdm.getMaxPower(player);
        boolean powerBar = pdm.isPowerBarEnabled(player);
        int deaths     = player.getStatistic(Statistic.DEATHS);
        int pvpKills   = player.getStatistic(Statistic.PLAYER_KILLS);
        double nextCoins = lm.getNextLevelCoins(player);
        String nextPerk  = lm.getNextPerk(player);

        // ── Glass filler ─────────────────────────────────────
        Set<Integer> content = Set.of(13, 19, 21, 23, 25, 28, 30, 32, 34, 37, 39, 41, 43);
        for (int i = 0; i < 54; i++) {
            if (content.contains(i)) continue;
            final int slot = i;
            addButton(new Button(slot) {
                @Override public ItemStack getItem() {
                    ItemStack pane = new ItemStack(
                            slot < 9 || slot > 44 || slot % 9 == 0 || slot % 9 == 8
                                    ? Material.BLACK_STAINED_GLASS_PANE
                                    : Material.GRAY_STAINED_GLASS_PANE);
                    pane.setData(DataComponentTypes.ITEM_NAME, Component.empty());
                    return pane;
                }
                @Override public void onClick(Player player) {}
            });
        }

        // ── Slot 13: Player Head ─────────────────────────────
        addButton(new Button(13) {
            @Override public ItemStack getItem() {
                ItemStack stack = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta meta = (SkullMeta) stack.getItemMeta();
                meta.setOwningPlayer(player);
                meta.lore(List.of(
                        Component.empty(),
                        row("World",    player.getWorld().getName()),
                        row("Gamemode", cap(player.getGameMode().name())),
                        Component.empty(),
                        row("Coins",    String.format("%.2f", Main.getInstance()
                                .getCoinsEconomy().getBalance(
                                        Bukkit.getOfflinePlayer(player.getUniqueId()))))
                ));
                stack.setItemMeta(meta);
                stack.setData(DataComponentTypes.ITEM_NAME,
                        Component.text(player.getName(), NamedTextColor.YELLOW)
                                .decoration(TextDecoration.BOLD, true)
                                .decoration(TextDecoration.ITALIC, false));
                return stack;
            }
            @Override public void onClick(Player player) {}
        });

        // ── Slot 19: Level ───────────────────────────────────
        addButton(new Button(19) {
            @Override public ItemStack getItem() {
                String bar = progressBar(xp, 100, 20);
                return stat(Material.EXPERIENCE_BOTTLE, "⭐ Level " + level, NamedTextColor.GREEN,
                        List.of(
                                Component.empty(),
                                row("Level",    String.valueOf(level)),
                                row("XP",       xp + " / 100"),
                                Component.text(bar, NamedTextColor.GREEN)
                                        .decoration(TextDecoration.ITALIC, false),
                                Component.empty(),
                                row("Next LvL", "+" + String.format("%.2f", nextCoins) + " Coins"),
                                row("Next Perk", nextPerk)
                        ));
            }
            @Override public void onClick(Player player) {}
        });

        // ── Slot 21: Hearts ──────────────────────────────────
        addButton(new Button(21) {
            @Override public ItemStack getItem() {
                return stat(Material.RED_DYE, "❤ Hearts", NamedTextColor.RED,
                        List.of(
                                Component.empty(),
                                row("Hearts", hearts + " ❤"),
                                row("Max HP", (hearts * 2) + " HP"),
                                Component.empty(),
                                Component.text("Next heart at Level "
                                                + nextHeartLevel(level), NamedTextColor.DARK_GRAY)
                                        .decoration(TextDecoration.ITALIC, false)
                        ));
            }
            @Override public void onClick(Player player) {}
        });

        // ── Slot 23: Power ───────────────────────────────────
        addButton(new Button(23) {
            @Override public ItemStack getItem() {
                String bar = progressBar((int) power, (int) maxPower, 20);
                return stat(Material.BLAZE_POWDER, "⚡ Power", NamedTextColor.YELLOW,
                        List.of(
                                Component.empty(),
                                row("Power", String.format("%.0f / %.0f ⚡", power, maxPower)),
                                Component.text(bar, NamedTextColor.YELLOW)
                                        .decoration(TextDecoration.ITALIC, false)
                        ));
            }
            @Override public void onClick(Player player) {}
        });

        // ── Slot 25: Power Bar Toggle ────────────────────────
        addButton(new Button(25) {
            @Override public ItemStack getItem() {
                return stat(Material.LEVER, "Power Bar", NamedTextColor.WHITE,
                        List.of(
                                Component.empty(),
                                powerBar
                                        ? Component.text("Status: Enabled ✔", NamedTextColor.GREEN)
                                        .decoration(TextDecoration.ITALIC, false)
                                        : Component.text("Status: Disabled ✘", NamedTextColor.RED)
                                        .decoration(TextDecoration.ITALIC, false),
                                Component.empty(),
                                Component.text("Click to toggle!", NamedTextColor.DARK_GRAY)
                                        .decoration(TextDecoration.ITALIC, false)
                        ));
            }
            @Override public void onClick(Player clicker) {
                pdm.setPowerBarEnabled(clicker, !pdm.isPowerBarEnabled(clicker));
                Bukkit.getScheduler().runTask(Main.getInstance(),
                        () -> new ProfileMenu(clicker).displayTo(clicker));
            }
        });

        // ── Slot 28: Deaths ──────────────────────────────────
        addButton(new Button(28) {
            @Override public ItemStack getItem() {
                return stat(Material.WITHER_ROSE, "☠ Deaths", NamedTextColor.DARK_GRAY,
                        List.of(Component.empty(), row("Deaths", String.valueOf(deaths))));
            }
            @Override public void onClick(Player player) {}
        });

        // ── Slot 30: PvP Kills ───────────────────────────────
        addButton(new Button(30) {
            @Override public ItemStack getItem() {
                return stat(Material.IRON_SWORD, "⚔ PvP Kills", NamedTextColor.WHITE,
                        List.of(Component.empty(), row("Kills", String.valueOf(pvpKills))));
            }
            @Override public void onClick(Player player) {}
        });

        // ── Slot 32: Location ────────────────────────────────
        addButton(new Button(32) {
            @Override public ItemStack getItem() {
                var loc = player.getLocation();
                return stat(Material.COMPASS, "📍 Location", NamedTextColor.AQUA,
                        List.of(
                                Component.empty(),
                                row("X", String.valueOf(loc.getBlockX())),
                                row("Y", String.valueOf(loc.getBlockY())),
                                row("Z", String.valueOf(loc.getBlockZ())),
                                row("World", player.getWorld().getName())
                        ));
            }
            @Override public void onClick(Player player) {}
        });

        // ── Slot 34: Perks ───────────────────────────────────
        addButton(new Button(34) {
            @Override public ItemStack getItem() {
                List<Component> lore = new ArrayList<>();
                lore.add(Component.empty());
                boolean fastRegen = Main.getInstance().getPlayerData()
                        .getBoolean("players." + player.getUniqueId() + ".perks.fast_regen", false);
                boolean extraSlot = Main.getInstance().getPlayerData()
                        .getBoolean("players." + player.getUniqueId() + ".perks.extra_gem_slot", false);
                boolean xpBoost = Main.getInstance().getPlayerData()
                        .getBoolean("players." + player.getUniqueId() + ".perks.xp_boost", false);

                lore.add(perkLine("⚡ Fast Power Regen", fastRegen));
                lore.add(perkLine("✦ Extra Gem Slot",   extraSlot));
                lore.add(perkLine("⭐ +20% Quest XP",   xpBoost));
                lore.add(Component.empty());
                lore.add(Component.text("Next: " + nextPerk, NamedTextColor.DARK_GRAY)
                        .decoration(TextDecoration.ITALIC, false));
                return stat(Material.NETHER_STAR, "✦ Perks", NamedTextColor.LIGHT_PURPLE, lore);
            }
            @Override public void onClick(Player player) {}
        });

        // ── Slot 37: Food ────────────────────────────────────
        addButton(new Button(37) {
            @Override public ItemStack getItem() {
                int food = player.getFoodLevel();
                String bar = progressBar(food, 20, 20);
                return stat(Material.COOKED_BEEF, "Food Level", NamedTextColor.GOLD,
                        List.of(
                                Component.empty(),
                                row("Food", food + " / 20"),
                                Component.text(bar, NamedTextColor.GOLD)
                                        .decoration(TextDecoration.ITALIC, false)
                        ));
            }
            @Override public void onClick(Player player) {}
        });
    }

    // ── Helpers ──────────────────────────────────────────────

    private static String progressBar(int current, int max, int length) {
        int filled = max > 0 ? (int) ((current / (double) max) * length) : 0;
        StringBuilder bar = new StringBuilder("[");
        for (int i = 0; i < length; i++) {
            bar.append(i < filled ? "█" : "░");
        }
        bar.append("] ").append(current).append("/").append(max);
        return bar.toString();
    }

    private static int nextHeartLevel(int currentLevel) {
        int next = currentLevel + 1;
        while (!(next >= 5 && next % 5 == 0)) next++;
        return next;
    }

    private static Component perkLine(String name, boolean unlocked) {
        return Component.text((unlocked ? "✔ " : "✘ ") + name,
                        unlocked ? NamedTextColor.GREEN : NamedTextColor.DARK_GRAY)
                .decoration(TextDecoration.ITALIC, false);
    }

    private static ItemStack stat(Material mat, String name, NamedTextColor color, List<Component> lore) {
        ItemStack stack = new ItemStack(mat);
        ItemMeta meta = stack.getItemMeta();
        meta.displayName(Component.text(name, color)
                .decoration(TextDecoration.ITALIC, false));
        meta.lore(lore);
        stack.setItemMeta(meta);
        return stack;
    }

    private static Component row(String label, String value) {
        return Component.text(label + ": ", NamedTextColor.GRAY)
                .decoration(TextDecoration.ITALIC, false)
                .append(Component.text(value, NamedTextColor.WHITE)
                        .decoration(TextDecoration.ITALIC, false));
    }

    private static String cap(String s) {
        return s.charAt(0) + s.substring(1).toLowerCase();
    }
}