package com.curtin.mathtest.Activities;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.curtin.mathtest.Database.UserHandler;
import com.curtin.mathtest.Database.UserImageHandler;
import com.curtin.mathtest.MainActivity;
import com.curtin.mathtest.Model.AppAlerter;
import com.curtin.mathtest.Model.InputValidator;
import com.curtin.mathtest.Model.User;
import com.curtin.mathtest.R;
import com.curtin.mathtest.Server.SessionManager;

public class UserConfigurationActivity extends AppCompatActivity {

    private EditText firstnameField;
    private EditText lastnameField;
    private EditText emailField;
    private EditText contactField;
    private EditText passwordField;

    private Button confirmButton;
    private Button searchContact;
    private ImageView previewImage;
    private Button searchPhotos;
    private Button takePhoto;

    private String contact;
    private String password;
    private boolean defaultPhoto = true;
    private boolean registerMode = false;
    private boolean addUserMode = false;
    private SessionManager sessionManager;
    private UserHandler userHandler;
    private UserImageHandler userImageHandler;
    private AppAlerter alert;
    private Intent intent;

    static final int REQUEST_THUMBNAIL = 1;

    private ActivityResultLauncher<Intent> resultLauncher;
    private ActivityResultLauncher<Intent> takePhotoLauncher;
    private static final int REQUEST_CONTACT = 2;
    private static final int REQUEST_READ_CONTACT_PERMISSION = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_configuration);
        alert = new AppAlerter(getApplicationContext());
        firstnameField = (EditText) findViewById(R.id.firstNameInput);
        lastnameField = (EditText) findViewById(R.id.lastNameInput);
        emailField =  (EditText) findViewById(R.id.emailInput);
        contactField = (EditText) findViewById(R.id.contactEditInput);
        passwordField = (EditText) findViewById(R.id.passwordEditInput);


        confirmButton = (Button) findViewById(R.id.confirmSettingButton);
        searchPhotos = (Button) findViewById(R.id.searchPhotoButton);
        takePhoto = (Button) findViewById(R.id.takePhotoButton);
        searchContact = (Button) findViewById(R.id.searchContacts);
        previewImage = (ImageView) findViewById(R.id.profileImagePreview);
        sessionManager = new SessionManager(getApplicationContext());
        userHandler = new UserHandler(getApplicationContext());
        userImageHandler = new UserImageHandler(getApplicationContext());

        intent = getIntent();

        contact = intent.getStringExtra("CONTACT");
        password = intent.getStringExtra("PASSWORD");

        System.out.println("Contact received: " + contact);

        takePhotoLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            if(data == null) {
                                return;
                            }
                            Bitmap image = (Bitmap) data.getExtras().get("data");
                            if(image != null){
                                previewImage.setImageBitmap(image);
                            }
                            if(userImageHandler.hasCustomProfileImage(contact)) {
                                userImageHandler.updateUserProfile(contact,contact,image);
                            }
                            else {
                                userImageHandler.setUserImage(contact,image);
                            }

                        }
                    }
                });

        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            Intent data = result.getData();
                            if(data == null) {
                                return;
                            }
                            Uri contactUri = data.getData();
                            String[] queryFields = new String[]{
                                    ContactsContract.Contacts._ID,
                                    ContactsContract.Contacts.DISPLAY_NAME,
                            };
                            ContentResolver contentResolver = getContentResolver();
                            Cursor c = contentResolver.query(
                                    contactUri, queryFields, null, null, null);
                            try {
                                if (c.getCount() > 0) {
                                    c.moveToFirst();
                                    int contactId = c.getInt(0);// ID first
                                    String contactName = c.getString(1); // Name second
                                    Uri phoneNumberUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                                    String whereClause = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?";
                                    String [] whereValues = new String[]{
                                            String.valueOf(contactId)
                                    };
                                    String[] phoneFields = new String[] {
                                            ContactsContract.CommonDataKinds.Phone.NUMBER
                                    };
                                    Cursor phoneCursor = contentResolver.query(
                                            phoneNumberUri, phoneFields, whereClause,whereValues, null);
                                    phoneCursor.moveToFirst();
                                    String phoneNumber = phoneCursor.getString(0);
                                    phoneCursor.close();
                                    String text = phoneNumber;
                                    contactField.setText(text);
                                }
                            } finally {
                                c.close();
                            }
                        }
                    }
                });

        if(userImageHandler.hasCustomProfileImage(contact)) {
            Bitmap profileImage = userImageHandler.getUserProfile(contact);
            previewImage.setImageBitmap(profileImage);
        }


        if(intent.hasExtra("REGISTER")) {
            this.registerMode = true;
            contactField.setVisibility(View.INVISIBLE);
            passwordField.setVisibility(View.INVISIBLE);
            previewImage.setVisibility(View.INVISIBLE);
            searchPhotos.setVisibility(View.INVISIBLE);
            takePhoto.setVisibility(View.INVISIBLE);
            searchContact.setText(View.INVISIBLE);
        }
        else if(intent.hasExtra("CREATE")) {
            this.addUserMode = true;
            previewImage.setVisibility(View.INVISIBLE);
            searchPhotos.setVisibility(View.INVISIBLE);
            takePhoto.setVisibility(View.INVISIBLE);
        }

        searchContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent contactIntent = new Intent();
                contactIntent.setAction(Intent.ACTION_PICK);
                contactIntent.setData(ContactsContract.Contacts.CONTENT_URI);
                resultLauncher.launch(contactIntent);
            }
        });
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String firstname = firstnameField.getText().toString();
                String lastname = lastnameField.getText().toString();
                String email = emailField.getText().toString();

                if(registerMode) {
                    boolean invalidChanges =    InputValidator.validateName(firstname) &&
                                                InputValidator.validateName(lastname) &&
                                                InputValidator.validateEmail(email);
                    if(!invalidChanges) {
                        alert = new AppAlerter(getApplicationContext());
                        alert.error("Invalid Profile settings, try again.");
                        return;
                    }
                    User user = new User(firstname,lastname,contact,email,password);
                    sessionManager.loginUser(contact,password);
                    userHandler.addUser(user,password);
                    alert = new AppAlerter(getApplicationContext());
                    alert.displayMessage("Created user " + firstname + " " + lastname);
                    Intent intentToMain = new Intent(UserConfigurationActivity.this, MainActivity.class);
                    startActivity(intentToMain);
                    finish();

                }
                else if(addUserMode) {
                    String contact = contactField.getText().toString();
                    String password = passwordField.getText().toString();
                    boolean invalidChanges =    InputValidator.validateName(firstname) &&
                                                InputValidator.validateName(lastname) &&
                                                InputValidator.validateEmail(email) &&
                                                InputValidator.ValidateNumber(contact) &&
                                                !password.trim().equals("");
                    if(!invalidChanges) {
                        alert.error("Invalid Profile settings, try again.");
                        return;
                    }
                    if(userHandler.userExist(contact)) {
                        alert.error("User with contact " + contact + " alreadt exists.");
                        return;
                    }

                    User user = new User(firstname,lastname,contact,email,password);
                    userHandler.addUser(user,password);
                    alert.displayMessage("Created user " + firstname + " " + lastname);
                    Intent intentToMain = new Intent(UserConfigurationActivity.this, MainActivity.class);
                    startActivity(intentToMain);
                    finish();
                }
                else {
                    User user = userHandler.getUser(contact);
                    String newContact = contactField.getText().toString();
                    String newPassword = passwordField.getText().toString();
                    boolean invalidChanges =    InputValidator.ValidateNumber(newContact) &&
                                                InputValidator.validateName(firstname) &&
                                                InputValidator.validateName(lastname) &&
                                                InputValidator.validateEmail(email) &&
                                                !newPassword.trim().equals("");
                    newContact = InputValidator.ValidateNumber(newContact) ? newContact : contact;
                    newPassword = newPassword.trim().equals("") ? user.getPassword() : newPassword;
                    firstname = InputValidator.validateName(firstname) ? firstname : user.getFirstName();
                    lastname = InputValidator.validateName(lastname) ? lastname : user.getLastName();
                    email = InputValidator.validateEmail(email) ? email : user.getEmail();


                    if(InputValidator.ValidateNumber(newContact) && userHandler.userExist(newContact) && !newContact.equals(user.getContact())) {
                        alert.error(" User with contact: " + newContact + " already exists.");
                        return;
                    }

                    User newUser = new User(firstname,lastname,newContact,email,newPassword);

                    userHandler.editUser(newUser,user,password);
                    if(!invalidChanges) {
                        alert.error("Invalid changes were detected, some changes may not apply.");
                    }
                    else {
                        alert.displayMessage("Successfully made changes.");
                    }

                    if(user.getContact().equals(sessionManager.getUser())) {
                        sessionManager.logoutUser();
                        sessionManager.loginUser(newContact,newPassword);

                        alert.displayMessage("Changes applied to logged user.");
                    }
                }

            }
        });

        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                takePhotoLauncher.launch(intent);
            }
        });

        searchPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentToSearchPhotoActivity = new Intent(UserConfigurationActivity.this,SearchPhotoActivity.class);
                intentToSearchPhotoActivity.putExtra("CONTACT",contact);
                startActivity(intentToSearchPhotoActivity);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(registerMode) {
            Intent returnToRegister = new Intent(UserConfigurationActivity.this, RegisterActivity.class);
            startActivity(returnToRegister);
            finish();
        }
        Intent returnToMainIntent = new Intent(UserConfigurationActivity.this, MainActivity.class);
        startActivity(returnToMainIntent);
        finish();
    }


}