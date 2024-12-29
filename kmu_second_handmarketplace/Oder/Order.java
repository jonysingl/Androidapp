package com.example.kmu_second_handmarketplace.Oder;

public class Order {
    private String  orderId;         // 订单ID
    private int productId;       // 商品ID，外键关联到商品表
    private String buyerName;       // 买家名称
    private String orderDate;       // 订单日期（字符串格式的时间）
    private String orderStatus;     // 订单状态（例如："Pending", "Paid", "Cancelled"）

    // 新增字段
    private String productName;     // 商品名称
    private String productPrice;    // 商品价格
    private String productImageResId;  // 商品图片资源ID（现在是String类型）

    // 构造函数
    public Order(String orderId, int productId, String buyerName, String orderDate, String orderStatus,
                 String productName, String productPrice, String productImageResId) {
        this.orderId = orderId;
        this.productId = productId;
        this.buyerName = buyerName;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productImageResId = productImageResId;  // Now accepting String type for image resource ID
    }

    // Getter 和 Setter 方法
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int  productId) {
        this.productId = productId;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    // 新增 Getter 和 Setter 方法
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductImageResId() {
        return productImageResId;
    }

    public void setProductImageResId(String productImageResId) {
        this.productImageResId = productImageResId;  // Now accepting String type for image resource ID
    }

    // 重写 toString 方法，方便打印订单信息
    @Override
    public String toString() {
        return "Order{" +
                "orderId='" + orderId + '\'' +
                ", productId='" + productId + '\'' +
                ", buyerName='" + buyerName + '\'' +
                ", orderDate='" + orderDate + '\'' +
                ", orderStatus='" + orderStatus + '\'' +
                ", productName='" + productName + '\'' +
                ", productPrice='" + productPrice + '\'' +
                ", productImageResId='" + productImageResId + '\'' +
                '}';
    }
}
