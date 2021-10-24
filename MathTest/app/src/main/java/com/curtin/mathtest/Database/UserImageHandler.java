package com.curtin.mathtest.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.curtin.mathtest.Database.Schema.UserSchema;
import com.curtin.mathtest.Model.UserImage;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class UserImageHandler {
    private SQLiteDatabase db;
    private Context ctx;
    public UserImageHandler(Context context) {
        this.ctx  = context;
        load();
    }

    public void load() {
        db = new DatabaseHandler(ctx.getApplicationContext()).getReadableDatabase();
    }

    public void setUserImage(String contact, Bitmap image) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, bos);

        byte[] data = bos.toByteArray();
        ContentValues cv = new ContentValues();
        cv.put(UserSchema.ProfileImageTable.Cols.CONTACT,contact);
        cv.put(UserSchema.ProfileImageTable.Cols.IMAGE,data);
        db.insert(UserSchema.ProfileImageTable.TABLE_NAME, null,cv);
    }

    public Bitmap getUserProfile(String contact) {
        Bitmap image = null;
        try (UserImageCursor cursor = new UserImageCursor(db.query(UserSchema.ProfileImageTable.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null))) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                UserImage userImg = cursor.getUserImage();
                if(userImg.getContact().equals(contact)) {
                    image = userImg.getUserImage();
                }
                cursor.moveToNext();
            }
        }
        return image;
    }

    public List<UserImage> getUserImages() {
        List<UserImage> userImages = new ArrayList<>();
        try (UserImageCursor cursor = new UserImageCursor(db.query(UserSchema.ProfileImageTable.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null))) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                UserImage userImg = cursor.getUserImage();
                userImages.add(userImg);
                cursor.moveToNext();
            }
        }
        return userImages;
    }


    public boolean hasCustomProfileImage(String contact) {
        boolean hasImage = false;
        try (UserImageCursor cursor = new UserImageCursor(db.query(UserSchema.ProfileImageTable.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null))) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast() && !hasImage) {
                UserImage userImg = cursor.getUserImage();
                if(userImg.getContact().equals(contact)) {
                    hasImage = true;
                }
                cursor.moveToNext();
            }
        }
        return hasImage;
    }

    public void updateUserName(String contact,String newContact) {
        if(hasCustomProfileImage(contact)) {
            Bitmap image = getUserProfile(contact);
            updateUserProfile(contact,newContact,image);
        }
    }

    public void updateUserProfile(String oldContact, String newContact, Bitmap image) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, bos);

        byte[] data = bos.toByteArray();
        ContentValues cv = new ContentValues();
        cv.put(UserSchema.ProfileImageTable.Cols.CONTACT, newContact);
        cv.put(UserSchema.ProfileImageTable.Cols.IMAGE, data);
        String[] whereValue = { String.valueOf(oldContact)};

        db.update(UserSchema.ProfileImageTable.TABLE_NAME,cv, UserSchema.ProfileImageTable.Cols.CONTACT + " = ? ", whereValue);
    }

    public void userOnDelete(String contact) {
        String command = "DELETE FROM " + UserSchema.ProfileImageTable.TABLE_NAME + " WHERE " + UserSchema.ProfileImageTable.Cols.CONTACT + " = " + contact + ";";
        String[] whereValue = { String.valueOf(contact) };
        db.delete(UserSchema.ProfileImageTable.TABLE_NAME, UserSchema.ProfileImageTable.Cols.CONTACT + " = ?", whereValue);
    }

    private class UserImageCursor extends CursorWrapper {

        public UserImageCursor(Cursor cursor) {
            super(cursor);
        }

        public UserImage getUserImage() {
            byte[] imageData = getBlob(getColumnIndex(UserSchema.ProfileImageTable.Cols.IMAGE));
            String contact = getString(getColumnIndex(UserSchema.ProfileImageTable.Cols.CONTACT));
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
            return new UserImage(contact,bitmap);
        }
    }
}
