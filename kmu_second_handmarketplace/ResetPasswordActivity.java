package com.example.kmu_second_handmarketplace;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kmu_second_handmarketplace.database.UserManager;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextPhone, editTextNewPassword;
    private Button buttonResetPassword;
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetpassword);

        // 初始化控件
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextNewPassword = findViewById(R.id.editTextNewPassword);
        buttonResetPassword = findViewById(R.id.buttonResetPassword);

        // 初始化 UserManager
        userManager = new UserManager(this);

        // 设置按钮点击事件
        buttonResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString().trim();
                String phone = editTextPhone.getText().toString().trim();
                String newPassword = editTextNewPassword.getText().toString().trim();

                // 检查输入是否为空
                if (email.isEmpty() || phone.isEmpty() || newPassword.isEmpty()) {
                    Toast.makeText(ResetPasswordActivity.this, "请输入完整信息", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 调用 UserManager 的 resetPassword 方法
                boolean isResetSuccessful = userManager.resetPassword(email, phone, newPassword);

                if (isResetSuccessful) {
                    Toast.makeText(ResetPasswordActivity.this, "密码重置成功，请返回登录", Toast.LENGTH_SHORT).show();
                    finish(); // 关闭当前活动，返回登录界面
                } else {
                    Toast.makeText(ResetPasswordActivity.this, "邮箱或手机号错误，密码重置失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
