package org.michiganhackers.photoassassin.CustomViews;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

public class CustomTextView extends AppCompatTextView {
    public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypefaceUtil.setFont(context, this);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypefaceUtil.setFont(context, this);
    }

    public CustomTextView(Context context) {
        super(context);
        TypefaceUtil.setFont(context, this);
    }
}