package com.company.menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Scanner;

public class CommandMenu {
    protected final Scanner scanner;
    private final String menuTitle;
    private final ArrayList<MenuItem> menuItems;
    private final HashMap<String, ICommand> actionsMap;

    public CommandMenu(String menu_title, Scanner scanner) {
        this.scanner = scanner;
        this.menuTitle = menu_title;
        this.menuItems = new ArrayList<>();
        this.actionsMap = new HashMap<>();
    }

    //添加菜单选项
    public void addMenuItem(String caption, String key) {
        MenuItem item = new MenuItem(caption, key);
        this.menuItems.add(item);
    }

    //注册选项指令
    public void registerCommand(String key, ICommand cmd) {
        this.actionsMap.put(key.toLowerCase(Locale.ROOT), cmd);
    }


    //打印菜单界面
    protected void printLine(char lineChar) {
        for (int i = 0; i < this.menuTitle.length() + 4; i++)
            System.out.print(lineChar);
        System.out.println();
    }

    protected void printHeadBar() {
        printLine('=');
        System.out.print('|');
        System.out.print(this.menuTitle);
        System.out.print('|');
        System.out.println();
        printLine('=');
    }

    protected void printItems() {
        int i = 1;
        for (MenuItem item : this.menuItems) {
            System.out.println(i + ". " + item.getTitle());
            i++;
        }
        printLine('-');
    }

    //执行输入的命令
    protected int executeAction(String cmdKey) {
        ICommand cmd = this.actionsMap.get(cmdKey);

        if (cmd != null) {
            return cmd.execute();
        }

        return -1;
    }

    protected String getCmdKey() {
        System.out.print("请输入选项 > ");
        String cmd = scanner.next();
        scanner.nextLine();
        return cmd;
    }

    protected void showMenu() {
        printHeadBar();
        printItems();
    }

    public int run() {
        while (true) {
            showMenu();
            String cmd = getCmdKey();
            int state = executeAction(cmd);
            //state 大于0时 代表指令执行成功，需要返回主菜单
            if (state > 0) {
                //提示回到主菜单
                System.out.println("\n提示：按回车键返回菜单。");
                scanner.nextLine();
            }
            //state 等于0时 代表程序正常结束，需要退出程序
            else if (state == 0) {
                break;
            }
            //state 小于0时 代表执行执行异常
            else {
                System.out.println("\n错误：系统执行出错，请重新输入选项！");
            }
        }

        return 0;
    }

}


