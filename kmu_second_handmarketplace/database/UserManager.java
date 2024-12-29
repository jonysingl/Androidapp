package com.example.kmu_second_handmarketplace.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.kmu_second_handmarketplace.User;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class UserManager {

    private SQLiteDatabase db;  // 用于进行数据库操作的SQLiteDatabase对象
    private DatabaseHelper dbHelper;
    // 设置默认头像，路径可以是 drawable 资源 ID
    String defaultAvatar = "android.resource://com.example.kmu_second_handmarketplace/drawable/default_avatar";

    // 构造函数，初始化DatabaseHelper实例
    public UserManager(Context context) {
        dbHelper = new DatabaseHelper(context);  // 创建一个DatabaseHelper对象
    }

    // 用户注册方法
    public boolean registerUser(String email, String password, String nickname, String phone) {
        db = dbHelper.getWritableDatabase();  // 获取一个可写的数据库对象

        // 创建一个ContentValues对象来存储用户的注册信息
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_EMAIL, email);  // 存储用户的邮箱
        values.put(DatabaseHelper.COLUMN_PASSWORD, password);  // 存储密码
        values.put(DatabaseHelper.COLUMN_NICKNAME, nickname);  // 存储用户的昵称
        values.put(DatabaseHelper.COLUMN_AVATAR, defaultAvatar);  // 存储用户的头像
        values.put(DatabaseHelper.COLUMN_PHONE, phone);  // 存储用户的手机号

        try {
            // 执行插入操作，将用户数据存入数据库
            long result = db.insert(DatabaseHelper.USERS_TABLE, null, values);
            Log.d("RegisterActivity", "User registered, result: " + result);
            // 如果插入成功，返回 true，否则返回 false
            return result != -1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;  // 捕获异常，如果插入失败返回 false
        } finally {
            db.close();  // 最后关闭数据库连接
        }
    }

    // 用户登录方法
    public boolean loginUser(String username, String password) {
        db = dbHelper.getReadableDatabase();  // 获取一个可读的数据库对象

        // 构建查询语句，依据用户名（昵称）查找对应的加密密码
        String query = "SELECT " + DatabaseHelper.COLUMN_PASSWORD + " FROM " +
                DatabaseHelper.USERS_TABLE + " WHERE " + DatabaseHelper.COLUMN_NICKNAME + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username});  // 执行查询

        if (cursor.moveToFirst()) {//moveToFirst()表示至少有一行数据
            // 如果查询到结果，获取数据库中存储的加密密码
            String storedPassword = cursor.getString(0);
            Log.d("LoginActivity", "Stored Password: " + storedPassword);
            cursor.close();  // 关闭Cursor对象

            // 使用verifyPassword方法验证输入的密码是否与数据库中存储的加密密码匹配
            return verifyPassword(password, storedPassword);
        } else {
            cursor.close();  // 如果没有找到对应的用户，关闭Cursor
            return false;  // 用户名不存在，登录失败
        }
    }

    // 验证密码：对比输入密码和数据库中存储的加密密码
    private boolean verifyPassword(String inputPassword, String storedPassword) {
        // 比较输入的密码是否与数据库中存储的加密密码一致
        return inputPassword.equals(storedPassword);
    }

    // 根据用户名获取用户信息
    public Cursor getUserByUsername(String username) {
        db = dbHelper.getReadableDatabase();  // 获取一个可读的数据库对象

        // 根据用户名查询用户的电子邮件、手机号码和头像
        String query = "SELECT " + DatabaseHelper.COLUMN_EMAIL + ", " +
                DatabaseHelper.COLUMN_PHONE + ", " +
                DatabaseHelper.COLUMN_AVATAR + " FROM " + DatabaseHelper.USERS_TABLE +
                " WHERE " + DatabaseHelper.COLUMN_NICKNAME + " = ?";

        return db.rawQuery(query, new String[]{username});  // 返回查询结果的Cursor对象
    }

    // 更新用户信息（包括电子邮件和手机号码）依据用户名
    public boolean updateUserInfo(String username, String newEmail, String newPhone) {
        db = dbHelper.getWritableDatabase();  // 获取一个可写的数据库对象

        // 创建ContentValues对象来存储新的用户信息
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_EMAIL, newEmail);  // 新的电子邮件
        values.put(DatabaseHelper.COLUMN_PHONE, newPhone);  // 新的手机号

        // 执行更新操作，依据用户名更新用户信息（电子邮件和手机号）
        int rowsAffected = db.update(DatabaseHelper.USERS_TABLE, values,
                DatabaseHelper.COLUMN_NICKNAME + " = ?", new String[]{username});

        db.close();  // 关闭数据库连接
        return rowsAffected > 0;  // 如果更新了至少一行数据，返回 true
    }

    // 更新用户头像依据用户名，头像以字节数组存储
    public boolean updateUserAvatar(String username, byte[] avatarBytes) {
        db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_AVATAR, avatarBytes);  // 保存字节数组而非路径

        int rowsAffected = db.update(DatabaseHelper.USERS_TABLE, values,
                DatabaseHelper.COLUMN_NICKNAME + " = ?", new String[]{username});

        db.close();
        return rowsAffected > 0;
    }

    // 重置密码：通过邮箱和手机号验证身份后重置密码
    public boolean resetPassword(String email, String phone, String newPassword) {
        db = dbHelper.getWritableDatabase();  // 获取一个可写的数据库对象

        // 构建查询语句，验证邮箱和手机号是否匹配
        String query = "SELECT " + DatabaseHelper.COLUMN_EMAIL + " FROM " +
                DatabaseHelper.USERS_TABLE + " WHERE " +
                DatabaseHelper.COLUMN_EMAIL + " = ? AND " +
                DatabaseHelper.COLUMN_PHONE + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email, phone});  // 执行查询

        if (cursor.moveToFirst()) {
            // 如果查询到匹配的用户，进行密码更新
            ContentValues values = new ContentValues();
            String encryptedPassword = newPassword;  // 加密新密码
            values.put(DatabaseHelper.COLUMN_PASSWORD, encryptedPassword);  // 更新密码

            // 执行更新操作，依据邮箱更新密码
            int rowsAffected = db.update(DatabaseHelper.USERS_TABLE, values,
                    DatabaseHelper.COLUMN_EMAIL + " = ?", new String[]{email});

            cursor.close();  // 关闭Cursor对象
            db.close();  // 关闭数据库连接
            return rowsAffected > 0;  // 如果更新了至少一行数据，返回 true
        } else {
            cursor.close();  // 如果没有找到匹配的邮箱和手机号，关闭Cursor
            db.close();  // 关闭数据库连接
            return false;  // 身份验证失败，返回 false
        }
    }

    // 获取所有注册用户信息（仅用于测试）
// 获取所有用户的所有记录（包括所有字段）
    public void logAllUsers() {
        db = dbHelper.getReadableDatabase();  // 获取一个可读的数据库对象

        // 查询所有用户的所有字段
        String query = "SELECT * FROM " + DatabaseHelper.USERS_TABLE;

        Cursor cursor = db.rawQuery(query, null);  // 执行查询

        if (cursor != null && cursor.moveToFirst()) {
            do {
                // 获取每一列的值
                int userId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_ID));
                String email = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_EMAIL));
                String password = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PASSWORD));
                byte[] avatar = cursor.getBlob(cursor.getColumnIndex(DatabaseHelper.COLUMN_AVATAR));
                String nickname = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NICKNAME));
                String phone = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PHONE));

                // 输出到日志
                Log.d("UserManager", "User ID: " + userId + ", Email: " + email + ", Password: " + password +
                        ", Avatar: " + (avatar != null ? "Available" : "Not Available") +
                        ", Nickname: " + nickname + ", Phone: " + phone);
            } while (cursor.moveToNext());

            cursor.close();  // 关闭Cursor
        } else {
            Log.d("UserManager", "No users found.");
        }

        db.close();  // 关闭数据库连接
    }
    // 清空用户表中的所有记录
    public void clearUsersTable() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            db.execSQL("DELETE FROM " + DatabaseHelper.USERS_TABLE);  // 删除所有数据
            Log.d("UserManager", "All users have been deleted.");
        } catch (Exception e) {
            Log.e("UserManager", "Error clearing users table: " + e.getMessage());
        } finally {
            db.close();  // 确保关闭数据库连接
        }
    }
        /**
         * 新增用户
         * @param email 用户邮箱
         * @param password 用户密码
         * @param nickname 用户昵称
         * @param phone 用户手机号
         * @param avatarBytes 用户头像（字节数组，可选）
         * @return 是否新增成功
         */
        public boolean addUser(String email, String password, String nickname, String phone, byte[] avatarBytes) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_EMAIL, email);
            values.put(DatabaseHelper.COLUMN_PASSWORD, password);
            values.put(DatabaseHelper.COLUMN_NICKNAME, nickname);
            values.put(DatabaseHelper.COLUMN_PHONE, phone);
            values.put(DatabaseHelper.COLUMN_AVATAR, avatarBytes != null ? avatarBytes : defaultAvatar.getBytes());

            try {
                long result = db.insert(DatabaseHelper.USERS_TABLE, null, values);
                return result != -1;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            } finally {
                db.close();
            }
        }

        /**
         * 删除用户
         * @param username 用户昵称
         * @return 是否删除成功
         */
        public boolean deleteUser(String username) {
            SQLiteDatabase db = dbHelper.getWritableDatabase(); // 获取可写数据库对象

            try {
                // 删除操作，依据用户昵称
                int rowsAffected = db.delete(DatabaseHelper.USERS_TABLE,
                        DatabaseHelper.COLUMN_NICKNAME + " = ?",
                        new String[]{username});

                Log.d("UserManager", "Deleted rows: " + rowsAffected);
                return rowsAffected > 0; // 如果删除了至少一行数据，则返回 true
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            } finally {
                db.close(); // 确保关闭数据库连接
            }
        }
    /**
     * 查询所有用户信息
     * @return List<User> 包含所有用户数据
     */
    public List<User> getAllUsersAsList() {
        List<User> userList = new ArrayList<>(); // 用于保存所有用户的列表
        SQLiteDatabase db = dbHelper.getReadableDatabase(); // 获取可读数据库对象
        Cursor cursor = null;

        try {
            // 查询用户表的所有字段
            String query = "SELECT * FROM " + DatabaseHelper.USERS_TABLE;
            cursor = db.rawQuery(query, null); // 执行查询操作

            // 遍历查询结果
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    // 从 Cursor 中获取每一列的值
                    String nickname = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NICKNAME));
                    String email = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EMAIL));
                    String phone = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PHONE));
                    byte[] avatar = cursor.getBlob(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_AVATAR));

                    // 创建 User 对象并加入列表
                    User user = new User(nickname, email, phone, avatar);
                    userList.add(user);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("UserManager", "Error retrieving users: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close(); // 关闭 Cursor
            db.close(); // 关闭数据库连接
        }

        return userList; // 返回所有用户的列表
    }

    // 修改后的 getUserByUsername 方法
    public List<User> searchUser(String username) {
        List<User> userList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;

        try {
            // 构建查询语句，查询指定用户名的用户信息
            String query = "SELECT " + DatabaseHelper.COLUMN_EMAIL + ", " +
                    DatabaseHelper.COLUMN_PHONE + ", " +
                    DatabaseHelper.COLUMN_AVATAR + " FROM " +
                    DatabaseHelper.USERS_TABLE + " WHERE " +
                    DatabaseHelper.COLUMN_NICKNAME + " = ?";
            cursor = db.rawQuery(query, new String[]{username});  // 执行查询

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    // 从Cursor中获取用户信息
                    String email = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EMAIL));
                    String phone = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PHONE));
                    byte[] avatar = cursor.getBlob(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_AVATAR));

                    // 创建 User 对象
                    User user = new User(username, email, phone, avatar);
                    userList.add(user);  // 将用户对象加入列表
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("UserManager", "Error retrieving user by username: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();  // 确保关闭Cursor
            db.close();  // 确保关闭数据库连接
        }

        return userList;  // 返回符合条件的用户列表
    }




}
