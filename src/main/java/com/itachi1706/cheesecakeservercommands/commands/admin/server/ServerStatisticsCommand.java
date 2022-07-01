package com.itachi1706.cheesecakeservercommands.commands.admin.server;

import com.itachi1706.cheesecakeservercommands.CheesecakeServerCommands;
import com.itachi1706.cheesecakeservercommands.commands.BaseCommand;
import com.itachi1706.cheesecakeservercommands.util.LogHelper;
import com.itachi1706.cheesecakeservercommands.util.ServerUtil;
import com.itachi1706.cheesecakeservercommands.util.TextUtil;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import javax.management.*;
import java.util.stream.IntStream;

public class ServerStatisticsCommand extends BaseCommand {
    public ServerStatisticsCommand(String name, int permissionLevel, boolean enabled) {
        super(name, permissionLevel, enabled);
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution() {
        return builder.executes(context -> getServerStats(context.getSource()));
    }
    
    private int getServerStats(CommandSourceStack sender) {
        // CPU
        int percentageCPU = -1;
        try {
            ObjectName name    = ObjectName.getInstance("java.lang:type=OperatingSystem");
            AttributeList list = CheesecakeServerCommands.getPlatformBean().getAttributes(name, new String[]{ "ProcessCpuLoad" });
            if (!list.isEmpty()) {
                Attribute att = (Attribute) list.get(0);
                Double value = (Double) att.getValue();
                // usually takes a couple of seconds before we get real values
                if (value != -1.0) percentageCPU = (int)(value * 1000) / 10;
            }
        } catch (InstanceNotFoundException | MalformedObjectNameException | ReflectionException e) {
            e.printStackTrace();
        }


        // RAM
        Runtime runtime = Runtime.getRuntime();

        long maxMemory = runtime.maxMemory();
        long allocatedMemory = runtime.totalMemory();
        int processors = runtime.availableProcessors();
        long usedMemory = getUsedMemory(runtime);
        double percentageMemoryUsed = (usedMemory/(double) maxMemory) * 100;
        double percentageMemoryAllocated = (allocatedMemory/(double) maxMemory) * 100;

        // Print out stats
        sendSuccessMessage(sender, ChatFormatting.GOLD + TextUtil.generateChatBreaks());
        sendSuccessMessage(sender, TextUtil.centerText("System Statistics"));
        sendSuccessMessage(sender, ChatFormatting.GOLD + TextUtil.generateChatBreaks());
        sendSuccessMessage(sender, String.format("CPU: %s (%d%%) [%s cores]", generatePercentageStep(percentageCPU), percentageCPU, ChatFormatting.LIGHT_PURPLE + "" + processors + ChatFormatting.RESET));
        sendSuccessMessage(sender, String.format("Memory: %s %s%s%s/%s%s%s (%.0f%%)", generatePercentageStep((int) percentageMemoryUsed), ChatFormatting.GREEN,
                getReadableMemorySizeString(usedMemory), ChatFormatting.RESET, ChatFormatting.RED, getReadableMemorySizeString(maxMemory), ChatFormatting.RESET, percentageMemoryUsed));
        sendSuccessMessage(sender, String.format("Allocated: %s (%.0f%%)", getReadableMemorySizeString(allocatedMemory), percentageMemoryAllocated));
        if (ServerUtil.getServerInstance().isDedicatedServer()) {
            // Get Players online
            int playersOnline = ServerUtil.getServerInstance().getPlayerCount();
            int maxPlayers = ServerUtil.getServerInstance().getMaxPlayers();
            sendSuccessMessage(sender, String.format("Players: %s %s%s%s/%s%s", generatePercentageStep((int) ((playersOnline/(double)maxPlayers) * 100)),
                    ChatFormatting.GREEN, playersOnline, ChatFormatting.RESET, ChatFormatting.RED, maxPlayers));
        }
        sendSuccessMessage(sender, ChatFormatting.GOLD + TextUtil.generateChatBreaks());
        sendSuccessMessage(sender, TextUtil.centerText("TPS Info"));
        sendSuccessMessage(sender, ChatFormatting.GOLD + TextUtil.generateChatBreaks());
        MinecraftServer server = ServerUtil.getServerInstance();
        double meanTickTime = mean(server.tickTimes) * 1.0E-6D;
        if (meanTickTime <= 0) {
            meanTickTime = 0.00000000001D;
        }
        double meanTPS = Math.min(1000.0/meanTickTime, 20);
        sendSuccessMessage(sender, String.format("Overall: %s%.1f%s (%.4fms)", ChatFormatting.LIGHT_PURPLE, meanTPS, ChatFormatting.RESET, meanTickTime));
        for (ServerLevel level : server.getAllLevels())
        {
            long[] tickTimes = server.getTickTime(level.dimension());
            if (tickTimes == null) {
                LogHelper.warn("Skipping level {} as there are no tick times", level.dimension().location().toString());
                continue;
            }
            double worldTickTime = mean(tickTimes) * 1.0E-6D;
            if (worldTickTime <= 0) {
                worldTickTime = 0.00000000001D;
            }
            double worldTPS = Math.min(1000.0/worldTickTime, 20);
            sendSuccessMessage(sender, String.format("%s: %s%.1f%s (%.4fms)", getDimensionPrefix(level), ChatFormatting.LIGHT_PURPLE, worldTPS, ChatFormatting.RESET, worldTickTime));
        }

        sendSuccessMessage(sender, ChatFormatting.GOLD + TextUtil.generateChatBreaks());
        return Command.SINGLE_SUCCESS;
    }

    public static long getUsedMemory(Runtime runtime) {
        return runtime.totalMemory() - runtime.freeMemory();
    }

    private static String getDimensionPrefix(Level level)
    {
        String name = level.dimension().location().toString();
        return String.format("Dimension %s%s%s", ChatFormatting.AQUA, name, ChatFormatting.RESET);
    }

    private static long mean(long[] values)
    {
        long sum = 0L;
        for (long v : values)
        {
            sum += v;
        }
        return sum / values.length;
    }

    private static String generatePercentageStep(int percentage) {
        int percentageStep = percentage/5; // Steps of 5

        StringBuilder sb = new StringBuilder();
        sb.append("[").append(ChatFormatting.AQUA);
        IntStream.range(0, percentageStep).forEach(s -> sb.append("="));
        IntStream.range(0, 20 - percentageStep).forEach(s -> sb.append(" "));
        sb.append(ChatFormatting.RESET).append("]");
        return sb.toString();
    }

    public static String getReadableMemorySizeString(long fileSizeInBytes) {
        int i = -1;
        double fileSizeBytes = fileSizeInBytes;
        String[] byteUnits = {"KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"};
        do {
            fileSizeBytes = fileSizeBytes / 1024.0;
            i++;
        } while (fileSizeBytes > 1024);

        return String.format("%.1f %s", Math.max(fileSizeBytes, 0.1), byteUnits[i]);
    }
}
