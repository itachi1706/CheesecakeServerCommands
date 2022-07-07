package com.itachi1706.cheesecakeservercommands.dbstorage;

import com.itachi1706.cheesecakeservercommands.util.LogHelper;
import com.itachi1706.cheesecakeservercommands.util.TextUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;

import javax.annotation.Nonnull;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Kenneth on 9/7/2018.
 * for CheesecakeServerCommands in package com.itachi1706.cheesecakeservercommands.dbstorage
 */
public class CommandsLogDB extends BaseSQLiteDB {
    // TODO: Use https://www.sqlitetutorial.net/sqlite-java/insert/ PreparedStatements instead to prevent SQL Injections

    private static CommandsLogDB instance;

    public CommandsLogDB() {
        super("commands_log");
    }

    public static CommandsLogDB getInstance() {
        if (instance == null) {
            instance = new CommandsLogDB();
        }
        return instance;
    }

    @Override
    public void checkTablesExists(){
        Connection db = getSQLiteDBConnection();
        String createDB = "CREATE TABLE IF NOT EXISTS COMMANDS " +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "NAME varchar(30) not null, " +
                "UUID TEXT NOT NULL, " +
                "TIME TEXT NOT NULL DEFAULT (DATETIME('now')), " +
                "IP VARCHAR(20) NOT NULL, " +
                "COMMAND_BASE TEXT NOT NULL, " +
                "FULL_COMMAND TEXT NOT NULL);";

        if (!createTable(db, createDB, "Commands"))
            LogHelper.error("Commands Log DB fail to ensure that table is created");
    }

    public void addLog(String commandUser, UUID uuid, String commandBase, String commandExecuted, String ip) {
        String insertQuery = "INSERT INTO COMMANDS (NAME,UUID,IP,COMMAND_BASE,FULL_COMMAND) " +
                "VALUES('" + commandUser + "','" + uuid + "','" + ip + "','"
                + commandBase + "','" + commandExecuted.trim() + "');";

        Connection db = getSQLiteDBConnection();
        if (db == null) {
            LogHelper.error("Unable to add log due to failed db connection");
            return;
        }

        insertRecord(db, insertQuery, "Unable to insert command record");
    }

    private int getCommandUsageByPlayerNameOrUuid(String name){
        return getCount(name);
    }

    public int getCommandUsageByPlayerNameOrUuid(UUID uuid){
        return getCommandUsageByPlayerNameOrUuid(uuid.toString());
    }

    private int getCount(@Nonnull String nameOrUuid){
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
            commandUsageCount = getCount(db, queryString);
        } catch (SQLException e) {
            e.printStackTrace();
            LogHelper.error("Unable to get commands usage count for " + nameOrUuid + " from database");
        }

        return commandUsageCount;
    }

    public int getTotalCount(){
        String queryString = "SELECT COUNT(*) FROM COMMANDS;";
        Connection db = getSQLiteDBConnection();
        if (db == null){
            LogHelper.error("Unable to add log due to failed db connection");
            return -1;
        }
        int commandUsageCount = 0;

        try {
            commandUsageCount = getCount(db, queryString);
        } catch (SQLException e) {
            e.printStackTrace();
            LogHelper.error("Unable to get commands usage count from database");
        }

        return commandUsageCount;
    }

    public void deleteLogs(CommandSourceStack sender, String target){
        String sqlQuery = "DELETE FROM COMMANDS WHERE NAME='" + target + "' OR UUID='" + target + "';";
        if (target.equalsIgnoreCase("console") || target.equalsIgnoreCase("server"))
            sqlQuery = "DELETE FROM COMMANDS WHERE IP='localhost';";
        Connection db = getSQLiteDBConnection();
        if (db == null){
            LogHelper.error("Unable to delete logs due to failed db connection");
            return;
        }
        try {
            deleteRecord(db, sqlQuery);
            TextUtil.sendChatMessage(sender, ChatFormatting.GREEN + target + " command usage logs deleted!");
        } catch (Exception e) {
            TextUtil.sendChatMessage(sender, ChatFormatting.RED + "An Error Occured trying to delete logs! (" + e.toString() + ")");
            LogHelper.error("Error occurred deleting logs (" + e.toString() + ")");
            e.printStackTrace();
        }
    }

    private String sendCommands(int no, String name, String uuid, String command, String datetime, String ip){
        //1. name (uuid) executed command at datetime with IP
        return ChatFormatting.GOLD + "" + no + ". " +
                ChatFormatting.AQUA + name + ChatFormatting.RESET + " (" + uuid + ") executed " +
                ChatFormatting.GREEN + "/" + command + ChatFormatting.RESET + " at " +
                ChatFormatting.RESET + "" + ChatFormatting.ITALIC + datetime + " UTC" +
                ChatFormatting.RESET + " with " + ChatFormatting.LIGHT_PURPLE + ip;
    }

    @Nonnull
    private ArrayList<String> getFullEntityPlayerLogs(String target){
        Connection db = getSQLiteDBConnection();
        if (db == null){
            LogHelper.error("Unable to check commands stats due to failed db connection");
            return new ArrayList<>();
        }

        String querySQL = "SELECT NAME,UUID,FULL_COMMAND,TIME,IP FROM COMMANDS WHERE (NAME='" + target + "' OR UUID='" + target + "') ORDER BY TIME DESC;";

        // Check if CONSOLE
        if (target.equalsIgnoreCase("console") || target.equalsIgnoreCase("server"))
            querySQL = "SELECT NAME,UUID,FULL_COMMAND,TIME,IP FROM COMMANDS WHERE (IP='localhost') ORDER BY TIME DESC;";

        try (Statement statement = db.createStatement()) {
            db.setAutoCommit(false);
            ResultSet rs = statement.executeQuery(querySQL);
            int i = 1;
            ArrayList<String> commandHist = new ArrayList<>();
            while (rs.next()){
                commandHist.add(sendCommands(i, rs.getString("NAME"), rs.getString("UUID"), rs.getString("FULL_COMMAND"), rs.getString("TIME"), rs.getString("IP")));
                i++;
            }
            rs.close();
            db.close();
            return commandHist;
        } catch (Exception e) {
            LogHelper.error("Error Occurred parsing player logs (" + e.toString() + ")");
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void checkCommandLogs(CommandSourceStack p, String target, int no){
        ArrayList<String> commandList = getFullEntityPlayerLogs(target);
        if (commandList.isEmpty()){
            //Exception
            TextUtil.sendChatMessage(p, ChatFormatting.RED + "An Error Occured trying to get logs!");
        } else {
            parseMessages(commandList, p, no, target);
        }
    }

    @Override
    protected void parseMessages(List<String> stringList, CommandSourceStack sender, int arg, String target) {
        parseMessages(stringList, sender, arg, target, "Command History");
    }

    public void checkCommandStats(CommandSourceStack p, String target, UUID uuid){
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
    public ArrayList<String> getPlayerNames() {
        Connection db = getSQLiteDBConnection();
        if (db == null) {
            LogHelper.error("Unable to get list of player names from DB");
            return new ArrayList<>();
        }

        String querySQL = "SELECT DISTINCT NAME FROM COMMANDS;";

        try (Statement statement = db.createStatement()) {
            db.setAutoCommit(false);
            ResultSet rs = statement.executeQuery(querySQL);
            ArrayList<String> playerList = new ArrayList<>();
            while (rs.next()){
                playerList.add(rs.getString("NAME"));
            }
            rs.close();
            db.close();
            return playerList;
        } catch (Exception e) {
            LogHelper.error("Error Occurred getting player names (" + e.toString() + ")");
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
