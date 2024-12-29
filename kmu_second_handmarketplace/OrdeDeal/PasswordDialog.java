package com.example.kmu_second_handmarketplace.OrdeDeal;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.kmu_second_handmarketplace.R;

public class PasswordDialog {

    private Context context;  // 用于创建对话框的上下文环境
    private OnPasswordEnteredListener listener;  // 密码输入后的回调监听器

    /**
     * 构造方法，初始化 PasswordDialog 类
     *
     * @param context 当前的上下文，用于创建对话框
     * @param listener 密码输入后触发的回调接口
     */
    public PasswordDialog(Context context, OnPasswordEnteredListener listener) {
        this.context = context;
        this.listener = listener;  // 保存回调接口的引用
    }

    /**
     * 显示密码输入对话框
     * 使用 AlertDialog 创建自定义的密码输入界面
     */
    public void show() {
        // 创建 AlertDialog.Builder 对象，用于构建对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Enter Payment Password");  // 设置对话框的标题

        // 获取自定义布局，显示密码输入框
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_password_input, null);
        EditText editTextPassword = view.findViewById(R.id.editTextPassword);  // 获取布局中的 EditText 控件，用于输入密码
        builder.setView(view);  // 将自定义布局添加到对话框中

        // 设置 "确认" 按钮，点击后触发密码输入监听器的回调
        builder.setPositiveButton("Confirm", (dialog, which) -> {
            // 获取用户输入的密码
            String password = editTextPassword.getText().toString();
            if (listener != null) {  // 如果回调监听器不为 null
                listener.onPasswordEntered(password);  // 调用回调方法，将输入的密码传递出去
            }
        });

        // 设置 "取消" 按钮，点击后关闭对话框
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        // 显示对话框
        builder.show();
    }

    /**
     * 密码输入监听器接口
     * 该接口用于在密码输入完成后执行某些操作
     */
    public interface OnPasswordEnteredListener {
        void onPasswordEntered(String password);  // 密码输入完成后的回调方法
    }
}
