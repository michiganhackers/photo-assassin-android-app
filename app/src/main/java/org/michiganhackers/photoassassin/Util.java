package org.michiganhackers.photoassassin;


import android.util.Log;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

public final class Util {
    private static final String TAG = "Util";

    // Private constructor to prevent instantiation
    private Util() {
    }asd
    f
            asdf
    asdf
                    asdf


    // NOTE: Will ignore fields if they have null values
    public static Map<String, Object> pojoToMap(Object obj) {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(obj);
        Type mapStringObjType = new TypeToken<Map<String, Object>>(){}.getType();
        return new Gson().fromJson(json, mapStringObjType);
    }

    // ensures space is reclaimed below TextInputLayout if there is no error
    public static void setTextInputLayoutErrorReclaim(TextInputLayout textInputLayout, String errorMsg){
        textInputLayout.setError(errorMsg);
        textInputLayout.setErrorEnabled(errorMsg != null);
    }
}
