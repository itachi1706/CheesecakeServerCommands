package com.itachi1706.cheesecakeservercommands;

import com.itachi1706.cheesecakeservercommands.dbstorage.LoginLogoutDB;
import com.itachi1706.cheesecakeservercommands.events.PlayerEvents;
import com.itachi1706.cheesecakeservercommands.jsonstorage.LastKnownUsernameJsonHelper;
import com.itachi1706.cheesecakeservercommands.jsonstorage.LastKnownUsernames;
import com.itachi1706.cheesecakeservercommands.proxy.IProxy;
import com.itachi1706.cheesecakeservercommands.reference.References;
import com.itachi1706.cheesecakeservercommands.server.commands.CCLoggerCommand;
import com.itachi1706.cheesecakeservercommands.server.commands.MainCommand;
import com.itachi1706.cheesecakeservercommands.server.commands.MojangServerCommand;
import com.itachi1706.cheesecakeservercommands.server.commands.ServerPropertiesCommand;
import com.itachi1706.cheesecakeservercommands.server.commands.admin.*;
import com.itachi1706.cheesecakeservercommands.server.commands.admin.item.*;
import com.itachi1706.cheesecakeservercommands.server.commands.admin.server.GetCommandBookCommand;
import com.itachi1706.cheesecakeservercommands.server.commands.admin.server.ModlistCommand;
import com.itachi1706.cheesecakeservercommands.server.commands.admin.server.ServerSettingsCommand;
import com.itachi1706.cheesecakeservercommands.server.commands.admin.world.BiomeInfoCommand;
import com.itachi1706.cheesecakeservercommands.util.LogHelper;
import com.itachi1706.cheesecakeservercommands.util.TeleportHelper;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kenneth on 9/11/2015.
 * for CheesecakeServerCommands in package com.itachi1706.cheesecakeservercommands
 */
@Mod(modid = References.MOD_ID, name=References.MOD_NAME, version=References.VERSION, acceptableRemoteVersions = "*")
public class CheesecakeServerCommands {

    public static List<LastKnownUsernames> lastKnownUsernames;
    public static File configFileDirectory;

    @Mod.Instance(References.MOD_ID)
    public static CheesecakeServerCommands instance;

    @SidedProxy(clientSide = References.CLIENT_PROXY, serverSide = References.SERVER_PROXY)
    public static IProxy proxy;

    @Mod.EventHandler
    public void FMLPreInitEvent(FMLPreInitializationEvent event){
        File file = new File(event.getModConfigurationDirectory().getAbsolutePath() + File.separator + "cheesecakeserver");
        LogHelper.info(">>> Folder name: " + file.getAbsolutePath());
        if (!file.exists() && file.mkdir()){
            LogHelper.info("Created Cheesecake Server Internal Directory");
        }

        configFileDirectory = file;

    }

    @Mod.EventHandler
    public void FMLInitEvent(FMLInitializationEvent event){
        FMLCommonHandler.instance().bus().register(new PlayerEvents());
        FMLCommonHandler.instance().bus().register(new TeleportHelper());
    }

    @Mod.EventHandler
    public void serverStartingEvent(FMLServerStartingEvent event){
        //Register Loggers
        registerLoggers();

        //Register Commands
        //event.registerServerCommand(new SampleCommand());
        event.registerServerCommand(new CCLoggerCommand());
        event.registerServerCommand(new MojangServerCommand());
        event.registerServerCommand(new ServerPropertiesCommand());
        event.registerServerCommand(new MainCommand());

        // Admin Commands
        event.registerServerCommand(new GMCCommand());
        event.registerServerCommand(new GMSCommand());
        event.registerServerCommand(new GMACommand());
        event.registerServerCommand(new ZeusCommand());
        event.registerServerCommand(new WowCommand());
        event.registerServerCommand(new FlingCommand());
        event.registerServerCommand(new KickCommand());

        event.registerServerCommand(new EnchantForceCommand());
        event.registerServerCommand(new InvSeeEnderChestCommand());
        event.registerServerCommand(new ClearInventoryCommand());
        event.registerServerCommand(new GiveItemCommand());
        event.registerServerCommand(new MoreItemsCommand());

        // Adapted from Essentials

        // Essentials Server Commands
        event.registerServerCommand(new FlyCommand());
        event.registerServerCommand(new SpeedCommand());
        event.registerServerCommand(new HealCommand());
        event.registerServerCommand(new SmiteCommand());
        event.registerServerCommand(new KillCommand());
        event.registerServerCommand(new InvSeeCommand());
        event.registerServerCommand(new BurnCommand());
        event.registerServerCommand(new LocateCommand());
        event.registerServerCommand(new SudoCommand());
        event.registerServerCommand(new GamemodeCommand());
        event.registerServerCommand(new TpToCommand());
        event.registerServerCommand(new TpHereCommand());

        // Essentials Items
        event.registerServerCommand(new CraftCommand());
        event.registerServerCommand(new DechantCommand());
        event.registerServerCommand(new EnchantCommand());
        event.registerServerCommand(new DuplicateCommand());
        event.registerServerCommand(new EnderChestCommand());
        event.registerServerCommand(new RenameCommand());
        event.registerServerCommand(new RepairCommand());

        // Essentials Server
        event.registerServerCommand(new ModlistCommand());
        event.registerServerCommand(new ServerSettingsCommand());
        event.registerServerCommand(new GetCommandBookCommand());

        // Essentials World
        event.registerServerCommand(new BiomeInfoCommand());
    }

    @Mod.EventHandler
    public void serverStoppingEvent(FMLServerStoppingEvent event){
        LastKnownUsernameJsonHelper.writeToFile();
    }

    private void registerLoggers(){
        lastKnownUsernames = new ArrayList<LastKnownUsernames>();
        if (LastKnownUsernameJsonHelper.fileExists())
            lastKnownUsernames = LastKnownUsernameJsonHelper.readFromFile();

        LoginLogoutDB.checkTablesExists();

    }

}
