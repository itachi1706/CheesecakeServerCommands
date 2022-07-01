package com.itachi1706.cheesecakeservercommands;

import com.itachi1706.cheesecakeservercommands.commands.BaseCommand;
import com.itachi1706.cheesecakeservercommands.commands.MainCommand;
import com.itachi1706.cheesecakeservercommands.commands.PingCommand;
import com.itachi1706.cheesecakeservercommands.commands.admin.WowCommand;
import com.itachi1706.cheesecakeservercommands.commands.admin.ZeusCommand;
import com.itachi1706.cheesecakeservercommands.commands.admin.server.ServerStatisticsCommand;
import com.itachi1706.cheesecakeservercommands.events.PlayerEvents;
import com.itachi1706.cheesecakeservercommands.jsonstorage.LastKnownUsernameJsonHelper;
import com.itachi1706.cheesecakeservercommands.jsonstorage.LastKnownUsernames;
import com.itachi1706.cheesecakeservercommands.reference.CommandPermissionsLevel;
import com.itachi1706.cheesecakeservercommands.reference.InitDamageSources;
import com.itachi1706.cheesecakeservercommands.reference.References;
import com.itachi1706.cheesecakeservercommands.util.LogHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.logging.LogUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
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

    private static File configFileDirectory;
    private static Map<String, DamageSource> knownDamageSources;
    private static final ArrayList<BaseCommand> commands = new ArrayList<>();

    private static List<LastKnownUsernames> lastKnownUsernames = new ArrayList<>();

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
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        // some preinit code
        LOGGER.info("HELLO FROM PREINIT");

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

        // Init OS Bean
        setPlatformBean(ManagementFactory.getPlatformMBeanServer());

        InitDamageSources.initalizeDamages();
    }

    private void registerLoggers(){
        if (LastKnownUsernameJsonHelper.fileExists())
            setLastKnownUsernames(LastKnownUsernameJsonHelper.readFromFile());

//        LoginLogoutDB.checkTablesExists();
//        CommandsLogDB.checkTablesExists();
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
        commands.add(new MainCommand("csc", CommandPermissionsLevel.ALL, true));
        commands.add(new MainCommand("cheesecakeservercommands", CommandPermissionsLevel.ALL, true));

        // Admin Commands
        commands.add(new ZeusCommand("zeus", CommandPermissionsLevel.OPS, true));
        commands.add(new WowCommand("wow", CommandPermissionsLevel.ALL, true));
        commands.add(new WowCommand("doge", CommandPermissionsLevel.ALL, true));

        // Essentials Server
        commands.add(new ServerStatisticsCommand("serverstats", CommandPermissionsLevel.CONSOLE, true));

        // General Commands (For all players)
        commands.add(new PingCommand("ping", CommandPermissionsLevel.ALL, false));

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
        LOGGER.info("HELLO from server stopping");
    }
}
