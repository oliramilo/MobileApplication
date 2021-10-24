package com.curtin.mathtest.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.curtin.mathtest.Activities.RegisterActivity;
import com.curtin.mathtest.Database.UserHandler;
import com.curtin.mathtest.MainActivity;
import com.curtin.mathtest.Model.AppAlerter;
import com.curtin.mathtest.R;
import com.curtin.mathtest.Server.SessionManager;

public class LoginFragment extends Fragment {


    private SessionManager sessionManager;


    private EditText loginEditField;
    private EditText passwordEditField;
    private Button loginButton;
    private Button registerButton;
    private CheckBox rememberMe;

    private UserHandler userHandler;
    private AppAlerter alert;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login,container,false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        loginEditField = (EditText) view.findViewById(R.id.loginField);
        passwordEditField = (EditText) view.findViewById(R.id.passwordField);
        loginButton = (Button) view.findViewById(R.id.loginButton);
        registerButton = (Button) view.findViewById(R.id.registerButton);

        rememberMe = (CheckBox) view.findViewById(R.id.rememberMeCheckBox);
        userHandler = new UserHandler(requireActivity().getApplicationContext());
        alert = new AppAlerter(requireActivity().getApplicationContext());
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean checked = rememberMe.isChecked();
                String login = loginEditField.getText().toString();
                String password = passwordEditField.getText().toString();
                sessionManager = new SessionManager(requireActivity().getApplicationContext());
                if(!userHandler.userExist(login)) {
                    alert.error("User login does not exist in database");
                    return;
                }
                if(!userHandler.authenticateUser(login,password)) {
                    alert.error("Invalid password for user login");
                    return;
                }
                sessionManager.loginUser(login,password);
                Intent intentToMain = new Intent(getActivity(),MainActivity.class);
                startActivity(intentToMain);
                getActivity().finish();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentToRegisterActivity = new Intent(getActivity(), RegisterActivity.class);
                startActivity(intentToRegisterActivity);
                getActivity().finish();
            }
        });

    }
}