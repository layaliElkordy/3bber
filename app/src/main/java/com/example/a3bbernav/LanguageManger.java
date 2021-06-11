package com.example.a3bbernav;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;

import java.util.Locale;

public class LanguageManger{

    private Context context;
    private SharedPreferences sp;

    public LanguageManger(Context ctx){
        context=ctx;
        sp = context.getSharedPreferences("LANG",Context.MODE_PRIVATE);
    }

    /*@Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base, "en"));
    }*/

    public void updateResource(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());

        setLang(languageCode);
    }

    public String getLang(){
        return sp.getString("lang","ar");
    }

    public void setLang(String code){
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("lang", code);
        editor.commit();
    }
}