package com.itachi1706.cheesecakeservercommands.server.commands.admin;

import com.itachi1706.cheesecakeservercommands.util.ChatHelper;
import com.itachi1706.cheesecakeservercommands.util.PlayerMPUtil;
import com.itachi1706.cheesecakeservercommands.util.ServerUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.Explosion;
import net.minecraft.world.WorldServer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Kenneth on 9/11/2015.
 * for CheesecakeServerCommands in package com.itachi1706.cheesecakeservercommands.server.commands
 */
@SuppressWarnings("unused")
public class FlingCommand implements ICommand {

    private List<String> aliases;

    public FlingCommand(){
        this.aliases = new ArrayList<>();
        this.aliases.add("fling");
    }

    @Override
    @Nonnull
    public String getName() {
        return "fling";
    }

    @Override
    @Nonnull
    public String getUsage(@Nonnull ICommandSender sender) {
        return "/fling [player]";
    }

    @Override
    @Nonnull
    public List<String> getAliases() {
        return this.aliases;
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender iCommandSender, @Nonnull String[] args) {
        if (args.length == 0)
        {
            if (!PlayerMPUtil.isPlayer(iCommandSender)) {
                ChatHelper.sendMessage(iCommandSender, "Cannot fling CONSOLE");
                return;
            } else {
                EntityPlayerMP player = (EntityPlayerMP) PlayerMPUtil.castToPlayer(iCommandSender);
                if (player == null) {
                    ChatHelper.sendMessage(iCommandSender, "Cannot fling" + iCommandSender.getName());
                    return;
                }

                //player.world.spawnParticle("hugeexplosion", player.posX + 0.5D, player.posY + 1.0D, player.posZ + 0.5D, 1.0D, 0.0D, 0.0D);
                player.motionY = 10;
                player.velocityChanged = true;
                player.addPotionEffect(new PotionEffect(Potion.getPotionFromResourceLocation("resistance"), 300, 100, true, false));
                WorldServer worldServer = (WorldServer) player.world;
                worldServer.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, player.posX, player.posY, player.posZ, 0, 0, 0, 0, 0, new int[0]);
                Explosion explosion = new Explosion(player.world, player, player.posX, player.posY, player.posZ, 0, true, true);
                explosion.doExplosionB(true);
                ChatHelper.sendMessage(iCommandSender, TextFormatting.DARK_PURPLE + "You were flung into the air");
                ChatHelper.sendAdminMessage(iCommandSender, "Flung own self into the air!");
                return;
            }
        }

        String subname = args[0];
        EntityPlayerMP player = PlayerMPUtil.getPlayer(subname);
        if (player == null) {
            ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "Player not found");
            return;
        }

        player.motionY = 10;
        player.velocityChanged = true;
        player.addPotionEffect(new PotionEffect(Potion.getPotionFromResourceLocation("resistance"), 300, 100, true, false));
        WorldServer worldServer = (WorldServer) player.world;
        worldServer.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, player.posX, player.posY, player.posZ, 0, 0, 0, 0, 0, new int[0]);
        Explosion explosion = new Explosion(player.world, player, player.posX, player.posY, player.posZ, 0, true, true);
        explosion.doExplosionB(true);
        ChatHelper.sendMessage(iCommandSender, TextFormatting.GOLD + "Flung " + player.getName() + " into the air!");
        ChatHelper.sendAdminMessage(iCommandSender, "Flung " + player.getName() + " into the air");
        ChatHelper.sendMessage(player, TextFormatting.DARK_PURPLE + "You were flung into the air");
    }

    @Override
    @Nonnull
    public List<String> getTabCompletions(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1)
            return CommandBase.getListOfStringsMatchingLastWord(args, ServerUtil.getServerInstance().getOnlinePlayerNames());
        return Collections.emptyList();
    }

    @Override
    public boolean checkPermission(@Nonnull MinecraftServer server, @Nonnull ICommandSender iCommandSender) {
        return PlayerMPUtil.isOperatorOrConsole(iCommandSender);
    }

    @Override
    public boolean isUsernameIndex(@Nonnull String[] args, int p_82358_2_) {
        return false;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public int compareTo(ICommand o) {
        return 0;
    }
}
