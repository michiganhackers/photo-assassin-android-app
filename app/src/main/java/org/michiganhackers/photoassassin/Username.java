package org.michiganhackers.photoassassin;

import android.content.Context;

import java.util.regex.Pattern;

public class Username {
    public static final int MIN_LENGTH = 5;
    public static final int MAX_LENGTH = 20;
    private String username;
    private Context context;
    private static final Pattern pattern = Pattern.compile("[^0-9a-zA-Z]");

    public Username(String username, Context context) {
        this.username = username;
        this.context = context;
    }

    public String getUsername() {
        return username;
    }

    // Returns error message if username is invalid. Otherwise, returns null
    // Useful for TextInputLayout.setError(username.getError())
    public String getError() {
        if (username.isEmpty()) {
            return context.getString(R.string.username_too_short_msg);
        } else if (pattern.matcher(username).find()) {
            return context.getString(R.string.invalid_characters_in_username_msg);
        } else if (username.length() < MIN_LENGTH) {
            return context.getString(R.string.username_too_short_msg);
        } else if (username.length() > MAX_LENGTH) {
            return context.getString(R.string.username_too_long_msg);
        }
        return null;
    }
}
