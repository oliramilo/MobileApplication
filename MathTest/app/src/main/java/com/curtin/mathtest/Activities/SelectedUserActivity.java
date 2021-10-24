package com.curtin.mathtest.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.curtin.mathtest.Database.TestHandler;
import com.curtin.mathtest.Database.UserHandler;
import com.curtin.mathtest.Database.UserImageHandler;
import com.curtin.mathtest.MainActivity;
import com.curtin.mathtest.Model.User;
import com.curtin.mathtest.R;

public class SelectedUserActivity extends AppCompatActivity {

    private UserHandler userHandler;
    private UserImageHandler userImageHandler;
    private TestHandler testHandler;
    private ImageView profileImage;
    private TextView userTextBox;

    private Button editButton;
    private Button deleteUserButton;
    private Button viewTestButton;

    private Context ctx;
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_user);

        Intent intent = getIntent();
        String contact = intent.getStringExtra("CONTACT");
        this.ctx = getApplicationContext();
        System.out.println("Contact received to select user activity" + contact);

        testHandler = new TestHandler(ctx);
        userHandler = new UserHandler(ctx);
        userImageHandler = new UserImageHandler(ctx);

        profileImage = (ImageView) findViewById(R.id.selectedUserProfileImage);
        userTextBox = (TextView) findViewById(R.id.userInfoTextBox);
        editButton = (Button) findViewById(R.id.editButton);
        deleteUserButton = (Button) findViewById(R.id.deleteUser);
        viewTestButton = (Button) findViewById(R.id.viewSelectedUserTestsButton);

        boolean hasImage = userImageHandler.hasCustomProfileImage(contact);
        user = userHandler.getUser(contact);
        userTextBox.setText("Name: "+user.getFullName() + "\nContact: " + user.getContact() + "\nEmail: " + user.getEmail());
        if(hasImage) {
            Bitmap image = userImageHandler.getUserProfile(contact);
            profileImage.setImageBitmap(image);
        }


        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentToEditActivity = new Intent(SelectedUserActivity.this,UserConfigurationActivity.class);
                intentToEditActivity.putExtra("CONTACT",contact);
                startActivity(intentToEditActivity);
            }
        });

        deleteUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userHandler.removeUser(contact);
                userImageHandler.userOnDelete(contact);
                testHandler.removeTestsFromUser(contact);
            }
        });


        viewTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentToViewTests = new Intent(SelectedUserActivity.this,ViewTestsActivity.class);
                intentToViewTests.putExtra("CONTACT",user.getContact());
                startActivity(intentToViewTests);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intentToViewUsers = new Intent(SelectedUserActivity.this, MainActivity.class);
        startActivity(intentToViewUsers);
    }
}