package org.michiganhackers.photoassassin;

import com.google.android.material.textfield.TextInputLayout;

import androidx.databinding.BindingAdapter;

public class BindingAdapters {
    @BindingAdapter("errorText")
    public static void setError(TextInputLayout view, Object stringOrSringResourceId) {
        if (stringOrSringResourceId instanceof Integer) {
            view.setError(view.getContext().getString((Integer) stringOrSringResourceId));
        } else {
            view.setError((String) stringOrSringResourceId);
        }
    }
}
