package com.itachi1706.cheesecakeservercommands.server.commands.admin.world;

import com.itachi1706.cheesecakeservercommands.util.ChatHelper;
import com.itachi1706.cheesecakeservercommands.util.PlayerMPUtil;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.BiomeGenBase;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

// TODO: Add to Main Command

/**
 * Created by Kenneth on 9/11/2015.
 * for CheesecakeServerCommands in package com.itachi1706.cheesecakeservercommands.server.commands
 */
@SuppressWarnings("unused")
public class BiomeInfoCommand implements ICommand {

    private List<String> aliases;

    public BiomeInfoCommand(){
        this.aliases = new ArrayList<String>();
        this.aliases.add("biomeinfo");
    }

    @Override
    public String getCommandName() {
        return "biomeinfo";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_) {
        return "biomeinfo [list]";
    }

    @Override
    public List getCommandAliases() {
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
            BiomeGenBase biome = player.worldObj.getBiomeGenForCoords(x, z);
            ChatHelper.sendMessage(iCommandSender, ChatFormatting.GOLD + "Current Biome: " + ChatFormatting.AQUA + biome.biomeName);
            return;
        }

        if (args[0].equalsIgnoreCase("list")) {
            ChatHelper.sendMessage(iCommandSender, ChatFormatting.GOLD + "Registered Biome: ");
            boolean skip = false;
            for (int i = 0; i < BiomeGenBase.getBiomeGenArray().length; i++)
            {
                BiomeGenBase biome = BiomeGenBase.getBiomeGenArray()[i];
                if (biome == null)
                {
                    skip = true;
                    continue;
                }
                if (skip)
                {
                    skip = false;
                    ChatHelper.sendMessage(iCommandSender, ChatFormatting.GOLD + "----");
                }
                ChatHelper.sendMessage(iCommandSender, ChatFormatting.GOLD + "#" + i + ": " + ChatFormatting.AQUA + biome.biomeName);
            }
        } else {
            ChatHelper.sendMessage(iCommandSender, ChatFormatting.RED + "Invalid Command. Usage: /biomeinfo [list]");
        }
    }

    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender iCommandSender, String[] typedValue, @Nullable BlockPos pos) {
        if (typedValue.length == 1) {
            return CommandBase.getListOfStringsMatchingLastWord(typedValue, "list");
        }
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
