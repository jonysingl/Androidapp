package com.example.kmu_second_handmarketplace;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.kmu_second_handmarketplace.database.UserManager;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextUsername, editTextPhoneNumber, editTextEmail, editTextPassword;
    private Button buttonRegister;
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize the views
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPhoneNumber = findViewById(R.id.telephonenumber);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonRegister = findViewById(R.id.buttonRegister);

        // Initialize the UserManager
        userManager = new UserManager(this);

        // 注册按钮点击事件
        buttonRegister.setOnClickListener(v -> {
            String username = editTextUsername.getText().toString().trim();
            String phoneNumber = editTextPhoneNumber.getText().toString().trim();
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            // 验证输入
            if (TextUtils.isEmpty(username)) {
                Toast.makeText(RegisterActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(phoneNumber)) {
                Toast.makeText(RegisterActivity.this, "手机号不能为空", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(RegisterActivity.this, "邮箱不能为空", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(RegisterActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                return;
            }

            // 注册用户
            boolean isRegistered = userManager.registerUser(email, password, username, phoneNumber);


            if (isRegistered) {
                Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                finish();  // 注册成功后关闭当前页面
            } else {
                Toast.makeText(RegisterActivity.this, "注册失败，请重试", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
