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
public class WowCommand implements ICommand {

    private List<String> aliases;

    public WowCommand(){
        this.aliases = new ArrayList<>();
        this.aliases.add("wow");
        this.aliases.add("doge");
    }

    @Override
    @Nonnull
    public String getName() {
        return "wow";
    }

    @Override
    @Nonnull
    public String getUsage(@Nonnull ICommandSender sender) {
        return "/wow [player]";
    }

    @Override
    @Nonnull
    public List<String> getAliases() {
        return this.aliases;
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender iCommandSender, @Nonnull String[] args) {
        if (args.length == 0)
        {
            // Doge yourself
            ChatHelper.sendMessage(iCommandSender, TextFormatting.GREEN + "Such command, Very Nothing");
            ChatHelper.sendAdminMessage(iCommandSender, "Doged Own Self");
            sendToEveryone(iCommandSender, null);
            return;
        }

        String subname = args[0];
        EntityPlayerMP player = PlayerMPUtil.getPlayer(subname);
        if (player == null) {
            ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "Player not found");
            return;
        }

        if (!PlayerMPUtil.isOperatorOrConsole(iCommandSender)) {
            ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "You do not have permission to do this to other players");
            ChatHelper.sendAdminMessage(iCommandSender, "Tried to Doge " + player.getName() + " while he is not an OP");
            return;
        }

        ChatHelper.sendMessage(iCommandSender, TextFormatting.GOLD + "Doged " + player.getName());
        ChatHelper.sendAdminMessage(iCommandSender, "Doged " + player.getName());
        ChatHelper.sendMessage(player, TextFormatting.GREEN + "Such randomness, Very Nothing");
        sendToEveryone(iCommandSender, player);
    }

    private void sendToEveryone(ICommandSender starter, EntityPlayerMP recepient) {
        List<EntityPlayerMP> players = PlayerMPUtil.getOnlinePlayers();
        for (EntityPlayerMP player : players) {
            if (!(player.getName().equals(starter.getName()))) {
                if (recepient != null && !player.getName().equals(recepient.getName())) {
                    ChatHelper.sendMessage(player, TextFormatting.GOLD + recepient.getName() + TextFormatting.GRAY + " just got doged :D");
                } else if (recepient == null) {
                    ChatHelper.sendMessage(player, TextFormatting.GOLD + starter.getName() + TextFormatting.GRAY + " just got doged :D");
                }
            }
        }
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
        // Just for this command all can use :D
        return true;
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
