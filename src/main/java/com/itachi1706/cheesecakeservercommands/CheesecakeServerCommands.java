package com.itachi1706.cheesecakeservercommands;

import com.itachi1706.cheesecakeservercommands.dbstorage.CommandsLogDB;
import com.itachi1706.cheesecakeservercommands.dbstorage.LoginLogoutDB;
import com.itachi1706.cheesecakeservercommands.events.PlayerEvents;
import com.itachi1706.cheesecakeservercommands.jsonstorage.LastKnownUsernameJsonHelper;
import com.itachi1706.cheesecakeservercommands.jsonstorage.LastKnownUsernames;
import com.itachi1706.cheesecakeservercommands.jsonstorage.MP3Songs;
import com.itachi1706.cheesecakeservercommands.jsonstorage.MP3SongsJsonHelper;
import com.itachi1706.cheesecakeservercommands.nbtstorage.AdminSilenced;
import com.itachi1706.cheesecakeservercommands.nbtstorage.CSCAdminSilenceWorldSavedData;
import com.itachi1706.cheesecakeservercommands.noteblocksongs.NoteblockSongs;
import com.itachi1706.cheesecakeservercommands.proxy.IProxy;
import com.itachi1706.cheesecakeservercommands.reference.InitDamageSources;
import com.itachi1706.cheesecakeservercommands.reference.References;
import com.itachi1706.cheesecakeservercommands.server.commands.*;
import com.itachi1706.cheesecakeservercommands.server.commands.admin.*;
import com.itachi1706.cheesecakeservercommands.server.commands.admin.item.*;
import com.itachi1706.cheesecakeservercommands.server.commands.admin.server.GetCommandBookCommand;
import com.itachi1706.cheesecakeservercommands.server.commands.admin.server.ModlistCommand;
import com.itachi1706.cheesecakeservercommands.server.commands.admin.server.ServerSettingsCommand;
import com.itachi1706.cheesecakeservercommands.server.commands.admin.world.BiomeInfoCommand;
import com.itachi1706.cheesecakeservercommands.util.LogHelper;
import com.itachi1706.cheesecakeservercommands.util.TeleportHelper;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Kenneth on 9/11/2015.
 * for CheesecakeServerCommands in package com.itachi1706.cheesecakeservercommands
 */
@Mod(modid = References.MOD_ID, name=References.MOD_NAME, version=References.VERSION, acceptableRemoteVersions = "*")
public class CheesecakeServerCommands {

    public static List<LastKnownUsernames> lastKnownUsernames;
    public static List<MP3Songs> mp3SongsList;
    public static File configFileDirectory;
    public static HashMap<String, DamageSource> knownDamageSources;

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
        MinecraftForge.EVENT_BUS.register(new PlayerEvents());
        MinecraftForge.EVENT_BUS.register(new TeleportHelper());
    }

    @Mod.EventHandler
    public void serverStartingEvent(FMLServerStartingEvent event){
        //Register Loggers
        registerLoggers();
        CSCAdminSilenceWorldSavedData.get(event.getServer().getEntityWorld(), true);
        LogHelper.info("Admin Silenced List: " + AdminSilenced.getState());

        //Register Commands
        //event.registerServerCommand(new SampleCommand());
        event.registerServerCommand(new CCLoggerCommand());
        event.registerServerCommand(new CommandUsageCommand());
        event.registerServerCommand(new MojangServerCommand());
        event.registerServerCommand(new ServerPropertiesCommand());
        event.registerServerCommand(new MainCommand());

        // Admin Commands
        event.registerServerCommand(new GMCCommand());
        event.registerServerCommand(new GMSCommand());
        event.registerServerCommand(new GMACommand());
        event.registerServerCommand(new GMSPCommand());
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
        event.registerServerCommand(new GodCommand());
        event.registerServerCommand(new SpeedCommand());
        event.registerServerCommand(new HealCommand());
        event.registerServerCommand(new FeedCommand());
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

        // General Commands (For all players)
        event.registerServerCommand(new PingCommand());

        // Initialize Damage Sources
        InitDamageSources.initalizeDamages();

        // NBS Player
        event.registerServerCommand(new NoteblockSongsCommand());
        NoteblockSongs.refreshSongs();
    }

    @Mod.EventHandler
    public void serverStoppingEvent(FMLServerStoppingEvent event){
        LastKnownUsernameJsonHelper.writeToFile();
    }

    private void registerLoggers(){
        lastKnownUsernames = new ArrayList<>();
        if (LastKnownUsernameJsonHelper.fileExists())
            lastKnownUsernames = LastKnownUsernameJsonHelper.readFromFile();

        mp3SongsList = new ArrayList<>();
        if (MP3SongsJsonHelper.fileExists())
            mp3SongsList = MP3SongsJsonHelper.readFromFile();

        LoginLogoutDB.checkTablesExists();
        CommandsLogDB.checkTablesExists();
    }

}
