package com.itachi1706.cheesecakeservercommands.util;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.List;

/**
 * Server Player Utilities (formerly PlayerMPUtil)
 */
public class ServerPlayerUtil {

    public static boolean isOperator(Player player){
        if (ServerUtil.getServerInstance().isSingleplayer())
            return true;

        GameProfile profile = player.getGameProfile();
        return ServerUtil.getServerPlayers().isOp(profile);
    }

    public static Player castToPlayer(CommandSourceStack sender){
        try {
            return sender.getPlayerOrException();
        } catch (CommandSyntaxException e) {
            return null;
        }
    }

    public static boolean isPlayer(CommandSourceStack sender){
        return castToPlayer(sender) != null;
    }

    public static List<ServerPlayer> getOnlinePlayers(){
        return ServerUtil.getServerPlayers().getPlayers();
    }

    public static ServerPlayer getPlayer(String username) {
        List<ServerPlayer> players = getOnlinePlayers();
        for (ServerPlayer playerMP : players) {
            if (playerMP.getName().getString().equals(username)) {
                return playerMP;
            }
        }
        return null;
    }

}
