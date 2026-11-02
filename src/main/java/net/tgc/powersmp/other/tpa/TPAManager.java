package net.tgc.powersmp.other.tpa;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.tgc.powersmp.Main;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class TPAManager {
    private final Main plugin;
    private final String prefix;

    private final MiniMessage mm = MiniMessage.miniMessage();

    Map<Integer, TPA> requests = new HashMap<>();
    int id = 0;

    public TPAManager(Main plugin) {
        this.plugin = plugin;
        this.prefix = plugin.getConfig().getString("plugin-prefix");
    }

    public TPA getPendingRequest(Player target) {
        TPA latestRequest = null;

        for (TPA request : requests.values()) {
            if (!request.getTarget().getUniqueId().equals(target.getUniqueId())) {
                continue;
            }

            if (latestRequest == null || request.getId() > latestRequest.getId()) {
                latestRequest = request;
            }
        }

        return latestRequest;
    }

    public TPA sendRequest(Player sender, Player target, TPAType type) {
        TPA tpa = null;
        switch (type) {
            case TPA -> tpa = handleTPA(sender, target);
            case TPHERE -> tpa = handleTPHere(sender, target);
        }

        return tpa;
    }

    public TPA acceptRequest(TPA tpa) {
        if (tpa == null || !requests.containsKey(tpa.getId())) {
            return new TPA(tpa.getSender(), tpa.getTarget(), tpa.getType(), tpa.getId()).setAccepted(false);
        }

        requests.remove(tpa.getId());

        if (tpa.getType() == TPAType.TPA) {
            // The target accepted a request to teleport to them.
            tpa.getSender().teleport(tpa.getTarget().getLocation());
        } else {
            // The target accepted a request for the sender to teleport them.
            tpa.getTarget().teleport(tpa.getSender().getLocation());
        }

        tpa.getTarget().sendMessage(mm.deserialize(prefix + "<green>Teleport request accepted!"));

        return tpa.setAccepted(true);
    }

    public TPA denyRequest(TPA tpa) {
        if (tpa == null || !requests.containsKey(tpa.getId())) {
            return new TPA(tpa.getSender(), tpa.getTarget(), tpa.getType(), tpa.getId()).setAccepted(false);
        }

        requests.remove(tpa.getId());

        tpa.getSender().sendMessage(mm.deserialize(prefix + "<white>Your tpa request to <green>" + tpa.getTarget().getName() + " <white>has been <red>denied!"));
        tpa.getTarget().sendMessage(mm.deserialize(prefix + "<red>Teleport request denied!"));

        return tpa.setAccepted(false);
    }

    private TPA handleTPA(Player sender, Player target) {
        target.sendMessage(mm.deserialize(prefix + "<green>" + sender.getName() + " <white>sent you a tpa request!"));

        int newID = id++;
        TPA tpa = new TPA(sender, target, TPAType.TPA, newID);
        requests.put(newID, tpa);
        return tpa;
    }

    private TPA handleTPHere(Player sender, Player target) {
        target.sendMessage(mm.deserialize(prefix + "<green>" + sender.getName() + " <white>sent you a tp here request!"));

        int newID = id++;
        TPA tpa = new TPA(sender, target, TPAType.TPHERE, newID);
        requests.put(newID, tpa);
        return tpa;
    }
}
