package net.tgc.powersmp.command;

import de.nikey.combatLog.Combat.CombatManager;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

public class SpawnCommand implements BasicCommand {
    private final CombatManager combat;
    public SpawnCommand(CombatManager combat) {
        this.combat = combat;
    }

    MiniMessage mm = MiniMessage.miniMessage();

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        Player p = (Player) source.getExecutor();
        Location loc = new Location(Bukkit.getWorld("world"), 61, 75, -219);
        assert p != null;

        if (combat.isInCombat(p)) {
            Component parsed = mm.deserialize("<gray>[<yellow>PowerSMP<gray>] <red>You are not able to perform this command while in combat!");
            p.sendMessage(parsed);
            return;
        }

        p.teleport(loc);
    }

    @Override
    public @Nullable String permission() {
        return "powersmp.fast_travel.spawn";
    }
}
