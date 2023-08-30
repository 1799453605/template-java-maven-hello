package com.company.models.purchasehistory;

import com.company.models.shoppingcart.CartItem;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class PurchaseHistoryItem {
    private final String purchaseDate;
    private final String productsInfoJson;
    private final ArrayList<CartItem> productsInfo;
    private final double totalPrice;

    public PurchaseHistoryItem(String purchaseDate, String productsInfoJson, double totalPrice) {
        this.purchaseDate = purchaseDate;
        this.productsInfoJson = productsInfoJson;
        this.productsInfo = processJsonString(productsInfoJson);
        this.totalPrice = totalPrice;
    }

    public PurchaseHistoryItem(String purchaseDate, ArrayList<CartItem> productsInfo, double totalPrice) {
        this.purchaseDate = purchaseDate;
        this.productsInfo = productsInfo;
        this.productsInfoJson = toJsonString(productsInfo);
        this.totalPrice = totalPrice;
    }

    private ArrayList<CartItem> processJsonString(String productsInfoJson) {
        JSONArray productsInfoArray = new JSONArray(productsInfoJson);
        ArrayList<CartItem> productsInfo = new ArrayList<>();
        for (int i = 0; i < productsInfoArray.length(); i++) {
            JSONObject productInfoJsonObject = productsInfoArray.getJSONObject(i);
            productsInfo.add(new CartItem(
                    productInfoJsonObject.getInt("product_id"),
                    productInfoJsonObject.getString("product_name"),
                    productInfoJsonObject.getInt("quantity"),
                    productInfoJsonObject.getDouble("price")
            ));
        }
        return productsInfo;
    }

    public String toJsonString(ArrayList<CartItem> productsInfo) {
        StringBuilder productInfoJson = new StringBuilder();
        productInfoJson.append("[");
        for (CartItem item : productsInfo) {
            if (productInfoJson.length() > 1) {
                productInfoJson.append(",");
            }
            productInfoJson.append("{\"product_id\":").append(item.getProductId())
                    .append(",\"product_name\":\"").append(item.getProductName())
                    .append("\",\"quantity\":").append(item.getQuantity())
                    .append(",\"price\":").append(item.getProductPrice())
                    .append("}");
        }
        productInfoJson.append("]");
        return productInfoJson.toString();
    }

    public ArrayList<CartItem> getProductsInfo() {
        return productsInfo;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public String getProductsInfoJson() {
        return productsInfoJson;
    }
}
