package com.example.kmu_second_handmarketplace.admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kmu_second_handmarketplace.Oder.Order;
import com.example.kmu_second_handmarketplace.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private Context context;
    private List<Order> orderList;
    private OnDeleteClickListener deleteClickListener;

    // 删除按钮回调接口
    public interface OnDeleteClickListener {
        void onDeleteClick(String orderId);
    }

    // 构造函数
    public OrderAdapter(Context context, List<Order> orderList, OnDeleteClickListener deleteClickListener) {
        this.context = context;
        this.orderList = orderList;
        this.deleteClickListener = deleteClickListener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.mannage_item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        // 获取当前订单对象
        Order order = orderList.get(position);

        // 绑定订单数据到视图
        holder.tvOrderId.setText("订单号: " + order.getOrderId());
        holder.tvProductName.setText("商品名: " + order.getProductName());
        holder.tvProductId.setText("商品ID: " + order.getProductId());
        holder.tvBuyerName.setText("买家: " + order.getBuyerName());

        // 转换并显示订单时间
        String formattedDate = convertTimestampToDate(order.getOrderDate());
        holder.tvOrderTime.setText("订单时间: " + formattedDate);

        // 删除按钮点击事件
        holder.btnDeleteOrder.setOnClickListener(v -> {
            if (deleteClickListener != null) {
                deleteClickListener.onDeleteClick(order.getOrderId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    // 更新数据的方法
    public void updateData(List<Order> newOrderList) {
        this.orderList = newOrderList;
        notifyDataSetChanged();
    }

    // 时间戳转换方法
    private String convertTimestampToDate(String timestamp) {
        try {
            long time = Long.parseLong(timestamp); // 将字符串转换为 long
            Date date = new Date(time); // 创建 Date 对象
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            return sdf.format(date); // 返回格式化后的日期字符串
        } catch (Exception e) {
            e.printStackTrace();
            return "时间格式错误"; // 转换失败时返回默认值
        }
    }

    // ViewHolder 内部类
    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvProductName, tvProductId, tvBuyerName, tvOrderTime;
        Button btnDeleteOrder;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);

            tvOrderId = itemView.findViewById(R.id.tv_order_id);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvProductId = itemView.findViewById(R.id.tv_product_id);
            tvBuyerName = itemView.findViewById(R.id.tv_buyer_name);
            tvOrderTime = itemView.findViewById(R.id.tv_order_time);
            btnDeleteOrder = itemView.findViewById(R.id.btn_delete_order);
        }
    }
}
