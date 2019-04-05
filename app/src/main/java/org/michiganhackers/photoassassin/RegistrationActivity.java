package org.michiganhackers.photoassassin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RegistrationActivity extends AppCompatActivity {

    private TextInputEditText emailEditText;
    private TextInputLayout emailTextInputLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        emailEditText = findViewById(R.id.text_input_edit_text_email);
        emailTextInputLayout = findViewById(R.id.text_input_layout_email);
    }

    public void onRegisterButtonClick(android.view.View view) {
        if (emailEditText.getText() == null) {
            return;
        }
        Email email = new Email(emailEditText.getText().toString(), this);
        String errorMsg = email.getError();
        emailTextInputLayout.setError(errorMsg);
        if (errorMsg == null) {
            return;
        }

    }

    private void onLoginButtonClick() {

    }

    private void onRegisterGoogleButtonClick() {

    }

    private void onRegisterFacebookButtonClick() {

    }
}
