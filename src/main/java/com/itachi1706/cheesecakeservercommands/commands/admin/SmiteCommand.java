package com.itachi1706.cheesecakeservercommands.commands.admin;

import com.itachi1706.cheesecakeservercommands.commands.BaseCommand;
import com.itachi1706.cheesecakeservercommands.util.ServerPlayerUtil;
import com.itachi1706.cheesecakeservercommands.util.TextUtil;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class SmiteCommand extends BaseCommand {
    public SmiteCommand(String name, int permissionLevel, boolean enabled) {
        super(name, permissionLevel, enabled);
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution() {
        return builder.executes(context -> smiteLookingAt(context.getSource()))
                .then(Commands.literal("me").executes(context -> smiteYourself(context.getSource())))
                .then(Commands.literal("player")
                        .then(Commands.argument("victim", EntityArgument.player())
                                .executes(context -> smitePlayer(context.getSource(), EntityArgument.getPlayer(context, "victim"), false))))
                .then(Commands.literal("location")
                        .then(Commands.argument("vectors", Vec3Argument.vec3()).executes(context -> smiteCoordinates(context.getSource(), Vec3Argument.getVec3(context, "vectors")))));
    }

    private int smiteLookingAt(CommandSourceStack sender) {
        ServerPlayer player = ensureIsPlayer(sender, "Cannot smite CONSOLE", "Cannot smite " + sender.getTextName());
        if (player == null) {
            return 0; // Already sent message
        }

        HitResult pos = ServerPlayerUtil.getPlayerLookingSpot(player, false);
        if (pos == null || pos.getType() == HitResult.Type.MISS) {
            sendFailureMessage(sender, "Please make sure you are looking at a ground!");
            return 0;
        }

        if (!strikeLightning(player.getLevel(), pos.getLocation())) {
            sendFailureMessage(sender, "Cannot create lightning bolt");
            return 0;
        }

        sendSuccessMessage(sender, ChatFormatting.GOLD + "Struck the ground with lightning");
        String message = "Struct %d, %d, %d with lightning";
        TextUtil.sendAdminChatMessage(sender,  String.format(message, pos.getLocation().x(), pos.getLocation().y(), pos.getLocation().z()));

        return Command.SINGLE_SUCCESS;
    }

    private int smiteYourself(CommandSourceStack sender) {
        ServerPlayer player = ensureIsPlayer(sender, "Cannot smite CONSOLE", "Cannot smite " + sender.getTextName());
        if (player == null) {
            return 0; // Already sent message
        }

        return smitePlayer(sender, player, true);
    }

    private int smitePlayer(CommandSourceStack sender, ServerPlayer player, boolean yourself) {
        strikeLightning(player.getLevel(), player.getPosition(0));

        if (yourself) {
            sendSuccessMessage(sender, ChatFormatting.GOLD + "Struck yourself with lightning");
            TextUtil.sendAdminChatMessage(sender, "Struck own self with lightning");
        } else {
            sendSuccessMessage(sender, String.format("%sStruck %s with lightning", ChatFormatting.GOLD, player.getName().getString()));
            TextUtil.sendAdminChatMessage(sender, String.format("Struck %s with lightning", player.getName().getString()));
            sendMessage(player, "You were struck by lightning");
        }
        return Command.SINGLE_SUCCESS;
    }

    private int smiteCoordinates(CommandSourceStack sender, Vec3 location) {
        strikeLightning(sender.getLevel(), location);

        sendSuccessMessage(sender, ChatFormatting.GOLD + "Smited " + location.x + ", " + location.y + ", " + location.z);
        TextUtil.sendAdminChatMessage(sender, "Struck " + location.x + ", " + location.y + ", " + location.z + " with lightning");
        return Command.SINGLE_SUCCESS;
    }

    private boolean strikeLightning(Level world, Vec3 location) {
        LightningBolt kaboom = EntityType.LIGHTNING_BOLT.create(world);
        if (kaboom == null) {
            return false;
        }
        kaboom.moveTo(Vec3.atBottomCenterOf(new BlockPos(location)));
        kaboom.setVisualOnly(false);
        world.addFreshEntity(kaboom);
        return true;
    }
}
