package com.itachi1706.cheesecakeservercommands.server.commands.admin.item;

import com.itachi1706.cheesecakeservercommands.util.ChatHelper;
import com.itachi1706.cheesecakeservercommands.util.LogHelper;
import com.itachi1706.cheesecakeservercommands.util.PlayerMPUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
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
public class GiveItemCommand implements ICommand {

    private List<String> aliases;

    public GiveItemCommand(){
        this.aliases = new ArrayList<String>();
        this.aliases.add("i");
        this.aliases.add("giveitem");
    }

    @Override
    public String getCommandName() {
        return "giveitem";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_) {
        return "giveitem <item> [amount] [data] [player] [spillover] (Spillover if inventory gets full while duplicating)";
    }

    @Override
    public List getCommandAliases() {
        return this.aliases;
    }

    @Override
    public void processCommand(ICommandSender iCommandSender, String[] astring) {
        if (astring.length == 0) {
            ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.RED + "Usage: /giveitem <item> [amount] [data] [player] [spillover]");
            ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.RED + "Spillover will spew items on the ground after inventory is filled");
            return;
        }

        if (astring.length == 1)
        {
            if (!PlayerMPUtil.isPlayer(iCommandSender)) {
                ChatHelper.sendMessage(iCommandSender, "Cannot give items to CONSOLE");
                return;
            }
        }

        Item itemToAdd = null;
        int itemDamageValue = 0;
        int stacksize = 1;

        itemToAdd = CommandBase.getItemByText(iCommandSender, astring[0]);
        LogHelper.info(itemToAdd.getUnlocalizedName());

        if (astring.length >= 2) {
            // Set item stack size
            stacksize = CommandBase.parseInt(iCommandSender, astring[1]);
        }

        if (astring.length >= 3) {
            // Gets item damage value
            itemDamageValue = CommandBase.parseInt(iCommandSender, astring[2]);
        }

        ItemStack itemStack = new ItemStack(itemToAdd, stacksize, itemDamageValue);

        EntityPlayerMP player;
        boolean toOthers = false;
        if (astring.length >= 4) {
            String subname = astring[3];
            player = PlayerMPUtil.getPlayer(subname);
            if (player == null) {
                ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.RED + "Player not found");
                return;
            }
            toOthers = true;
        } else {
            player = (EntityPlayerMP) PlayerMPUtil.castToPlayer(iCommandSender);
            if (player == null) {
                ChatHelper.sendMessage(iCommandSender, "Cannot give items to " + iCommandSender.getCommandSenderName());
                return;
            }
        }

        boolean spillover = false;
        if (astring.length == 5) {
            spillover = CommandBase.parseBoolean(iCommandSender, astring[4]);
        }

        if (spillover) {
            PlayerMPUtil.give(player, itemStack);
        } else {
            PlayerMPUtil.giveNormal(player, itemStack);
        }

        String messageToSender;
        String adminMessage;
        if (toOthers) {
            messageToSender = EnumChatFormatting.GOLD + "Gave " + EnumChatFormatting.AQUA + stacksize + EnumChatFormatting.GOLD
                    + " of " + EnumChatFormatting.LIGHT_PURPLE + itemStack.getDisplayName() + EnumChatFormatting.GOLD + " to " + player.getCommandSenderName();
            adminMessage = "Gave " + stacksize + " of " + itemStack.getDisplayName() + " to " + player.getCommandSenderName();
            String messageToRecepient = EnumChatFormatting.GOLD +  "Received " + EnumChatFormatting.AQUA + stacksize + EnumChatFormatting.GOLD
                    + " of " + EnumChatFormatting.LIGHT_PURPLE + itemStack.getDisplayName();

            ChatHelper.sendMessage(player, messageToRecepient);
        } else {
            messageToSender = EnumChatFormatting.GOLD +  "Received " + EnumChatFormatting.AQUA + stacksize + EnumChatFormatting.GOLD
                    + " of " + EnumChatFormatting.LIGHT_PURPLE + itemStack.getDisplayName();;
            adminMessage = "Gave " + stacksize + " of " + itemStack.getDisplayName() + " to self";
        }

        ChatHelper.sendMessage(iCommandSender, messageToSender);
        ChatHelper.sendAdminMessage(iCommandSender, adminMessage);
    }

    @Override
    public List addTabCompletionOptions(ICommandSender iCommandSender, String[] typedValue) {
        if (typedValue.length == 1)
            return CommandBase.getListOfStringsFromIterableMatchingLastWord(typedValue, Item.itemRegistry.getKeys());
        if (typedValue.length == 4)
            return CommandBase.getListOfStringsMatchingLastWord(typedValue, MinecraftServer.getServer().getAllUsernames());
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
