package com.itachi1706.cheesecakeservercommands.server.commands.admin.item;

import com.itachi1706.cheesecakeservercommands.util.ChatHelper;
import com.itachi1706.cheesecakeservercommands.util.PlayerMPUtil;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
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
public class MoreItemsCommand implements ICommand {

    private List<String> aliases;

    public MoreItemsCommand(){
        this.aliases = new ArrayList<String>();
        this.aliases.add("more");
    }

    @Override
    public String getCommandName() {
        return "more";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_) {
        return "more";
    }

    @Override
    public List getCommandAliases() {
        return this.aliases;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender iCommandSender, String[] args) throws CommandException {
        if (!PlayerMPUtil.isPlayer(iCommandSender)) {
            ChatHelper.sendMessage(iCommandSender, "Cannot duplicate max stack items for CONSOLE");
            return;
        }

        EntityPlayerMP player = (EntityPlayerMP) PlayerMPUtil.castToPlayer(iCommandSender);
        if (player == null) {
            ChatHelper.sendMessage(iCommandSender, "Cannot duplicate max stack items for " + iCommandSender.getName());
            return;
        }

        ItemStack stack = player.getHeldItemMainhand();
        if (stack == null) {
            ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "No items selected");
            return;
        }

        int stackSize = stack.getMaxStackSize() - stack.stackSize;

        if (stackSize == 0) {
            ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "You already have the max stack size of the item");
            ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "Use /duplicate to create a new stack");
            return;
        }

        ItemStack newItem = stack.copy();
        newItem.stackSize = stackSize;

        PlayerMPUtil.giveNormal(player, newItem);

        String message = TextFormatting.GOLD + "Gave you more of " + stack.getDisplayName();
        String adminmessage = "Given more of " + stack.getDisplayName() + " to self";
        ChatHelper.sendMessage(iCommandSender, message);
        ChatHelper.sendAdminMessage(iCommandSender, adminmessage);
    }

    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender iCommandSender, String[] typedValue, @Nullable BlockPos pos) {
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
