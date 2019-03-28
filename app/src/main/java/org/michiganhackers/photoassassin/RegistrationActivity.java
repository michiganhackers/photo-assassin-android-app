package org.michiganhackers.photoassassin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import org.michiganhackers.photoassassin.databinding.ActivityRegistrationBinding;

public class RegistrationActivity extends AppCompatActivity {
    private ActivityRegistrationBinding binding;
    private RegistrationActivityViewModel viewmodel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_registration);
        binding.setLifecycleOwner(this);
        viewmodel = ViewModelProviders.of(this).get(RegistrationActivityViewModel.class);
        binding.setViewmodel(viewmodel);

        viewmodel.getEmail().setValue("TEST");
    }
}
