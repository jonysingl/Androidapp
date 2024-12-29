package com.example.kmu_second_handmarketplace.admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kmu_second_handmarketplace.AddProduct.AddProductActivity;
import com.example.kmu_second_handmarketplace.Product.Product;
import com.example.kmu_second_handmarketplace.R;
import com.example.kmu_second_handmarketplace.database.ProductManager;

import java.util.ArrayList;
import java.util.List;

public class ProductManagementActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private ProductManager productManager;
    private List<Product> productList = new ArrayList<>();
    private EditText etSearchId;
    private Button btnSearch, btnAddProduct;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mannagement); // 布局文件

        // 初始化视图
        recyclerView = findViewById(R.id.recycler_view);
        etSearchId = findViewById(R.id.et_search_id);
        btnSearch = findViewById(R.id.btn_search);
        btnAddProduct = findViewById(R.id.btn_add_product);

        // RecyclerView 设置
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        productManager = new ProductManager(this);
        productAdapter = new ProductAdapter(this, productList, this::deleteProduct);
        recyclerView.setAdapter(productAdapter);

        // 加载所有商品
        loadAllProducts();

        // 搜索按钮逻辑
        btnSearch.setOnClickListener(v -> searchProduct());

        // 增加商品按钮逻辑
        btnAddProduct.setOnClickListener(v -> {
            Intent intent = new Intent(ProductManagementActivity.this, AddProductActivity.class);
            startActivity(intent);
        });
    }

    /**
     * 加载所有商品数据
     */
    private void loadAllProducts() {
        productList.clear();
        productList.addAll(productManager.getAllProductsAsList());
        productAdapter.notifyDataSetChanged();
    }

    /**
     * 删除商品
     */
    private void deleteProduct(int productId) {
        boolean isDeleted = productManager.deleteProduct(productId);
        if (isDeleted) {
            Toast.makeText(this, "商品删除成功", Toast.LENGTH_SHORT).show();
            loadAllProducts(); // 刷新列表
        } else {
            Toast.makeText(this, "删除失败，请重试", Toast.LENGTH_SHORT).show();
        }
    }

    private void searchProduct() {
        String searchName = etSearchId.getText().toString().trim();

        if (TextUtils.isEmpty(searchName)) {
            Toast.makeText(this, "请输入产品名称进行搜索", Toast.LENGTH_SHORT).show();
            return;
        }

        // 调用 getProductsByName 来查找匹配的商品
        List<Product> searchResults = productManager.getProductsByName(searchName);

        if (searchResults != null && !searchResults.isEmpty()) {
            productList.clear();
            productList.addAll(searchResults);
            productAdapter.notifyDataSetChanged();
            Toast.makeText(this, "搜索成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "未找到匹配的商品", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        loadAllProducts(); // 重新加载数据，确保列表最新
    }
}
