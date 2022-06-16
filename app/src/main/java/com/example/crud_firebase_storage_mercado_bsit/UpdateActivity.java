package com.example.crud_firebase_storage_mercado_bsit;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class UpdateActivity extends AppCompatActivity {
    EditText foodNameET, quantityET, priceET, descriptionET;
    String foodNameTxt, quantityTxt, priceTxt, descriptionTxt, UID, imgURL;
    ImageView foodUpdateIV;
    Button saveBtn;
    StorageReference storageReference;
    Uri uri1;
    final int PICK_IMAGE = 100;
    FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference reference;
    Boolean isThere = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        reference = FirebaseDatabase.getInstance().getReference("Food");
        foodUpdateIV=(ImageView)findViewById(R.id.iv_food_update);
        foodNameET=(EditText)findViewById(R.id.et_food_name);
        quantityET=(EditText)findViewById(R.id.et_quantity);
        priceET=(EditText)findViewById(R.id.et_price);
        descriptionET=(EditText)findViewById(R.id.et_description);
        saveBtn=(Button)findViewById(R.id.btn_save);

        verifyPermissions();
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            UID = bundle.getString("UID");
            reference.child(auth.getUid()).orderByKey().equalTo(UID).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Picasso.get().load(snapshot.child("imgURL").getValue().toString()).into(foodUpdateIV);
                    imgURL = snapshot.child("imgURL").getValue().toString();
                    foodNameET.setText(snapshot.child("foodName").getValue().toString());
                    quantityET.setText(snapshot.child("quantity").getValue().toString());
                    priceET.setText(snapshot.child("price").getValue().toString());
                    descriptionET.setText(snapshot.child("description").getValue().toString());
                }
                @Override public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }
                @Override public void onChildRemoved(@NonNull DataSnapshot snapshot) { }
                @Override public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }
                @Override public void onCancelled(@NonNull DatabaseError error) { }
            });
        }
        ActivityResultLauncher<String> getContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        foodUpdateIV.setImageURI(uri);
                        uri1 = uri;
                        isThere = true;
                    }
                });
        foodUpdateIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                getContent.launch("image/*");
            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                foodNameTxt = foodNameET.getText().toString();
                quantityTxt = quantityET.getText().toString();
                priceTxt = priceET.getText().toString();
                descriptionTxt = descriptionET.getText().toString();

                if(foodNameTxt.isEmpty() || quantityTxt.isEmpty() || priceTxt.isEmpty() || descriptionTxt.isEmpty()){
                    Toast.makeText(UpdateActivity.this, "Please fill all empty fields!", Toast.LENGTH_SHORT).show();
                }else{
                    if(isThere == true){
                        upload();
                        Toast.makeText(UpdateActivity.this, "Successfully Updated", Toast.LENGTH_SHORT).show();
                    }else{
                        FoodModel model = new FoodModel(UID,foodNameTxt,quantityTxt,priceTxt,descriptionTxt,imgURL);
                        reference.child(auth.getUid()).child(UID).setValue(model);
                        Intent intent = new Intent(UpdateActivity.this,MainActivity.class);
                        Toast.makeText(UpdateActivity.this, "Successfully Updated", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    public void upload() {
        storageReference = FirebaseStorage.getInstance().getReference("FoodPicture").child(System.currentTimeMillis() + "." +getFileExtension(uri1));
        storageReference.putFile(uri1).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        ImageModel imageModel = new ImageModel("FoodPicture", uri.toString());
                        FoodModel model= new FoodModel(auth.getUid(),foodNameET.getText().toString(),quantityET.getText().toString(),priceET.getText().toString(),descriptionET.getText().toString(),imageModel.getImageUrl());
                        reference.child(auth.getUid()).setValue(model);}
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override public void onFailure(@NonNull Exception e) { }
        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) { }
        });
    }
    private void verifyPermissions() {
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permissions[0]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permissions[1]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permissions[2]) == PackageManager.PERMISSION_GRANTED) {
        } else {
            ActivityCompat.requestPermissions(this,
                    permissions,
                    PICK_IMAGE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}