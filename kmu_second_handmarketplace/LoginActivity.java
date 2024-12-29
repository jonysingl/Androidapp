package com.example.kmu_second_handmarketplace;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kmu_second_handmarketplace.database.OrderManager;
import com.example.kmu_second_handmarketplace.database.ProductManager;
import com.example.kmu_second_handmarketplace.database.UserManager;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    // 定义 UI 控件
    private EditText editTextUsername, editTextPassword;  // 用户名和密码输入框
    private Button buttonLogin, buttonRegister, buttonForgotPassword;  // 登录、注册和忘记密码按钮
    private UserManager userManager;  // 用户管理类，用于与数据库交互
    private UserAdapter userAdapter;  // 用户适配器（暂时未使用，注释掉了相关代码）
    private ArrayList<User> userList;  // 用户列表
    private ProductManager productManager;  // 商品管理类，负责商品操作（暂时未使用）

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);  // 设置当前活动的布局文件

        // 初始化 UI 控件
        editTextUsername = findViewById(R.id.editTextUsername);  // 获取用户名输入框
        editTextPassword = findViewById(R.id.editTextPassword);  // 获取密码输入框
        buttonLogin = findViewById(R.id.buttonLogin);  // 获取登录按钮
        buttonRegister = findViewById(R.id.buttonRegister);  // 获取注册按钮
        buttonForgotPassword = findViewById(R.id.buttonForgotPassword);  // 获取忘记密码按钮

        // 初始化 UserManager，用于用户的注册和登录操作
        userManager = new UserManager(this);  // 创建 UserManager 实例

        // 初始化用户列表和适配器（虽然当前未使用RecyclerView，相关代码被注释掉了）
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(userList);

        // （暂时注释掉）查询所有用户并显示
//        loadAllUsers();

        // 登录按钮点击事件
        buttonLogin.setOnClickListener(v -> {
            // 获取用户输入的用户名和密码
            String username = editTextUsername.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            // 检查用户输入是否为空
            if (username.isEmpty() || password.isEmpty()) {
                // 提示用户输入用户名和密码
                Toast.makeText(LoginActivity.this, "请输入邮箱和密码", Toast.LENGTH_SHORT).show();
            } else {
                // 使用 UserManager 进行登录验证
                boolean isLoginSuccessful = userManager.loginUser(username, password);

                if (isLoginSuccessful) {
                    // 登录成功后保存用户名到 SharedPreferences
                    saveCurrentUsername(username);

                    // 提示登录成功并跳转到主界面
                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();  // 登录成功后关闭当前登录页面
                } else {
                    // 登录失败，提示用户名或密码错误
                    Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 忘记密码按钮点击事件，跳转到密码重置页面
        buttonForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
            startActivity(intent);
        });

        // 注册按钮点击事件，跳转到注册页面
        buttonRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // （暂时注释掉）查看所有用户按钮点击事件，刷新用户列表
//        buttonViewAllUsers.setOnClickListener(v -> loadAllUsers());
    }

    // 保存当前用户名到 SharedPreferences（用于持久化存储当前用户的信息）
    private void saveCurrentUsername(String username) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);  // 获取 SharedPreferences 对象
        SharedPreferences.Editor editor = sharedPreferences.edit();  // 获取 SharedPreferences 编辑器
        editor.putString("currentUsername", username);  // 保存用户名到 SharedPreferences
        editor.apply();  // 提交保存
    }

    // 加载所有用户数据并显示（暂时注释掉）
    // private void loadAllUsers() {
    //     Cursor cursor = userManager.getAllUsers();  // 获取所有用户
    //     userList.clear();  // 清空当前用户列表

    //     // 遍历所有用户数据并添加到列表中
    //     if (cursor != null && cursor.moveToFirst()) {
    //         do {
    //             String nickname = cursor.getString(cursor.getColumnIndex("nickname"));
    //             String email = cursor.getString(cursor.getColumnIndex("email"));
    //             // 添加用户到列表
    //             userList.add(new User(nickname, email, null, null));
    //         } while (cursor.moveToNext());

    //         cursor.close();  // 关闭 Cursor
    //     }

    //     // 更新 RecyclerView（如果用户列表发生变化）
    //     userAdapter.notifyDataSetChanged();
    // }
}
