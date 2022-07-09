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
}
