package com.curtin.appinteraction;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;

public class PhotoFileProvider extends AppCompatActivity {
    private static final int REQUEST_PHOTO = 3;

    private Button btn;
    private ImageView imgView;
    private File photoFile;
    private Intent photoIntent;
    private Uri cameraUri;
    private ActivityResultLauncher<Intent> resultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_file_provider);




        btn = (Button) findViewById(R.id.getImageFileProviderButton);
        imgView = (ImageView) findViewById(R.id.fileProviderImageView);


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
                            Bitmap photo = BitmapFactory.decodeFile(photoFile.toString());
                            imgView.setImageBitmap(photo);

                        }
                    }
                });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photoIntent = new Intent();
                photoFile = new File(getFilesDir(),"photo.jpg");
                cameraUri = FileProvider.getUriForFile(
                        getApplicationContext(),
                        "com.curtin.appinteraction.fileprovider",
                        photoFile
                );
                photoIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                photoIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
                PackageManager pacman = getPackageManager();

                for(ResolveInfo a : pacman.queryIntentActivities(photoIntent,PackageManager.MATCH_DEFAULT_ONLY))
                {
                    grantUriPermission(a.activityInfo.packageName,cameraUri,Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
                resultLauncher.launch(photoIntent);
            }
        });

    }
}