package com.curtin.mathtest.Model;

import android.graphics.Bitmap;

public class UserImage {

    private Bitmap image;
    private String contact;

    public UserImage(String userContact, Bitmap image) {
        contact = userContact;
        this.image = image;
    }

    public Bitmap getUserImage(){
        return image;
    }

    public String getContact() {
         return contact;
    }
}
