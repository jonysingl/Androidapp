package com.example.kmu_second_handmarketplace.DetailandBuy;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.kmu_second_handmarketplace.OrdeDeal.OrderConfirmationActivity;
import com.example.kmu_second_handmarketplace.R;

public class ProductDetailActivity extends AppCompatActivity {

    // 定义视图组件
    private ImageView imageViewProduct;  // 商品图片
    private TextView textViewProductName, textViewProductDescription, textViewProductPrice,textViewProductSeller;  // 商品名称、描述和价格
    private Button buttonBuy;  // 购买按钮


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        // 初始化视图组件
        imageViewProduct = findViewById(R.id.imageViewProduct);
        textViewProductName = findViewById(R.id.textViewProductName);
        textViewProductDescription = findViewById(R.id.textViewProductDescription);
        textViewProductPrice = findViewById(R.id.textViewProductPrice);
        buttonBuy = findViewById(R.id.buttonBuy);
        textViewProductSeller=findViewById(R.id.textViewProductSeller);

        // 获取通过 Intent 传递过来的商品信息
        Intent intent = getIntent();
        String productName = intent.getStringExtra("productName");  // 获取商品名称
        String productDescription = intent.getStringExtra("productDescription");  // 获取商品描述
        String productPrice = intent.getStringExtra("productPrice");  // 获取商品价格
        byte[] productImageBytes = intent.getByteArrayExtra("productImage");  // 获取商品图片的字节数组
        String productSeller = intent.getStringExtra("productSeller");  // 获取卖家名称
        int productId = getIntent().getIntExtra("productId", -1);  // 第二个参数是默认值

        Log.d("OrderConfirmationActivity", "Received Product ID: " + productId);

        // 输出日志检查传递的商品价格和图片字节数组
        Log.d("ProductDetailActivity", "Received product price: " + productPrice);
        Log.d("ProductDetailActivity", "Product image byte array: " + (productImageBytes != null ? productImageBytes.length : "null"));

        // 设置商品名称、描述、价格
        textViewProductName.setText(productName);
        textViewProductSeller.setText(productSeller);
        textViewProductDescription.setText(productDescription);
        textViewProductPrice.setText("Price: $" + productPrice);

        // 使用 Glide 加载商品图片（如果字节数组存在）
        if (productImageBytes != null && productImageBytes.length > 0) {
            Bitmap productImage = BitmapFactory.decodeByteArray(productImageBytes, 0, productImageBytes.length);
            Glide.with(this)
                    .load(productImage)  // 使用字节数组转换后的 Bitmap 加载图片
                    .placeholder(R.drawable.defaut_avatar)  // 设置占位图
                    .error(R.drawable.defaut_avatar)  // 设置错误图
                    .into(imageViewProduct);  // 设置到 ImageView
        } else {
            // 如果没有有效的字节数组，使用默认头像
            imageViewProduct.setImageResource(R.drawable.defaut_avatar);
        }

        // 设置购买按钮的点击事件
        buttonBuy.setOnClickListener(v -> {
            // 将商品信息传递到 OrderConfirmationActivity
            Intent orderIntent = new Intent(ProductDetailActivity.this, OrderConfirmationActivity.class);
            orderIntent.putExtra("productName", productName);
            orderIntent.putExtra("productDescription", productDescription);
            orderIntent.putExtra("productPrice", productPrice);
            orderIntent.putExtra("productImage", productImageBytes);  // 传递字节数组作为图片数据
            orderIntent.putExtra("productSeller", productSeller);
            orderIntent.putExtra("productId",productId);// 将卖家信息传递过去

            // 启动订单确认页面
            startActivity(orderIntent);

            // 显示购买提示
            Toast.makeText(ProductDetailActivity.this, "Proceeding to order confirmation", Toast.LENGTH_SHORT).show();
        });
    }

}
