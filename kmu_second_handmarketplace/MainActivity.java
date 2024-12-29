package com.example.kmu_second_handmarketplace;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kmu_second_handmarketplace.Adapter.ProductAdapter;
import com.example.kmu_second_handmarketplace.database.ProductManager;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    private EditText editTextSearch;
    private Button buttonSearch;
    private Button buttonHome;
    private Button buttonProfile;
    private TabLayout tabLayout;
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private ProductManager productManager;

    private static final int REQUEST_CODE = 1;  // 请求代码常量

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化控件
        editTextSearch = findViewById(R.id.editTextSearch);
        buttonSearch = findViewById(R.id.buttonSearch);
        tabLayout = findViewById(R.id.tabLayout);
        recyclerView = findViewById(R.id.recyclerView);
        buttonHome = findViewById(R.id.buttonHome);
        buttonProfile = findViewById(R.id.buttonProfile);

        // 设置 RecyclerView 为 GridLayoutManager，显示两列商品
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        productManager = new ProductManager(this);
        productAdapter = new ProductAdapter(this);
        recyclerView.setAdapter(productAdapter);

        // 检查并请求存储权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
        } else {
            Log.d("MainActivity", "存储权限已授予");
        }

        // **确保默认加载全部商品**
        Log.d("MainActivity", "正在加载所有商品...");
        loadAllProducts();

        // 搜索框逻辑
        buttonSearch.setOnClickListener(v -> {
            String searchTerm = editTextSearch.getText().toString().trim();
            if (!searchTerm.isEmpty()) {
                Log.d("MainActivity", "执行搜索操作，搜索词：" + searchTerm);
                searchProducts(searchTerm);
            } else {
                Toast.makeText(MainActivity.this, "请输入搜索内容", Toast.LENGTH_SHORT).show();
                Log.d("MainActivity", "搜索框为空");
            }
        });

        // 分类按钮逻辑
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String category = tab.getText().toString();
                Log.d("MainActivity", "选中分类：" + category);
                if ("全部商品".equals(category)) {
                    loadAllProducts();  // 加载所有商品
                } else {
                    filterProductsByCategory(category);  // 根据类别加载商品
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        // 主页按钮点击事件
        buttonHome.setOnClickListener(v -> {
            Log.d("MainActivity", "返回主页");
            loadAllProducts();  // 加载所有商品
            Toast.makeText(MainActivity.this, "返回主页", Toast.LENGTH_SHORT).show();
        });

        // 个人中心按钮点击事件
        buttonProfile.setOnClickListener(v -> {
            Log.d("MainActivity", "跳转到个人中心");
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Log.e("MainActivity", "权限被拒绝，无法加载图片");

                // 如果用户拒绝了权限，并选择了“不再询问”，提示用户手动开启权限
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    new AlertDialog.Builder(this)
                            .setMessage("应用需要存储权限来加载图片，请到设置中开启权限。")
                            .setPositiveButton("去设置", (dialog, which) -> {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                            })
                            .setNegativeButton("取消", null)
                            .show();
                } else {
                    Toast.makeText(this, "权限被拒绝，无法加载图片", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.i("MainActivity", "权限已授予，可以加载图片");
            }
        }
    }

    // 默认加载所有商品
    private void loadAllProducts() {
        Log.d("MainActivity", "开始加载所有商品...");
        try {
            Cursor cursor = productManager.getAllProducts();
            if (cursor != null && cursor.getCount() > 0) {
                Log.d("MainActivity", "成功加载 " + cursor.getCount() + " 条商品数据");
                productAdapter.updateProductList(cursor);
            } else {
                Log.e("MainActivity", "没有商品数据，或数据库查询失败");
                productAdapter.updateProductList(null);  // 清空适配器
                Toast.makeText(this, "没有商品数据", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("MainActivity", "加载商品时发生错误：" + e.getMessage());
            e.printStackTrace();
        }
    }

    // 根据商品名称搜索商品
    private void searchProducts(String searchTerm) {
        Log.d("MainActivity", "执行搜索操作，搜索词：" + searchTerm);
        try {
            Cursor cursor = productManager.searchProductsByName(searchTerm);
            if (cursor != null && cursor.getCount() > 0) {
                Log.d("MainActivity", "成功找到 " + cursor.getCount() + " 条匹配商品");
                productAdapter.updateProductList(cursor);
            } else {
                Log.e("MainActivity", "未找到匹配的商品：" + searchTerm);
                productAdapter.updateProductList(null);  // 清空适配器
                Toast.makeText(this, "未找到匹配的商品", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("MainActivity", "搜索商品时发生错误：" + e.getMessage());
            e.printStackTrace();
        }
    }

    // 根据类别筛选商品
    private void filterProductsByCategory(String category) {
        Log.d("MainActivity", "筛选商品，类别：" + category);
        try {
            Cursor cursor = productManager.getProductsByCategory(category);
            if (cursor != null && cursor.getCount() > 0) {
                Log.d("MainActivity", "成功找到 " + cursor.getCount() + " 条 " + category + " 类别的商品");
                productAdapter.updateProductList(cursor);
            } else {
                Log.e("MainActivity", "该类别下没有商品：" + category);
                productAdapter.updateProductList(null);  // 清空适配器
                Toast.makeText(this, "该类别下没有商品", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("MainActivity", "筛选商品时发生错误：" + e.getMessage());
            e.printStackTrace();
        }
    }
}

