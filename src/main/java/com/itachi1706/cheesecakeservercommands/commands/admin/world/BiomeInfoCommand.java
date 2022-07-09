package com.itachi1706.cheesecakeservercommands.commands.admin.world;

import com.itachi1706.cheesecakeservercommands.commands.BaseCommand;
import com.itachi1706.cheesecakeservercommands.util.ServerUtil;
import com.itachi1706.cheesecakeservercommands.util.TextUtil;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.Vec3;

public class BiomeInfoCommand extends BaseCommand {
    public BiomeInfoCommand(String name, int permissionLevel, boolean enabled) {
        super(name, permissionLevel, enabled);
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution() {
        return builder.executes(context -> getCurrentBiomeSelf(context.getSource()))
                .then(Commands.literal("list").executes(context -> listAllBiomes(context.getSource())))
                .then(Commands.literal("find")
                        .then(Commands.argument("players", EntityArgument.player())
                                .executes(context -> getCurrentBiomeOthers(context.getSource(), EntityArgument.getPlayer(context, "players")))));
    }

    private int getCurrentBiomeSelf(CommandSourceStack sender) {
        ServerPlayer player = ensureIsPlayer(sender, "Cannot find current biome for CONSOLE", "Cannot find current biome for " + sender.getTextName());
        if (player == null) {
            return 0; // Already sent message
        }

        Vec3 vec = getPlayerLocation(player);
        Holder<Biome> holder = player.getLevel().getBiome(new BlockPos(vec));

        sendSuccessMessage(sender, ChatFormatting.GOLD + "Current Biome: " + ChatFormatting.AQUA + printBiome(holder));

        return Command.SINGLE_SUCCESS;
    }

    private String printBiome(Holder<Biome> biome) {
        return biome.unwrap().map(b -> b.location().toString(), e -> "[unregistered " + e + "]");
    }

    private Vec3 getPlayerLocation(ServerPlayer player) {
        int x = (int) Math.floor(player.getX());
        int y = (int) Math.floor(player.getY());
        int z = (int) Math.floor(player.getZ());

        return new Vec3(x,y,z);
    }

    private int getCurrentBiomeOthers(CommandSourceStack sender, ServerPlayer player) {
        Vec3 vec = getPlayerLocation(player);
        Holder<Biome> holder = player.getLevel().getBiome(new BlockPos(vec));

        sendSuccessMessage(sender, ChatFormatting.GOLD + "Current Biome for " + player.getName().getString() + ": " + ChatFormatting.AQUA + printBiome(holder));
        TextUtil.sendAdminChatMessage(sender, "Located biome of " + player.getName().getString());

        return Command.SINGLE_SUCCESS;
    }

    private int listAllBiomes(CommandSourceStack sender) {
        sendSuccessMessage(sender, ChatFormatting.GOLD + "Registered Biome: ");
        boolean skip = false;
        int i = 0;
        for (Registry<Biome> biome : ServerUtil.getServerInstance().registryAccess().registry(Registry.BIOME_REGISTRY).stream().toList()) {
            for (ResourceLocation rsl : biome.keySet().stream().toList()) {
                if (rsl == null) {
                    skip = true;
                    continue;
                }
                if (skip) {
                    skip = false;
                    sendSuccessMessage(sender, ChatFormatting.GOLD + "----");
                }
                sendSuccessMessage(sender, ChatFormatting.GOLD + "#" + i + ": " + ChatFormatting.AQUA + rsl);
                i++;
            }
        }
        return Command.SINGLE_SUCCESS;
    }
}
