package com.itachi1706.cheesecakeservercommands.server.commands.admin;

import com.itachi1706.cheesecakeservercommands.util.ChatHelper;
import com.itachi1706.cheesecakeservercommands.util.PlayerMPUtil;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
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
    public void execute(MinecraftServer server, ICommandSender iCommandSender, String[] args) throws CommandException {

        if(args.length == 0)
        {
            ChatHelper.sendMessage(iCommandSender, ChatFormatting.RED + "Usage: /kick <player> [reason]");
            return;
        }

        String subname = args[0];
        EntityPlayerMP player = PlayerMPUtil.getPlayer(subname);
        if (player == null) {
            ChatHelper.sendMessage(iCommandSender, ChatFormatting.RED + "Player not found");
            return;
        }

        String reason = "Kicked by an operator.";
        if (args.length > 1) {
            reason = "";
            for (int i = 1; i < args.length - 1; i++) {
                reason += args[i] + " ";
            }
            reason += args[args.length - 1];
        }

        reason = ChatHelper.formatString(reason);

        player.connection.kickPlayerFromServer(reason);
        ChatHelper.sendMessage(iCommandSender, "Kicked " + player.getName() + " with reason " + reason);
        ChatHelper.sendAdminMessage(iCommandSender, "Kicked " + player.getName() + " with reason " + reason);
    }

    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender iCommandSender, String[] typedValue, @Nullable BlockPos pos) {
        if (typedValue.length == 1)
            return CommandBase.getListOfStringsMatchingLastWord(typedValue, PlayerMPUtil.getServerInstance().getAllUsernames());
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
