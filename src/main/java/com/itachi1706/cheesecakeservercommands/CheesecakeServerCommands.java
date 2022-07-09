package com.itachi1706.cheesecakeservercommands;

import com.itachi1706.cheesecakeservercommands.commands.*;
import com.itachi1706.cheesecakeservercommands.commands.admin.*;
import com.itachi1706.cheesecakeservercommands.commands.admin.item.*;
import com.itachi1706.cheesecakeservercommands.commands.admin.server.*;
import com.itachi1706.cheesecakeservercommands.commands.admin.world.BiomeInfoCommand;
import com.itachi1706.cheesecakeservercommands.dbstorage.CommandsLogDB;
import com.itachi1706.cheesecakeservercommands.dbstorage.LoginLogoutDB;
import com.itachi1706.cheesecakeservercommands.events.PlayerEvents;
import com.itachi1706.cheesecakeservercommands.jsonstorage.LastKnownUsernameJsonHelper;
import com.itachi1706.cheesecakeservercommands.jsonstorage.LastKnownUsernames;
import com.itachi1706.cheesecakeservercommands.nbtstorage.AdminSilenced;
import com.itachi1706.cheesecakeservercommands.nbtstorage.CSCAdminSilenceWorldSavedData;
import com.itachi1706.cheesecakeservercommands.noteblocksongs.NoteblockSongs;
import com.itachi1706.cheesecakeservercommands.proxy.ClientProxy;
import com.itachi1706.cheesecakeservercommands.proxy.IProxy;
import com.itachi1706.cheesecakeservercommands.proxy.ServerProxy;
import com.itachi1706.cheesecakeservercommands.reference.CommandPermissionsLevel;
import com.itachi1706.cheesecakeservercommands.reference.InitDamageSources;
import com.itachi1706.cheesecakeservercommands.reference.References;
import com.itachi1706.cheesecakeservercommands.util.LogHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.logging.LogUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.GameType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.slf4j.Logger;

import javax.management.MBeanServer;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(References.MOD_ID)
public class CheesecakeServerCommands
{
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    private static List<LastKnownUsernames> lastKnownUsernames = new ArrayList<>();
    private static File configFileDirectory;
    private static Map<String, DamageSource> knownDamageSources;
    private static final ArrayList<BaseCommand> commands = new ArrayList<>();
    public static final IProxy PROXY = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> ServerProxy::new);

    public CheesecakeServerCommands()
    {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the enqueueIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new PlayerEvents());

        PROXY.init();
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        // some preinit code
        LOGGER.info("Preinitializing mod");

        // Setup configuration object
        File file = new File(FMLPaths.CONFIGDIR.get() + File.separator + "cheesecakeserver");
        LogHelper.info(">>> Folder name: " + file.getAbsolutePath());
        if (!file.exists() && file.mkdir()){
            LogHelper.info("Created Cheesecake Server Internal Directory");
        }

        setConfigFileDirectory(file);
    }

    private static void setConfigFileDirectory(File file){
        configFileDirectory = file;
    }

    public static File getConfigFileDirectory(){
        return configFileDirectory;
    }

    public static Map<String, DamageSource> getKnownDamageSources() {
        return knownDamageSources;
    }

    public static List<LastKnownUsernames> getLastKnownUsernames() {
        return lastKnownUsernames;
    }

    public static void setLastKnownUsernames(List<LastKnownUsernames> lastKnownUsernames) {
        CheesecakeServerCommands.lastKnownUsernames = lastKnownUsernames;
    }

    public static void setKnownDamageSources(Map<String, DamageSource> knownDamageSources) {
        CheesecakeServerCommands.knownDamageSources = knownDamageSources;
    }

    private void enqueueIMC(final InterModEnqueueEvent event)
    {
        // Some example code to dispatch IMC to another mod
        InterModComms.sendTo(References.MOD_ID, "helloworld", () -> { LOGGER.info("Hello world from the MDK"); return "Hello world";});
    }

    private void processIMC(final InterModProcessEvent event)
    {
        // Some example code to receive and process InterModComms from other mods
        LOGGER.info("Got IMC {}", event.getIMCStream().
                map(m->m.messageSupplier().get()).toList());
    }

    // Init OS Bean
    private static MBeanServer platformBean;

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");

        // Register Loggers
        registerLoggers();
        CSCAdminSilenceWorldSavedData.get();
        LogHelper.info("Admin Silenced List: " + AdminSilenced.getState());

        // Init OS Bean
        setPlatformBean(ManagementFactory.getPlatformMBeanServer());

        InitDamageSources.initalizeDamages();
        NoteblockSongs.refreshSongs();
    }

    private void registerLoggers(){
        if (LastKnownUsernameJsonHelper.fileExists())
            setLastKnownUsernames(LastKnownUsernameJsonHelper.readFromFile());

       LoginLogoutDB.getInstance().checkTablesExists();
       CommandsLogDB.getInstance().checkTablesExists();
    }

    private static void setPlatformBean(MBeanServer myBean) {
        platformBean = myBean;
    }

    public static MBeanServer getPlatformBean() {
        return platformBean;
    }

    // Register commands here

    /**
     * Register Commands here and set their permission level
     *
     * <a href="https://minecraft.fandom.com/wiki/Permission_level">Permission Levels</a> (see {@link CommandPermissionsLevel})
     * 0 - Normal Player
     * 1 - Player can bypass spawn protection.
     * 2 - Player or executor can use more commands (see Commands) and player can use command blocks. (Cheat commands)
     * 3 - Player or executor can use more commands. (Multiplayer management)
     * 4 - Player or executor can use all the commands. (Server management)
     *
     * From Command Code checkPermission
     * return true -> 0
     * return isOperatorOrConsole -> 2
     *
     * @param event Register Commands Event
     */
    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event) {
        LOGGER.info("Starting command registration");

        // Add commands
        commands.add(new CCLoggerCommand("cclogger", CommandPermissionsLevel.SERVER, true));
        commands.add(new CCLoggerCommand("cheesecakelogger", CommandPermissionsLevel.SERVER, true));
        commands.add(new CCLoggerCommand("ccl", CommandPermissionsLevel.SERVER, true));
        commands.add(new CommandUsageCommand("commanduse", CommandPermissionsLevel.SERVER, true));
        commands.add(new ServerPropertiesCommand("serverproperties", CommandPermissionsLevel.CONSOLE, true));
        commands.add(new MainCommand("csc", CommandPermissionsLevel.ALL, true));
        commands.add(new MainCommand("cheesecakeservercommands", CommandPermissionsLevel.ALL, true));
        commands.add(new AdminSilenceCommand("adminsilence", CommandPermissionsLevel.CONSOLE, true));

        // Admin Commands
        commands.add(new GMCommand("gmc", CommandPermissionsLevel.OPS, true, GameType.CREATIVE, "Creative Mode"));
        commands.add(new GMCommand("gms", CommandPermissionsLevel.OPS, true, GameType.SURVIVAL, "Survival Mode"));
        commands.add(new GMCommand("gma", CommandPermissionsLevel.OPS, true, GameType.ADVENTURE, "Adventure Mode"));
        commands.add(new GMCommand("gmsp", CommandPermissionsLevel.OPS, true, GameType.SPECTATOR, "Spectator Mode"));
        commands.add(new ZeusCommand("zeus", CommandPermissionsLevel.OPS, true));
        commands.add(new WowCommand("wow", CommandPermissionsLevel.ALL, true));
        commands.add(new WowCommand("doge", CommandPermissionsLevel.ALL, true));
        commands.add(new FlingCommand("fling", CommandPermissionsLevel.OPS, true));
        commands.add(new KickCommand("pkick", CommandPermissionsLevel.OPS, true));

        commands.add(new InvSeeEnderChestCommand("invseeender", CommandPermissionsLevel.OPS, true));
        commands.add(new ClearInventoryCommand("clearinventory", CommandPermissionsLevel.OPS, true));
        commands.add(new ClearInventoryCommand("ci", CommandPermissionsLevel.OPS, true));
        commands.add(new GiveItemCommand("giveitem", CommandPermissionsLevel.OPS, true));
        commands.add(new GiveItemCommand("i", CommandPermissionsLevel.OPS, true));
        commands.add(new MoreItemsCommand("more", CommandPermissionsLevel.OPS, true));

        // Adapted from Essentials

        // Essentials Server Commands
        commands.add(new FlyCommand("fly", CommandPermissionsLevel.OPS, true));
        commands.add(new GodCommand("god", CommandPermissionsLevel.OPS, true));
        commands.add(new SpeedCommand("speed", CommandPermissionsLevel.OPS, true));
        commands.add(new HealCommand("heal", CommandPermissionsLevel.OPS, true));
        commands.add(new FeedCommand("feed", CommandPermissionsLevel.OPS, true));
        commands.add(new SmiteCommand("smite", CommandPermissionsLevel.OPS, true));
        commands.add(new KillCommand("pkill", CommandPermissionsLevel.OPS, true));
        commands.add(new InvSeeCommand("invsee", CommandPermissionsLevel.OPS, true));
        commands.add(new BurnCommand("burn", CommandPermissionsLevel.OPS, true));
        commands.add(new LocateCommand("loc", CommandPermissionsLevel.OPS, true));
        commands.add(new LocateCommand("locateplayer", CommandPermissionsLevel.OPS, true));
        commands.add(new LocateCommand("gps", CommandPermissionsLevel.OPS, true));
        commands.add(new SudoCommand("sudo", CommandPermissionsLevel.OPS, true));
        commands.add(new GamemodeCommand("gm", CommandPermissionsLevel.OPS, true));
        commands.add(new TpToCommand("tpto", CommandPermissionsLevel.OPS, true));
        commands.add(new TpHereCommand("tphere", CommandPermissionsLevel.OPS, true));

        // Essentials Items
        commands.add(new CraftCommand("craft", CommandPermissionsLevel.OPS, true));
        commands.add(new DechantCommand("dechantitem", CommandPermissionsLevel.OPS, true));
        commands.add(new EnchantCommand("enchantitem", CommandPermissionsLevel.OPS, true));
        commands.add(new DuplicateCommand("duplicate", CommandPermissionsLevel.OPS, true));
        commands.add(new EnderChestCommand("enderchest", CommandPermissionsLevel.OPS, true));
        commands.add(new RenameCommand("renameitem", CommandPermissionsLevel.OPS, true));
        commands.add(new RepairCommand("repairitem", CommandPermissionsLevel.OPS, true));

        // Essentials Server
        commands.add(new ModlistCommand("modlist", CommandPermissionsLevel.OPS, true));
        commands.add(new ServerSettingsCommand("serversettings", CommandPermissionsLevel.CONSOLE, true));
        commands.add(new ServerStatisticsCommand("serverstats", CommandPermissionsLevel.CONSOLE, true));
        commands.add(new GarbageCollectorCommand("gc", CommandPermissionsLevel.CONSOLE, true));
        commands.add(new GetCommandBookCommand("getcommandbook", CommandPermissionsLevel.OPS, true));

        // Essentials World
        commands.add(new BiomeInfoCommand("biomeinfo", CommandPermissionsLevel.OPS, true));

        // General Commands (For all players)
        commands.add(new PingCommand("ping", CommandPermissionsLevel.ALL, true));

        // NBS Player
        commands.add(new NoteblockSongsCommand("noteblocksongs", CommandPermissionsLevel.OPS, true));
        commands.add(new NoteblockSongsCommand("midi", CommandPermissionsLevel.OPS, true));
        commands.add(new NoteblockSongsCommand("nbs", CommandPermissionsLevel.OPS, true));

        // Register to Forge
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        commands.forEach(cmd -> {
            if (cmd.setExecution() != null) {
                LOGGER.info("Added command: {}", cmd.getName());
                dispatcher.register(cmd.getBuilder());
            }
        });
        LOGGER.info("Command registration complete");
    }

    // Server stopping event. Save stuff here
    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        LOGGER.info("Server stopping... Flushing last known usernames to file");
        LastKnownUsernameJsonHelper.writeToFile();
    }
}
