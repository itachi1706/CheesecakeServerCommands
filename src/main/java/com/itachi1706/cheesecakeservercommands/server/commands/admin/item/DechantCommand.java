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

import javax.annotation.Nullable;
import java.util.*;

/**
 * Created by Kenneth on 9/11/2015.
 * for CheesecakeServerCommands in package com.itachi1706.cheesecakeservercommands.server.commands
 */
@SuppressWarnings("unused")
public class DechantCommand implements ICommand {

    private List<String> aliases;

    public DechantCommand(){
        this.aliases = new ArrayList<>();
        this.aliases.add("dechant");
    }

    @Override
    public String getName() {
        return "dechant";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/dechant <enchantmentname>";
    }

    @Override
    public List getAliases() {
        return this.aliases;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender iCommandSender, String[] args) throws CommandException {
        if (args.length == 0) {
            ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "Usage: /dechant <enchantmentname>");
            return;
        }

        String enchantstring = args[0];

        if (!PlayerMPUtil.isPlayer(iCommandSender)) {
            ChatHelper.sendMessage(iCommandSender, "Cannot dechant an item for CONSOLE");
        } else {
            EntityPlayerMP player = (EntityPlayerMP) PlayerMPUtil.castToPlayer(iCommandSender);
            if (player == null) {
                ChatHelper.sendMessage(iCommandSender, "Cannot dechant an item for " + iCommandSender.getName());
                return;
            }

            ItemStack stack = player.getHeldItemMainhand();
            if (stack == null) {
                ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "Invalid Item held");
                return;
            }

            // Get Enchantment List
            @SuppressWarnings("unchecked")
            Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
            List<String> validEnchantmentNames = new ArrayList<>();
            Map<String, Enchantment> validEnchantments = new HashMap<>();
            for (Enchantment enchantment : Enchantment.REGISTRY) {
                if (enchantment != null && enchantments.containsKey(enchantment)) {
                    String name = I18n.translateToLocal(enchantment.getName()).replaceAll(" ", "");
                    validEnchantmentNames.add(name);
                    validEnchantments.put(name.toLowerCase(), enchantment);
                }
            }

            if (enchantstring.equalsIgnoreCase("all")) {
                enchantments.clear();
                EnchantmentHelper.setEnchantments(enchantments, stack);

                ChatHelper.sendMessage(iCommandSender, TextFormatting.GOLD + "Removed all enchantments from " + stack.getDisplayName());
                ChatHelper.sendAdminMessage(iCommandSender, "Removed all enchantments from " + stack.getDisplayName());
                return;
            } else {
                Enchantment enchantment = validEnchantments.get(enchantstring.toLowerCase());
                if (enchantment == null) {
                    ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "Invalid enchantment: " + enchantstring);
                    return;
                }
                enchantments.remove(enchantment);
            }

            EnchantmentHelper.setEnchantments(enchantments, stack);

            ChatHelper.sendMessage(iCommandSender, TextFormatting.GOLD + "Removed " + TextFormatting.AQUA + enchantstring
                    + TextFormatting.GOLD + " from " + stack.getDisplayName());
            ChatHelper.sendAdminMessage(iCommandSender, "Dechanted " + enchantstring + " from " + stack.getDisplayName());
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (!PlayerMPUtil.isPlayer(sender)) {
            return Collections.emptyList();
        }
        if (args.length == 1) {

            EntityPlayerMP player = (EntityPlayerMP) PlayerMPUtil.castToPlayer(sender);
            if (player == null) {
                return Collections.emptyList();
            }

            ItemStack stack = player.getHeldItemMainhand();
            if (stack == null) {
                return Collections.emptyList();
            }

            // Get Enchantment List
            @SuppressWarnings("unchecked")
            Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
            List<String> validEnchantmentNames = new ArrayList<>();
            for (Enchantment enchantment : Enchantment.REGISTRY) {
                if (enchantment != null && enchantments.containsKey(enchantment)) {
                    String name = I18n.translateToLocal(enchantment.getName()).replaceAll(" ", "");
                    validEnchantmentNames.add(name);
                }
            }
            validEnchantmentNames.add("all");

            String[] validEnchantmentArray = new String[validEnchantmentNames.size()];
            validEnchantmentArray = validEnchantmentNames.toArray(validEnchantmentArray);
            return CommandBase.getListOfStringsMatchingLastWord(args, validEnchantmentArray);
        }

        return Collections.emptyList();
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
