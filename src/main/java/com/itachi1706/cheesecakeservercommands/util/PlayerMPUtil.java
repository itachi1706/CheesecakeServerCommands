package com.itachi1706.cheesecakeservercommands.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.List;

/**
 * Created by Kenneth on 9/11/2015.
 * for CheesecakeServerCommands in package com.itachi1706.cheesecakeservercommands.util
 */
public class PlayerMPUtil {

    public static boolean isOperatorOrConsole(ICommandSender iCommandSender) {
        return !(sender instanceof EntityPlayer) || isOperator((EntityPlayer) sender);
    }

    //TODO: Move to better area
    public static MinecraftServer getServerInstance() {
        return FMLCommonHandler.instance().getMinecraftServerInstance();
    }

    public static boolean isOperator(EntityPlayer player){
        if (getServerInstance().isSinglePlayer())
            return true;

        GameProfile profile = player.getGameProfile();
        return PlayerMPUtil.getServerInstance().getConfigurationManager().func_152596_g(profile);
    }

    public static boolean isPlayer(ICommandSender iCommandSender){
        return sender instanceof EntityPlayer;
    }

    public static EntityPlayer castToPlayer(ICommandSender iCommandSender){
        if (isPlayer(sender))
            return (EntityPlayer) sender;
        return null;
    }

    @SuppressWarnings("unchecked")
    public static List<EntityPlayerMP> getOnlinePlayers(){
        return PlayerMPUtil.getServerInstance().getConfigurationManager().playerEntityList;
    }

    public static EntityPlayerMP getPlayer(String username) {
        List<EntityPlayerMP> players = getOnlinePlayers();
        for (EntityPlayerMP playerMP : players) {
            if (playerMP.getName().equals(username)) {
                return playerMP;
            }
        }
        return null;
    }

    /**
     * Get player's looking-at spot.
     *
     * @param player
     * @return The position as a MovingObjectPosition Null if not existent.
     */
    public static MovingObjectPosition getPlayerLookingSpot(EntityPlayer player)
    {
        if (player instanceof EntityPlayerMP)
            return getPlayerLookingSpot(player, ((EntityPlayerMP) player).theItemInWorldManager.getBlockReachDistance());
        else
            return getPlayerLookingSpot(player, 5);
    }

    /**
     * Get player's looking spot.
     *
     * @param player
     * @param maxDistance
     *            Keep max distance to 5.
     * @return The position as a MovingObjectPosition Null if not existent.
     */
    public static MovingObjectPosition getPlayerLookingSpot(EntityPlayer player, double maxDistance)
    {
        Vec3 lookAt = player.getLook(1);
        Vec3 playerPos = Vec3.createVectorHelper(player.posX, player.posY + (player.getEyeHeight() - player.getDefaultEyeHeight()), player.posZ);
        Vec3 pos1 = playerPos.addVector(0, player.getEyeHeight(), 0);
        Vec3 pos2 = pos1.addVector(lookAt.xCoord * maxDistance, lookAt.yCoord * maxDistance, lookAt.zCoord * maxDistance);
        return player.worldObj.rayTraceBlocks(pos1, pos2);
    }

    /**
     * Give player the item stack or drop it if his inventory is full
     * If the player inventory fills up while it gets full, spill over to the ground
     *
     * @param player
     * @param item
     */
    public static void give(EntityPlayer player, ItemStack item)
    {
        int itemstack = item.stackSize;
        while (itemstack > 64) {
            ItemStack senditem = item.copy();
            senditem.stackSize = 64;
            EntityItem entityitem = player.dropItem(senditem, false);
            if (entityitem == null) continue;
            entityitem.setNoPickupDelay();
            entityitem.setOwner(player.getName());
            itemstack -= 64;
        }
        item.stackSize = itemstack;
        EntityItem entityitem = player.dropItem(item, false);
        if (entityitem == null) return;
        entityitem.setNoPickupDelay();
        entityitem.setOwner(player.getName());
    }

    /**
     * Give player the item stack or drop it if his inventory is full
     * This command execute without ground spillage if player is given into his/her inventory
     *
     * @param player
     * @param item
     */
    public static void giveNormal(EntityPlayer player, ItemStack item)
    {
        EntityItem entityitem = player.dropItem(item, false);
        if (entityitem == null) return;
        entityitem.setNoPickupDelay();
        entityitem.setOwner(player.getName());
    }
}
