package org.michiganhackers.photoassassin.LoginPages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import org.michiganhackers.photoassassin.MainActivity;
import org.michiganhackers.photoassassin.R;

public class ResetPasswordActivity extends AppCompatActivity {

    private TextInputEditText emailEditText;
    private TextInputLayout emailTextInputLayout;
    private CoordinatorLayout coordinatorLayout;
    private FirebaseAuth auth;
    private final String TAG = getClass().getCanonicalName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        auth = FirebaseAuth.getInstance();

        coordinatorLayout = findViewById(R.id.coordinator_layout);
        emailEditText = findViewById(R.id.text_input_edit_text_email);
        emailTextInputLayout = findViewById(R.id.text_input_layout_email);
    }

    public void onResetPasswordButtonClick(android.view.View view) {
        // Validate email and set error message
        if (emailEditText.getText() == null) {
            return;
        }
        Email email = new Email(emailEditText.getText().toString(), this);
        String errorMsg = email.getError();
        emailTextInputLayout.setError(errorMsg);
        if (errorMsg != null) {
            return;
        }

        auth.sendPasswordResetEmail(email.getEmail())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            setResult(Activity.RESULT_OK);
                            finish();
                        } else {
                            Snackbar.make(coordinatorLayout, "Failed to send password reset email!", Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void onRegisterButtonClick(android.view.View view) {
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
    }

    public void onLoginButtonClick(android.view.View view) {
        finish();
    }
}
