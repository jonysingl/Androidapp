package com.example.kmu_second_handmarketplace.OrdeDeal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kmu_second_handmarketplace.MainActivity;
import com.example.kmu_second_handmarketplace.R;

public class PurchaseSuccessActivity extends AppCompatActivity {

    private TextView textViewOrderNumber, textViewSuccessMessage;
    private Button buttonGoHome, buttonViewOrders;
    private String orderNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_success);

        // 初始化控件
        textViewOrderNumber = findViewById(R.id.textViewOrderNumber);
        textViewSuccessMessage = findViewById(R.id.textViewSuccessMessage);
        buttonGoHome = findViewById(R.id.buttonGoHome);
        buttonViewOrders = findViewById(R.id.buttonViewOrders);

        // 获取订单编号从Intent
        Intent intent = getIntent();
        orderNumber = intent.getStringExtra("orderNumber");

        // 显示成功信息
        textViewOrderNumber.setText("Order Number: " + orderNumber);
        textViewSuccessMessage.setText("Your payment was successful! Thank you for your purchase.");

        // 返回主页按钮点击事件
        buttonGoHome.setOnClickListener(v -> {
            // 跳转到主页面
            Intent homeIntent = new Intent(PurchaseSuccessActivity.this, MainActivity.class);
            startActivity(homeIntent);
            finish();
        });

        // 查看订单按钮点击事件
//        buttonViewOrders.setOnClickListener(v -> {
//            // 跳转到查看订单页面
//            Intent viewOrdersIntent = new Intent(PurchaseSuccessActivity.this, ViewOrdersActivity.class);
//            startActivity(viewOrdersIntent);
//        });
    }
}
