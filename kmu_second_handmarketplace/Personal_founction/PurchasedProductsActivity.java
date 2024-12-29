package com.example.kmu_second_handmarketplace.Personal_founction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kmu_second_handmarketplace.Adapter.PurchasedProductAdapter;
import com.example.kmu_second_handmarketplace.Product.Product;
import com.example.kmu_second_handmarketplace.R;
import com.example.kmu_second_handmarketplace.database.OrderManager;
import com.example.kmu_second_handmarketplace.database.ProductManager;

import java.util.ArrayList;
import java.util.List;
import android.util.Pair;

public class PurchasedProductsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewPurchasedProducts;
    private PurchasedProductAdapter adapter;
    private OrderManager orderManager;
    private ProductManager productManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchased_products);

        recyclerViewPurchasedProducts = findViewById(R.id.recyclerViewPurchasedProducts);
        orderManager = new OrderManager(this);
        productManager = new ProductManager(this);

        // 从 Intent 中获取传递的 BUYER_NAME
        String loggedInUsername = getIntent().getStringExtra("BUYER_NAME");
        Log.d("PurchasedProductsActivity", "Logged-in username: " + loggedInUsername);

        if (loggedInUsername == null || loggedInUsername.isEmpty()) {
            Toast.makeText(this, "No user logged in.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 输出所有订单到日志，便于调试
        orderManager.logAllOrders();

        // 获取用户购买的订单 ID 和商品 ID 列表
        List<Pair<String, Integer>> orderAndProductIds = orderManager.getPurchasedOrderAndProductIdsByBuyer(loggedInUsername);

        // 如果没有找到订单和商品 ID，则显示提示
        if (orderAndProductIds.isEmpty()) {
            Toast.makeText(this, "No purchased products found.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 获取商品详细信息及其对应的订单 ID
        List<Pair<Product, String>> purchasedProductsWithOrders = productManager.getProductDetailsWithOrderIds(orderAndProductIds);

        // 调试：打印查询结果
        Log.d("PurchasedProductsActivity", "Purchased products count: " + purchasedProductsWithOrders.size());

        // 如果找到了商品，则更新 RecyclerView，否则显示空提示
        if (purchasedProductsWithOrders.isEmpty()) {
            Toast.makeText(this, "No purchased products found.", Toast.LENGTH_SHORT).show();
        } else {
            // 将 `Context` 传递给 `PurchasedProductAdapter`
            adapter = new PurchasedProductAdapter(this, purchasedProductsWithOrders);
            recyclerViewPurchasedProducts.setLayoutManager(new LinearLayoutManager(this));
            recyclerViewPurchasedProducts.setAdapter(adapter);
        }
    }
}
