package com.itachi1706.cheesecakeservercommands.commands.admin.server;

import com.itachi1706.cheesecakeservercommands.commands.BaseCommand;
import com.itachi1706.cheesecakeservercommands.util.LogHelper;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;

public class GarbageCollectorCommand extends BaseCommand {

    public GarbageCollectorCommand(String name, int permissionLevel, boolean enabled) {
        super(name, permissionLevel, enabled);
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution() {
        return builder.executes(context -> gc(context.getSource()));
    }

    @SuppressWarnings("java:S1215")
    private int gc(CommandSourceStack sender) {
        long oldUsedMemory = ServerStatisticsCommand.getUsedMemory(Runtime.getRuntime());
        LogHelper.info("Old Memory Usage: " + ServerStatisticsCommand.getReadableMemorySizeString(oldUsedMemory) + ". Performing GC...");
        System.gc(); // Supress S1215 warning as this is very much intentional
        long newUsedMemory = ServerStatisticsCommand.getUsedMemory(Runtime.getRuntime());
        LogHelper.info("GC Completed. New Memory Usage: " + ServerStatisticsCommand.getReadableMemorySizeString(newUsedMemory));
        sendSuccessMessage(sender, ChatFormatting.GREEN + "Garbage Collector has been successfully called! Freed " +
                ServerStatisticsCommand.getReadableMemorySizeString(oldUsedMemory - newUsedMemory) + " from RAM");
        sendSuccessMessage(sender, ChatFormatting.RED + "Note: Do not call this command often as it may be detrimental to server performance!");

        return Command.SINGLE_SUCCESS;
    }
}
