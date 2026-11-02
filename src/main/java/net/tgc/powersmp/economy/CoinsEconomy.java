package net.tgc.powersmp.economy;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.tgc.powersmp.Main;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.List;

public class CoinsEconomy implements Economy {
    private final Main plugin;

    public CoinsEconomy(Main plugin) {
        this.plugin = plugin;
    }

    private EconomyResponse notSupported() {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not supported.");
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return "Coins";
    }

    @Override
    public boolean hasBankSupport() {
        return true;
    }

    @Override
    public int fractionalDigits() {
        return 0;
    }

    @Override
    public String format(double amount) {
        return (int) amount + " Coins";
    }

    @Override
    public String currencyNamePlural() {
        return "Coins";
    }

    @Override
    public String currencyNameSingular() {
        return "Coin";
    }

    @Override
    public boolean hasAccount(String playerName) {
        return false;
    }

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        return plugin.getPlayerData().contains("players." + player.getUniqueId() + ".coins");
    }

    @Override
    public boolean hasAccount(String playerName, String worldName) {
        return false;
    }

    @Override
    public boolean hasAccount(OfflinePlayer player, String worldName) {
        return hasAccount(player);
    }

    @Override
    public double getBalance(String playerName) {
        return 0;
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        return plugin.getPlayerData().getDouble("players." + player.getUniqueId() + ".coins", 0);
    }

    @Override
    public double getBalance(String playerName, String world) {
        return 0;
    }

    @Override
    public double getBalance(OfflinePlayer player, String world) {
        return getBalance(player);
    }

    @Override
    public boolean has(String playerName, double amount) {
        return false;
    }

    @Override
    public boolean has(OfflinePlayer player, double amount) {
        return getBalance(player) >= amount;
    }

    @Override
    public boolean has(String playerName, String worldName, double amount) {
        return false;
    }

    @Override
    public boolean has(OfflinePlayer player, String worldName, double amount) {
        return has(player, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        return null;
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        if (amount < 0) {
            return new EconomyResponse(0, getBalance(player),
                    EconomyResponse.ResponseType.FAILURE, "Cannot withdraw negative amount.");
        }
        if (!has(player, amount)) {
            return new EconomyResponse(0, getBalance(player),
                    EconomyResponse.ResponseType.FAILURE, "Insufficient funds.");
        }
        double newBalance = getBalance(player) - amount;
        plugin.getPlayerData().set("players." + player.getUniqueId() + ".coins", newBalance);
        plugin.savePlayerData();
        return new EconomyResponse(amount, newBalance, EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
        return null;
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {
        return withdrawPlayer(player, amount);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        return null;
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        if (amount < 0) {
            return new EconomyResponse(0, getBalance(player),
                    EconomyResponse.ResponseType.FAILURE, "Cannot deposit negative amount.");
        }
        double newBalance = getBalance(player) + amount;
        plugin.getPlayerData().set("players." + player.getUniqueId() + ".coins", newBalance);
        plugin.savePlayerData();
        return new EconomyResponse(amount, newBalance, EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
        return null;
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) {
        return depositPlayer(player, amount);
    }

    private boolean bankExists(String name) {
        return plugin.getBanksData().contains("banks." + name);
    }

    private double getBankBalance(String name) {
        return plugin.getBanksData().getDouble("banks." + name + ".bank-balance", 0);
    }

    @Override
    public EconomyResponse createBank(String name, String player) {
        return createBank(name, Bukkit.getOfflinePlayer(player));
    }

    @Override
    public EconomyResponse createBank(String name, OfflinePlayer player) {

        if (bankExists(name)) {
            return new EconomyResponse(
                    0,
                    0,
                    EconomyResponse.ResponseType.FAILURE,
                    "Bank already exists."
            );
        }

        plugin.getBanksData().set("banks." + name + ".name", name);
        plugin.getBanksData().set("banks." + name + ".owner", player.getUniqueId().toString());
        plugin.getBanksData().set(
                "banks." + name + ".interest-rate",
                plugin.getConfig().getDouble("banks-interest-rate")
        );
        plugin.getBanksData().set("banks." + name + ".bank-balance", 0D);

        plugin.saveBanksData();

        return new EconomyResponse(
                0,
                0,
                EconomyResponse.ResponseType.SUCCESS,
                null
        );
    }

    @Override
    public EconomyResponse deleteBank(String name) {

        if (!bankExists(name)) {
            return new EconomyResponse(
                    0,
                    0,
                    EconomyResponse.ResponseType.FAILURE,
                    "Bank does not exist."
            );
        }

        plugin.getBanksData().set("banks." + name, null);
        plugin.saveBanksData();

        return new EconomyResponse(
                0,
                0,
                EconomyResponse.ResponseType.SUCCESS,
                null
        );
    }

    @Override
    public EconomyResponse bankBalance(String name) {

        if (!bankExists(name)) {
            return new EconomyResponse(
                    0,
                    0,
                    EconomyResponse.ResponseType.FAILURE,
                    "Bank does not exist."
            );
        }

        return new EconomyResponse(
                0,
                getBankBalance(name),
                EconomyResponse.ResponseType.SUCCESS,
                null
        );
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {

        if (!bankExists(name)) {
            return new EconomyResponse(
                    0,
                    0,
                    EconomyResponse.ResponseType.FAILURE,
                    "Bank does not exist."
            );
        }

        if (amount < 0) {
            return new EconomyResponse(
                    0,
                    getBankBalance(name),
                    EconomyResponse.ResponseType.FAILURE,
                    "Amount cannot be negative."
            );
        }

        double currentBalance = getBankBalance(name);

        if (currentBalance < amount) {
            return new EconomyResponse(
                    amount,
                    currentBalance,
                    EconomyResponse.ResponseType.FAILURE,
                    "The bank does not have enough funds."
            );
        }

        return new EconomyResponse(
                amount,
                currentBalance,
                EconomyResponse.ResponseType.SUCCESS,
                null
        );
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {

        if (!bankExists(name)) {
            return new EconomyResponse(
                    0,
                    0,
                    EconomyResponse.ResponseType.FAILURE,
                    "Bank does not exist."
            );
        }

        if (amount < 0) {
            return new EconomyResponse(
                    0,
                    getBankBalance(name),
                    EconomyResponse.ResponseType.FAILURE,
                    "Amount cannot be negative."
            );
        }

        double currentBalance = getBankBalance(name);

        if (currentBalance < amount) {
            return new EconomyResponse(
                    amount,
                    currentBalance,
                    EconomyResponse.ResponseType.FAILURE,
                    "Insufficient bank funds."
            );
        }

        double newBalance = currentBalance - amount;

        plugin.getBanksData().set(
                "banks." + name + ".bank-balance",
                newBalance
        );

        plugin.saveBanksData();

        return new EconomyResponse(
                amount,
                newBalance,
                EconomyResponse.ResponseType.SUCCESS,
                null
        );
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {

        if (!bankExists(name)) {
            return new EconomyResponse(
                    0,
                    0,
                    EconomyResponse.ResponseType.FAILURE,
                    "Bank does not exist."
            );
        }

        if (amount < 0) {
            return new EconomyResponse(
                    0,
                    getBankBalance(name),
                    EconomyResponse.ResponseType.FAILURE,
                    "Amount cannot be negative."
            );
        }

        double currentBalance = getBankBalance(name);
        double newBalance = currentBalance + amount;

        plugin.getBanksData().set(
                "banks." + name + ".bank-balance",
                newBalance
        );

        plugin.saveBanksData();

        return new EconomyResponse(
                amount,
                newBalance,
                EconomyResponse.ResponseType.SUCCESS,
                null
        );
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        return isBankOwner(name, Bukkit.getOfflinePlayer(playerName));
    }

    @Override
    public EconomyResponse isBankOwner(String name, OfflinePlayer player) {

        if (!bankExists(name)) {
            return new EconomyResponse(
                    0,
                    0,
                    EconomyResponse.ResponseType.FAILURE,
                    "Bank does not exist."
            );
        }

        String ownerUuid = plugin.getBanksData().getString(
                "banks." + name + ".owner"
        );

        if (ownerUuid == null) {
            return new EconomyResponse(
                    0,
                    0,
                    EconomyResponse.ResponseType.FAILURE,
                    "Bank owner missing."
            );
        }

        boolean owner = ownerUuid.equals(
                player.getUniqueId().toString()
        );

        return new EconomyResponse(
                0,
                0,
                owner
                        ? EconomyResponse.ResponseType.SUCCESS
                        : EconomyResponse.ResponseType.FAILURE,
                owner
                        ? null
                        : "Player is not the owner."
        );
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        return isBankMember(name, Bukkit.getOfflinePlayer(playerName));
    }

    @Override
    public EconomyResponse isBankMember(String name, OfflinePlayer player) {

        // For now owner counts as member.
        return isBankOwner(name, player);
    }

    @Override
    public List<String> getBanks() {

        var section = plugin.getBanksData()
                .getConfigurationSection("banks");

        if (section == null) {
            return List.of();
        }

        return List.copyOf(section.getKeys(false));
    }

    private String bankKey(String name) {
        return name.toLowerCase()
                .replace(".", "_")
                .replace(" ", "_");
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player) {
        if (hasAccount(player)) return false;
        plugin.getPlayerData().set("players." + player.getUniqueId() + ".coins", plugin.getConfig().getInt("default-balance"));
        plugin.savePlayerData();
        return true;
    }

    @Override
    public boolean createPlayerAccount(String playerName, String worldName) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player, String worldName) {
        return createPlayerAccount(player);
    }
}
