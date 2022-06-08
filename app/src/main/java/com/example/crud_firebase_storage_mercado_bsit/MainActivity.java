package com.example.crud_firebase_storage_mercado_bsit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
    EmployeeAdapter adapter;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    TextView logoutTV;
    ArrayList<EmployeeModel> usersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView)findViewById(R.id.rv);
        logoutTV = (TextView) findViewById(R.id.tv_log_out);
        reference = FirebaseDatabase.getInstance().getReference("Employee");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        usersList = new ArrayList<>();
        recyclerView.setAdapter(adapter);

        reference.orderByKey().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                EmployeeModel model;
                String id = snapshot.getKey();
                String imgURL = snapshot.child("imgURL").getValue().toString();
                String fullname = snapshot.child("name").getValue().toString();
                String address = snapshot.child("address").getValue().toString();
                String email = snapshot.child("email").getValue().toString();
                String number = snapshot.child("number").getValue().toString();
                model = new EmployeeModel(id,fullname,email,address,number,imgURL);
                usersList.add(model);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
            @Override public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }
            @Override public void onChildRemoved(@NonNull DataSnapshot snapshot) { }
            @Override public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }
            @Override public void onCancelled(@NonNull DatabaseError error) { }
        });
        adapter = new EmployeeAdapter(usersList, new EmployeeAdapter.itemOnClick() {
            @Override
            public void itemDelete(int position, EmployeeModel model) {
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
            public void itemEdit(EmployeeModel model) {
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

    }
}