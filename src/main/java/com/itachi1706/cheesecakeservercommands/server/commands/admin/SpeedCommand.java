package com.itachi1706.cheesecakeservercommands.server.commands.admin;

import com.itachi1706.cheesecakeservercommands.util.ChatHelper;
import com.itachi1706.cheesecakeservercommands.util.PlayerMPUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kenneth on 9/11/2015.
 * for CheesecakeServerCommands in package com.itachi1706.cheesecakeservercommands.server.commands
 */
@SuppressWarnings("unused")
public class SpeedCommand implements ICommand {

    private List<String> aliases;

    public static final float DEFAULT_FLY_SPEED = 0.05F;
    public static final float DEFAULT_WALK_SPEED = 0.1F;

    public static final String CAPABILITY_ABILITY = "abilities";
    public static final String CAPABILITY_FLY_SPEED = "flySpeed";
    public static final String CAPABILITY_WALK_SPEED = "walkSpeed";

    public SpeedCommand(){
        this.aliases = new ArrayList<String>();
        this.aliases.add("fly");
    }

    @Override
    public String getCommandName() {
        return "speed";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_) {
        return "speed <fly/walk/all> <speed> [player]";
    }

    @Override
    public List getCommandAliases() {
        return this.aliases;
    }

    @Override
    public void processCommand(ICommandSender iCommandSender, String[] astring) {

        if (astring.length < 2) {
            ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.RED + "Usage: /speed <fly/walk/all> <speed/reset> [player]");
            return;
        }
        String stat = astring[0];
        String speedStr = astring[1];
        if (!(stat.equalsIgnoreCase("fly") || stat.equalsIgnoreCase("walk") || stat.equalsIgnoreCase("all"))) {
            ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.RED + "Invalid speed modification. Available modification: fly,walk,all");
            return;
        }

        boolean modifyFly = false;
        boolean modifyWalk = false;
        if (stat.equalsIgnoreCase("fly") || stat.equalsIgnoreCase("all")) modifyFly = true;
        if (stat.equalsIgnoreCase("walk") || stat.equalsIgnoreCase("all")) modifyWalk = true;

        boolean resetSpeed = false;
        float speed = 0.05F;
        int multiplier = 0;
        if (speedStr.equalsIgnoreCase("reset")) {
            // Reset
            resetSpeed = true;
        } else {
            try {
                multiplier = Integer.parseInt(speedStr);
            } catch (NumberFormatException ex) {
                ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.RED + "Invalid speed. Available speed from 1 - 10");
                return;
            }
            if (multiplier > 10) {
                multiplier = 10;
            }
            if (multiplier < 1) {
                multiplier = 1;
            }
            speed *= multiplier;
        }

        if (astring.length == 2)
        {
            if (!PlayerMPUtil.isPlayer(iCommandSender)) {
                ChatHelper.sendMessage(iCommandSender, "Cannot set " + stat + " speed of CONSOLE");
                return;
            } else {
                EntityPlayerMP player = (EntityPlayerMP) PlayerMPUtil.castToPlayer(iCommandSender);
                if (player == null) {
                    ChatHelper.sendMessage(iCommandSender, "Cannot " + (resetSpeed ? "reset " : "set ") + iCommandSender.getCommandSenderName() + "'s " + stat + " speed");
                    return;
                }

                if (resetSpeed) {
                    // Reset
                    NBTTagCompound tagCompound = new NBTTagCompound();
                    player.capabilities.writeCapabilitiesToNBT(tagCompound);
                    if (modifyFly) {
                        tagCompound.getCompoundTag(CAPABILITY_ABILITY).setTag(CAPABILITY_FLY_SPEED, new NBTTagFloat(DEFAULT_FLY_SPEED));
                        ChatHelper.sendMessage(iCommandSender, "Fly Speed:" + EnumChatFormatting.AQUA + " Reset");
                        ChatHelper.sendAdminMessage(iCommandSender, "Reset own Fly Speed");
                    }
                    if (modifyWalk) {
                        tagCompound.getCompoundTag(CAPABILITY_ABILITY).setTag(CAPABILITY_WALK_SPEED, new NBTTagFloat(DEFAULT_WALK_SPEED));
                        ChatHelper.sendMessage(iCommandSender, "Walk Speed:" + EnumChatFormatting.AQUA + " Reset");
                        ChatHelper.sendAdminMessage(iCommandSender, "Reset own Walk Speed");
                    }
                    player.capabilities.readCapabilitiesFromNBT(tagCompound);
                    player.sendPlayerAbilities();
                    return;
                }

                NBTTagCompound tagCompound = new NBTTagCompound();
                player.capabilities.writeCapabilitiesToNBT(tagCompound);
                if (modifyFly) {
                    tagCompound.getCompoundTag(CAPABILITY_ABILITY).setTag(CAPABILITY_FLY_SPEED, new NBTTagFloat(speed));
                    ChatHelper.sendMessage(iCommandSender, "Fly Speed: " + EnumChatFormatting.AQUA + multiplier);
                    ChatHelper.sendAdminMessage(iCommandSender, "Set own Fly Speed to " + multiplier);
                }
                if (modifyWalk) {
                    tagCompound.getCompoundTag(CAPABILITY_ABILITY).setTag(CAPABILITY_WALK_SPEED, new NBTTagFloat(speed));
                    ChatHelper.sendMessage(iCommandSender, "Walk Speed: " + EnumChatFormatting.AQUA + multiplier);
                    ChatHelper.sendAdminMessage(iCommandSender, "Set own Walk Speed to " + multiplier);
                }
                player.capabilities.readCapabilitiesFromNBT(tagCompound);
                player.sendPlayerAbilities();
                return;
            }
        }

        String subname = astring[2];
        EntityPlayerMP player = PlayerMPUtil.getPlayer(subname);
        if (player == null) {
            ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.RED + "Player not found");
            return;
        }

        if (resetSpeed) {
            // Reset
            NBTTagCompound tagCompound = new NBTTagCompound();
            player.capabilities.writeCapabilitiesToNBT(tagCompound);
            if (modifyFly) {
                tagCompound.getCompoundTag(CAPABILITY_ABILITY).setTag(CAPABILITY_FLY_SPEED, new NBTTagFloat(DEFAULT_FLY_SPEED));
                ChatHelper.sendMessage(iCommandSender, "Reset " + player.getCommandSenderName() + " Fly Speed");
                ChatHelper.sendMessage(player, "Fly Speed:" + EnumChatFormatting.AQUA + " Reset");
                ChatHelper.sendAdminMessage(iCommandSender, "Reset " + player.getCommandSenderName() + " Fly Speed");
            }
            if (modifyWalk) {
                tagCompound.getCompoundTag(CAPABILITY_ABILITY).setTag(CAPABILITY_WALK_SPEED, new NBTTagFloat(DEFAULT_WALK_SPEED));
                ChatHelper.sendMessage(iCommandSender, "Reset " + player.getCommandSenderName() + " Walk Speed");
                ChatHelper.sendMessage(player, "Walk Speed:" + EnumChatFormatting.AQUA + " Reset");
                ChatHelper.sendAdminMessage(iCommandSender, "Reset " + player.getCommandSenderName() + " Walk Speed");
            }
            player.capabilities.readCapabilitiesFromNBT(tagCompound);
            player.sendPlayerAbilities();
            return;
        }

        NBTTagCompound tagCompound = new NBTTagCompound();
        player.capabilities.writeCapabilitiesToNBT(tagCompound);
        if (modifyFly) {
            tagCompound.getCompoundTag(CAPABILITY_ABILITY).setTag(CAPABILITY_FLY_SPEED, new NBTTagFloat(speed));
            ChatHelper.sendMessage(iCommandSender, "Set " + player.getCommandSenderName() + " Fly Speed to " + EnumChatFormatting.AQUA + multiplier);
            ChatHelper.sendMessage(player, "Fly Speed: " + EnumChatFormatting.AQUA + multiplier);
            ChatHelper.sendAdminMessage(iCommandSender, "Set " + player.getCommandSenderName() + " Fly Speed to " + multiplier);
        }
        if (modifyWalk) {
            tagCompound.getCompoundTag(CAPABILITY_ABILITY).setTag(CAPABILITY_WALK_SPEED, new NBTTagFloat(speed));
            ChatHelper.sendMessage(iCommandSender, "Set " + player.getCommandSenderName() + " Walk Speed to " + EnumChatFormatting.AQUA + multiplier);
            ChatHelper.sendMessage(player, "Walk Speed: " + EnumChatFormatting.AQUA + multiplier);
            ChatHelper.sendAdminMessage(iCommandSender, "Set " + player.getCommandSenderName() + " Walk Speed to " + multiplier);
        }
        player.capabilities.readCapabilitiesFromNBT(tagCompound);
        player.sendPlayerAbilities();
    }

    @Override
    public List addTabCompletionOptions(ICommandSender iCommandSender, String[] typedValue) {
        if (typedValue.length == 1)
            return CommandBase.getListOfStringsMatchingLastWord(typedValue, "fly", "walk", "all");
        if (typedValue[0].equals("fly") || typedValue[0].equals("walk") || typedValue[0].equals("all")) {
            if (typedValue.length == 2)
                return CommandBase.getListOfStringsMatchingLastWord(typedValue, "reset", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10");
            if (typedValue.length == 3)
                return CommandBase.getListOfStringsMatchingLastWord(typedValue, MinecraftServer.getServer().getAllUsernames());
        }
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
