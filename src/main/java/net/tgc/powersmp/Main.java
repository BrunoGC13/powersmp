package net.tgc.powersmp;

import de.nikey.combatLog.Combat.CombatManager;
import de.nikey.combatLog.CombatLog;
//import dev.pretti.prtsell.PrtSell;
import io.lumine.mythic.bukkit.MythicBukkit;
import net.luckperms.api.LuckPerms;
import net.minecraft.core.HolderSet;
import net.minecraft.core.MappedRegistry;
import net.minecraft.tags.TagKey;
import net.tgc.powersmp.ability.*;
import net.tgc.powersmp.api.PowerSMPAPI;
import net.tgc.powersmp.api.command.ApiCommand;
import net.tgc.powersmp.api.item.CustomItemRegistry;
import net.tgc.powersmp.civilization.CivilizationLevelManager;
import net.tgc.powersmp.civilization.CivilizationManager;
import net.tgc.powersmp.civilization.CivilizationRegistry;
import net.tgc.powersmp.civilization.defense.DefenseEventListener;
import net.tgc.powersmp.civilization.defense.DefenseSystem;
import net.tgc.powersmp.command.*;
import net.tgc.powersmp.command.dialogs.FastTravelCommand;
import net.tgc.powersmp.command.dialogs.FastTravelListener;
import net.tgc.powersmp.command.dialogs.TestDialogCommand;
import net.tgc.powersmp.command.economy.OrderCommand;
import net.tgc.powersmp.command.menus.*;
import net.tgc.powersmp.command.menus.shop.GemShopCommand;
import net.tgc.powersmp.command.menus.shop.MagicianShop;
import net.tgc.powersmp.command.menus.shop.SecretStrongholdShopMenuCommand;
import net.tgc.powersmp.dungeon.*;
import net.tgc.powersmp.economy.CoinsEconomy;
import net.tgc.powersmp.economy.ItemOwnerShip;
import net.tgc.powersmp.economy.networth.NetWorthManager;
import net.tgc.powersmp.economy.order.OrderDialogListener;
import net.tgc.powersmp.economy.order.OrderManager;
import net.tgc.powersmp.item.armor.EnforcedDiamondArmor;
import net.tgc.powersmp.item.gems.EscapeGem;
import net.tgc.powersmp.item.gems.NoCooldownGem;
import net.tgc.powersmp.item.*;
import net.tgc.powersmp.item.armor.KineticArmor;
import net.tgc.powersmp.item.gems.FireballItem;
import net.tgc.powersmp.item.gems.PowerDiamondItem;
import net.tgc.powersmp.item.gems.PowerEmeraldItem;
import net.tgc.powersmp.item.materials.*;
import net.tgc.powersmp.item.materials.kinetic.HardKineticShard;
import net.tgc.powersmp.item.materials.kinetic.KineticShard;
import net.tgc.powersmp.item.materials.listener.MaterialDropListener;
import net.tgc.powersmp.item.materials.resonite.ResoniteCrystal;
import net.tgc.powersmp.item.materials.resonite.ResoniteCrystalFragment;
import net.tgc.powersmp.item.redstone.RedstoneRecipes;
import net.tgc.powersmp.item.tools.CoalPickaxe;
import net.tgc.powersmp.item.tools.LumbersAxe;
import net.tgc.powersmp.item.tools.PrimalEmberPickaxe;
import net.tgc.powersmp.item.weapons.*;
import net.tgc.powersmp.item.materials.enforce.EnforcedDiamond;
import net.tgc.powersmp.item.materials.enforce.EnforcementShard;
import net.tgc.powersmp.item.world.EliasPendant;
import net.tgc.powersmp.levelSystem.LevelManager;
import net.tgc.powersmp.listener.*;
import net.tgc.powersmp.listener.dialogs.ProfileDialogListener;
import net.tgc.powersmp.menus.MultiSellMenu;
import net.tgc.powersmp.menus.npcs.GemMasterMenu;
import net.tgc.powersmp.menus.shop.ButcherMenu;
import net.tgc.powersmp.menus.shop.FarmerShop;
import net.tgc.powersmp.menus.shop.LogsMenu;
import net.tgc.powersmp.menus.shop.StrongholdShopMenu;
import net.tgc.powersmp.menus.skills.SkillsMenu;
import net.tgc.powersmp.objectives.NpcAssignmentListener;
import net.tgc.powersmp.menus.MenuRegistry;
import net.tgc.powersmp.menus.TestMenu;
import net.tgc.powersmp.menus.profiles.ProfileMenu;
import net.tgc.powersmp.menus.quests.QuestTreeMenu;
import net.tgc.powersmp.manager.CommandManager;
import net.tgc.powersmp.objectives.ObjectivesManager;
import net.tgc.powersmp.other.AFKManager;
import net.tgc.powersmp.other.LocationManager;
import net.tgc.powersmp.other.tpa.TPAManager;
import net.tgc.powersmp.other.tpa.TPAType;
import net.tgc.powersmp.player.PlayerDataManager;
import net.tgc.powersmp.quests.QuestListener;
import net.tgc.powersmp.quests.QuestService;
import net.tgc.powersmp.quests.QuestStorage;
import net.tgc.powersmp.recipe.PowerSMPRecipes;
import net.tgc.powersmp.registry.listener.Enchantments;
import net.tgc.powersmp.sell.SellCommand;
import net.tgc.powersmp.sell.SellManager;
import net.tgc.powersmp.skills.*;
import org.bukkit.*;
import org.bukkit.command.CommandMap;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;

public final class Main extends JavaPlugin {
    private static Main instance;
    private CommandManager commandManager;
    private QuestStorage questStorage;
    private DungeonLootManager dungeonLootManager;
    private DungeonManager dungeonManager;
    private MythicBukkit mythicMobs;
    private boolean debugMode = false;
    private ObjectivesManager objectiveManager;
    private SkillsManager skillsManager;
    private SkillsRegistry skillsRegistry;
    private DungeonMiningManager dungeonMiningManager;
    private LorePagesManager lorePagesManager;
    private DungeonAreaRegistry dungeonAreaRegistry;
    private DungeonPlayerData dungeonPlayerData;
    private DungeonAreaManager dungeonAreaManager;
    private CoinsEconomy coinsEconomy;
    private MobSpawnAreasManager mobSpawnAreasManager;
    private SellManager sellManager;
    private CivilizationManager civilizationManager;
    private CivilizationRegistry civilizationRegistry;
    private CivilizationLevelManager civilizationLevelManager;
    private DefenseSystem defenseSystem;
    private TPAManager tpaManager;
//    private PrtSell prtSell;

    private KineticArmor kineticArmor;
    private EnforcedDiamondArmor enforcedDiamondArmor;

    // Add these as fields alongside your other fields at the top of Main
    private File playerDataFile;
    private File skillsDataFile;
    private File dungeonsDataFile;
    private File areasDataFile;
    private File sellPricesDataFile;
    private File banksFile;
    private File civilizationFile;
    private File warsFile;
    private File ordersFile;
    private File itemOwnerShipFile;
    private File networthFile;
    private File enchantsFile;
    private FileConfiguration playerData;
    private FileConfiguration skillsData;
    private FileConfiguration areasData;
    private FileConfiguration sellPricesData;
    private FileConfiguration banksData;
    private FileConfiguration civiliazationData;
    private FileConfiguration warsData;
    private FileConfiguration ordersData;
    private FileConfiguration itemOwnerShipData;
    private FileConfiguration networthData;
    private FileConfiguration enchantsData;

    // Combat Log

    private File dungeonsPlayerDataFile;
    private FileConfiguration dungeonsPlayerData;
    private File questDataFile;
    private FileConfiguration questData;
    private FileConfiguration dungeonsData;
    private FileConfiguration npcMenusConfig;
    private FileConfiguration npcAssignmentsConfig;
    private LevelManager levelManager;
    private OrderManager orderManager;
    private ItemOwnerShip itemOwnerShip;
    private NetWorthManager netWorthManager;

    private CombatManager combatManager;
    private LuckPerms luckPermsApi;
    private LocationManager locationManager;
    private AFKManager afkManager;

    private File specialLocationsFile;
    private FileConfiguration specialLocationsData;

    @Override
    public void onEnable() {
        instance = this;
// 1. Ensure files exist on disk first
        saveResource("players.yml", false);
        saveResource("quests.yml", false);
        saveResource("dungeons.yml", false);
        saveResource("npc_menus.yml", false);
        saveResource("npc_assignments.yml", false);
        saveResource("skills.yml", false);
        saveResource("areas.yml", false);
        saveResource("dungeon_players.yml", false);
        saveResource("sell_prices.yml", false);
        saveResource("banks.yml", false);
        saveResource("civilizations.yml", false);
        saveResource("wars.yml", false);
        saveResource("item_ownership.yml", false);
        saveResource("networth.yml", false);
        saveResource("special_locations.yml", false);
        saveResource("enchants.yml", false);

        sellPricesDataFile = new File(getDataFolder(), "sell_prices.yml");
        sellPricesData = YamlConfiguration.loadConfiguration(sellPricesDataFile);
        sellManager = new SellManager(this);

// 2. Then load them into memory
        playerDataFile = new File(getDataFolder(), "players.yml");
        playerData = YamlConfiguration.loadConfiguration(playerDataFile);

        dungeonsPlayerDataFile = new File(getDataFolder(), "dungeon_players.yml");
        dungeonsPlayerData = YamlConfiguration.loadConfiguration(dungeonsPlayerDataFile);

        areasDataFile = new File(getDataFolder(), "areas.yml");
        areasData = YamlConfiguration.loadConfiguration(areasDataFile);

        dungeonsDataFile = new File(getDataFolder(), "dungeons.yml");
        dungeonsData = YamlConfiguration.loadConfiguration(dungeonsDataFile);

        skillsDataFile = new File(getDataFolder(), "skills.yml");
        skillsData = YamlConfiguration.loadConfiguration(skillsDataFile);

        questDataFile = new File(getDataFolder(), "quests.yml");
        questData = YamlConfiguration.loadConfiguration(questDataFile);
        npcMenusConfig = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "npc_menus.yml"));
        npcAssignmentsConfig = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "npc_assignments.yml"));

        banksFile = new File(getDataFolder(), "banks.yml");
        banksData = YamlConfiguration.loadConfiguration(banksFile);

        civilizationFile = new File(getDataFolder(), "civilizations.yml");
        civiliazationData = YamlConfiguration.loadConfiguration(civilizationFile);

        warsFile = new File(getDataFolder(), "wars.yml");
        warsData = YamlConfiguration.loadConfiguration(warsFile);

        ordersFile = new File(getDataFolder(), "orders.yml");
        ordersData = YamlConfiguration.loadConfiguration(ordersFile);

        itemOwnerShipFile = new File(getDataFolder(), "item_ownership.yml");
        itemOwnerShipData = YamlConfiguration.loadConfiguration(itemOwnerShipFile);

        networthFile = new File(getDataFolder(), "networth.yml");
        networthData = YamlConfiguration.loadConfiguration(networthFile);

        enchantsFile = new File(getDataFolder(), "enchants.yml");
        enchantsData = YamlConfiguration.loadConfiguration(enchantsFile);

        // Luckperms
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            luckPermsApi = provider.getProvider();

        }

        levelManager = new LevelManager(this);

        // Init objective manager early so PlayerJoinListener can use it
        objectiveManager = new ObjectivesManager(this);
        objectiveManager.loadNpcAssignments(npcAssignmentsConfig);

        specialLocationsFile =
                new File(getDataFolder(), "special_locations.yml");
        specialLocationsData =
                YamlConfiguration.loadConfiguration(specialLocationsFile);

// ... existing code ...

        boolean ecoEnabled = getConfig().getBoolean("economy");

        if (ecoEnabled) {
            if (getServer().getPluginManager().getPlugin("Vault") != null) {
                getServer().getServicesManager().register(
                        net.milkbowl.vault.economy.Economy.class,
                        new CoinsEconomy(this),
                        this,
                        org.bukkit.plugin.ServicePriority.Highest
                );
                getLogger().info("CoinsEconomy registered with Vault.");
            } else {
                getLogger().warning("Vault not found — economy will be unavailable.");
            }
        }

        // Init singletons
        PlayerDataManager.getInstance().init(this);
        CustomItemRegistry.getInstance().setPlugin(this);

        commandManager = new CommandManager(this);
        questStorage = new QuestStorage(this);

        coinsEconomy = new CoinsEconomy(this);

        // Menu registry — register all openable menus here
        final QuestStorage qs = questStorage; // effectively-final for lambdas
        MenuRegistry.register("profile", (viewer, target) -> new ProfileMenu(target));
        MenuRegistry.register("quests",  (viewer, target) -> new QuestTreeMenu(target, qs));
        MenuRegistry.register("test",    (viewer, target) -> new TestMenu());
        MenuRegistry.register("skills_menu", (viewer, target) -> new SkillsMenu(this));
        MenuRegistry.register("gem_master_menu", (viewer, target) -> new GemMasterMenu(target, coinsEconomy));
        MenuRegistry.register("stronghold_shop", (viewer, target) ->
                new StrongholdShopMenu(this, coinsEconomy));
        MenuRegistry.register("magician_shop", (viewer, target) -> new MagicianShop(this, coinsEconomy));
        MenuRegistry.register("recipe_book", (viewer, target) -> new net.tgc.powersmp.menus.recipes.RecipeBookMenu(this));
        MenuRegistry.register("farming_shop", (viewer, target) -> new FarmerShop(this, coinsEconomy));
        MenuRegistry.register("butcher_shop", (viewer, target) -> new ButcherMenu(this, coinsEconomy));
        MenuRegistry.register("logs_shop", (viewer, target) -> new LogsMenu(this, coinsEconomy));

        registerCombatLogHook();

        locationManager = new LocationManager(this);

//        Plugin prtSell = Bukkit.getPluginManager().getPlugin("PrtSell");
//
//        if (prtSell == null || !prtSell.isEnabled()) {
//            getLogger().warning("PrtSell nicht verfügbar.");
//        } else {
//            this.prtSell = (PrtSell) prtSell;
//        }
//
//        this.prtSell = (PrtSell) prtSell;

        // Commandsgem_master
        commandManager.registerCommand("quests",    "Opens Quest Menu",         List.of("quest"),       new QuestGUICommand());
        commandManager.registerCommand("powersmp",  "PowerSMP Admin Commands",  List.of("psmp"),         new PowerSMPAdminCommand());
        commandManager.registerCommand("openmenu",  "Opens a GUI menu",         List.of(),               new OpenMenuCommand());
        commandManager.registerCommand("coins", "Manage player coins", List.of(), new CoinsCommand(this));
        commandManager.registerCommand("skills", "Manage your skills", List.of("skill", "sk"), new SkillsGUICommand(this));
        commandManager.registerCommand("gems", "Buy gems from the gem master", List.of("gem"), new GemShopCommand(this));
        commandManager.registerCommand("secretstrongholdshop", "The shop for secret stronghold items", List.of("ssshop", "sshop", "secretshop", "strongholdshop"), new SecretStrongholdShopMenuCommand(this));
        commandManager.registerCommand("recipes", "Opens recipe menu", List.of("recipe", "rec", "recs"), new RecipeBookMenuCommand(this));
        // commandManager.registerCommand("sell", "Opens the Sell Menu", List.of("s"), new SellMeuCommand(this));
        commandManager.registerCommand("singlesell", "Sell Items", List.of(), new SellCommand(this));
        commandManager.registerCommand("testdialog", new TestDialogCommand());
        commandManager.registerCommand(
                "specialloc",
                "Manage special locations",
                List.of("speciallocation", "sloc"),
                new SpecialLocationCommand(this)
        );
        commandManager.registerCommand("fasttravel", "Travel faster", List.of("ft", "travel", "trv", "fast"), new FastTravelCommand());

        // Listeners
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new DeathListener(), this);
        getServer().getPluginManager().registerEvents(new ItemUseListener(), this);
        getServer().getPluginManager().registerEvents(new GemApplicationListener(), this);
        getServer().getPluginManager().registerEvents(new MaterialDropListener(this), this);
        // getServer().getPluginManager().registerEvents(new AnvilListener(this), this);
        getServer().getPluginManager().registerEvents(new MobSpawnListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(this), this);
        getServer().getPluginManager().registerEvents(new NpcAssignmentListener(this), this);
        dungeonManager = new DungeonManager(this);
        getServer().getPluginManager().registerEvents(dungeonManager, this);
        getServer().getPluginManager().registerEvents(new MythicMobDeath(this), this);
        getServer().getPluginManager().registerEvents(new PrimalEmberPickaxeListener(this), this);

        QuestStorage questStorage = new QuestStorage(this);
        QuestService questService = new QuestService(this, questStorage, levelManager);
        getServer().getPluginManager().registerEvents(new QuestListener(questService), this);
        getServer().getPluginManager().registerEvents(new DungeonMusicStarter(this), this);
//        getServer().getPluginManager().registerEvents(new UniversalItemListener(this), this);


        // Recurring tasks
        PowerBarTask.start(this);
        PowerRegenTask.start(this);

        dungeonLootManager = new DungeonLootManager(this);
        dungeonLootManager.spawnOrbsForAllDungeons();
        DungeonLootTask.start(this, dungeonLootManager);

        // Built-in commands
        HeartsCommand heartsCmd = new HeartsCommand();
        PowerCommand  powerCmd  = new PowerCommand();

        CommandMap commandMap = getServer().getCommandMap();
        commandMap.register("powersmp", new ApiCommand(this, "hearts",   heartsCmd,           heartsCmd));
        commandMap.register("powersmp", new ApiCommand(this, "power",    powerCmd,            powerCmd));
        commandMap.register("powersmp", new ApiCommand(this, "powerbar", new PowerBarCommand(), null));;

        // --- Materials ---
        KineticShard kineticShard    = new KineticShard(this);
        VoidFragment    voidFragment    = new VoidFragment(this);
        ResoniteCrystal resoniteCrystal = new ResoniteCrystal(this);
        PrimalEmber     primalEmber     = new PrimalEmber(this);
        SoulDust        soulDust        = new SoulDust(this);
        StormEssence    stormEssence    = new StormEssence(this);
        ResoniteCrystalFragment resoniteCrystalFragment = new ResoniteCrystalFragment(this);
        HardKineticShard hardKineticShard = new HardKineticShard(this);
        AlexGrandmaRelic alexGrandmaRelic = new AlexGrandmaRelic(this);
        PrimalEmberPickaxe primalEmberPickaxe = new PrimalEmberPickaxe(this);
        EnforcementShard enforcementShard = new EnforcementShard(this);
        RubySpear rubySpear = new RubySpear(this);
        LumbersAxe lumbersAxe = new LumbersAxe(this);

        PowerSMPAPI.registerItem(kineticShard);
        PowerSMPAPI.registerItem(voidFragment);
        PowerSMPAPI.registerItem(resoniteCrystal);
        PowerSMPAPI.registerItem(primalEmber);
        PowerSMPAPI.registerItem(soulDust);
        PowerSMPAPI.registerItem(stormEssence);
        PowerSMPAPI.registerItem(resoniteCrystalFragment);
        PowerSMPAPI.registerItem(hardKineticShard);
        PowerSMPAPI.registerItem(alexGrandmaRelic);
        PowerSMPAPI.registerItem(primalEmberPickaxe);
        PowerSMPAPI.registerItem(enforcementShard);
        PowerSMPAPI.registerItem(rubySpear);
        PowerSMPAPI.registerItem(lumbersAxe);

        // --- Built-in items & abilities ---
        PowerEmeraldItem emeraldItem  = new PowerEmeraldItem(this);
        PowerDiamondItem diamondItem  = new PowerDiamondItem(this);
        NoCooldownGem noCooldownGem = new NoCooldownGem(this);
        FireballItem fireballItem = new FireballItem(this);
        EscapeGem escapeGem = new EscapeGem(this);
        AspectOfTheEnd aspectOfTheEnd = new AspectOfTheEnd(this);
        CoalPickaxe coalPickaxe = new CoalPickaxe(this);
        TheArmorersSword theArmorersSword = new TheArmorersSword(this);
        EnforcedDiamond enforcedDiamond = new EnforcedDiamond(this);
        Ruby ruby = new Ruby(this);
        EliasPendant eliasPendant = new EliasPendant(this);
        HeartItem heartItem = new HeartItem(this);

        PowerSMPAPI.registerAbility(new EmeraldDashAbility());
        PowerSMPAPI.registerAbility(new DiamondDashAbility());
        PowerSMPAPI.registerAbility(new FireballAbility());
        PowerSMPAPI.registerAbility(new NoCooldownAbility(this));
        PowerSMPAPI.registerAbility(new EscapeAbility(this));
        PowerSMPAPI.registerAbility(new ArmorersSecretAbility());
        PowerSMPAPI.registerItem(emeraldItem);
        PowerSMPAPI.registerItem(diamondItem);
        PowerSMPAPI.registerItem(fireballItem);
        PowerSMPAPI.registerItem(noCooldownGem);
        PowerSMPAPI.registerItem(escapeGem);
        PowerSMPAPI.registerItem(aspectOfTheEnd);
        PowerSMPAPI.registerItem(coalPickaxe);
        PowerSMPAPI.registerItem(theArmorersSword);
        PowerSMPAPI.registerItem(enforcedDiamond);
        PowerSMPAPI.registerItem(ruby);
        PowerSMPAPI.registerItem(eliasPendant);
        PowerSMPAPI.registerItem(heartItem);

        // --- Weapons ---
        EmberBlade emberBlade = new EmberBlade(this);
        VoidScythe voidScythe = new VoidScythe(this);

        PowerSMPAPI.registerAbility(new InfernoSurgeAbility());
        PowerSMPAPI.registerAbility(new SoulRendAbility());
        PowerSMPAPI.registerItem(emberBlade);
        PowerSMPAPI.registerItem(voidScythe);

        // Pre-build material stacks for ExactChoice matching.
        // ExactChoice ensures players must use the actual custom material, not vanilla items.
        RecipeChoice kineticChoice   = new RecipeChoice.ExactChoice(CustomItemRegistry.getInstance().createItemStack(kineticShard.getKey()));
        RecipeChoice voidChoice      = new RecipeChoice.ExactChoice(CustomItemRegistry.getInstance().createItemStack(voidFragment.getKey()));
        RecipeChoice resoniteChoice  = new RecipeChoice.ExactChoice(CustomItemRegistry.getInstance().createItemStack(resoniteCrystal.getKey()));
        RecipeChoice emberChoice     = new RecipeChoice.ExactChoice(CustomItemRegistry.getInstance().createItemStack(primalEmber.getKey()));
        RecipeChoice soulDustChoice  = new RecipeChoice.ExactChoice(CustomItemRegistry.getInstance().createItemStack(soulDust.getKey()));
        RecipeChoice stormChoice     = new RecipeChoice.ExactChoice(CustomItemRegistry.getInstance().createItemStack(stormEssence.getKey()));
        RecipeChoice enforcementChoice = new RecipeChoice.ExactChoice(CustomItemRegistry.getInstance().createItemStack(enforcementShard.getKey()));
        RecipeChoice enforcedDiamondChoice = new RecipeChoice.ExactChoice(CustomItemRegistry.getInstance().createItemStack(enforcedDiamond.getKey()));
        RecipeChoice rubyChoice = new RecipeChoice.ExactChoice((CustomItemRegistry.getInstance().createItemStack(ruby.getKey())));

        saveDefaultConfig();
        loadEnchantConfig();

        FileConfiguration config = getConfig();

        commandManager.activate();
        try {
            org.bukkit.plugin.Plugin mmPlugin = getServer().getPluginManager().getPlugin("MythicMobs");
            if (mmPlugin != null) {
                this.mythicMobs = (MythicBukkit) mmPlugin;
                getLogger().info("Successfully hooked into MythicMobs!");
                getServer().getPluginManager().registerEvents(new MythicDungeonListener(this, dungeonManager), this);
            } else {
                getLogger().warning("MythicMobs not found — dungeon bosses will be disabled.");
            }
        } catch (Exception | NoClassDefFoundError ex) {
            getLogger().warning("Failed to hook into MythicMobs (" + ex.getMessage() + ") — dungeon bosses will be disabled.");
        }

        try {
            org.bukkit.plugin.Plugin citizensPlugin = getServer().getPluginManager().getPlugin("Citizens");
            if (citizensPlugin != null) {
                getServer().getPluginManager().registerEvents(new CitizensNPCListener(this), this);
                getLogger().info("Successfully hooked into Citizens — NPC menus enabled.");
            } else {
                getLogger().info("Citizens not found — NPC menus will be unavailable.");
            }
        } catch (Exception | NoClassDefFoundError ex) {
            getLogger().warning("Failed to hook into Citizens (" + ex.getMessage() + ") — NPC menus disabled.");
        }

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new Placeholders(this).register();
            getLogger().info("PlaceholderAPI hooked — placeholders registered.");
        }

        dungeonMiningManager = new DungeonMiningManager(this);
        getServer().getPluginManager().registerEvents(dungeonMiningManager, this);

        // Skills
        skillsManager = new SkillsManager(this);
        skillsRegistry = new SkillsRegistry(this);
        skillsRegistry.registerSkill(new Skill("mining_skill", 50));
        skillsRegistry.registerSkill(new Skill("lumber_skill", 20));
        skillsRegistry.registerSkill(new Skill("hunting_skill", 20));

        getServer().getPluginManager().registerEvents(new MinerSkill(this), this);
        getServer().getPluginManager().registerEvents(new LumberSkill(this), this);
        getServer().getPluginManager().registerEvents(new HuntingSkill(this), this);
        getServer().getPluginManager().registerEvents(new GetLorePage(this), this);
        getServer().getPluginManager().registerEvents(new UniversalBlockBreakListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerEnterNetherListener(this), this);
        getServer().getPluginManager().registerEvents(new Enchantments(), this);

        if (getConfig().getBoolean("enableRules")) {
            getServer().getPluginManager().registerEvents(new AcceptRulesListener(this), this);
        }

        lorePagesManager = new LorePagesManager(this);
        lorePagesManager.spawn();

        dungeonAreaManager = new DungeonAreaManager(this);
        dungeonAreaRegistry = new DungeonAreaRegistry(this);
        dungeonPlayerData = new DungeonPlayerData(this);

        getServer().getPluginManager().registerEvents(new RedstoneItemListener(this), this);

        getServer().getScheduler().runTask(this, () -> {
            loadDungeonWorlds();
            mobSpawnAreasManager.initMobSpawns();
        });

        mobSpawnAreasManager = new MobSpawnAreasManager(this);
        mobSpawnAreasManager.initMobSpawns();

        kineticArmor = new KineticArmor();
        enforcedDiamondArmor = new EnforcedDiamondArmor();

        registerStabShotRecipe();
        registerNukeShotRecipe();

        getServer().getScheduler().runTask(this, () -> {
            getLogger().info(
                    "worlds.nether = " + getConfig().getString("worlds.nether")
            );
            getLogger().info("Loaded worlds: " + Bukkit.getWorlds());

            String netherName = getConfig().getString("worlds.nether");

            if (netherName != null && !netherName.isBlank()
                    && Bukkit.getWorld(netherName) == null) {

                World netherWorld = Bukkit.createWorld(
                        new WorldCreator(netherName)
                                .environment(World.Environment.NETHER)
                );

                if (netherWorld == null) {
                    getLogger().severe(
                            "Failed to load Nether world: " + netherName
                    );
                } else {
                    getLogger().info(
                            "Successfully loaded Nether world: " + netherName
                    );
                }
            }
        });

        civilizationRegistry = new CivilizationRegistry(this);
        civilizationManager = new CivilizationManager(this);

        civilizationLevelManager = new CivilizationLevelManager(this);

        // Initialize defense system
        defenseSystem = new DefenseSystem(this);

        // Register event listener
        getServer().getPluginManager().registerEvents(new DefenseEventListener(this), this);

        // Setup defenses for existing civilizations
        // civilizationRegistry.setupExistingCivilizationDefense();
        registerCommand("civilization", List.of("civ"), new CivilizationCommand(this));

        commandManager.registerCommand("withdrawhearts", "Withdraws hearts", List.of("whearts", "wh"), new WithdrawHeartsCommand(this));

        orderManager = new OrderManager(this, coinsEconomy);

        MenuRegistry.register(
                "orders",
                (viewer, target) ->
                        new net.tgc.powersmp.menus.OrderMenu(
                                this,
                                orderManager
                        )
        );

        getServer().getPluginManager().registerEvents(new OrderDialogListener(this, orderManager), this);

        registerCommand("order", new OrderCommand(this));

        itemOwnerShip = new ItemOwnerShip(this);
        getServer().getPluginManager().registerEvents(new ItemMovementListener(this), this);

        netWorthManager = new NetWorthManager(this, itemOwnerShip);

        commandManager.registerCommand("profile", "Opens Player Profile (Dialog)", new ProfileMenuGUICommand(this));

        getServer().getPluginManager().registerEvents(new ProfileDialogListener(), this);
        getServer().getPluginManager().registerEvents(new HeartItemListener(this), this);

        new PowerSMPRecipes(this).registerAll();

        MenuRegistry.register("multi_sell", (viewer, target) -> new MultiSellMenu(this));
        commandManager.registerCommand("multisell", "Multi sell your items", List.of("sell", "s"), new MultiSellCommand(this));

        getServer().getPluginManager().registerEvents(new MenuListener(), this);
        tpaManager = new TPAManager(this);

        commandManager.registerCommand(
                "tpa",
                "Send a teleport request",
                new TPARequestCommand(TPAType.TPA)
        );

        commandManager.registerCommand(
                "tpahere",
                "Request a player to teleport to you",
                new TPARequestCommand(TPAType.TPHERE)
        );

        commandManager.registerCommand(
                "tpaccept",
                "Accept a teleport request",
                new TPAResponseCommand(true)
        );

        commandManager.registerCommand(
                "tpdeny",
                "Deny a teleport request",
                new TPAResponseCommand(false)
        );

        afkManager = new AFKManager(this);
        getServer().getPluginManager().registerEvents(afkManager, this);

        getLogger().info("NMS running...");

        getLogger().info("PowerSMP API enabled.");
    }

    public Map<Enchantment, Integer> overrideEnchants = new HashMap<>();

    public void loadEnchantConfig() {
        ConfigurationSection section = getConfig().getConfigurationSection("overrideEnchants");
        if (section == null) return;

        for (String key : section.getKeys(false)) {
            Enchantment ench = Enchantment.getByName(key.toUpperCase());

            if (ench == null) {
                getLogger().warning("Unbekanntes Enchantment: " + key);
                continue;
            }

            int maxLevel = section.getInt(key);
            overrideEnchants.put(ench, maxLevel);
        }
    }

    public LocationManager getLocationManager() {
        return locationManager;
    }

    public FileConfiguration getSpecialLocationsData() {
        return specialLocationsData;
    }

    public void saveSpecialLocationsData() {
        if (specialLocationsData == null || specialLocationsFile == null) {
            return;
        }

        try {
            specialLocationsData.save(specialLocationsFile);
        } catch (IOException e) {
            getLogger().severe(
                    "Could not save special_locations.yml: "
                            + e.getMessage()
            );
        }
    }

    public AFKManager getAfkManager() {
        return afkManager;
    }

    public NetWorthManager getNetWorthManager() {
        return netWorthManager;
    }

    public CombatManager getCombatManager() {
        return combatManager;
    }

    public ItemOwnerShip getItemOwnerShip() {
        return itemOwnerShip;
    }

    public LuckPerms getLuckPermsApi() {
        return luckPermsApi;
    }

    public OrderManager getOrderManager() {
        return orderManager;
    }

    public LevelManager getLevelManager() {
        return levelManager;
    }

    public SkillsManager getSkillsManager() {
        return skillsManager;
    }

    public SkillsRegistry getSkillsRegistry() {
        return skillsRegistry;
    }

    public DungeonAreaRegistry getDungeonAreaRegistry() {
        return dungeonAreaRegistry;
    }

    public DungeonAreaManager getDungeonAreaManager() {
        return dungeonAreaManager;
    }

    public CivilizationLevelManager getCivilizationLevelManager() {
        return civilizationLevelManager;
    }

    public String getPlayerCivilization(UUID playerId) {
        // Your existing method to get player's civilization
        return civilizationManager.getPlayerCivilization(playerId);
    }

    public CoinsEconomy getCoinsEconomy() {
        return coinsEconomy;
    }

    public CivilizationManager getCivilizationManager() {
        return civilizationManager;
    }

    public CivilizationRegistry getCivilizationRegistry() {
        return civilizationRegistry;
    }

    public DungeonPlayerData getDungeonPlayerData() {
        return dungeonPlayerData; // gibt DungeonPlayerData zurück, nicht FileConfiguration
    }

    public SellManager getSellManager() {
        return sellManager;
    }

    public FileConfiguration getDungeonProgressData() {
        return dungeonsPlayerData; // raw config für DungeonPlayerData intern
    }

    public FileConfiguration getEnchantsData() {
        return enchantsData;
    }

    public FileConfiguration getWarsData() {
        return warsData;
    }

    public void saveDungeonProgressData() {
        try {
            dungeonsPlayerData.save(dungeonsPlayerDataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveWarsData() {
        try {
            warsData.save(warsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getAreasData() {
        return areasData;
    }

//    public PrtSell getPrtSell() {
//        return prtSell;
//    }

    @Override
    public void onDisable() {
        if (dungeonLootManager != null) dungeonLootManager.removeAll();
        savePlayerData();
        saveQuestData();
        saveSkillsData();
        lorePagesManager.despawnAll();

        if (locationManager != null) {
            locationManager.saveLocations();
        }

        getLogger().info("PowerSMP API disabled.");
    }

    private boolean combatCommandsRegistered;

    private void registerCombatLogHook() {
        getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onPluginEnable(PluginEnableEvent event) {
                if (event.getPlugin().getName().equalsIgnoreCase("CombatLog")) {
                    hookCombatLog(event.getPlugin());
                }
            }
        }, this);

        Plugin combatLog = getServer()
                .getPluginManager()
                .getPlugin("CombatLog");

        if (combatLog != null && combatLog.isEnabled()) {
            hookCombatLog(combatLog);
        } else {
            getLogger().info(
                    "CombatLog is not enabled yet; waiting for PluginEnableEvent."
            );
        }
    }

    private void hookCombatLog(Plugin plugin) {
        if (combatCommandsRegistered) {
            return;
        }

        if (!(plugin instanceof CombatLog combatLogPlugin)) {
            getLogger().severe(
                    "A plugin named CombatLog was found, but it has an unexpected type."
            );
            return;
        }

        if (!plugin.isEnabled()) {
            return;
        }

        CombatManager manager = combatLogPlugin.getCombatManager();

        if (manager == null) {
            getLogger().severe(
                    "CombatLog is enabled, but getCombatManager() returned null."
            );
            return;
        }

        combatManager = manager;
        combatCommandsRegistered = true;

        commandManager.registerCommand(
                "secretstronghold",
                "Get to the secret stronghold",
                List.of("ss", "sstronghold"),
                new SecretStrongholdCommand(this, combatManager)
        );

        commandManager.registerCommand(
                "spawn",
                "Get yourself to spawn",
                List.of("hub", "worldspawn", "wspawn"),
                new SpawnCommand(combatManager)
        );

        getServer().getPluginManager().registerEvents(new FastTravelListener(this, combatManager), this);

        commandManager.registerCommand(
                "afk",
                "Set yourself afk",
                new AFKCommand(this, combatManager)
        );

        getLogger().info("Successfully hooked into CombatLog.");
    }

    public LorePagesManager getLorePagesManager() {
        return lorePagesManager;
    }

    public FileConfiguration getItemOwnerShipData() {
        return itemOwnerShipData;
    }

    public FileConfiguration getPlayerData() {
        return playerData;
    }

    public FileConfiguration getSellPricesData() {
        return sellPricesData;
    }

    public FileConfiguration getSkillsData() {
        return skillsData;
    }

    public FileConfiguration getQuestData() {
        return questData;
    }

    public FileConfiguration getDungeonsData() {
        return dungeonsData;
    }

    public FileConfiguration getBanksData() {
        return banksData;
    }

    public FileConfiguration getNetworthData() {
        return networthData;
    }

    public void savePlayerData() {
        if (playerData == null || playerDataFile == null) return;

        try {
            playerData.save(playerDataFile);
        } catch (IOException e) {
            getLogger().severe("Could not save players.yml: " + e.getMessage());
        }
    }

    public void saveItemOwnerShipData() {
        if (itemOwnerShipData == null || itemOwnerShipFile == null) return;

        try {
            itemOwnerShipData.save(itemOwnerShipFile);
        } catch (IOException e) {
            getLogger().severe("Could not save item_ownership.yml: " + e.getMessage());
        }
    }

    public void saveNetworthData() {
        if (networthData == null || networthFile == null) return;

        try {
            networthData.save(networthFile);
        } catch (IOException e) {
            getLogger().severe("Could not save networth.yml: " + e.getMessage());
        }
    }

    public void saveBanksData() {
        if (banksData == null || banksFile == null) return;

        try {
            banksData.save(banksFile);
        } catch (IOException e) {
            getLogger().severe("Could not save banks.yml: " + e.getMessage());
        }
    }

    public void saveCivilizationsData() {
        if (civiliazationData == null || civilizationFile == null) return;

        try {
            civiliazationData.save(civilizationFile);
        } catch (IOException e) {
            getLogger().severe("Could not save civilizations.yml: " + e.getMessage());
        }
    }

    public QuestStorage getQuestStorage() {
        return questStorage;
    }

    public void saveQuestData() {
        if (questData == null || questDataFile == null) return;

        try {
            questData.save(questDataFile);
        } catch (IOException e) {
            getLogger().severe("Could not save quests.yml: " + e.getMessage());
        }
    }

    public MythicBukkit getMythicMobs() {
        return mythicMobs;
    }

    public FileConfiguration getNpcMenusConfig() {
        return npcMenusConfig;
    }

    public FileConfiguration getNpcAssignmentsConfig() {
        return npcAssignmentsConfig;
    }

    public FileConfiguration getCiviliazationData() {
        return civiliazationData;
    }

    public FileConfiguration getOrdersData() {
        return ordersData;
    }

    public void reload() {
        npcMenusConfig       = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "npc_menus.yml"));
        npcAssignmentsConfig = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "npc_assignments.yml"));
        dungeonsData         = YamlConfiguration.loadConfiguration(dungeonsDataFile);
        objectiveManager.loadNpcAssignments(npcAssignmentsConfig);
        getLogger().info("[PowerSMP] Config reloaded.");
    }

    public void saveSkillsData() {
        if (skillsData == null || skillsDataFile == null) return;
        try {
            skillsData.save(skillsDataFile);
        } catch (IOException e) {
            getLogger().severe("Could not save skills.yml: " + e.getMessage());
        }
    }

    public void saveDungeonAreasData() {
        if (areasData == null || areasDataFile == null) return;
        try {
            areasData.save(areasDataFile);
        } catch (IOException e) {
            getLogger().severe("Could not save areas.yml: " + e.getMessage());
        }
    }

    private void registerStabShotRecipe() {
        StabShotItem stabShotItem = new StabShotItem(this);

        NamespacedKey stabShotKey = new NamespacedKey(this, "stab_shot");

        ShapedRecipe recipe = new ShapedRecipe(
                stabShotKey,
                stabShotItem.getStabShot("stab", 0, 0, 0)
        );

        recipe.shape(
                " W ",
                "EFE",
                " S "
        );

        recipe.setIngredient('W', Material.NETHER_STAR);
        recipe.setIngredient('E', Material.ENDER_EYE);
        recipe.setIngredient('F', Material.FISHING_ROD);
        recipe.setIngredient('S', Material.STICK);

        Bukkit.addRecipe(recipe);
    }

    private void registerNukeShotRecipe() {
        NukeShotItem nukeShotItem = new NukeShotItem(this);

        NamespacedKey stabShotKey = new NamespacedKey(this, "nuke_shot");

        ShapedRecipe recipe = new ShapedRecipe(
                stabShotKey,
                nukeShotItem.getStabShot("nuke", 0, 0, 0)
        );

        recipe.shape(
                " W ",
                "EFE",
                " T "
        );

        recipe.setIngredient('W', Material.NETHER_STAR);
        recipe.setIngredient('E', Material.ENDER_EYE);
        recipe.setIngredient('F', Material.FISHING_ROD);
        recipe.setIngredient('T', Material.TNT);

        Bukkit.addRecipe(recipe);
    }

    private void loadDungeonWorlds() {
        ConfigurationSection dungeons =
                dungeonsData.getConfigurationSection("dungeons");

        if (dungeons == null) {
            getLogger().warning("[Worlds] No dungeons section found in dungeons.yml.");
            return;
        }

        for (String dungeonKey : dungeons.getKeys(false)) {
            ConfigurationSection dungeon =
                    dungeons.getConfigurationSection(dungeonKey);

            if (dungeon == null) {
                continue;
            }

            String worldName = dungeon.getString("world");

            if (worldName == null || worldName.isBlank()) {
                getLogger().warning(
                        "[Worlds] Dungeon '" + dungeonKey
                                + "' has no world configured."
                );
                continue;
            }

            World world = Bukkit.getWorld(worldName);

            if (world != null) {
                getLogger().info(
                        "[Worlds] World '" + worldName
                                + "' is already loaded."
                );
                continue;
            }

            File worldFolder = new File(
                    getServer().getWorldContainer(),
                    worldName
            );

            File levelFile = new File(worldFolder, "level.dat");

            if (!levelFile.isFile()) {
                getLogger().warning(
                        "[Worlds] Cannot load world '" + worldName
                                + "' for dungeon '" + dungeonKey
                                + "': level.dat was not found at "
                                + worldFolder.getAbsolutePath()
                );
                continue;
            }

            getLogger().info(
                    "[Worlds] Loading world '" + worldName
                            + "' for dungeon '" + dungeonKey + "'."
            );

            World loadedWorld = Bukkit.createWorld(
                    new WorldCreator(worldName)
            );

            if (loadedWorld == null) {
                getLogger().severe(
                        "[Worlds] Failed to load world '" + worldName + "'."
                );
            } else {
                getLogger().info(
                        "[Worlds] Successfully loaded world '"
                                + loadedWorld.getName() + "'."
                );
            }
        }
    }

    public DungeonLootManager getDungeonLootManager() {
        return dungeonLootManager;
    }

    public DungeonManager getDungeonManager() {
        return dungeonManager;
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    public static Main getInstance() {
        return instance;
    }

    public ObjectivesManager getObjectiveManager() { return objectiveManager; }

    public TPAManager getTpaManager() {
        return tpaManager;
    }
}
