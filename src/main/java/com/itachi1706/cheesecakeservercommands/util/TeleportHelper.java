package com.itachi1706.cheesecakeservercommands.util;

import com.itachi1706.cheesecakeservercommands.commons.selections.WarpPoint;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.HashMap;
import java.util.Iterator;
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
        public boolean placeInExistingPortal(Entity entity, double x, double y, double z, float rotationYaw)
        {
            entity.setLocationAndAngles(x, y, z, rotationYaw, entity.rotationPitch);
            return true;
        }

        @Override
        public void removeStalePortalLocations(long totalWorldTime)
        {
            /* do nothing */
        }

        @Override
        public void placeInPortal(Entity entity, double x, double y, double z, float rotationYaw)
        {
            placeInExistingPortal(entity, x, y, z, rotationYaw);
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
                ChatHelper.sendMessage(player, ChatFormatting.RED + "Teleport cancelled");
                return true;
            }
            if (System.currentTimeMillis() - start < timeout)
            {
                return false;
            }
            checkedTeleport(player, point);
            ChatHelper.sendMessage(player, ChatFormatting.GREEN + "Teleported");
            return true;
        }

    }

    public static final String TELEPORT_COOLDOWN = "fe.teleport.cooldown";
    public static final String TELEPORT_WARMUP = "fe.teleport.warmup";
    public static final String TELEPORT_CROSSDIM = "fe.teleport.crossdim";
    public static final String TELEPORT_FROM = "fe.teleport.from";
    public static final String TELEPORT_TO = "fe.teleport.to";

    private static Map<UUID, TeleportInfo> tpInfos = new HashMap<UUID, TeleportInfo>();

    public static void teleport(EntityPlayerMP player, WarpPoint point)
    {
        if (point.getWorld() == null)
        {
            DimensionManager.initDimension(point.getDimension());
            if (point.getWorld() == null)
            {
                ChatHelper.sendMessage(player, ChatFormatting.RED + "Unable to teleport! Target dimension does not exist");
                return;
            }
        }


        if (!canTeleportTo(point))
        {
            ChatHelper.sendMessage(player, ChatFormatting.RED + "Unable to teleport! Target location obstructed.");
            return;
        }

        // Setup timed teleport
        tpInfos.put(player.getPersistentID(), new TeleportInfo(player, point, 0));
        //ChatHelper.sendMessage(player, ChatFormatting.YELLOW + "Teleporting...");
    }

    public static boolean canTeleportTo(WarpPoint point)
    {
        if (point.getY() < 0)
            return false;
        Block block1 = point.getWorld().getBlockState(new BlockPos(point.getBlockX(), point.getBlockY(), point.getBlockZ())).getBlock();
        Block block2 = point.getWorld().getBlockState(new BlockPos(point.getBlockX(), point.getBlockY() + 1, point.getBlockZ())).getBlock();
        boolean block1Free = !block1.getMaterial().isSolid() || block1.getBlockBoundsMaxX() < 1 || block1.getBlockBoundsMaxY() > 0;
        boolean block2Free = !block2.getMaterial().isSolid() || block2.getBlockBoundsMaxX() < 1 || block2.getBlockBoundsMaxY() > 0;
        return block1Free && block2Free;
    }

    public static void checkedTeleport(EntityPlayerMP player, WarpPoint point)
    {
        if (!canTeleportTo(point))
        {
            ChatHelper.sendMessage(player, ChatFormatting.RED + "Unable to teleport! Target location obstructed.");
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
            for (Iterator<TeleportInfo> it = tpInfos.values().iterator(); it.hasNext();)
            {
                TeleportInfo tpInfo = it.next();
                if (tpInfo.check())
                {
                    it.remove();
                }
            }
        }
    }

    public static void transferPlayerToDimension(EntityPlayerMP player, int dimension, Teleporter teleporter)
    {
        int oldDim = player.dimension;
        MinecraftServer mcServer = PlayerMPUtil.getServerInstance();

        WorldServer oldWorld = mcServer.worldServerForDimension(player.dimension);
        player.dimension = dimension;
        WorldServer newWorld = mcServer.worldServerForDimension(player.dimension);
        player.connection.sendPacket(new S07PacketRespawn(player.dimension, newWorld.difficultySetting,
                newWorld.getWorldInfo().getTerrainType(), player.interactionManager.getGameType())); // Forge: Use new dimensions information
        oldWorld.removePlayerEntityDangerously(player);
        player.isDead = false;

        transferEntityToWorld(player, oldDim, oldWorld, newWorld, teleporter);

        mcServer.getPlayerList().preparePlayer(player, oldWorld);
        player.connection.setPlayerLocation(player.posX, player.posY, player.posZ, player.rotationYaw,
                player.rotationPitch);
        player.interactionManager.setWorld(newWorld);
        mcServer.getPlayerList().updateTimeAndWeatherForPlayer(player, newWorld);
        mcServer.getPlayerList().syncPlayerInventory(player);
        Iterator<?> iterator = player.getActivePotionEffects().iterator();
        while (iterator.hasNext())
        {
            PotionEffect potioneffect = (PotionEffect) iterator.next();
            player.connection.sendPacket(new S1DPacketEntityEffect(player.getEntityId(), potioneffect));
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
        d0 = MathHelper.clamp_int((int) d0, -29999872, 29999872);
        d1 = MathHelper.clamp_int((int) d1, -29999872, 29999872);
        if (entity.isEntityAlive())
        {
            entity.setLocationAndAngles(d0, entity.posY, d1, entity.rotationYaw, entity.rotationPitch);
            teleporter.placeInPortal(entity, d3, d4, d5, f);
            newWorld.spawnEntityInWorld(entity);
            newWorld.updateEntityWithOptionalForce(entity, false);
        }
        entity.setWorld(newWorld);
    }

}
