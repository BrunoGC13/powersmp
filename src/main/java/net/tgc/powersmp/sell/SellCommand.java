package net.tgc.powersmp.sell;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.core.component.DataComponentType;
import net.tgc.powersmp.Main;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public class SellCommand implements BasicCommand {
    private Main plugin;
    public SellCommand(Main plugin) {
        this.plugin = plugin;
    }

    MiniMessage mm = MiniMessage.miniMessage();

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        if (!(source.getExecutor() instanceof Player p)) return;

        ItemStack item = p.getInventory().getItemInMainHand();

        if (item.getType().isAir() || item.getType() == Material.AIR) {
            p.sendMessage(mm.deserialize(plugin.getConfig().getString("plugin-prefix") + "<white>Cannot sell air!"));
            return;
        }

        SellResult result = plugin.getSellManager().sell(item, p);
        if (!(result.isSuccess())) {
            if (result.hasMessage()) {
                p.sendMessage(result.getMessage());
                return;
            }
            p.sendMessage(mm.deserialize(plugin.getConfig().getString("plugin-prefix") + "<red>An error occurred!"));
        }
        assert result.getItem().getData(DataComponentTypes.ITEM_NAME) != null;
        String itemName;

        if (result.getItem().getItemMeta().hasDisplayName()) {
            itemName = result.getItem().getItemMeta().getDisplayName();
        } else {
            itemName = result.getItem().getType().name().toLowerCase();
        }

        p.sendMessage(mm.deserialize(
                plugin.getConfig().getString("plugin-prefix")
                        + "<white>Sold <green>"
                        + result.getAmount()
                        + " "
                        + itemName
                        + " <white>you earned <blue>"
                        + result.getPrice()
        ));
    }

    @Override
    public @Nullable String permission() {
        return "powersmp.commands.sell";
    }
}
