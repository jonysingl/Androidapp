package com.example.kmu_second_handmarketplace;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.Serializable;

public class User implements Serializable {

    private String nickname;
    private String email;
    private String phone;
    private byte[] avatar;  // 存储用户头像的字节数组

    // 构造函数，用于创建User对象
    public User(String nickname, String email, String phone, byte[] avatar) {
        this.nickname = nickname;
        this.email = email;
        this.phone = phone;
        this.avatar = avatar;
    }

    // Getter 和 Setter 方法
    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public byte[] getAvatar() {
        return avatar;
    }

    public void setAvatar(byte[] avatar) {
        this.avatar = avatar;
    }

    // 可选：转换字节数组为 Bitmap 进行头像展示（可以使用 Glide 或其他库加载）
    public Bitmap getAvatarBitmap() {
        if (avatar != null) {
            return BitmapFactory.decodeByteArray(avatar, 0, avatar.length);
        }
        return null;
    }
}
