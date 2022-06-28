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
public class MoreItemsCommand implements ICommand {

    private List<String> aliases;

    public MoreItemsCommand(){
        this.aliases = new ArrayList<>();
        this.aliases.add("more");
    }

    @Override
    @Nonnull
    public String getName() {
        return "more";
    }

    @Override
    @Nonnull
    public String getUsage(@Nonnull ICommandSender sender) {
        return "/more";
    }

    @Override
    @Nonnull
    public List<String> getAliases() {
        return this.aliases;
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender iCommandSender, @Nonnull String[] args) {
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

        int stackSize = stack.getMaxStackSize() - stack.getCount();

        if (stackSize == 0) {
            ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "You already have the max stack size of the item");
            ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "Use /duplicate to create a new stack");
            return;
        }

        ItemStack newItem = stack.copy();
        newItem.setCount(stackSize);

        PlayerMPUtil.giveNormal(player, newItem);

        String message = TextFormatting.GOLD + "Gave you more of " + stack.getDisplayName();
        String adminmessage = "Given more of " + stack.getDisplayName() + " to self";
        ChatHelper.sendMessage(iCommandSender, message);
        ChatHelper.sendAdminMessage(iCommandSender, adminmessage);
    }

    @Override
    @Nonnull
    public List<String> getTabCompletions(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args, @Nullable BlockPos targetPos) {
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
