package com.itachi1706.cheesecakeservercommands.damagesource;

import com.itachi1706.cheesecakeservercommands.util.LogHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

/**
 * Created by Kenneth on 3/5/2016.
 * for com.itachi1706.cheesecakeservercommands.damagesource in CheesecakeServerCommands
 */
public class ZeusDamage extends DamageSource {
    public ZeusDamage(String p_i1566_1_) {
        super(p_i1566_1_);
    }

    @Override
    public ITextComponent getDeathMessage(EntityLivingBase p_151519_1_)
    {
        String text = "%s suffered the Wrath of Zeus!";
        String username = p_151519_1_.getName();
        LogHelper.info(username);
        String finaltext = String.format(text, username);
        return new TextComponentString(finaltext);
    }

}
