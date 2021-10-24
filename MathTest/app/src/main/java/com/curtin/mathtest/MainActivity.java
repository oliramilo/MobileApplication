package com.curtin.mathtest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.curtin.mathtest.Activities.EmailerActivity;
import com.curtin.mathtest.Activities.LoginActivity;
import com.curtin.mathtest.Activities.MathTestActivity;
import com.curtin.mathtest.Activities.UserConfigurationActivity;
import com.curtin.mathtest.Activities.ViewTestsActivity;
import com.curtin.mathtest.Activities.ViewUsersActivity;
import com.curtin.mathtest.Database.UserHandler;
import com.curtin.mathtest.Database.UserImageHandler;
import com.curtin.mathtest.Fragments.DashboardFragment;
import com.curtin.mathtest.Model.AppAlerter;
import com.curtin.mathtest.Model.User;
import com.curtin.mathtest.Server.SessionManager;

public class MainActivity extends AppCompatActivity {
    private AppAlerter alert;
    private Button logoutButton;
    private Button startTestButton;
    private Button viewTestButton;
    private Button viewUsersButton;
    private Button editProfileButton;
    private Button emailStudents;
    private Button createStudentButton;
    private Button searchContactsButton;
    private SessionManager sessionManager;
    private DashboardFragment dashboardFragment;

    private UserImageHandler userImageHandler;
    private UserHandler userHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userHandler = new UserHandler(getApplicationContext());
        userImageHandler = new UserImageHandler(getApplicationContext());
        setTitle("Home page");

        alert = new AppAlerter(this);
        logoutButton = (Button) findViewById(R.id.logoutButton);
        startTestButton = (Button) findViewById(R.id.startTestButton);
        editProfileButton = (Button) findViewById(R.id.editProfileButton);
        viewTestButton = (Button) findViewById(R.id.viewTestButton);
        viewUsersButton = (Button) findViewById(R.id.viewUsers);
        emailStudents = (Button) findViewById(R.id.sendEmail);
        createStudentButton = (Button) findViewById(R.id.addUser);
        sessionManager = new SessionManager(getApplicationContext());

        if(!sessionManager.hasUserSession()) {
            Intent intentToLoginActivity = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intentToLoginActivity);
            finish();
        }


        String contact = sessionManager.getUser();
        User user = userHandler.getUser(contact);
        System.out.println("Logged in as: " +  user.toString());

        if(user.getContact().equals("0411639250")) {
            emailStudents.setVisibility(View.VISIBLE);
            viewUsersButton.setVisibility(View.VISIBLE);
            createStudentButton.setVisibility(View.VISIBLE);
            viewUsersButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intentToViewUsers = new Intent(MainActivity.this, ViewUsersActivity.class);
                    startActivity(intentToViewUsers);
                }
            });

            createStudentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intentToUserConfig = new Intent(MainActivity.this,UserConfigurationActivity.class);
                    intentToUserConfig.putExtra("CREATE", true);
                    startActivity(intentToUserConfig);
                }
            });
        }

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentToEditActivity = new Intent(MainActivity.this, UserConfigurationActivity.class);
                intentToEditActivity.putExtra("CONTACT",sessionManager.getUser());
                intentToEditActivity.putExtra("PASSWORD",sessionManager.getPassword());
                startActivity(intentToEditActivity);
            }
        });



        viewTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentToViewTests = new Intent(MainActivity.this, ViewTestsActivity.class);
                startActivity(intentToViewTests);
            }
        });


        startTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentToTest = new Intent(MainActivity.this, MathTestActivity.class);
                startActivity(intentToTest);
            }
        });


        emailStudents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentToEmailer = new Intent(MainActivity.this, EmailerActivity.class);
                startActivity(intentToEmailer);
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = alert.getAlert();
                DialogInterface.OnClickListener positiveListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                };

                DialogInterface.OnClickListener negativeListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sessionManager.logoutUser();
                        Intent intentToLoginActivity = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intentToLoginActivity);
                        finish();
                    }
                };
                builder.setMessage("You are about to close the app").setPositiveButton("Continue", positiveListener)
                        .setNegativeButton("Logout and exit",negativeListener);
                builder.show();

            }
        });


        FragmentManager fragmentManager = getSupportFragmentManager();
        dashboardFragment = (DashboardFragment) fragmentManager.findFragmentById(R.id.userDashBoardFragment);
        if(dashboardFragment == null) {
            Bitmap img = null;
            if(userImageHandler.hasCustomProfileImage(user.getContact())) {
                img = userImageHandler.getUserProfile(user.getContact());
            }
            dashboardFragment = new DashboardFragment(img,user.getFullName());
            fragmentManager.beginTransaction().add(R.id.userDashBoardFragment,dashboardFragment).commit();
        }


    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = alert.getAlert();
        DialogInterface.OnClickListener positiveListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        };

        DialogInterface.OnClickListener negativeListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                sessionManager.logoutUser();
                finish();
            }
        };
        builder.setMessage("You are about to close the app").setPositiveButton("Continue", positiveListener)
                .setNegativeButton("Logout and exit",negativeListener);
        builder.show();
    }
}