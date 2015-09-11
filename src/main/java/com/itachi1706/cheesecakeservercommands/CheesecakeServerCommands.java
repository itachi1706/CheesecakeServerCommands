package com.itachi1706.cheesecakeservercommands;

import com.itachi1706.cheesecakeservercommands.jsonstorage.Logins;
import com.itachi1706.cheesecakeservercommands.jsonstorage.LoginsJsonHelper;
import com.itachi1706.cheesecakeservercommands.proxy.IProxy;
import com.itachi1706.cheesecakeservercommands.reference.References;
import com.itachi1706.cheesecakeservercommands.server.commands.CCLoggerCommand;
import com.itachi1706.cheesecakeservercommands.server.commands.SampleCommand;
import com.itachi1706.cheesecakeservercommands.util.LogHelper;
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

    public static List<Logins> loginLogoutLogger;
    public static File configFileDirectory;

    @Mod.Instance(References.MOD_ID)
    public static CheesecakeServerCommands instance;

    @SidedProxy(clientSide = References.CLIENT_PROXY, serverSide = References.SERVER_PROXY)
    public static IProxy proxy;

    @Mod.EventHandler
    public void FMLPreInitEvent(FMLPreInitializationEvent event){
        File file = new File(event.getModConfigurationDirectory().getAbsolutePath() + File.separator + "cheesecakeserver");
        LogHelper.info(">>> File name: " + file.getAbsolutePath());
        if (!file.exists() && file.mkdir()){
            LogHelper.info("Created Cheesecake Server Internal Directory");
        }

        configFileDirectory = file;
    }

    @Mod.EventHandler
    public void serverStartingEvent(FMLServerStartingEvent event){
        //Register Loggers
        registerLoggers();

        //Register Commands
        event.registerServerCommand(new SampleCommand());
        event.registerServerCommand(new CCLoggerCommand());
    }

    @Mod.EventHandler
    public void serverStoppingEvent(FMLServerStoppingEvent event){
        LoginsJsonHelper.writeToFile();
    }

    private void registerLoggers(){
        loginLogoutLogger = new ArrayList<Logins>();
        if (LoginsJsonHelper.fileExists())
            loginLogoutLogger = LoginsJsonHelper.readFromFile();

    }

}
