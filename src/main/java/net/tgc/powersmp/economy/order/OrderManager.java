
package net.tgc.powersmp.economy.order;

import net.milkbowl.vault.economy.EconomyResponse;
import net.tgc.powersmp.Main;
import net.tgc.powersmp.economy.CoinsEconomy;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class OrderManager {

    private final Main plugin;
    private final CoinsEconomy eco;
    private final File file;
    private YamlConfiguration data;

    private final Map<Long, Order> orders = new HashMap<>();

    private final Map<Material, List<Order>> ordersByMaterial =
            new HashMap<>();

    private long nextId = 1;

    public OrderManager(Main plugin, CoinsEconomy eco) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "orders.yml");
        this.eco = eco;

        loadOrders();
    }

    public OrderResult createOrder(Player player, ItemStack item, int amount, int priceEach) {
        if (player == null || item == null || item.getType().isAir()
                || amount <= 0 || priceEach <= 0) {
            return new OrderResult(null, OrderResultType.ERROR,
                    "Invalid order parameters.");
        }

        long totalCost = (long) amount * priceEach;

        // Vault's economy API uses double amounts.
        if (totalCost > Integer.MAX_VALUE) {
            return new OrderResult(null, OrderResultType.ERROR,
                    "Order cost is too high.");
        }

        EconomyResponse ecoRes = eco.withdrawPlayer(
                player,
                (double) totalCost
        );

        if (!ecoRes.transactionSuccess()) {
            return new OrderResult(null, OrderResultType.ERROR,
                    ecoRes.errorMessage != null
                            ? ecoRes.errorMessage
                            : "Insufficient funds."
            );
        }

        long id = nextId++;

        Order order = new Order(
                id,
                item,
                amount,
                priceEach,
                player.getUniqueId(),
                0
        );

        orders.put(id, order);

        ordersByMaterial
                .computeIfAbsent(
                        item.getType(),
                        material -> new ArrayList<>()
                )
                .add(order);

        saveOrders();

        return new OrderResult(order, OrderResultType.SUCCESS);
    }

    public OrderResult sendItems(Order order, Player sender, ItemStack item) {
        if (item == null || item.getType().isAir()
                || sender == null || order == null) {
            return new OrderResult(
                    order, OrderResultType.ERROR, "Invalid order or item."
            );
        }

        if (orders.get(order.getId()) != order) {
            return new OrderResult(
                    order, OrderResultType.ERROR, "Order not found."
            );
        }

        if (order.isComplete()) {
            return new OrderResult(
                    order, OrderResultType.ERROR, "Order is already complete."
            );
        }

        if (order.getItem().getType() != item.getType()) {
            return new OrderResult(
                    order, OrderResultType.ERROR, "Materials do not match."
            );
        }

        int itemAmount = item.getAmount();
        int accepted = Math.min(itemAmount, order.getRemaining());
        int overflow = itemAmount - accepted;

        double payment = (double) accepted * order.getPriceEach();

        EconomyResponse ecoRes = eco.depositPlayer(sender, payment);

        if (!ecoRes.transactionSuccess()) {
            return new OrderResult(
                    order,
                    OrderResultType.ERROR,
                    ecoRes.errorMessage != null
                            ? ecoRes.errorMessage
                            : "Payment failed."
            );
        }

        // Return excess items, if any.
        if (overflow > 0) {
            ItemStack overflowItem = item.clone();
            overflowItem.setAmount(overflow);

            Map<Integer, ItemStack> leftovers =
                    sender.getInventory().addItem(overflowItem);

            // Drop any remaining items so they are not lost.
            leftovers.values().forEach(leftover ->
                    sender.getWorld().dropItemNaturally(
                            sender.getLocation(), leftover
                    )
            );



//            OrderResult result = finishOrder(order);
//            if (result.getType() == OrderResultType.ERROR) {
//                return new OrderResult(order, OrderResultType.ERROR, result.getMsg());
//            }
        }

        order.deliver(accepted);
        saveOrders();

        return new OrderResult(order, OrderResultType.SUCCESS);
    }

//    public OrderResult finishOrder(Order order) {
//        Player owner = Bukkit.getPlayer(order.getBuyer());
//        if (owner == null) {
//            return new OrderResult(order, OrderResultType.ERROR, "Owner of order is null");
//        }
//
//
//    }

    public Order getOrder(long id) {
        return orders.get(id);
    }

    public Optional<Order> getBestPayingOrder(Material material) {
        return getOrders(material).stream()
                .filter(order -> !order.isComplete())
                .max(Comparator.comparingInt(Order::getPriceEach));
    }

    public void loadOrders() {
        orders.clear();
        ordersByMaterial.clear();
        nextId = 1;

        if (!file.exists()) {
            plugin.getDataFolder().mkdirs();
            data = new YamlConfiguration();
            saveOrders();
            return;
        }

        data = YamlConfiguration.loadConfiguration(file);

        ConfigurationSection section =
                data.getConfigurationSection("orders");

        if (section == null) {
            return;
        }

        for (String key : section.getKeys(false)) {
            try {
                long id = Long.parseLong(key);
                String path = "orders." + key;

                UUID buyer = UUID.fromString(
                        data.getString(path + ".buyer", "")
                );

                ItemStack item =
                        data.getItemStack(path + ".item");

                int amount = data.getInt(path + ".amount");
                int priceEach = data.getInt(path + ".priceEach");
                int delivered = data.getInt(path + ".delivered");

                if (item == null || item.getType().isAir()
                        || amount <= 0 || priceEach <= 0
                        || delivered < 0 || delivered > amount) {
                    plugin.getLogger().warning(
                            "Skipping invalid order " + key
                    );
                    continue;
                }

                Order order = new Order(
                        id,
                        item,
                        amount,
                        priceEach,
                        buyer,
                        delivered
                );

                orders.put(id, order);

                ordersByMaterial
                        .computeIfAbsent(
                                item.getType(),
                                material -> new ArrayList<>()
                        )
                        .add(order);

                nextId = Math.max(nextId, id + 1);

            } catch (IllegalArgumentException ex) {
                plugin.getLogger().warning(
                        "Could not load order " + key
                                + ": " + ex.getMessage()
                );
            }
        }

        plugin.getLogger().info(
                "Loaded " + orders.size() + " orders."
        );
    }

    public void saveOrders() {
        if (data == null) {
            data = new YamlConfiguration();
        }

        data.set("orders", null);

        for (Order order : orders.values()) {
            String path = "orders." + order.getId();

            data.set(path + ".buyer",
                    order.getBuyer().toString());
            data.set(path + ".item", order.getItem());
            data.set(path + ".amount", order.getAmount());
            data.set(path + ".priceEach", order.getPriceEach());
            data.set(path + ".delivered", order.getDelivered());
        }

        try {
            if (!plugin.getDataFolder().exists()) {
                plugin.getDataFolder().mkdirs();
            }

            data.save(file);
        } catch (IOException ex) {
            plugin.getLogger().severe(
                    "Failed to save orders.yml: "
                            + ex.getMessage()
            );
            ex.printStackTrace();
        }
    }

    public List<Order> getOrders(Material material) {
        return new ArrayList<>(
                ordersByMaterial.getOrDefault(
                        material,
                        Collections.emptyList()
                )
        );
    }

    public List<Order> getActiveOrders() {
        return orders.values()
                .stream()
                .filter(order -> !order.isComplete())
                .sorted(Comparator.comparingLong(Order::getId))
                .toList();
    }

    public List<Order> getOrdersForBuyer(UUID buyer) {
        return orders.values()
                .stream()
                .filter(order -> order.getBuyer().equals(buyer))
                .sorted(Comparator.comparingLong(Order::getId))
                .toList();
    }

    public OrderResult claimOrder(Player player, Order order) {
        if (player == null || order == null) {
            return new OrderResult(
                    order,
                    OrderResultType.ERROR,
                    "Invalid player or order."
            );
        }

        Order storedOrder = orders.get(order.getId());

        if (storedOrder != order) {
            return new OrderResult(
                    order,
                    OrderResultType.ERROR,
                    "Order not found."
            );
        }

        if (!storedOrder.getBuyer().equals(player.getUniqueId())) {
            return new OrderResult(
                    order,
                    OrderResultType.ERROR,
                    "You do not own this order."
            );
        }

        if (!storedOrder.isComplete()) {
            return new OrderResult(
                    order,
                    OrderResultType.ERROR,
                    "This order is not complete yet."
            );
        }

        ItemStack reward = storedOrder.getItem();
        reward.setAmount(storedOrder.getAmount());

        Map<Integer, ItemStack> leftovers =
                player.getInventory().addItem(reward);

        leftovers.values().forEach(leftover ->
                player.getWorld().dropItemNaturally(
                        player.getLocation(),
                        leftover
                )
        );

        orders.remove(storedOrder.getId());

        List<Order> materialOrders =
                ordersByMaterial.get(storedOrder.getItem().getType());

        if (materialOrders != null) {
            materialOrders.remove(storedOrder);

            if (materialOrders.isEmpty()) {
                ordersByMaterial.remove(storedOrder.getItem().getType());
            }
        }

        saveOrders();

        return new OrderResult(
                storedOrder,
                OrderResultType.SUCCESS
        );
    }
}
