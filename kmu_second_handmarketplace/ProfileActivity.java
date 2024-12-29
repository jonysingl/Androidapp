package com.example.kmu_second_handmarketplace;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.kmu_second_handmarketplace.AddProduct.AddProductActivity;
import com.example.kmu_second_handmarketplace.Personal_founction.PersonalInfoActivity;
import com.example.kmu_second_handmarketplace.Personal_founction.SellerProductActivity;
import com.example.kmu_second_handmarketplace.Personal_founction.PurchasedProductsActivity;
import com.example.kmu_second_handmarketplace.admin.AdminActivity;
import com.example.kmu_second_handmarketplace.database.UserManager;

import java.io.ByteArrayInputStream;

public class ProfileActivity extends AppCompatActivity {

    // 声明UI组件
    private Button buttonHome, buttonProfile, buttonAddProduct, buttonViewPersonalInfo,
            buttonViewPostedItems, buttonViewPurchasedItems, buttonAppInfo,
            buttonLogout, buttonAdminFeatures; // 各种按钮
    private ImageView imageViewProfile; // 用户头像
    private TextView textViewUsername; // 用户名显示
    private UserManager userManager; // 用于管理用户数据的UserManager

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);  // 设置该活动的布局文件

        // 初始化视图组件
        buttonHome = findViewById(R.id.buttonHome);
        buttonProfile = findViewById(R.id.buttonProfile);
        buttonAddProduct = findViewById(R.id.buttonAddProduct);
        buttonViewPersonalInfo = findViewById(R.id.buttonViewPersonalInfo);
        buttonViewPostedItems = findViewById(R.id.buttonViewPostedItems);
        buttonViewPurchasedItems = findViewById(R.id.buttonViewPurchasedItems);
        buttonAppInfo = findViewById(R.id.buttonAppInfo);
        buttonLogout = findViewById(R.id.buttonLogout);  // 退出登录按钮
        imageViewProfile = findViewById(R.id.imageViewProfile); // 头像
        textViewUsername = findViewById(R.id.textViewUsername); // 用户名显示

        buttonAdminFeatures = findViewById(R.id.buttonAdminFeatures); // 管理员功能按钮

        userManager = new UserManager(this); // 实例化UserManager，负责数据库操作

        // 获取当前用户名
        String currentUsername = getCurrentUsername();

        // 如果用户名不为空，加载用户信息（头像和用户名）
        if (currentUsername != null) {
            loadUserInfo(currentUsername);
        }

        // 设置首页按钮的点击事件
        buttonHome.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            startActivity(intent);  // 跳转到主页
            finish();  // 结束当前页面，防止返回
        });

        // 设置个人中心按钮的点击事件
        buttonProfile.setOnClickListener(v -> {
            // 提示用户已经在个人中心页面
            Toast.makeText(ProfileActivity.this, "您已经在个人中心页面", Toast.LENGTH_SHORT).show();
        });

        // 设置添加商品按钮的点击事件
        buttonAddProduct.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, AddProductActivity.class);
            startActivity(intent);  // 跳转到添加商品页面
        });

        // 设置查看个人信息按钮的点击事件
        buttonViewPersonalInfo.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, PersonalInfoActivity.class);
            startActivity(intent);  // 跳转到个人信息页面
        });

        // 设置查看卖家已发布商品按钮的点击事件
        buttonViewPostedItems.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
            String loggedInUsername = sharedPreferences.getString("currentUsername", null);

            // 获取用户名，如果登录了则跳转到已发布商品页面
            if (loggedInUsername != null) {
                Intent intent = new Intent(ProfileActivity.this, SellerProductActivity.class);
                intent.putExtra("SELLER_NAME", loggedInUsername);
                startActivity(intent);
            } else {
                Toast.makeText(ProfileActivity.this, "无法获取用户名，请重新登录", Toast.LENGTH_SHORT).show();
            }
        });

        // 设置查看购买的商品按钮的点击事件
        buttonViewPurchasedItems.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
            String loggedInUsername = sharedPreferences.getString("currentUsername", null);

            // 获取用户名，如果登录了则跳转到已购买商品页面
            if (loggedInUsername != null) {
                Intent intent = new Intent(ProfileActivity.this, PurchasedProductsActivity.class);
                intent.putExtra("BUYER_NAME", loggedInUsername);
                startActivity(intent);
            } else {
                Toast.makeText(ProfileActivity.this, "无法获取用户名，请重新登录", Toast.LENGTH_SHORT).show();
            }
        });

        // 设置管理员功能按钮的点击事件
        buttonAdminFeatures.setOnClickListener(v -> {
            // 弹出一个对话框要求输入管理员密码
            AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
            builder.setTitle("管理员验证");
            builder.setMessage("请输入管理员密码以进入管理员模式：");

            // 创建输入框
            final EditText inputPassword = new EditText(ProfileActivity.this);
            inputPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD); // 设置为密码输入框
            builder.setView(inputPassword);

            // 设置确认按钮，验证管理员密码
            builder.setPositiveButton("确认", (dialog, which) -> {
                String enteredPassword = inputPassword.getText().toString();
                String correctPassword = "123"; // 预设的管理员密码

                // 验证密码是否正确
                if (enteredPassword.equals(correctPassword)) {
                    // 密码正确，跳转到管理员页面
                    Intent intent = new Intent(ProfileActivity.this, AdminActivity.class);
                    startActivity(intent);
                    finish(); // 结束当前页面
                } else {
                    // 密码错误，提示用户
                    Toast.makeText(ProfileActivity.this, "密码错误，请重试", Toast.LENGTH_SHORT).show();
                }
            });

            // 设置取消按钮，关闭对话框
            builder.setNegativeButton("取消", (dialog, which) -> dialog.cancel());

            // 显示对话框
            builder.show();
        });

        // 设置退出登录按钮的点击事件
        buttonLogout.setOnClickListener(v -> {
            // 清除SharedPreferences中的登录信息
            SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();  // 清空所有数据
            editor.apply();

            // 提示用户退出成功
            Toast.makeText(ProfileActivity.this, "成功退出登录", Toast.LENGTH_SHORT).show();

            // 跳转到登录界面
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);  // 假设登录界面是LoginActivity
            startActivity(intent);
            finish();  // 结束当前页面，防止返回此页面
        });
    }

    // 获取当前登录用户名
    private String getCurrentUsername() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        return sharedPreferences.getString("currentUsername", null);  // 返回保存的用户名
    }

    // 加载用户信息（用户名和头像）
    private void loadUserInfo(String username) {
        // 从数据库中查询该用户名的用户信息
        Cursor userCursor = userManager.getUserByUsername(username);

        if (userCursor != null && userCursor.moveToFirst()) {
            // 获取用户的电子邮件、电话和头像数据
            String email = userCursor.getString(userCursor.getColumnIndex("email"));
            String phone = userCursor.getString(userCursor.getColumnIndex("phone"));
            byte[] avatarBytes = userCursor.getBlob(userCursor.getColumnIndex("avatar"));

            // 设置用户名到页面
            textViewUsername.setText(username);

            // 如果头像存在，则加载头像
            if (avatarBytes != null && avatarBytes.length > 0) {
                // 将字节数组转换为Bitmap对象
                Bitmap avatarImage = BitmapFactory.decodeByteArray(avatarBytes, 0, avatarBytes.length);

                // 使用Glide加载头像图片
                Glide.with(this)
                        .load(avatarImage)  // 加载头像
                        .placeholder(R.drawable.defaut_avatar)  // 设置占位图
                        .error(R.drawable.defaut_avatar)  // 设置加载错误时的默认头像
                        .into(imageViewProfile);  // 显示头像
            } else {
                // 如果没有头像，则显示默认头像
                imageViewProfile.setImageResource(R.drawable.defaut_avatar);
            }

            userCursor.close();  // 关闭Cursor
        }
    }
}
