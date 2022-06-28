package com.itachi1706.cheesecakeservercommands.server.commands.admin.item;

import com.itachi1706.cheesecakeservercommands.util.ChatHelper;
import com.itachi1706.cheesecakeservercommands.util.LogHelper;
import com.itachi1706.cheesecakeservercommands.util.PlayerMPUtil;
import com.itachi1706.cheesecakeservercommands.util.ServerUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
public class GiveItemCommand implements ICommand {

    private List<String> aliases;

    public GiveItemCommand(){
        this.aliases = new ArrayList<>();
        this.aliases.add("i");
        this.aliases.add("giveitem");
    }

    @Override
    @Nonnull
    public String getName() {
        return "giveitem";
    }

    @Override
    @Nonnull
    public String getUsage(@Nonnull ICommandSender sender) {
        return "/giveitem <item> [amount] [data] [player] [spillover] (Spillover if inventory gets full while duplicating)";
    }

    @Override
    @Nonnull
    public List<String> getAliases() {
        return this.aliases;
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender iCommandSender, @Nonnull String[] args) throws CommandException {
        if (args.length == 0) {
            ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "Usage: /giveitem <item> [amount] [data] [player] [spillover]");
            ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "Spillover will spew items on the ground after inventory is filled");
            return;
        }

        if (args.length == 1)
        {
            if (!PlayerMPUtil.isPlayer(iCommandSender)) {
                ChatHelper.sendMessage(iCommandSender, "Cannot give items to CONSOLE");
                return;
            }
        }

        Item itemToAdd;
        int itemDamageValue = 0;
        int stacksize = 1;

        itemToAdd = CommandBase.getItemByText(iCommandSender, args[0]);
        LogHelper.info(itemToAdd.getUnlocalizedName());

        if (args.length >= 2) {
            // Set item stack size
            stacksize = CommandBase.parseInt(args[1]);
        }

        if (args.length >= 3) {
            // Gets item damage value
            itemDamageValue = CommandBase.parseInt(args[2]);
        }

        ItemStack itemStack = new ItemStack(itemToAdd, stacksize, itemDamageValue);

        EntityPlayerMP player;
        boolean toOthers = false;
        if (args.length >= 4) {
            String subname = args[3];
            player = PlayerMPUtil.getPlayer(subname);
            if (player == null) {
                ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "Player not found");
                return;
            }
            toOthers = true;
        } else {
            player = (EntityPlayerMP) PlayerMPUtil.castToPlayer(iCommandSender);
            if (player == null) {
                ChatHelper.sendMessage(iCommandSender, "Cannot give items to " + iCommandSender.getName());
                return;
            }
        }

        boolean spillover = false;
        if (args.length == 5) {
            spillover = CommandBase.parseBoolean(args[4]);
        }

        if (spillover) {
            PlayerMPUtil.give(player, itemStack);
        } else {
            PlayerMPUtil.giveNormal(player, itemStack);
        }

        String messageToSender;
        String adminMessage;
        if (toOthers) {
            messageToSender = TextFormatting.GOLD + "Gave " + TextFormatting.AQUA + stacksize + TextFormatting.GOLD
                    + " of " + TextFormatting.LIGHT_PURPLE + itemStack.getDisplayName() + TextFormatting.GOLD + " to " + player.getName();
            adminMessage = "Gave " + stacksize + " of " + itemStack.getDisplayName() + " to " + player.getName();
            String messageToRecepient = TextFormatting.GOLD +  "Received " + TextFormatting.AQUA + stacksize + TextFormatting.GOLD
                    + " of " + TextFormatting.LIGHT_PURPLE + itemStack.getDisplayName();

            ChatHelper.sendMessage(player, messageToRecepient);
        } else {
            messageToSender = TextFormatting.GOLD +  "Received " + TextFormatting.AQUA + stacksize + TextFormatting.GOLD
                    + " of " + TextFormatting.LIGHT_PURPLE + itemStack.getDisplayName();
            adminMessage = "Gave " + stacksize + " of " + itemStack.getDisplayName() + " to self";
        }

        ChatHelper.sendMessage(iCommandSender, messageToSender);
        ChatHelper.sendAdminMessage(iCommandSender, adminMessage);
    }

    @Override
    @Nonnull
    public List<String> getTabCompletions(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1)
            return CommandBase.getListOfStringsMatchingLastWord(args, Item.REGISTRY.getKeys());
        if (args.length == 4)
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
