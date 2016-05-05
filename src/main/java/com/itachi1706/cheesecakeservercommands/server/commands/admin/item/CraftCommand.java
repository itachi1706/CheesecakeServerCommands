package com.itachi1706.cheesecakeservercommands.server.commands.admin.item;

import com.itachi1706.cheesecakeservercommands.server.commands.util.ContainerCheatyWorkbench;
import com.itachi1706.cheesecakeservercommands.util.ChatHelper;
import com.itachi1706.cheesecakeservercommands.util.PlayerMPUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
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
public class CraftCommand implements ICommand {

    private List<String> aliases;

    public CraftCommand(){
        this.aliases = new ArrayList<String>();
        this.aliases.add("craft");
    }

    @Override
    public String getCommandName() {
        return "craft";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_) {
        return "craft [player]";
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
                ChatHelper.sendMessage(iCommandSender, "Cannot open the crafting window of CONSOLE");
                return;
            } else {
                EntityPlayerMP player = (EntityPlayerMP) PlayerMPUtil.castToPlayer(iCommandSender);
                if (player == null) {
                    ChatHelper.sendMessage(iCommandSender, "Cannot open the crafting window of " + iCommandSender.getCommandSenderName());
                    return;
                }

                player.getNextWindowId();
                player.playerNetServerHandler.sendPacket(new S2DPacketOpenWindow(player.currentWindowId, 1, "Crafting", 9, true));
                player.openContainer = new ContainerCheatyWorkbench(player.inventory, player.worldObj);
                player.openContainer.windowId = player.currentWindowId;
                player.openContainer.addCraftingToCrafters(player);

                ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.GOLD + "Opened Crafting Window");
                ChatHelper.sendAdminMessage(iCommandSender, "Opened crafting window");
                return;
            }
        }

        String subname = astring[0];
        EntityPlayerMP player = PlayerMPUtil.getPlayer(subname);
        if (player == null) {
            ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.RED + "Player not found");
            return;
        }

        player.getNextWindowId();
        player.playerNetServerHandler.sendPacket(new S2DPacketOpenWindow(player.currentWindowId, 1, "Crafting", 9, true));
        player.openContainer = new ContainerCheatyWorkbench(player.inventory, player.worldObj);
        player.openContainer.windowId = player.currentWindowId;
        player.openContainer.addCraftingToCrafters(player);

        ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.GOLD + "Opened Crafting Window for " + player.getCommandSenderName());
        ChatHelper.sendAdminMessage(iCommandSender, "Opened crafting window for " + player.getCommandSenderName());
        ChatHelper.sendMessage(player, EnumChatFormatting.GOLD + "Opened Crafting Window");
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
