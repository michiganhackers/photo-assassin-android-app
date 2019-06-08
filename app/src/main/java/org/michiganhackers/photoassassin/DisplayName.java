package org.michiganhackers.photoassassin;

import android.content.Context;

public class DisplayName {
    private String displayName;
    private Context context;

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
        } else if (!displayName.matches("(?i)^(?![- '])(?![×Þß÷þø])[- '0-9a-zÀ-ÿ]+(?<![- '])$")) {
            return context.getString(R.string.invalid_characters_in_display_name_msg);
        } else if (displayName.length() < 5) {
            return context.getString(R.string.display_name_too_short_msg);
        } else if (displayName.length() > 20) {
            return context.getString(R.string.display_name_too_long_msg);
        }
        return null;
    }
}
