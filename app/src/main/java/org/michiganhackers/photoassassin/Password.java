package org.michiganhackers.photoassassin;

import android.text.TextUtils;

public class Password {
    private String password;

    // TODO: Get these strings from string resource file
    private static final String EMPTY_PASSWORD_MESSAGE = "Password field cannot be empty";
    private static final String PASSWORD_TOO_SHORT_MESSAGE = "Password must be at least 8 characters";


    public Password(String password) {
        // TODO: trim whitespace?
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    // Returns error message if password is invalid. Otherwise, returns null
    // Useful for TextInputLayout.setError(password.getError())
    public String getError() {
        if (TextUtils.isEmpty(password)) {
            return EMPTY_PASSWORD_MESSAGE;
        }
        if (password.length() < 8) {
            return PASSWORD_TOO_SHORT_MESSAGE;
        }
        return null;
    }
}
