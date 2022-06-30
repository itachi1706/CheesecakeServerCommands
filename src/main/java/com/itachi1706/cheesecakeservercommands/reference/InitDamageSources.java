package com.itachi1706.cheesecakeservercommands.reference;

import com.itachi1706.cheesecakeservercommands.CheesecakeServerCommands;
import com.itachi1706.cheesecakeservercommands.commands.admin.ZeusCommand;
import net.minecraft.world.damagesource.DamageSource;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kenneth on 27/1/2018.
 * for com.itachi1706.cheesecakeservercommands.reference in CheesecakeServerCommands
 */
public class InitDamageSources {

    private InitDamageSources() {
        throw new IllegalStateException("Utility class");
    }

    public static void initalizeDamages() {
        Map<String, DamageSource> knownDamageSource = new HashMap<>();
        knownDamageSource.put(DamageSource.IN_FIRE.getMsgId(), DamageSource.IN_FIRE);
        knownDamageSource.put(DamageSource.LIGHTNING_BOLT.getMsgId(), DamageSource.LIGHTNING_BOLT);
        knownDamageSource.put(DamageSource.ON_FIRE.getMsgId(), DamageSource.ON_FIRE);
        knownDamageSource.put(DamageSource.LAVA.getMsgId(), DamageSource.LAVA);
        knownDamageSource.put(DamageSource.HOT_FLOOR.getMsgId(), DamageSource.HOT_FLOOR);
        knownDamageSource.put(DamageSource.IN_WALL.getMsgId(), DamageSource.IN_WALL);
        knownDamageSource.put(DamageSource.CRAMMING.getMsgId(), DamageSource.CRAMMING);
        knownDamageSource.put(DamageSource.DROWN.getMsgId(), DamageSource.DROWN);
        knownDamageSource.put(DamageSource.STARVE.getMsgId(), DamageSource.STARVE);
        knownDamageSource.put(DamageSource.CACTUS.getMsgId(), DamageSource.CACTUS);
        knownDamageSource.put(DamageSource.FALL.getMsgId(), DamageSource.FALL);
        knownDamageSource.put(DamageSource.FLY_INTO_WALL.getMsgId(), DamageSource.FLY_INTO_WALL);
        knownDamageSource.put(DamageSource.OUT_OF_WORLD.getMsgId(), DamageSource.OUT_OF_WORLD);
        knownDamageSource.put(DamageSource.GENERIC.getMsgId(), DamageSource.GENERIC);
        knownDamageSource.put(DamageSource.MAGIC.getMsgId(), DamageSource.MAGIC);
        knownDamageSource.put(DamageSource.WITHER.getMsgId(), DamageSource.WITHER);
        knownDamageSource.put(DamageSource.ANVIL.getMsgId(), DamageSource.ANVIL);
        knownDamageSource.put(DamageSource.FALLING_BLOCK.getMsgId(), DamageSource.FALLING_BLOCK);
        knownDamageSource.put(DamageSource.DRAGON_BREATH.getMsgId(), DamageSource.DRAGON_BREATH);
        // TODO: Update
//        knownDamageSource.put(DamageSource.FIREWORKS.getMsgId(), DamageSource.FIREWORKS);
        knownDamageSource.put("zeus", ZeusCommand.getDamageSource());

        CheesecakeServerCommands.setKnownDamageSources(knownDamageSource);
    }
}
