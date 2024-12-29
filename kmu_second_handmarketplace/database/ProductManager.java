package com.example.kmu_second_handmarketplace.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.Pair;

import com.example.kmu_second_handmarketplace.Oder.Order;
import com.example.kmu_second_handmarketplace.Product.Product;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProductManager {

    private SQLiteDatabase db;
    private DatabaseHelper dbHelper;

    public ProductManager(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    // 将 Bitmap 转换为字节数组
    private byte[] convertBitmapToBytes(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); // 将图像压缩成字节数组
        return baos.toByteArray();
    }

    // 添加商品
// 添加商品
    public long addProduct(String name, String price, String description, byte[] imageResId, String category, String sellerName) {
        db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_PRODUCT_NAME, name);
        values.put(DatabaseHelper.COLUMN_PRODUCT_PRICE, price);
        values.put(DatabaseHelper.COLUMN_PRODUCT_DESCRIPTION, description);

        // 确保上传的图片经过压缩处理，保持 byte[] 类型
        byte[] compressedImage = convertBitmapToBytes(BitmapFactory.decodeByteArray(imageResId, 0, imageResId.length));  // 压缩图片
        values.put(DatabaseHelper.COLUMN_PRODUCT_IMAGE_RES_ID, compressedImage);  // 使用压缩后的图片字节数组

        values.put(DatabaseHelper.COLUMN_PRODUCT_CATEGORY, category);
        values.put(DatabaseHelper.COLUMN_SELLER_NAME, sellerName);

        try {
            // 执行插入操作，获取插入的行的 ID（即 product_id）
            long result = db.insert(DatabaseHelper.PRODUCTS_TABLE, null, values);
            Log.d("ProductManager", "Product added, productId: " + result);
            return result; // 返回生成的 product_id
        } catch (SQLException e) {
            e.printStackTrace();
            return -1; // 插入失败时返回 -1
        } finally {
            db.close(); // 关闭数据库连接
        }
    }

    // 获取所有商品并返回包含卖家昵称的Cursor
    public Cursor getAllProductsWithSeller() {
        db = dbHelper.getReadableDatabase();

        // 查询所有商品并获取卖家信息（使用卖家昵称）
        String query = "SELECT p." + DatabaseHelper.COLUMN_PRODUCT_ID + ", " +
                "p." + DatabaseHelper.COLUMN_PRODUCT_NAME + ", " +
                "p." + DatabaseHelper.COLUMN_PRODUCT_PRICE + ", " +
                "p." + DatabaseHelper.COLUMN_PRODUCT_DESCRIPTION + ", " +
                "p." + DatabaseHelper.COLUMN_PRODUCT_IMAGE_RES_ID + ", " +
                "p." + DatabaseHelper.COLUMN_PRODUCT_CATEGORY + ", " +
                "p." + DatabaseHelper.COLUMN_SELLER_NAME + " " +  // 使用 seller_name 字段
                "FROM " + DatabaseHelper.PRODUCTS_TABLE + " p";

        return db.rawQuery(query, null);  // 返回查询结果的Cursor对象
    }

    // 获取商品的图片（从字节数组转换为 Bitmap）
    public Bitmap getProductImage(int productId) {
        db = dbHelper.getReadableDatabase();
        String query = "SELECT " + DatabaseHelper.COLUMN_PRODUCT_IMAGE_RES_ID + " FROM " +
                DatabaseHelper.PRODUCTS_TABLE + " WHERE " + DatabaseHelper.COLUMN_PRODUCT_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(productId)});
        Bitmap productImage = null;

        if (cursor != null && cursor.moveToFirst()) {
            byte[] imageBytes = cursor.getBlob(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_IMAGE_RES_ID));
            productImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);  // 将字节数组转换为Bitmap
        }

        cursor.close();
        db.close();
        return productImage;
    }

    // 更新商品信息
    public boolean updateProduct(int productId, String name, String price, String description, byte[] imageByteArray, String category, String sellerName) {
        db = dbHelper.getWritableDatabase();

        // 创建ContentValues对象来存储新的商品信息
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_PRODUCT_NAME, name);
        values.put(DatabaseHelper.COLUMN_PRODUCT_PRICE, price);
        values.put(DatabaseHelper.COLUMN_PRODUCT_DESCRIPTION, description);
        values.put(DatabaseHelper.COLUMN_PRODUCT_IMAGE_RES_ID, imageByteArray != null ? imageByteArray : new byte[0]); // 使用字节数组
        values.put(DatabaseHelper.COLUMN_PRODUCT_CATEGORY, category);  // 更新类别字段

        // 执行更新操作，依据商品ID更新商品信息
        int rowsAffected = db.update(DatabaseHelper.PRODUCTS_TABLE, values,
                DatabaseHelper.COLUMN_PRODUCT_ID + " = ?", new String[]{String.valueOf(productId)});

        db.close();
        return rowsAffected > 0;  // 如果更新了至少一行数据，返回true
    }


    // 删除商品
    public boolean deleteProduct(int productId) {
        db = dbHelper.getWritableDatabase();

        // 执行删除操作，依据商品ID删除商品
        int rowsAffected = db.delete(DatabaseHelper.PRODUCTS_TABLE,
                DatabaseHelper.COLUMN_PRODUCT_ID + " = ?", new String[]{String.valueOf(productId)});

        db.close();
        return rowsAffected > 0;  // 如果删除了至少一行数据，返回true
    }


// 根据订单列表获取商品的所有详细信息
public List<Pair<Product, String>> getProductDetailsWithOrderIds(List<Pair<String, Integer>> orderAndProductIds) {
    SQLiteDatabase db = dbHelper.getReadableDatabase();
    List<Pair<Product, String>> productWithOrderList = new ArrayList<>();

    for (Pair<String, Integer> pair : orderAndProductIds) {
        String orderId = pair.first; // 提取订单 ID
        int productId = pair.second; // 提取商品 ID

        // 查询指定商品 ID 的商品
        String query = "SELECT * FROM " + DatabaseHelper.PRODUCTS_TABLE +
                " WHERE " + DatabaseHelper.COLUMN_PRODUCT_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(productId)});

        if (cursor != null && cursor.moveToFirst()) {
            // 提取商品的所有字段
            String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_NAME));
            byte[] imageBytes = cursor.getBlob(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_IMAGE_RES_ID));
            String price = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_PRICE));
            String description = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_DESCRIPTION));
            String category = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_CATEGORY));
            String sellerName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_SELLER_NAME));

            // 创建 Product 对象
            Product product = new Product(productId, name, price, description, imageBytes, category, sellerName);

            // 添加商品和订单 ID 到列表中
            productWithOrderList.add(new Pair<>(product, orderId));

            // 调试日志
            Log.d("ProductManager", "Product Details: ID: " + productId + ", Name: " + name +
                    ", Price: " + price + ", Description: " + description +
                    ", Category: " + category + ", Seller: " + sellerName + ", Order ID: " + orderId);
        } else {
            Log.d("ProductManager", "No product found with ID: " + productId);
        }

        if (cursor != null) {
            cursor.close(); // 关闭游标
        }
    }

    db.close(); // 关闭数据库连接
    return productWithOrderList; // 返回商品和订单 ID 的列表
}

    // 获取所有商品列表
// 获取所有商品列表
    public Cursor getAllProducts() {
        db = dbHelper.getReadableDatabase();

        // 查询所有商品的字段，包括：ID、名称、价格、描述、图片资源ID、类别、卖家名称
        String query = "SELECT " +
                DatabaseHelper.COLUMN_PRODUCT_ID + ", " +
                DatabaseHelper.COLUMN_PRODUCT_NAME + ", " +
                DatabaseHelper.COLUMN_PRODUCT_PRICE + ", " +
                DatabaseHelper.COLUMN_PRODUCT_DESCRIPTION + ", " +
                DatabaseHelper.COLUMN_PRODUCT_IMAGE_RES_ID + ", " +
                DatabaseHelper.COLUMN_PRODUCT_CATEGORY + ", " +  // 新增类别字段
                DatabaseHelper.COLUMN_SELLER_NAME +  // 包括卖家名称字段
                " FROM " + DatabaseHelper.PRODUCTS_TABLE;

        // 执行查询并返回结果的 Cursor 对象
        return db.rawQuery(query, null);
    }


    // 获取指定商品ID的商品信息
    public Cursor getProductById(int productId) {
        db = dbHelper.getReadableDatabase();

        // 查询指定商品ID的商品
        String query = "SELECT * FROM " + DatabaseHelper.PRODUCTS_TABLE +
                " WHERE " + DatabaseHelper.COLUMN_PRODUCT_ID + " = ?";
        return db.rawQuery(query, new String[]{String.valueOf(productId)});  // 返回Cursor对象
    }

    // 根据类别获取商品
    public Cursor getProductsByCategory(String category) {
        db = dbHelper.getReadableDatabase();

        // 查询指定类别的商品
        String query = "SELECT * FROM " + DatabaseHelper.PRODUCTS_TABLE +
                " WHERE " + DatabaseHelper.COLUMN_PRODUCT_CATEGORY + " = ?";
        return db.rawQuery(query, new String[]{category});  // 返回Cursor对象
    }

    // 输出所有商品信息到日志
    public void logAllProducts() {
        db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + DatabaseHelper.PRODUCTS_TABLE;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    int productId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_ID));
                    String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_NAME));
                    String price = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_PRICE));
                    String description = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_DESCRIPTION));
                    byte[] imageResId = cursor.getBlob(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_IMAGE_RES_ID));
                    String category = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_CATEGORY));
                    String sellerName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_SELLER_NAME));
                    Log.d("ProductManager", "ID: " + productId + ", Name: " + name +
                            ", Price: " + price + ", Desc: " + description +
                            ", Image: " + (imageResId != null ? imageResId.length : "No image") +
                            ", Category: " + category +
                            ", Seller: " + sellerName);
                }
            } finally {
                cursor.close();
                db.close();
            }
        }
    }

    // 根据卖家名称获取商品
    public Cursor getProductsBySeller(String sellerName) {
        db = dbHelper.getReadableDatabase();  // 修复：使用 db 对象
        String query = "SELECT * FROM " + DatabaseHelper.PRODUCTS_TABLE + " WHERE " + DatabaseHelper.COLUMN_SELLER_NAME + " = ?";
        return db.rawQuery(query, new String[]{sellerName});  // 返回Cursor对象
    }

    // 根据商品名称进行搜索
    public Cursor searchProductsByName(String searchTerm) {
        db = dbHelper.getReadableDatabase();

        // 模糊查询商品名称
        String query = "SELECT * FROM " + DatabaseHelper.PRODUCTS_TABLE +
                " WHERE " + DatabaseHelper.COLUMN_PRODUCT_NAME + " LIKE ?";
        return db.rawQuery(query, new String[]{"%" + searchTerm + "%"});  // 返回Cursor对象
    }
    // 清空商品表中的所有记录
    public void clearProductsTable() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            db.execSQL("DELETE FROM " + DatabaseHelper.PRODUCTS_TABLE);  // 删除所有数据
            Log.d("ProductManager", "All products have been deleted.");
        } catch (Exception e) {
            Log.e("ProductManager", "Error clearing products table: " + e.getMessage());
        } finally {
            db.close();  // 确保关闭数据库连接
        }
    }
    public List<Product> getAllProductsAsList() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Product> productList = new ArrayList<>();  // 用于存储商品信息的列表

        // 查询所有商品信息
        String query = "SELECT * FROM " + DatabaseHelper.PRODUCTS_TABLE;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    int productId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_ID));
                    String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_NAME));
                    String price = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_PRICE));
                    String description = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_DESCRIPTION));
                    byte[] imageResId = cursor.getBlob(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_IMAGE_RES_ID));
                    String category = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_CATEGORY));
                    String sellerName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_SELLER_NAME));

                    // 创建 Product 对象
                    Product product = new Product(productId, name, price, description, imageResId, category, sellerName);

                    // 添加到列表中
                    productList.add(product);

                    // 输出到日志
                    Log.d("ProductManager", "Product ID: " + productId +
                            ", Name: " + name +
                            ", Price: " + price +
                            ", Description: " + description +
                            ", Category: " + category +
                            ", Seller: " + sellerName +
                            ", Image Size: " + (imageResId != null ? imageResId.length : "No image"));
                }
            } catch (Exception e) {
                Log.e("ProductManager", "Error while retrieving products: " + e.getMessage());
            } finally {
                cursor.close();  // 关闭 Cursor
                db.close();  // 关闭数据库连接
            }
        } else {
            Log.e("ProductManager", "Cursor is null. Query failed.");
        }

        return productList;  // 返回包含所有商品的列表
    }
    public List<Product> getProductsByName(String productName) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Product> productList = new ArrayList<>();

        // 使用 LIKE 进行模糊查询
        String query = "SELECT * FROM " + DatabaseHelper.PRODUCTS_TABLE + " WHERE "
                + DatabaseHelper.COLUMN_PRODUCT_NAME + " LIKE ?";

        // 加上通配符 % 进行模糊匹配
        Cursor cursor = db.rawQuery(query, new String[]{"%" + productName + "%"});

        // 遍历查询结果
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int productId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_ID));
                String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_NAME));
                String price = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_PRICE));
                String description = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_DESCRIPTION));
                byte[] imageResId = cursor.getBlob(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_IMAGE_RES_ID));
                String category = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_CATEGORY));
                String sellerName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_SELLER_NAME));

                // 创建产品对象并加入列表
                Product product = new Product(productId, name, price, description, imageResId, category, sellerName);
                productList.add(product);
            } while (cursor.moveToNext());
        }

        // 关闭 Cursor 和数据库连接
        if (cursor != null) cursor.close();
        db.close();

        return productList;
    }

    // 更新商品信息
    public boolean userupdateProduct(int productId, String name, String price, String description, Bitmap imageBitmap, String category) {
        db = dbHelper.getWritableDatabase();

        // 创建ContentValues对象来存储新的商品信息
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_PRODUCT_NAME, name);
        values.put(DatabaseHelper.COLUMN_PRODUCT_PRICE, price);
        values.put(DatabaseHelper.COLUMN_PRODUCT_DESCRIPTION, description);
        // 将图片转换为字节数组，如果没有提供图片，则设置为空字节数组
        values.put(DatabaseHelper.COLUMN_PRODUCT_IMAGE_RES_ID, imageBitmap != null ? convertBitmapToBytes(imageBitmap) : new byte[0]);
        values.put(DatabaseHelper.COLUMN_PRODUCT_CATEGORY, category);  // 更新类别字段

        // 执行更新操作，依据商品ID更新商品信息
        int rowsAffected = db.update(DatabaseHelper.PRODUCTS_TABLE, values,
                DatabaseHelper.COLUMN_PRODUCT_ID + " = ?", new String[]{String.valueOf(productId)});

        db.close();
        return rowsAffected > 0;  // 如果更新了至少一行数据，返回true
    }
    // 缩放图片到指定的最大尺寸，保持纵横比
    private Bitmap scaleBitmap(Bitmap original, int maxWidth, int maxHeight) {
        int width = original.getWidth();
        int height = original.getHeight();

        // 计算缩放比例
        float scaleWidth = ((float) maxWidth) / width;
        float scaleHeight = ((float) maxHeight) / height;

        // 选择最小的缩放比例，保持纵横比
        float scale = Math.min(scaleWidth, scaleHeight);

        // 计算缩放后的新宽高
        int newWidth = (int) (width * scale);
        int newHeight = (int) (height * scale);

        // 创建缩放后的图片
        return Bitmap.createScaledBitmap(original, newWidth, newHeight, true);
    }






}
