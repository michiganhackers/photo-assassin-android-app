package org.michiganhackers.photoassassin.CustomViews;

import android.content.Context;
import android.util.AttributeSet;

import com.google.android.material.textfield.TextInputEditText;

public class CustomTextInputEditText extends TextInputEditText {
    public CustomTextInputEditText(Context context) {
        super(context);
        TypefaceUtil.setFont(context, this);
    }

    public CustomTextInputEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypefaceUtil.setFont(context, this);
    }

    public CustomTextInputEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypefaceUtil.setFont(context, this);
    }
}
