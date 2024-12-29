package com.example.kmu_second_handmarketplace.OrdeDeal;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.kmu_second_handmarketplace.Oder.Order;
import com.example.kmu_second_handmarketplace.R;
import com.example.kmu_second_handmarketplace.database.OrderManager;
import com.example.kmu_second_handmarketplace.database.DatabaseHelper;

import java.util.Random;

public class OrderConfirmationActivity extends AppCompatActivity {

    // 声明UI控件变量
    private TextView textViewProductName, textViewProductPrice, textViewSellerName, textViewBuyerName, textViewOrderId, textViewProductId;
    private ImageView imageViewProduct;
    private Button buttonPayNow, buttonCancelOrder;

    private String orderNumber;  // 用于存储生成的订单号

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_confirmation_activity);  // 设置当前 Activity 的布局文件

        // 初始化控件
        imageViewProduct = findViewById(R.id.imageViewProduct);
        textViewProductName = findViewById(R.id.textViewProductName);
        textViewProductPrice = findViewById(R.id.textViewProductPrice);
        textViewSellerName = findViewById(R.id.textViewSellerName);
        textViewBuyerName = findViewById(R.id.textViewBuyerName);
        textViewOrderId = findViewById(R.id.textViewOrderId);
        textViewProductId = findViewById(R.id.textViewProductId);
        buttonPayNow = findViewById(R.id.buttonPayNow);
        buttonCancelOrder = findViewById(R.id.buttonCancelOrder);

        // 检查数据库中是否存在订单表
        if (!isTableExists(DatabaseHelper.ORDERS_TABLE)) {
            Log.e("DatabaseError", "订单表不存在，请检查数据库初始化！");
            Toast.makeText(this, "系统错误，请稍后重试", Toast.LENGTH_LONG).show();
            finish();  // 如果订单表不存在，则退出当前 Activity
            return;
        }

        // 获取从前一个 Activity 传递过来的商品信息
        Intent intent = getIntent();
        String productName = intent.getStringExtra("productName");  // 商品名称
        String productPrice = intent.getStringExtra("productPrice");  // 商品价格
        int productImage = intent.getIntExtra("productImage", 0);  // 商品图片（资源 ID）
        String productSeller = intent.getStringExtra("productSeller");  // 卖家名称
        int productId = intent.getIntExtra("productId", -1);  // 商品 ID

        // 设置商品信息到 UI 控件上
        textViewProductName.setText(productName);
        textViewProductPrice.setText("Price: $" + productPrice);
        textViewSellerName.setText("Seller: " + productSeller);
        textViewProductId.setText("Id: " + productId);

        // 使用 Glide 加载商品图片，如果图片是字节数组形式，调用 loadProductImage 方法加载
        loadProductImage(intent.getByteArrayExtra("productImage"));

        textViewProductId.setText("Id: " + productId);  // 更新商品 ID 显示
        Log.d("OrderConfirmation", "Product ID: " + productId);

        // 生成订单号并显示在界面上
        orderNumber = generateOrderNumber();  // 调用方法生成订单号
        textViewOrderId.setText("Order Number: " + orderNumber);

        // 获取当前用户（买家）的信息
        String buyerName = getSharedPreferences("UserSession", MODE_PRIVATE).getString("currentUsername", "Unknown");
        textViewBuyerName.setText("Buyer: " + buyerName);

        // 设置支付按钮点击事件，显示支付密码对话框
        buttonPayNow.setOnClickListener(v -> showPasswordDialog());
        // 设置取消订单按钮点击事件，关闭当前 Activity
        buttonCancelOrder.setOnClickListener(v -> {
            Toast.makeText(this, "Order " + orderNumber + " has been cancelled.", Toast.LENGTH_SHORT).show();
            finish();  // 取消订单后退出当前 Activity
        });
    }

    /**
     * 显示支付密码输入框，并验证密码
     */
    private void showPasswordDialog() {
        // 创建密码输入对话框，用户输入支付密码
        PasswordDialog passwordDialog = new PasswordDialog(this, password -> {
            // 验证输入的密码是否正确
            if ("123456".equals(password)) {
                // 如果密码正确，则提示支付成功
                Toast.makeText(this, "Payment Successful!", Toast.LENGTH_SHORT).show();
                saveOrderToDatabase();  // 保存订单到数据库
                navigateToSuccessPage();  // 跳转到支付成功页面
            } else {
                // 如果密码错误，则提示密码错误
                Toast.makeText(this, "Incorrect password.", Toast.LENGTH_SHORT).show();
            }
        });
        passwordDialog.show();  // 显示密码输入对话框
    }

    /**
     * 将订单信息保存到数据库中
     */
    private void saveOrderToDatabase() {
        // 获取买家的用户名
        String buyerName = getSharedPreferences("UserSession", MODE_PRIVATE).getString("currentUsername", "Unknown");
        String productName = textViewProductName.getText().toString();
        String productPrice = textViewProductPrice.getText().toString().replace("Price: $", "");  // 去掉 "Price: $" 部分
        int productId = Integer.parseInt(textViewProductId.getText().toString().replace("Id:", "").trim());  // 获取商品 ID
                        //将获取到的字符串转换为int
        // 创建订单对象
        Order order = new Order(orderNumber, productId, buyerName, String.valueOf(System.currentTimeMillis()), "paid",
                productName, productPrice, null);  // 创建订单对象，状态为 "paid"（已支付）

        // 使用 OrderManager 保存订单
        OrderManager orderManager = new OrderManager(this);
        boolean isSaved = orderManager.saveOrder(order);  // 保存订单到数据库

        if (isSaved) {
            // 如果订单保存成功，打印日志并提示用户
            Log.i("OrderConfirmation", "订单保存成功：订单编号：" + orderNumber + "，买家：" + buyerName);
            Toast.makeText(this, "Order saved successfully!", Toast.LENGTH_SHORT).show();

            // 打印所有订单信息
            orderManager.logAllOrders();
        } else {
            // 如果订单保存失败，提示用户
            Toast.makeText(this, "Failed to save order. Please try again.", Toast.LENGTH_SHORT).show();
            Log.e("OrderConfirmation", "订单保存失败");
        }
    }

    /**
     * 跳转到支付成功页面
     */
    private void navigateToSuccessPage() {
        // 创建 Intent 跳转到支付成功页面
        Intent intent = new Intent(this, PurchaseSuccessActivity.class);
        intent.putExtra("orderNumber", orderNumber);  // 传递订单号
        startActivity(intent);  // 启动支付成功页面
        finish();  // 结束当前页面
    }

    /**
     * 生成唯一的订单号
     * 订单号由当前时间戳和一个随机数组成，确保每个订单号唯一
     */
    private String generateOrderNumber() {
        return System.currentTimeMillis() + "-" + new Random().nextInt(1000);  // 生成基于时间戳和随机数的订单号
    }

    /**
     * 检查数据库中是否存在指定的表
     * @param tableName 表名
     * @return 如果表存在返回 true，否则返回 false
     */
    private boolean isTableExists(String tableName) {
        // 获取数据库对象
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        // 查询指定表是否存在
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name=?", new String[]{tableName});
        boolean exists = cursor.moveToFirst();  // 如果表存在，cursor 会移到第一个位置
        cursor.close();
        db.close();
        return exists;  // 返回查询结果
    }

    /**
     * 加载商品图片（字节数组形式）
     * @param imageBytes 商品图片字节数组
     */
    private void loadProductImage(byte[] imageBytes) {
        // 使用 Glide 加载商品图片
        if (imageBytes != null && imageBytes.length > 0) {
            Bitmap productImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);  // 将字节数组转为 Bitmap
            Glide.with(this)
                    .load(productImage)  // 加载图片
                    .placeholder(R.drawable.defaut_avatar)  // 设置占位图
                    .error(R.drawable.touxiang)  // 设置错误图
                    .into(imageViewProduct);  // 显示在 ImageView 中
        } else {
            // 如果没有有效的字节数组，使用默认头像
            imageViewProduct.setImageResource(R.drawable.defaut_avatar);
        }
    }
}
