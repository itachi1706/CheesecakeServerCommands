package com.itachi1706.cheesecakeservercommands.util;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Server Player Utilities (formerly PlayerMPUtil)
 */
public class ServerPlayerUtil {

    private ServerPlayerUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean isOperator(Player player){
        if (ServerUtil.getServerInstance().isSingleplayer())
            return true;

        GameProfile profile = player.getGameProfile();
        return ServerUtil.getServerPlayers().isOp(profile);
    }

    public static ServerPlayer castToPlayer(CommandSourceStack sender){
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

    /**
     * Give player the item stack or drop it if his inventory is full
     * If the player inventory fills up while it gets full, spill over to the ground
     *
     * @param player Player object
     * @param item Item to give
     */
    public static void give(Player player, ItemStack item)
    {
        int itemstack = item.getCount();
        while (itemstack > 64) {
            ItemStack senditem = item.copy();
            senditem.setCount(64);
            ItemEntity entityitem = player.drop(senditem, false);
            if (entityitem == null) continue;
            entityitem.setNoPickUpDelay();
            entityitem.setOwner(player.getUUID());
            itemstack -= 64;
        }
        item.setCount(itemstack);
        ItemEntity entityitem = player.drop(item, false);
        if (entityitem == null) return;
        entityitem.setNoPickUpDelay();
        entityitem.setOwner(player.getUUID());
    }

    /**
     * Give player the item stack or drop it if his inventory is full
     * This command execute without ground spillage if player is given into his/her inventory
     *
     * @param player Player object
     * @param item Item to give
     */
    public static void giveNormal(Player player, ItemStack item)
    {
        ItemEntity entityitem = player.drop(item, false);
        if (entityitem == null) return;
        entityitem.setNoPickUpDelay();
        entityitem.setOwner(player.getUUID());
    }

}
