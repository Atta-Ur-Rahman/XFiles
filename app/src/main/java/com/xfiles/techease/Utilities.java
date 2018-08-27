package com.xfiles.techease;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
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
