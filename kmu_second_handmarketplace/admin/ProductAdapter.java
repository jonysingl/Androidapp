package com.example.kmu_second_handmarketplace.admin;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kmu_second_handmarketplace.Product.Product;
import com.example.kmu_second_handmarketplace.R;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> productList;
    private OnDeleteClickListener deleteClickListener;

    // 删除回调接口
    public interface OnDeleteClickListener {
        void onDeleteClick(int productId);
    }

    // 构造函数
    public ProductAdapter(Context context, List<Product> productList, OnDeleteClickListener deleteClickListener) {
        this.context = context;
        this.productList = productList;
        this.deleteClickListener = deleteClickListener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.manage_item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        // 设置商品信息
        holder.tvProductId.setText("商品ID: " + product.getProductId());
        holder.tvProductName.setText("商品名称: " + product.getName());
        holder.tvProductPrice.setText("价格: ¥" + product.getPrice());
        holder.tvProductPublisher.setText("发布者: " + product.getSellerName());

        // 设置图片
        if (product.getImageResId() != null && product.getImageResId().length > 0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(product.getImageResId(), 0, product.getImageResId().length);
            holder.ivProductImage.setImageBitmap(bitmap);
        } else {
            holder.ivProductImage.setImageResource(R.drawable.ic_launcher_foreground);
        }

        // 删除按钮点击事件
        holder.buttonDelete.setOnClickListener(v -> {
            if (deleteClickListener != null) {
                deleteClickListener.onDeleteClick(product.getProductId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    // ViewHolder 类
    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        TextView tvProductId, tvProductName, tvProductPrice, tvProductPublisher;
        Button buttonDelete;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.item_product_image);
            tvProductId = itemView.findViewById(R.id.item_product_id);
            tvProductName = itemView.findViewById(R.id.item_product_name);
            tvProductPrice = itemView.findViewById(R.id.item_product_price);
            tvProductPublisher = itemView.findViewById(R.id.item_product_publisher);
            buttonDelete = itemView.findViewById(R.id.button_delete_product); // 删除按钮
        }
    }
}
