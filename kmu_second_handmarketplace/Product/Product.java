package com.example.kmu_second_handmarketplace.Product;

public class Product {
    private int productId;  // 产品 ID, 自动生成
    private String name;
    private String price;
    private String description;
    private byte[] imageResId;  // Store image as byte[]
    private String category;
    private String sellerName;

    // Constructor without productId since it's auto-generated
    public Product(int productId, String name, String price, String description, byte[] imageResId, String category, String sellerName) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.description = description;
        this.imageResId = imageResId;
        this.category = category;
        this.sellerName = sellerName;
    }

    // Getter and Setter methods
    public int getProductId() {
        return productId;  // Return the product ID
    }

    public void setProductId(int productId) {
        this.productId = productId;  // Set the product ID
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public byte[] getImageResId() {
        return imageResId;  // Return the byte array image
    }

    public String getCategory() {
        return category;
    }

    public String getSellerName() {
        return sellerName;
    }
}
