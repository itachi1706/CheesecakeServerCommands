package com.itachi1706.cheesecakeservercommands.server.commands.admin.item;

import com.itachi1706.cheesecakeservercommands.util.ChatHelper;
import com.itachi1706.cheesecakeservercommands.util.PlayerMPUtil;
import com.itachi1706.cheesecakeservercommands.util.ServerUtil;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
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
public class RepairCommand implements ICommand {

    private List<String> aliases;

    public RepairCommand(){
        this.aliases = new ArrayList<>();
        this.aliases.add("repairitem");
    }

    @Override
    public String getName() {
        return "repairitem";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/repairitem [player]";
    }

    @Override
    public List getAliases() {
        return this.aliases;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender iCommandSender, String[] args) throws CommandException {

        if (args.length == 0) {
            if (!PlayerMPUtil.isPlayer(iCommandSender)) {
                ChatHelper.sendMessage(iCommandSender, "Cannot repair item for CONSOLE");
                return;
            }

            EntityPlayerMP player = (EntityPlayerMP) PlayerMPUtil.castToPlayer(iCommandSender);
            if (player == null) {
                ChatHelper.sendMessage(iCommandSender, "Cannot repair item for " + iCommandSender.getName());
                return;
            }

            ItemStack item = player.getHeldItemMainhand();
            if (item == null) {
                ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "You are not holding an item that can be repaired");
                return;
            }

            item.setItemDamage(0);
            ChatHelper.sendMessage(iCommandSender, TextFormatting.GOLD + "Repaired durability for " + item.getDisplayName());
            ChatHelper.sendAdminMessage(iCommandSender, "Repaired durability for " + item.getDisplayName());
            return;
        }

        // Gamemode others
        String subname = args[0];
        EntityPlayerMP player = PlayerMPUtil.getPlayer(subname);
        if (player == null) {
            ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "Player not found");
            return;
        }



        ItemStack item = player.getHeldItemMainhand();
        if (item == null) {
            ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + player.getName() + " is not holding an item that can be repaired");
            return;
        }

        item.setItemDamage(0);
        ChatHelper.sendMessage(iCommandSender, TextFormatting.GOLD + "Repaired durability for " + item.getDisplayName() + " in " + player.getName() + "'s hand");
        ChatHelper.sendAdminMessage(iCommandSender, "Helped to repair " + player.getName() + "'s durability for " + item.getDisplayName());
        ChatHelper.sendMessage(player, TextFormatting.GOLD + "Item Durability repaired");
        return;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1) {
            return CommandBase.getListOfStringsMatchingLastWord(args, ServerUtil.getServerInstance().getOnlinePlayerNames());
        }
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
