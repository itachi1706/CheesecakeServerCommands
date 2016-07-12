package com.itachi1706.cheesecakeservercommands.server.commands.admin.item;

import com.itachi1706.cheesecakeservercommands.server.commands.util.ContainerCheatyWorkbench;
import com.itachi1706.cheesecakeservercommands.util.ChatHelper;
import com.itachi1706.cheesecakeservercommands.util.PlayerMPUtil;
import com.itachi1706.cheesecakeservercommands.util.ServerUtil;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.block.BlockWorkbench;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
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
    public void execute(MinecraftServer server, ICommandSender iCommandSender, String[] args) throws CommandException {
        if (args.length == 0)
        {
            if (!PlayerMPUtil.isPlayer(iCommandSender)) {
                ChatHelper.sendMessage(iCommandSender, "Cannot open the crafting window of CONSOLE");
                return;
            } else {
                EntityPlayerMP player = (EntityPlayerMP) PlayerMPUtil.castToPlayer(iCommandSender);
                if (player == null) {
                    ChatHelper.sendMessage(iCommandSender, "Cannot open the crafting window of " + iCommandSender.getName());
                    return;
                }

                player.getNextWindowId();
                player.displayGui(new BlockWorkbench.InterfaceCraftingTable(player.worldObj, new BlockPos(player.posX, player.posY, player.posZ)));
                player.openContainer = new ContainerCheatyWorkbench(player.inventory, player.worldObj);
                player.openContainer.windowId = player.currentWindowId;
                player.openContainer.addListener(player);

                ChatHelper.sendMessage(iCommandSender, ChatFormatting.GOLD + "Opened Crafting Window");
                ChatHelper.sendAdminMessage(iCommandSender, "Opened crafting window");
                return;
            }
        }

        String subname = args[0];
        EntityPlayerMP player = PlayerMPUtil.getPlayer(subname);
        if (player == null) {
            ChatHelper.sendMessage(iCommandSender, ChatFormatting.RED + "Player not found");
            return;
        }

        player.getNextWindowId();
        player.displayGui(new BlockWorkbench.InterfaceCraftingTable(player.worldObj, new BlockPos(player.posX, player.posY, player.posZ)));
        player.openContainer = new ContainerCheatyWorkbench(player.inventory, player.worldObj);
        player.openContainer.windowId = player.currentWindowId;
        player.openContainer.addListener(player);

        ChatHelper.sendMessage(iCommandSender, ChatFormatting.GOLD + "Opened Crafting Window for " + player.getName());
        ChatHelper.sendAdminMessage(iCommandSender, "Opened crafting window for " + player.getName());
        ChatHelper.sendMessage(player, ChatFormatting.GOLD + "Opened Crafting Window");
    }

    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender iCommandSender, String[] typedValue, @Nullable BlockPos pos) {
        if (typedValue.length == 1)
            return CommandBase.getListOfStringsMatchingLastWord(typedValue, ServerUtil.getServerInstance().getAllUsernames());
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
