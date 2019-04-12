package org.michiganhackers.photoassassin.CustomViews;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

final class TypefaceUtil {
    private static final String FONT_PATH = "fonts/Economica-Regular-OTF.otf";

    private TypefaceUtil() {
    }

    static void setFont(Context context, TextView textView) {
        Typeface tf = Typeface.createFromAsset(context.getAssets(), FONT_PATH);
        textView.setTypeface(tf);
    }
}
