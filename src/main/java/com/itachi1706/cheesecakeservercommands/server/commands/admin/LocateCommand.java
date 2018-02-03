package com.itachi1706.cheesecakeservercommands.server.commands.admin;

import com.itachi1706.cheesecakeservercommands.commons.selections.WorldPoint;
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
public class LocateCommand implements ICommand {

    private List<String> aliases;

    public LocateCommand(){
        this.aliases = new ArrayList<>();
        this.aliases.add("locate");
        this.aliases.add("loc");
        this.aliases.add("gps");
    }

    @Override
    public String getName() {
        return "locate";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/locate [player]";
    }

    @Override
    public List getAliases() {
        return this.aliases;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender iCommandSender, String[] args) throws CommandException {
        if (args.length == 0)
        {
            if (!PlayerMPUtil.isPlayer(iCommandSender)) {
                ChatHelper.sendMessage(iCommandSender, "Cannot locate CONSOLE");
                return;
            } else {
                EntityPlayerMP player = (EntityPlayerMP) PlayerMPUtil.castToPlayer(iCommandSender);
                if (player == null) {
                    ChatHelper.sendMessage(iCommandSender, "Cannot locate" + iCommandSender.getName());
                    return;
                }

                float toHeal = player.getMaxHealth() - player.getHealth();
                WorldPoint point = new WorldPoint(player);
                ChatHelper.sendMessage(iCommandSender, String.format(TextFormatting.GOLD + "You are at %d, %d, %d in dimension %d with a gamemode of %s",
                        point.getX(), point.getY(), point.getZ(), point.getDimension(), player.interactionManager.getGameType().getName()));
                ChatHelper.sendAdminMessage(iCommandSender, "Located Own location");
                return;
            }
        }

        String subname = args[0];
        EntityPlayerMP player = PlayerMPUtil.getPlayer(subname);
        if (player == null) {
            ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "Player not found");
            return;
        }

        WorldPoint point = new WorldPoint(player);
        ChatHelper.sendMessage(iCommandSender, String.format(TextFormatting.GOLD + "%s is at %d, %d, %d in dimension %d with a gamemode of %s",
                player.getName(), point.getX(), point.getY(), point.getZ(), point.getDimension(), player.interactionManager.getGameType().getName()));
        ChatHelper.sendAdminMessage(iCommandSender, "Located " + player.getName() + "'s location");
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1)
            return CommandBase.getListOfStringsMatchingLastWord(args, ServerUtil.getServerInstance().getOnlinePlayerNames());
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
