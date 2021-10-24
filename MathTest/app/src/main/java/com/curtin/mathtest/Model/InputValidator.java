package com.curtin.mathtest.Model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**This class was re-used from Mobile Application Development Assignment 1 **/

public class InputValidator {
    public static final int NAME_LENGTH = 12;

    public static boolean validateUserName(String username) {
        if(username == null) {
            return false;
        }
        /**Character length must be 8-24 characters must only container letters and
         * numbers ._ are allowed but username cannot start with it or be next to each other**/
        String regex = "^[a-zA-Z0-9]([._-](?![._-])|[a-zA-Z0-9]){6,22}[a-zA-Z0-9]$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(username);
        if(m.find()) {
            return true;
        }
        return false;
    }

    public static boolean validateEmail(String email) {
        if(email == null) {
            return false;
        }
        /**Character length**/
        String regex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
        Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(email);
        if(!email.trim().equals("") && m.find()) {
            return true;
        }
        return false;
    }

    public static boolean validateName(String name) {
        if(name == null) {
            return false;
        }
        String regex = "^[a-zA-Z ]*$";
        Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(name);
        if(!name.trim().equals("") && m.find()) {
            return true;
        }
        return false;
    }

    public static boolean PasswordCompare(String password1, String password2) {
        return password1.equals(password2);
    }

    public static boolean ValidateNumber(String number) {
        String regex = "\\d+";
        return number.length() == 10 && number.matches(regex);
    }


}