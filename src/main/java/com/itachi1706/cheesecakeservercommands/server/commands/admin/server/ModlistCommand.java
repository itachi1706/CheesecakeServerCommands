package com.itachi1706.cheesecakeservercommands.server.commands.admin.server;

import com.itachi1706.cheesecakeservercommands.util.ChatHelper;
import com.itachi1706.cheesecakeservercommands.util.PlayerMPUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Kenneth on 9/11/2015.
 * for CheesecakeServerCommands in package com.itachi1706.cheesecakeservercommands.server.commands
 */
@SuppressWarnings("unused")
public class ModlistCommand implements ICommand {

    private List<String> aliases;

    public ModlistCommand(){
        this.aliases = new ArrayList<>();
        this.aliases.add("modlist");
    }

    @Override
    public String getName() {
        return "modlist";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/modlist [page]";
    }

    @Override
    public List getAliases() {
        return this.aliases;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender iCommandSender, String[] args) throws CommandException {
        int size = Loader.instance().getModList().size();
        int perPage = 7;
        int pages = (int) Math.ceil(size / (float) perPage);

        int page = 0;
        if (args.length > 0) {
            page = CommandBase.parseInt(args[0]);
            page -= 1;
        }
        int min = Math.min(page * perPage, size);

        if (page >= pages) {
            ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "Invalid Page. There are only " + pages + " pages");
            return;
        }

        ChatHelper.sendMessage(iCommandSender, String.format(TextFormatting.GOLD + "--- Showing modlist page %1$d of %2$d ---", page + 1, pages));
        for (int i = page * perPage; i < min + perPage; i++)
        {
            if (i >= size)
            {
                break;
            }
            ModContainer mod = Loader.instance().getModList().get(i);
            ChatHelper.sendMessage(iCommandSender, mod.getName() + " - " + TextFormatting.AQUA + mod.getVersion());
        }
        ChatHelper.sendMessage(iCommandSender, TextFormatting.GOLD + "-------------------------------");
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        return Collections.emptyList();
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
