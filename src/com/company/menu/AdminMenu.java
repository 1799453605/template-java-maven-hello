package com.company.menu;

import com.company.models.Admin;
import com.company.data.DatabaseManager;
import java.util.Scanner;

public class AdminMenu extends CommandMenu{
    private final CommandMenu passwordMenu;
    private final CommandMenu customerMenu;
    private final CommandMenu productMenu;

    public AdminMenu(String username, Scanner scanner, DatabaseManager databaseManager) {
        super("      管理员菜单      ", scanner);
        Admin admin = new Admin(username, scanner, databaseManager);
        addMenuItem("密码管理","1");
        registerCommand("1", this::enterPasswordMenu);
        addMenuItem("客户管理","2");
        registerCommand("2", this::enterCustomerMenu);
        addMenuItem("商品管理","3");
        registerCommand("3", this::enterProductMenu);
        addMenuItem("退出登录","4");
        registerCommand("4", this::logout);

        this.passwordMenu = new CommandMenu("      密码管理      ", scanner);
        this.passwordMenu.addMenuItem("修改自身密码", "1");
        this.passwordMenu.registerCommand("1", admin::changePassword);
        this.passwordMenu.addMenuItem("重置用户密码", "2");
        this.passwordMenu.registerCommand("2", admin::resetUserPassword);
        this.passwordMenu.addMenuItem("返回上一级菜单", "3");
        this.passwordMenu.registerCommand("3", this::exitSubMenu);

        this.customerMenu = new CommandMenu("      客户管理      ", scanner);
        this.customerMenu.addMenuItem("列出所有客户信息", "1");
        this.customerMenu.registerCommand("1", admin::listCustomers);
        this.customerMenu.addMenuItem("删除客户信息", "2");
        this.customerMenu.registerCommand("2", admin::deleteCustomer);
        this.customerMenu.addMenuItem("查询客户信息", "3");
        this.customerMenu.registerCommand("3", admin::searchCustomer);
        this.customerMenu.addMenuItem("返回上一级菜单", "4");
        this.customerMenu.registerCommand("4", this::exitSubMenu);

        this.productMenu = new CommandMenu("     商品管理     ", scanner);
        this.productMenu.addMenuItem("列出所有商品信息", "1");
        this.productMenu.registerCommand("1", admin::listProducts);
        this.productMenu.addMenuItem("添加商品信息", "2");
        this.productMenu.registerCommand("2", admin::addProduct);
        this.productMenu.addMenuItem("修改商品信息", "3");
        this.productMenu.registerCommand("3", admin::modifyProduct);
        this.productMenu.addMenuItem("删除商品信息", "4");
        this.productMenu.registerCommand("4", admin::deleteProduct);
        this.productMenu.addMenuItem("查询商品信息", "5");
        this.productMenu.registerCommand("5", admin::searchProduct);
        this.productMenu.addMenuItem("返回上一级菜单", "6");
        this.productMenu.registerCommand("6", this::exitSubMenu);
    }

    private int enterPasswordMenu() {
        if (this.passwordMenu.run() == 0) {
            return 1;
        }
        return -1;
    }

    private int enterCustomerMenu() {
        if (this.customerMenu.run() == 0) {
            return 1;
        }
        return -1;
    }

    private int enterProductMenu() {
        if (this.productMenu.run() == 0) {
            return 1;
        }
        return -1;
    }

    private int logout(){
        System.out.println("提示：退出登录成功！");
        return 0;
    }

    private int exitSubMenu() {
        return 0;
    }
}
