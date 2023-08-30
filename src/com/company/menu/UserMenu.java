package com.company.menu;

import com.company.data.DatabaseManager;
import java.util.Scanner;
import com.company.models.User;

public class UserMenu extends CommandMenu{
    private final CommandMenu passwordMenu;
    private final CommandMenu shoppingCartMenu;

    public UserMenu(Scanner scanner, DatabaseManager databaseManager) {
        super("      用户菜单      ", scanner);

        //登录成功后的子菜单
        CommandMenu subMenu = new CommandMenu("      用户菜单      ", scanner);
        subMenu.addMenuItem("密码管理","1");
        subMenu.registerCommand("1", this::enterPasswordMenu);
        subMenu.addMenuItem("购物","2");
        subMenu.registerCommand("2", this::enterShoppingCartMenu);
        subMenu.addMenuItem("退出登录","3");
        subMenu.registerCommand("3", this::logout);

        User user = new User(scanner, databaseManager, subMenu);
        addMenuItem("注册","1");
        registerCommand("1", user::register);
        addMenuItem("登录","2");
        registerCommand("2", user::login);
        addMenuItem("忘记密码","3");
        registerCommand("3", user::resetPassword);
        addMenuItem("返回上一级菜单", "4");
        registerCommand("4", this::exitSubMenu);


        this.passwordMenu = new CommandMenu("     密码管理     ", scanner);
        this.passwordMenu.addMenuItem("修改密码", "1");
        this.passwordMenu.registerCommand("1", user::changePassword);
        this.passwordMenu.addMenuItem("重置密码", "2");
        this.passwordMenu.registerCommand("2", user::resetPassword);
        this.passwordMenu.addMenuItem("返回上一级菜单", "3");
        this.passwordMenu.registerCommand("3", this::exitSubMenu);

        this.shoppingCartMenu = new CommandMenu("      购物车      ", scanner);
        this.shoppingCartMenu.addMenuItem("查看购物车", "1");
        this.shoppingCartMenu.registerCommand("1", user::displayShoppingCart);
        this.shoppingCartMenu.addMenuItem("搜索商品并加入购物车", "2");
        this.shoppingCartMenu.registerCommand("2", user::addToCart);
        this.shoppingCartMenu.addMenuItem("移除商品", "3");
        this.shoppingCartMenu.registerCommand("3", user::removeFromCart);
        this.shoppingCartMenu.addMenuItem("修改商品", "4");
        this.shoppingCartMenu.registerCommand("4", user::modifyCart);
        this.shoppingCartMenu.addMenuItem("结账", "5");
        this.shoppingCartMenu.registerCommand("5", user::checkout);
        this.shoppingCartMenu.addMenuItem("查看购物历史", "6");
        this.shoppingCartMenu.registerCommand("6", user::viewShoppingHistory);
        this.shoppingCartMenu.addMenuItem("返回上一级菜单", "7");
        this.shoppingCartMenu.registerCommand("7", this::exitSubMenu);
    }

    protected int enterPasswordMenu() {
        if (this.passwordMenu.run() == 0) {
            return 1;
        }
        return -1;
    }

    protected int enterShoppingCartMenu() {
        if (this.shoppingCartMenu.run() == 0) {
            return 1;
        }
        return -1;
    }

    protected int logout() {
        System.out.println("提示：退出登录成功！");
        return 0;
    }

    protected int exitSubMenu() {
        return 0;
    }

}
