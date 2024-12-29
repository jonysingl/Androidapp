package com.example.kmu_second_handmarketplace.Personal_founction;


import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kmu_second_handmarketplace.Adapter.SellerProductAdapter;
import com.example.kmu_second_handmarketplace.R;
import com.example.kmu_second_handmarketplace.database.ProductManager;

public class SellerProductActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SellerProductAdapter adapter;
    private ProductManager productManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_seller_products); // 假设布局文件名为 activity_seller_product.xml

        // 初始化视图和适配器
        recyclerView = findViewById(R.id.recyclerViewSellerProducts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new SellerProductAdapter(this);
        recyclerView.setAdapter(adapter);

        productManager = new ProductManager(this);

        // 获取卖家名称，这里假设卖家名称通过Intent传递
        String sellerName = getIntent().getStringExtra("SELLER_NAME");

        if (sellerName != null) {
            // 加载卖家的商品
            loadSellerProducts(sellerName);
        } else {
            Toast.makeText(this, "卖家名称为空", Toast.LENGTH_SHORT).show();
        }
    }

    // 加载卖家的商品
    private void loadSellerProducts(String sellerName) {
        Cursor cursor = productManager.getProductsBySeller(sellerName);
        if (cursor != null && cursor.getCount() > 0) {
            Toast.makeText(this, "查询到 " + cursor.getCount() + " 件商品", Toast.LENGTH_SHORT).show();
            adapter.updateProductList(cursor);
        } else {
            Toast.makeText(this, "没有找到已发布的商品", Toast.LENGTH_SHORT).show();
        }
    }

}