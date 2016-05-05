package com.itachi1706.cheesecakeservercommands.server.commands.admin.item;

import com.itachi1706.cheesecakeservercommands.util.ChatHelper;
import com.itachi1706.cheesecakeservercommands.util.PlayerMPUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
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
public class InvSeeEnderChestCommand implements ICommand {

    private List<String> aliases;

    public InvSeeEnderChestCommand(){
        this.aliases = new ArrayList<String>();
        this.aliases.add("invseeender");
    }

    @Override
    public String getCommandName() {
        return "invseeender";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_) {
        return "invseeender [player]";
    }

    @Override
    public List getCommandAliases() {
        return this.aliases;
    }

    @Override
    public void processCommand(ICommandSender iCommandSender, String[] astring) {
        if (!PlayerMPUtil.isPlayer(iCommandSender)) {
            ChatHelper.sendMessage(iCommandSender, "Cannot invsee ender chests for CONSOLE");
            return;
        }

        EntityPlayerMP player = (EntityPlayerMP) PlayerMPUtil.castToPlayer(iCommandSender);
        if (player == null) {
            ChatHelper.sendMessage(iCommandSender, "Cannot invsee ender chests for " + iCommandSender.getCommandSenderName());
            return;
        }

        if (player.openContainer != player.inventoryContainer) {
            player.closeScreen();
        }

        player.getNextWindowId();

        if (astring.length == 0)
        {
            InventoryEnderChest chest = player.getInventoryEnderChest();
            player.playerNetServerHandler.sendPacket(new S2DPacketOpenWindow(player.currentWindowId, 0, "Ender Chest", chest.getSizeInventory(), true));
            player.openContainer = new ContainerChest(player.inventory, chest);
            player.openContainer.windowId = player.currentWindowId;
            player.openContainer.addCraftingToCrafters(player);

            ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.GOLD + "Viewing your own Ender Chest");
            ChatHelper.sendAdminMessage(iCommandSender, "Viewing own Ender Chest");
            return;
        }

        String subname = astring[0];
        EntityPlayerMP victim = PlayerMPUtil.getPlayer(subname);
        if (victim == null) {
            ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.RED + "Player not found");
            return;
        }

        InventoryEnderChest chest = victim.getInventoryEnderChest();
        player.playerNetServerHandler.sendPacket(new S2DPacketOpenWindow(player.currentWindowId, 0, victim.getCommandSenderName() + "'s Ender Chest", chest.getSizeInventory(), true));
        player.openContainer = new ContainerChest(player.inventory, chest);
        player.openContainer.windowId = player.currentWindowId;
        player.openContainer.addCraftingToCrafters(player);

        ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.GOLD + "Viewing " + player.getCommandSenderName() + "'s Ender Chest");
        ChatHelper.sendAdminMessage(iCommandSender, "Viewing ender chest of " + player.getCommandSenderName());
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
