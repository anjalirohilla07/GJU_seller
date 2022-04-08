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
import com.example.zapzoo_seller.models.SingleProduct;
import com.example.zapzoo_seller.util.SingleProductOnItemClickListener;

import java.util.List;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.MyViewHolder> {

    private List<SingleProduct> productList;
    private SingleProductOnItemClickListener listener;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView item_name, item_price, item_quantity, removeBtn, cartBtn;
        private ImageButton minusBtn, addBtn;
        private ImageView imv, deleteBtn;

        public MyViewHolder(@NonNull View itemView, final SingleProductOnItemClickListener listener) {
            super(itemView);
            item_name = (TextView) itemView.findViewById(R.id.name);
            item_price = (TextView) itemView.findViewById(R.id.price);
            item_quantity = (TextView) itemView.findViewById(R.id.quantity);
            cartBtn = (TextView) itemView.findViewById(R.id.cartBtn);
            imv = (ImageView) itemView.findViewById(R.id.imv);
            deleteBtn = (ImageView) itemView.findViewById(R.id.deleteBtn);

            itemView.setOnClickListener(this);

            cartBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onAddToCartClick(getAdapterPosition());
                }
            });

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onDeleteBtnClick(getAdapterPosition());
                }
            });
        }

        @Override
        public void onClick(View view) {
            listener.onItemClick(getAdapterPosition());
        }
    }

    public ProductListAdapter(List<SingleProduct> productList, SingleProductOnItemClickListener listener, Context context) {
        this.productList = productList;
        this.listener = listener;
        this.context = context;
    }

    @NonNull
    @Override
    public ProductListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.singleproduct_list_row, parent, false);

        return new MyViewHolder(itemView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductListAdapter.MyViewHolder holder, int position) {
        holder.item_name.setText(productList.get(position).getName());
        holder.item_price.setText("₹ "+productList.get(position).getPrice());
        holder.item_quantity.setText(""+productList.get(position).getQuantity());
        Glide.with(context).load(productList.get(position).getImage_url()).placeholder(R.drawable.cart1).into(holder.imv);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
}

