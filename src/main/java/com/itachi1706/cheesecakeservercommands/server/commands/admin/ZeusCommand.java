package com.itachi1706.cheesecakeservercommands.server.commands.admin;

import com.itachi1706.cheesecakeservercommands.damagesource.ZeusDamage;
import com.itachi1706.cheesecakeservercommands.util.ChatHelper;
import com.itachi1706.cheesecakeservercommands.util.PlayerMPUtil;
import com.itachi1706.cheesecakeservercommands.util.ServerUtil;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kenneth on 9/11/2015.
 * for CheesecakeServerCommands in package com.itachi1706.cheesecakeservercommands.server.commands
 */
@SuppressWarnings("unused")
public class ZeusCommand implements ICommand {

    private List<String> aliases;

    public ZeusCommand(){
        this.aliases = new ArrayList<String>();
        this.aliases.add("zeus");
    }

    @Override
    public String getCommandName() {
        return "zeus";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_) {
        return "zeus [player]";
    }

    @Override
    public List getCommandAliases() {
        return this.aliases;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender iCommandSender, String[] args) throws CommandException {
        if (args.length == 0)
        {
            // Wrath of Zeus yourself
            if (!PlayerMPUtil.isPlayer(iCommandSender)) {
                ChatHelper.sendMessage(iCommandSender, "CONSOLE cannot feel the wrath of Zeus");
                return;
            }
            EntityPlayerMP player = (EntityPlayerMP) PlayerMPUtil.castToPlayer(iCommandSender);
            if (player == null) {
                ChatHelper.sendMessage(iCommandSender, "Cannot make " + iCommandSender.getName() + " suffer the Wrath of Zeus!");
                return;
            }

            player.worldObj.addWeatherEffect(new EntityLightningBolt(player.worldObj, player.posX, player.posY, player.posZ, false));
            player.attackEntityFrom(getDamageSource(), Float.MAX_VALUE);
            ChatHelper.sendMessage(iCommandSender, ChatFormatting.GOLD + "You angered Zeus and hence suffered the Wrath of Zeus!");
            ChatHelper.sendAdminMessage(iCommandSender, "Made own self suffer the wrath of Zeus");
            return;

        }

        // Smite another player (or yourself)
        String subname = args[0];

        EntityPlayerMP player = PlayerMPUtil.getPlayer(subname);
        if (player == null) {
            ChatHelper.sendMessage(iCommandSender, ChatFormatting.RED + "Player not found");
            return;
        }

        player.worldObj.addWeatherEffect(new EntityLightningBolt(player.worldObj, player.posX, player.posY, player.posZ, false));
        player.attackEntityFrom(getDamageSource(), Float.MAX_VALUE);
        ChatHelper.sendMessage(iCommandSender, ChatFormatting.GOLD + "Made " + player.getName() + " suffer the Wrath of Zeus");
        ChatHelper.sendAdminMessage(iCommandSender, "Made " + player.getName() + " suffer the Wrath of Zeus");
        ChatHelper.sendMessage(player, ChatFormatting.GOLD + "You angered Zeus and hence suffered the Wrath of Zeus!");
    }

    public static DamageSource getDamageSource() {
        return new ZeusDamage("csczeuswrath").setDamageAllowedInCreativeMode().setDamageBypassesArmor();
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
