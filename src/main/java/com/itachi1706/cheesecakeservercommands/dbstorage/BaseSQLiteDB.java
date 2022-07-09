package com.itachi1706.cheesecakeservercommands.dbstorage;

import com.itachi1706.cheesecakeservercommands.CheesecakeServerCommands;
import com.itachi1706.cheesecakeservercommands.util.LogHelper;
import com.itachi1706.cheesecakeservercommands.util.TextUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;

import java.io.File;
import java.sql.*;
import java.util.List;

public abstract class BaseSQLiteDB {
    private final String dbName;

    protected static final String ERR_STACK_TRACE = "Stack Trace: ";
    protected static final String QUERY_AND_UUID = "' OR UUID='";

    protected BaseSQLiteDB(String dbName) {
        this.dbName = dbName;
    }

    protected Connection getSQLiteDBConnection() {
        Connection c;
        try {
            c = DriverManager.getConnection("jdbc:sqlite:" + CheesecakeServerCommands.getConfigFileDirectory().getAbsolutePath() + File.separator + this.dbName + ".db");
        } catch (Exception e) {
            LogHelper.error("Error creating DB Connection", e);
            LogHelper.error(e.getClass().getName() + ": " + e.getMessage());

            return null;
        }
        return c;
    }

    public abstract void checkTablesExists();
    protected abstract void parseMessages(List<String> stringList, CommandSourceStack sender, int arg, String target);
    protected void parseMessages(List<String> stringList, CommandSourceStack sender, int arg, String target, String title) {
        int maxPossibleValue = stringList.size();	//Max possible based on stringList
        int maxPossiblePage = (stringList.size() / 10) + 1;
        if (maxPossiblePage < arg){
            TextUtil.sendChatMessage(sender, ChatFormatting.RED + "Max amount of pages is " + maxPossiblePage + ". Please specify a value within that range!");
            return;
        }
        //1 (0-9), 2 (10,19)...
        int minValue = (arg - 1) * 10;
        int maxValue = (arg * 10) - 1;
        TextUtil.sendChatMessage(sender, ChatFormatting.GOLD + TextUtil.centerText(" " + title + " For " + target + " Page " + arg + " of " + maxPossiblePage + " ", '-'));
        if (maxValue > maxPossibleValue) {	//Exceeds
            for (int i = minValue; i < stringList.size(); i++){
                TextUtil.sendChatMessage(sender, stringList.get(i));
            }
        } else {
            for (int i = minValue; i <= maxValue; i++) {
                TextUtil.sendChatMessage(sender, stringList.get(i));
            }
        }
        TextUtil.sendChatMessage(sender, ChatFormatting.GOLD + TextUtil.generateChatBreaks('-'));
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    protected boolean createTable(Connection db, String createSql, String tableName) {
        if (db == null){
            LogHelper.error("Unable to create table " + tableName + " as database connection failed");
            return false;
        }

        try (Statement statement = db.createStatement()) {
            statement.executeUpdate(createSql);
            db.close();
        } catch (SQLException e) {
            LogHelper.error(ERR_STACK_TRACE, e);
            LogHelper.error("Unable to check table " + tableName + " exists");
            return false;
        }
        return true;
    }

    protected void insertRecord(Connection db, String insertSql, String errorMsg) {
        try (Statement stmt = db.createStatement()) {
            db.setAutoCommit(false);
            stmt.executeUpdate(insertSql);
            db.commit();
            db.close();
        } catch (SQLException e) {
            LogHelper.error(ERR_STACK_TRACE, e);
            LogHelper.error(errorMsg);
        }
    }

    protected void deleteRecord(Connection db, String deleteSql) throws SQLException {
        try (Statement statement = db.createStatement()) {
            db.setAutoCommit(false);
            statement.executeUpdate(deleteSql);
            db.commit();
            db.close();
        }
    }

    protected int getCount(Connection db, String statementString) throws SQLException {
        int count = 0;
        try (Statement statement = db.createStatement()) {
            db.setAutoCommit(false);
            try (ResultSet rs = statement.executeQuery(statementString)) {
                while (rs.next()){
                    String tmp = rs.getString(1);
                    if (tmp != null){
                        count = Integer.parseInt(tmp);
                    }
                }
            }
            db.close();
        } catch (SQLException e) {
            throw e;
        } catch (NumberFormatException e){
            LogHelper.error(e.toString());
            LogHelper.error(ERR_STACK_TRACE, e);
            return -2;
        } catch (Exception e) {
            LogHelper.error(e.toString());
            LogHelper.error(ERR_STACK_TRACE, e);
            return -1;
        }

        return count;
    }
}
