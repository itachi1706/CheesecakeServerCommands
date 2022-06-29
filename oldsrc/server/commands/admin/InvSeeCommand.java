package com.itachi1706.cheesecakeservercommands.server.commands.admin;

import com.itachi1706.cheesecakeservercommands.server.commands.util.PlayerInvChest;
import com.itachi1706.cheesecakeservercommands.util.ChatHelper;
import com.itachi1706.cheesecakeservercommands.util.PlayerMPUtil;
import com.itachi1706.cheesecakeservercommands.util.ServerUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Kenneth on 9/11/2015.
 * for CheesecakeServerCommands in package com.itachi1706.cheesecakeservercommands.server.commands
 */
@SuppressWarnings("unused")
public class InvSeeCommand implements ICommand {

    private List<String> aliases;

    public InvSeeCommand(){
        this.aliases = new ArrayList<>();
        this.aliases.add("invsee");
    }

    @Override
    @Nonnull
    public String getName() {
        return "invsee";
    }

    @Override
    @Nonnull
    public String getUsage(@Nonnull ICommandSender sender) {
        return "/invsee [player]";
    }

    @Override
    @Nonnull
    public List<String> getAliases() {
        return this.aliases;
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender iCommandSender, @Nonnull String[] args) {
        if (!PlayerMPUtil.isPlayer(iCommandSender)) {
            ChatHelper.sendMessage(iCommandSender, "CONSOLE cannot use invsee. Sorry :(");
            return;
        }
        // See your inventory
        EntityPlayerMP commandSender = (EntityPlayerMP) PlayerMPUtil.castToPlayer(iCommandSender);
        if (commandSender == null) {
            ChatHelper.sendMessage(iCommandSender, "Cannot see inventory of" + iCommandSender.getName());
            return;
        }
        if (args.length == 0)
        {
            if (commandSender.openContainer != commandSender.inventoryContainer) {
                commandSender.closeScreen();
            }
            commandSender.getNextWindowId();

            PlayerInvChest chest = new PlayerInvChest(commandSender, commandSender);
            commandSender.connection.sendPacket(new SPacketOpenWindow(commandSender.currentWindowId, "minecraft:chest", chest.getDisplayName(), chest.getSizeInventory(), commandSender.getEntityId()));
            commandSender.openContainer = new ContainerChest(commandSender.inventory, chest, commandSender);
            commandSender.openContainer.windowId = commandSender.currentWindowId;
            commandSender.openContainer.addListener(commandSender);
            ChatHelper.sendMessage(iCommandSender, TextFormatting.GOLD + "Opened your inventory");
            ChatHelper.sendAdminMessage(iCommandSender, "Seen own inventory");
            return;
        }

        // See other people's inventory
        String subname = args[0];
        EntityPlayerMP player = PlayerMPUtil.getPlayer(subname);
        if (player == null) {
            ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "Player not found");
            return;
        }

        if (commandSender.openContainer != commandSender.inventoryContainer) {
            commandSender.closeScreen();
        }
        commandSender.getNextWindowId();

        PlayerInvChest chest = new PlayerInvChest(player, commandSender);
        commandSender.connection.sendPacket(new SPacketOpenWindow(commandSender.currentWindowId, "minecraft:chest", chest.getDisplayName(), chest.getSizeInventory(), commandSender.getEntityId()));
        commandSender.openContainer = new ContainerChest(commandSender.inventory, chest, commandSender);
        commandSender.openContainer.windowId = commandSender.currentWindowId;
        commandSender.openContainer.addListener(commandSender);
        ChatHelper.sendMessage(iCommandSender, TextFormatting.GOLD + "Opened " + player.getName() + "'s inventory");
        ChatHelper.sendAdminMessage(iCommandSender, "Seen " + player.getName() + "'s inventory");
    }

    @Override
    @Nonnull
    public List<String> getTabCompletions(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1)
            return CommandBase.getListOfStringsMatchingLastWord(args, ServerUtil.getServerInstance().getOnlinePlayerNames());
        return Collections.emptyList();
    }

    @Override
    public boolean checkPermission(@Nonnull MinecraftServer server, @Nonnull ICommandSender iCommandSender) {
        return PlayerMPUtil.isOperatorOrConsole(iCommandSender);
    }

    @Override
    public boolean isUsernameIndex(@Nonnull String[] args, int p_82358_2_) {
        return false;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public int compareTo(ICommand o) {
        return 0;
    }
}
