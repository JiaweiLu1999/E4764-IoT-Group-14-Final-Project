package com.example.vendingmachine;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.text.SimpleDateFormat;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

public class SecondActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference database, user, products, doritos, cheetos1, cheetos2;
    private TextView DoritosQuantity, DoritosPrice;
    private TextView CheetosCrunchyQuantity, CheetosCrunchyPrice;
    private TextView CheetosPuffQuantity, CheetosPuffPrice;
    private TextView UserName, Balance;
    private ProgressDialog progressDialog;
    private Button buyDoritos, buyCheetos1, buyCheetos2, btnTopUp;
    private String url;
    private EditText topUpAmount;
    private Integer q1, q2, q3;
    private double balance, p1, p2, p3;
    private FirebaseUser database_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        DoritosQuantity = findViewById(R.id.tvDoritosQuantity);
        DoritosPrice = findViewById(R.id.tvDoritosPrice);
        CheetosCrunchyQuantity = findViewById(R.id.tvCheetosCrunchyQuantity);
        CheetosCrunchyPrice = findViewById(R.id.tvCheetosCrunchyPrice);
        CheetosPuffQuantity = findViewById(R.id.tvCheetosPuffQuantity);
        CheetosPuffPrice = findViewById(R.id.tvCheetosPuffPrice);
        UserName = findViewById(R.id.tvUser);
        Balance = findViewById(R.id.tvBalance);
        buyDoritos = findViewById(R.id.btnDoritosBuy);
        buyCheetos1 = findViewById(R.id.btnCheetosCrunchyBuy);
        buyCheetos2 = findViewById(R.id.btnCheetosPuffBuy);
        btnTopUp = findViewById(R.id.btnTopUp);
        topUpAmount = findViewById(R.id.etTopUp);
        url = "http://192.168.31.128:6060";

        database_user = FirebaseAuth.getInstance().getCurrentUser();
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();
        user = database.child("Users").child(database_user.getUid());

        products = database.child("Products");
        doritos = products.child("Doritos");
        cheetos1 = products.child("Cheetos: Crunchy");
        cheetos2 = products.child("Cheetos: Puff");

        progressDialog = new ProgressDialog(this);

        // Listen on name
        user.child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.getValue(String.class);
                UserName.setText("User: " + name);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });


        // Listen on balance
        user.child("balance").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                balance = dataSnapshot.getValue(double.class);
                String strBalance = String.format("%.2f", balance);
                Balance.setText("Balance: " + strBalance);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });


        // Listen on doritos
        doritos.child("Quantity").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                q1 = dataSnapshot.getValue(Integer.class);
                DoritosQuantity.setText("In stock: " + q1);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        doritos.child("Price").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                p1 = dataSnapshot.getValue(double.class);
                DoritosPrice.setText("Price: $" + p1);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        // Listen to cheetos
        cheetos1.child("Quantity").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                q2 = dataSnapshot.getValue(Integer.class);
                CheetosCrunchyQuantity.setText("In stock: " + q2);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        cheetos1.child("Price").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                p2 = dataSnapshot.getValue(double.class);
                CheetosCrunchyPrice.setText("Price: $" + p2);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        cheetos2.child("Quantity").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                q3 = dataSnapshot.getValue(Integer.class);
                CheetosPuffQuantity.setText("In stock: " + q3);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        cheetos2.child("Price").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                p3 = dataSnapshot.getValue(double.class);
                CheetosPuffPrice.setText("Price: $" + p3);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });


        btnTopUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!topUpAmount.getText().toString().equals("")) {
                    double money = Double.valueOf(topUpAmount.getText().toString());
                    user.child("balance").setValue(balance + money);
                } else {
                    Toast.makeText(SecondActivity.this, "Input an amount first!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buyDoritos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (q1 > 0) {
                    if (balance >= p1) {
                        String res = "run motor 0";
                        String path = url + "?msg=";
                        sendGet(path, res, doritos.child("Quantity"), q1, p1);
                    } else {
                        Toast.makeText(SecondActivity.this, "Not enough balance, top up first!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SecondActivity.this, "Out of Stock!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buyCheetos1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (q2 > 0) {
                    if (balance >= p2) {
                        String res = "run motor 1";
                        String path = url + "?msg=";
                        sendGet(path, res, cheetos1.child("Quantity"), q2, p2);
                    } else {
                        Toast.makeText(SecondActivity.this, "Not enough balance, top up first!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SecondActivity.this, "Out of Stock!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buyCheetos2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (q3 > 0) {
                    if (balance >= p3) {
                        String res = "run motor 2";
                        String path = url + "?msg=";
                        sendGet(path, res, cheetos2.child("Quantity"), q3, p3);
                    } else {
                        Toast.makeText(SecondActivity.this, "Not enough balance, top up first!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SecondActivity.this, "Out of Stock!", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }

    private void Logout() {
        firebaseAuth.signOut();
        startActivity(new Intent(SecondActivity.this, MainActivity.class));
    }

    private void UploadPhoto() {
        startActivity(new Intent(SecondActivity.this, UploadPhoto.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logoutMenu: Logout();
            case R.id.UploadMenu: UploadPhoto();
        }
        return super.onOptionsItemSelected(item);
    }

    // Send HTTP request
    private void sendGet(String path, String msg, DatabaseReference db, Integer q, double price) {
        progressDialog.setMessage("Coming out now! \nPlease wait...");
        progressDialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(path + msg, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                progressDialog.dismiss();
                db.setValue(q - 1);
                user.child("balance").setValue(balance - price);
                String resp = new String(bytes);
                Toast.makeText(getApplicationContext(), resp, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Status code: "+ i + "Error message" + throwable, Toast.LENGTH_LONG).show();
            }
        });
    }
}