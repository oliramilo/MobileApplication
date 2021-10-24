package com.curtin.mathtest.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;

import com.curtin.mathtest.Database.Schema.UserSchema;
import com.curtin.mathtest.Model.User;

import java.util.ArrayList;
import java.util.List;

public class UserHandler {

    private SQLiteDatabase db;
    private Context context;
    public UserHandler(Context context) {
        this.context  = context;
        load();
    }

    public void load() {
        db = new DatabaseHandler(context.getApplicationContext()).getReadableDatabase();
    }

    public void addUser(User user, String password) {
        if(userExist(user.getContact())) {
            return;
        }
        String email = user.getEmail().equals("") ? "N/A" : user.getEmail();
        ContentValues cv = new ContentValues();
        cv.put(UserSchema.UserTable.Cols.CONTACT, user.getContact());
        cv.put(UserSchema.UserTable.Cols.FIRSTNAME, user.getFirstName());
        cv.put(UserSchema.UserTable.Cols.LASTNAME, user.getLastName());
        cv.put(UserSchema.UserTable.Cols.EMAIL, email);
        cv.put(UserSchema.UserTable.Cols.PASSWORD, password);
        db.insert(UserSchema.UserTable.TABLE_NAME,null,cv);
    }

    public void removeUser(String contact) {
        if(!userExist(contact)) {
            return;
        }
        String command = "DELETE FROM " + UserSchema.UserTable.TABLE_NAME + " WHERE " + UserSchema.UserTable.Cols.CONTACT + " = " + contact + ";";
        String[] whereValue = { String.valueOf(contact)};
        db.delete(UserSchema.UserTable.TABLE_NAME, UserSchema.UserTable.Cols.CONTACT + " = ?", whereValue);
    }

    public void editUser(User userToEdit, User oldUser, String password) {
        if(!userExist(oldUser.getContact())) {
            return;
        }
        String newUserContact = userToEdit.getContact();
        String newFirstName = userToEdit.getFirstName();
        String newLastName = userToEdit.getLastName();
        String newEmail = userToEdit.getEmail();

        ContentValues cv = new ContentValues();
        cv.put(UserSchema.ProfileImageTable.Cols.CONTACT, newUserContact);
        cv.put(UserSchema.UserTable.Cols.FIRSTNAME, newFirstName);
        cv.put(UserSchema.UserTable.Cols.LASTNAME, newLastName);
        cv.put(UserSchema.UserTable.Cols.EMAIL, newEmail);
        String[] where = {oldUser.getContact()};
        db.update(UserSchema.UserTable.TABLE_NAME, cv,UserSchema.UserTable.Cols.CONTACT + " = ? ",where);
    }



    public List<User> getAllUsers(){
        List<User> users = new ArrayList<>();
        try (UserCursor cursor = new UserCursor(db.query(UserSchema.UserTable.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null))) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                User user = cursor.getUser();
                users.add(user);
                cursor.moveToNext();
            }
        }
        return users;
    }

    public List<User> getAllUsersWithEmail(){
        List<User> users = new ArrayList<>();
        try (UserCursor cursor = new UserCursor(db.query(UserSchema.UserTable.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null))) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                User user = cursor.getUser();
                if(user.hasEmail()) {
                    users.add(user);
                }
                cursor.moveToNext();
            }
        }
        return users;
    }



    public User getUser(String contact){
        boolean found = false;
        User user = null;
        try (UserCursor cursor = new UserCursor(db.query(UserSchema.UserTable.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null))) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast() && !found) {
                user = cursor.getUser();
                System.out.println("User in db: " + user.toString());
                if(user.getContact().equals(contact)) {
                    found = true;

                }
                cursor.moveToNext();
            }
        }
        return user;
    }

    public boolean userExist(String contact) {
        boolean exist = false;
        UserCursor cursor = new UserCursor(db.query(UserSchema.UserTable.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null));

        try {
            cursor.moveToFirst();
            while(!cursor.isAfterLast() && !exist) {
                User user = cursor.getUser();
                if(user.getContact().equals(contact)) {
                    exist = true;
                }
                cursor.moveToNext();
            }

        }
        finally {
            cursor.close();
        }
        return exist;
    }

    public boolean authenticateUser(String contact,String password) {
        boolean auth  = userExist(contact);
        boolean found = false;
        if(!auth) {
            return false;
        }

        try (UserCursor cursor = new UserCursor(db.query(UserSchema.UserTable.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null))) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast() && !found) {
                User user = cursor.getUser();
                if(user.getContact().equals(contact)) {
                    System.out.println("Password: " + user.getPassword());
                    if(user.getPassword().equals(password)) {
                        auth = true;
                        found = true;
                    }
                }
                cursor.moveToNext();
            }
        }
        return auth;
    }

    private class UserCursor extends CursorWrapper {
        public UserCursor(Cursor cursor) {
            super(cursor);
        }

        public User getUser() {
            String firstname = getString(getColumnIndex(UserSchema.UserTable.Cols.FIRSTNAME));
            String lastname = getString(getColumnIndex(UserSchema.UserTable.Cols.LASTNAME));
            String contact = getString(getColumnIndex(UserSchema.UserTable.Cols.CONTACT));
            String email = getString(getColumnIndex(UserSchema.UserTable.Cols.EMAIL));
            String password =  getString(getColumnIndex(UserSchema.UserTable.Cols.PASSWORD));
            return new User(firstname, lastname, contact, email, password);
        }
    }

}
