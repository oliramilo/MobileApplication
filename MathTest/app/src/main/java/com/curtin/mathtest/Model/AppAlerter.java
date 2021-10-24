package com.curtin.mathtest.Model;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.Toast;

public class AppAlerter {
    Context ctx = null;
    private AlertDialog.Builder builder = null;
    public AppAlerter(Context context) {
        builder = new AlertDialog.Builder(context);
        ctx = context;
    }

    public void displayMessage(String message){
        Toast toast = Toast.makeText(ctx, message, Toast.LENGTH_LONG);
        toast.show();
    }

    public void invalidSelection() {
        CharSequence seq = "Invalid selection.";
        Toast toast = Toast.makeText(ctx, seq, Toast.LENGTH_LONG);
        toast.show();
    }

    public void showSuccess() {
        CharSequence seq = "Success!";
        Toast successToast = Toast.makeText(ctx, seq, Toast.LENGTH_SHORT);
        successToast.show();

    }

    public void showSuccessWithMessage(String message) {
        CharSequence seq = "Success! " + message;
        Toast successToast = Toast.makeText(ctx, seq, Toast.LENGTH_SHORT);
        successToast.show();
    }

    public void showUserExists(String username) {
        CharSequence seq = "User " + username + " already exists.";
        Toast toast = Toast.makeText(ctx, seq, Toast.LENGTH_LONG);
        toast.show();
    }

    public void error(String errorMessage) {
        CharSequence seq = "Error: " + errorMessage;
        Toast toast = Toast.makeText(ctx, seq, Toast.LENGTH_LONG);
        toast.show();
    }

    public AlertDialog.Builder getAlert() {
        return builder;
    }


}