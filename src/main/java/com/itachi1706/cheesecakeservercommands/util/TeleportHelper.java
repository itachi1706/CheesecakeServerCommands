package com.itachi1706.cheesecakeservercommands.util;

import com.itachi1706.cheesecakeservercommands.objects.TeleportLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.ChunkPos;

public class TeleportHelper {

    private TeleportHelper() {
        throw new IllegalStateException("Utility class");
    }

    public static void teleportPlayer(ServerPlayer player, ServerLevel level, BlockPos position, ChunkPos chunkPos, float yRot, float xRot) {
        level.getChunkSource().addRegionTicket(TicketType.POST_TELEPORT, chunkPos, 1, player.getId());
        player.stopRiding();
        if (player.isSleeping())
            player.stopSleepInBed(true, true);

        if (level == player.level) {
            player.connection.teleport(position.getX(), position.getY(), position.getZ(), yRot, xRot);
        } else {
            player.teleportTo(level, position.getX(), position.getY(), position.getZ(), yRot, xRot);
        }

        player.setYHeadRot(yRot);
    }

    public static TeleportLocation getTeleportLocation(ServerPlayer player) {
        BlockPos position = new BlockPos(player.getX(), player.getY(), player.getZ());
        ServerLevel level = player.getLevel();
        float yRot = player.getYRot();
        float xRot = player.getXRot();

        return new TeleportLocation(position, new ChunkPos(position), yRot, xRot, level);
    }
}
