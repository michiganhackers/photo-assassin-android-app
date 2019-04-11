package org.michiganhackers.photoassassin.LoginPages;

import android.content.Context;
import android.text.TextUtils;

import org.michiganhackers.photoassassin.R;

public class Email {
    private String email;
    private Context context;

    public Email(String email, Context context) {
        // TODO: trim whitespace?
        this.email = email;
        this.context = context;
    }

    public String getEmail() {
        return email;
    }

    // Returns error message if email is invalid. Otherwise, returns null
    // Useful for TextInputLayout.setError(email.getError())
    public String getError() {
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return context.getString(R.string.invalid_email_message);
        }
        return null;
    }

}
