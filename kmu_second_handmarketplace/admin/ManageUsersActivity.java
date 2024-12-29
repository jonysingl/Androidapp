package com.example.kmu_second_handmarketplace.admin;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kmu_second_handmarketplace.R;
import com.example.kmu_second_handmarketplace.User;
import com.example.kmu_second_handmarketplace.database.UserManager;

import java.util.List;

public class ManageUsersActivity extends AppCompatActivity {

    private static final String TAG = "AdminActivity";

    private EditText editTextUsername, editTextPhone, editTextEmail;
    private Button buttonAddUser, buttonSearchUser, buttonShowAllUsers;
    private RecyclerView recyclerViewUsers;
    private UserAdapter userAdapter;

    private UserManager userManager; // 数据库管理类

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);

        // 初始化控件
        initViews();

        // 初始化数据库管理类
        userManager = new UserManager(this);

        // 初始化 RecyclerView
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this));

        // 默认显示所有用户
        loadAllUsers();

        // 设置按钮点击事件
        setButtonListeners();
    }

    /**
     * 初始化控件
     */
    private void initViews() {
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextEmail = findViewById(R.id.editTextEmail);

        buttonAddUser = findViewById(R.id.buttonAddUser);
        buttonSearchUser = findViewById(R.id.buttonSearchUser);
        buttonShowAllUsers=findViewById(R.id.buttonShowAllUsers);


        recyclerViewUsers = findViewById(R.id.recyclerViewUsers);
    }

    /**
     * 设置按钮点击事件  增加用户
     */
    private void setButtonListeners() {
        buttonAddUser.setOnClickListener(v -> {
            String username = editTextUsername.getText().toString().trim();
            String phone = editTextPhone.getText().toString().trim();
            String email = editTextEmail.getText().toString().trim();

            if (!username.isEmpty() && !phone.isEmpty() && !email.isEmpty()) {
                // 添加用户
                boolean result = userManager.addUser(email, "123", username, phone, null);

                if (result) {
                    Log.d(TAG, "User added: " + username);
                    loadAllUsers(); // 刷新列表

                    // 清空输入框
                    editTextUsername.setText("");
                    editTextPhone.setText("");
                    editTextEmail.setText("");
                } else {
                    Toast.makeText(this, "Failed to add user", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            }
        });




        // 查询用户
        buttonSearchUser.setOnClickListener(v -> {
            String username = editTextUsername.getText().toString().trim();
            if (!username.isEmpty()) {
                List<User> users = userManager.searchUser(username);
                updateRecyclerView(users);
            }
        });

        // 显示全部用户
        buttonShowAllUsers.setOnClickListener(v -> loadAllUsers());
    }

    /**
     * 加载所有用户到 RecyclerView（默认功能）
     */
    private void loadAllUsers() {
        List<User> userList = userManager.getAllUsersAsList();
        updateRecyclerView(userList);
    }

    /**
     * 更新 RecyclerView 数据
     */
    private void updateRecyclerView(List<User> userList) {
        if (userAdapter == null) {
            userAdapter = new UserAdapter(this, userList, nickname -> {
                boolean result = userManager.deleteUser(nickname);
                if (result) {
                    Log.d(TAG, "User deleted: " + nickname);
                    loadAllUsers(); // 刷新数据
                }
            });
            recyclerViewUsers.setAdapter(userAdapter);
        } else {
            userAdapter.updateData(userList);
        }
    }
}
