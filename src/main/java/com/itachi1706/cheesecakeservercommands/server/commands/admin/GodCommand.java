package com.itachi1706.cheesecakeservercommands.server.commands.admin;

import com.itachi1706.cheesecakeservercommands.util.ChatHelper;
import com.itachi1706.cheesecakeservercommands.util.PlayerMPUtil;
import com.itachi1706.cheesecakeservercommands.util.ServerUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Kenneth on 1/7/2018.
 * for CheesecakeServerCommands in package com.itachi1706.cheesecakeservercommands.server.commands
 */
@SuppressWarnings("unused")
public class GodCommand implements ICommand {

    private List<String> aliases;

    public GodCommand(){
        this.aliases = new ArrayList<>();
        this.aliases.add("god");
    }

    @Override
    @Nonnull
    public String getName() {
        return "god";
    }

    @Override
    @Nonnull
    public String getUsage(@Nonnull ICommandSender sender) {
        return "/god [player]";
    }

    @Override
    @Nonnull
    public List<String> getAliases() {
        return this.aliases;
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender iCommandSender, @Nonnull String[] args) {

        if(args.length == 0)
        {
            if (!PlayerMPUtil.isPlayer(iCommandSender)) {
                ChatHelper.sendMessage(iCommandSender, "Cannot set invulnerability status of CONSOLE");
                return;
            } else {
                EntityPlayerMP player = (EntityPlayerMP) PlayerMPUtil.castToPlayer(iCommandSender);
                if (player == null) {
                    ChatHelper.sendMessage(iCommandSender, "Cannot set " + iCommandSender.getName() + "'s invulnerability status");
                    return;
                }
                player.capabilities.disableDamage = !player.capabilities.disableDamage;
                player.sendPlayerAbilities();
                ChatHelper.sendMessage(iCommandSender, "God Mode " + (player.capabilities.disableDamage ? TextFormatting.GREEN + "Enabled" : TextFormatting.RED + "Disabled"));
                ChatHelper.sendAdminMessage(iCommandSender, "Set own invulnerability to " + (player.capabilities.disableDamage ? "Invulnerable" : "Vulnerable"));
                return;
            }
        }

        String subname = args[0];
        EntityPlayerMP player = PlayerMPUtil.getPlayer(subname);
        if (player == null) {
            ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "Player not found");
            return;
        }

        player.capabilities.disableDamage = !player.capabilities.disableDamage;
        player.sendPlayerAbilities();
        ChatHelper.sendMessage(iCommandSender, "Set " + player.getName() + " invulnerability to " + (player.capabilities.disableDamage ? TextFormatting.GREEN + "Invulnerable" : TextFormatting.RED + "Vulnerable"));
        ChatHelper.sendMessage(player, "God Mode " + (player.capabilities.disableDamage ? TextFormatting.GREEN + "Enabled" : TextFormatting.RED + "Disabled"));
        ChatHelper.sendAdminMessage(iCommandSender, "Set " + player.getName() + " invulnerability to " + (player.capabilities.disableDamage ? "Invulnerable" : "Vulnerable"));
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
