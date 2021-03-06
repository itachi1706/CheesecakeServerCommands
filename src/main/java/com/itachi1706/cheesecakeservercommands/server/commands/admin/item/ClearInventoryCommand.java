package com.itachi1706.cheesecakeservercommands.server.commands.admin.item;

import com.itachi1706.cheesecakeservercommands.util.ChatHelper;
import com.itachi1706.cheesecakeservercommands.util.PlayerMPUtil;
import com.itachi1706.cheesecakeservercommands.util.ServerUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
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
public class ClearInventoryCommand implements ICommand {

    private List<String> aliases;

    public ClearInventoryCommand(){
        this.aliases = new ArrayList<>();
        this.aliases.add("ci");
        this.aliases.add("clearinventory");
    }

    @Override
    @Nonnull
    public String getName() {
        return "ci";
    }

    @Override
    @Nonnull
    public String getUsage(@Nonnull ICommandSender sender) {
        return "/ci [player] [item] [damage]";
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
                ChatHelper.sendMessage(iCommandSender, "Cannot clear inventory of CONSOLE");
                return;
            } else {
                EntityPlayerMP player = (EntityPlayerMP) PlayerMPUtil.castToPlayer(iCommandSender);
                if (player == null) {
                    ChatHelper.sendMessage(iCommandSender, "Cannot clear inventory of " + iCommandSender.getName());
                    return;
                }

                int clearedcount = player.inventory.clearMatchingItems(null, -1, -1, null);
                player.inventoryContainer.detectAndSendChanges();

                ChatHelper.sendMessage(iCommandSender, TextFormatting.GOLD + "Inventory Cleared of " + clearedcount + " items");
                ChatHelper.sendAdminMessage(iCommandSender, "Cleared own inventory of " + clearedcount + " items");
                return;
            }
        }

        String subname = args[0];
        EntityPlayerMP player = PlayerMPUtil.getPlayer(subname);
        if (player == null) {
            ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "Player not found");
            return;
        }

        Item itemToClear = null;
        int itemDamageValue = -1;

        if (args.length == 2) {
            // Clears only an item
            itemToClear = CommandBase.getItemByText(iCommandSender, args[1]);
        }

        if (args.length == 3) {
            // Clears damage value
            itemDamageValue = CommandBase.parseInt(args[2]);
        }

        int clearedcount = player.inventory.clearMatchingItems(itemToClear, itemDamageValue, -1, null);
        player.inventoryContainer.detectAndSendChanges();

        ChatHelper.sendMessage(iCommandSender, TextFormatting.GOLD + "Cleared Inventory of " + player.getName() + " of " + clearedcount + " items");
        ChatHelper.sendAdminMessage(iCommandSender, "Cleared Inventory of " + player.getName() + " of " + clearedcount + " items");
        ChatHelper.sendMessage(player, TextFormatting.GOLD + "Inventory Cleared of " + clearedcount + " items");
    }

    @Override
    @Nonnull
    public List<String> getTabCompletions(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1)
            return CommandBase.getListOfStringsMatchingLastWord(args, ServerUtil.getServerInstance().getOnlinePlayerNames());
        if (args.length == 2)
            return CommandBase.getListOfStringsMatchingLastWord(args, Item.REGISTRY.getKeys());
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
