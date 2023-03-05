package com.example.pushnotification;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
public class StartActivity extends AppCompatActivity {

    Button btnConnect, btnExit;
    EditText txtAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        btnConnect = findViewById(R.id.connect);
        btnExit = findViewById(R.id.exit);
        txtAddress = findViewById(R.id.address);


        btnExit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        btnConnect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String address = txtAddress.getText().toString();
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.putExtra("address", address);
                startActivity(i);
            }
        });

    }
}