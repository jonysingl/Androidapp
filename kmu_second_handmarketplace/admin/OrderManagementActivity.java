package com.example.kmu_second_handmarketplace.admin;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kmu_second_handmarketplace.Oder.Order;
import com.example.kmu_second_handmarketplace.R;
import com.example.kmu_second_handmarketplace.database.OrderManager;

import java.util.ArrayList;
import java.util.List;

public class OrderManagementActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    private OrderManager orderManager;
    private List<Order> orderList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_management);

        // 初始化 UI 组件
        recyclerView = findViewById(R.id.recycler_view_orders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 初始化 OrderManager 和 Adapter
        orderManager = new OrderManager(this);
        orderAdapter = new OrderAdapter(this, orderList, orderId -> {
            // 删除订单的逻辑
            deleteOrder(orderId);
        });
        recyclerView.setAdapter(orderAdapter);

        // 加载所有订单
        loadAllOrders();
    }

    private void loadAllOrders() {
        orderList = orderManager.getAllOrders();
        orderAdapter.updateData(orderList);
    }

    private void deleteOrder(String orderId) {
        boolean isDeleted = orderManager.deleteOrder(orderId); // 假设有 deleteOrder 方法
        if (isDeleted) {
            Toast.makeText(this, "订单删除成功", Toast.LENGTH_SHORT).show();
            loadAllOrders(); // 重新加载订单列表
        } else {
            Toast.makeText(this, "订单删除失败", Toast.LENGTH_SHORT).show();
        }
    }
}
