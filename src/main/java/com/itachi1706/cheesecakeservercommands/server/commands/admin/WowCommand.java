package com.itachi1706.cheesecakeservercommands.server.commands.admin;

import com.itachi1706.cheesecakeservercommands.util.ChatHelper;
import com.itachi1706.cheesecakeservercommands.util.PlayerMPUtil;
import com.itachi1706.cheesecakeservercommands.util.ServerUtil;
import net.minecraft.util.text.TextFormatting;
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
public class WowCommand implements ICommand {

    private List<String> aliases;

    public WowCommand(){
        this.aliases = new ArrayList<String>();
        this.aliases.add("wow");
        this.aliases.add("doge");
    }

    @Override
    public String getName() {
        return "wow";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "wow [player]";
    }

    @Override
    public List getAliases() {
        return this.aliases;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender iCommandSender, String[] args) throws CommandException {
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

    public void sendToEveryone(ICommandSender starter, EntityPlayerMP recepient) {
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
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1)
            return CommandBase.getListOfStringsMatchingLastWord(args, ServerUtil.getServerInstance().getOnlinePlayerNames());
        return null;
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender iCommandSender) {
        // Just for this command all can use :D
        return true;
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
