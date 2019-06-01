package org.michiganhackers.photoassassin;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

public final class Util {
    private static final String TAG = "Util";

    // Private constructor to prevent instantiation
    private Util() {
    }

    // NOTE: Will ignore fields if they have null values
    public static Map<String, Object> pojoToMap(Object obj) {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(obj);
        Type mapStringObjType = new TypeToken<Map<String, Object>>(){}.getType();
        return new Gson().fromJson(json, mapStringObjType);
    }

    public static RuntimeException prependToException(String prefix, Exception e){
        String msg = e == null ? prefix : prefix + ": " + e.getLocalizedMessage();
        return new RuntimeException(msg);
    }
}
