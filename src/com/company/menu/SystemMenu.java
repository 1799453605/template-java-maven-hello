package com.company.menu;

import com.company.data.DatabaseManager;

import java.sql.*;
import java.util.Scanner;

public class SystemMenu extends CommandMenu{
    private final UserMenu userMenu;
    private final DatabaseManager databaseManager;

    public SystemMenu(Scanner scanner, DatabaseManager databaseManager) {
        super("      购物管理系统      ", scanner);
        this.databaseManager = databaseManager;
        this.userMenu = new UserMenu(scanner, databaseManager);

        addMenuItem("进入用户菜单", "1");
        registerCommand("1", this::enterUserMenu);

        addMenuItem("进入管理员菜单", "2");
        registerCommand("2", this::enterAdminMenu);

        addMenuItem("退出系统", "3");
        registerCommand("3", this::exitSystem);
    }

    private int enterUserMenu() {
        if (this.userMenu.run() == 0) {
            return 1;
        }
        return -1;
    }

    private int enterAdminMenu() {
        String username;
        String password;
        System.out.print("请输入用户名 > ");
        username = scanner.nextLine();
        System.out.print("请输入密码 > ");
        password = scanner.nextLine();

        String query = "SELECT * FROM admin WHERE username = '" + username + "' AND password = '" + password + "'";
        ResultSet resultSet = databaseManager.executeQuery(query);

        try {
            //如果结果集不为空，且存在匹配的结果
            if (resultSet != null && resultSet.next()) {
                System.out.println("提示：登录成功！");
                AdminMenu adminMenu = new AdminMenu(username, scanner, databaseManager);
                if (adminMenu.run() == 0) {
                    return 1;
                } else {
                    return -1;
                }
            } else {
                System.out.println("错误：登录失败，请检查用户名和密码！");
                return 1;
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

        return 1;
    }

    private int exitSystem() {
        System.out.println("提示：已退出购物管理系统！");
        return 0;
    }
}
