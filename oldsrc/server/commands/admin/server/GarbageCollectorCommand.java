package com.itachi1706.cheesecakeservercommands.server.commands.admin.server;

import com.itachi1706.cheesecakeservercommands.util.ChatHelper;
import com.itachi1706.cheesecakeservercommands.util.LogHelper;
import com.itachi1706.cheesecakeservercommands.util.PlayerMPUtil;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Kenneth on 3/6/2019.
 * for CheesecakeServerCommands in package com.itachi1706.cheesecakeservercommands.server.commands.admin.server
 */
@SuppressWarnings("unused")
public class GarbageCollectorCommand implements ICommand {

    private List<String> aliases;

    public GarbageCollectorCommand(){
        this.aliases = new ArrayList<>();
        this.aliases.add("gc");
    }

    @Override
    @Nonnull
    public String getName() {
        return "gc";
    }

    @Override
    @Nonnull
    public String getUsage(@Nonnull ICommandSender sender) {
        return "/gc";
    }

    @Override
    @Nonnull
    public List<String> getAliases() {
        return this.aliases;
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender iCommandSender, @Nonnull String[] args) throws CommandException {
        long oldUsedMemory = ServerStatisticsCommand.getUsedMemory(Runtime.getRuntime());
        LogHelper.info("Old Memory Usage: " + ServerStatisticsCommand.getReadableMemorySizeString(oldUsedMemory) + ". Performing GC...");
        System.gc();
        long newUsedMemory = ServerStatisticsCommand.getUsedMemory(Runtime.getRuntime());
        LogHelper.info("GC Completed. New Memory Usage: " + ServerStatisticsCommand.getReadableMemorySizeString(newUsedMemory));
        ChatHelper.sendMessage(iCommandSender, TextFormatting.GREEN + "Garbage Collector has been successfully called! Freed " + ServerStatisticsCommand.
                getReadableMemorySizeString(oldUsedMemory - newUsedMemory) + " from RAM");
        ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "Note: Do not call this command often as it may be detrimental to server performance!");
    }

    @Override
    @Nonnull
    public List<String> getTabCompletions(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args, @Nullable BlockPos targetPos) {
        return Collections.emptyList();
    }

    @Override
    public boolean checkPermission(@Nonnull MinecraftServer server, @Nonnull ICommandSender iCommandSender) {
        return PlayerMPUtil.isOperatorOrConsole(iCommandSender);
        // Its now OPs, but we should put it console level for this
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
