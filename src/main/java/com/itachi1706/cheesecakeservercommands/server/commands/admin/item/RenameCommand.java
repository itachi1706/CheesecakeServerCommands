package com.itachi1706.cheesecakeservercommands.server.commands.admin.item;

import com.itachi1706.cheesecakeservercommands.util.ChatHelper;
import com.itachi1706.cheesecakeservercommands.util.PlayerMPUtil;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Kenneth on 9/11/2015.
 * for CheesecakeServerCommands in package com.itachi1706.cheesecakeservercommands.server.commands
 */
@SuppressWarnings("unused")
public class RenameCommand implements ICommand {

    private List<String> aliases;

    public RenameCommand(){
        this.aliases = new ArrayList<>();
        this.aliases.add("renameitem");
    }

    @Override
    public String getName() {
        return "renameitem";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/renameitem <newname>";
    }

    @Override
    public List getAliases() {
        return this.aliases;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender iCommandSender, String[] args) throws CommandException {
        if (args.length == 0) {
            ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "Usage: /renameitem <newname>");
            return;
        }

        String newname = args[0];

        if (!PlayerMPUtil.isPlayer(iCommandSender)) {
            ChatHelper.sendMessage(iCommandSender, "Cannot rename item for CONSOLE");
            return;
        }

        EntityPlayerMP player = (EntityPlayerMP) PlayerMPUtil.castToPlayer(iCommandSender);
        if (player == null) {
            ChatHelper.sendMessage(iCommandSender, "Cannot rename item " + iCommandSender.getName());
            return;
        }

        ItemStack item = player.getHeldItemMainhand();
        if (item == null) {
            ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "You are not holding an item");
            return;
        }

        StringBuilder builder = new StringBuilder();
        for (String s : args) {
            builder.append(s + " ");
        }

        String oldname = item.getDisplayName();
        String finalnewname = builder.toString().trim();
        item.setStackDisplayName(finalnewname);

        ChatHelper.sendMessage(iCommandSender, TextFormatting.GOLD + "Renamed " + oldname + " to " + finalnewname);
        ChatHelper.sendAdminMessage(iCommandSender, "Renamed " + oldname + " to " + finalnewname);
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        return Collections.emptyList();
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
