package com.itachi1706.cheesecakeservercommands.server.commands.admin;

import com.itachi1706.cheesecakeservercommands.util.ChatHelper;
import com.itachi1706.cheesecakeservercommands.util.PlayerMPUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kenneth on 9/11/2015.
 * for CheesecakeServerCommands in package com.itachi1706.cheesecakeservercommands.server.commands
 */
@SuppressWarnings("unused")
public class KillCommand implements ICommand {

    private List<String> aliases;

    public KillCommand(){
        this.aliases = new ArrayList<String>();
        this.aliases.add("kill");
    }

    @Override
    public String getCommandName() {
        return "kill";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_) {
        return "kill [player]";
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
                ChatHelper.sendMessage(iCommandSender, "Cannot kill CONSOLE");
                return;
            } else {
                EntityPlayerMP player = (EntityPlayerMP) PlayerMPUtil.castToPlayer(iCommandSender);
                if (player == null) {
                    ChatHelper.sendMessage(iCommandSender, "Cannot kill" + iCommandSender.getCommandSenderName());
                    return;
                }

                player.attackEntityFrom(DamageSource.outOfWorld, Float.MAX_VALUE);
                ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.GOLD + "You were slain");
                ChatHelper.sendAdminMessage(iCommandSender, "Took their own life");
                return;
            }
        }

        String subname = astring[0];
        EntityPlayerMP player = PlayerMPUtil.getPlayer(subname);
        if (player == null) {
            ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.RED + "Player not found");
            return;
        }

        player.attackEntityFrom(DamageSource.outOfWorld, Float.MAX_VALUE);
        ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.GOLD + "Killed " + player.getCommandSenderName());
        ChatHelper.sendAdminMessage(iCommandSender, "Killed " + player.getCommandSenderName());
        ChatHelper.sendMessage(player, EnumChatFormatting.GOLD + "You were slain");
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
