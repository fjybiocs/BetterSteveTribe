package org.stevetribe.betterstevetribe.utils;

import org.stevetribe.betterstevetribe.BetterSteveTribe;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class SQLiteManager {

    private static final String DATABASE_NAME = "data.db";

    // 初始化连接
    public static Connection initializeConnection() throws SQLException {
        File dataFolder = BetterSteveTribe.getPlugin().getDataFolder();

        // 创建数据库文件路径
        File databaseFile = new File(dataFolder, DATABASE_NAME);

        // 检查数据目录是否存在，不存在则递归创建
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        // 获取数据库文件的绝对路径
        String databasePath = databaseFile.getAbsolutePath();

        // 现在可以使用 databasePath 来设置数据库连接
        String DATABASE_URL = "jdbc:sqlite:" + databasePath;

        return DriverManager.getConnection(DATABASE_URL);
    }

    // 创建表格
    public static void createTables() {
        try (Connection connection = initializeConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "CREATE TABLE IF NOT EXISTS player_data (" +
                             "username TEXT PRIMARY KEY, " +
                             "first_join BIGINT NOT NULL, " +
                             "freshman_teleport_usage_count INTEGER DEFAULT 0)"
             )) {
            statement.executeUpdate();
            // 再创建一张表，记录玩家某天（只具体到天）出售某物品（材质名称）的数量
            PreparedStatement tableSell = connection.prepareStatement("CREATE TABLE IF NOT EXISTS sell_item (" +
                    "username TEXT NOT NULL, " +
                    "date BIGINT NOT NULL, " +
                    "item TEXT NOT NULL, " +
                    "count INTEGER NOT NULL, " +
                    "PRIMARY KEY (username, date, item))");
            tableSell.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 设置第一次加入时间
    public static void setFirstJoinTime(String username, LocalDateTime firstJoinTime) {
        try (Connection connection = initializeConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO player_data (username, first_join) VALUES (?, ?)")) {
            statement.setString(1, username);
            statement.setLong(2, firstJoinTime.toEpochSecond(ZoneOffset.UTC));
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 获取第一次加入时间
    public static LocalDateTime getFirstJoinTime(String username) {
        try (Connection connection = initializeConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT first_join FROM player_data WHERE username = ?")) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return LocalDateTime.ofEpochSecond(resultSet.getLong("first_join"), 0, ZoneOffset.UTC);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 获取道具使用次数
    public static int getFreshmanTeleportUsageCount(String username) {
        try (Connection connection = initializeConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT freshman_teleport_usage_count FROM player_data WHERE username = ?")) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("freshman_teleport_usage_count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // 增加道具使用次数
    public static void incrementFreshmanTeleportUsageCount(String username) {
        try (Connection connection = initializeConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE player_data SET freshman_teleport_usage_count = freshman_teleport_usage_count + 1 WHERE username = ?")) {
            statement.setString(1, username);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 写入玩家出售某物品（材质名称）的记录
    public static void setItemSellRecord(String username, String material, int count, long date) throws SQLException {
        try (Connection connection = initializeConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO sell_item (username, item, count, date) VALUES (?, ?, ?, ?)")) {
            statement.setString(1, username);
            statement.setString(2, material);
            statement.setInt(3, count);
            statement.setLong(4, date);
            statement.executeUpdate();
        }
    }

    // 获取玩家某天（只具体到天）出售某物品的总数
    public static int getItemSellCount(String username, String material, long date) throws SQLException {
        // date是当天任意一个时间，计算这一天开始的时间戳和结束的时间戳
        long start = date - date % 86400000;
        long end = start + 86400000;
        try (Connection connection = initializeConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT SUM(count) AS total FROM sell_item WHERE username = ? AND item = ? AND date >= ? AND date < ?")) {
            statement.setString(1, username);
            statement.setString(2, material);
            statement.setLong(3, start);
            statement.setLong(4, end);

            System.out.println(statement.toString());

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("total");
            } else {
                return 0;
            }
        }
    }

}
