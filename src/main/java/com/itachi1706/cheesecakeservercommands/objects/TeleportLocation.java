package com.itachi1706.cheesecakeservercommands.objects;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;

public record TeleportLocation(BlockPos position, ChunkPos chunkPos, float yRot, float xRot, ServerLevel level) {

    public TeleportLocation(BlockPos position, ChunkPos chunkPos, float yRot, float xRot, ServerLevel level) {
        this.position = position;
        this.chunkPos = chunkPos;
        this.yRot = Mth.wrapDegrees(yRot);
        this.xRot = Mth.wrapDegrees(xRot);
        this.level = level;
    }
}
