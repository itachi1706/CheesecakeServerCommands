package com.itachi1706.cheesecakeservercommands.server.commands.admin;

import com.itachi1706.cheesecakeservercommands.util.ChatHelper;
import com.itachi1706.cheesecakeservercommands.util.PlayerMPUtil;
import com.itachi1706.cheesecakeservercommands.util.WorldUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kenneth on 9/11/2015.
 * for CheesecakeServerCommands in package com.itachi1706.cheesecakeservercommands.server.commands
 */
@SuppressWarnings("unused")
public class KickCommand implements ICommand {

    private List<String> aliases;

    public KickCommand(){
        this.aliases = new ArrayList<String>();
        this.aliases.add("kick");
    }

    @Override
    public String getCommandName() {
        return "kick";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_) {
        return "kick <player> [reason]";
    }

    @Override
    public List getCommandAliases() {
        return this.aliases;
    }

    @Override
    public void processCommand(ICommandSender iCommandSender, String[] astring) {

        if(astring.length == 0)
        {
            ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.RED + "Usage: /kick <player> [reason]");
            return;
        }

        String subname = astring[0];
        EntityPlayerMP player = PlayerMPUtil.getPlayer(subname);
        if (player == null) {
            ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.RED + "Player not found");
            return;
        }

        String reason = "Kicked by an operator.";
        if (astring.length > 1) {
            reason = "";
            for (int i = 1; i < astring.length - 1; i++) {
                reason += astring[i] + " ";
            }
            reason += astring[astring.length - 1];
        }

        reason = ChatHelper.formatString(reason);

        player.playerNetServerHandler.kickPlayerFromServer(reason);
        ChatHelper.sendMessage(iCommandSender, "Kicked " + player.getCommandSenderName() + " with reason " + reason);
        ChatHelper.sendAdminMessage(iCommandSender, "Kicked " + player.getCommandSenderName() + " with reason " + reason);
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
