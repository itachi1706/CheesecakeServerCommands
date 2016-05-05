package com.itachi1706.cheesecakeservercommands.server.commands.admin.server;

import com.itachi1706.cheesecakeservercommands.util.ChatHelper;
import com.itachi1706.cheesecakeservercommands.util.PlayerMPUtil;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
import java.util.List;

// TODO: Add to Main Command

/**
 * Created by Kenneth on 9/11/2015.
 * for CheesecakeServerCommands in package com.itachi1706.cheesecakeservercommands.server.commands
 */
@SuppressWarnings("unused")
public class ModlistCommand implements ICommand {

    private List<String> aliases;

    public ModlistCommand(){
        this.aliases = new ArrayList<String>();
        this.aliases.add("modlist");
    }

    @Override
    public String getCommandName() {
        return "modlist";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_) {
        return "modlist [page]";
    }

    @Override
    public List getCommandAliases() {
        return this.aliases;
    }

    @Override
    public void processCommand(ICommandSender iCommandSender, String[] astring) {
        int size = Loader.instance().getModList().size();
        int perPage = 7;
        int pages = (int) Math.ceil(size / (float) perPage);

        int page = 0;
        if (astring.length > 0) {
            try {
                page = Integer.parseInt(astring[0]);
            } catch (NumberFormatException e) {
                page = 1;
            }
            page -= 1;
        }
        int min = Math.min(page * perPage, size);

        ChatHelper.sendMessage(iCommandSender, String.format(EnumChatFormatting.GOLD + "--- Showing modlist page %1$d of %2$d ---", page + 1, pages));
        for (int i = page * perPage; i < min + perPage; i++)
        {
            if (i >= size)
            {
                break;
            }
            ModContainer mod = Loader.instance().getModList().get(i);
            ChatHelper.sendMessage(iCommandSender, mod.getName() + " - " + EnumChatFormatting.AQUA + mod.getVersion());
        }
        ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.GOLD + "-------------------------------");
    }

    @Override
    public List addTabCompletionOptions(ICommandSender iCommandSender, String[] typedValue) {
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
