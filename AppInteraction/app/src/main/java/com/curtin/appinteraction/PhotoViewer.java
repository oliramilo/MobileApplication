package com.curtin.appinteraction;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class PhotoViewer extends AppCompatActivity {

    static final int REQUEST_THUMBNAIL = 1;
    private Button getImageButton;
    private ImageView img;
    private ActivityResultLauncher<Intent> resultLauncher;
    private Bitmap image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_viewer);
        getImageButton = (Button) findViewById(R.id.getImageBtn);
        img = (ImageView) findViewById(R.id.imageView);

        getImageButton = (Button) findViewById(R.id.getImageBtn);

        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            if(data == null) {
                                return;
                            }
                            image = (Bitmap) data.getExtras().get("data");
                            if(image != null){
                                img.setImageBitmap(image);
                            }

                        }
                    }
                });

        getImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                resultLauncher.launch(intent);
            }
        });
    }
}