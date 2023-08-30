package com.company.models.shoppingcart;

import com.company.data.DatabaseManager;
import com.company.models.Product;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ShoppingCart {
    private final DatabaseManager databaseManager;
    private final String username;
    private ArrayList<CartItem> cartItems;
    private double totalPrice;

    public ShoppingCart(String username, DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
        this.username = username;
        this.cartItems = new ArrayList<>();
        try {
            String query = "SELECT product_name, product_id, quantity, price FROM cart WHERE username = '" + username + "'";
            ResultSet resultSet = databaseManager.executeQuery(query);

            //计算购物车总价
            double totalPrice = 0.0;
            while (resultSet.next()) {
                cartItems.add(new CartItem(resultSet.getInt("product_id"),
                        resultSet.getString("product_name"),
                        resultSet.getInt("quantity"),
                        resultSet.getDouble("price")));
                int productQuantity = resultSet.getInt("quantity");
                double productPrice = resultSet.getDouble("price");
                totalPrice += productQuantity * productPrice;
            }
            this.totalPrice = totalPrice;
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 更新数据函数，需要用到数据库

    public boolean addProduct(int productId, int productQuantity) {
        Product product = new Product(productId, databaseManager);
        if (!isEmpty()) {
            int index = getCartItemIndex(productId);
            if (index > 0) {
                // 如果购物车中已有该商品，则更新商品数量和价格
                return setPurchaseQuantity(productId, productQuantity + cartItems.get(index).getQuantity());
            }
        }
        // 如果购物车中没有该商品，则插入新记录
        this.totalPrice += productQuantity * product.getProductPrice();
        String updateQuery = "INSERT INTO cart (username, product_id, product_name, quantity, price) VALUES " +
                "('" + username + "', " + productId + ", '" + product.getProductName() + "', " + productQuantity + ", " + product.getProductPrice() + ")";
        cartItems.add(new CartItem(productId, product.getProductName(), productQuantity, product.getProductPrice()));
        return databaseManager.executeUpdate(updateQuery) > 0;
    }

    public boolean setPurchaseQuantity(int productId, int productQuantity) {
        int index = getCartItemIndex(productId);
        totalPrice += cartItems.get(index).getProductPrice() * (productQuantity - cartItems.get(index).getQuantity());
        cartItems.get(index).setQuantity(productQuantity);
        String updateQuery = "UPDATE cart SET quantity = " + cartItems.get(index).getQuantity() +
        " WHERE username = '" + username + "' AND product_id = " + productId;
        return databaseManager.executeUpdate(updateQuery) > 0;
    }

    public boolean removeFromCart(int productId) {
        int index = getCartItemIndex(productId);
        totalPrice -= cartItems.get(index).getProductPrice() * cartItems.get(index).getQuantity();
        cartItems.remove(getCartItemIndex(productId));
        String removeQuery = "DELETE FROM cart WHERE username = '" + username + "' AND product_id = " + productId;
        return databaseManager.executeUpdate(removeQuery) > 0;
    }

    public void clearCart() {
        for (CartItem item : cartItems) {
            String removeQuery = "DELETE FROM cart WHERE username = '" + username + "' AND product_id = " + item.getProductId();
            databaseManager.executeUpdate(removeQuery);
        }
        totalPrice = 0.0;
        cartItems = new ArrayList<>();
    }

    // 辅助函数，没有用到数据库

    public void printCart() {
        System.out.println("\n购物车：");
        System.out.println("---------------------------");
        for (CartItem item : cartItems){
            item.printCartItemInfo();
            System.out.println("---------------------------");
        }
        System.out.println("合计：" + this.totalPrice + " 元\n");
    }

    public boolean isEmpty() {
        return cartItems.isEmpty();
    }

    public ArrayList<CartItem> getShoppingCart() {
        return cartItems;
    }

    public int getCartItemIndex(int productId) {
        int index = 0;
        for (CartItem item : cartItems) {
            if (item.getProductId() == productId)
                return index;
            else
                index++;
        }
        return -1;
    }

    public CartItem getCartItem(int index) {
        return this.cartItems.get(index);
    }

    public double getTotalPrice() {
        return this.totalPrice;
    }

}
