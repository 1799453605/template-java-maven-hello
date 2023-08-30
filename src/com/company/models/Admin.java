package com.company.models;

import com.company.data.DatabaseManager;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Admin {
    private final String username;
    private final Scanner scanner;
    private final DatabaseManager databaseManager;

    public Admin(String username, Scanner scanner, DatabaseManager databaseManager) {
        this.username = username;
        this.scanner = scanner;
        this.databaseManager = databaseManager;
    }

    // 功能函数

    public int changePassword() {
        System.out.print("请输入原密码 > ");
        String oldPassword = scanner.nextLine();
        // 查询当前密码
        String getPasswordQuery = "SELECT password FROM admin WHERE username = '" + username + "'";
        ResultSet resultSet = databaseManager.executeQuery(getPasswordQuery);

        try {
            if (resultSet.next()) {
                String savedPassword = resultSet.getString("password");

                // 检查原密码是否匹配
                if (oldPassword.equals(savedPassword)) {
                    // 原密码匹配，执行修改密码操作
                    System.out.print("请输入修改后的密码 > ");
                    String newPassword = scanner.nextLine();

                    if (oldPassword.equals(newPassword)) {
                        System.out.println("错误：新密码不能与原密码相同！");
                        return 1;
                    } else {
                        String updatePasswordQuery = "UPDATE admin SET password = '" + newPassword + "' WHERE username = '" + username + "'";
                        int rowsAffected = databaseManager.executeUpdate(updatePasswordQuery);
                        if (rowsAffected > 0) {
                            System.out.println("提示：密码修改成功！");
                        } else {
                            System.out.println("错误：密码修改失败！");
                        }
                    }

                } else {
                    System.out.println("错误：原密码不匹配，密码修改失败！");
                    return 1;
                }
            } else {
                System.out.println("错误：管理员账号错误！");
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

    public int resetUserPassword() {
        System.out.print("请输入要重置密码的用户名 > ");
        String resetUsername = scanner.nextLine();
        User user = new User(databaseManager);
        if (user.existUserName(resetUsername)) {
            String newPassword = generateRandomPassword();
            if (user.updatePassword(hashPassword(newPassword))) {
                // 调用发送邮件的方法
                System.out.println("提示：重置后的密码已发送至用户邮箱，请提醒用户查看邮件。");
                System.out.println("测试信息:用户的密码为 " +  newPassword + " (用作测试）");
                user.updateAccountLocked(0);
                user.updateLoginAttempts(0);
            } else {
                System.out.println("错误：密码重置失败！");
            }
        } else {
            System.out.println("错误：用户不存在，请检查用户名！");
        }
        return 1;

    }

    public int listCustomers() {
        try {
            String query = "SELECT * FROM user";
            ResultSet resultSet = databaseManager.executeQuery(query);
            if (resultSet != null && resultSet.next()) {
                System.out.println("以下为所有客户信息：");
                System.out.println("-----------------------------");
                do {
                    User user = new User(databaseManager);
                    user.getUserInfoName(resultSet.getString("username"));
                    user.printUserInfo();
                    System.out.println("-----------------------------");
                } while (resultSet.next());
                resultSet.close();
            } else {
                System.out.println("暂无客户信息！");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 1;
    }

    public int deleteCustomer() {
        System.out.print("请输入要删除的客户编号 > ");
        int userIdToDelete = scanner.nextInt();
        scanner.nextLine();
        User user = new User(databaseManager);

        if (user.exitUserID(userIdToDelete)) {
            System.out.println("警告：请确认是否继续删除操作。");
            if (confirmMenu()) {
                user.getUserInfoID(userIdToDelete);
                if (user.deleteUserInfo()) {
                    System.out.println("提示：成功删除客户信息！");
                } else {
                    System.out.println("错误：删除客户信息失败！");
                }
            } else {
                System.out.println("提示：已取消删除操作。");
            }
        } else {
            System.out.println("错误：未找到匹配的客户信息！");
        }

        return 1;
    }

    public int searchCustomer() {
        System.out.print("请选择查询方式：\n" +
                "1. 按客户ID查询\n" +
                "2. 按用户名查询\n" +
                "3. 查询所有用户信息\n" +
                "请输入选项 > ");
        String choice = scanner.nextLine();
        User user = new User(databaseManager);
        switch (choice) {
            case "1" :
                System.out.print("请输入客户ID > ");
                int userID = scanner.nextInt();
                scanner.nextLine();
                if (user.exitUserID(userID)) {
                    user.getUserInfoID(userID);
                    System.out.println("客户信息如下：");
                    System.out.println("-----------------------------");
                    user.printUserInfo();
                    System.out.println("-----------------------------");
                } else {
                    System.out.println("错误：未找到客户");
                }
                break;
            case "2" :
                System.out.print("请输入客户用户名 > ");
                String username = scanner.nextLine();
                if (user.existUserName(username)) {
                    user.getUserInfoName(username);
                    System.out.println("客户信息如下：");
                    System.out.println("-----------------------------");
                    user.printUserInfo();
                    System.out.println("-----------------------------");
                } else {
                    System.out.println("错误：未找到客户");
                }
                break;
            case "3" :
                listCustomers();
                return 1;
            default:
                System.out.println("错误：请输入正确选项！");
                return 1;
        }

        return 1;
    }

    public int listProducts() {
        try {
            String query = "SELECT id FROM product";
            ResultSet resultSet = databaseManager.executeQuery(query);
            if (resultSet != null && resultSet.next()) {
                Product product = new Product(databaseManager);
                System.out.println("以下为所有商品信息：");
                System.out.println("-----------------------------");
                do {
                    if (product.getProductInfo(resultSet.getInt("id"))) {
                        product.printProductInfo();
                        System.out.println("-----------------------------");
                    } else {
                    System.out.println("错误：编号为 " + resultSet.getInt("id") + " 的商品出错！");
                    }
                } while (resultSet.next());
                resultSet.close();
            } else {
                System.out.println("错误：暂无商品信息！");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 1;
    }

    public int addProduct() {
        System.out.print("请输入商品名称 > ");
        String productName = scanner.nextLine();
        System.out.print("请输入生产厂家 > ");
        String manufacturer = scanner.nextLine();
        System.out.print("请输入生产日期 > ");
        String productionDate = scanner.nextLine();
        System.out.print("请输入商品型号 > ");
        String model = scanner.nextLine();
        System.out.print("请输入商品的进货价 > ");
        double purchasePrice = scanner.nextDouble();
        System.out.print("请输入商品的零售价 > ");
        double productPrice = scanner.nextDouble();
        System.out.print("请输入商品库存 > ");
        int productStock = scanner.nextInt();
        Product product = new Product(productName, manufacturer, productionDate, model,
        purchasePrice, productPrice, productStock, databaseManager);
        if (product.saveProductInfo()) {
            System.out.println("提示：商品信息添加成功！");
        } else {
            System.out.println("错误：商品信息添加失败！");
        }
        return 1;
    }

    public int modifyProduct() {
        System.out.print("请输入要修改的商品的编号 > ");
        int productId = scanner.nextInt();
        // 消耗多余的换行符
        scanner.nextLine();

        Product product = new Product(databaseManager);
        if (product.existProduct(productId)) {
            product.getProductInfo(productId);
            System.out.print("商品信息：\n" +
                    "1. 商品名称\n" +
                    "2. 生产厂家\n" +
                    "3. 生产日期\n" +
                    "4. 型号\n" +
                    "5. 进货价\n" +
                    "6. 零售价\n" +
                    "7. 商品库存\n" +
                    "请输入要修改的商品信息项 > ");
            String infoItem = scanner.nextLine();
            boolean updateResult;
            switch (infoItem) {
                case "1":
                    System.out.print("请输入商品名称 > ");
                    updateResult = product.updateName(scanner.nextLine());
                    break;
                case "2" :
                    System.out.print("请输入生产产家 > ");
                    updateResult = product.updateManufacturer(scanner.nextLine());
                    break;
                case "3" :
                    System.out.print("请输入生产日期 > ");
                    updateResult = product.updateProductionDate(scanner.nextLine());
                    break;
                case "4" :
                    System.out.print("请输入型号 > ");
                    updateResult = product.updateModel(scanner.nextLine());
                    break;
                case "5" :
                    System.out.print("请输入进货价 > ");
                    updateResult = product.updatePurchasePrice(scanner.nextDouble());
                    // 消耗多余的换行符
                    scanner.nextLine();
                    break;
                case "6":
                    System.out.print("请输入商品零售价 > ");
                    updateResult = product.updatePrice(scanner.nextDouble());
                    // 消耗多余的换行符
                    scanner.nextLine();
                    break;
                case "7":
                    System.out.print("请输入商品库存 > ");
                    updateResult = product.updateStock(scanner.nextInt());
                    // 消耗多余的换行符
                    scanner.nextLine();
                    break;
                default:
                    System.out.println("错误：选项输入错误！");
                    return 1;
            }

            if (updateResult) {
                System.out.println("提示：商品信息修改成功！");
            } else {
                System.out.println("错误：商品信息修改失败！");
            }
        } else {
        System.out.println("错误：找无指定的商品！");
        }
        return 1;
    }

    public int deleteProduct() {
        System.out.print("请输入要删除的商品的商品编号 > ");
        int productId = scanner.nextInt();
        scanner.nextLine();
        // 查询商品是否存在
        Product product = new Product(databaseManager);
        if (product.existProduct(productId)) {
            System.out.println("警告：删除后无法恢复，请确认是否继续删除操作。");
            if (confirmMenu()) {
                if (product.deleteProduct(productId)) {
                    System.out.println("提示：商品信删除成功！");
                } else {
                    System.out.println("错误：商品信息删除失败！");
                }
            }
            else {
                System.out.println("提示：已取消删除操作。");
            }
        } else {
            System.out.println("错误：找不到指定的商品！");
        }
        return 1;
    }

    public int searchProduct() {
        String query;
        System.out.print("查询方式：\n" +
                "1. 单独查询\n" +
                "2. 组合查询\n" +
                "请选择查询方式 > ");
        String searchChoice = scanner.nextLine();
        switch (searchChoice) {
            case "1":
                System.out.print("查询方式：\n" +
                        "1. 商品名称\n" +
                        "2. 生产厂家\n" +
                        "3. 零售价格\n" +
                        "请选择查询方式 > ");
                String separateChoice = scanner.nextLine();
                switch (separateChoice) {
                    case "1":
                        System.out.print("请输入商品名称 > ");
                        String productName = scanner.nextLine();
                        query = "SELECT * FROM product WHERE name = " + "'" + productName + "'";
                        break;
                    case "2":
                        System.out.print("请输入生产厂家 > ");
                        String manufacturer = scanner.nextLine();
                        query = "SELECT * FROM product WHERE manufacturer = " + "'" + manufacturer + "'";
                        break;
                    case "3":
                        System.out.print("请输入零售价格 > ");
                        double productPrice = scanner.nextDouble();
                        scanner.nextLine();
                        query = "SELECT * FROM product WHERE price >= " + productPrice;
                        break;
                    default:
                        System.out.println("错误：选项输入错误！");
                        return 1;
                }
                break;
            case "2":
                System.out.print("查询方式：\n" +
                        "1. 商品名称 + 生产厂家\n" +
                        "2. 商品名称 + 零售价格\n" +
                        "3. 生产厂家 + 零售价格\n" +
                        "4. 商品名称 + 生产厂家 + 零售价格\n" +
                        "请选择查询方式 > ");
                String combinationChoice = scanner.nextLine();
                switch (combinationChoice) {
                    case "1":
                        System.out.print("请输入商品名称 > ");
                        String productName = scanner.nextLine();
                        System.out.print("请输入生产厂家 > ");
                        String manufacturer = scanner.nextLine();
                        query = "SELECT * FROM product WHERE name = " + "'" + productName + "' AND manufacturer = '" + manufacturer + "'";
                        break;
                    case "2":
                        System.out.print("请输入商品名称 > ");
                        productName = scanner.nextLine();
                        System.out.print("请输入零售价格 > ");
                        double productPrice = scanner.nextDouble();
                        scanner.nextLine();
                        query = "SELECT * FROM product WHERE name = '" + productName + "' AND price >= " + productPrice;
                        break;
                    case "3":
                        System.out.print("请输入生产厂家 > ");
                        manufacturer = scanner.nextLine();
                        System.out.print("请输入零售价格 > ");
                        productPrice = scanner.nextDouble();
                        scanner.nextLine();
                        query = "SELECT * FROM product WHERE manufacturer = '" + manufacturer + "' AND price >= " + productPrice;
                        break;
                    case "4":
                        System.out.print("请输入商品名称 > ");
                        productName = scanner.nextLine();
                        System.out.print("请输入生产厂家 > ");
                        manufacturer = scanner.nextLine();
                        System.out.print("请输入零售价格 > ");
                        productPrice = scanner.nextDouble();
                        scanner.nextLine();
                        query = "SELECT * FROM product WHERE name = '" + productName + "' AND manufacturer = '" + manufacturer + "' AND price >= " + productPrice;
                        break;
                    default:
                        System.out.println("错误：选项输入错误！");
                        return 1;
                }
                break;
            default:
                System.out.println("错误：选项输入错误！");
                return 1;
        }

        try {
            ResultSet resultSet = databaseManager.executeQuery(query);
            if (resultSet != null && resultSet.next()) {
                System.out.println("\n以下为匹配的商品信息：");
                System.out.println("-----------------------------");
                do {
                    int productId = resultSet.getInt("id");
                    String productName = resultSet.getString("name");
                    String manufacturer = resultSet.getString("manufacturer");
                    String productionDate = resultSet.getString("production_date");
                    String model = resultSet.getString("model");
                    double purchasePrice = resultSet.getDouble("purchase_price");
                    double productPrice = resultSet.getDouble("price");
                    int productStock = resultSet.getInt("stock");


                    System.out.println("商品编号: " + productId);
                    System.out.println("商品名称: " + productName);
                    System.out.println("生产厂家：" + manufacturer);
                    System.out.println("生产日期：" + productionDate);
                    System.out.println("型号：" + model);
                    System.out.println("进货价: " + purchasePrice);
                    System.out.println("零售价：" + productPrice);
                    System.out.println("库存: " + productStock);
                    System.out.println("-----------------------------");
                } while (resultSet.next());
                resultSet.close();
            } else {
                System.out.println("提示：查无匹配的商品。");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 1;
    }

    // 其他辅助函数

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(password.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte hashByte : hashBytes) {
                String hex = Integer.toHexString(0xff & hashByte);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String generateRandomPassword() {
        // 生成随机密码
        String LOWERCASE_LETTERS = "abcdefghijklmnopqrstuvwxyz";
        String UPPERCASE_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String DIGITS = "0123456789";
        String SPECIAL_CHARACTERS = "!@#$%^&*()-_+=<>?.,";
        SecureRandom random = new SecureRandom();
        int passwordLength = random.nextInt(5) + 8; // 随机生成8到12之间的密码长度
        StringBuilder passwordBuilder = new StringBuilder();
        String allCharacters = LOWERCASE_LETTERS + UPPERCASE_LETTERS + DIGITS + SPECIAL_CHARACTERS;

        passwordBuilder.append(LOWERCASE_LETTERS.charAt(random.nextInt(LOWERCASE_LETTERS.length())));
        passwordBuilder.append(UPPERCASE_LETTERS.charAt(random.nextInt(UPPERCASE_LETTERS.length())));
        passwordBuilder.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        passwordBuilder.append(SPECIAL_CHARACTERS.charAt(random.nextInt(SPECIAL_CHARACTERS.length())));

        for (int i = 4; i < passwordLength; i++) {
            passwordBuilder.append(allCharacters.charAt(random.nextInt(allCharacters.length())));
        }

        String password = passwordBuilder.toString();
        char[] passwordArray = password.toCharArray();
        for (int i = 0; i < passwordLength; i++) {
            int randomIndex = random.nextInt(passwordLength);
            char temp = passwordArray[i];
            passwordArray[i] = passwordArray[randomIndex];
            passwordArray[randomIndex] = temp;
        }

        return new String(passwordArray);
    }

    private boolean confirmMenu() {
        System.out.print( "================\n"+
                "       确认      \n" +
                "================\n"+
                "1. 是\n" +
                "2. 否\n" +
                "----------------\n" +
                "请输入选项 > ");
        String choice = scanner.nextLine();
        while (true) {
            if (choice.equals("1")) {
                return true;
            } else if (choice.equals("2")) {
                return false;
            }
            System.out.println("错误：选项输入错误！");
            System.out.print("请重新输入选项 > ");
            choice = scanner.nextLine();
        }
    }
}
