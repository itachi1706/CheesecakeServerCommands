package com.itachi1706.cheesecakeservercommands.server.commands.admin;

import com.itachi1706.cheesecakeservercommands.util.ChatHelper;
import com.itachi1706.cheesecakeservercommands.util.PlayerMPUtil;
import com.itachi1706.cheesecakeservercommands.util.ServerUtil;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kenneth on 9/11/2015.
 * for CheesecakeServerCommands in package com.itachi1706.cheesecakeservercommands.server.commands
 */
@SuppressWarnings("unused")
public class SmiteCommand implements ICommand {

    private List<String> aliases;

    public SmiteCommand(){
        this.aliases = new ArrayList<>();
        this.aliases.add("smite");
    }

    @Override
    public String getName() {
        return "smite";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/smite [player/me/x] [y] [z]";
    }

    @Override
    public List getAliases() {
        return this.aliases;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender iCommandSender, String[] args) throws CommandException {
        if (args.length == 0)
        {
            // Smite where you are staring at
            if (!PlayerMPUtil.isPlayer(iCommandSender)) {
                ChatHelper.sendMessage(iCommandSender, "Cannot smite CONSOLE");
                return;
            } else {
                EntityPlayerMP player = (EntityPlayerMP) PlayerMPUtil.castToPlayer(iCommandSender);
                if (player == null) {
                    ChatHelper.sendMessage(iCommandSender, "Cannot smite" + iCommandSender.getName());
                    return;
                }

                RayTraceResult pos = PlayerMPUtil.getPlayerLookingSpot(player);
                if (pos == null) {
                    ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "Please make sure you are looking at a ground!");
                    return;
                }
                player.world.addWeatherEffect(new EntityLightningBolt(player.world, pos.getBlockPos().getX(), pos.getBlockPos().getY(), pos.getBlockPos().getZ(), false));
                ChatHelper.sendMessage(iCommandSender, TextFormatting.GOLD + "Struck the ground with lightning");
                ChatHelper.sendAdminMessage(iCommandSender, "Struck " + pos.getBlockPos().getX() + ", " + pos.getBlockPos().getY() + ", " + pos.getBlockPos().getZ() + " with lightning");
                return;
            }
        } else if (args.length == 1) {
            // Smite another player (or yourself)
            String subname = args[0];

            if (subname.equalsIgnoreCase("me")) {
                // Smite yourself
                if (!PlayerMPUtil.isPlayer(iCommandSender)) {
                    ChatHelper.sendMessage(iCommandSender, "Cannot smite CONSOLE");
                    return;
                } else {
                    EntityPlayerMP player = (EntityPlayerMP) PlayerMPUtil.castToPlayer(iCommandSender);
                    if (player == null) {
                        ChatHelper.sendMessage(iCommandSender, "Cannot smite" + iCommandSender.getName());
                        return;
                    }

                    player.world.addWeatherEffect(new EntityLightningBolt(player.world, player.posX, player.posY, player.posZ, false));
                    ChatHelper.sendMessage(iCommandSender, TextFormatting.GOLD + "Struck yourself with lightning");
                    ChatHelper.sendAdminMessage(iCommandSender, "Struck own self with lightning");
                    return;
                }
            }

            EntityPlayerMP player = PlayerMPUtil.getPlayer(subname);
            if (player == null) {
                ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "Player not found");
                return;
            }

            player.world.addWeatherEffect(new EntityLightningBolt(player.world, player.posX, player.posY, player.posZ, false));
            ChatHelper.sendMessage(iCommandSender, TextFormatting.GOLD + "Smited " + player.getName());
            ChatHelper.sendAdminMessage(iCommandSender, "Struck " + player.getName() + " with lightning");
            ChatHelper.sendMessage(player, TextFormatting.GOLD + "You were struck by lightning");
            return;
        } else if (args.length == 2) {
            // Invalid coords
            ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "Invalid Coordinates (requires X, Y, Z)");
            return;
        }

        // Smite a location (PlayerOnly)
        if (!PlayerMPUtil.isPlayer(iCommandSender)) {
            ChatHelper.sendMessage(iCommandSender, "CONSOLE can't smite a location. Sorry :(");
            return;
        }
        int x = 0, y = 0, z = 0;

        EntityPlayerMP player = (EntityPlayerMP) PlayerMPUtil.castToPlayer(iCommandSender);
        if (player == null) {
            ChatHelper.sendMessage(iCommandSender, TextFormatting.DARK_RED + "FATAL: Unable to get player object");
            return;
        }

        try {
            x = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            if (args[0].startsWith("~")) {
                x = (int) player.posX;
                if (args[0].length() > 1) {
                    int addval;
                    try {
                        addval = Integer.parseInt(args[0].substring(1));
                        x += addval;
                    } catch (NumberFormatException ignored) {}
                }
            } else {
                ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "Invalid Coordinates (requires X, Y, Z)");
                return;
            }
        }
        try {
            y = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            if (args[1].startsWith("~")) {
                y = (int) player.posY;
                if (args[1].length() > 1) {
                    int addval;
                    try {
                        addval = Integer.parseInt(args[1].substring(1));
                        y += addval;
                    } catch (NumberFormatException ignored) {}
                }
            } else {
                ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "Invalid Coordinates (requires X, Y, Z)");
                return;
            }
        }
        try {
            z = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            if (args[2].startsWith("~")) {
                z = (int) player.posZ;
                if (args[2].length() > 1) {
                    int addval = 0;
                    try {
                        addval = Integer.parseInt(args[2].substring(1));
                        z += addval;
                    } catch (NumberFormatException ignored) {}
                }
            } else {
                ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "Invalid Coordinates (requires X, Y, Z)");
                return;
            }
        }
        player.world.addWeatherEffect(new EntityLightningBolt(player.world, x, y, z, false));
        ChatHelper.sendMessage(iCommandSender, TextFormatting.GOLD + "Smited " + x + ", " + y + ", " + z);
        ChatHelper.sendAdminMessage(iCommandSender, "Struck " + x + ", " + y + ", " + z + " with lightning");
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1)
            return CommandBase.getListOfStringsMatchingLastWord(args, ServerUtil.getServerInstance().getOnlinePlayerNames());
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
