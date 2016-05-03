package com.itachi1706.cheesecakeservercommands.server.commands.admin;

import com.itachi1706.cheesecakeservercommands.util.ChatHelper;
import com.itachi1706.cheesecakeservercommands.util.PlayerMPUtil;
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
public class WowCommand implements ICommand {

    private List<String> aliases;

    public WowCommand(){
        this.aliases = new ArrayList<String>();
        this.aliases.add("wow");
        this.aliases.add("doge");
    }

    @Override
    public String getCommandName() {
        return "wow";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_) {
        return "wow [player]";
    }

    @Override
    public List getCommandAliases() {
        return this.aliases;
    }

    @Override
    public void processCommand(ICommandSender iCommandSender, String[] astring) {
        if (astring.length == 0)
        {
            // Doge yourself
            ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.GREEN + "Such command, Very Nothing");
            ChatHelper.sendAdminMessage(iCommandSender, "Doged Own Self");
            sendToEveryone(iCommandSender, null);
            return;
        }

        String subname = astring[0];
        EntityPlayerMP player = PlayerMPUtil.getPlayer(subname);
        if (player == null) {
            ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.RED + "Player not found");
            return;
        }

        if (!PlayerMPUtil.isOperatorOrConsole(iCommandSender)) {
            ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.RED + "You do not have permission to do this to other players");
            ChatHelper.sendAdminMessage(iCommandSender, "Tried to Doge " + player.getCommandSenderName() + " while he is not an OP");
            return;
        }

        ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.GOLD + "Doged " + player.getCommandSenderName());
        ChatHelper.sendAdminMessage(iCommandSender, "Doged " + player.getCommandSenderName());
        ChatHelper.sendMessage(player, EnumChatFormatting.GREEN + "Such randomness, Very Nothing");
        sendToEveryone(iCommandSender, player);
    }

    public void sendToEveryone(ICommandSender starter, EntityPlayerMP recepient) {
        List<EntityPlayerMP> players = PlayerMPUtil.getOnlinePlayers();
        for (EntityPlayerMP player : players) {
            if (!(player.getCommandSenderName().equals(starter.getCommandSenderName()))) {
                if (recepient != null && !player.getCommandSenderName().equals(recepient.getCommandSenderName())) {
                    ChatHelper.sendMessage(player, EnumChatFormatting.GOLD + recepient.getCommandSenderName() + EnumChatFormatting.GRAY + " just got doged :D");
                } else if (recepient == null) {
                    ChatHelper.sendMessage(player, EnumChatFormatting.GOLD + starter.getCommandSenderName() + EnumChatFormatting.GRAY + " just got doged :D");
                }
            }
        }
    }

    @Override
    public List addTabCompletionOptions(ICommandSender iCommandSender, String[] typedValue) {
        if (typedValue.length == 1)
            return CommandBase.getListOfStringsMatchingLastWord(typedValue, MinecraftServer.getServer().getAllUsernames());
        return null;
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender iCommandSender) {
        // Just for this command all can use :D
        return true;
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
