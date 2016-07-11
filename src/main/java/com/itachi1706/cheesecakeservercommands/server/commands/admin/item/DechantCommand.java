package com.itachi1706.cheesecakeservercommands.server.commands.admin.item;

import com.itachi1706.cheesecakeservercommands.util.ChatHelper;
import com.itachi1706.cheesecakeservercommands.util.PlayerMPUtil;
import com.mojang.realmsclient.gui.ChatFormatting;
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
import net.minecraft.util.text.translation.I18n;

import javax.annotation.Nullable;
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
public class DechantCommand implements ICommand {

    private List<String> aliases;

    public DechantCommand(){
        this.aliases = new ArrayList<String>();
        this.aliases.add("dechant");
    }

    @Override
    public String getCommandName() {
        return "dechant";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_) {
        return "dechant <enchantmentname>";
    }

    @Override
    public List getCommandAliases() {
        return this.aliases;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender iCommandSender, String[] args) throws CommandException {
        if (args.length == 0) {
            ChatHelper.sendMessage(iCommandSender, ChatFormatting.RED + "Usage: /dechant <enchantmentname>");
            return;
        }

        String enchantstring = args[0];

        if (!PlayerMPUtil.isPlayer(iCommandSender)) {
            ChatHelper.sendMessage(iCommandSender, "Cannot dechant an item for CONSOLE");
            return;
        } else {
            EntityPlayerMP player = (EntityPlayerMP) PlayerMPUtil.castToPlayer(iCommandSender);
            if (player == null) {
                ChatHelper.sendMessage(iCommandSender, "Cannot dechant an item for " + iCommandSender.getName());
                return;
            }

            ItemStack stack = player.getHeldItemMainhand();
            if (stack == null) {
                ChatHelper.sendMessage(iCommandSender, ChatFormatting.RED + "Invalid Item held");
                return;
            }

            // Get Enchantment List
            @SuppressWarnings("unchecked")
            Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
            List<String> validEnchantmentNames = new ArrayList<String>();
            Map<String, Enchantment> validEnchantments = new HashMap<String, Enchantment>();
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

                ChatHelper.sendMessage(iCommandSender, ChatFormatting.GOLD + "Removed all enchantments from " + stack.getDisplayName());
                ChatHelper.sendAdminMessage(iCommandSender, "Removed all enchantments from " + stack.getDisplayName());
                return;
            } else {
                Enchantment enchantment = validEnchantments.get(enchantstring.toLowerCase());
                if (enchantment == null) {
                    ChatHelper.sendMessage(iCommandSender, ChatFormatting.RED + "Invalid enchantment: " + enchantstring);
                    return;
                }
                enchantments.remove(enchantment);
            }

            EnchantmentHelper.setEnchantments(enchantments, stack);

            ChatHelper.sendMessage(iCommandSender, ChatFormatting.GOLD + "Removed " + ChatFormatting.AQUA + enchantstring
                    + ChatFormatting.GOLD + " from " + stack.getDisplayName());
            ChatHelper.sendAdminMessage(iCommandSender, "Dechanted " + enchantstring + " from " + stack.getDisplayName());
        }
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
            Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
            List<String> validEnchantmentNames = new ArrayList<String>();
            for (Enchantment enchantment : Enchantment.REGISTRY) {
                if (enchantment != null && enchantments.containsKey(enchantment)) {
                    String name = I18n.translateToLocal(enchantment.getName()).replaceAll(" ", "");
                    validEnchantmentNames.add(name);
                }
            }
            validEnchantmentNames.add("all");

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
