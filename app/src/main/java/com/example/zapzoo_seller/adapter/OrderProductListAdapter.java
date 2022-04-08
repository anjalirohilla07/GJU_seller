package com.example.zapzoo_seller.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.zapzoo_seller.R;
import com.example.zapzoo_seller.models.Product;

import java.util.List;

public class OrderProductListAdapter extends RecyclerView.Adapter<OrderProductListAdapter.MyViewHolder> {

    private List<Product> productList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView item_name, item_price, item_quantity, removeBtn;
        private ImageButton minusBtn, addBtn;
        private ImageView imv;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            item_name = (TextView) itemView.findViewById(R.id.item_name);
            item_price = (TextView) itemView.findViewById(R.id.item_price);
            item_quantity = (TextView) itemView.findViewById(R.id.item_quantity);
            removeBtn = (TextView) itemView.findViewById(R.id.removeBtn);
            minusBtn = (ImageButton) itemView.findViewById(R.id.minusBtn);
            addBtn = (ImageButton) itemView.findViewById(R.id.addBtn);
            imv = (ImageView) itemView.findViewById(R.id.imv);
        }
    }

    public OrderProductListAdapter(List<Product> productList, Context context) {
        this.productList = productList;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderProductListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cartitem_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderProductListAdapter.MyViewHolder holder, int position) {
        holder.item_name.setText(productList.get(position).getName());
        holder.item_price.setText("₹ "+productList.get(position).getPrice());
        holder.item_quantity.setText(""+productList.get(position).getQuantity());
        Glide.with(context).load(productList.get(position).getImage_url()).placeholder(R.drawable.cart1).into(holder.imv);
        holder.removeBtn.setVisibility(View.GONE);
        holder.addBtn.setVisibility(View.GONE);
        holder.minusBtn.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
}

