package com.itachi1706.cheesecakeservercommands.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import java.util.List;

/**
 * Created by Kenneth on 9/11/2015.
 * for CheesecakeServerCommands in package com.itachi1706.cheesecakeservercommands.util
 */
public class PlayerMPUtil {

    public static boolean isOperatorOrConsole(ICommandSender sender) {
        return !(sender instanceof EntityPlayer) || isOperator((EntityPlayer) sender);
    }

    public static boolean isOperator(EntityPlayer player){
        EntityPlayerMP playerMP = (EntityPlayerMP) player;

        if (MinecraftServer.getServer().isSinglePlayer())
            return true;

        GameProfile profile = player.getGameProfile();
        return MinecraftServer.getServer().getConfigurationManager().func_152596_g(profile);
    }

    public static boolean isPlayer(ICommandSender sender){
        return sender instanceof EntityPlayer;
    }

    public static EntityPlayer castToPlayer(ICommandSender sender){
        if (isPlayer(sender))
            return (EntityPlayer) sender;
        return null;
    }

    @SuppressWarnings("unchecked")
    public static List<EntityPlayerMP> getOnlinePlayers(){
        return MinecraftServer.getServer().getConfigurationManager().playerEntityList;
    }
}
