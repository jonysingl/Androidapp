package com.example.kmu_second_handmarketplace.editProduct;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.kmu_second_handmarketplace.R;
import com.example.kmu_second_handmarketplace.database.ProductManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class EditProductActivity extends AppCompatActivity {

    private EditText editTextSellerName, editTextProductName, editTextProductDescription, editTextProductPrice;
    private Spinner spinnerProductCategory;
    private Button buttonUploadImage, buttonSubmitProduct;
    private ImageView imageViewProductImage;
    private int productId; // 要编辑的商品ID
    private Bitmap imageBitmap; // 存储上传的商品图片
    private Uri imageUri;  // 商品图片的URI

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        // 获取商品ID和其他信息
        productId = getIntent().getIntExtra("productId", -1); // 获取传递的商品ID
        String productName = getIntent().getStringExtra("product_name");
        String productPrice = getIntent().getStringExtra("product_price");
        String productDescription = getIntent().getStringExtra("product_description");
        byte[] imageResId = getIntent().getByteArrayExtra("product_image"); // 获取商品图片
        String productCategory = getIntent().getStringExtra("product_category");
        String sellerName = getIntent().getStringExtra("product_seller_name");

        // 初始化视图组件
        editTextSellerName = findViewById(R.id.editTextSellerName);
        editTextProductName = findViewById(R.id.editTextProductName);
        editTextProductDescription = findViewById(R.id.editTextProductDescription);
        editTextProductPrice = findViewById(R.id.editTextProductPrice);
        spinnerProductCategory = findViewById(R.id.spinnerProductCategory);
        buttonUploadImage = findViewById(R.id.buttonUploadImage);
        buttonSubmitProduct = findViewById(R.id.buttonSubmitProduct);
        imageViewProductImage = findViewById(R.id.imageViewProductImage);

        // 填充现有商品数据
        editTextSellerName.setText(sellerName);
        editTextProductName.setText(productName);
        editTextProductDescription.setText(productDescription);
        editTextProductPrice.setText(productPrice);

        // 填充商品图片，如果有的话
        if (imageResId != null && imageResId.length > 0) {
            imageBitmap = BitmapFactory.decodeByteArray(imageResId, 0, imageResId.length);
            imageViewProductImage.setImageBitmap(imageBitmap);
        }

        // 设置商品类别的适配器
        String[] categories = {"电子产品", "家具", "服饰", "图书", "其他"}; // 商品类别列表
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        spinnerProductCategory.setAdapter(adapter);

        // 设置当前商品的类别为选中的类别
        int categoryPosition = adapter.getPosition(productCategory);
        spinnerProductCategory.setSelection(categoryPosition);

        // 设置上传图片按钮的点击事件
        buttonUploadImage.setOnClickListener(v -> openFileChooser());

        // 设置提交按钮的点击事件
        buttonSubmitProduct.setOnClickListener(v -> submitProduct());
    }

    // 上传商品图片的方法
    private void openFileChooser() {
        // 启动图片选择器，选择图片
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "选择商品图片"), 1);
    }

    // 处理图片选择的结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();  // 获取选择的图片URI
            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);  // 获取图片Bitmap
                imageViewProductImage.setImageBitmap(imageBitmap);  // 显示图片
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "无法加载图片", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 提交商品的方法，调用更新商品的 API
    private void submitProduct() {
        String sellerName = editTextSellerName.getText().toString().trim();
        String productName = editTextProductName.getText().toString().trim();
        String productDescription = editTextProductDescription.getText().toString().trim();
        String productPrice = editTextProductPrice.getText().toString().trim();
        String category = spinnerProductCategory.getSelectedItem().toString(); // 获取选择的商品类别

        // 检查必填字段是否为空
        if (productName.isEmpty() || productDescription.isEmpty() || productPrice.isEmpty() || sellerName.isEmpty()) {
            Toast.makeText(this, "请填写所有字段", Toast.LENGTH_SHORT).show();
            return;
        }

        // 转换图片为字节数组
        byte[] imageByteArray = imageBitmap != null ? getImageByteArray(imageBitmap) : getDefaultImageByteArray();

        // 创建 ProductManager 实例来更新商品
        ProductManager productManager = new ProductManager(this);

        // 调用更新商品方法，传递参数进行更新
        boolean result = productManager.updateProduct(
                productId, productName, productPrice, productDescription, imageByteArray, category, sellerName
        );

        // 根据更新是否成功给出反馈
        if (result) {
            Toast.makeText(this, "商品信息更新成功", Toast.LENGTH_SHORT).show();
            finish();  // 更新成功后关闭当前页面
        } else {
            Toast.makeText(this, "商品更新失败", Toast.LENGTH_SHORT).show();
        }
    }

    // Helper method to convert Bitmap to byte array
    private byte[] getImageByteArray(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);  // Compress the bitmap into byte[]
        return byteArrayOutputStream.toByteArray();  // Return the byte array
    }

    // Helper method to get a default image's byte array
    private byte[] getDefaultImageByteArray() {
        Bitmap defaultBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.defaut_avatar);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        defaultBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }
}
