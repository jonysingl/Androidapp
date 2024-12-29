package com.example.kmu_second_handmarketplace.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.kmu_second_handmarketplace.R;

public class AdminActivity extends AppCompatActivity {

    // 定义按钮
    private Button buttonManageUsers, buttonManageProducts, buttonViewOrders, buttonExitAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity); // 使用对应的布局文件

        // 初始化按钮
        buttonManageUsers = findViewById(R.id.buttonManageUsers);
        buttonManageProducts = findViewById(R.id.buttonManageProducts);
        buttonViewOrders = findViewById(R.id.buttonViewOrders);
        buttonExitAdmin = findViewById(R.id.buttonExitAdmin);

        // 设置按钮点击事件

        // 管理用户按钮
        buttonManageUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 打开用户管理页面（假设已创建UserManagementActivity）
                Intent intent = new Intent(AdminActivity.this, ManageUsersActivity.class);
                startActivity(intent);
            }
        });

        // 管理商品按钮
        buttonManageProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 打开商品管理页面（假设已创建ProductManagementActivity）
                Intent intent = new Intent(AdminActivity.this, ProductManagementActivity.class);
                startActivity(intent);
            }
        });

        // 查看订单按钮
        buttonViewOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 打开订单查看页面（假设已创建OrderViewActivity）
                Intent intent = new Intent(AdminActivity.this, OrderManagementActivity.class);
                startActivity(intent);
            }
        });

        // 退出管理员界面按钮
        buttonExitAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 退出当前页面，通常可以返回登录界面或关闭应用
                finish(); // 关闭当前活动，回到上一个页面
            }
        });
    }
}
