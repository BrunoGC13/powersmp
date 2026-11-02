package net.tgc.powersmp.menus;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.tgc.powersmp.Main;
import net.tgc.powersmp.economy.order.Order;
import net.tgc.powersmp.economy.order.OrderManager;
import net.tgc.powersmp.economy.order.OrderResult;
import net.tgc.powersmp.economy.order.OrderResultType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class OrderMenu extends Menu {

    private static final int MENU_SIZE = 54;
    private static final int ORDERS_PER_PAGE = 45;

    private static final int PREVIOUS_PAGE_SLOT = 45;
    private static final int MY_ORDERS_SLOT = 49;
    private static final int NEXT_PAGE_SLOT = 53;

    private final Main plugin;
    private final OrderManager orderManager;
    private final int page;
    private String prefix;

    private final MiniMessage mm = MiniMessage.miniMessage();

    public OrderMenu(Main plugin, OrderManager orderManager) {
        this(plugin, orderManager, 0);
    }

    public OrderMenu(
            Main plugin,
            OrderManager orderManager,
            int page
    ) {
        this.plugin = plugin;
        this.orderManager = orderManager;
        this.page = Math.max(0, page);

        this.prefix = plugin.getConfig().getString("plugin-prefix");

        this.setSize(MENU_SIZE);
        this.setTitle("Active Orders");

        List<Order> activeOrders = orderManager.getActiveOrders();

        int totalPages = Math.max(
                1,
                (int) Math.ceil(
                        activeOrders.size() / (double) ORDERS_PER_PAGE
                )
        );

        int currentPage = Math.min(this.page, totalPages - 1);

        int startIndex = currentPage * ORDERS_PER_PAGE;
        int endIndex = Math.min(
                startIndex + ORDERS_PER_PAGE,
                activeOrders.size()
        );

        for (int index = startIndex; index < endIndex; index++) {
            Order order = activeOrders.get(index);
            int slot = index - startIndex;

            this.addButton(createActiveOrderButton(order, slot));
        }

        this.addButton(new Button(MY_ORDERS_SLOT) {
            @Override
            public ItemStack getItem() {
                return createMyOrdersItem();
            }

            @Override
            public void onClick(Player player) {
                new MyOrdersMenu(
                        plugin,
                        orderManager,
                        player
                ).displayTo(player);
            }
        });

        if (currentPage > 0) {
            this.addButton(new Button(PREVIOUS_PAGE_SLOT) {
                @Override
                public ItemStack getItem() {
                    return createNavigationItem(
                            Material.ARROW,
                            "<yellow>Previous Page"
                    );
                }

                @Override
                public void onClick(Player player) {
                    new OrderMenu(
                            plugin,
                            orderManager,
                            currentPage - 1
                    ).displayTo(player);
                }
            });
        }

        if (currentPage < totalPages - 1) {
            this.addButton(new Button(NEXT_PAGE_SLOT) {
                @Override
                public ItemStack getItem() {
                    return createNavigationItem(
                            Material.ARROW,
                            "<yellow>Next Page"
                    );
                }

                @Override
                public void onClick(Player player) {
                    new OrderMenu(
                            plugin,
                            orderManager,
                            currentPage + 1
                    ).displayTo(player);
                }
            });
        }
    }

    private Button createActiveOrderButton(Order order, int slot) {
        return new Button(slot) {
            @Override
            public ItemStack getItem() {
                ItemStack item = order.getItem();
                ItemMeta meta = item.getItemMeta();

                List<Component> lore = new ArrayList<>();
                lore.add(Component.empty());
                lore.add(mm.deserialize(
                        "<gray>Delivered: <white>"
                                + order.getDelivered()
                                + "/"
                                + order.getAmount()
                ));
                lore.add(mm.deserialize(
                        "<gray>Payment per item: <yellow>"
                                + order.getPriceEach()
                                + " coins"
                ));
                lore.add(Component.empty());

                meta.lore(lore);
                item.setItemMeta(meta);

                return item;
            }

            @Override
            public void onClick(Player player) {
                if (order.getBuyer().equals(player.getUniqueId())) {
                    player.sendMessage(mm.deserialize(
                            prefix +
                            "<red>You cannot deliver items to your own order."
                    ));
                    return;
                }

                if (order.isComplete()) {
                    player.sendMessage(mm.deserialize(
                            prefix +
                            "<red>This order has already been completed."
                    ));
                    return;
                }

                new OrderDeliveryMenu(
                        plugin,
                        orderManager,
                        order
                ).displayTo(player);
            }
        };
    }

    private ItemStack createMyOrdersItem() {
        ItemStack item = new ItemStack(Material.CHEST);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(
                mm.deserialize("<gold>Your Orders")
        );

        meta.lore(List.of(
                mm.deserialize("<gray>View your placed orders"),
                mm.deserialize("<gray>and collect completed orders"),
                Component.empty(),
                mm.deserialize("<yellow>Click to open")
        ));

        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createNavigationItem(
            Material material,
            String name
    ) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(mm.deserialize(name));
        item.setItemMeta(meta);

        return item;
    }

    private static class MyOrdersMenu extends Menu {

        private final Main plugin;
        private final OrderManager orderManager;
        private final Player owner;

        private String prefix;

        private final MiniMessage mm = MiniMessage.miniMessage();

        private MyOrdersMenu(
                Main plugin,
                OrderManager orderManager,
                Player owner
        ) {
            this.plugin = plugin;
            this.orderManager = orderManager;
            this.owner = owner;

            this.prefix = plugin.getConfig().getString("plugin-prefix");

            this.setSize(MENU_SIZE);
            this.setTitle("Your Orders");

            List<Order> orders =
                    orderManager.getOrdersForBuyer(owner.getUniqueId());

            for (int index = 0; index < orders.size(); index++) {
                if (index >= ORDERS_PER_PAGE) {
                    break;
                }

                Order order = orders.get(index);

                this.addButton(new Button(index) {
                    @Override
                    public ItemStack getItem() {
                        return createPersonalOrderItem(order);
                    }

                    @Override
                    public void onClick(Player player) {
                        if (!order.isComplete()) {
                            player.sendMessage(mm.deserialize(
                                    prefix +
                                    "<red>This order is not complete yet."
                            ));
                            return;
                        }

                        OrderResult result =
                                orderManager.claimOrder(player, order);

                        if (result.getType() == OrderResultType.ERROR) {
                            player.sendMessage(mm.deserialize(
                                    prefix +
                                    "<red>" + result.getMsg()
                            ));
                            return;
                        }

                        player.sendMessage(mm.deserialize(
                                prefix +
                                "<white>You collected <green>"
                                        + order.getAmount()
                                        + "x "
                                        + order.getItem()
                                        .getType()
                                        .name()
                                        .toLowerCase()
                                        + "<green>."
                        ));

                        new MyOrdersMenu(
                                plugin,
                                orderManager,
                                player
                        ).displayTo(player);
                    }
                });
            }

            this.addButton(new Button(49) {
                @Override
                public ItemStack getItem() {
                    ItemStack item = new ItemStack(Material.ARROW);
                    ItemMeta meta = item.getItemMeta();

                    meta.displayName(
                            mm.deserialize("<yellow>Back to Active Orders")
                    );

                    item.setItemMeta(meta);
                    return item;
                }

                @Override
                public void onClick(Player player) {
                    new OrderMenu(
                            plugin,
                            orderManager
                    ).displayTo(player);
                }
            });
        }

        private ItemStack createPersonalOrderItem(Order order) {
            ItemStack item = order.getItem();

            ItemMeta meta = item.getItemMeta();
            List<Component> lore = new ArrayList<>();

            lore.add(Component.empty());
            lore.add(mm.deserialize(
                    "<gray>Delivered: <white>"
                            + order.getDelivered()
                            + "/"
                            + order.getAmount()
            ));

            lore.add(Component.empty());

            if (order.isComplete()) {
                lore.add(mm.deserialize(
                        "<green>Completed"
                ));
                lore.add(mm.deserialize(
                        "<yellow>Click to collect your items"
                ));
            } else {
                lore.add(mm.deserialize(
                        "<red>Still waiting for delivery"
                ));
            }

            meta.lore(lore);
            item.setItemMeta(meta);

            return item;
        }
    }
}