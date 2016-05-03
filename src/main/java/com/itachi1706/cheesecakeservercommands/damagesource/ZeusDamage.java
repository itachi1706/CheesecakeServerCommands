package com.itachi1706.cheesecakeservercommands.damagesource;

import com.itachi1706.cheesecakeservercommands.util.ChatHelper;
import com.itachi1706.cheesecakeservercommands.util.LogHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.*;

/**
 * Created by Kenneth on 3/5/2016.
 * for com.itachi1706.cheesecakeservercommands.damagesource in CheesecakeServerCommands
 */
public class ZeusDamage extends DamageSource {
    public ZeusDamage(String p_i1566_1_) {
        super(p_i1566_1_);
    }

    @Override
    public IChatComponent func_151519_b(EntityLivingBase p_151519_1_)
    {
        String text = "%s suffered the Wrath of Zeus!";
        String username = p_151519_1_.getCommandSenderName();
        LogHelper.info(username);
        String finaltext = String.format(text, username);
        return new ChatComponentText(finaltext);
    }

}
