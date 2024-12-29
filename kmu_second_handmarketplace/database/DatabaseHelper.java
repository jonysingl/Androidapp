package com.example.kmu_second_handmarketplace.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "marketplace_db";
    private static final int DATABASE_VERSION = 5;  // 增加数据库版本号

    // 用户表字段
    public static final String USERS_TABLE = "users";
    public static final String COLUMN_USER_ID = "id";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_AVATAR = "avatar"; // 修改为 BLOB 类型
    public static final String COLUMN_NICKNAME = "nickname";
    public static final String COLUMN_PHONE = "phone";

    // 商品表字段
    public static final String PRODUCTS_TABLE = "products";
    public static final String COLUMN_PRODUCT_ID = "product_id";
    public static final String COLUMN_PRODUCT_NAME = "name";
    public static final String COLUMN_PRODUCT_PRICE = "price";
    public static final String COLUMN_PRODUCT_DESCRIPTION = "description";
    public static final String COLUMN_PRODUCT_IMAGE_RES_ID = "image_res_id";
    public static final String COLUMN_PRODUCT_CATEGORY = "category";  // 商品类别字段
    public static final String COLUMN_SELLER_NAME = "seller_name";  // 卖家用户名字段

    // 订单表字段
    public static final String ORDERS_TABLE = "orders";
    public static final String COLUMN_ORDER_ID = "order_id";
    public static final String COLUMN_ORDER_PRODUCT_ID = "product_id";  // 与商品表的外键关联
    public static final String COLUMN_ORDER_BUYER_NAME = "buyer_name";
    public static final String COLUMN_ORDER_DATE = "order_date";
    public static final String COLUMN_ORDER_STATUS = "order_status";  // 订单状态

    // 新增的字段
    public static final String COLUMN_ORDER_PRODUCT_NAME = "product_name";   // 商品名称
    public static final String COLUMN_ORDER_PRODUCT_PRICE = "product_price"; // 商品价格
    public static final String COLUMN_ORDER_PRODUCT_IMAGE_RES_ID = "product_image_res_id"; // 商品图片资源ID

    // 创建订单表的SQL语句
    private static final String CREATE_ORDERS_TABLE =
            "CREATE TABLE " + ORDERS_TABLE + " (" +
                    COLUMN_ORDER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_ORDER_PRODUCT_ID + " INTEGER, " +
                    COLUMN_ORDER_BUYER_NAME + " TEXT NOT NULL, " +
                    COLUMN_ORDER_DATE + " TEXT NOT NULL, " +
                    COLUMN_ORDER_STATUS + " TEXT, " +
                    COLUMN_ORDER_PRODUCT_NAME + " TEXT, " +  // 新增商品名称字段
                    COLUMN_ORDER_PRODUCT_PRICE + " TEXT, " + // 新增商品价格字段
                    COLUMN_ORDER_PRODUCT_IMAGE_RES_ID + " TEXT, " + // 新增商品图片字段
                    "FOREIGN KEY(" + COLUMN_ORDER_PRODUCT_ID + ") REFERENCES " + PRODUCTS_TABLE + "(" + COLUMN_PRODUCT_ID + "));";

    // 创建用户表的SQL语句（修改了 avatar 字段类型为 BLOB）
    private static final String CREATE_USERS_TABLE =
            "CREATE TABLE " + USERS_TABLE + " (" +
                    COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_EMAIL + " TEXT UNIQUE NOT NULL, " +
                    COLUMN_PASSWORD + " TEXT NOT NULL, " +
                    COLUMN_AVATAR + " BLOB, " +  // 修改为 BLOB 类型
                    COLUMN_NICKNAME + " TEXT, " +
                    COLUMN_PHONE + " TEXT);";

    // 创建商品表的SQL语句（增加了卖家字段）
    private static final String CREATE_PRODUCTS_TABLE =
            "CREATE TABLE " + PRODUCTS_TABLE + " (" +
                    COLUMN_PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_PRODUCT_NAME + " TEXT NOT NULL, " +
                    COLUMN_PRODUCT_PRICE + " TEXT NOT NULL, " +
                    COLUMN_PRODUCT_DESCRIPTION + " TEXT, " +
                    COLUMN_PRODUCT_IMAGE_RES_ID + " TEXT, " +  // 图片路径字段
                    COLUMN_PRODUCT_CATEGORY + " TEXT, " +      // 商品类别字段
                    COLUMN_SELLER_NAME + " TEXT);";            // 卖家用户名字段

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // 创建表
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USERS_TABLE);  // 创建用户表
        db.execSQL(CREATE_PRODUCTS_TABLE);  // 创建商品表
        db.execSQL(CREATE_ORDERS_TABLE);  // 创建订单表
    }

    // 升级数据库
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // 升级到版本2，添加商品表的category列
            db.execSQL("ALTER TABLE " + PRODUCTS_TABLE + " ADD COLUMN " + COLUMN_PRODUCT_CATEGORY + " TEXT;");
        }
        if (oldVersion < 3) {
            // 升级到版本3，添加卖家字段
            db.execSQL("ALTER TABLE " + PRODUCTS_TABLE + " ADD COLUMN " + COLUMN_SELLER_NAME + " TEXT;");
        }
        if (oldVersion < 4) {
            // 升级到版本4，创建订单表
            db.execSQL(CREATE_ORDERS_TABLE);  // 创建订单表
        }
        if (oldVersion < 5) {
            // 升级到版本5，修改用户表的 avatar 字段类型为 BLOB
            // 1. 创建临时表
            db.execSQL("CREATE TABLE users_temp (" +
                    COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_EMAIL + " TEXT UNIQUE NOT NULL, " +
                    COLUMN_PASSWORD + " TEXT NOT NULL, " +
                    COLUMN_AVATAR + " BLOB, " +  // 修改为 BLOB
                    COLUMN_NICKNAME + " TEXT, " +
                    COLUMN_PHONE + " TEXT);");

            // 2. 将原表数据迁移到临时表
            db.execSQL("INSERT INTO users_temp (" +
                    COLUMN_USER_ID + ", " +
                    COLUMN_EMAIL + ", " +
                    COLUMN_PASSWORD + ", " +
                    COLUMN_AVATAR + ", " +
                    COLUMN_NICKNAME + ", " +
                    COLUMN_PHONE + ") " +
                    "SELECT " +
                    COLUMN_USER_ID + ", " +
                    COLUMN_EMAIL + ", " +
                    COLUMN_PASSWORD + ", " +
                    COLUMN_AVATAR + ", " +
                    COLUMN_NICKNAME + ", " +
                    COLUMN_PHONE +
                    " FROM " + USERS_TABLE + ";");

            // 3. 删除原表
            db.execSQL("DROP TABLE " + USERS_TABLE);

            // 4. 重命名临时表为原表名
            db.execSQL("ALTER TABLE users_temp RENAME TO " + USERS_TABLE + ";");

            // 5. 添加订单表的新字段
            db.execSQL("ALTER TABLE " + ORDERS_TABLE + " ADD COLUMN " + COLUMN_ORDER_PRODUCT_NAME + " TEXT;");
            db.execSQL("ALTER TABLE " + ORDERS_TABLE + " ADD COLUMN " + COLUMN_ORDER_PRODUCT_PRICE + " TEXT;");
            db.execSQL("ALTER TABLE " + ORDERS_TABLE + " ADD COLUMN " + COLUMN_ORDER_PRODUCT_IMAGE_RES_ID + " TEXT;");
        }
    }
}
