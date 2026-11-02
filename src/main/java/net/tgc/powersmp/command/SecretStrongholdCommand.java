package net.tgc.powersmp.command;

import de.nikey.combatLog.Combat.CombatManager;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.tgc.powersmp.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

public class SecretStrongholdCommand implements BasicCommand {
    private final CombatManager combat;
    private Main plugin;

    public SecretStrongholdCommand(Main plugin, CombatManager combat) {
        this.combat = combat;
        this.plugin = plugin;
    }

    MiniMessage mm = MiniMessage.miniMessage();

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        if (!(source.getExecutor() instanceof Player p)) {
            source.getSender().sendRichMessage(
                    "<red>Only players can use this command.</red>"
            );
            return;
        }

        if (!p.hasPermission(Objects.requireNonNull(permission()))) {
            p.sendRichMessage(
                    "<red>You do not have permission to use this command.</red>"
            );
            return;
        }

        FileConfiguration dungeons = plugin.getDungeonsData();
        String worldName = dungeons.getString("dungeons.secret_stronghold.world");
        double x = dungeons.getDouble("dungeons.secret_stronghold.center.x");
        double y = dungeons.getDouble("dungeons.secret_stronghold.center.y");
        double z = dungeons.getDouble("dungeons.secret_stronghold.center.z");

        assert worldName != null;
        Location loc = new Location(Bukkit.getWorld(worldName), x, y, z);
        assert p != null;

        if (combat.isInCombat(p)) {
            Component parsed = mm.deserialize("<gray>[<yellow>PowerSMP<gray>] <red>You are not able to perform this command while in combat!");
            p.sendMessage(parsed);
            return;
        }

        p.teleport(loc);
    }

    @Override
    public boolean canUse(org.bukkit.command.CommandSender sender) {
        return sender.hasPermission(permission());
    }

    @Override
    public @Nullable String permission() {
        return "powersmp.fast_travel.secret_stronghold";
    }
}
