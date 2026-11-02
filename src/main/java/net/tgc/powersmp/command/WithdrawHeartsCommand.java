package net.tgc.powersmp.command;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.tgc.powersmp.Main;
import net.tgc.powersmp.api.item.CustomItemRegistry;
import net.tgc.powersmp.item.HeartItem;
import net.tgc.powersmp.player.HeartManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class WithdrawHeartsCommand implements BasicCommand {
    private Main plugin;
    MiniMessage mm = MiniMessage.miniMessage();
    String prefix;

    public WithdrawHeartsCommand(Main plugin) {
        this.plugin = plugin;
        this.prefix = plugin.getConfig().getString("plugin-prefix");
    }

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        Player p = (Player) source.getExecutor();
        if (args[0] == null) {
            assert p != null;
            p.sendMessage(mm.deserialize(prefix + "<red>Usage: /whearts <amount>"));
        }

        int amount;
        try {
            amount = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            assert p != null;
            p.sendMessage(mm.deserialize(prefix + "<red>Please enter a valid integer amount."));
            return;
        }

        if (amount <= 0) {
            p.sendMessage(mm.deserialize(prefix + "<red>Amount must be greater than zero."));
            return;
        }

        p.sendMessage(mm.deserialize(prefix + "<white>Withdrawing <green>" + amount + " <white>heart(s)..."));
        HeartManager.removeHearts(p, amount);

        HeartItem heartItem = new HeartItem(plugin);
        ItemStack item = CustomItemRegistry.getInstance().createItemStack(heartItem.getKey());
        item.setAmount(amount);

        p.getInventory().addItem(item);
    }

    @Override
    public Collection<String> suggest(CommandSourceStack commandSourceStack, String[] args) {
        List<String> suggestions = new ArrayList<>();
        suggestions.add("1");
        suggestions.add("2");
        suggestions.add("4");
        suggestions.add("6");
        suggestions.add("8");
        suggestions.add("10");
        return suggestions;
    }

    @Override
    public @Nullable String permission() {
        return "powersmp.commands.hearts.withdraw";
    }
}
