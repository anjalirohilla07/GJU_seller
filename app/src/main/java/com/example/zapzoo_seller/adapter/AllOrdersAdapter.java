package com.example.zapzoo_seller.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.zapzoo_seller.R;
import com.example.zapzoo_seller.models.Order;
import com.example.zapzoo_seller.models.Product;
import com.example.zapzoo_seller.util.AllOrdersItemOnClickListener;

import java.util.Date;
import java.util.List;

public class AllOrdersAdapter extends RecyclerView.Adapter<AllOrdersAdapter.MyViewHolder> {

    private List<Order> orderList;
    private Context context;
    private AllOrdersItemOnClickListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView datetime, orderid, quantity, viewDetail, status, username;

        public MyViewHolder(@NonNull View itemView, AllOrdersItemOnClickListener listener) {
            super(itemView);
            datetime = itemView.findViewById(R.id.datetime);
            orderid = itemView.findViewById(R.id.orderid);
            quantity = itemView.findViewById(R.id.quantity);
            viewDetail = itemView.findViewById(R.id.viewDetail);
            status = itemView.findViewById(R.id.status);
            username = itemView.findViewById(R.id.username);

            viewDetail.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onItemClick(getAdapterPosition());
        }
    }

    public AllOrdersAdapter(Context context, List<Order> orderList, AllOrdersItemOnClickListener listener) {
        this.orderList = orderList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AllOrdersAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.allorders_row, parent, false);

        return new MyViewHolder(itemView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull AllOrdersAdapter.MyViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.datetime.setText("Date:"+order.getTimestamp().toString());
        holder.orderid.setText("Order ID:"+order.getTimestamp().getTime());
        holder.quantity.setText(""+order.getProductList().size()+" Items | "+calcTotal(order.getProductList())+"Rs");
        holder.status.setText("⚫ "+order.getStatus());
        holder.username.setText("Username:"+order.getUsername());

        if(order.getStatus().contains("Rejected"))
        {
            holder.status.setTextColor(Color.RED);
        } else if(order.getStatus().contains("Paid"))
        {
            holder.status.setTextColor(Color.GREEN);
        }
    }

    private int calcTotal(List<Product> productList) {
        int sum = 0;
        for(int i=0; i<productList.size(); i++)
        {
            sum += productList.get(i).getPrice();
        }
        return sum;
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }
}