package com.itachi1706.cheesecakeservercommands.server.commands.admin.server;

import com.itachi1706.cheesecakeservercommands.CheesecakeServerCommands;
import com.itachi1706.cheesecakeservercommands.util.ChatHelper;
import com.itachi1706.cheesecakeservercommands.util.PlayerMPUtil;
import com.itachi1706.cheesecakeservercommands.util.ServerUtil;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.management.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Created by Kenneth on 3/6/2019.
 * for CheesecakeServerCommands in package com.itachi1706.cheesecakeservercommands.server.commands.admin.server
 */
@SuppressWarnings("unused")
public class ServerStatisticsCommand implements ICommand {

    private List<String> aliases;

    public ServerStatisticsCommand(){
        this.aliases = new ArrayList<>();
        this.aliases.add("serverstats");
    }

    @Override
    @Nonnull
    public String getName() {
        return "serverstats";
    }

    @Override
    @Nonnull
    public String getUsage(@Nonnull ICommandSender sender) {
        return "/serverstats";
    }

    @Override
    @Nonnull
    public List<String> getAliases() {
        return this.aliases;
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender iCommandSender, @Nonnull String[] args) throws CommandException {
        // CPU
        int percentageCPU = -1;
        try {
            ObjectName name    = ObjectName.getInstance("java.lang:type=OperatingSystem");
            AttributeList list = CheesecakeServerCommands.platformBean.getAttributes(name, new String[]{ "ProcessCpuLoad" });
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
        long usedMemory = allocatedMemory - freeMemory;
        int processors = runtime.availableProcessors();
        double percentageMemoryUsed = (usedMemory/(double) maxMemory) * 100;
        double percentageMemoryAllocated = (allocatedMemory/(double) maxMemory) * 100;

        // Print out stats
        ChatHelper.sendMessage(iCommandSender, TextFormatting.GOLD + ChatHelper.generateChatBreaks());
        ChatHelper.sendMessage(iCommandSender, ChatHelper.centerText("System Statistics"));
        ChatHelper.sendMessage(iCommandSender, TextFormatting.GOLD + ChatHelper.generateChatBreaks());
        ChatHelper.sendMessage(iCommandSender, String.format("CPU: %s (%d%%) [%s cores]", generatePercentageStep(percentageCPU), percentageCPU, TextFormatting.LIGHT_PURPLE + "" + processors + TextFormatting.RESET));
        ChatHelper.sendMessage(iCommandSender, String.format("Memory: %s " + TextFormatting.GREEN + "%s" + TextFormatting.RESET + "/" + TextFormatting.RED + "%s" + TextFormatting.RESET + " (%.0f%%)",
                generatePercentageStep((int) percentageMemoryUsed), getReadableMemorySizeString(usedMemory), getReadableMemorySizeString(maxMemory), percentageMemoryUsed));
        ChatHelper.sendMessage(iCommandSender, String.format("Allocated: %s (%.0f%%)", getReadableMemorySizeString(allocatedMemory), percentageMemoryAllocated));
        if (ServerUtil.getServerInstance().isDedicatedServer()) {
            // Get Players online
            int playersOnline = ServerUtil.getServerInstance().getCurrentPlayerCount();
            int maxPlayers = ServerUtil.getServerInstance().getMaxPlayers();
            ChatHelper.sendMessage(iCommandSender, String.format("Players: %s " + TextFormatting.GREEN + "%s" + TextFormatting.RESET + "/" + TextFormatting.RED + "%s",
                    generatePercentageStep((int) ((playersOnline/(double)maxPlayers) * 100)), playersOnline, maxPlayers));
        }
        ChatHelper.sendMessage(iCommandSender, TextFormatting.GOLD + ChatHelper.generateChatBreaks());
        ChatHelper.sendMessage(iCommandSender, ChatHelper.centerText("TPS Info"));
        ChatHelper.sendMessage(iCommandSender, TextFormatting.GOLD + ChatHelper.generateChatBreaks());
        double meanTickTime = mean(server.tickTimeArray) * 1.0E-6D;
        double meanTPS = Math.min(1000.0/meanTickTime, 20);
        ChatHelper.sendMessage(iCommandSender, String.format("%s: " + TextFormatting.LIGHT_PURPLE + "%.1f" + TextFormatting.RESET + " (%.4fms)", "Overall", meanTPS, meanTickTime));
        for (Integer dimId : DimensionManager.getIDs())
        {
            double worldTickTime = mean(server.worldTickTimes.get(dimId)) * 1.0E-6D;
            double worldTPS = Math.min(1000.0/worldTickTime, 20);
            ChatHelper.sendMessage(iCommandSender, String.format("%s: " + TextFormatting.LIGHT_PURPLE + "%.1f" + TextFormatting.RESET + " (%.4fms)", getDimensionPrefix(dimId), worldTPS, worldTickTime));
        }

        ChatHelper.sendMessage(iCommandSender, TextFormatting.GOLD + ChatHelper.generateChatBreaks());
    }

    private static String getDimensionPrefix(int dimId)
    {
        DimensionType providerType = DimensionManager.getProviderType(dimId);
        if (providerType == null) return String.format("Dim " + TextFormatting.AQUA + "%d" + TextFormatting.RESET, dimId);
        else return String.format("Dim " + TextFormatting.AQUA + "%d" + TextFormatting.RESET + " (%s)", dimId, providerType.getName());
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
        sb.append("[").append(TextFormatting.AQUA);
        IntStream.range(0, percentageStep).forEach((s) -> sb.append("="));
        IntStream.range(0, 20 - percentageStep).forEach((s) -> sb.append(" "));
        sb.append(TextFormatting.RESET).append("]");
        return sb.toString();
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
    
    private static String getReadableMemorySizeString(long fileSizeInBytes) {
        int i = -1;
        String[] byteUnits = {"KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"};
        do {
            fileSizeInBytes = fileSizeInBytes / 1024;
            i++;
        } while (fileSizeInBytes > 1024);

        //return Math.max(fileSizeInBytes, 0.1).toFixed(1) + byteUnits[i];
        return String.format("%.1f %s", Math.max(fileSizeInBytes, 0.1), byteUnits[i]);
    }
}
