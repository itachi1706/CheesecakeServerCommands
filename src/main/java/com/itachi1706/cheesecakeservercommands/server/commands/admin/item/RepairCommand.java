package com.itachi1706.cheesecakeservercommands.server.commands.admin.item;

import com.itachi1706.cheesecakeservercommands.util.ChatHelper;
import com.itachi1706.cheesecakeservercommands.util.PlayerMPUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
import java.util.List;

// TODO: Add to Main Command

/**
 * Created by Kenneth on 9/11/2015.
 * for CheesecakeServerCommands in package com.itachi1706.cheesecakeservercommands.server.commands
 */
@SuppressWarnings("unused")
public class RepairCommand implements ICommand {

    private List<String> aliases;

    public RepairCommand(){
        this.aliases = new ArrayList<String>();
        this.aliases.add("repairitem");
    }

    @Override
    public String getCommandName() {
        return "repairitem";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_) {
        return "repairitem [player]";
    }

    @Override
    public List getCommandAliases() {
        return this.aliases;
    }

    @Override
    public void processCommand(ICommandSender iCommandSender, String[] astring) {

        if (astring.length == 0) {
            if (!PlayerMPUtil.isPlayer(iCommandSender)) {
                ChatHelper.sendMessage(iCommandSender, "Cannot repair item for CONSOLE");
                return;
            }

            EntityPlayerMP player = (EntityPlayerMP) PlayerMPUtil.castToPlayer(iCommandSender);
            if (player == null) {
                ChatHelper.sendMessage(iCommandSender, "Cannot repair item for " + iCommandSender.getCommandSenderName());
                return;
            }

            ItemStack item = player.getHeldItem();
            if (item == null) {
                ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.RED + "You are not holding an item that can be repaired");
                return;
            }

            item.setItemDamage(0);
            ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.GOLD + "Repaired durability for " + item.getDisplayName());
            ChatHelper.sendAdminMessage(iCommandSender, "Repaired durability for " + item.getDisplayName());
            return;
        }

        // Gamemode others
        String subname = astring[0];
        EntityPlayerMP player = PlayerMPUtil.getPlayer(subname);
        if (player == null) {
            ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.RED + "Player not found");
            return;
        }



        ItemStack item = player.getHeldItem();
        if (item == null) {
            ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.RED + player.getCommandSenderName() + " is not holding an item that can be repaired");
            return;
        }

        item.setItemDamage(0);
        ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.GOLD + "Repaired durability for " + item.getDisplayName() + " in " + player.getCommandSenderName() + "'s hand");
        ChatHelper.sendAdminMessage(iCommandSender, "Helped to repair " + player.getCommandSenderName() + "'s durability for " + item.getDisplayName());
        ChatHelper.sendMessage(player, EnumChatFormatting.GOLD + "Item Durability repaired");
        return;
    }

    @Override
    public List addTabCompletionOptions(ICommandSender iCommandSender, String[] typedValue) {
        if (typedValue.length == 1) {
            return CommandBase.getListOfStringsMatchingLastWord(typedValue, MinecraftServer.getServer().getAllUsernames());
        }
        return null;
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
