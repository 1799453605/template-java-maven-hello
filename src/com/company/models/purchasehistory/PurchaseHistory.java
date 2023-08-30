package com.company.models.purchasehistory;

import com.company.data.DatabaseManager;
import com.company.models.shoppingcart.CartItem;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PurchaseHistory {
    private final String username;
    private final ArrayList<PurchaseHistoryItem> purchaseHistoryItems;

    private final DatabaseManager databaseManager;

    public PurchaseHistory(String username, DatabaseManager databaseManager) {
        this.username = username;
        this.purchaseHistoryItems = new ArrayList<>();
        this.databaseManager = databaseManager;
        try {
            String checkPurchaseHistoryQuery = "SELECT purchase_date, products_info, total_price FROM purchase_history WHERE username = '" + username + "' ORDER BY purchase_date";
            ResultSet resultSet = databaseManager.executeQuery(checkPurchaseHistoryQuery);
            while (resultSet.next()) {
                purchaseHistoryItems.add(new PurchaseHistoryItem(
                        resultSet.getString("purchase_date"),
                        resultSet.getString("products_info"),
                        resultSet.getDouble("total_price")));
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 更新数据函数，需要用到数据库

    public void addPurChaseHistory(PurchaseHistoryItem purchaseHistoryItem) {
        this.purchaseHistoryItems.add(purchaseHistoryItem);
        String insertHistoryQuery = "INSERT INTO purchase_history (username, purchase_date, products_info, total_price) " +
                "VALUES ('" + this.username + "', '" +
                purchaseHistoryItem.getPurchaseDate() + "', '" +
                purchaseHistoryItem.getProductsInfoJson() + "', " +
                purchaseHistoryItem.getTotalPrice() + ")";
        databaseManager.executeUpdate(insertHistoryQuery);
    }

    // 辅助函数，无需用到数据库

    public boolean isEmpty() {
        return purchaseHistoryItems.isEmpty();
    }

    public void printPurchaseHistory() {
        System.out.println("购物历史：\n" +
                "----------------------------");
        for (PurchaseHistoryItem purchaseHistoryItem : this.purchaseHistoryItems) {
            System.out.println("购买时间：" + purchaseHistoryItem.getPurchaseDate());
            System.out.println("商品清单：");
            for (CartItem cartItem : purchaseHistoryItem.getProductsInfo()) {
                System.out.println("——商品编号：" + cartItem.getProductId() +
                        "， 商品名称：" + cartItem.getProductName() +
                        "， 商品单价：" + cartItem.getProductPrice() +
                        "， 购买数量：" + cartItem.getQuantity());
            }
            System.out.println("合计：" + purchaseHistoryItem.getTotalPrice() + " 元\n" +
                    "----------------------------");
        }
    }
}
