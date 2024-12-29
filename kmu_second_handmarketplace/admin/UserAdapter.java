package com.example.kmu_second_handmarketplace.admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kmu_second_handmarketplace.R;
import com.example.kmu_second_handmarketplace.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList; // 用户数据列表
    private Context context;
    private OnDeleteClickListener deleteClickListener;

    // 定义删除回调接口
    public interface OnDeleteClickListener {
        void onDeleteClick(String nickname);
    }

    // 构造函数
    public UserAdapter(Context context, List<User> userList, OnDeleteClickListener deleteClickListener) {
        this.context = context;
        this.userList = userList;
        this.deleteClickListener = deleteClickListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        // 获取用户对象
        User user = userList.get(position);

        // 绑定用户数据到视图
        holder.textViewUserName.setText("用户名: " + user.getNickname());
        holder.textViewUserPhone.setText("手机号: " + user.getPhone());
        holder.textViewUserEmail.setText("邮箱: " + user.getEmail());

        // 设置删除按钮的点击事件
        holder.buttonDelete.setOnClickListener(v -> {
            if (deleteClickListener != null) {
                deleteClickListener.onDeleteClick(user.getNickname());
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    // 刷新数据
    public void updateData(List<User> newUserList) {
        this.userList = newUserList;
        notifyDataSetChanged();
    }

    // ViewHolder 类
    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView textViewUserName, textViewUserPhone, textViewUserEmail;
        Button buttonDelete;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewUserName = itemView.findViewById(R.id.textViewUserName);
            textViewUserPhone = itemView.findViewById(R.id.textViewUserPhone);
            textViewUserEmail = itemView.findViewById(R.id.textViewUserEmail);
            buttonDelete = itemView.findViewById(R.id.buttonDeleteUser);
        }
    }
}
