package com.curtin.mathtest.Activities;



import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.curtin.mathtest.Database.UserHandler;
import com.curtin.mathtest.Model.AppAlerter;
import com.curtin.mathtest.Model.InputValidator;
import com.curtin.mathtest.R;
import com.curtin.mathtest.Server.SessionManager;

public class RegisterActivity extends AppCompatActivity {
    private SessionManager sessionManager;
    private EditText contactEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private Button getContactsButton;
    private Button registerButton;
    private ActivityResultLauncher<Intent> resultLauncher;
    private UserHandler userHandler;
    AppAlerter alert;
    private static final int REQUEST_CONTACT = 2;
    private static final int REQUEST_READ_CONTACT_PERMISSION = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        alert = new AppAlerter(getApplicationContext());
        userHandler = new UserHandler(getApplicationContext());
        sessionManager = new SessionManager(getApplicationContext());
        contactEditText = (EditText) findViewById(R.id.contactInput);
        passwordEditText = (EditText) findViewById(R.id.passwordInput);
        confirmPasswordEditText = (EditText) findViewById(R.id.confirmPasswordInput);
        getContactsButton = (Button) findViewById(R.id.getContactButton);
        registerButton = (Button) findViewById(R.id.userRegisterButton);

        if(ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_READ_CONTACT_PERMISSION);
        }
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
                                    contactEditText.setText(text);
                                }
                            } finally {
                                c.close();
                            }
                        }
                    }
                });


        getContactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent contactIntent = new Intent();
                contactIntent.setAction(Intent.ACTION_PICK);
                contactIntent.setData(ContactsContract.Contacts.CONTENT_URI);
                resultLauncher.launch(contactIntent);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userContact = contactEditText.getText().toString();
                String password1 = passwordEditText.getText().toString();
                String password2 = confirmPasswordEditText.getText().toString();
                boolean same = InputValidator.PasswordCompare(password1, password2);
                if(!same) {

                    alert.error("Password entered are not the same.");
                    return;
                }
                if(!InputValidator.ValidateNumber(userContact)) {
                    alert.error("Invalid Phone Number.");
                    return;
                }
                if(userHandler.userExist(userContact)) {
                    alert.error("User with contact already exists.");
                    return;
                }
                Intent intentToUserConfig = new Intent(RegisterActivity.this, UserConfigurationActivity.class);
                System.out.println("Contact sending to user config:" + userContact);
                intentToUserConfig.putExtra("CONTACT",userContact);
                intentToUserConfig.putExtra("PASSWORD",password1);
                intentToUserConfig.putExtra("REGISTER",true);
                startActivity(intentToUserConfig);
                finish();

            }
        });



    }

}