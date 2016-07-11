package com.itachi1706.cheesecakeservercommands.server.commands.admin.item;

import com.itachi1706.cheesecakeservercommands.util.ChatHelper;
import com.itachi1706.cheesecakeservercommands.util.PlayerMPUtil;
import com.mojang.realmsclient.gui.ChatFormatting;
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

import javax.annotation.Nullable;
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
    public void execute(MinecraftServer server, ICommandSender iCommandSender, String[] args) throws CommandException {
        if (!PlayerMPUtil.isPlayer(iCommandSender)) {
            ChatHelper.sendMessage(iCommandSender, "Cannot invsee ender chests for CONSOLE");
            return;
        }

        EntityPlayerMP player = (EntityPlayerMP) PlayerMPUtil.castToPlayer(iCommandSender);
        if (player == null) {
            ChatHelper.sendMessage(iCommandSender, "Cannot invsee ender chests for " + iCommandSender.getName());
            return;
        }

        if (player.openContainer != player.inventoryContainer) {
            player.closeScreen();
        }

        player.getNextWindowId();

        if (args.length == 0)
        {
            InventoryEnderChest chest = player.getInventoryEnderChest();
            player.connection.sendPacket(new SPacketOpenWindow(player.currentWindowId, "minecraft:ender_chest", new TextComponentString("Ender Chest"), chest.getSizeInventory(), player.getEntityId()));
            player.openContainer = new ContainerChest(player.inventory, chest, player);
            player.openContainer.windowId = player.currentWindowId;
            player.openContainer.addListener(player);

            ChatHelper.sendMessage(iCommandSender, ChatFormatting.GOLD + "Viewing your own Ender Chest");
            ChatHelper.sendAdminMessage(iCommandSender, "Viewing own Ender Chest");
            return;
        }

        String subname = args[0];
        EntityPlayerMP victim = PlayerMPUtil.getPlayer(subname);
        if (victim == null) {
            ChatHelper.sendMessage(iCommandSender, ChatFormatting.RED + "Player not found");
            return;
        }

        InventoryEnderChest chest = victim.getInventoryEnderChest();
        player.connection.sendPacket(new SPacketOpenWindow(player.currentWindowId, "minecraft:ender_chest", new TextComponentString(victim.getName() + "'s Ender Chest"), chest.getSizeInventory(), player.getEntityId()));
        player.openContainer = new ContainerChest(player.inventory, chest, player);
        player.openContainer.windowId = player.currentWindowId;
        player.openContainer.addListener(player);

        ChatHelper.sendMessage(iCommandSender, ChatFormatting.GOLD + "Viewing " + victim.getName() + "'s Ender Chest");
        ChatHelper.sendAdminMessage(iCommandSender, "Viewing ender chest of " + victim.getName());
    }

    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender iCommandSender, String[] typedValue, @Nullable BlockPos pos) {
        if (typedValue.length == 1)
            return CommandBase.getListOfStringsMatchingLastWord(typedValue, PlayerMPUtil.getServerInstance().getAllUsernames());
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
