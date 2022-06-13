package com.example.crud_firebase_storage_mercado_bsit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.MyViewHolder> {
    private ArrayList<FoodModel>userList;
    itemOnClick listener;

    public FoodAdapter(ArrayList<FoodModel> userList, itemOnClick listener){
        this.userList = userList;
        this.listener = listener;
    }
    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView foodname,quantity,price,description;
        Button edit, delete;
        ImageView imageView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.iv_food);
            foodname = (TextView) itemView.findViewById(R.id.tv_food_name);
            quantity = (TextView) itemView.findViewById(R.id.tv_quantity);
            price = (TextView) itemView.findViewById(R.id.tv_price);
            description = (TextView) itemView.findViewById(R.id.tv_description);
            edit = (Button) itemView.findViewById(R.id.btn_edit);
            delete = (Button) itemView.findViewById(R.id.btn_delete);
        }
    }
    @NonNull
    @Override public FoodAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_item, parent, false);
        return new MyViewHolder(itemView);
    }
    public interface itemOnClick{
        void itemDelete(int pos, FoodModel model);
        void itemEdit(FoodModel model);
    }
    @Override public void onBindViewHolder(@NonNull FoodAdapter.MyViewHolder holder, int position) {
        Picasso.get().load(userList.get(position).getImgURL()).into(holder.imageView);
        holder.foodname.setText(userList.get(position).getFoodName());
        holder.quantity.setText(userList.get(position).getQuantity());
        holder.price.setText(userList.get(position).getPrice());
        holder.description.setText(userList.get(position).getDescription());
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.itemEdit(userList.get(position));
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.itemDelete(holder.getAdapterPosition(),userList.get(position));
            }
        });
    }
    @Override
    public int getItemCount() {
        return userList.size();
    }
}
