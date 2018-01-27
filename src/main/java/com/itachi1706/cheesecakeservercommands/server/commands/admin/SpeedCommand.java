package com.itachi1706.cheesecakeservercommands.server.commands.admin;

import com.itachi1706.cheesecakeservercommands.util.ChatHelper;
import com.itachi1706.cheesecakeservercommands.util.PlayerMPUtil;
import com.itachi1706.cheesecakeservercommands.util.ServerUtil;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
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
public class SpeedCommand implements ICommand {

    private List<String> aliases;

    public static final float DEFAULT_FLY_SPEED = 0.05F;
    public static final float DEFAULT_WALK_SPEED = 0.1F;

    public static final String CAPABILITY_ABILITY = "abilities";
    public static final String CAPABILITY_FLY_SPEED = "flySpeed";
    public static final String CAPABILITY_WALK_SPEED = "walkSpeed";

    public SpeedCommand(){
        this.aliases = new ArrayList<>();
        this.aliases.add("speed");
    }

    @Override
    public String getName() {
        return "speed";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "speed <fly/walk/all> <speed> [player]";
    }

    @Override
    public List getAliases() {
        return this.aliases;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender iCommandSender, String[] args) throws CommandException {

        if (args.length < 2) {
            ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "Usage: /speed <fly/walk/all> <speed/reset> [player]");
            return;
        }
        String stat = args[0];
        String speedStr = args[1];
        if (!(stat.equalsIgnoreCase("fly") || stat.equalsIgnoreCase("walk") || stat.equalsIgnoreCase("all"))) {
            ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "Invalid speed modification. Available modification: fly,walk,all");
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
            multiplier = CommandBase.parseInt(speedStr, 1, 10);
            if (multiplier > 10) {
                multiplier = 10;
            }
            if (multiplier < 1) {
                multiplier = 1;
            }
            speed *= multiplier;
        }

        if (args.length == 2)
        {
            if (!PlayerMPUtil.isPlayer(iCommandSender)) {
                ChatHelper.sendMessage(iCommandSender, "Cannot set " + stat + " speed of CONSOLE");
                return;
            } else {
                EntityPlayerMP player = (EntityPlayerMP) PlayerMPUtil.castToPlayer(iCommandSender);
                if (player == null) {
                    ChatHelper.sendMessage(iCommandSender, "Cannot " + (resetSpeed ? "reset " : "set ") + iCommandSender.getName() + "'s " + stat + " speed");
                    return;
                }

                if (resetSpeed) {
                    // Reset
                    NBTTagCompound tagCompound = new NBTTagCompound();
                    player.capabilities.writeCapabilitiesToNBT(tagCompound);
                    if (modifyFly) {
                        tagCompound.getCompoundTag(CAPABILITY_ABILITY).setTag(CAPABILITY_FLY_SPEED, new NBTTagFloat(DEFAULT_FLY_SPEED));
                        ChatHelper.sendMessage(iCommandSender, "Fly Speed:" + TextFormatting.AQUA + " Reset");
                        ChatHelper.sendAdminMessage(iCommandSender, "Reset own Fly Speed");
                    }
                    if (modifyWalk) {
                        tagCompound.getCompoundTag(CAPABILITY_ABILITY).setTag(CAPABILITY_WALK_SPEED, new NBTTagFloat(DEFAULT_WALK_SPEED));
                        ChatHelper.sendMessage(iCommandSender, "Walk Speed:" + TextFormatting.AQUA + " Reset");
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
                    ChatHelper.sendMessage(iCommandSender, "Fly Speed: " + TextFormatting.AQUA + multiplier);
                    ChatHelper.sendAdminMessage(iCommandSender, "Set own Fly Speed to " + multiplier);
                }
                if (modifyWalk) {
                    tagCompound.getCompoundTag(CAPABILITY_ABILITY).setTag(CAPABILITY_WALK_SPEED, new NBTTagFloat(speed));
                    ChatHelper.sendMessage(iCommandSender, "Walk Speed: " + TextFormatting.AQUA + multiplier);
                    ChatHelper.sendAdminMessage(iCommandSender, "Set own Walk Speed to " + multiplier);
                }
                player.capabilities.readCapabilitiesFromNBT(tagCompound);
                player.sendPlayerAbilities();
                return;
            }
        }

        String subname = args[2];
        EntityPlayerMP player = PlayerMPUtil.getPlayer(subname);
        if (player == null) {
            ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "Player not found");
            return;
        }

        if (resetSpeed) {
            // Reset
            NBTTagCompound tagCompound = new NBTTagCompound();
            player.capabilities.writeCapabilitiesToNBT(tagCompound);
            if (modifyFly) {
                tagCompound.getCompoundTag(CAPABILITY_ABILITY).setTag(CAPABILITY_FLY_SPEED, new NBTTagFloat(DEFAULT_FLY_SPEED));
                ChatHelper.sendMessage(iCommandSender, "Reset " + player.getName() + " Fly Speed");
                ChatHelper.sendMessage(player, "Fly Speed:" + TextFormatting.AQUA + " Reset");
                ChatHelper.sendAdminMessage(iCommandSender, "Reset " + player.getName() + " Fly Speed");
            }
            if (modifyWalk) {
                tagCompound.getCompoundTag(CAPABILITY_ABILITY).setTag(CAPABILITY_WALK_SPEED, new NBTTagFloat(DEFAULT_WALK_SPEED));
                ChatHelper.sendMessage(iCommandSender, "Reset " + player.getName() + " Walk Speed");
                ChatHelper.sendMessage(player, "Walk Speed:" + TextFormatting.AQUA + " Reset");
                ChatHelper.sendAdminMessage(iCommandSender, "Reset " + player.getName() + " Walk Speed");
            }
            player.capabilities.readCapabilitiesFromNBT(tagCompound);
            player.sendPlayerAbilities();
            return;
        }

        NBTTagCompound tagCompound = new NBTTagCompound();
        player.capabilities.writeCapabilitiesToNBT(tagCompound);
        if (modifyFly) {
            tagCompound.getCompoundTag(CAPABILITY_ABILITY).setTag(CAPABILITY_FLY_SPEED, new NBTTagFloat(speed));
            ChatHelper.sendMessage(iCommandSender, "Set " + player.getName() + " Fly Speed to " + TextFormatting.AQUA + multiplier);
            ChatHelper.sendMessage(player, "Fly Speed: " + TextFormatting.AQUA + multiplier);
            ChatHelper.sendAdminMessage(iCommandSender, "Set " + player.getName() + " Fly Speed to " + multiplier);
        }
        if (modifyWalk) {
            tagCompound.getCompoundTag(CAPABILITY_ABILITY).setTag(CAPABILITY_WALK_SPEED, new NBTTagFloat(speed));
            ChatHelper.sendMessage(iCommandSender, "Set " + player.getName() + " Walk Speed to " + TextFormatting.AQUA + multiplier);
            ChatHelper.sendMessage(player, "Walk Speed: " + TextFormatting.AQUA + multiplier);
            ChatHelper.sendAdminMessage(iCommandSender, "Set " + player.getName() + " Walk Speed to " + multiplier);
        }
        player.capabilities.readCapabilitiesFromNBT(tagCompound);
        player.sendPlayerAbilities();
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1)
            return CommandBase.getListOfStringsMatchingLastWord(args, "fly", "walk", "all");
        if (args[0].equals("fly") || args[0].equals("walk") || args[0].equals("all")) {
            if (args.length == 2)
                return CommandBase.getListOfStringsMatchingLastWord(args, "reset", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10");
            if (args.length == 3)
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
