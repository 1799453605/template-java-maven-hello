package com.company;

import com.company.data.DatabaseManager;
import com.company.menu.*;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        DatabaseManager databaseManager = new DatabaseManager();
        databaseManager.initializeDatabase();
        SystemMenu system = new SystemMenu(scanner, databaseManager);
        System.out.println("欢迎使用购物管理系统！");
        system.run();
        databaseManager.closeDatabase();
    }
}
