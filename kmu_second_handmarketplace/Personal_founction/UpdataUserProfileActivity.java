package com.example.kmu_second_handmarketplace.Personal_founction;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kmu_second_handmarketplace.R;
import com.example.kmu_second_handmarketplace.database.UserManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class UpdataUserProfileActivity extends AppCompatActivity {

    // 声明界面上的控件
    private ImageView imageViewProfile;  // 用于显示用户头像的 ImageView
    private EditText editTextUsername, editTextEmail, editTextPhone;  // 用户名、邮箱和手机号的输入框
    private Button buttonUploadAvatar, buttonSaveProfile;  // 上传头像和保存个人信息的按钮

    private UserManager userManager;  // 管理用户信息的 UserManager 实例
    private static final int IMAGE_PICK_CODE = 1000;  // 定义图片选择请求码
    private String avatarPath;  // 用于保存选中的头像图片路径，稍后转为字节数组保存

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);  // 设置当前界面的布局文件

        // 初始化界面控件
        imageViewProfile = findViewById(R.id.imageViewProfile);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPhone = findViewById(R.id.editTextPhone);
        buttonUploadAvatar = findViewById(R.id.buttonUploadAvatar);
        buttonSaveProfile = findViewById(R.id.buttonSaveProfile);

        // 初始化 UserManager 实例，用于用户数据的操作
        userManager = new UserManager(this);

        // 点击保存按钮时保存用户信息
        buttonSaveProfile.setOnClickListener(v -> saveUserProfile());

        // 点击上传头像按钮时打开图片选择器
        buttonUploadAvatar.setOnClickListener(v -> openImagePicker());
    }

    // 打开图片选择器，允许用户选择头像图片
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);  // 创建一个选择图片的 Intent
        intent.setType("image/*");  // 设置Intent的类型为图片
        startActivityForResult(intent, IMAGE_PICK_CODE);  // 启动图片选择器，并设置请求码为 IMAGE_PICK_CODE
    }

    // 处理从图片选择器返回的结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 判断请求码是否匹配，并且用户选择了图片
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE && data != null) {
            Uri selectedImageUri = data.getData();  // 获取用户选择的图片 URI
            imageViewProfile.setImageURI(selectedImageUri);  // 将选中的图片设置到 ImageView 中显示
            avatarPath = selectedImageUri.toString();  // 保存选择的图片路径
        }
    }

    // 保存用户信息的方法
    private void saveUserProfile() {
        // 获取用户输入的信息
        String username = editTextUsername.getText().toString().trim();
        String newEmail = editTextEmail.getText().toString().trim();
        String newPhone = editTextPhone.getText().toString().trim();

        // 检查输入的必填字段是否为空
        if (username.isEmpty() || newEmail.isEmpty() || newPhone.isEmpty()) {
            Toast.makeText(this, "用户名、电子邮件和手机号码不能为空", Toast.LENGTH_SHORT).show();
            return;  // 如果有空字段，则提示并返回，不进行保存操作
        }

        // 根据用户名获取当前用户的信息
        Cursor cursor = userManager.getUserByUsername(username);
        if (cursor != null && cursor.moveToFirst()) {
            // 调用 UserManager 更新用户信息
            boolean isUpdated = userManager.updateUserInfo(username, newEmail, newPhone);
            if (isUpdated) {
                Toast.makeText(this, "个人信息更新成功", Toast.LENGTH_SHORT).show();
                // 输出所有用户信息，帮助调试
                userManager.logAllUsers();  // 输出所有用户记录
            } else {
                Toast.makeText(this, "个人信息更新失败", Toast.LENGTH_SHORT).show();
            }

            // 如果选择了新的头像，更新头像信息
            if (avatarPath != null && !avatarPath.isEmpty()) {
                // 将选择的头像路径转换为字节数组
                byte[] avatarBytes = convertImageUriToByteArray(avatarPath);

                // 调用 UserManager 更新头像
                boolean isAvatarUpdated = userManager.updateUserAvatar(username, avatarBytes);
                if (isAvatarUpdated) {
                    Toast.makeText(this, "头像更新成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "头像更新失败", Toast.LENGTH_SHORT).show();
                }
            }
            cursor.close();  // 关闭数据库游标
        } else {
            Toast.makeText(this, "未找到用户，检查用户名是否正确", Toast.LENGTH_SHORT).show();
        }
    }

    // 将图片 URI 转换为字节数组
    private byte[] convertImageUriToByteArray(String imageUri) {
        try {
            // 解析 URI 字符串为 Uri 对象
            Uri uri = Uri.parse(imageUri);
            // 通过 ContentResolver 获取图片并转换为 Bitmap 对象
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            // 创建字节数组输出流，用于将 Bitmap 压缩为字节数组
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            // 将 Bitmap 压缩为 JPEG 格式，质量为 100，并写入字节数组输出流
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            // 返回字节数组
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();  // 捕获异常并打印堆栈信息
            return null;  // 如果发生异常，返回 null
        }
    }
}
