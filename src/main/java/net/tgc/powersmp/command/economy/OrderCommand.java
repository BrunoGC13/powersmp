package net.tgc.powersmp.command.economy;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.tgc.powersmp.Main;
import net.tgc.powersmp.economy.order.OrderDialog;
import net.tgc.powersmp.economy.order.OrderManager;
import net.tgc.powersmp.economy.order.OrderResult;
import net.tgc.powersmp.economy.order.OrderResultType;
import net.tgc.powersmp.menus.OrderMenu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class OrderCommand implements BasicCommand {
    private Main plugin;
    private OrderManager orderManager;
    private String prefix;
    MiniMessage mm = MiniMessage.miniMessage();

    public OrderCommand(Main plugin) {
        this.plugin = plugin;
        this.orderManager = plugin.getOrderManager();
        this.prefix = plugin.getConfig().getString("plugin-prefix");
    }

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        if (!(source.getExecutor() instanceof Player p)) {
            source.getSender().sendRichMessage(
                    "<red>Only players can use this command.</red>"
            );
            return;
        }

        if (args.length == 0) {
            openOrdersMenu(p);
            return;
        }

        String subCommand = args[0].toLowerCase(java.util.Locale.ROOT);

        if (subCommand.equals("create")) {
            openCreateDialog(p);
            return;
        }

        p.sendMessage(mm.deserialize(
                prefix + "<red>Unknown sub-command. Use <white>/order</white> "
                        + "or <white>/order create</white>."
        ));
    }

    @Override
    public Collection<String> suggest(CommandSourceStack source, String[] args) {
        if (args.length == 1) {
            return List.of("create");
        }

        return List.of();
    }

// ... existing code ...

    @Override
    public @Nullable String permission() {
        return "powersmp.command.order";
    }

    // Sub Functions
//    private void handleCreation(CommandSourceStack source, Player p, String[] args, OrderManager manager) {
//        if (args.length < 4) {
//            p.sendMessage(mm.deserialize(prefix + "<red>Usage: /order create <material> <amount> <price per item>"));
//            return;
//        }
//
//        Material mat = Material.getMaterial(args[1].toLowerCase());
//        if (mat == null) {
//            p.sendMessage(prefix + "<red>Invalid material: " + args[1].toLowerCase());
//            return;
//        }
//
//        int amount = Integer.parseInt(args[2]);
//        int pricePerItem = Integer.parseInt(args[3]);
//
//        ItemStack item = new ItemStack(mat);
//
//        OrderResult result = manager.createOrder(p, item, amount, pricePerItem);
//        if (result.getType().equals(OrderResultType.ERROR)) {
//            p.sendMessage(prefix + "<red>An error occurred: " + result.getMsg());
//        }
//
//        p.sendMessage(prefix + "<white>Created a order for <green>" + result.getOrder().getAmount() + " " + result.getOrder().getItem().displayName() + ". <white>A total of <yellow>" + result.getOrder().getCost() + " coins <white>has been withdrawn from your account.");
//    }

    private void openCreateDialog(Player p) {
        p.showDialog(new OrderDialog(plugin, orderManager).getDialog());
    }

    private void openOrdersMenu(Player p) {
        new OrderMenu(plugin, orderManager).displayTo(p);
    }
}
