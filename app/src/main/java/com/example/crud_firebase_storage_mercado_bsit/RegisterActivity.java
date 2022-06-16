package com.example.crud_firebase_storage_mercado_bsit;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseAuth auth;
    private FirebaseDatabase rootNode;
    private DatabaseReference reference;
    StorageReference storageReference;
    Boolean passwordVisible = false;
    Uri uri1;
    final int PICK_IMAGE = 100;
    String fullnameTxt,addressTxt,emailTxt,numberTxt,passwordTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        auth = FirebaseAuth.getInstance();
        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference();
        EditText fullnameET = (EditText)findViewById(R.id.et_fullname);
        EditText addressET = (EditText)findViewById(R.id.et_address);
        EditText numberET = (EditText)findViewById(R.id.et_number);
        ImageView imageProfile = (ImageView)findViewById(R.id.iv_profile_register);
        EditText emailET = (EditText)findViewById(R.id.et_email_register);
        EditText passwordET = (EditText)findViewById(R.id.et_password_register);
        Button registerBtn = (Button) findViewById(R.id.btn_sign_in);
        TextView loginBtn = (TextView) findViewById(R.id.tv_login_now);
        verifyPermissions();

        passwordET.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                final int Right = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= passwordET.getRight() - passwordET.getCompoundDrawables()[Right].getBounds().width()) {
                        int selection = passwordET.getSelectionEnd();
                        if (passwordVisible) {
                            passwordET.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_baseline_lock_24, 0, R.drawable.ic_baseline_visibility_off_24, 0);
                            passwordET.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordVisible = false;
                        } else {
                            passwordET.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_baseline_lock_24, 0, R.drawable.ic_baseline_visibility_24, 0);
                            passwordET.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passwordVisible = true;
                        }
                        passwordET.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });
        ActivityResultLauncher<String> getContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        imageProfile.setImageURI(uri);
                        uri1 = uri;
                    }
                });

        imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                getContent.launch("image/*");
            }
        });
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fullnameTxt = fullnameET.getText().toString();
                addressTxt = addressET.getText().toString();
                numberTxt = numberET.getText().toString();
                emailTxt = emailET.getText().toString();
                passwordTxt = passwordET.getText().toString();

                if(fullnameTxt.isEmpty() || addressTxt.isEmpty() || numberTxt.isEmpty() || emailTxt.isEmpty() || passwordTxt.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Please fill all empty fields!", Toast.LENGTH_SHORT).show();
                }else{
                    createUser(emailTxt,passwordTxt);
                    Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                }
            }
        });
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    public void createUser(String email, String password) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                   if (task.isSuccessful()) {
                      upload();
                      Toast.makeText(RegisterActivity.this, "Account creation successful.", Toast.LENGTH_SHORT).show();
                      Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                   } else {
                      try {
                         throw task.getException();
                      } catch (FirebaseAuthWeakPasswordException e) {
                                Toast.makeText(RegisterActivity.this, "Password must be at least 6 characters!",
                                        Toast.LENGTH_SHORT).show();
                     } catch (FirebaseAuthUserCollisionException e) {
                                Toast.makeText(RegisterActivity.this, "Email entered is already in use!", Toast.LENGTH_SHORT).show();
                     } catch (Exception e) {
                                Toast.makeText(RegisterActivity.this, "Enter must be Email!", Toast.LENGTH_SHORT).show();
                                Log.e("", e.getMessage());
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
                        EmployeeModel model= new EmployeeModel(auth.getUid(),fullnameTxt,emailTxt,addressTxt,numberTxt,imageModel.getImageUrl());
                        reference.child("Employee").child(auth.getUid()).setValue(model);}
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