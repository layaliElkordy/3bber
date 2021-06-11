package com.example.a3bbernav;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class AppCompat extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LanguageManger lang = new LanguageManger(this);
        lang.updateResource(lang.getLang());
    }

}
