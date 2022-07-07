package com.itachi1706.cheesecakeservercommands.dbstorage;

import com.itachi1706.cheesecakeservercommands.CheesecakeServerCommands;
import com.itachi1706.cheesecakeservercommands.util.LogHelper;
import com.itachi1706.cheesecakeservercommands.util.TextUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;

import javax.annotation.Nonnull;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Kenneth on 9/7/2018.
 * for CheesecakeServerCommands in package com.itachi1706.cheesecakeservercommands.dbstorage
 */
@SuppressWarnings("SqlNoDataSourceInspection")
public class CommandsLogDB {

    private static Connection getSQLiteDBConnection(){
        Connection c;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:" + CheesecakeServerCommands.getConfigFileDirectory().getAbsolutePath() + File.separator + "command_use.db");
        } catch (Exception e) {
            e.printStackTrace();
            LogHelper.error(e.getClass().getName() + ": " + e.getMessage());
            return null;
        }
        return c;
    }

    public static void checkTablesExists(){
        Connection db = getSQLiteDBConnection();
        String createDB = "CREATE TABLE IF NOT EXISTS COMMANDS " +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "NAME varchar(30) not null, " +
                "UUID TEXT NOT NULL, " +
                "TIME TEXT NOT NULL DEFAULT (DATETIME('now')), " +
                "IP VARCHAR(20) NOT NULL, " +
                "COMMAND_BASE TEXT NOT NULL, " +
                "FULL_COMMAND TEXT NOT NULL);";

        if (!DBUtils.createTable(db, createDB, "Commands"))
            LogHelper.error("Commands Log DB fail to ensure that table is created");
    }

    public static void addLog(String commandUser, UUID uuid, String commandBase, String commandExecuted, String ip) {
        String insertQuery = "INSERT INTO COMMANDS (NAME,UUID,IP,COMMAND_BASE,FULL_COMMAND) " +
                "VALUES('" + commandUser + "','" + uuid + "','" + ip + "','"
                + commandBase + "','" + commandExecuted.trim() + "');";

        Connection db = getSQLiteDBConnection();
        if (db == null) {
            LogHelper.error("Unable to add log due to failed db connection");
            return;
        }

        DBUtils.insertRecord(db, insertQuery, "Unable to insert command record");
    }

    private static int getCommandUsageByPlayerNameOrUuid(String name){
        return getCount(name);
    }

    public static int getCommandUsageByPlayerNameOrUuid(UUID uuid){
        return getCommandUsageByPlayerNameOrUuid(uuid.toString());
    }

    private static int getCount(@Nonnull String nameOrUuid){
        String queryString = "SELECT COUNT(*) FROM COMMANDS WHERE (NAME='" + nameOrUuid + "' OR UUID='" + nameOrUuid + "');";
        if (nameOrUuid.equalsIgnoreCase("console") || nameOrUuid.equalsIgnoreCase("server"))
            queryString = "SELECT COUNT(*) FROM COMMANDS WHERE (IP='localhost');";
        Connection db = getSQLiteDBConnection();
        if (db == null){
            LogHelper.error("Unable to get count due to failed db connection");
            return -1;
        }
        int commandUsageCount = 0;

        try {
            commandUsageCount = DBUtils.getCount(db, queryString);
        } catch (SQLException e) {
            e.printStackTrace();
            LogHelper.error("Unable to get commands usage count for " + nameOrUuid + " from database");
        }

        return commandUsageCount;
    }

    public static int getTotalCount(){
        String queryString = "SELECT COUNT(*) FROM COMMANDS;";
        Connection db = getSQLiteDBConnection();
        if (db == null){
            LogHelper.error("Unable to add log due to failed db connection");
            return -1;
        }
        int commandUsageCount = 0;

        try {
            commandUsageCount = DBUtils.getCount(db, queryString);
        } catch (SQLException e) {
            e.printStackTrace();
            LogHelper.error("Unable to get commands usage count from database");
        }

        return commandUsageCount;
    }

    public static void deleteLogs(CommandSourceStack CommandSourceStack, String target){
        String sqlQuery = "DELETE FROM COMMANDS WHERE NAME='" + target + "' OR UUID='" + target + "';";
        if (target.equalsIgnoreCase("console") || target.equalsIgnoreCase("server"))
            sqlQuery = "DELETE FROM COMMANDS WHERE IP='localhost';";
        Connection db = getSQLiteDBConnection();
        if (db == null){
            LogHelper.error("Unable to delete logs due to failed db connection");
            return;
        }
        try {
            DBUtils.deleteRecord(db, sqlQuery);
            TextUtil.sendChatMessage(CommandSourceStack, ChatFormatting.GREEN + target + " command usage logs deleted!");
        } catch (Exception e) {
            TextUtil.sendChatMessage(CommandSourceStack, ChatFormatting.RED + "An Error Occured trying to delete logs! (" + e.toString() + ")");
            LogHelper.error("Error occurred deleting logs (" + e.toString() + ")");
            e.printStackTrace();
        }
    }

    private static String sendCommands(int no, String name, String uuid, String command, String datetime, String ip){
        //1. name (uuid) executed command at datetime with IP
        return ChatFormatting.GOLD + "" + no + ". " +
                ChatFormatting.AQUA + name + ChatFormatting.RESET + " (" + uuid + ") executed " +
                ChatFormatting.GREEN + "/" + command + ChatFormatting.RESET + " at " +
                ChatFormatting.RESET + "" + ChatFormatting.ITALIC + datetime + " UTC" +
                ChatFormatting.RESET + " with " + ChatFormatting.LIGHT_PURPLE + ip;
    }

    private static ArrayList<String> getFullEntityPlayerLogs(String target){
        Connection db = getSQLiteDBConnection();
        if (db == null){
            LogHelper.error("Unable to check commands stats due to failed db connection");
            return null;
        }
        Statement statement;

        String querySQL = "SELECT NAME,UUID,FULL_COMMAND,TIME,IP FROM COMMANDS WHERE (NAME='" + target + "' OR UUID='" + target + "') ORDER BY TIME DESC;";

        // Check if CONSOLE
        if (target.equalsIgnoreCase("console") || target.equalsIgnoreCase("server"))
            querySQL = "SELECT NAME,UUID,FULL_COMMAND,TIME,IP FROM COMMANDS WHERE (IP='localhost') ORDER BY TIME DESC;";

        try {
            db.setAutoCommit(false);
            statement = db.createStatement();
            ResultSet rs = statement.executeQuery(querySQL);
            int i = 1;
            ArrayList<String> commandHist = new ArrayList<>();
            while (rs.next()){
                commandHist.add(sendCommands(i, rs.getString("NAME"), rs.getString("UUID"), rs.getString("FULL_COMMAND"), rs.getString("TIME"), rs.getString("IP")));
                i++;
            }
            rs.close();
            statement.close();
            db.close();
            return commandHist;
        } catch (Exception e) {
            LogHelper.error("Error Occurred parsing player logs (" + e.toString() + ")");
            e.printStackTrace();
            return null;
        }
    }

    public static void checkCommandLogs(CommandSourceStack p, String target, int no){
        ArrayList<String> commandList = getFullEntityPlayerLogs(target);
        if (commandList == null){
            //Exception
            TextUtil.sendChatMessage(p, ChatFormatting.RED + "An Error Occured trying to get logs!");
        } else {
            parseMessages(commandList, p, no, target);
        }
    }

    private static void parseMessages(ArrayList<String> stringList, CommandSourceStack CommandSourceStack, int arg, String target){
        int maxPossibleValue = stringList.size();	//Max possible based on stringList
        int maxPossiblePage = (stringList.size() / 10) + 1;
        if (maxPossiblePage < arg){
            TextUtil.sendChatMessage(CommandSourceStack, ChatFormatting.RED + "Max amount of pages is " + maxPossiblePage + ". Please specify a value within that range!");
            return;
        }
        //1 (0-9), 2 (10,19)...
        int minValue = (arg - 1) * 10;
        int maxValue = (arg * 10) - 1;
        TextUtil.sendChatMessage(CommandSourceStack, ChatFormatting.GOLD + "--- Command History For " + target + " Page " + arg + " of " + maxPossiblePage + " ---");
        if (maxValue > maxPossibleValue) {	//Exceeds
            for (int i = minValue; i < stringList.size(); i++){
                TextUtil.sendChatMessage(CommandSourceStack, stringList.get(i));
            }
        } else {
            for (int i = minValue; i <= maxValue; i++) {
                TextUtil.sendChatMessage(CommandSourceStack, stringList.get(i));
            }
        }
        TextUtil.sendChatMessage(CommandSourceStack, ChatFormatting.GOLD + "-------------------------------------------");
    }

    public static void checkCommandStats(CommandSourceStack p, String target, UUID uuid){
        int commands = getCount(target);
        if (commands == -2){
            TextUtil.sendChatMessage(p, ChatFormatting.RED + "An Error Occured trying to convert command usage count!");
            return;
        } else if (commands == -1){
            TextUtil.sendChatMessage(p, ChatFormatting.RED + "An Error Occured trying to get command usage stats!");
            return;
        }

        //Present them all out
        TextUtil.sendChatMessage(p, ChatFormatting.GOLD + "--- Command Usage Statistics ---");
        TextUtil.sendChatMessage(p, ChatFormatting.GOLD + "Name: " + ChatFormatting.RESET + target);
        TextUtil.sendChatMessage(p, ChatFormatting.GOLD + "UUID: " + ChatFormatting.RESET + uuid);
        TextUtil.sendChatMessage(p, ChatFormatting.GOLD + "Command Usage Counts: " + ChatFormatting.RESET + commands);
        TextUtil.sendChatMessage(p, ChatFormatting.GOLD + "-----------------------------");
    }

    @Nonnull
    public static ArrayList<String> getPlayerNames() {
        Connection db = getSQLiteDBConnection();
        if (db == null) {
            LogHelper.error("Unable to get list of player names from DB");
            return new ArrayList<>();
        }

        Statement statement;
        String querySQL = "SELECT DISTINCT NAME FROM COMMANDS;";

        try {
            db.setAutoCommit(false);
            statement = db.createStatement();
            ResultSet rs = statement.executeQuery(querySQL);
            ArrayList<String> playerList = new ArrayList<>();
            while (rs.next()){
                playerList.add(rs.getString("NAME"));
            }
            rs.close();
            statement.close();
            db.close();
            return playerList;
        } catch (Exception e) {
            LogHelper.error("Error Occurred getting player names (" + e.toString() + ")");
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
