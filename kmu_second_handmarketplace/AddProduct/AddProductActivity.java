package com.example.kmu_second_handmarketplace.AddProduct;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.kmu_second_handmarketplace.R;
import com.example.kmu_second_handmarketplace.database.ProductManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AddProductActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;  // 定义常量，表示图片选择请求的代码

    // 声明UI组件
    private EditText editTextProductName, editTextProductDescription, editTextProductPrice, editTextSellerName;
    private Spinner spinnerProductCategory;  // 商品类别下拉框
    private Button buttonUploadImage, buttonSubmitProduct;  // 上传图片按钮和提交商品按钮
    private ImageView imageViewProductImage;  // 显示商品图片的ImageView
    private Uri imageUri;  // 存储选择图片的URI

    private ProductManager productManager;  // ProductManager实例，用于处理商品数据库操作

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);  // 设置该活动的布局文件

        // 初始化视图组件
        editTextProductName = findViewById(R.id.editTextProductName);
        editTextProductDescription = findViewById(R.id.editTextProductDescription);
        editTextProductPrice = findViewById(R.id.editTextProductPrice);
        editTextSellerName = findViewById(R.id.editTextSellerName);
        spinnerProductCategory = findViewById(R.id.spinnerProductCategory);
        buttonUploadImage = findViewById(R.id.buttonUploadImage);
        buttonSubmitProduct = findViewById(R.id.buttonSubmitProduct);
        imageViewProductImage = findViewById(R.id.imageViewProductImage);

        // 初始化 ProductManager 实例，用于处理商品相关的数据库操作
        productManager = new ProductManager(this);

        // 设置商品类别下拉框（Spinner）的内容
        String[] categories = {"电子产品", "家具", "服饰", "图书", "其他"};
        // 创建ArrayAdapter并设置下拉框的显示内容
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProductCategory.setAdapter(categoryAdapter);  // 设置Spinner的适配器

        // 设置上传图片按钮的点击事件监听
        buttonUploadImage.setOnClickListener(v -> openFileChooser());  // 点击按钮时调用打开文件选择器的方法

        // 设置提交商品按钮的点击事件监听
        buttonSubmitProduct.setOnClickListener(v -> submitProduct());  // 点击按钮时调用提交商品的方法
    }

    // 打开文件选择器以选择图片
    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");  // 设置选择的文件类型为图片
        startActivityForResult(Intent.createChooser(intent, "选择商品图片"), PICK_IMAGE_REQUEST);  // 启动选择图片的Activity，并请求返回结果
    }
//    使用 Intent.ACTION_PICK 启动选择文件的动作。
//    设置选择类型为 image/*，表示选择的文件必须是图片。
//    通过 startActivityForResult 启动选择器，并使用 PICK_IMAGE_REQUEST 常量来标记这个请求，以便稍后处理返回结果。
//    Intent.createChooser(intent, "选择商品图片") 创建一个对话框，提供给用户选择文件的方式（如图库、相册等）。

    /**
     * 处理从图片选择器返回的结果
     *
     * 当用户选择图片后，系统会回调这个方法，处理图片选择的结果。
     * 该方法会根据返回的请求码、结果码和数据，获取用户选择的图片并显示在界面上。
     *
     * @param requestCode 请求码，用来标识请求的操作
     *                    在这里，我们通过它来确定是从图片选择器中返回的结果
     * @param resultCode 返回结果码，表示操作的结果
     *                    如果是成功返回，则 `resultCode` 为 `RESULT_OK`
     * @param data 包含返回数据的 `Intent` 对象，数据中可能包含了用户选择的图片URI
     *             该参数是一个 `Intent`，用来传递图片的URI（Uniform Resource Identifier）
     *             URI表示选中的图片文件在设备中的位置
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 判断请求码是否为图片选择请求（PICK_IMAGE_REQUEST），且返回结果是成功（RESULT_OK）
        // 如果数据不为null，则表示用户已经成功选择了图片
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            // 获取从选择器返回的图片URI，这个URI指向图片在设备中的位置
            imageUri = data.getData();

            // 如果URI不为null，表示成功获取了图片的URI
            if (imageUri != null) {
                try {
                    // 使用ContentResolver获取图片URI对应的Bitmap对象
                    // getContentResolver() 是一个系统服务，用于访问设备上的文件或内容提供者
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);

                    // 将获取到的Bitmap对象设置到ImageView中显示在UI界面上
                    imageViewProductImage.setImageBitmap(bitmap);
                } catch (IOException e) {
                    // 捕获IOException异常，处理图像加载失败的情况
                    e.printStackTrace();

                    // 如果无法加载图片，显示提示信息给用户
                    Toast.makeText(this, "无法加载图片", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    // 提交商品的方法
    private void submitProduct() {
        // 获取用户输入的商品信息
        String productName = editTextProductName.getText().toString().trim();
        String productDescription = editTextProductDescription.getText().toString().trim();
        String productPrice = editTextProductPrice.getText().toString().trim();
        String productCategory = spinnerProductCategory.getSelectedItem().toString();  // 获取选中的商品类别
        String sellerName = editTextSellerName.getText().toString().trim();

        // 校验输入内容是否为空，若有空项则提示用户
        if (TextUtils.isEmpty(productName)) {
            Toast.makeText(this, "请输入商品名称", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(productDescription)) {
            Toast.makeText(this, "请输入商品描述", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(productPrice)) {
            Toast.makeText(this, "请输入商品价格", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(sellerName)) {
            Toast.makeText(this, "请输入卖家昵称", Toast.LENGTH_SHORT).show();
        } else {
            byte[] imageByteArray = null;

            // 如果选了图片，则将图片转换为字节数组
            if (imageUri != null) {
                imageByteArray = getImageByteArray(imageUri);  // 调用方法获取图片的字节数组
            } else {
                // 如果没有选择图片，则使用默认图片
                imageByteArray = getDefaultImageByteArray();  // 获取默认图片的字节数组
            }

            // 调用ProductManager的addProduct方法，插入商品数据到数据库，并获取返回的商品ID
            long productId = productManager.addProduct(productName, productPrice, productDescription, imageByteArray, productCategory, sellerName);

            // 如果商品添加成功，返回商品ID
            if (productId != -1) {
                Toast.makeText(this, "商品发布成功，商品 ID: " + productId, Toast.LENGTH_SHORT).show();
                productManager.logAllProducts();  // 打印所有商品信息

                // 清空输入框和图片，准备下一次输入
                editTextProductName.setText("");
                editTextProductDescription.setText("");
                editTextProductPrice.setText("");
                editTextSellerName.setText("");
                imageViewProductImage.setImageResource(R.drawable.defaut_avatar);  // 设置默认图片
            } else {
                Toast.makeText(this, "商品发布失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 将选中的图片URI转换为字节数组
     *
     * @param imageUri 选中图片的URI（统一资源标识符），指向设备中图片的存储位置。
     *                 URI是Android中用来表示资源位置的一种方式，指向外部存储或内容提供者。
     * @return 返回图片的字节数组，如果发生异常则返回null。
     *         字节数组可以用于存储、上传或处理图片。
     */
    private byte[] getImageByteArray(Uri imageUri) {
        try {
            // 使用ContentResolver根据图片的URI获取对应的Bitmap对象
            // getContentResolver() 方法获取一个 ContentResolver 用于访问设备上的数据
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);

            // 创建一个字节数组输出流，用于将图片数据写入到字节数组中
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            // 将Bitmap对象压缩成JPEG格式的字节流
            // Bitmap.CompressFormat.JPEG表示将图片压缩为JPEG格式
            // 100表示压缩质量，100表示最高质量，0表示最低质量
            // 将压缩后的数据写入到byteArrayOutputStream流中
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

            // 将字节流转换为字节数组并返回
            // 此时byteArrayOutputStream中存储的就是图片的字节数据
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            // 捕获IOException异常，如果发生错误（如无法获取Bitmap），则打印异常信息
            e.printStackTrace();
            return null;  // 返回null表示发生了异常，无法处理图片
        }
    }


    // 获取默认图片的字节数组
    private byte[] getDefaultImageByteArray() {
        // 解码默认图片资源为Bitmap对象
        Bitmap defaultBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.defaut_avatar);
        // 将Bitmap图像压缩并写入字节数组输出流
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        defaultBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);  // 压缩为JPEG格式
        return stream.toByteArray();  // 返回默认图片的字节数组
    }
}
