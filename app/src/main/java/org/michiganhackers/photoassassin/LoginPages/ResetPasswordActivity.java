package org.michiganhackers.photoassassin.LoginPages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import org.michiganhackers.photoassassin.Email;
import org.michiganhackers.photoassassin.R;
import org.michiganhackers.photoassassin.Util;

public class ResetPasswordActivity extends AppCompatActivity {

    private TextInputEditText emailEditText;
    private TextInputLayout emailTextInputLayout;
    private CoordinatorLayout coordinatorLayout;
    private FirebaseAuth auth;
    private final String TAG = getClass().getCanonicalName();
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        auth = FirebaseAuth.getInstance();
        coordinatorLayout = findViewById(R.id.coordinator_layout);

        emailEditText = findViewById(R.id.text_input_edit_text_email);
        emailTextInputLayout = findViewById(R.id.text_input_layout_email);

        progressBar = findViewById(R.id.progress_bar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    public void onResetPasswordButtonClick(android.view.View view) {
        // Validate email and set error message
        if (emailEditText.getText() == null) {
            Log.e(TAG, "EditText getText() returned null");
            return;
        }
        Email email = new Email(emailEditText.getText().toString(), this);
        String errorMsg = email.getError();
        Util.setTextInputLayoutErrorReclaim(emailTextInputLayout, errorMsg);
        if (errorMsg != null) {
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        auth.sendPasswordResetEmail(email.getEmail())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            setResult(Activity.RESULT_OK);
                            finish();
                        } else {
                            Snackbar.make(coordinatorLayout, R.string.failed_send_pwd_reset_email, Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void onRegisterButtonClick(android.view.View view) {
        Intent intent = new Intent(this, SetupProfileActivity.class);
        startActivity(intent);
    }

    public void onLoginButtonClick(android.view.View view) {
        finish();
    }
}
