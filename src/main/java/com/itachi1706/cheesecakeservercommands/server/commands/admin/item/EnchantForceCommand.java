package com.itachi1706.cheesecakeservercommands.server.commands.admin.item;

import com.itachi1706.cheesecakeservercommands.util.ChatHelper;
import com.itachi1706.cheesecakeservercommands.util.PlayerMPUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO: Add to Main Command
/**
 * Created by Kenneth on 9/11/2015.
 * for CheesecakeServerCommands in package com.itachi1706.cheesecakeservercommands.server.commands
 */
@SuppressWarnings("unused")
public class EnchantForceCommand implements ICommand {

    private List<String> aliases;

    public EnchantForceCommand(){
        this.aliases = new ArrayList<String>();
        this.aliases.add("enchantforce");
    }

    @Override
    public String getCommandName() {
        return "enchantforce";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_) {
        return "enchantforce <enchantmentname> [level]";
    }

    @Override
    public List getCommandAliases() {
        return this.aliases;
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void processCommand(ICommandSender iCommandSender, String[] astring) {
        if (astring.length == 0) {
            ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.RED + "Usage: /enchantforce <enchantmentname> [level]");
            return;
        }

        if (!PlayerMPUtil.isPlayer(iCommandSender)) {
            ChatHelper.sendMessage(iCommandSender, "Cannot enchant an item for CONSOLE");
            return;
        }

        String enchantstring = astring[0];

        EntityPlayerMP player = (EntityPlayerMP) PlayerMPUtil.castToPlayer(iCommandSender);
        if (player == null) {
            ChatHelper.sendMessage(iCommandSender, "Cannot enchant an item for " + iCommandSender.getCommandSenderName());
            return;
        }

        ItemStack stack = player.getCurrentEquippedItem();
        if (stack == null) {
            ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.RED + "Invalid Item held");
            return;
        }

        // Get Enchantment List
        @SuppressWarnings("unchecked")
        List<String> validEnchantmentNames = new ArrayList<String>();
        Map<String, Enchantment> validEnchantments = new HashMap<String, Enchantment>();
        for (Enchantment enchantment : Enchantment.enchantmentsList) {
            if (enchantment != null) {
                String name = StatCollector.translateToLocal(enchantment.getName()).replaceAll(" ", "");
                validEnchantmentNames.add(name);
                validEnchantments.put(name.toLowerCase(), enchantment);
            }
        }

        @SuppressWarnings("unchecked")
        Map<Integer, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
        Enchantment enchantment = validEnchantments.get(enchantstring.toLowerCase());
        if (enchantment == null) {
            ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.RED + "Invalid enchantment: " + enchantstring);
            return;
        }

        int enchantlevel = enchantment.getMaxLevel();
         if (astring.length == 2) {
             String enchantlevelStr = astring[1];
             try {
                 enchantlevel = Integer.parseInt(enchantlevelStr);
             } catch (NumberFormatException e) {
                 enchantlevel = enchantment.getMaxLevel();
             }
         }

        enchantments.put(enchantment.effectId, enchantlevel);


        EnchantmentHelper.setEnchantments(enchantments, stack);

        ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.GOLD + "Enchanted " + stack.getDisplayName() + " with " + EnumChatFormatting.AQUA + enchantstring
                + EnumChatFormatting.GOLD + " (" + EnumChatFormatting.LIGHT_PURPLE + enchantlevel + EnumChatFormatting.GOLD + ")");
        ChatHelper.sendAdminMessage(iCommandSender, "Enchanted " + stack.getDisplayName() + " with " + enchantstring + " (" + enchantlevel + ")");
    }

    @Override
    public List addTabCompletionOptions(ICommandSender iCommandSender, String[] typedValue) {
        if (!PlayerMPUtil.isPlayer(iCommandSender)) {
            return null;
        }
        if (typedValue.length == 1) {

            EntityPlayerMP player = (EntityPlayerMP) PlayerMPUtil.castToPlayer(iCommandSender);
            if (player == null) {
                return null;
            }

            ItemStack stack = player.getCurrentEquippedItem();
            if (stack == null) {
                return null;
            }

            // Get Enchantment List
            @SuppressWarnings("unchecked")
            List<String> validEnchantmentNames = new ArrayList<String>();
            Map<String, Enchantment> validEnchantments = new HashMap<String, Enchantment>();
            for (Enchantment enchantment : Enchantment.enchantmentsList) {
                if (enchantment != null) {
                    String name = StatCollector.translateToLocal(enchantment.getName()).replaceAll(" ", "");
                    validEnchantmentNames.add(name);
                }
            }

            String[] validEnchantmentArray = new String[validEnchantmentNames.size()];
            validEnchantmentArray = validEnchantmentNames.toArray(validEnchantmentArray);
            return CommandBase.getListOfStringsMatchingLastWord(typedValue, validEnchantmentArray);
        }

        return null;
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender iCommandSender) {
        return PlayerMPUtil.isOperatorOrConsole(iCommandSender);
    }

    @Override
    public boolean isUsernameIndex(String[] p_82358_1_, int p_82358_2_) {
        return false;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
