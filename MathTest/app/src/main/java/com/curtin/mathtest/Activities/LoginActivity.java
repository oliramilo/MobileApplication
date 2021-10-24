package com.curtin.mathtest.Activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.curtin.mathtest.Fragments.LoginFragment;
import com.curtin.mathtest.R;

public class LoginActivity extends AppCompatActivity {

    private LoginFragment loginFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        FragmentManager fragmentManager = getSupportFragmentManager();
        loginFragment = (LoginFragment) fragmentManager.findFragmentById(R.id.fragmentLogin);
        if(loginFragment == null) {
            loginFragment = new LoginFragment();
            fragmentManager.beginTransaction().add(R.id.fragmentLogin,loginFragment).commit();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}