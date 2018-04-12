package com.github.SquadAlpha.AutoBuildFarms.utils;

import com.github.SquadAlpha.AutoBuildFarms.reference.Reference;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.logging.Logger;

public class Utils {

    // Replaces the pattern with a paragraph symbol for easy color conversion.
    // Keeps into account an escaping slash (\)
    public static String replaceWithParagraph(String pattern, String subject) {
        return subject.replaceAll("(?<!\\\\)" + pattern, "§");
    }

    public static void prettyPrintException(Logger logger, Exception e) {
        logger.severe(e.getLocalizedMessage() + " caused by " + e.getCause().getLocalizedMessage());
        Arrays.stream(e.getStackTrace()).forEachOrdered(s -> logger.severe(s.toString()));
    }


    public enum permDeniedReson {
        Farm,
        Size,
        None
    }

    public static class econ {

        public static EconomyResponse takeMoneyForFarm(Player p, Farm.Size s) {
            return Reference.econ.withdrawPlayer(p, p.getWorld().getName(), s.getPrice());
        }

        public static boolean takeMoneyForFarmAndYell(Player p, Farm f, Farm.Size s, ChatBuilder builder, String use) {
            EconomyResponse resp = takeMoneyForFarm(p, s);
            switch (resp.type) {
                case SUCCESS:
                    return true;
                case FAILURE:
                    builder
                            .append(ChatColor.RED, resp.errorMessage)
                            .newLine(ChatColor.RED)
                            .append(ChatColor.RED, "You don't have enough money to ")
                            .append(ChatColor.RED, use)
                            .append(ChatColor.RED, " ")
                            .append(ChatColor.GRAY, f.getFancyName())
                            .append(ChatColor.WHITE, "->")
                            .append(ChatColor.GRAY, s.getFancyName())
                            .newLine(ChatColor.RED)
                            .append(ChatColor.WHITE, "You have ")
                            .append(ChatColor.AQUA, Reference.econ.format(Reference.econ.getBalance(p, p.getWorld().getName())))
                            .append(ChatColor.WHITE, " but need ")
                            .append(ChatColor.AQUA, Reference.econ.format(s.getPrice()));
                    return false;
                case NOT_IMPLEMENTED:
                    builder.append(ChatColor.RED, "The server owner has an economy plugin that does not implement required methods")
                            .newLine(ChatColor.RED)
                            .append(ChatColor.AQUA, "net.milkbowl.vault.economy.Economy.withdrawPlayer(org.bukkit.OfflinePlayer, java.lang.String, double)")
                            .newLine(ChatColor.RED)
                            .append(ChatColor.RED, resp.errorMessage);

                    return false;
                default:
                    return false;
            }
        }
    }

    public static class perms {

        public static String getFarmPerm(Farm f, String use) {
            return Reference.plugin.getDescription().getPrefix() + "." + use + "." + f.getName();
        }

        public static String getSizePerm(Farm.Size s, Farm f, String use) {
            return getFarmPerm(f, use) + "." + s.getName();
        }

        public static permDeniedReson checkPlacePerms(Player p, Farm f, Farm.Size s, String use) {
            if (Reference.perms.has(p, getFarmPerm(f, use))) {
                if (Reference.perms.has(p, getSizePerm(s, f, use))) {
                    return permDeniedReson.None;
                }
                return permDeniedReson.Size;
            }
            return permDeniedReson.Farm;
        }


        public static boolean checkPlacePermsAndYell(Player p, Farm f, Farm.Size s, ChatBuilder builder, String use) {
            switch (checkPlacePerms(p, f, s, use)) {
                case None:
                    return true;
                case Farm:
                    noPermissionMessageAppend(builder, getFarmPerm(f, use));
                    return false;
                case Size:
                    noPermissionMessageAppend(builder, getSizePerm(s, f, use));
                    return false;
                default:
                    throw new IllegalArgumentException("got wrong permDeniedReason this shouldn't happen!!");
            }
        }

        public static void noPermissionMessageAppend(ChatBuilder builder, String perm) {
            builder.append(ChatColor.RED, "You don't have permission:")
                    .append(ChatColor.AQUA, perm)
                    .append(ChatColor.RED, " to execute this command");
        }
    }
}
