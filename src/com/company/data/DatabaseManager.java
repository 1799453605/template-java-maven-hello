package com.company.data;

import java.sql.*;

public class DatabaseManager {
    private static final String DATABASE_URL = "jdbc:sqlite:shopping_system.db";
    private Connection connection;

    public void initializeDatabase() {
        try {
            // 创建或连接到数据库
            connection = DriverManager.getConnection(DATABASE_URL);
            createTables();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTables() {
        createAdminTable();
        createUserTable();
        createProductTable();
        createShoppingCartTable();
        createPurchaseHistoryTable();
    }

    private void createAdminTable() {
        // 创建管理员表
        String createAdminTableQuery = "CREATE TABLE IF NOT EXISTS admin (" +
                "username TEXT PRIMARY KEY, " +
                "password TEXT)";
        executeUpdate(createAdminTableQuery);

        // 如果库中没有账号，则创建管理员账号，用户名和密码直接保存在数据库中
        String checkAccountQuery = "SELECT COUNT(*) FROM admin";
        ResultSet resultSet = executeQuery(checkAccountQuery);
        try {
            // 检查结果集中的第一行第一列的值
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                if (count <= 0) {
                    //数据库中没有账号，则创建新账号
                    String createAdminAccount = "INSERT INTO admin (username, password) VALUES ('admin', 'ynuinfo#777')";
                    executeUpdate(createAdminAccount);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // 关闭结果集
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void createUserTable() {
        // 创建用户表
        String createUserTableQuery = "CREATE TABLE IF NOT EXISTS user (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT, " +
                "password TEXT, " +
                "login_attempts INTEGER DEFAULT 0, " +
                "account_locked BOOLEAN DEFAULT 0, " +
                "level TEXT DEFAULT 'bronze', " +
                "registration_date TEXT, " +
                "total_spent REAL DEFAULT 0, " +
                "phone TEXT, " +
                "email TEXT)";
        executeUpdate(createUserTableQuery);
    }

    private void createProductTable() {
        // 创建商品表
        String createProductTableQuery = "CREATE TABLE IF NOT EXISTS product (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "manufacturer TEXT, " +
                "production_date TEXT, " +
                "model TEXT, " +
                "purchase_price REAL, " +
                "price REAL, " +
                "stock INTEGER)";
        executeUpdate(createProductTableQuery);
    }

    private void createShoppingCartTable() {
        // 创建购物车表
        String createCartTableQuery = "CREATE TABLE IF NOT EXISTS cart (" +
                "cart_id INTEGER PRIMARY KEY, " +
                "username TEXT, " +
                "product_id INTEGER, " +
                "product_name TEXT, " +
                "quantity INTEGER, " +
                "price REAL, " +
                "FOREIGN KEY (username) REFERENCES user(username), " +
                "FOREIGN KEY (product_id) REFERENCES product(id))";
        executeUpdate(createCartTableQuery);
    }

    private void createPurchaseHistoryTable() {
        // 创建购物历史记录表
        String createPurchaseHistoryTableQuery = "CREATE TABLE IF NOT EXISTS purchase_history (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT, " +
                "purchase_date TEXT, " +
                "products_info TEXT, " +
                "total_price REAL, " +
                "FOREIGN KEY(username) REFERENCES user(username))";
        executeUpdate(createPurchaseHistoryTableQuery);
    }

    //使用SELECT语句时使用executeQuery方法返回结果集
    public ResultSet executeQuery(String query) {
        try {
            Statement statement = connection.createStatement();
            return statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    //使用INSERT, UPDATE 和 DELETE 语句时使用executeUpdate方法
    public int executeUpdate(String query) {
        try {
            Statement statement = connection.createStatement();
            return statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void closeDatabase() {
        try {
            if (connection != null)
                connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}