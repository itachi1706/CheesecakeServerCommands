package com.itachi1706.cheesecakeservercommands.commands.admin;

import com.itachi1706.cheesecakeservercommands.commands.BaseCommand;
import com.itachi1706.cheesecakeservercommands.reference.References;
import com.itachi1706.cheesecakeservercommands.util.ServerPlayerUtil;
import com.itachi1706.cheesecakeservercommands.util.TextUtil;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.UUID;

public class SpeedCommand extends BaseCommand {

    private static final float DEFAULT_FLY_SPEED = 0.05F;
    private static final float DEFAULT_WALK_SPEED = 0.1F;
    
    private static final String ARG_PLAYER = "player";
    private static final String ARG_SPEED = "speed";
    private static final String ARG_RESET = "reset";
    private static final String ARG_SET = "set";

    private static final String MSG_FLY_SPEED = "Fly Speed: ";
    private static final String MSG_WALK_SPEED = "Walk Speed: ";
    private static final String MSG_ALL_SPEED = "Fly and Walk Speed: ";
    private static final String MSG_ERR_SET_WALK_SPEED = "An error occurred trying to set walk speed";
    private static final String MSG_RESET_TAIL = "Reset";
    private static final String MSG_RESET_HEAD = "Reset ";
    
    public SpeedCommand(String name, int permissionLevel, boolean enabled) {
        super(name, permissionLevel, enabled);
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution() {
        return builder
                .then(Commands.literal("fly")
                        .then(Commands.literal(ARG_RESET)
                                .executes(context -> resetFlySpeedSelf(context.getSource()))
                                .then(Commands.argument(ARG_PLAYER, EntityArgument.player())
                                        .executes(context -> resetFlySpeedOthers(context.getSource(), EntityArgument.getPlayer(context, ARG_PLAYER)))))
                        .then(Commands.literal(ARG_SET)
                                .then(Commands.argument(ARG_SPEED, IntegerArgumentType.integer(1, 10))
                                .executes(context -> changeFlySpeedSelf(context.getSource(), IntegerArgumentType.getInteger(context, ARG_SPEED)))
                                        .then(Commands.argument(ARG_PLAYER, EntityArgument.player())
                                                .executes(context -> changeFlySpeedOthers(context.getSource(), IntegerArgumentType.getInteger(context, ARG_SPEED), EntityArgument.getPlayer(context, ARG_PLAYER))))))
                )
                .then(Commands.literal("walk")
                        .then(Commands.literal(ARG_RESET)
                                .executes(context -> resetWalkSpeedSelf(context.getSource()))
                                .then(Commands.argument(ARG_PLAYER, EntityArgument.player())
                                        .executes(context -> resetWalkSpeedOthers(context.getSource(), EntityArgument.getPlayer(context, ARG_PLAYER)))))
                        .then(Commands.literal(ARG_SET)
                                .then(Commands.argument(ARG_SPEED, IntegerArgumentType.integer(1, 10))
                                .executes(context -> changeWalkSpeedSelf(context.getSource(), IntegerArgumentType.getInteger(context, ARG_SPEED)))
                                        .then(Commands.argument(ARG_PLAYER, EntityArgument.player())
                                                .executes(context -> changeWalkSpeedOthers(context.getSource(), IntegerArgumentType.getInteger(context, ARG_SPEED), EntityArgument.getPlayer(context, ARG_PLAYER))))))
                )
                .then(Commands.literal("all")
                        .then(Commands.literal(ARG_RESET)
                                .executes(context -> resetAllSpeedsSelf(context.getSource()))
                                .then(Commands.argument(ARG_PLAYER, EntityArgument.player())
                                        .executes(context -> resetAllSpeedsOthers(context.getSource(), EntityArgument.getPlayer(context, ARG_PLAYER)))))
                        .then(Commands.literal(ARG_SET)
                                .then(Commands.argument(ARG_SPEED, IntegerArgumentType.integer(1, 10))
                                        .executes(context -> changeAllSpeedsSelf(context.getSource(), IntegerArgumentType.getInteger(context, ARG_SPEED)))
                                        .then(Commands.argument(ARG_PLAYER, EntityArgument.player())
                                                .executes(context -> changeAllSpeedsOthers(context.getSource(), IntegerArgumentType.getInteger(context, ARG_SPEED), EntityArgument.getPlayer(context, ARG_PLAYER))))))
                );
    }

    private int changeFlySpeedSelf(CommandSourceStack sender, int speed) {
        ServerPlayer player = ServerPlayerUtil.castToPlayer(sender);
        if (player == null) {
            sendFailureMessage(sender, "Cannot change fly speed of CONSOLE");
            return 0;
        }

        setFlySpeed(player, speed);
        sendSuccessMessage(sender, MSG_FLY_SPEED + ChatFormatting.AQUA + speed);
        TextUtil.sendAdminChatMessage(sender, "Set own Fly Speed to " + speed);

        return Command.SINGLE_SUCCESS;
    }

    private int resetFlySpeedSelf(CommandSourceStack sender) {
        ServerPlayer player = ServerPlayerUtil.castToPlayer(sender);
        if (player == null) {
            sendFailureMessage(sender, "Cannot reset fly speed of CONSOLE");
            return 0;
        }

        setFlySpeed(player, -1);

        sendSuccessMessage(sender, MSG_FLY_SPEED + ChatFormatting.AQUA + MSG_RESET_TAIL);
        TextUtil.sendAdminChatMessage(sender, "Reset own Fly Speed");

        return Command.SINGLE_SUCCESS;
    }

    private int changeWalkSpeedSelf(CommandSourceStack sender, int speed) {
        ServerPlayer player = ServerPlayerUtil.castToPlayer(sender);
        if (player == null) {
            sendFailureMessage(sender, "Cannot change walk speed of CONSOLE");
            return 0;
        }

        boolean res = setWalkSpeed(player, speed);
        if (!res) sendFailureMessage(sender, MSG_ERR_SET_WALK_SPEED);
        sendSuccessMessage(sender, MSG_WALK_SPEED + ChatFormatting.AQUA + speed);
        TextUtil.sendAdminChatMessage(sender, "Set own Walk Speed to " + speed);

        return Command.SINGLE_SUCCESS;
    }

    private int resetWalkSpeedSelf(CommandSourceStack sender) {
        ServerPlayer player = ServerPlayerUtil.castToPlayer(sender);
        if (player == null) {
            sendFailureMessage(sender, "Cannot reset walk speed of CONSOLE");
            return 0;
        }

        boolean res = setWalkSpeed(player, -1);
        if (!res) sendFailureMessage(sender, MSG_ERR_SET_WALK_SPEED);

        sendSuccessMessage(sender, MSG_WALK_SPEED + ChatFormatting.AQUA + MSG_RESET_TAIL);
        TextUtil.sendAdminChatMessage(sender, "Reset own Walk Speed");

        return Command.SINGLE_SUCCESS;
    }

    private int changeAllSpeedsSelf(CommandSourceStack sender, int speed) {
        ServerPlayer player = ServerPlayerUtil.castToPlayer(sender);
        if (player == null) {
            sendFailureMessage(sender, "Cannot change speed of CONSOLE");
            return 0;
        }

        setFlySpeed(player, speed);
        boolean res = setWalkSpeed(player, speed);
        if (!res) sendFailureMessage(sender, MSG_ERR_SET_WALK_SPEED);
        sendSuccessMessage(sender, MSG_ALL_SPEED + ChatFormatting.AQUA + speed);
        TextUtil.sendAdminChatMessage(sender, "Set own Fly and Walk Speed to " + speed);

        return Command.SINGLE_SUCCESS;
    }

    private int resetAllSpeedsSelf(CommandSourceStack sender) {
        ServerPlayer player = ServerPlayerUtil.castToPlayer(sender);
        if (player == null) {
            sendFailureMessage(sender, "Cannot reset speed of CONSOLE");
            return 0;
        }

        setFlySpeed(player, -1);
        boolean res = setWalkSpeed(player, -1);
        if (!res) sendFailureMessage(sender, MSG_ERR_SET_WALK_SPEED);

        sendSuccessMessage(sender, MSG_ALL_SPEED + ChatFormatting.AQUA + MSG_RESET_TAIL);
        TextUtil.sendAdminChatMessage(sender, "Reset own Fly and Walk Speed");

        return Command.SINGLE_SUCCESS;
    }
    
    private int changeFlySpeedOthers(CommandSourceStack sender, int speed, ServerPlayer player) {
        setFlySpeed(player, speed);

        sendSuccessMessage(sender, "Set " + player.getName().getString() + " Fly Speed to " + ChatFormatting.AQUA + speed);
        sendMessage(player, MSG_FLY_SPEED + ChatFormatting.AQUA + speed);
        TextUtil.sendAdminChatMessage(sender, "Set " + player.getName().getString() + " Fly Speed to " + speed);
        
        return Command.SINGLE_SUCCESS;
    }
    
    private int resetFlySpeedOthers(CommandSourceStack sender, ServerPlayer player) {
        setFlySpeed(player, -1);

        sendSuccessMessage(sender, MSG_RESET_HEAD + player.getName().getString() + " Fly Speed");
        sendMessage(player, MSG_FLY_SPEED + ChatFormatting.AQUA + MSG_RESET_TAIL);
        TextUtil.sendAdminChatMessage(sender, MSG_RESET_HEAD + player.getName().getString() + " Fly Speed");

        return Command.SINGLE_SUCCESS;
    }
    
    private int changeWalkSpeedOthers(CommandSourceStack sender, int speed, ServerPlayer player) {
        boolean res = setWalkSpeed(player, speed);
        if (!res) sendFailureMessage(sender, MSG_ERR_SET_WALK_SPEED);

        sendSuccessMessage(sender, "Set " + player.getName().getString() + " Walk Speed to " + ChatFormatting.AQUA + speed);
        sendMessage(player, MSG_WALK_SPEED + ChatFormatting.AQUA + speed);
        TextUtil.sendAdminChatMessage(sender, "Set " + player.getName().getString() + " Walk Speed to " + speed);

        return Command.SINGLE_SUCCESS;
    }
    
    private int resetWalkSpeedOthers(CommandSourceStack sender, ServerPlayer player) {
        boolean res = setWalkSpeed(player, -1);
        if (!res) sendFailureMessage(sender, MSG_ERR_SET_WALK_SPEED);

        sendSuccessMessage(sender, MSG_RESET_HEAD + player.getName().getString() + " Walk Speed");
        sendMessage(player, MSG_WALK_SPEED + ChatFormatting.AQUA + MSG_RESET_TAIL);
        TextUtil.sendAdminChatMessage(sender, MSG_RESET_HEAD + player.getName().getString() + " Walk Speed");

        return Command.SINGLE_SUCCESS;
    }
    
    private int changeAllSpeedsOthers(CommandSourceStack sender, int speed, ServerPlayer player) {
        setFlySpeed(player, speed);
        boolean res = setWalkSpeed(player, speed);
        if (!res) sendFailureMessage(sender, MSG_ERR_SET_WALK_SPEED);

        sendSuccessMessage(sender, "Set " + player.getName().getString() + " Fly and Walk Speed to " + ChatFormatting.AQUA + speed);
        sendMessage(player, MSG_ALL_SPEED + ChatFormatting.AQUA + speed);
        TextUtil.sendAdminChatMessage(sender, "Set " + player.getName().getString() + " Fly and Walk Speed to " + speed);

        return Command.SINGLE_SUCCESS;
    }
    
    private int resetAllSpeedsOthers(CommandSourceStack sender, ServerPlayer player) {
        setFlySpeed(player, -1);
        boolean res = setWalkSpeed(player, -1);
        if (!res) sendFailureMessage(sender, MSG_ERR_SET_WALK_SPEED);

        sendSuccessMessage(sender, MSG_RESET_HEAD + player.getName().getString() + " Fly and Walk Speed");
        sendMessage(player, MSG_ALL_SPEED + ChatFormatting.AQUA + MSG_RESET_TAIL);
        TextUtil.sendAdminChatMessage(sender, MSG_RESET_HEAD + player.getName().getString() + " Fly and Walk Speed");

        return Command.SINGLE_SUCCESS;
    }
    
    private void setFlySpeed(ServerPlayer player, int multiplier) {
        if (multiplier == -1) {
            // Reset
            player.getAbilities().setFlyingSpeed(DEFAULT_FLY_SPEED);
        } else {
            float newSpeed = getFlySpeedMultiplier(multiplier);
            player.getAbilities().setFlyingSpeed(newSpeed);
        }
        player.onUpdateAbilities();
    }
    
    private boolean setWalkSpeed(ServerPlayer player, int multiplier) {
        String movementSpeedCustom = References.MOD_ID + "_movement_speed";
        UUID uuid = UUID.nameUUIDFromBytes(movementSpeedCustom.getBytes());
        AttributeInstance speedInstance = player.getAttributes().getInstance(Attributes.MOVEMENT_SPEED);
        if (speedInstance == null) {
            return false;
        }

        if (multiplier == -1) {
            // Reset
            speedInstance.removeModifier(uuid); // Remove any existing one
        } else {
            float newSpeed = getWalkSpeedMultiplier(multiplier);
            // Remove any old one
            speedInstance.removeModifier(uuid); // Remove any existing one
            speedInstance.addTransientModifier(new AttributeModifier(uuid, Attributes.MOVEMENT_SPEED.getDescriptionId(), newSpeed, AttributeModifier.Operation.MULTIPLY_TOTAL));
        }
        return true;
    }

    private float getFlySpeedMultiplier(int multiplier) {
        return DEFAULT_FLY_SPEED * multiplier;
    }

    private float getWalkSpeedMultiplier(int multiplier) {
        return DEFAULT_WALK_SPEED * multiplier;
    }
}
