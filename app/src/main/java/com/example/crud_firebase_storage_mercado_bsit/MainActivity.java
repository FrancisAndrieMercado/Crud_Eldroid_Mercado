package com.example.crud_firebase_storage_mercado_bsit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    DatabaseReference reference;
    FoodAdapter adapter;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    TextView logoutTV;
    FirebaseUser user;
    ImageView addBTN;
    ArrayList<FoodModel> usersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView)findViewById(R.id.rv);
        logoutTV = (TextView) findViewById(R.id.tv_log_out);
        addBTN = (ImageView) findViewById(R.id.iv_add_info);
        reference = FirebaseDatabase.getInstance().getReference("Food");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        usersList = new ArrayList<FoodModel>();
        recyclerView.setAdapter(adapter);
        user = FirebaseAuth.getInstance().getCurrentUser();

        reference.child(user.getUid()).orderByKey().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                FoodModel model;
                String id = snapshot.getKey();
                String imgURL = snapshot.child("imgURL").getValue().toString();
                String foodName = snapshot.child("foodName").getValue().toString();
                String quantity = snapshot.child("quantity").getValue().toString();
                String price = snapshot.child("price").getValue().toString();
                String description = snapshot.child("description").getValue().toString();
                model = new FoodModel(id,foodName,quantity,price,description,imgURL);
                usersList.add(model);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
            @Override public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }
            @Override public void onChildRemoved(@NonNull DataSnapshot snapshot) { }
            @Override public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }
            @Override public void onCancelled(@NonNull DatabaseError error) { }
        });
        adapter = new FoodAdapter(usersList, new FoodAdapter.itemOnClick() {
            @Override
            public void itemDelete(int position, FoodModel model) {
                reference.orderByKey().equalTo(model.getUID()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                            dataSnapshot.getRef().removeValue();
                            adapter.notifyItemRemoved(position);
                            usersList.remove(position);
                            adapter.notifyDataSetChanged();
                        }
                    }
                    @Override public void onCancelled(@NonNull DatabaseError error) { }
                });
            }
            @Override
            public void itemEdit(FoodModel model) {
                Intent intent = new Intent(MainActivity.this,UpdateActivity.class);
                intent.putExtra("UID", model.getUID());
                startActivity(intent);
            }
        });
        logoutTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });
        addBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AddActivity.class));
            }
        });

    }
}