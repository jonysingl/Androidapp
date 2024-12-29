package com.example.kmu_second_handmarketplace.Adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.kmu_second_handmarketplace.DetailandBuy.ProductDetailActivity;
import com.example.kmu_second_handmarketplace.Product.Product;
import com.example.kmu_second_handmarketplace.R;
import com.example.kmu_second_handmarketplace.database.ProductManager;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;  // 上下文对象，供适配器使用
    private List<Product> productList;  // 存储商品的列表，adapter会用它来展示数据
    private ProductManager productManager;  // 用于与数据库交互的 ProductManager

    // 构造函数，初始化上下文和商品列表
    public ProductAdapter(Context context) {
        this.context = context;  // 获取上下文，通常是Activity或Fragment
        this.productManager = new ProductManager(context);  // 创建ProductManager实例，用于数据库操作
        this.productList = new ArrayList<>();  // 初始化商品列表为空列表
    }

    // 创建视图持有者（ViewHolder），用于缓存视图
    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 使用布局填充器来加载item_product布局
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);  // 返回新的 ProductViewHolder，它将持有每个item的视图
    }

    // 绑定数据到视图中的各个组件
    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        // 获取当前商品对象
        Product product = productList.get(position);

        // 设置商品的各项信息到对应的视图组件中
        holder.productNameTextView.setText(product.getName());  // 设置商品名称
        holder.productPriceTextView.setText(product.getPrice());  // 设置商品价格
        holder.productDescriptionTextView.setText(product.getDescription());  // 设置商品描述
        holder.productCategoryTextView.setText(product.getCategory());  // 设置商品类别
        holder.sellerNameTextView.setText(product.getSellerName());  // 设置卖家名称

        // 获取商品图片字节数组
        byte[] imageBytes = product.getImageResId();  // 获取商品图片的字节数组

        // 如果图片字节数组不为空，使用 Glide 加载图片
        if (imageBytes != null && imageBytes.length > 0) {
            // 将字节数组转换为 Bitmap
            Bitmap productImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            Glide.with(context)
                    .load(productImage)  // 使用转换后的 Bitmap 加载图片
                    .placeholder(R.drawable.defaut_avatar)  // 设置占位图
                    .error(R.drawable.touxiang)  // 设置加载错误时的图片
                    .into(holder.productImageView);  // 将图片设置到 ImageView
        } else {
            // 如果没有图片数据，显示默认图片
            holder.productImageView.setImageResource(R.drawable.defaut_avatar);  // 设置默认的占位图
        }

        // 设置点击事件，当用户点击某个商品时，跳转到商品详情页面
        holder.itemView.setOnClickListener(v -> {
            // 创建 Intent，跳转到商品详情页
            Intent intent = new Intent(context, ProductDetailActivity.class);
            // 传递商品的各种信息到商品详情页
            intent.putExtra("productName", product.getName());  // 传递商品名称
            intent.putExtra("productDescription", product.getDescription());  // 传递商品描述
            intent.putExtra("productPrice", product.getPrice());  // 传递商品价格
            intent.putExtra("productImage", imageBytes); // 传递字节数组作为商品图片
            intent.putExtra("productCategory", product.getCategory());  // 传递商品类别
            intent.putExtra("productSeller", product.getSellerName());  // 传递卖家名称
            intent.putExtra("productId", product.getProductId());  // 传递商品 ID

            // 打印日志，查看商品 ID，便于调试
            Log.d("ProductAdapter", "Product ID: " + product.getProductId());

            // 启动商品详情页面
            context.startActivity(intent);
        });
    }

    // 返回商品列表的大小
    @Override
    public int getItemCount() {
        return productList.size();  // 返回商品列表的长度，表示有多少项数据
    }

    // 更新商品列表的方法，通过数据库游标更新商品列表
    public void updateProductList(Cursor cursor) {
        productList.clear();  // 清空当前商品列表

        if (cursor != null && cursor.moveToFirst()) {  // 如果游标不为空并且有数据
            // 获取各列的索引，依据数据库表的字段名
            int idIndex = cursor.getColumnIndex("product_id");  // 商品 ID
            int nameIndex = cursor.getColumnIndex("name");  // 商品名称
            int priceIndex = cursor.getColumnIndex("price");  // 商品价格
            int descriptionIndex = cursor.getColumnIndex("description");  // 商品描述
            int imageResIdIndex = cursor.getColumnIndex("image_res_id");  // 商品图片
            int categoryIndex = cursor.getColumnIndex("category");  // 商品类别
            int sellerNameIndex = cursor.getColumnIndex("seller_name");  // 卖家名称

            // 检查是否缺少任何必要的列
            if (idIndex == -1 || nameIndex == -1 || priceIndex == -1 || descriptionIndex == -1 ||
                    imageResIdIndex == -1 || categoryIndex == -1 || sellerNameIndex == -1) {
                Log.e("ProductAdapter", "缺少一个或多个必要的列。");
                return;  // 如果缺少必要列，直接返回
            }

            // 遍历数据库查询结果，将每个商品对象添加到商品列表
            do {
                try {
                    // 从游标中读取各列的值
                    int productId = cursor.getInt(idIndex);  // 获取商品 ID
                    String name = cursor.getString(nameIndex);  // 获取商品名称
                    String price = cursor.getString(priceIndex);  // 获取商品价格
                    String description = cursor.getString(descriptionIndex);  // 获取商品描述
                    byte[] imageResId = cursor.getBlob(imageResIdIndex);  // 获取商品图片字节数组
                    String category = cursor.getString(categoryIndex);  // 获取商品类别
                    String sellerName = cursor.getString(sellerNameIndex);  // 获取卖家名称

                    // 打印日志，查看是否正确读取商品数据
                    Log.d("ProductAdapter", "读取商品 - ID: " + productId + ", 名称: " + name + ", 价格: " + price);

                    // 使用从数据库获取的数据创建商品对象
                    Product product = new Product(productId, name, price, description, imageResId, category, sellerName);
                    productList.add(product);  // 将商品对象添加到列表
                } catch (Exception e) {
                    Log.e("ProductAdapter", "Error processing product data: " + e.getMessage());
                }
            } while (cursor.moveToNext());  // 遍历游标中的所有记录
            cursor.close();  // 关闭游标
        } else {
            Log.d("ProductAdapter", "No products found.");
        }

        notifyDataSetChanged();  // 通知适配器更新视图
    }

    // 搜索商品的方法，根据商品名称进行模糊查询
    public void searchProducts(String searchTerm) {
        Cursor cursor = productManager.searchProductsByName(searchTerm);  // 根据名称搜索商品
        updateProductList(cursor);  // 更新商品列表
    }

    // 根据商品类别过滤商品
    public void filterProductsByCategory(String category) {
        Cursor cursor = productManager.getProductsByCategory(category);  // 根据类别获取商品
        updateProductList(cursor);  // 更新商品列表
    }

    // ViewHolder 类，用于缓存商品的视图组件
    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView productNameTextView;  // 商品名称文本
        TextView productPriceTextView;  // 商品价格文本
        TextView productDescriptionTextView;  // 商品描述文本
        ImageView productImageView;  // 商品图片
        TextView productCategoryTextView;  // 商品类别文本
        TextView sellerNameTextView;  // 卖家名称文本

        // 构造函数，用于绑定视图组件
        public ProductViewHolder(View itemView) {
            super(itemView);
            // 将布局中的各个视图组件与代码中的变量绑定
            productNameTextView = itemView.findViewById(R.id.textViewProductName);  // 商品名称
            productPriceTextView = itemView.findViewById(R.id.productPrice);  // 商品价格
            productDescriptionTextView = itemView.findViewById(R.id.productDescription);  // 商品描述
            productImageView = itemView.findViewById(R.id.imageViewProduct);  // 商品图片
            productCategoryTextView = itemView.findViewById(R.id.productCategory);  // 商品类别
            sellerNameTextView = itemView.findViewById(R.id.productSeller);  // 卖家名称
        }
    }
}
