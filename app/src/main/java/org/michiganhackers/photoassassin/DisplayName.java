package org.michiganhackers.photoassassin;

import android.content.Context;

import java.util.regex.Pattern;

public class DisplayName {
    public static final int MIN_LENGTH = 5;
    public static final int MAX_LENGTH = 20;
    private String displayName;
    private Context context;
    private static final Pattern pattern = Pattern.compile("(?i)^(?![- '])(?![×Þß÷þø])[- '0-9a-zÀ-ÿ]+(?<![- '])$");


    public DisplayName(String displayName, Context context) {
        this.displayName = displayName;
        this.context = context;
    }

    public String getDisplayName() {
        return displayName;
    }

    // Returns error message if displayName is invalid. Otherwise, returns null
    // Useful for TextInputLayout.setError(displayName.getError())
    public String getError() {
        // Reference https://regex101.com/r/gY7rO4/348 for the regex
        if (displayName.isEmpty()) {
            return context.getString(R.string.display_name_too_short_msg);
        } else if (!pattern.matcher(displayName).matches()) {
            return context.getString(R.string.invalid_characters_in_display_name_msg);
        } else if (displayName.length() < MIN_LENGTH) {
            return context.getString(R.string.display_name_too_short_msg);
        } else if (displayName.length() > MAX_LENGTH) {
            return context.getString(R.string.display_name_too_long_msg);
        }
        return null;
    }
}
