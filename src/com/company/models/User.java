package com.company.models;

import com.company.data.DatabaseManager;
import com.company.menu.CommandMenu;
import com.company.models.purchasehistory.PurchaseHistory;
import com.company.models.purchasehistory.PurchaseHistoryItem;
import com.company.models.shoppingcart.CartItem;
import com.company.models.shoppingcart.ShoppingCart;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class User {
    private int id;
    private String username;
    private String password;
    private int loginAttempts;
    private int accountLocked;
    private String level;
    private String registrationDate;
    private double totalSpent;
    private String phone;
    private String email;

    private ShoppingCart shoppingCart;
    private PurchaseHistory purchaseHistory;

    private Scanner scanner = null;
    private final DatabaseManager databaseManager;
    private CommandMenu subMenu = null;

    public User(DatabaseManager databaseManagers) {
        this.databaseManager = databaseManagers;
    }

    public User(Scanner scanner, DatabaseManager databaseManager, CommandMenu subMenu) {
        this.scanner = scanner;
        this.databaseManager = databaseManager;
        this.subMenu = subMenu;
    }

    // 更新数据函数
    public boolean existUserName(String username) {
        String checkUserQuery = "SELECT * FROM user WHERE username = '" + username + "'";
        ResultSet checkResult = databaseManager.executeQuery(checkUserQuery);
        try {
            if (checkResult != null && checkResult.next()) {
                this.username = username;
                checkResult.close();
                return true;
            } else
                return false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean exitUserID(int id) {
        String checkUserQuery = "SELECT * FROM user WHERE id = " + id;
        ResultSet checkResult = databaseManager.executeQuery(checkUserQuery);
        try {
            if (checkResult != null && checkResult.next()) {
                this.id = id;
                checkResult.close();
                return true;
            } else
                return false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void getUserInfoName(String username) {
        try {
            String query = "SELECT * FROM user WHERE username = '" + username + "'";
            ResultSet resultSet = databaseManager.executeQuery(query);
            if (resultSet != null && resultSet.next()) {
                this.id = resultSet.getInt("id");
                this.username = username;
                this.password = resultSet.getString("password");
                this.loginAttempts = resultSet.getInt("login_attempts");
                this.accountLocked = resultSet.getInt("account_locked");
                this.level = resultSet.getString("level");
                this.registrationDate = resultSet.getString("registration_date");
                this.totalSpent = resultSet.getDouble("total_spent");
                this.phone = resultSet.getString("phone");
                this.email = resultSet.getString("email");
                this.shoppingCart = new ShoppingCart(username, databaseManager);
                this.purchaseHistory = new PurchaseHistory(username, databaseManager);
                resultSet.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void getUserInfoID(int id) {
        try {
            String query = "SELECT * FROM user WHERE id = " + id;
            ResultSet resultSet = databaseManager.executeQuery(query);
            if (resultSet != null && resultSet.next()) {
                this.id = id;
                this.username = resultSet.getString("username");
                this.password = resultSet.getString("password");
                this.loginAttempts = resultSet.getInt("login_attempts");
                this.accountLocked = resultSet.getInt("account_locked");
                this.level = resultSet.getString("level");
                this.registrationDate = resultSet.getString("registration_date");
                this.totalSpent = resultSet.getDouble("total_spent");
                this.phone = resultSet.getString("phone");
                this.email = resultSet.getString("email");
                this.shoppingCart = new ShoppingCart(username, databaseManager);
                this.purchaseHistory = new PurchaseHistory(username, databaseManager);
                resultSet.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean updatePassword(String password) {
        this.password = password;
        String query = "UPDATE user SET password = '" + this.password + "' WHERE username = '" + this.username + "'";
        return databaseManager.executeUpdate(query) > 0;
    }

    public void updateLoginAttempts(int loginAttempts) {
        this.loginAttempts = loginAttempts;
        String query = "UPDATE user SET login_attempts = " + this.loginAttempts + " WHERE username = '" + this.username + "'";
        databaseManager.executeUpdate(query);
    }

    public void updateAccountLocked(int accountLocked) {
        this.accountLocked = accountLocked;
        String query = "UPDATE user SET account_locked = " + this.accountLocked + " WHERE username = '" + this.username + "'";
        databaseManager.executeUpdate(query);
    }

    private void updateLevel(String level) {
        this.level = level;
        String query = "UPDATE user SET level = '" + this.level + "' WHERE username = '" + this.username + "'";
        databaseManager.executeUpdate(query);
    }

    private void updateTotalSpent(double totalSpent) {
        this.totalSpent = totalSpent;
        String query = "UPDATE user SET total_spent = " + this.totalSpent + " WHERE username = '" + this.username + "'";
        databaseManager.executeUpdate(query);
    }

    public boolean deleteUserInfo() {
        String deleteShoppingCartQuery = "DELETE FROM cart WHERE username = '" + username + "'";
        databaseManager.executeUpdate(deleteShoppingCartQuery);
        String deletePurchaseHistoryQuery = "DELETE FROM purchase_history WHERE username = '" + username + "'";
        databaseManager.executeUpdate(deletePurchaseHistoryQuery);
        String deleteUserQuery = "DELETE FROM user WHERE id = " + id;
        return databaseManager.executeUpdate(deleteUserQuery) > 0;
    }

    // 功能函数

    public int register() {
        String username;
        String password;
        String phone;
        String email;

        System.out.print("请输入用户名 > ");
        username = scanner.nextLine();

        if (existUserName(username)) {
            System.out.println("错误：用户名已存在！");
            return 1;
        }

        // 验证用户名长度
        if (username.length() < 5) {
            System.out.println("错误：用户名长度应不少于5个字符！");
            return 1;
        }

        System.out.print("请输入密码 > ");
        password = scanner.nextLine();
        // 验证密码长度和格式
        if (isInvalidPassword(password)) {
            System.out.println("错误：密码长度应大于8个字符，并且必须包含大小写字母、数字和标点符号。");
            return 1;
        }

        String hashedPassword = hashPassword(password);

        System.out.print("请输入手机号 > ");
        phone = scanner.nextLine();
        // 检查电话号码格式
        if (!phone.matches("\\d{11}")) {
            System.out.println("错误：手机号格式错误！");
            return 1;
        }

        System.out.print("请输入邮箱 > ");
        email = scanner.nextLine();
        if ((email == null)
                || (email.isEmpty())
                || (!email.matches("[A-Z0-9a-z_]+@[A-Z0-9a-z_]+(\\.[A-Z0-9a-z]+)+"))) {
            System.out.println("错误：邮箱格式错误！");
            return 1;
        }

        // 执行注册操作
        String registrationDate = getCurrentTime();
        String insertCustomerQuery = "INSERT INTO user (phone, email, username, password, registration_date) VALUES ('" + phone + "', '" + email + "', '" + username + "', '" + hashedPassword + "', '" + registrationDate + "')";
        databaseManager.executeUpdate(insertCustomerQuery);

        System.out.println("提示：注册成功，请登录！");

        return 1;
    }

    public int login() {
        System.out.print("请输入用户名 > ");
        String username = scanner.nextLine();
        System.out.print("请输入密码 > ");
        String password = hashPassword(scanner.nextLine());

        //如果结果集不为空，且存在匹配的结果
        if (existUserName(username)) {
            getUserInfoName(username);
            if (Objects.equals(password, this.password) && this.accountLocked == 0) {
                System.out.println("提示：登录成功！");
                updateLoginAttempts(0);
                if (this.subMenu.run() == 0) {
                    return 1;
                }
            } else if (this.accountLocked != 0) {
                System.out.println("错误：账号已锁定，请联系管理员重置密码解锁！");
                return 1;
            } else {
                System.out.println("错误：密码错误！");
                if (++ this.loginAttempts >= 5) {
                    updateAccountLocked(1);
                    System.out.println("错误：账号已锁定，请联系管理员重置密码解锁！");
                } else {
                    System.out.println("提示：还有" + (5 - this.loginAttempts) + "次尝试机会。");
                }
                updateLoginAttempts(this.loginAttempts);
                return 1;
            }
        } else {
            System.out.println("错误：查无用户名为 " + username + " 的用户，请检查用户名！");
        }

        return 1;
    }

    public int changePassword() {
        System.out.print("请输入原密码 > ");
        String oldPassword = hashPassword(scanner.nextLine());

        if (oldPassword != null) {
            // 检查原密码是否匹配
            if (Objects.equals(oldPassword, this.password)) {
                // 原密码匹配，执行修改密码操作
                System.out.print("请输入新密码 > ");
                String newPassword = scanner.nextLine();

                // 验证密码长度和格式
                if (isInvalidPassword(newPassword)) {
                    System.out.println("错误：密码长度应大于8个字符，并且必须包含大小写字母、数字和标点符号。");
                    return 1;
                }

                newPassword = hashPassword(newPassword);
                if (oldPassword.equals(newPassword)) {
                    System.out.println("错误：新密码不能与原密码相同！");
                    return 1;
                }

                if (updatePassword(newPassword)) {
                    System.out.println("提示：密码修改成功！");
                    return 1;
                } else {
                    System.out.println("错误：密码修改失败！");
                }

            } else {
                System.out.println("错误：原密码错误！");
            }
        } else {
            System.out.println("错误：原密码错误！");
        }


        return 1;
    }

    public int resetPassword() {
        System.out.print("请输入用户名 > ");
        String confirmUsername = scanner.nextLine();
        System.out.print("请输入注册使用的邮箱地址 > ");
        String confirmEmail = scanner.nextLine();

        if (existUserName(username)) {
            getUserInfoName(confirmUsername);
            if (confirmEmail.equals(this.email)) {
                String newPassword = generateRandomPassword();
                if (updatePassword(hashPassword(newPassword))) {
                    // 调用发送邮件的方法
                    System.out.println("提示：重置后的密码已发送至邮箱，请查收邮件获取密码。");
                    System.out.println("提示：(由于为模拟重置密码功能，未实际发送邮件，所以直接显示重置后的密码。)\n" +
                            "重置后的密码为 " + newPassword);
                    updateLoginAttempts(0);
                    updateAccountLocked(0);
                } else {
                    System.out.println("错误：密码重置失败！");
                }
            } else {
                System.out.println("错误：邮箱输入错误！");
            }

        } else {
            System.out.println("错误：用户不存在，请检查用户名！");
        }
        return 1;
    }

    public int displayShoppingCart() {
        if(shoppingCart.isEmpty())
            System.out.println("购物车为空！");
        else
            shoppingCart.printCart();
        return 1;
    }

    public int addToCart() {
        System.out.print("请输入要加入购物车的商品编号 > ");
        int productID = scanner.nextInt();
        scanner.nextLine();
        Product product = new Product(databaseManager);
        // 搜索商品表中对应项
        if (product.existProduct(productID)) {
            product.getProductInfo(productID);
            System.out.println("\n商品信息：");
            System.out.println("-----------------------------");
            product.printProductInfo();
            System.out.println("-----------------------------");

            int productStock = product.getProductStock();
            if (productStock <= 0) {
                System.out.println("提示：该商品暂时无货，无法添加到购物车。");
            } else {
                System.out.print("请输入要购买的商品数量（最多 " + productStock + " 件） > ");
                int productQuantity = scanner.nextInt();
                scanner.nextLine();

                if (productQuantity > productStock) {
                    System.out.println("提示：购买数量超出库存，已将最大数量 " + productStock + " 件商品添加到购物车。");
                    productQuantity = productStock;
                } else if (productQuantity < 1) {
                    System.out.println("错误：购买数量不合法，添加购物车失败。");
                    return 1;
                }

                if (shoppingCart.addProduct(productID, productQuantity))
                    System.out.println("提示：加入购物车成功！已将 " + productQuantity + " 件商品添加到购物车。");
                else
                    System.out.println("错误：加入购物车时出错！");
            }
        } else {
            System.out.println("错误：查无匹配的商品，请检查编号是否正确！");
        }
        return 1;
    }

    public int removeFromCart() {
        System.out.print("请输入要移除的商品编号 > ");
        int productId = scanner.nextInt();
        scanner.nextLine();
        Product product = new Product(productId, databaseManager);
        if (product.existProduct(productId)) {
            System.out.println("警告：请确认是否继续移除操作。");
            if (this.confirmMenu()) {
                if (shoppingCart.removeFromCart(productId)) {
                    System.out.println("提示：商品已成功从购物车中移除。");
                } else {
                    System.out.println("错误：商品移除失败！");
                }
            } else {
                System.out.println("提示：已取消移除操作。");
            }
        } else {
            System.out.println("错误：购物车中不存在该商品！");
        }

        return 1;
    }

    public int modifyCart() {
        System.out.print("请输入要修改的商品编号 > ");
        int productId = scanner.nextInt();
        scanner.nextLine();

        int index = shoppingCart.getCartItemIndex(productId);
            if (index != -1) {
                CartItem cartItem = shoppingCart.getCartItem(index);
                Product product = new Product(productId, databaseManager);
                if (product.getProductStock() <= 0) {
                    System.out.println("提示：该商品暂时无货，无法添加到购物车。");
                } else {
                    System.out.println("提示：当前商品库存为 " + product.getProductStock() + " 件。");
                    System.out.print("请输入增加或减少的数量（正数代表增加，负数代表减少） > ");
                    int modifyQuantity = scanner.nextInt();
                    scanner.nextLine();
                    int newQuantity = cartItem.getQuantity() + modifyQuantity;
                    if (newQuantity <= 0) {
                        //修改后的商品数量小于等于0时，将该商品从购物车中清除
                        System.out.println("提示：修改后的商品数量小于或等于0，已将该商品从购物车中移除。");
                        shoppingCart.removeFromCart(productId);
                    } else {
                        if (newQuantity > product.getProductStock()) {
                            //修改后的数量大于库存时，修改为最大数量（即剩余库存）
                            System.out.println("提示：修改后的商品数量超出库存，已修改为最大数量 " + product.getProductStock() + " 件。");
                            newQuantity = product.getProductStock();
                        } else {
                            System.out.println("提示：商品数量修改成功！");
                        }
                        shoppingCart.setPurchaseQuantity(productId, newQuantity);
                    }
                }

            } else {
                //如果购物车中无该商品，则返回
                System.out.println("错误：购物车中无该商品！");
            }

        return 1;
    }

    public int checkout() {
        if (shoppingCart.isEmpty()) {
            System.out.println("提示：购物车为空，请添加商品！");
            return 1;
        }

        ArrayList<CartItem> cartItems = shoppingCart.getShoppingCart();
        for (CartItem cartItem : cartItems) {
            int productId = cartItem.getProductId();
            String productName = cartItem.getProductName();

            Product product = new Product(databaseManager);
            // 仓库中有该商品
            if (product.existProduct(productId)) {
                product.getProductInfo(productId);
                int purchaseQuantity = cartItem.getQuantity();
                int productStock = product.getProductStock();
                // 购买数量小于等于0
                if (purchaseQuantity < 1) {
                    System.out.println("提示：商品 " + productName + " 购买数量小于等于0，已从购物车中移除。");
                    shoppingCart.removeFromCart(productId);
                    continue;
                } else if (productStock < 1) {
                    // 商品无货
                    System.out.println("提示：商品 " + productName + " 无货，已从购物车中移除。");
                    shoppingCart.removeFromCart(productId);
                    continue;
                } else if (productStock < purchaseQuantity) {
                    // 购买数量超过商品库存
                    System.out.println("提示：商品 " + productName + " 库存少于 " + purchaseQuantity + " 件，已修改为最大数量 " + productStock + " 件。");
                    purchaseQuantity = productStock;
                    shoppingCart.setPurchaseQuantity(productId, purchaseQuantity);
                }
                product.getProductInfo(productId);
                product.updateStock(productStock - purchaseQuantity);
            } else {
                //仓库中无该商品
                System.out.println("提示：商品 " + productName + " 已下架，已从购物车中移除。");
                shoppingCart.removeFromCart(productId);
            }
        }

        System.out.print("\n-------------------\n" +
                "支付渠道：\n" +
                "-------------------\n" +
                "1. 支付宝\n" +
                "2. 微信支付\n" +
                "3. 银行卡\n" +
                "-------------------\n" +
                "请选择支付渠道 > ");
        String paymentChannels = scanner.nextLine();
        switch (paymentChannels) {
            case "1":
                //调用支付宝结账方法
                break;
            case "2":
                //调用微信支付结账方法
                break;
            case "3":
                //调用银行卡结账方法
                break;
            default:
                //输入选项错误
                System.out.println("错误：请输入正确选项！");
                return 1;
        }

        System.out.println("提示：购买成功！");

        // 创建购物历史记录
        PurchaseHistoryItem purchaseHistoryItem = new PurchaseHistoryItem(getCurrentTime(),
                shoppingCart.getShoppingCart(),
                shoppingCart.getTotalPrice());
        purchaseHistory.addPurChaseHistory(purchaseHistoryItem);
        shoppingCart.clearCart();
        //更新用户级别
        this.totalSpent += shoppingCart.getTotalPrice();
        updateTotalSpent(this.totalSpent);
        // 累计消费金额100为银牌客户，累计消费1000为金牌客户
        if (totalSpent >= 100.0 && this.level.equals("bronze")) {
            System.out.println("提示：您已升级为银牌客户！");
            updateLevel("silver");
        } else if (this.totalSpent >= 1000.0 && this.level.equals("silver")) {
            System.out.println("提示：您已升级为金牌客户！");
            updateLevel("gold");
        }
        return 1;
    }

    public int viewShoppingHistory() {
        if (purchaseHistory.isEmpty()) {
            System.out.println("提示：购物历史为空！");
        } else {
            purchaseHistory.printPurchaseHistory();
        }
        return 1;
    }

    // 其他辅助函数

    public void printUserInfo() {
        System.out.println("客户ID：" + id);
        System.out.println("用户名：" + username);
        System.out.println("用户级别：" + switchLevel(level));
        System.out.println("注册时间：" + registrationDate);
        System.out.println("累计消费总金额：" + totalSpent);
        System.out.println("手机号：" + phone);
        System.out.println("邮箱：" + email);
    }

    private boolean isInvalidPassword(String password) {
        // 密码必须包含大小写字母、数字和标点符号
        return (password.length() <= 8 || !password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()-_+=<>?.,]).+$"));
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

    private String getCurrentTime() {
        // 获取当前时间
        long currentTimeMillis = System.currentTimeMillis();
        Timestamp timestamp = new Timestamp(currentTimeMillis);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(timestamp);
    }

    private String switchLevel(String level) {
        switch (level) {
            case "gold":
                return "金牌客户";
            case "silver":
                return "银牌客户";
            default:
                return "铜牌客户";
        }
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
