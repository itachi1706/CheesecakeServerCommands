package com.itachi1706.cheesecakeservercommands.dbstorage;

import com.itachi1706.cheesecakeservercommands.util.LogHelper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Kenneth on 9/7/2018.
 * for com.itachi1706.cheesecakeservercommands.dbstorage in CheesecakeServerCommands
 */
public class DBUtils {

    public static boolean createTable(Connection db, String createSql, String tableName) {
        Statement statement;
        if (db == null){
            LogHelper.error("Unable to create table " + tableName + " as database connection failed");
            return false;
        }

        try {
            statement = db.createStatement();
            statement.executeUpdate(createSql);
            statement.close();
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
            LogHelper.error("Unable to check table " + tableName + " exists");
            return false;
        }
        return true;
    }

    public static void insertRecord(Connection db, String insertSql, String errorMsg) {
        Statement stmt;
        try {
            db.setAutoCommit(false);
            stmt = db.createStatement();
            stmt.executeUpdate(insertSql);
            stmt.close();
            db.commit();
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
            LogHelper.error(errorMsg);
        }
    }

    public static void deleteRecord(Connection db, String deleteSql) throws Exception {
        Statement statement;

        try{
            db.setAutoCommit(false);
            statement = db.createStatement();
            statement.executeUpdate(deleteSql);
            db.commit();
            statement.close();
            db.close();
        } catch (Exception e) {
            throw e;
        }
    }

    public static int getCount(Connection db, String statementString) throws SQLException {
        Statement statement;
        int count = 0;
        try {
            db.setAutoCommit(false);
            statement = db.createStatement();
            ResultSet rs = statement.executeQuery(statementString);
            while (rs.next()){
                String tmp = rs.getString(1);
                if (tmp != null){
                    count = Integer.parseInt(tmp);
                }
            }
            rs.close();
            statement.close();
            db.close();
        } catch (SQLException e) {
            throw e;
        } catch (NumberFormatException e){
            LogHelper.error(e.toString());
            e.printStackTrace();
            return -2;
        } catch (Exception e) {
            LogHelper.error(e.toString());
            e.printStackTrace();
            return -1;
        }

        return count;
    }
}
