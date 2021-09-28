package com.curtin.appinteraction;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.ContactsContract;

public class MainActivity extends AppCompatActivity {


    private static final int REQUEST_CONTACT = 2;


    private Button callButton;
    private Button getPhoneNumber;
    private Button showLocation;
    private Button thumbNailActivity;
    private Button fileProviderButton;

    private EditText phoneNumberInput;
    private TextView phoneContactReceived;
    private EditText latitudeInput;
    private EditText longitudeInput;


    private ActivityResultLauncher<Intent> resultLauncher;

    private static final int REQUEST_READ_CONTACT_PERMISSION = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        callButton = (Button) findViewById(R.id.callButton);
        getPhoneNumber = (Button) findViewById(R.id.getNumberButton);
        showLocation = (Button) findViewById(R.id.showLocationButton);

        thumbNailActivity = (Button) findViewById(R.id.thumbNailActivityButton);

        fileProviderButton = (Button) findViewById(R.id.fileProviderBtn);

        phoneContactReceived = (TextView) findViewById(R.id.phoneNumber);

        phoneNumberInput = (EditText) findViewById(R.id.phoneNumberInput);
        latitudeInput = (EditText) findViewById(R.id.latitudeEditText);
        longitudeInput = (EditText) findViewById(R.id.longitudeEditText);


        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_READ_CONTACT_PERMISSION);
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
                                    String whereClause = ContactsContract.CommonDataKinds.Phone._ID + "=?";
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
                                    String text = contactName + ": " + contactId + " no. " + phoneNumber;
                                    phoneContactReceived.setText(text);
                                }
                            } finally {
                                c.close();
                            }
                        }
                    }
                });



        fileProviderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentToFileProviderActivity = new Intent(MainActivity.this, PhotoFileProvider.class);
                startActivity(intentToFileProviderActivity);
            }
        });


        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number = phoneNumberInput.getText().toString();
                try {
                    int phone = Integer.parseInt(number.trim());
                    Uri uri = Uri.parse(String.format("tel:%d", phone));
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_DIAL);
                    intent.setData(uri);
                    startActivity(intent);
                }
                catch(NumberFormatException e) {
                    String err = "Invalid phone number input.";
                    Toast toast = Toast.makeText(getApplicationContext(), err, Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });


        getPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent contactIntent = new Intent();
                contactIntent.setAction(Intent.ACTION_PICK);
                contactIntent.setData(ContactsContract.Contacts.CONTENT_URI);
                resultLauncher.launch(contactIntent);
            }
        });


        showLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String latitude = latitudeInput.getText().toString();
                String longitude = longitudeInput.getText().toString();
                try {
                    double lat = Double.parseDouble(latitude);
                    double lon = Double.parseDouble(longitude);
                    Uri uri = Uri.parse(String.format("geo:%f,%f", lat, lon));
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setData(uri);
                    startActivity(intent);
                }
                catch(NumberFormatException e) {
                    String err = "Invalid phone number input.";
                    Toast toast = Toast.makeText(getApplicationContext(), err, Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });


        thumbNailActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PhotoViewer.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_READ_CONTACT_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Contact Reading Permission Granted", Toast.LENGTH_SHORT).show();
            }
        }
    }
}