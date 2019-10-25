package org.michiganhackers.photoassassin;

import android.content.Context;
import android.text.TextUtils;

import org.michiganhackers.photoassassin.R;

public class Password {
    private String password;
    private Context context;

    public Password(String password, Context context) {
        this.password = password;
        this.context = context;
    }

    public String getPassword() {
        return password;
    }

    // Returns error message if password is invalid. Otherwise, returns null
    // Useful for TextInputLayout.setError(password.getError())
    public String getError() {
        if (password.length() < 8) {
            return context.getString(R.string.password_too_short_message);
        }
        return null;
    }
}
