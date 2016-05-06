package com.itachi1706.cheesecakeservercommands.server.commands.admin.world;

import com.itachi1706.cheesecakeservercommands.util.ChatHelper;
import com.itachi1706.cheesecakeservercommands.util.PlayerMPUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.biome.BiomeGenBase;

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
    public void processCommand(ICommandSender iCommandSender, String[] astring) {
        if (!PlayerMPUtil.isPlayer(iCommandSender) && astring.length == 0) {
            ChatHelper.sendMessage(iCommandSender, "Cannot view biome for CONSOLE");
            return;
        }

        if (astring.length == 0) {
            EntityPlayerMP player = (EntityPlayerMP) PlayerMPUtil.castToPlayer(iCommandSender);
            if (player == null) {
                ChatHelper.sendMessage(iCommandSender, "Cannot view biome for " + iCommandSender.getCommandSenderName());
                return;
            }

            int x = (int) Math.floor(player.posX);
            int z = (int) Math.floor(player.posZ);
            BiomeGenBase biome = player.worldObj.getBiomeGenForCoords(x, z);
            ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.GOLD + "Current Biome: " + EnumChatFormatting.AQUA + biome.biomeName);
            //ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.GOLD + " " + EnumChatFormatting.AQUA + biome.getClass().getName());
            return;
        }

        if (astring[0].equalsIgnoreCase("list")) {
            ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.GOLD + "Registered Biome: ");
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
                    ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.GOLD + "----");
                }
                ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.GOLD + "#" + i + ": " + EnumChatFormatting.AQUA + biome.biomeName);
            }
        } else {
            ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.RED + "Invalid Command. Usage: /biomeinfo [list]");
        }
    }

    @Override
    public List addTabCompletionOptions(ICommandSender iCommandSender, String[] typedValue) {
        return CommandBase.getListOfStringsMatchingLastWord(typedValue, "list");
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender iCommandSender) {
        return PlayerMPUtil.isOperatorOrConsole(iCommandSender);
    }

    @Override
    public boolean isUsernameIndex(String[] p_82358_1_, int p_82358_2_) {
        return false;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
