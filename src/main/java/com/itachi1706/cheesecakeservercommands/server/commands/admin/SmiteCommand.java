package com.itachi1706.cheesecakeservercommands.server.commands.admin;

import com.itachi1706.cheesecakeservercommands.util.ChatHelper;
import com.itachi1706.cheesecakeservercommands.util.PlayerMPUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;

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
        this.aliases = new ArrayList<String>();
        this.aliases.add("smite");
    }

    @Override
    public String getCommandName() {
        return "smite";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_) {
        return "smite [player/me/x] [y] [z]";
    }

    @Override
    public List getCommandAliases() {
        return this.aliases;
    }

    @Override
    public void processCommand(ICommandSender iCommandSender, String[] astring) {
        if (astring.length == 0)
        {
            // Smite where you are staring at
            if (!PlayerMPUtil.isPlayer(iCommandSender)) {
                ChatHelper.sendMessage(iCommandSender, "Cannot smite CONSOLE");
                return;
            } else {
                EntityPlayerMP player = (EntityPlayerMP) PlayerMPUtil.castToPlayer(iCommandSender);
                if (player == null) {
                    ChatHelper.sendMessage(iCommandSender, "Cannot smite" + iCommandSender.getCommandSenderName());
                    return;
                }

                MovingObjectPosition pos = PlayerMPUtil.getPlayerLookingSpot(player);
                if (pos == null) {
                    ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.RED + "Please make sure you are looking at a ground!");
                    return;
                }
                player.worldObj.addWeatherEffect(new EntityLightningBolt(player.worldObj, pos.blockX, pos.blockY, pos.blockZ));
                ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.GOLD + "Struck the ground with lightning");
                ChatHelper.sendAdminMessage(iCommandSender, "Struck " + pos.blockX + ", " + pos.blockY + ", " + pos.blockZ + " with lightning");
                return;
            }
        } else if (astring.length == 1) {
            // Smite another player (or yourself)
            String subname = astring[0];

            if (subname.equalsIgnoreCase("me")) {
                // Smite yourself
                if (!PlayerMPUtil.isPlayer(iCommandSender)) {
                    ChatHelper.sendMessage(iCommandSender, "Cannot smite CONSOLE");
                    return;
                } else {
                    EntityPlayerMP player = (EntityPlayerMP) PlayerMPUtil.castToPlayer(iCommandSender);
                    if (player == null) {
                        ChatHelper.sendMessage(iCommandSender, "Cannot smite" + iCommandSender.getCommandSenderName());
                        return;
                    }

                    player.worldObj.addWeatherEffect(new EntityLightningBolt(player.worldObj, player.posX, player.posY, player.posZ));
                    ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.GOLD + "Struck yourself with lightning");
                    ChatHelper.sendAdminMessage(iCommandSender, "Struck own self with lightning");
                    return;
                }
            }

            EntityPlayerMP player = PlayerMPUtil.getPlayer(subname);
            if (player == null) {
                ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.RED + "Player not found");
                return;
            }

            player.worldObj.addWeatherEffect(new EntityLightningBolt(player.worldObj, player.posX, player.posY, player.posZ));
            ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.GOLD + "Smited " + player.getCommandSenderName());
            ChatHelper.sendAdminMessage(iCommandSender, "Struck " + player.getCommandSenderName() + " with lightning");
            ChatHelper.sendMessage(player, EnumChatFormatting.GOLD + "You were struck by lightning");
            return;
        } else if (astring.length == 2) {
            // Invalid coords
            ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.RED + "Invalid Coordinates (requires X, Y, Z)");
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
            ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.DARK_RED + "FATAL: Unable to get player object");
            return;
        }

        try {
            x = Integer.parseInt(astring[0]);
        } catch (NumberFormatException e) {
            if (astring[0].startsWith("~")) {
                x = (int) player.posX;
                if (astring[0].length() > 1) {
                    int addval;
                    try {
                        addval = Integer.parseInt(astring[0].substring(1));
                        x += addval;
                    } catch (NumberFormatException ignored) {}
                }
            } else {
                ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.RED + "Invalid Coordinates (requires X, Y, Z)");
                return;
            }
        }
        try {
            y = Integer.parseInt(astring[1]);
        } catch (NumberFormatException e) {
            if (astring[1].startsWith("~")) {
                y = (int) player.posY;
                if (astring[1].length() > 1) {
                    int addval;
                    try {
                        addval = Integer.parseInt(astring[1].substring(1));
                        y += addval;
                    } catch (NumberFormatException ignored) {}
                }
            } else {
                ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.RED + "Invalid Coordinates (requires X, Y, Z)");
                return;
            }
        }
        try {
            z = Integer.parseInt(astring[2]);
        } catch (NumberFormatException e) {
            if (astring[2].startsWith("~")) {
                z = (int) player.posZ;
                if (astring[2].length() > 1) {
                    int addval = 0;
                    try {
                        addval = Integer.parseInt(astring[2].substring(1));
                        z += addval;
                    } catch (NumberFormatException ignored) {}
                }
            } else {
                ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.RED + "Invalid Coordinates (requires X, Y, Z)");
                return;
            }
        }
        player.worldObj.addWeatherEffect(new EntityLightningBolt(player.worldObj, x, y, z));
        ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.GOLD + "Smited " + x + ", " + y + ", " + z);
        ChatHelper.sendAdminMessage(iCommandSender, "Struck " + x + ", " + y + ", " + z + " with lightning");
    }

    @Override
    public List addTabCompletionOptions(ICommandSender iCommandSender, String[] typedValue) {
        if (typedValue.length == 1)
            return CommandBase.getListOfStringsMatchingLastWord(typedValue, MinecraftServer.getServer().getAllUsernames());
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
