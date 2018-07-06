package com.itachi1706.cheesecakeservercommands.server.commands.admin.item;

import com.itachi1706.cheesecakeservercommands.util.ChatHelper;
import com.itachi1706.cheesecakeservercommands.util.PlayerMPUtil;
import com.itachi1706.cheesecakeservercommands.util.ServerUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
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
public class EnderChestCommand implements ICommand {

    private List<String> aliases;

    public EnderChestCommand(){
        this.aliases = new ArrayList<>();
        this.aliases.add("enderchest");
    }

    @Override
    @Nonnull
    public String getName() {
        return "enderchest";
    }

    @Override
    @Nonnull
    public String getUsage(@Nonnull ICommandSender sender) {
        return "/enderchest [player]";
    }

    @Override
    @Nonnull
    public List<String> getAliases() {
        return this.aliases;
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender iCommandSender, @Nonnull String[] args) throws CommandException {
        if (args.length == 0)
        {
            if (!PlayerMPUtil.isPlayer(iCommandSender)) {
                ChatHelper.sendMessage(iCommandSender, "Cannot open the ender chest of CONSOLE");
                return;
            } else {
                EntityPlayerMP player = (EntityPlayerMP) PlayerMPUtil.castToPlayer(iCommandSender);
                if (player == null) {
                    ChatHelper.sendMessage(iCommandSender, "Cannot open the ender chest " + iCommandSender.getName());
                    return;
                }

                if (player.openContainer != player.inventoryContainer) {
                    player.closeScreen();
                }

                player.getNextWindowId();

                InventoryEnderChest chest = player.getInventoryEnderChest();
                player.connection.sendPacket(new SPacketOpenWindow(player.currentWindowId, "minecraft:ender_chest", new TextComponentString("Ender Chest"), chest.getSizeInventory(), player.getEntityId()));
                player.openContainer = new ContainerChest(player.inventory, chest, player);
                player.openContainer.windowId = player.currentWindowId;
                player.openContainer.addListener(player);

                ChatHelper.sendMessage(iCommandSender, TextFormatting.GOLD + "Opened Ender Chest");
                ChatHelper.sendAdminMessage(iCommandSender, "Opened Ender Chest");
                return;
            }
        }

        String subname = args[0];
        EntityPlayerMP player = PlayerMPUtil.getPlayer(subname);
        if (player == null) {
            ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "Player not found");
            return;
        }

        if (player.openContainer != player.inventoryContainer) {
            player.closeScreen();
        }

        player.getNextWindowId();

        InventoryEnderChest chest = player.getInventoryEnderChest();
        player.connection.sendPacket(new SPacketOpenWindow(player.currentWindowId, "minecraft:ender_chest", new TextComponentString("Ender Chest"), chest.getSizeInventory(), player.getEntityId()));
        player.openContainer = new ContainerChest(player.inventory, chest, player);
        player.openContainer.windowId = player.currentWindowId;
        player.openContainer.addListener(player);

        ChatHelper.sendMessage(iCommandSender, TextFormatting.GOLD + "Opened Ender Chest for " + player.getName());
        ChatHelper.sendAdminMessage(iCommandSender, "Opened ender chest for " + player.getName());
        ChatHelper.sendMessage(player, TextFormatting.GOLD + "Opened Ender Chest");
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
