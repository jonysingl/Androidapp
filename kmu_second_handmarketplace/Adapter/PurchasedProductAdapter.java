package com.example.kmu_second_handmarketplace.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.kmu_second_handmarketplace.Product.Product;
import com.example.kmu_second_handmarketplace.R;

import java.util.List;
import android.util.Pair; // 用于存储商品和订单 ID 的数据结构

public class PurchasedProductAdapter extends RecyclerView.Adapter<PurchasedProductAdapter.ViewHolder> {

    private List<Pair<Product, String>> purchasedProductsWithOrders; // 商品和订单 ID 的列表
    private Context context;

    // 构造函数
    public PurchasedProductAdapter(Context context, List<Pair<Product, String>> purchasedProductsWithOrders) {
        this.context = context;
        this.purchasedProductsWithOrders = purchasedProductsWithOrders;
    }

    // 创建 ViewHolder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_purchased_product, parent, false);
        return new ViewHolder(view);
    }

    // 绑定数据到 ViewHolder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Pair<Product, String> productWithOrder = purchasedProductsWithOrders.get(position);
        Product product = productWithOrder.first; // 商品对象
        String orderId = productWithOrder.second; // 对应的订单 ID

        // 设置商品名称
        holder.textViewProductName.setText(product.getName());

        // 设置商品价格
        holder.textViewProductPrice.setText("Price: $" + product.getPrice());

        // 设置商品描述
        holder.textViewProductDescription.setText(product.getDescription());

        // 设置订单 ID
        holder.textViewOrderId.setText("Order ID: " + orderId);

        // 设置商品图片
        byte[] imageBytes = product.getImageResId();
        if (imageBytes != null && imageBytes.length > 0) {
            // 将字节数组转换为 Bitmap
            Bitmap productImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            // 使用 Glide 加载图片
            Glide.with(context)
                    .load(productImage) // 加载 Bitmap
                    .placeholder(R.drawable.defaut_avatar) // 设置占位符
                    .error(R.drawable.touxiang) // 设置错误图片
                    .into(holder.imageViewProduct);
        } else {
            // 如果图片为空，则显示默认图片
            holder.imageViewProduct.setImageResource(R.drawable.defaut_avatar);
        }
    }

    // 获取列表中的项目数
    @Override
    public int getItemCount() {
        return purchasedProductsWithOrders.size();
    }

    // 定义 ViewHolder
    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageViewProduct;
        TextView textViewProductName;
        TextView textViewProductPrice;
        TextView textViewProductDescription;
        TextView textViewOrderId; // 新增订单 ID 的控件

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageViewProduct = itemView.findViewById(R.id.imageViewProduct);
            textViewProductName = itemView.findViewById(R.id.textViewProductName);
            textViewProductPrice = itemView.findViewById(R.id.textViewProductPrice);
            textViewProductDescription = itemView.findViewById(R.id.textViewProductDescription);
            textViewOrderId = itemView.findViewById(R.id.textViewOrderId); // 绑定订单 ID 控件
        }
    }
}
