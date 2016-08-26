package com.itachi1706.cheesecakeservercommands.server.commands.admin.server;

import com.itachi1706.cheesecakeservercommands.util.ChatHelper;
import com.itachi1706.cheesecakeservercommands.util.PlayerMPUtil;
import com.itachi1706.cheesecakeservercommands.util.ServerUtil;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Created by Kenneth on 9/11/2015.
 * for CheesecakeServerCommands in package com.itachi1706.cheesecakeservercommands.server.commands
 */
@SuppressWarnings("unused")
public class GetCommandBookCommand implements ICommand {

    private List<String> aliases;

    public GetCommandBookCommand(){
        this.aliases = new ArrayList<String>();
        this.aliases.add("getcommandbook");
    }

    @Override
    public String getCommandName() {
        return "getcommandbook";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_) {
        return "getcommandbook [player]";
    }

    @Override
    public List getCommandAliases() {
        return this.aliases;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender iCommandSender, String[] args) throws CommandException {

        EntityPlayerMP player = null;
        if (args.length == 0) {
            if (!PlayerMPUtil.isPlayer(iCommandSender)) {
                ChatHelper.sendMessage(iCommandSender, "Cannot give command book to CONSOLE");
                return;
            }

            player = (EntityPlayerMP) PlayerMPUtil.castToPlayer(iCommandSender);
            if (player == null) {
                ChatHelper.sendMessage(iCommandSender, "Cannot give command book to " + iCommandSender.getName());
                return;
            }
        } else {
            // Gamemode others
            String subname = args[0];
            player = PlayerMPUtil.getPlayer(subname);
            if (player == null) {
                ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "Player not found");
                return;
            }
        }

        if (player.inventory.hasItemStack(new ItemStack(Items.WRITTEN_BOOK))) {
            for (int i = 0; i < player.inventory.mainInventory.length; i++)
            {
                ItemStack e = player.inventory.mainInventory[i];
                if (e != null && e.hasTagCompound() && e.getTagCompound().hasKey("title") && e.getTagCompound().hasKey("author")
                        && e.getTagCompound().getString("title").equals("CommandBook") && e.getTagCompound().getString("author").equals("ForgeEssentials"))
                {
                    player.inventory.setInventorySlotContents(i, null);
                }
            }
        }

        Set<String> pages = new TreeSet<String>();
        for (ICommand cmd : ServerUtil.getServerInstance().getCommandManager().getCommands().values())
        {
            Set<String> commands = new HashSet<String>();
            commands.add("/" + cmd.getCommandName());

            // Add aliases
            List<?> aliases = cmd.getCommandAliases();
            if (aliases != null && aliases.size() > 0)
            {
                for (Object alias : aliases)
                    commands.add("/" + alias);
            }

            String commandusage = new TextComponentTranslation(cmd.getCommandUsage(player)).getUnformattedText();
            String text = TextFormatting.GOLD + StringUtils.join(commands, ' ') + '\n'  + TextFormatting.BLACK + commandusage;
            pages.add(text);
        }

        NBTTagList pagesNbt = new NBTTagList();
        for (String page : pages)
            pagesNbt.appendTag(new NBTTagString("{\"text\":\"" + page + "\"}"));


        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("author", "Cheesecake Network");
        tag.setString("title", "CommandBook");
        tag.setTag("pages", pagesNbt);

        ItemStack is = new ItemStack(Items.WRITTEN_BOOK);
        is.setTagCompound(tag);
        player.inventory.addItemStackToInventory(is);

        if (args.length == 0) {
            ChatHelper.sendMessage(iCommandSender, TextFormatting.GOLD + "Obtained a Command Book");
            ChatHelper.sendAdminMessage(iCommandSender, "Obtained a Command Book");
        } else {
            ChatHelper.sendMessage(iCommandSender, TextFormatting.GOLD + "Gave a Command Book to " + player.getName());
            ChatHelper.sendAdminMessage(iCommandSender, "Gave a Command Book to " + player.getName());
            ChatHelper.sendMessage(player, TextFormatting.GOLD + "Received a Command Book");
        }
    }

    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender iCommandSender, String[] typedValue, @Nullable BlockPos pos) {
        if (typedValue.length == 1) {
            return CommandBase.getListOfStringsMatchingLastWord(typedValue, ServerUtil.getServerInstance().getAllUsernames());
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
