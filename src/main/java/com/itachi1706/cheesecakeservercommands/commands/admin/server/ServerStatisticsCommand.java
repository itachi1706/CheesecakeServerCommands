package com.itachi1706.cheesecakeservercommands.commands.admin.server;

import com.itachi1706.cheesecakeservercommands.CheesecakeServerCommands;
import com.itachi1706.cheesecakeservercommands.commands.BaseCommand;
import com.itachi1706.cheesecakeservercommands.util.ServerUtil;
import com.itachi1706.cheesecakeservercommands.util.TextUtil;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;

import javax.management.*;
import java.text.NumberFormat;
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
        NumberFormat format = NumberFormat.getInstance();

        long maxMemory = runtime.maxMemory();
        long allocatedMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        int processors = runtime.availableProcessors();
        long usedMemory = getUsedMemory(runtime);
        double percentageMemoryUsed = (usedMemory/(double) maxMemory) * 100;
        double percentageMemoryAllocated = (allocatedMemory/(double) maxMemory) * 100;

        // Print out stats
        sendSuccessMessage(sender, ChatFormatting.GOLD + TextUtil.generateChatBreaks());
        sendSuccessMessage(sender, TextUtil.centerText("System Statistics"));
        sendSuccessMessage(sender, ChatFormatting.GOLD + TextUtil.generateChatBreaks());
        sendSuccessMessage(sender, String.format("CPU: %s (%d%%) [%s cores]", generatePercentageStep(percentageCPU), percentageCPU, ChatFormatting.LIGHT_PURPLE + "" + processors + ChatFormatting.RESET));
        sendSuccessMessage(sender, String.format("Memory: %s " + ChatFormatting.GREEN + "%s" + ChatFormatting.RESET + "/" + ChatFormatting.RED + "%s" + ChatFormatting.RESET + " (%.0f%%)",
                generatePercentageStep((int) percentageMemoryUsed), getReadableMemorySizeString(usedMemory), getReadableMemorySizeString(maxMemory), percentageMemoryUsed));
        sendSuccessMessage(sender, String.format("Allocated: %s (%.0f%%)", getReadableMemorySizeString(allocatedMemory), percentageMemoryAllocated));
        if (ServerUtil.getServerInstance().isDedicatedServer()) {
            // Get Players online
            int playersOnline = ServerUtil.getServerInstance().getPlayerCount();
            int maxPlayers = ServerUtil.getServerInstance().getMaxPlayers();
            sendSuccessMessage(sender, String.format("Players: %s " + ChatFormatting.GREEN + "%s" + ChatFormatting.RESET + "/" + ChatFormatting.RED + "%s",
                    generatePercentageStep((int) ((playersOnline/(double)maxPlayers) * 100)), playersOnline, maxPlayers));
        }
        sendSuccessMessage(sender, ChatFormatting.GOLD + TextUtil.generateChatBreaks());
        sendSuccessMessage(sender, TextUtil.centerText("TPS Info"));
        sendSuccessMessage(sender, ChatFormatting.GOLD + TextUtil.generateChatBreaks());
        MinecraftServer server = ServerUtil.getServerInstance();
        double meanTickTime = mean(server.tickTimes) * 1.0E-6D;
        double meanTPS = Math.min(1000.0/meanTickTime, 20);
        sendSuccessMessage(sender, String.format("%s: " + ChatFormatting.LIGHT_PURPLE + "%.1f" + ChatFormatting.RESET + " (%.4fms)", "Overall", meanTPS, meanTickTime));
        for (Level level : server.getAllLevels())
        {
            double worldTickTime = mean(server.getTickTime(level.dimension())) * 1.0E-6D;
            double worldTPS = Math.min(1000.0/worldTickTime, 20);
            sendSuccessMessage(sender, String.format("%s: " + ChatFormatting.LIGHT_PURPLE + "%.1f" + ChatFormatting.RESET + " (%.4fms)", getDimensionPrefix(level), worldTPS, worldTickTime));
        }

        sendSuccessMessage(sender, ChatFormatting.GOLD + TextUtil.generateChatBreaks());
        return Command.SINGLE_SUCCESS;
    }

    public static long getUsedMemory(Runtime runtime) {
        return runtime.totalMemory() - runtime.freeMemory();
    }

    private static String getDimensionPrefix(Level level)
    {
        // TODO: Test if this works as intended. If it does remove the old code
        //DimensionType type = level.dimensionType();
        String name = level.dimension().getRegistryName().toString();
//        if (type == null)
        return String.format("Dim " + ChatFormatting.AQUA + "%d" + ChatFormatting.RESET, name);
        //else return String.format("Dim " + ChatFormatting.AQUA + "%d" + ChatFormatting.RESET + " (%s)", name, type.);
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
        IntStream.range(0, percentageStep).forEach((s) -> sb.append("="));
        IntStream.range(0, 20 - percentageStep).forEach((s) -> sb.append(" "));
        sb.append(ChatFormatting.RESET).append("]");
        return sb.toString();
    }

    public static String getReadableMemorySizeString(double fileSizeInBytes) {
        int i = -1;
        String[] byteUnits = {"KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"};
        do {
            fileSizeInBytes = fileSizeInBytes / 1024.0;
            i++;
        } while (fileSizeInBytes > 1024);

        //return Math.max(fileSizeInBytes, 0.1).toFixed(1) + byteUnits[i];
        return String.format("%.1f %s", Math.max(fileSizeInBytes, 0.1), byteUnits[i]);
    }
}
