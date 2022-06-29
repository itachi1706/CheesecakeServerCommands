package com.itachi1706.cheesecakeservercommands.server.commands.admin.item;

import com.itachi1706.cheesecakeservercommands.util.ChatHelper;
import com.itachi1706.cheesecakeservercommands.util.PlayerMPUtil;
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
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * Created by Kenneth on 9/11/2015.
 * for CheesecakeServerCommands in package com.itachi1706.cheesecakeservercommands.server.commands
 */
@SuppressWarnings("unused")
public class EnchantForceCommand implements ICommand {

    private List<String> aliases;

    public EnchantForceCommand(){
        this.aliases = new ArrayList<>();
        this.aliases.add("enchantforce");
    }

    @Override
    @Nonnull
    public String getName() {
        return "enchantforce";
    }

    @Override
    @Nonnull
    public String getUsage(@Nonnull ICommandSender sender) {
        return "/enchantforce <enchantmentname> [level]";
    }

    @Override
    @Nonnull
    public List<String> getAliases() {
        return this.aliases;
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender iCommandSender, @Nonnull String[] args) throws CommandException {
        if (args.length == 0) {
            ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "Usage: /enchantforce <enchantmentname> [level]");
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
        if (stack.equals(ItemStack.EMPTY)) {
            ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "Invalid Item held");
            return;
        }

        // Get Enchantment List
        @SuppressWarnings("unchecked")
        List<String> validEnchantmentNames = new ArrayList<>();
        Map<String, Enchantment> validEnchantments = new HashMap<>();
        for (Enchantment enchantment : Enchantment.REGISTRY) {
            if (enchantment != null) {
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
    @Nonnull
    public List<String> getTabCompletions(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args, @Nullable BlockPos targetPos) {
        if (!PlayerMPUtil.isPlayer(sender)) {
            return Collections.emptyList();
        }
        if (args.length == 1) {

            EntityPlayerMP player = (EntityPlayerMP) PlayerMPUtil.castToPlayer(sender);
            if (player == null) {
                return Collections.emptyList();
            }

            ItemStack stack = player.getHeldItemMainhand();
            if (stack.equals(ItemStack.EMPTY)) {
                return Collections.emptyList();
            }

            // Get Enchantment List
            @SuppressWarnings("unchecked")
            List<String> validEnchantmentNames = new ArrayList<>();
            Map<String, Enchantment> validEnchantments = new HashMap<>();
            for (Enchantment enchantment : Enchantment.REGISTRY) {
                if (enchantment != null) {
                    String name = I18n.translateToLocal(enchantment.getName()).replaceAll(" ", "");
                    validEnchantmentNames.add(name);
                }
            }

            String[] validEnchantmentArray = new String[validEnchantmentNames.size()];
            validEnchantmentArray = validEnchantmentNames.toArray(validEnchantmentArray);
            return CommandBase.getListOfStringsMatchingLastWord(args, validEnchantmentArray);
        }

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
