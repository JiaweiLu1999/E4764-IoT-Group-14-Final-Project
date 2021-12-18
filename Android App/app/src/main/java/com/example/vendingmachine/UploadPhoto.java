package com.example.vendingmachine;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class UploadPhoto extends AppCompatActivity {
    private ImageView profilePic;
    private Button btn_upload, btnBack;
    private Uri imageUri;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseUser database_user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_photo);

        profilePic = findViewById(R.id.profilePic);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        database_user = FirebaseAuth.getInstance().getCurrentUser();
        btn_upload = findViewById(R.id.btn_uploadPic);
        btnBack = findViewById(R.id.btnBack);

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePicture();
            }
        });

        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPicture();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UploadPhoto.this, SecondActivity.class));
            }
        });
    }

    private void choosePicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        selectPhotoResultLauncher.launch(intent);
    }

    ActivityResultLauncher<Intent> selectPhotoResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            imageUri = data.getData();
                            profilePic.setImageURI(imageUri);
                        }
                    }
                }
            }
    );

    private void uploadPicture() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Uploading Image...");
        pd.show();

        final String userId = database_user.getUid();
        StorageReference riversRef = storageReference.child("faces/" + userId);
        UploadTask uploadTask = riversRef.putFile(imageUri);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                pd.dismiss();
                Toast.makeText(UploadPhoto.this, "Fail to Uploaded.", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                pd.dismiss();
                Snackbar.make(findViewById(android.R.id.content), "Image Uploaded!", Snackbar.LENGTH_LONG).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progressPercent = 100* snapshot.getBytesTransferred()/snapshot.getTotalByteCount();
                pd.setMessage("Progress: " + (int) progressPercent + "%");
            }
        });
    }

}