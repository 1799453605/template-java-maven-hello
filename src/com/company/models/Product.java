package com.company.models;

import com.company.data.DatabaseManager;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Product {
    private int productId;
    private String productName;
    private String manufacturer;
    private String productionDate;
    private String model;
    private double purchasePrice;
    private double productPrice;
    private int productStock;

    private final DatabaseManager databaseManager;

    public Product(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public Product(int productId, DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
        try {
            String query = "SELECT * FROM product WHERE id = " + productId;
            ResultSet resultSet = databaseManager.executeQuery(query);
            if (resultSet != null && resultSet.next()) {
                String productName = resultSet.getString("name");
                String manufacturer = resultSet.getString("manufacturer");
                String productionDate = resultSet.getString("production_date");
                String model = resultSet.getString("model");
                double purchasePrice = resultSet.getDouble("purchase_price");
                double productPrice = resultSet.getDouble("price");
                int productStock = resultSet.getInt("stock");

                this.productId = productId;
                this.productName = productName;
                this.manufacturer = manufacturer;
                this.productionDate = productionDate;
                this.model = model;
                this.purchasePrice = purchasePrice;
                this.productPrice = productPrice;
                this.productStock = productStock;
                resultSet.close();
            } else {
                System.out.println("\n错误：无编号为 " + productId + " 的商品信息！");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Product(String productName, String manufacturer, String productionDate, String model,
                   double purchasePrice, double productPrice, int productStock, DatabaseManager databaseManager) {
        this.productName = productName;
        this.manufacturer = manufacturer;
        this.productionDate = productionDate;
        this.model = model;
        this.purchasePrice = purchasePrice;
        this.productPrice = productPrice;
        this.productStock = productStock;

        this.databaseManager = databaseManager;
    }

    public boolean existProduct(int productId) {
        try {
            String query = "SELECT * FROM product WHERE id = " + productId;
            ResultSet resultSet = databaseManager.executeQuery(query);
            if (resultSet != null && resultSet.next()) {
                this.productId = productId;
                resultSet.close();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean getProductInfo(int productId) {
        try {
            String query = "SELECT * FROM product WHERE id = " + productId;
            ResultSet resultSet = databaseManager.executeQuery(query);
            if (resultSet != null && resultSet.next()) {
                String productName = resultSet.getString("name");
                String manufacturer = resultSet.getString("manufacturer");
                String productionDate = resultSet.getString("production_date");
                String model = resultSet.getString("model");
                double purchasePrice = resultSet.getDouble("purchase_price");
                double productPrice = resultSet.getDouble("price");
                int productStock = resultSet.getInt("stock");

                this.productId = productId;
                this.productName = productName;
                this.manufacturer = manufacturer;
                this.productionDate = productionDate;
                this.model = model;
                this.purchasePrice = purchasePrice;
                this.productPrice = productPrice;
                this.productStock = productStock;
                resultSet.close();
                return true;
            } else {
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean saveProductInfo() {
        String insertProductQuery = "INSERT INTO product (name, manufacturer, production_date, model, purchase_price, price, stock) VALUES ('" +
                productName + "', '" + manufacturer + "', '" +
                productionDate + "', '" + model + "', " + purchasePrice + ", " +
                productPrice + ", " + productStock + ")";
        return databaseManager.executeUpdate(insertProductQuery) > 0;
    }

    public boolean deleteProduct(int productId) {
        String deleteProductQuery = "DELETE FROM product WHERE id = " + productId;
        return databaseManager.executeUpdate(deleteProductQuery) > 0;
    }


    public boolean updateName(String newProductName) {
        this.productName = newProductName;
        String query = "UPDATE product SET name = '" + newProductName + "' WHERE id = " + this.productId;
        return databaseManager.executeUpdate(query) > 0;
    }

    public boolean updateManufacturer(String newManufacturer) {
        this.manufacturer = newManufacturer;
        String query = "UPDATE product SET manufacturer = '" + newManufacturer + "' WHERE id = " + this.productId;
        return databaseManager.executeUpdate(query) > 0;
    }

    public boolean updateProductionDate(String newProductionDate) {
        this.productionDate = newProductionDate;
        String query = "UPDATE product SET production_date = '" + newProductionDate + "' WHERE id = " + this.productId;
        return databaseManager.executeUpdate(query) > 0;
    }

    public boolean updateModel(String newModel) {
        this.model = newModel;
        String query = "UPDATE product SET model = '" + newModel + "' WHERE id = " + this.productId;
        return databaseManager.executeUpdate(query) > 0;
    }

    public boolean updatePurchasePrice(double newPurchasePrice) {
        this.purchasePrice = newPurchasePrice;
        String query = "UPDATE product SET purchase_price = " + newPurchasePrice + " WHERE id = " + this.productId;
        return databaseManager.executeUpdate(query) > 0;
    }

    public boolean updatePrice(double newPrice) {
        this.productPrice = newPrice;
        String query = "UPDATE product SET price = " + newPrice + " WHERE id = " + this.productId;
        return databaseManager.executeUpdate(query) > 0;
    }

    public boolean updateStock(int newStock) {
        this.productStock = newStock;
        String query = "UPDATE product SET stock = " + newStock + " WHERE id = " + this.productId;
        return databaseManager.executeUpdate(query) > 0;
    }

    // 辅助函数，无需用到数据库

    public String getProductName() {
        return this.productName;
    }

    public int getProductStock() {
        return this.productStock;
    }

    public double getProductPrice() {
        return this.productPrice;
    }

    public void printProductInfo() {
        System.out.println("商品编号: " + this.productId);
        System.out.println("商品名称: " + this.productName);
        System.out.println("生产厂家：" + this.manufacturer);
        System.out.println("生产日期：" + this.productionDate);
        System.out.println("型号：" + this.model);
        System.out.println("进货价: " + this.purchasePrice);
        System.out.println("零售价：" + this.productPrice);
        System.out.println("库存: " + this.productStock);
    }

}
