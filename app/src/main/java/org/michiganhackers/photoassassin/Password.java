package org.michiganhackers.photoassassin;

import android.content.Context;
import android.text.TextUtils;

public class Password {
    private String password;
    private Context context;

    public Password(String password, Context context) {
        // TODO: trim whitespace?
        this.password = password;
        this.context = context;
    }

    public String getPassword() {
        return password;
    }

    // Returns error message if password is invalid. Otherwise, returns null
    // Useful for TextInputLayout.setError(password.getError())
    public String getError() {
        if (TextUtils.isEmpty(password)) {
            return context.getString(R.string.empty_password_message);
        }
        if (password.length() < 8) {
            return context.getString(R.string.password_too_short_message);
        }
        return null;
    }
}
