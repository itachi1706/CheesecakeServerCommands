package com.itachi1706.cheesecakeservercommands.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import java.util.List;

/**
 * Created by Kenneth on 9/11/2015.
 * for CheesecakeServerCommands in package com.itachi1706.cheesecakeservercommands.util
 */
public class PlayerMPUtil {

    public static boolean isOperatorOrConsole(ICommandSender sender) {
        return !(sender instanceof EntityPlayer) || isOperator((EntityPlayer) sender);
    }

    /**
     * Get player's looking-at spot.
     *
     * @param player
     * @return The position as a RayTraceResult Null if not existent.
     */
    public static RayTraceResult getPlayerLookingSpot(EntityPlayer player)
    {
        return getPlayerLookingSpot(player, player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue());
    }

    /**
     * Get player's looking spot.
     *
     * @param player
     * @param maxDistance
     *            Keep max distance to 5.
     * @return The position as a RayTraceResult Null if not existent.
     */
    public static RayTraceResult getPlayerLookingSpot(EntityPlayer player, double maxDistance)
    {
        Vec3d lookAt = player.getLook(1);
        Vec3d playerPos = new Vec3d(player.posX, player.posY + (player.getEyeHeight() - player.getDefaultEyeHeight()), player.posZ);
        Vec3d pos1 = playerPos.addVector(0, player.getEyeHeight(), 0);
        Vec3d pos2 = pos1.addVector(lookAt.x * maxDistance, lookAt.y * maxDistance, lookAt.z * maxDistance);
        return player.world.rayTraceBlocks(pos1, pos2);
    }
}
