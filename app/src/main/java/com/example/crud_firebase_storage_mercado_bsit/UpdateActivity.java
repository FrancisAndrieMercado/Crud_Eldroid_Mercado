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
import android.util.Log;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
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
    EditText nameET, addressET, numberET, emailET;
    String fullnameTxt, addressTxt, numberTxt, emailTxt, UID, imgURL;
    ImageView profileIV;
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
        reference = FirebaseDatabase.getInstance().getReference("Employee");
        profileIV=(ImageView)findViewById(R.id.iv_profile_update);
        nameET=(EditText)findViewById(R.id.et_update_fullname);
        addressET=(EditText)findViewById(R.id.et_update_address);
        numberET=(EditText)findViewById(R.id.et_update_number);
        emailET=(EditText)findViewById(R.id.et_update_email);
        saveBtn=(Button)findViewById(R.id.btn_save);

        verifyPermissions();
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            UID = bundle.getString("UID");
            reference.orderByKey().equalTo(UID).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Picasso.get().load(snapshot.child("imgURL").getValue().toString()).into(profileIV);
                    imgURL = snapshot.child("imgURL").getValue().toString();
                    nameET.setText(snapshot.child("name").getValue().toString());
                    addressET.setText(snapshot.child("address").getValue().toString());
                    numberET.setText(snapshot.child("number").getValue().toString());
                    emailET.setText(snapshot.child("email").getValue().toString());
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
                        profileIV.setImageURI(uri);
                        uri1 = uri;
                        isThere = true;
                    }
                });
        profileIV.setOnClickListener(new View.OnClickListener() {
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
                fullnameTxt = nameET.getText().toString();
                addressTxt = addressET.getText().toString();
                numberTxt = numberET.getText().toString();
                emailTxt = emailET.getText().toString();

                if(fullnameTxt.isEmpty() || addressTxt.isEmpty() || numberTxt.isEmpty() || emailTxt.isEmpty()){
                    Toast.makeText(UpdateActivity.this, "Please fill all empty fields!", Toast.LENGTH_SHORT).show();
                }else{
                    if(isThere == true){
                        upload();
                        Toast.makeText(UpdateActivity.this, "Successfully Updated", Toast.LENGTH_SHORT).show();
                    }else{
                        EmployeeModel model = new EmployeeModel(UID,fullnameTxt,emailTxt,addressTxt,numberTxt,imgURL);
                        reference.child(UID).setValue(model);
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
        storageReference = FirebaseStorage.getInstance().getReference("EmployeePicture").child(System.currentTimeMillis() + "." +getFileExtension(uri1));
        storageReference.putFile(uri1).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        ImageModel imageModel = new ImageModel("EmployeeProfile", uri.toString());
                        EmployeeModel model= new EmployeeModel(auth.getUid(),nameET.getText().toString(),emailET.getText().toString(),addressET.getText().toString(),numberET.getText().toString(),imageModel.getImageUrl());
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