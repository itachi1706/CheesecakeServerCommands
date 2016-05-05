package com.itachi1706.cheesecakeservercommands.server.commands.admin.item;

import com.itachi1706.cheesecakeservercommands.server.commands.util.ContainerCheatyWorkbench;
import com.itachi1706.cheesecakeservercommands.util.ChatHelper;
import com.itachi1706.cheesecakeservercommands.util.LogHelper;
import com.itachi1706.cheesecakeservercommands.util.PlayerMPUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import java.util.ArrayList;
import java.util.List;

// TODO: Add to Main Command

/**
 * Created by Kenneth on 9/11/2015.
 * for CheesecakeServerCommands in package com.itachi1706.cheesecakeservercommands.server.commands
 */
@SuppressWarnings("unused")
public class ClearInventoryCommand implements ICommand {

    private List<String> aliases;

    public ClearInventoryCommand(){
        this.aliases = new ArrayList<String>();
        this.aliases.add("ci");
        this.aliases.add("clearinventory");
    }

    @Override
    public String getCommandName() {
        return "ci";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_) {
        return "ci [player] [item] [damage]";
    }

    @Override
    public List getCommandAliases() {
        return this.aliases;
    }

    @Override
    public void processCommand(ICommandSender iCommandSender, String[] astring) {
        if (astring.length == 0)
        {
            if (!PlayerMPUtil.isPlayer(iCommandSender)) {
                ChatHelper.sendMessage(iCommandSender, "Cannot clear inventory of CONSOLE");
                return;
            } else {
                EntityPlayerMP player = (EntityPlayerMP) PlayerMPUtil.castToPlayer(iCommandSender);
                if (player == null) {
                    ChatHelper.sendMessage(iCommandSender, "Cannot clear inventory of " + iCommandSender.getCommandSenderName());
                    return;
                }

                player.inventory.clearInventory(null, -1);
                player.inventoryContainer.detectAndSendChanges();

                ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.GOLD + "Inventory Cleared");
                ChatHelper.sendAdminMessage(iCommandSender, "Cleared own inventory");
                return;
            }
        }

        String subname = astring[0];
        EntityPlayerMP player = PlayerMPUtil.getPlayer(subname);
        if (player == null) {
            ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.RED + "Player not found");
            return;
        }

        Item itemToClear = null;
        int itemDamageValue = -1;

        if (astring.length == 2) {
            // Clears only an item
            itemToClear = CommandBase.getItemByText(iCommandSender, astring[1]);
        }

        if (astring.length == 3) {
            // Clears damage value
            try {
                itemDamageValue = Integer.parseInt(astring[2]);
            } catch (NumberFormatException e) {
                itemDamageValue = -1;
            }
        }

        player.inventory.clearInventory(itemToClear, itemDamageValue);
        player.inventoryContainer.detectAndSendChanges();

        ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.GOLD + "Cleared Inventory of " + player.getCommandSenderName());
        ChatHelper.sendAdminMessage(iCommandSender, "Cleared Inventory of " + player.getCommandSenderName());
        ChatHelper.sendMessage(player, EnumChatFormatting.GOLD + "Inventory Cleared");
    }

    @Override
    public List addTabCompletionOptions(ICommandSender iCommandSender, String[] typedValue) {
        if (typedValue.length == 1)
            return CommandBase.getListOfStringsMatchingLastWord(typedValue, MinecraftServer.getServer().getAllUsernames());
        if (typedValue.length == 2)
            return CommandBase.getListOfStringsFromIterableMatchingLastWord(typedValue, Item.itemRegistry.getKeys());
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
