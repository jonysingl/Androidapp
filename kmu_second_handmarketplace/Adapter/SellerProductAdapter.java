package com.example.kmu_second_handmarketplace.Adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.kmu_second_handmarketplace.Product.Product;
import com.example.kmu_second_handmarketplace.R;
import com.example.kmu_second_handmarketplace.editProduct.EditProductActivity;
// 引入编辑商品的Activity

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView 的适配器类，用于将商品数据加载到 RecyclerView 中。
 */
public class SellerProductAdapter extends RecyclerView.Adapter<SellerProductAdapter.ProductViewHolder> {

    private Context context; // 上下文对象
    private List<Product> productList; // 商品列表

    /**
     * 构造函数，用于初始化适配器。
     *
     * @param context 应用程序的上下文
     */
    public SellerProductAdapter(Context context) {
        this.context = context;
        this.productList = new ArrayList<>(); // 初始化商品列表
    }

    /**
     * 创建商品项视图的 ViewHolder。
     */
    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 加载单个商品项的布局
        View view = LayoutInflater.from(context).inflate(R.layout.item_product_edit, parent, false);
        return new ProductViewHolder(view);
    }

    /**
     * 将数据绑定到视图。
     *
     * @param holder   当前的 ViewHolder
     * @param position 当前商品在列表中的位置
     */
    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        // 获取当前位置的商品对象
        Product product = productList.get(position);

        // 设置视图上的数据
        holder.productIdTextView.setText("商品ID: " + product.getProductId());
        holder.productNameTextView.setText("商品名称: " + product.getName());
        holder.productPriceTextView.setText("价格: ¥" + product.getPrice());
        holder.productPublisherTextView.setText("发布者: " + product.getSellerName());
        holder.productDescriptionTextView.setText("商品描述: " + product.getDescription());

        // 获取商品的图片字节数组（如果有）
        byte[] imageResId = product.getImageResId();

        // 判断商品是否有图片
        if (imageResId != null && imageResId.length > 0) {
            // 将字节数组转换为 Bitmap 对象
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageResId, 0, imageResId.length);

            // 使用 Glide 加载图片到 ImageView 中
            Glide.with(context)
                    .load(bitmap)  // 加载 Bitmap 图片
                    .into(holder.productImageView);  // 设置到 ImageView 中
        } else {
            // 如果没有图片，加载默认图片
            Glide.with(context)
                    .load(R.drawable.defaut_avatar)  // 加载默认的头像图片
                    .into(holder.productImageView);  // 设置到 ImageView 中
        }

        // 设置编辑按钮的点击监听器
        holder.editButton.setOnClickListener(v -> {
            // 获取当前点击商品的 Product 对象
            Product productToEdit = productList.get(position);

            // 创建 Intent，用于启动 EditProductActivity
            Intent intent = new Intent(context, EditProductActivity.class);

            // 将商品数据通过 Intent 传递给 EditProductActivity
            intent.putExtra("productId", productToEdit.getProductId());  // 传递商品 ID
            intent.putExtra("product_name", productToEdit.getName());  // 传递商品名称
            intent.putExtra("product_price", productToEdit.getPrice());  // 传递商品价格
            intent.putExtra("product_description", productToEdit.getDescription());  // 传递商品描述
            intent.putExtra("product_image", productToEdit.getImageResId());  // 传递商品图片（字节数组）
            intent.putExtra("product_category", productToEdit.getCategory());  // 传递商品类别
            intent.putExtra("product_seller_name", productToEdit.getSellerName());  // 传递商品发布者

            // 启动 EditProductActivity
            context.startActivity(intent);
        });
    }

    /**
     * 返回商品的总数。
     */
    @Override
    public int getItemCount() {
        return productList.size();  // 返回商品列表的大小
    }

    /**
     * 根据数据库返回的 Cursor 更新商品列表。
     *
     * @param cursor 数据库查询返回的 Cursor 对象
     */
    public void updateProductList(Cursor cursor) {
        productList.clear();  // 清空当前商品列表
        if (cursor != null) {
            // 获取数据库列的索引
            int idIndex = cursor.getColumnIndex("product_id");
            int nameIndex = cursor.getColumnIndex("name");
            int priceIndex = cursor.getColumnIndex("price");
            int descriptionIndex = cursor.getColumnIndex("description");
            int imageResIdIndex = cursor.getColumnIndex("image_res_id");
            int categoryIndex = cursor.getColumnIndex("category");
            int sellerNameIndex = cursor.getColumnIndex("seller_name");

            // 检查必要的列是否都存在
            if (idIndex == -1 || nameIndex == -1 || priceIndex == -1 || descriptionIndex == -1 || imageResIdIndex == -1 || sellerNameIndex == -1) {
                return;  // 如果缺少必要的列，直接返回
            }

            // 遍历 Cursor，提取数据
            while (cursor.moveToNext()) {
                int productId = cursor.getInt(idIndex);  // 获取商品 ID
                String name = cursor.getString(nameIndex);  // 获取商品名称
                String price = cursor.getString(priceIndex);  // 获取商品价格
                String description = cursor.getString(descriptionIndex);  // 获取商品描述
                byte[] imageResId = cursor.getBlob(imageResIdIndex);  // 获取商品图片（字节数组）
                String category = cursor.getString(categoryIndex);  // 获取商品类别
                String sellerName = cursor.getString(sellerNameIndex);  // 获取商品发布者名称

                // 创建 Product 对象，并将商品数据加入商品列表
                Product product = new Product(productId, name, price, description, imageResId, category, sellerName);
                productList.add(product);
            }
            cursor.close();  // 关闭 Cursor 对象
        }
        notifyDataSetChanged();  // 通知适配器更新数据
    }

    /**
     * ViewHolder 类，用于缓存视图，避免频繁调用 findViewById。
     */
    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView productIdTextView;  // 商品 ID 文本
        TextView productNameTextView;  // 商品名称文本
        TextView productPriceTextView;  // 商品价格文本
        TextView productPublisherTextView;  // 商品发布者文本
        TextView productDescriptionTextView;  // 商品描述文本
        ImageView productImageView;  // 商品图片 ImageView
        Button editButton;  // 编辑商品按钮

        /**
         * ViewHolder 构造函数，用于初始化视图。
         *
         * @param itemView 商品项的根视图
         */
        public ProductViewHolder(View itemView) {
            super(itemView);
            // 绑定视图控件
            productIdTextView = itemView.findViewById(R.id.item_product_id);
            productNameTextView = itemView.findViewById(R.id.item_product_name);
            productPriceTextView = itemView.findViewById(R.id.item_product_price);
            productPublisherTextView = itemView.findViewById(R.id.item_product_publisher);
            productDescriptionTextView = itemView.findViewById(R.id.item_product_description);
            productImageView = itemView.findViewById(R.id.item_product_image);
            editButton = itemView.findViewById(R.id.button_edit_product);
        }
    }
}
