package com.itachi1706.cheesecakeservercommands.reference;

import com.itachi1706.cheesecakeservercommands.CheesecakeServerCommands;
import net.minecraft.world.damagesource.DamageSource;

import java.util.HashMap;

/**
 * Created by Kenneth on 27/1/2018.
 * for com.itachi1706.cheesecakeservercommands.reference in CheesecakeServerCommands
 */
public class InitDamageSources {

    public static void initalizeDamages() {
        CheesecakeServerCommands.knownDamageSources = new HashMap<>();
        CheesecakeServerCommands.knownDamageSources.put(DamageSource.IN_FIRE.getMsgId(), DamageSource.IN_FIRE);
        CheesecakeServerCommands.knownDamageSources.put(DamageSource.LIGHTNING_BOLT.getMsgId(), DamageSource.LIGHTNING_BOLT);
        CheesecakeServerCommands.knownDamageSources.put(DamageSource.ON_FIRE.getMsgId(), DamageSource.ON_FIRE);
        CheesecakeServerCommands.knownDamageSources.put(DamageSource.LAVA.getMsgId(), DamageSource.LAVA);
        CheesecakeServerCommands.knownDamageSources.put(DamageSource.HOT_FLOOR.getMsgId(), DamageSource.HOT_FLOOR);
        CheesecakeServerCommands.knownDamageSources.put(DamageSource.IN_WALL.getMsgId(), DamageSource.IN_WALL);
        CheesecakeServerCommands.knownDamageSources.put(DamageSource.CRAMMING.getMsgId(), DamageSource.CRAMMING);
        CheesecakeServerCommands.knownDamageSources.put(DamageSource.DROWN.getMsgId(), DamageSource.DROWN);
        CheesecakeServerCommands.knownDamageSources.put(DamageSource.STARVE.getMsgId(), DamageSource.STARVE);
        CheesecakeServerCommands.knownDamageSources.put(DamageSource.CACTUS.getMsgId(), DamageSource.CACTUS);
        CheesecakeServerCommands.knownDamageSources.put(DamageSource.FALL.getMsgId(), DamageSource.FALL);
        CheesecakeServerCommands.knownDamageSources.put(DamageSource.FLY_INTO_WALL.getMsgId(), DamageSource.FLY_INTO_WALL);
        CheesecakeServerCommands.knownDamageSources.put(DamageSource.OUT_OF_WORLD.getMsgId(), DamageSource.OUT_OF_WORLD);
        CheesecakeServerCommands.knownDamageSources.put(DamageSource.GENERIC.getMsgId(), DamageSource.GENERIC);
        CheesecakeServerCommands.knownDamageSources.put(DamageSource.MAGIC.getMsgId(), DamageSource.MAGIC);
        CheesecakeServerCommands.knownDamageSources.put(DamageSource.WITHER.getMsgId(), DamageSource.WITHER);
        CheesecakeServerCommands.knownDamageSources.put(DamageSource.ANVIL.getMsgId(), DamageSource.ANVIL);
        CheesecakeServerCommands.knownDamageSources.put(DamageSource.FALLING_BLOCK.getMsgId(), DamageSource.FALLING_BLOCK);
        CheesecakeServerCommands.knownDamageSources.put(DamageSource.DRAGON_BREATH.getMsgId(), DamageSource.DRAGON_BREATH);
        // TODO: Update
//        CheesecakeServerCommands.knownDamageSources.put(DamageSource.FIREWORKS.getMsgId(), DamageSource.FIREWORKS);
//        CheesecakeServerCommands.knownDamageSources.put("zeus", ZeusCommand.getDamageSource());
    }
}
