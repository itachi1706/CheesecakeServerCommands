package com.itachi1706.cheesecakeservercommands.reference;

import com.itachi1706.cheesecakeservercommands.CheesecakeServerCommands;
import com.itachi1706.cheesecakeservercommands.server.commands.admin.ZeusCommand;
import net.minecraft.util.DamageSource;

import java.util.HashMap;

/**
 * Created by Kenneth on 27/1/2018.
 * for com.itachi1706.cheesecakeservercommands.reference in CheesecakeServerCommands
 */
public class InitDamageSources {

    public static void initalizeDamages() {
        CheesecakeServerCommands.knownDamageSources = new HashMap<>();
        CheesecakeServerCommands.knownDamageSources.put(DamageSource.IN_FIRE.getDamageType(), DamageSource.IN_FIRE);
        CheesecakeServerCommands.knownDamageSources.put(DamageSource.LIGHTNING_BOLT.getDamageType(), DamageSource.LIGHTNING_BOLT);
        CheesecakeServerCommands.knownDamageSources.put(DamageSource.ON_FIRE.getDamageType(), DamageSource.ON_FIRE);
        CheesecakeServerCommands.knownDamageSources.put(DamageSource.LAVA.getDamageType(), DamageSource.LAVA);
        CheesecakeServerCommands.knownDamageSources.put(DamageSource.HOT_FLOOR.getDamageType(), DamageSource.HOT_FLOOR);
        CheesecakeServerCommands.knownDamageSources.put(DamageSource.IN_WALL.getDamageType(), DamageSource.IN_WALL);
        CheesecakeServerCommands.knownDamageSources.put(DamageSource.CRAMMING.getDamageType(), DamageSource.CRAMMING);
        CheesecakeServerCommands.knownDamageSources.put(DamageSource.DROWN.getDamageType(), DamageSource.DROWN);
        CheesecakeServerCommands.knownDamageSources.put(DamageSource.STARVE.getDamageType(), DamageSource.STARVE);
        CheesecakeServerCommands.knownDamageSources.put(DamageSource.CACTUS.getDamageType(), DamageSource.CACTUS);
        CheesecakeServerCommands.knownDamageSources.put(DamageSource.FALL.getDamageType(), DamageSource.FALL);
        CheesecakeServerCommands.knownDamageSources.put(DamageSource.FLY_INTO_WALL.getDamageType(), DamageSource.FLY_INTO_WALL);
        CheesecakeServerCommands.knownDamageSources.put(DamageSource.OUT_OF_WORLD.getDamageType(), DamageSource.OUT_OF_WORLD);
        CheesecakeServerCommands.knownDamageSources.put(DamageSource.GENERIC.getDamageType(), DamageSource.GENERIC);
        CheesecakeServerCommands.knownDamageSources.put(DamageSource.MAGIC.getDamageType(), DamageSource.MAGIC);
        CheesecakeServerCommands.knownDamageSources.put(DamageSource.WITHER.getDamageType(), DamageSource.WITHER);
        CheesecakeServerCommands.knownDamageSources.put(DamageSource.ANVIL.getDamageType(), DamageSource.ANVIL);
        CheesecakeServerCommands.knownDamageSources.put(DamageSource.FALLING_BLOCK.getDamageType(), DamageSource.FALLING_BLOCK);
        CheesecakeServerCommands.knownDamageSources.put(DamageSource.DRAGON_BREATH.getDamageType(), DamageSource.DRAGON_BREATH);
        CheesecakeServerCommands.knownDamageSources.put(DamageSource.FIREWORKS.getDamageType(), DamageSource.FIREWORKS);
        CheesecakeServerCommands.knownDamageSources.put("zeus", ZeusCommand.getDamageSource());
    }
}
