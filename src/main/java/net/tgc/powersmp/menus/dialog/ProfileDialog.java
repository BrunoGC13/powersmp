package net.tgc.powersmp.menus.dialog;

import de.nikey.combatLog.Combat.CombatManager;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.tgc.powersmp.Main;
import net.tgc.powersmp.levelSystem.LevelManager;
import net.tgc.powersmp.player.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.OptionalInt;

@SuppressWarnings("UnstableApiUsage")
public class ProfileDialog extends BasicDialog {
    private static final int XP_REQUIRED_PER_LEVEL = 100;

    private final MiniMessage mm = MiniMessage.miniMessage();
    private final Main plugin;
    private final CombatManager combatManager;

    public ProfileDialog(Player p, Main plugin, boolean thirdPerspective) {
        this.plugin = plugin;
        this.combatManager = plugin.getCombatManager();

        PlayerDataManager pdm = PlayerDataManager.getInstance();
        LevelManager levelManager = plugin.getLevelManager();

        int level = levelManager.getLevel(p);
        int xp = levelManager.getXp(p);
        int xpNeeded = Math.max(0, XP_REQUIRED_PER_LEVEL - xp);

        int hearts = pdm.getHearts(p);
        double power = pdm.getPower(p);
        double maxPower = pdm.getMaxPower(p);
        double coins = plugin.getCoinsEconomy()
                .getBalance(Bukkit.getOfflinePlayer(p.getUniqueId()));
        double netWorth = plugin.getNetWorthManager().getNetworth(p).getAmount();

        this.setTitle(mm.deserialize(
                "<gradient:#ffd54a:#ff9f43><bold>✦ " + p.getName() + "'s Profile</bold></gradient>"
        ));
        this.setCloseableEsc(true);
        this.setType(DialogType.notice(new BasicActionButton()
                .setTitle(mm.deserialize("<red><bold>CLOSE</bold>"))
                .setAction(DialogAction.customClick(
                        Key.key("powersmp:profile_menu"), null
                ))
                .getButton()
        ));

        /*
         * A player head cannot currently be displayed as a dialog body in the same
         * way as an inventory item. The player's name and profile header are used
         * instead, while the rest of the dialog remains compatible with Paper's
         * dialog API.
         */
        this.addBody(DialogBody.plainMessage(mm.deserialize(
                "<dark_gray>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</dark_gray>"
        )));
        this.addBody(DialogBody.plainMessage(mm.deserialize(
                "<gray>        <yellow>👤</yellow> <white><bold>" + p.getName()
                        + "</bold></white>"
                        + " <dark_gray>•</dark_gray> <gray>Player Profile</gray>"
        )));
        this.addBody(DialogBody.plainMessage(mm.deserialize(
                "<dark_gray>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</dark_gray>"
        )));

        this.addBody(DialogBody.plainMessage(mm.deserialize(
                "<yellow><bold>★ LEVEL PROGRESS</bold></yellow>"
        )));
        this.addBody(DialogBody.plainMessage(mm.deserialize(
                "<gray>Current level: <green><bold>" + level + "</bold></green>"
        )));
        this.addBody(DialogBody.plainMessage(mm.deserialize(
                "<gray>Experience: <aqua>" + xp + "</aqua> <dark_gray>/</dark_gray> "
                        + "<white>" + XP_REQUIRED_PER_LEVEL + "</white>"
        )));
        this.addBody(DialogBody.plainMessage(mm.deserialize(
                "<gray>Progress: " + progressBar(xp, XP_REQUIRED_PER_LEVEL, 18)
        )));
        this.addBody(DialogBody.plainMessage(mm.deserialize(
                "<gray>XP needed for level " + (level + 1) + ": "
                        + "<gold><bold>" + xpNeeded + "</bold></gold>"
        )));
        this.addBody(DialogBody.plainMessage(mm.deserialize(
                "<dark_gray>Level-up reward: <gold>"
                        + format(levelManager.getNextLevelCoins(p))
                        + " coins</gold>"
        )));

        this.addBody(DialogBody.plainMessage(mm.deserialize("")));
        this.addBody(DialogBody.plainMessage(mm.deserialize(
                "<blue><bold>⚔ PLAYER STATS</bold></blue>"
        )));
        this.addBody(DialogBody.plainMessage(mm.deserialize(
                "<gray>Hearts: <red>" + hearts + " ❤</red>"
        )));
        this.addBody(DialogBody.plainMessage(mm.deserialize(
                "<gray>Power: <blue>" + format(power) + " / " + format(maxPower)
                        + " ⚡</blue>"
        )));
        this.addBody(DialogBody.plainMessage(mm.deserialize(
                "<gray>Coins: <yellow>" + format(coins) + "</yellow>"
        )));
        this.addBody(DialogBody.plainMessage(mm.deserialize(
                "<gray>Net worth: <green>" + format(netWorth) + "</green>"
        )));

        this.addBody(DialogBody.plainMessage(mm.deserialize("")));
        this.addBody(DialogBody.plainMessage(mm.deserialize(
                "<dark_purple><bold>◆ STATUS</bold></dark_purple>"
        )));

        addCombatStatus(p);

        if (!thirdPerspective) {
            this.addBody(DialogBody.plainMessage(mm.deserialize("")));
            this.addInput(DialogInput.bool(
                    "powerbar",
                    mm.deserialize("<gray>Show power bar"),
                    pdm.isPowerBarEnabled(p),
                    "",
                    ""
            ));
        }
    }

    private void addCombatStatus(Player player) {
        if (combatManager == null) {
            plugin.getLogger().warning("ProfileDialog opened while CombatManager was null.");
            this.addBody(DialogBody.plainMessage(mm.deserialize(
                    "<gray>Combat status: <red>Unavailable"
            )));
            return;
        }

        boolean inCombat = combatManager.isInCombat(player);
        if (!inCombat) {
            this.addBody(DialogBody.plainMessage(mm.deserialize(
                    "<gray>Combat status: <green>Safe"
            )));
            return;
        }

        this.addBody(DialogBody.plainMessage(mm.deserialize(
                "<gray>Combat status: <red><bold>In combat</bold>"
        )));

        OptionalInt remaining = combatManager.getRemainingCombatSeconds(player);
        if (remaining.isPresent()) {
            this.addBody(DialogBody.plainMessage(mm.deserialize(
                    "<gray>Time remaining: <gold>" + remaining.getAsInt() + "s"
            )));
        }
    }

    private static String progressBar(int current, int maximum, int length) {
        int safeMaximum = Math.max(1, maximum);
        int safeCurrent = Math.max(0, Math.min(current, safeMaximum));
        int filled = (int) Math.round(
                safeCurrent / (double) safeMaximum * length
        );

        StringBuilder bar = new StringBuilder("<green>");
        for (int i = 0; i < length; i++) {
            bar.append(i < filled ? "■" : "<dark_gray>■</dark_gray>");
        }

        return bar.append("</green> <gray>")
                .append(safeCurrent)
                .append("/")
                .append(safeMaximum)
                .toString();
    }

    private static String format(double value) {
        if (value == Math.rint(value)) {
            return String.format("%.0f", value);
        }
        return String.format("%.2f", value);
    }
}