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
import java.util.List;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.MyViewHolder> {
    private ArrayList<EmployeeModel>userList;
    itemOnClick listener;

    public EmployeeAdapter(ArrayList<EmployeeModel> userList, itemOnClick listener){
        this.userList = userList;
        this.listener = listener;
    }
    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView name,address,number,email;
        Button edit, delete;
        ImageView imageView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.iv_profile);
            name = (TextView) itemView.findViewById(R.id.tv_fullname);
            address = (TextView) itemView.findViewById(R.id.tv_address);
            number = (TextView) itemView.findViewById(R.id.tv_number);
            email = (TextView) itemView.findViewById(R.id.tv_email);
            edit = (Button) itemView.findViewById(R.id.btn_edit);
            delete = (Button) itemView.findViewById(R.id.btn_delete);
        }
    }
    @NonNull
    @Override public EmployeeAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_item, parent, false);
        return new MyViewHolder(itemView);
    }
    public interface itemOnClick{
        void itemDelete(int pos, EmployeeModel model);
        void itemEdit(EmployeeModel model);
    }
    @Override public void onBindViewHolder(@NonNull EmployeeAdapter.MyViewHolder holder, int position) {
        Picasso.get().load(userList.get(position).getImgURL()).into(holder.imageView);
        holder.name.setText(userList.get(position).getName());
        holder.address.setText(userList.get(position).getAddress());
        holder.email.setText(userList.get(position).getEmail());
        holder.number.setText(userList.get(position).getNumber());
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
