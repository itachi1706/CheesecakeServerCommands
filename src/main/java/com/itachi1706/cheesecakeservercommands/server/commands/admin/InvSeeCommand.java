package com.itachi1706.cheesecakeservercommands.server.commands.admin;

import com.itachi1706.cheesecakeservercommands.util.ChatHelper;
import com.itachi1706.cheesecakeservercommands.util.PlayerInvChest;
import com.itachi1706.cheesecakeservercommands.util.PlayerMPUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kenneth on 9/11/2015.
 * for CheesecakeServerCommands in package com.itachi1706.cheesecakeservercommands.server.commands
 */
@SuppressWarnings("unused")
public class InvSeeCommand implements ICommand {

    private List<String> aliases;

    public InvSeeCommand(){
        this.aliases = new ArrayList<String>();
        this.aliases.add("invsee");
    }

    @Override
    public String getCommandName() {
        return "invsee";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_) {
        return "invsee [player]";
    }

    @Override
    public List getCommandAliases() {
        return this.aliases;
    }

    @Override
    public void processCommand(ICommandSender iCommandSender, String[] astring) {
        if (!PlayerMPUtil.isPlayer(iCommandSender)) {
            ChatHelper.sendMessage(iCommandSender, "CONSOLE cannot use invsee. Sorry :(");
            return;
        }
        // See your inventory
        EntityPlayerMP commandSender = (EntityPlayerMP) PlayerMPUtil.castToPlayer(iCommandSender);
        if (commandSender == null) {
            ChatHelper.sendMessage(iCommandSender, "Cannot see inventory of" + iCommandSender.getCommandSenderName());
            return;
        }
        if (astring.length == 0)
        {
            if (commandSender.openContainer != commandSender.inventoryContainer) {
                commandSender.closeScreen();
            }
            commandSender.getNextWindowId();

            PlayerInvChest chest = new PlayerInvChest(commandSender, commandSender);
            commandSender.playerNetServerHandler.sendPacket(new S2DPacketOpenWindow(commandSender.currentWindowId, 0, chest.getInventoryName(), chest.getSizeInventory(), true));
            commandSender.openContainer = new ContainerChest(commandSender.inventory, chest);
            commandSender.openContainer.windowId = commandSender.currentWindowId;
            commandSender.openContainer.addCraftingToCrafters(commandSender);
            ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.GOLD + "Opened your inventory");
            ChatHelper.sendAdminMessage(iCommandSender, "Seen own inventory");
            return;
        }

        // See other people's inventory
        String subname = astring[0];
        EntityPlayerMP player = PlayerMPUtil.getPlayer(subname);
        if (player == null) {
            ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.RED + "Player not found");
            return;
        }

        if (commandSender.openContainer != commandSender.inventoryContainer) {
            commandSender.closeScreen();
        }
        commandSender.getNextWindowId();

        PlayerInvChest chest = new PlayerInvChest(player, commandSender);
        commandSender.playerNetServerHandler.sendPacket(new S2DPacketOpenWindow(commandSender.currentWindowId, 0, chest.getInventoryName(), chest.getSizeInventory(), true));
        commandSender.openContainer = new ContainerChest(commandSender.inventory, chest);
        commandSender.openContainer.windowId = commandSender.currentWindowId;
        commandSender.openContainer.addCraftingToCrafters(commandSender);
        ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.GOLD + "Opened " + player.getCommandSenderName() + "'s inventory");
        ChatHelper.sendAdminMessage(iCommandSender, "Seen " + player.getCommandSenderName() + "'s inventory");
    }

    @Override
    public List addTabCompletionOptions(ICommandSender iCommandSender, String[] typedValue) {
        if (typedValue.length == 1)
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
