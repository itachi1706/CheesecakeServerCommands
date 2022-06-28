package com.itachi1706.cheesecakeservercommands.util;

import com.itachi1706.cheesecakeservercommands.commons.selections.WarpPoint;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.network.play.server.SPacketRespawn;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Kenneth on 3/5/2016.
 * for com.itachi1706.cheesecakeservercommands.util in CheesecakeServerCommands
 */
public class TeleportHelper {

    public static class SimpleTeleporter extends Teleporter
    {

        public SimpleTeleporter(WorldServer world)
        {
            super(world);
        }

        @Override
        public boolean placeInExistingPortal(Entity entity, float rotationYaw)
        {
            entity.setLocationAndAngles(entity.posX, entity.posY, entity.posZ, rotationYaw, entity.rotationPitch);
            return true;
        }

        @Override
        public void removeStalePortalLocations(long totalWorldTime)
        {
            /* do nothing */
        }

        @Override
        public void placeInPortal(Entity entityIn, float rotationYaw) {
            placeInExistingPortal(entityIn, rotationYaw);
        }

    }

    public static class TeleportInfo
    {

        private EntityPlayerMP player;

        private long start;

        private int timeout;

        private WarpPoint point;

        private WarpPoint playerPos;

        public TeleportInfo(EntityPlayerMP player, WarpPoint point, int timeout)
        {
            this.point = point;
            this.timeout = timeout;
            this.start = System.currentTimeMillis();
            this.player = player;
            this.playerPos = new WarpPoint(player);
        }

        public boolean check()
        {
            if (playerPos.distance(new WarpPoint(player)) > 0.2)
            {
                ChatHelper.sendMessage(player, TextFormatting.RED + "Teleport cancelled");
                return true;
            }
            if (System.currentTimeMillis() - start < timeout)
            {
                return false;
            }
            checkedTeleport(player, point);
            ChatHelper.sendMessage(player, TextFormatting.GREEN + "Teleported");
            return true;
        }

    }

    public static final String TELEPORT_COOLDOWN = "fe.teleport.cooldown";
    public static final String TELEPORT_WARMUP = "fe.teleport.warmup";
    public static final String TELEPORT_CROSSDIM = "fe.teleport.crossdim";
    public static final String TELEPORT_FROM = "fe.teleport.from";
    public static final String TELEPORT_TO = "fe.teleport.to";

    private static Map<UUID, TeleportInfo> tpInfos = new HashMap<>();

    public static void teleport(EntityPlayerMP player, WarpPoint point)
    {
        if (point.getWorld() == null)
        {
            DimensionManager.initDimension(point.getDimension());
            if (point.getWorld() == null)
            {
                ChatHelper.sendMessage(player, TextFormatting.RED + "Unable to teleport! Target dimension does not exist");
                return;
            }
        }


        if (!canTeleportTo(point))
        {
            ChatHelper.sendMessage(player, TextFormatting.RED + "Unable to teleport! Target location obstructed.");
            return;
        }

        // Setup timed teleport
        tpInfos.put(player.getPersistentID(), new TeleportInfo(player, point, 0));
        //ChatHelper.sendMessage(player, TextFormatting.YELLOW + "Teleporting...");
    }

    public static boolean canTeleportTo(WarpPoint point)
    {
        if (point.getY() < 0)
            return false;
        IBlockState blockS1 = point.getWorld().getBlockState(new BlockPos(point.getBlockX(), point.getBlockY(), point.getBlockZ()));
        IBlockState blockS2 = point.getWorld().getBlockState(new BlockPos(point.getBlockX(), point.getBlockY() + 1, point.getBlockZ()));

        Block block1 = blockS1.getBlock();
        Block block2 = blockS2.getBlock();
        // TODO: Figure out how to do this, returning true for now
        //boolean block1Free = !block1.getMaterial(blockS1).isSolid() || block1.getBlockBoundsMaxX() < 1 || block1.getBlockBoundsMaxY() > 0;
        //boolean block2Free = !block2.getMaterial(blockS2).isSolid() || block2.getBlockBoundsMaxX() < 1 || block2.getBlockBoundsMaxY() > 0;
        //return block1Free && block2Free;
        return true;
    }

    public static void checkedTeleport(EntityPlayerMP player, WarpPoint point)
    {
        if (!canTeleportTo(point))
        {
            ChatHelper.sendMessage(player, TextFormatting.RED + "Unable to teleport! Target location obstructed.");
            return;
        }

        doTeleport(player, point);
    }

    public static void doTeleport(EntityPlayerMP player, WarpPoint point)
    {
        if (point.getWorld() == null)
        {
            LogHelper.error("Error teleporting player. Target world is NULL");
            return;
        }
        // TODO: Handle teleportation of mounted entity
        player.mountEntityAndWakeUp();

        if (player.dimension != point.getDimension())
        {
            SimpleTeleporter teleporter = new SimpleTeleporter(point.getWorld());
            transferPlayerToDimension(player, point.getDimension(), teleporter);
        }
        player.connection.setPlayerLocation(point.getX(), point.getY(), point.getZ(), point.getYaw(), point.getPitch());
    }

    public static void doTeleportEntity(Entity entity, WarpPoint point)
    {
        if (entity instanceof EntityPlayerMP)
        {
            doTeleport((EntityPlayerMP) entity, point);
            return;
        }
        if (entity.dimension != point.getDimension())
            entity.changeDimension(point.getDimension());
        entity.setLocationAndAngles(point.getX(), point.getY(), point.getZ(), point.getYaw(), point.getPitch());
    }

    @SubscribeEvent
    public void serverTickEvent(TickEvent.ServerTickEvent e)
    {
        if (e.phase == TickEvent.Phase.START)
        {
            tpInfos.values().removeIf(TeleportInfo::check);
        }
    }

    public static void transferPlayerToDimension(EntityPlayerMP player, int dimension, Teleporter teleporter)
    {
        int oldDim = player.dimension;
        MinecraftServer mcServer = ServerUtil.getServerInstance();

        WorldServer oldWorld = mcServer.getWorld(player.dimension);
        player.dimension = dimension;
        WorldServer newWorld = mcServer.getWorld(player.dimension);
        player.connection.sendPacket(new SPacketRespawn(player.dimension, newWorld.getDifficulty(),
                newWorld.getWorldInfo().getTerrainType(), player.interactionManager.getGameType())); // Forge: Use new dimensions information
        oldWorld.removeEntityDangerously(player);
        player.isDead = false;

        transferEntityToWorld(player, oldDim, oldWorld, newWorld, teleporter);

        mcServer.getPlayerList().preparePlayer(player, oldWorld);
        player.connection.setPlayerLocation(player.posX, player.posY, player.posZ, player.rotationYaw,
                player.rotationPitch);
        player.interactionManager.setWorld(newWorld);
        mcServer.getPlayerList().updateTimeAndWeatherForPlayer(player, newWorld);
        mcServer.getPlayerList().syncPlayerInventory(player);
        for (PotionEffect potioneffect : player.getActivePotionEffects()) {
            player.connection.sendPacket(new SPacketEntityEffect(player.getEntityId(), potioneffect));
        }
        FMLCommonHandler.instance().firePlayerChangedDimensionEvent(player, oldDim, dimension);
    }

    public static void transferEntityToWorld(Entity entity, int oldDim, WorldServer oldWorld, WorldServer newWorld, Teleporter teleporter)
    {
        WorldProvider pOld = oldWorld.provider;
        WorldProvider pNew = newWorld.provider;
        double moveFactor = pOld.getMovementFactor() / pNew.getMovementFactor();
        double d0 = entity.posX * moveFactor;
        double d1 = entity.posZ * moveFactor;
        double d3 = entity.posX;
        double d4 = entity.posY;
        double d5 = entity.posZ;
        float f = entity.rotationYaw;
        d0 = MathHelper.clamp((int) d0, -29999872, 29999872);
        d1 = MathHelper.clamp((int) d1, -29999872, 29999872);
        if (entity.isEntityAlive())
        {
            entity.setLocationAndAngles(d0, entity.posY, d1, entity.rotationYaw, entity.rotationPitch);
            teleporter.placeInPortal(entity, f);
            newWorld.spawnEntity(entity);
            newWorld.updateEntityWithOptionalForce(entity, false);
        }
        entity.setWorld(newWorld);
    }

}
