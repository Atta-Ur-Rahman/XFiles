package com.xfiles.techease;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.attaurrahman.recordapplication.R;

import java.util.regex.Pattern;
import me.itangqi.waveloadingview.WaveLoadingView;

public class Utilities {
    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;



    public static SharedPreferences.Editor putValueInEditor(Context context){
        sharedPreferences = getSharedPreferences(context);
        editor = sharedPreferences.edit();
        return editor;
    }
    public static SharedPreferences getSharedPreferences(Context context){
        return context.getSharedPreferences(Configurations.MY_PREF, 0);
    }
    public static Fragment connectFragment(Context context, Fragment fragment) {
        ((AppCompatActivity) context).getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack("true").commit();
        return fragment;
    }
    public static Fragment connectFragmentWithOutBackStack(Context contextwithout, Fragment fragmentWithOut) {
        ((AppCompatActivity) contextwithout).getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragmentWithOut).commit();
        return fragmentWithOut;
    }

    private static class Configurations {
        public static final String MY_PREF = null;
    }

}
