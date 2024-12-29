package com.example.kmu_second_handmarketplace.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.Pair;

import com.example.kmu_second_handmarketplace.Oder.Order;

import java.util.ArrayList;
import java.util.List;

public class OrderManager {

    private DatabaseHelper databaseHelper;

    public OrderManager(Context context) {
        this.databaseHelper = new DatabaseHelper(context);
    }

    // 保存订单到数据库
    public boolean saveOrder(Order order) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_ORDER_PRODUCT_ID, order.getProductId()); // 使用 int 类型的商品 ID
            values.put(DatabaseHelper.COLUMN_ORDER_BUYER_NAME, order.getBuyerName());
            values.put(DatabaseHelper.COLUMN_ORDER_DATE, order.getOrderDate());
            values.put(DatabaseHelper.COLUMN_ORDER_STATUS, order.getOrderStatus());
            values.put(DatabaseHelper.COLUMN_ORDER_PRODUCT_NAME, order.getProductName()); // 添加商品名称
            values.put(DatabaseHelper.COLUMN_ORDER_PRODUCT_PRICE, order.getProductPrice()); // 添加商品价格
            values.put(DatabaseHelper.COLUMN_ORDER_PRODUCT_IMAGE_RES_ID, order.getProductImageResId()); // 添加商品图片资源 ID

            long result = db.insert(DatabaseHelper.ORDERS_TABLE, null, values);
            if (result != -1) {
                db.setTransactionSuccessful();
                return true;
            } else {
                Log.e("OrderManager", "订单插入失败。Result: " + result);
                return false;
            }
        } catch (Exception e) {
            Log.e("OrderManager", "保存订单出错: " + e.getMessage());
            return false;
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    // 获取特定订单
    public Order getOrder(String orderId) {
        // 您可以根据需要实现从数据库获取特定订单的功能
        return null; // 暂时返回 null
    }

    // 根据买家姓名获取已购买的商品 ID 和订单 ID 列表
    public List<Pair<String, Integer>> getPurchasedOrderAndProductIdsByBuyer(String buyerName) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        List<Pair<String, Integer>> orderAndProductIds = new ArrayList<>();

        // 查询订单表，获取符合买家姓名的订单中的所有订单 ID 和商品 ID
        String orderQuery = "SELECT " + DatabaseHelper.COLUMN_ORDER_ID + ", " + DatabaseHelper.COLUMN_ORDER_PRODUCT_ID +
                " FROM " + DatabaseHelper.ORDERS_TABLE +
                " WHERE " + DatabaseHelper.COLUMN_ORDER_BUYER_NAME + " = ?";
        Cursor orderCursor = null;
        try {
            orderCursor = db.rawQuery(orderQuery, new String[]{buyerName});

            if (orderCursor != null && orderCursor.moveToFirst()) {
                do {
                    // 提取订单 ID 和商品 ID
                    String orderId = orderCursor.getString(orderCursor.getColumnIndex(DatabaseHelper.COLUMN_ORDER_ID));
                    int productId = orderCursor.getInt(orderCursor.getColumnIndex(DatabaseHelper.COLUMN_ORDER_PRODUCT_ID));
                    Log.d("OrderManager", "Found Order ID: " + orderId + ", Product ID: " + productId);

                    // 将订单 ID 和商品 ID 添加到列表中
                    orderAndProductIds.add(new Pair<>(orderId, productId));
                } while (orderCursor.moveToNext());
            } else {
                Log.d("OrderManager", "No orders found for buyer: " + buyerName);
            }
        } catch (Exception e) {
            Log.e("OrderManager", "查询订单时发生错误: " + e.getMessage());
        } finally {
            if (orderCursor != null) {
                orderCursor.close(); // 关闭游标
            }
            db.close(); // 关闭数据库连接
        }

        Log.d("OrderManager", "Total orders and products found: " + orderAndProductIds.size());
        return orderAndProductIds; // 返回订单和商品 ID 列表
    }

    // 获取所有订单
    public List<Order> getAllOrders() {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        List<Order> orderList = new ArrayList<>();

        // 查询所有订单
        String query = "SELECT * FROM " + DatabaseHelper.ORDERS_TABLE;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String orderId = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ORDER_ID));
                    int productId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ORDER_PRODUCT_ID)); // 使用 int 类型
                    String buyerName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ORDER_BUYER_NAME));
                    String orderDate = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ORDER_DATE));
                    String orderStatus = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ORDER_STATUS));

                    // 获取商品名称、价格、图片资源 ID 等信息
                    String productName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ORDER_PRODUCT_NAME));
                    String productPrice = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ORDER_PRODUCT_PRICE));
                    String productImageResId = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ORDER_PRODUCT_IMAGE_RES_ID));

                    // 创建 Order 对象并添加到列表
                    Order order = new Order(orderId, productId, buyerName, orderDate, orderStatus, productName, productPrice, productImageResId);
                    orderList.add(order);
                } while (cursor.moveToNext());
            } else {
                Log.d("OrderManager", "订单表中没有任何记录。");
            }
        } catch (Exception e) {
            Log.e("OrderManager", "查询所有订单时发生错误: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close(); // 关闭游标
            }
            db.close(); // 关闭数据库连接
        }

        return orderList;
    }

    // 日志输出所有订单内容
    public void logAllOrders() {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Log.d("OrderManager", "开始打印订单表内容：");

        String query = "SELECT * FROM " + DatabaseHelper.ORDERS_TABLE;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String orderId = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ORDER_ID));
                    int productId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ORDER_PRODUCT_ID)); // 使用 int 类型
                    String buyerName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ORDER_BUYER_NAME));
                    String orderDate = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ORDER_DATE));
                    String orderStatus = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ORDER_STATUS));

                    Log.d("OrderManager", "订单记录: " +
                            "Order ID: " + orderId +
                            ", Product ID: " + productId +
                            ", Buyer Name: " + buyerName +
                            ", Order Date: " + orderDate +
                            ", Status: " + orderStatus);
                } while (cursor.moveToNext());
            } else {
                Log.d("OrderManager", "订单表中没有任何记录。");
            }
        } catch (Exception e) {
            Log.e("OrderManager", "查询所有订单时发生错误: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close(); // 关闭游标
            }
            db.close(); // 关闭数据库连接
        }
    }

    // 清空订单表中的所有记录
    public void clearOrdersTable() {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        try {
            db.execSQL("DELETE FROM " + DatabaseHelper.ORDERS_TABLE);  // 删除所有数据
            Log.d("OrderManager", "All orders have been deleted.");
        } catch (Exception e) {
            Log.e("OrderManager", "Error clearing orders table: " + e.getMessage());
        } finally {
            db.close();  // 确保关闭数据库连接
        }
    }

    // 获取所有订单作为列表
    public List<Order> getAllOrdersAsList() {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        List<Order> orderList = new ArrayList<>();

        // 查询订单表的所有数据
        String query = "SELECT * FROM " + DatabaseHelper.ORDERS_TABLE;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    // 提取订单信息
                    String orderId = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ORDER_ID));
                    int productId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ORDER_PRODUCT_ID));
                    String buyerName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ORDER_BUYER_NAME));
                    String orderDate = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ORDER_DATE));
                    String orderStatus = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ORDER_STATUS));
                    String productName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ORDER_PRODUCT_NAME));
                    String productPrice = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ORDER_PRODUCT_PRICE));
                    String productImageResId = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ORDER_PRODUCT_IMAGE_RES_ID));

                    // 创建 Order 对象并添加到列表
                    Order order = new Order(orderId, productId, buyerName, orderDate, orderStatus, productName, productPrice, productImageResId);
                    orderList.add(order);
                } while (cursor.moveToNext());
            } else {
                Log.d("OrderManager", "订单表中没有任何记录。");
            }
        } catch (Exception e) {
            Log.e("OrderManager", "查询所有订单时发生错误: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close(); // 关闭游标
            }
            db.close(); // 关闭数据库连接
        }

        return orderList;
    }

    // 删除订单
    public boolean deleteOrder(String orderId) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        boolean result = false;

        try {
            // 执行删除操作，依据订单ID删除对应记录
            int rowsAffected = db.delete(DatabaseHelper.ORDERS_TABLE,
                    DatabaseHelper.COLUMN_ORDER_ID + " = ?",
                    new String[]{orderId});

            // 判断删除是否成功
            if (rowsAffected > 0) {
                result = true;
                Log.d("OrderManager", "订单删除成功，订单号: " + orderId);
            } else {
                Log.d("OrderManager", "订单删除失败，未找到订单号: " + orderId);
            }
        } catch (Exception e) {
            Log.e("OrderManager", "删除订单时发生错误: " + e.getMessage());
        } finally {
            db.close(); // 关闭数据库连接
        }

        return result;
    }
}
