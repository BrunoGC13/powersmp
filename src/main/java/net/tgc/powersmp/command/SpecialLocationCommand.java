package net.tgc.powersmp.command;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.tgc.powersmp.Main;
import net.tgc.powersmp.other.LocationManager;
import net.tgc.powersmp.other.SpecialLocation;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

@SuppressWarnings("UnstableApiUsage")
public class SpecialLocationCommand implements BasicCommand {
    private final Main plugin;
    private final LocationManager locationManager;

    public SpecialLocationCommand(Main plugin) {
        this.plugin = plugin;
        this.locationManager = plugin.getLocationManager();
    }

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        if (!(source.getExecutor() instanceof Player player)) {
            source.getSender().sendRichMessage(
                    "<red>Only players can use this command.</red>"
            );
            return;
        }

        if (args.length == 0) {
            sendUsage(player);
            return;
        }

        String action = args[0].toLowerCase(Locale.ROOT);

        switch (action) {
            case "add" -> handleAdd(player, args);
            case "remove" -> handleRemove(player, args);
            case "tp" -> handleTeleport(player, args);
            default -> sendUsage(player);
        }
    }

    private void handleAdd(Player player, String[] args) {
        if (args.length < 2) {
            player.sendRichMessage(
                    "<red>Usage: /specialloc add <name></red>"
            );
            return;
        }

        String name = args[1].toLowerCase(Locale.ROOT);

        if (!name.matches("[a-z0-9_-]+")) {
            player.sendRichMessage(
                    "<red>Location names may only contain letters, numbers, "
                            + "underscores, and hyphens.</red>"
            );
            return;
        }

        if (locationManager.getLocation(name) != null) {
            player.sendRichMessage(
                    "<red>A special location with that name already exists.</red>"
            );
            return;
        }

        Location location = player.getLocation();

        if (!locationManager.addLocation(location, name)) {
            player.sendRichMessage(
                    "<red>Could not save the special location.</red>"
            );
            return;
        }

        player.sendRichMessage(
                "<green>Added special location <white>"
                        + name
                        + "</white>.</green>"
        );
    }

    private void handleRemove(Player player, String[] args) {
        if (args.length < 2) {
            player.sendRichMessage(
                    "<red>Usage: /specialloc remove <name></red>"
            );
            return;
        }

        String name = args[1].toLowerCase(Locale.ROOT);

        if (!locationManager.removeLocation(name)) {
            player.sendRichMessage(
                    "<red>No special location named <white>"
                            + name
                            + "</white> exists.</red>"
            );
            return;
        }

        player.sendRichMessage(
                "<green>Removed special location <white>"
                        + name
                        + "</white>.</green>"
        );
    }

    private void handleTeleport(Player player, String[] args) {
        if (args.length < 2) {
            player.sendRichMessage(
                    "<red>Usage: /specialloc tp <name></red>"
            );
            return;
        }

        String name = args[1].toLowerCase(Locale.ROOT);
        SpecialLocation specialLocation = locationManager.getLocation(name);

        if (specialLocation == null) {
            player.sendRichMessage(
                    "<red>No special location named <white>"
                            + name
                            + "</white> exists.</red>"
            );
            return;
        }

        Location location =
                locationManager.resolveLocation(specialLocation);

        if (location == null) {
            player.sendRichMessage(
                    "<red>The world for this location is not loaded.</red>"
            );
            return;
        }

        if (!player.teleport(location)) {
            player.sendRichMessage(
                    "<red>Teleportation failed.</red>"
            );
            return;
        }

        player.sendRichMessage(
                "<green>Teleported to <white>"
                        + name
                        + "</white>.</green>"
        );
    }

    private void sendUsage(Player player) {
        player.sendRichMessage(
                "<yellow>Usage: "
                        + "<white>/specialloc add <name></white>, "
                        + "<white>/specialloc remove <name></white>, or "
                        + "<white>/specialloc tp <name></white>"
        );
    }

    @Override
    public Collection<String> suggest(
            CommandSourceStack source,
            String[] args
    ) {
        if (args.length == 1) {
            return List.of("add", "remove", "tp");
        }

        if (args.length == 2
                && (args[0].equalsIgnoreCase("remove")
                || args[0].equalsIgnoreCase("tp"))) {
            return locationManager.getLocationNames();
        }

        return List.of();
    }

    @Override
    public @Nullable String permission() {
        return "powersmp.specialloc.command";
    }

    @Override
    public boolean canUse(org.bukkit.command.CommandSender sender) {
        return sender.hasPermission(permission()) || sender.isOp();
    }
}