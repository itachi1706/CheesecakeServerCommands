package com.itachi1706.cheesecakeservercommands.server.commands.admin;

import com.itachi1706.cheesecakeservercommands.util.ChatHelper;
import com.itachi1706.cheesecakeservercommands.util.PlayerMPUtil;
import com.itachi1706.cheesecakeservercommands.util.ServerUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Kenneth on 9/11/2015.
 * for CheesecakeServerCommands in package com.itachi1706.cheesecakeservercommands.server.commands
 */
@SuppressWarnings("unused")
public class KickCommand implements ICommand {

    private List<String> aliases;

    public KickCommand(){
        this.aliases = new ArrayList<>();
        this.aliases.add("kick");
    }

    @Override
    @Nonnull
    public String getName() {
        return "kick";
    }

    @Override
    @Nonnull
    public String getUsage(@Nonnull ICommandSender sender) {
        return "/kick <player> [reason]";
    }

    @Override
    @Nonnull
    public List<String> getAliases() {
        return this.aliases;
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender iCommandSender, @Nonnull String[] args) {

        if(args.length == 0)
        {
            ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "Usage: /kick <player> [reason]");
            return;
        }

        String subname = args[0];
        EntityPlayerMP player = PlayerMPUtil.getPlayer(subname);
        if (player == null) {
            ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "Player not found");
            return;
        }

        StringBuilder reason = new StringBuilder("Kicked by an operator.");
        if (args.length > 1) {
            reason = new StringBuilder();
            for (int i = 1; i < args.length - 1; i++) {
                reason.append(args[i]).append(" ");
            }
            reason.append(args[args.length - 1]);
        }

        reason = new StringBuilder(ChatHelper.formatString(reason.toString()));

        player.connection.disconnect(new TextComponentString(reason.toString()));
        ChatHelper.sendMessage(iCommandSender, "Kicked " + player.getName() + " with reason " + reason);
        ChatHelper.sendAdminMessage(iCommandSender, "Kicked " + player.getName() + " with reason " + reason);
    }

    @Override
    @Nonnull
    public List<String> getTabCompletions(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1)
            return CommandBase.getListOfStringsMatchingLastWord(args, ServerUtil.getServerInstance().getOnlinePlayerNames());
        return Collections.emptyList();
    }

    @Override
    public boolean checkPermission(@Nonnull MinecraftServer server, @Nonnull ICommandSender iCommandSender) {
        return PlayerMPUtil.isOperatorOrConsole(iCommandSender);
    }

    @Override
    public boolean isUsernameIndex(@Nonnull String[] args, int p_82358_2_) {
        return false;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public int compareTo(ICommand o) {
        return 0;
    }
}
