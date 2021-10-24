package com.curtin.mathtest.Model;

public class User {
    private String firstName = "N/A";
    private String lastName = "N/A";
    private String contact;
    private String email;
    private String password;
    public User(String firstName,String lastName, String contact, String email,String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.contact = contact;
        this.email = email;
        this.password = password;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }

    public String getContact() {
        return this.contact;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public boolean hasEmail() {
        return email != null;
    }
    @Override
    public String toString() {
        return getContact() + "," + getFullName();
    }


}
