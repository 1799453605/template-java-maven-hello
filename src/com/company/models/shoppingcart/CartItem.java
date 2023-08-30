package com.company.models.shoppingcart;
public class CartItem {
    private final int productId;
    private final String productName;
    private final double productPrice;
    private int productQuantity;

    public CartItem(int productId, String productName, int productQuantity, double productPrice) {
        this.productId = productId;
        this.productName = productName;
        this.productQuantity = productQuantity;
        this.productPrice = productPrice;
    }

    public int getProductId() {
        return this.productId;
    }

    public String getProductName() {
        return this.productName;
    }

    public int getQuantity() {
        return this.productQuantity;
    }

    public double getProductPrice() {
        return this.productPrice;
    }

    public void setQuantity(int productQuantity) {
        this.productQuantity = productQuantity;
    }

    public void printCartItemInfo() {
        System.out.println("商品编号: " + productId);
        System.out.println("商品名称：" + productName);
        System.out.println("数量：" + productQuantity);
        System.out.println("单价：" + productPrice);
    }
}
