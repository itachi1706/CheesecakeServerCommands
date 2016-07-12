package com.itachi1706.cheesecakeservercommands.util;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

/**
 * Created by Kenneth on 11/7/2016.
 * for com.itachi1706.cheesecakeservercommands.util in CheesecakeServerCommands
 */
public class ServerUtil {

    public static MinecraftServer getServerInstance() {
        return FMLCommonHandler.instance().getMinecraftServerInstance();
    }
}
