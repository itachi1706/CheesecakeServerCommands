package com.itachi1706.cheesecakeservercommands.server.commands.admin.world;

import com.itachi1706.cheesecakeservercommands.util.ChatHelper;
import com.itachi1706.cheesecakeservercommands.util.PlayerMPUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.biome.Biome;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Kenneth on 9/11/2015.
 * for CheesecakeServerCommands in package com.itachi1706.cheesecakeservercommands.server.commands
 */
@SuppressWarnings("unused")
public class BiomeInfoCommand implements ICommand {

    private List<String> aliases;

    public BiomeInfoCommand(){
        this.aliases = new ArrayList<>();
        this.aliases.add("biomeinfo");
    }

    @Override
    public String getName() {
        return "biomeinfo";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/biomeinfo [list]";
    }

    @Override
    public List getAliases() {
        return this.aliases;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender iCommandSender, String[] args) throws CommandException {
        if (!PlayerMPUtil.isPlayer(iCommandSender) && args.length == 0) {
            ChatHelper.sendMessage(iCommandSender, "Cannot view biome for CONSOLE");
            return;
        }

        if (args.length == 0) {
            EntityPlayerMP player = (EntityPlayerMP) PlayerMPUtil.castToPlayer(iCommandSender);
            if (player == null) {
                ChatHelper.sendMessage(iCommandSender, "Cannot view biome for " + iCommandSender.getName());
                return;
            }

            int x = (int) Math.floor(player.posX);
            int z = (int) Math.floor(player.posZ);
            Biome biome = player.world.getBiomeForCoordsBody(new BlockPos(x, 0, z));
            ChatHelper.sendMessage(iCommandSender, TextFormatting.GOLD + "Current Biome: " + TextFormatting.AQUA + getBiomeName(biome));
            return;
        }

        if (args[0].equalsIgnoreCase("list")) {
            ChatHelper.sendMessage(iCommandSender, TextFormatting.GOLD + "Registered Biome: ");
            boolean skip = false;
            int i = 0;
            for (Biome biome : Biome.REGISTRY) {
                if (biome == null) {
                    skip = true;
                    continue;
                }
                if (skip) {
                    skip = false;
                    ChatHelper.sendMessage(iCommandSender, TextFormatting.GOLD + "----");
                }
                ChatHelper.sendMessage(iCommandSender, TextFormatting.GOLD + "#" + i + ": " + TextFormatting.AQUA + getBiomeName(biome));
                i++;
            }
        } else {
            ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "Invalid Command. Usage: /biomeinfo [list]");
        }
    }

    // MC 1.12 made getBiomeName a client only thing, use reflection to get Biome Name
    private String getBiomeName(Biome biome) {
        try {
            Field field = Biome.class.getDeclaredField("biomeName");
            field.setAccessible(true);
            Object value = field.get(biome);
            return value.toString();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return "Unknown";
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1) {
            return CommandBase.getListOfStringsMatchingLastWord(args, "list");
        }
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
