package com.example.kmu_second_handmarketplace.Personal_founction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.kmu_second_handmarketplace.R;
import com.example.kmu_second_handmarketplace.database.UserManager;

import java.io.ByteArrayInputStream;

public class PersonalInfoActivity extends AppCompatActivity {

    private ImageView imageViewProfile;  // 用于显示头像的ImageView
    private TextView textViewUsername, textViewEmail, textViewPhone;  // 显示用户名、电子邮件和电话的TextView
    private Button buttonEditProfile;  // 编辑个人资料按钮

    private UserManager userManager;  // 管理用户数据的UserManager实例

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_personal_info);  // 设置布局文件

        // 初始化视图组件
        imageViewProfile = findViewById(R.id.imageViewProfile);
        textViewUsername = findViewById(R.id.textViewUsername);
        textViewEmail = findViewById(R.id.textViewEmail);
        textViewPhone = findViewById(R.id.textViewPhone);
        buttonEditProfile = findViewById(R.id.buttonEditProfile);

        userManager = new UserManager(this);  // 初始化UserManager

        // 获取当前登录用户的用户名
        String currentUsername = getCurrentUsername();

        if (currentUsername != null) {
            // 根据用户名从数据库查询用户信息
            Cursor userCursor = userManager.getUserByUsername(currentUsername);

            // 如果查询到用户信息
            if (userCursor != null && userCursor.moveToFirst()) {
                // 获取用户信息（邮箱、电话和头像）
                String email = userCursor.getString(userCursor.getColumnIndex("email"));
                String phone = userCursor.getString(userCursor.getColumnIndex("phone"));
                byte[] avatarBytes = userCursor.getBlob(userCursor.getColumnIndex("avatar"));

                // 填充视图数据
                textViewUsername.setText(currentUsername);
                textViewEmail.setText("Email: " + email);
                textViewPhone.setText("Phone: " + phone);

                Log.d("PersonalInfoActivity", "Avatar Bytes: " + (avatarBytes != null ? avatarBytes.length : "null"));

                // 获取字节数组（头像的字节数据）
                if (avatarBytes != null && avatarBytes.length > 0) {
                    // 将字节数组转换为Bitmap
                    Bitmap avatarImage = BitmapFactory.decodeByteArray(avatarBytes, 0, avatarBytes.length);

                    // 使用Glide加载转换后的Bitmap
                    Glide.with(this)
                            .load(avatarImage)  // 使用转换后的Bitmap加载图片
                            .placeholder(R.drawable.defaut_avatar)  // 设置占位图
                            .error(R.drawable.defaut_avatar)  // 设置错误图
                            .into(imageViewProfile);  // 设置到ImageView
                } else {
                    // 如果没有头像数据，使用默认头像
                    imageViewProfile.setImageResource(R.drawable.defaut_avatar);
                }

                // 关闭Cursor
                userCursor.close();
            }
        }

        // 编辑个人信息按钮点击事件
        buttonEditProfile.setOnClickListener(view -> {
            // 跳转到编辑个人信息页面
            Intent intent = new Intent(PersonalInfoActivity.this, UpdataUserProfileActivity.class);
            startActivity(intent);  // 启动新的Activity
        });
    }

    // 获取当前登录用户的用户名
    private String getCurrentUsername() {
        // 从SharedPreferences中获取当前用户名
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        return sharedPreferences.getString("currentUsername", null);  // 返回当前用户名，若没有则返回null
    }
}
