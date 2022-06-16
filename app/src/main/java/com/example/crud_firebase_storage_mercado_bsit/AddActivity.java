package com.example.crud_firebase_storage_mercado_bsit;

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

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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

public class AddActivity extends AppCompatActivity {
    EditText foodNameET, quantityET, priceET, descriptionET;
    String foodNameTxt, quantityTxt, priceTxt, descriptionTxt, UID, imgURL;
    ImageView foodaddIV;
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
        setContentView(R.layout.activity_add_food);
        reference = FirebaseDatabase.getInstance().getReference("Food");
        foodaddIV=(ImageView)findViewById(R.id.iv_food_add);
        foodNameET=(EditText)findViewById(R.id.et_food_name_add);
        quantityET=(EditText)findViewById(R.id.et_quantity_add);
        priceET=(EditText)findViewById(R.id.et_price_add);
        descriptionET=(EditText)findViewById(R.id.et_description_add);
        saveBtn=(Button)findViewById(R.id.btn_save_add);

        verifyPermissions();
        ActivityResultLauncher<String> getContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        foodaddIV.setImageURI(uri);
                        uri1 = uri;
                        isThere = true;
                    }
                });
        foodaddIV.setOnClickListener(new View.OnClickListener() {
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
                if(uri1 == null){
                    Toast.makeText(AddActivity.this, "Please put some Image!", Toast.LENGTH_SHORT).show();
                    return;
                }
                foodNameTxt = foodNameET.getText().toString();
                quantityTxt = quantityET.getText().toString();
                priceTxt = priceET.getText().toString();
                descriptionTxt = descriptionET.getText().toString();

                if(foodNameTxt.isEmpty() || quantityTxt.isEmpty() || priceTxt.isEmpty() || descriptionTxt.isEmpty()){
                    Toast.makeText(AddActivity.this, "Please fill all empty fields!", Toast.LENGTH_SHORT).show();
                }else{
                    if(isThere == true){
                        upload();
                        Toast.makeText(AddActivity.this, "Successfully Added", Toast.LENGTH_SHORT).show();
                    }else{
                        FoodModel model = new FoodModel(UID,foodNameTxt,quantityTxt,priceTxt,descriptionTxt,imgURL);
                        reference.child(auth.getUid()).child(UID).setValue(model);
                        Intent intent = new Intent(AddActivity.this,MainActivity.class);
                        Toast.makeText(AddActivity.this, "Successfully Added", Toast.LENGTH_SHORT).show();
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
                        reference.child(auth.getUid()).push().setValue(model);}
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