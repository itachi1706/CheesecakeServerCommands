package com.itachi1706.cheesecakeservercommands.server.commands.admin.item;

import com.itachi1706.cheesecakeservercommands.util.ChatHelper;
import com.itachi1706.cheesecakeservercommands.util.PlayerMPUtil;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Created by Kenneth on 9/11/2015.
 * for CheesecakeServerCommands in package com.itachi1706.cheesecakeservercommands.server.commands
 */
@SuppressWarnings("unused")
public class EnchantCommand implements ICommand {

    private List<String> aliases;

    public EnchantCommand(){
        this.aliases = new ArrayList<String>();
        this.aliases.add("enchant");
    }

    @Override
    public String getCommandName() {
        return "enchant";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_) {
        return "enchant <enchantmentname> [level]";
    }

    @Override
    public List getCommandAliases() {
        return this.aliases;
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void execute(MinecraftServer server, ICommandSender iCommandSender, String[] args) throws CommandException {
        if (args.length == 0) {
            ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "Usage: /enchant <enchantmentname> [level]");
            return;
        }

        if (!PlayerMPUtil.isPlayer(iCommandSender)) {
            ChatHelper.sendMessage(iCommandSender, "Cannot enchant an item for CONSOLE");
            return;
        }

        String enchantstring = args[0];

        EntityPlayerMP player = (EntityPlayerMP) PlayerMPUtil.castToPlayer(iCommandSender);
        if (player == null) {
            ChatHelper.sendMessage(iCommandSender, "Cannot enchant an item for " + iCommandSender.getName());
            return;
        }

        ItemStack stack = player.getHeldItemMainhand();
        if (stack == null) {
            ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "Invalid Item held");
            return;
        }

        // Get Enchantment List
        @SuppressWarnings("unchecked")
        List<String> validEnchantmentNames = new ArrayList<String>();
        Map<String, Enchantment> validEnchantments = new HashMap<String, Enchantment>();
        for (Enchantment enchantment : Enchantment.REGISTRY) {
            if (enchantment != null && enchantment.canApplyAtEnchantingTable(stack)) {
                String name = I18n.translateToLocal(enchantment.getName()).replaceAll(" ", "");
                validEnchantmentNames.add(name);
                validEnchantments.put(name.toLowerCase(), enchantment);
            }
        }

        @SuppressWarnings("unchecked")
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
        Enchantment enchantment = validEnchantments.get(enchantstring.toLowerCase());
        if (enchantment == null) {
            ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "Invalid enchantment: " + enchantstring);
            return;
        }

        int enchantlevel = enchantment.getMaxLevel();
         if (args.length == 2) {
             enchantlevel = CommandBase.parseInt(args[1]);
         }

        enchantments.put(enchantment, enchantlevel);


        EnchantmentHelper.setEnchantments(enchantments, stack);

        ChatHelper.sendMessage(iCommandSender, TextFormatting.GOLD + "Enchanted " + stack.getDisplayName() + " with " + TextFormatting.AQUA + enchantstring
                + TextFormatting.GOLD + " (" + TextFormatting.LIGHT_PURPLE + enchantlevel + TextFormatting.GOLD + ")");
        ChatHelper.sendAdminMessage(iCommandSender, "Enchanted " + stack.getDisplayName() + " with " + enchantstring + " (" + enchantlevel + ")");
    }

    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender iCommandSender, String[] typedValue, @Nullable BlockPos pos) {
        if (!PlayerMPUtil.isPlayer(iCommandSender)) {
            return null;
        }
        if (typedValue.length == 1) {

            EntityPlayerMP player = (EntityPlayerMP) PlayerMPUtil.castToPlayer(iCommandSender);
            if (player == null) {
                return null;
            }

            ItemStack stack = player.getHeldItemMainhand();
            if (stack == null) {
                return null;
            }

            // Get Enchantment List
            @SuppressWarnings("unchecked")
            List<String> validEnchantmentNames = new ArrayList<String>();
            Map<String, Enchantment> validEnchantments = new HashMap<String, Enchantment>();
            for (Enchantment enchantment : Enchantment.REGISTRY) {
                if (enchantment != null && enchantment.canApplyAtEnchantingTable(stack)) {
                    String name = I18n.translateToLocal(enchantment.getName()).replaceAll(" ", "");
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
    public boolean checkPermission(MinecraftServer server, ICommandSender iCommandSender) {
        return PlayerMPUtil.isOperatorOrConsole(iCommandSender);
    }

    @Override
    public boolean isUsernameIndex(String[] p_82358_1_, int p_82358_2_) {
        return false;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public int compareTo(ICommand o) {
        return 0;
    }
}
