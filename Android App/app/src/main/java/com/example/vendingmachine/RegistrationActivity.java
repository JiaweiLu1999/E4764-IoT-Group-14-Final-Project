package com.example.vendingmachine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends AppCompatActivity {

    private EditText userName, userPassword, userEmail;
    private Button regButton;
    private TextView userLogin;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference database, users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        setupUIViews();

        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();
        users = database.child("Users");

        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()) {
                    // Upload user information to database.
                    String user_email = userEmail.getText().toString().trim();
                    String user_name = userName.getText().toString();
                    String user_password = userPassword.getText().toString().trim();

                    firebaseAuth.createUserWithEmailAndPassword(user_email, user_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                users.child(user.getUid()).child("balance").setValue(0);
                                users.child(user.getUid()).child("name").setValue(user_name);
                                users.child(user.getUid()).child("email").setValue(user_email);
                                Toast.makeText(RegistrationActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                startActivity((new Intent(RegistrationActivity.this, MainActivity.class)));
                            } else {
                                Toast.makeText(RegistrationActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        userLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
            }
        });
    }



    private void setupUIViews() {
        userName = (EditText) findViewById(R.id.etUserName);
        userPassword = (EditText) findViewById(R.id.etUserPassword);
        userEmail = (EditText) findViewById(R.id.etUserEmail);
        regButton = (Button) findViewById(R.id.btnUserRegister);
        userLogin = (TextView) findViewById(R.id.tvUserLogin);
    }

    private Boolean validate() {
        boolean result = false;

        String name = userName.getText().toString();
        String password = userPassword.getText().toString();
        String email = userEmail.getText().toString();
        if (name.isEmpty() && password.isEmpty() && email.isEmpty()) {
            Toast.makeText(this, "Please enter all the details", Toast.LENGTH_SHORT).show();
        } else {
            result = true;
        }

        return result;
    }
}