package org.michiganhackers.photoassassin.CustomViews;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatButton;

public class CustomButton extends AppCompatButton {
    public CustomButton(Context context) {
        super(context);
        TypefaceUtil.setFont(context, this);
    }

    public CustomButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypefaceUtil.setFont(context, this);
    }

    public CustomButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypefaceUtil.setFont(context, this);
    }
}
