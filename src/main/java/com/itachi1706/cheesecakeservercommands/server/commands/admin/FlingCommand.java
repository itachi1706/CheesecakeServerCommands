package com.itachi1706.cheesecakeservercommands.server.commands.admin;

import com.itachi1706.cheesecakeservercommands.util.ChatHelper;
import com.itachi1706.cheesecakeservercommands.util.PlayerMPUtil;
import net.minecraft.client.particle.EntitySmokeFX;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.Explosion;
import net.minecraft.world.WorldServer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Kenneth on 9/11/2015.
 * for CheesecakeServerCommands in package com.itachi1706.cheesecakeservercommands.server.commands
 */
@SuppressWarnings("unused")
public class FlingCommand implements ICommand {

    private List<String> aliases;

    public FlingCommand(){
        this.aliases = new ArrayList<String>();
        this.aliases.add("fling");
    }

    @Override
    public String getCommandName() {
        return "fling";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_) {
        return "fling [player]";
    }

    @Override
    public List getCommandAliases() {
        return this.aliases;
    }

    @Override
    public void processCommand(ICommandSender iCommandSender, String[] astring) {
        if (astring.length == 0)
        {
            if (!PlayerMPUtil.isPlayer(iCommandSender)) {
                ChatHelper.sendMessage(iCommandSender, "Cannot fling CONSOLE");
                return;
            } else {
                EntityPlayerMP player = (EntityPlayerMP) PlayerMPUtil.castToPlayer(iCommandSender);
                if (player == null) {
                    ChatHelper.sendMessage(iCommandSender, "Cannot fling" + iCommandSender.getCommandSenderName());
                    return;
                }

                //player.worldObj.spawnParticle("hugeexplosion", player.posX + 0.5D, player.posY + 1.0D, player.posZ + 0.5D, 1.0D, 0.0D, 0.0D);
                player.motionY = 10;
                player.velocityChanged = true;
                player.addPotionEffect(new PotionEffect(Potion.resistance.id, 300, 100, true));
                WorldServer worldServer = (WorldServer) player.worldObj;
                worldServer.func_147487_a("hugeexplosion", player.posX, player.posY, player.posZ, 0, 0, 0, 0, 0);
                Explosion explosion = new Explosion(player.worldObj, player, player.posX, player.posY, player.posZ, 0);
                explosion.doExplosionB(true);
                ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.DARK_PURPLE + "You were flung into the air");
                ChatHelper.sendAdminMessage(iCommandSender, "Flung own self into the air!");
                return;
            }
        }

        String subname = astring[0];
        EntityPlayerMP player = PlayerMPUtil.getPlayer(subname);
        if (player == null) {
            ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.RED + "Player not found");
            return;
        }

        player.motionY = 10;
        player.velocityChanged = true;
        player.addPotionEffect(new PotionEffect(Potion.resistance.id, 300, 100, true));
        WorldServer worldServer = (WorldServer) player.worldObj;
        worldServer.func_147487_a("hugeexplosion", player.posX, player.posY, player.posZ, 0, 0, 0, 0, 0);
        Explosion explosion = new Explosion(player.worldObj, player, player.posX, player.posY, player.posZ, 0);
        explosion.doExplosionB(true);
        ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.GOLD + "Flung " + player.getCommandSenderName() + " into the air!");
        ChatHelper.sendAdminMessage(iCommandSender, "Flung " + player.getCommandSenderName() + " into the air");
        ChatHelper.sendMessage(player, EnumChatFormatting.DARK_PURPLE + "You were flung into the air");
    }

    @Override
    public List addTabCompletionOptions(ICommandSender iCommandSender, String[] typedValue) {
        if (typedValue.length == 1)
            return CommandBase.getListOfStringsMatchingLastWord(typedValue, MinecraftServer.getServer().getAllUsernames());
        return null;
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender iCommandSender) {
        return PlayerMPUtil.isOperatorOrConsole(iCommandSender);
    }

    @Override
    public boolean isUsernameIndex(String[] p_82358_1_, int p_82358_2_) {
        return false;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
