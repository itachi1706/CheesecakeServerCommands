package com.itachi1706.cheesecakeservercommands.server.commands.admin;

import com.itachi1706.cheesecakeservercommands.util.ChatHelper;
import com.itachi1706.cheesecakeservercommands.util.PlayerMPUtil;
import com.itachi1706.cheesecakeservercommands.util.ServerUtil;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.WorldServer;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

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
    public void execute(MinecraftServer server, ICommandSender iCommandSender, String[] args) throws CommandException {
        if (args.length == 0)
        {
            if (!PlayerMPUtil.isPlayer(iCommandSender)) {
                ChatHelper.sendMessage(iCommandSender, "Cannot fling CONSOLE");
                return;
            } else {
                EntityPlayerMP player = (EntityPlayerMP) PlayerMPUtil.castToPlayer(iCommandSender);
                if (player == null) {
                    ChatHelper.sendMessage(iCommandSender, "Cannot fling" + iCommandSender.getName());
                    return;
                }

                //player.worldObj.spawnParticle("hugeexplosion", player.posX + 0.5D, player.posY + 1.0D, player.posZ + 0.5D, 1.0D, 0.0D, 0.0D);
                player.motionY = 10;
                player.velocityChanged = true;
                ;
                player.addPotionEffect(new PotionEffect(Potion.getPotionFromResourceLocation("resistance"), 300, 100, true, false));
                WorldServer worldServer = (WorldServer) player.worldObj;
                worldServer.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, player.posX, player.posY, player.posZ, 0, 0, 0, 0, 0, new int[0]);
                Explosion explosion = new Explosion(player.worldObj, player, player.posX, player.posY, player.posZ, 0, true, true);
                explosion.doExplosionB(true);
                ChatHelper.sendMessage(iCommandSender, ChatFormatting.DARK_PURPLE + "You were flung into the air");
                ChatHelper.sendAdminMessage(iCommandSender, "Flung own self into the air!");
                return;
            }
        }

        String subname = args[0];
        EntityPlayerMP player = PlayerMPUtil.getPlayer(subname);
        if (player == null) {
            ChatHelper.sendMessage(iCommandSender, ChatFormatting.RED + "Player not found");
            return;
        }

        player.motionY = 10;
        player.velocityChanged = true;
        player.addPotionEffect(new PotionEffect(Potion.getPotionFromResourceLocation("resistance"), 300, 100, true, false));
        WorldServer worldServer = (WorldServer) player.worldObj;
        worldServer.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, player.posX, player.posY, player.posZ, 0, 0, 0, 0, 0, new int[0]);
        Explosion explosion = new Explosion(player.worldObj, player, player.posX, player.posY, player.posZ, 0, true, true);
        explosion.doExplosionB(true);
        ChatHelper.sendMessage(iCommandSender, ChatFormatting.GOLD + "Flung " + player.getName() + " into the air!");
        ChatHelper.sendAdminMessage(iCommandSender, "Flung " + player.getName() + " into the air");
        ChatHelper.sendMessage(player, ChatFormatting.DARK_PURPLE + "You were flung into the air");
    }

    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender iCommandSender, String[] typedValue, @Nullable BlockPos pos) {
        if (typedValue.length == 1)
            return CommandBase.getListOfStringsMatchingLastWord(typedValue, ServerUtil.getServerInstance().getAllUsernames());
        return null;
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender iCommandSender) {
        return PlayerMPUtil.isOperatorOrConsole(iCommandSender);
    }

    @Override
    public boolean isUsernameIndex(String[] p_82358_1_, int p_82358_2_) {
        return false;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public int compareTo(ICommand o) {
        return 0;
    }
}
