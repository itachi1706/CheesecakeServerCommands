package com.itachi1706.cheesecakeservercommands.damagesource;

import com.itachi1706.cheesecakeservercommands.util.LogHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Kenneth on 3/5/2016.
 * for com.itachi1706.cheesecakeservercommands.damagesource in CheesecakeServerCommands
 */
public class ZeusDamage extends DamageSource {
    public ZeusDamage(String p_19333_) {
        super(p_19333_);
    }

    @Override
    public @NotNull Component getLocalizedDeathMessage(LivingEntity p_19343_) {
        String text = "%s suffered the Wrath of Zeus!";
        String username = p_19343_.getName().toString();
        LogHelper.info(username);
        String finaltext = String.format(text, username);
        return new TextComponent(finaltext);
    }
}
