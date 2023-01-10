package com.example.safetyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MenuActivity extends AppCompatActivity {
    private LinearLayout Con;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
         Con = findViewById(R.id.con);
         Con.setOnClickListener(View->{
             Intent intent= new Intent(MenuActivity.this,Contacts.class);
             startActivity(intent);
         });
    }
}