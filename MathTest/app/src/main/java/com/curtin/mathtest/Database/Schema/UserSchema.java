package com.curtin.mathtest.Database.Schema;

public class UserSchema {

    public static class UserTable {
        public static final String TABLE_NAME = "users";
        public static class Cols {

            public static final String CONTACT = "contact";
            public static final String FIRSTNAME = "firstname";
            public static final String LASTNAME = "lastname";
            public static final String EMAIL = "email";
            public static final String PASSWORD = "password";
        }


        public static final String CREATE_TABLE =   "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
                                                    "( " + Cols.CONTACT + " TEXT PRIMARY KEY, " +
                                                    Cols.FIRSTNAME + " TEXT NOT NULL, " +
                                                    Cols.LASTNAME + " TEXT NOT NULL, " +
                                                    Cols.EMAIL + " TEXT , " +
                                                    Cols.PASSWORD + " TEXT NOT NULL " + ");";
    }

    public static class ProfileImageTable {
        public static final String TABLE_NAME = "user_images";
        public static class Cols {
            public static final String IMAGE = "image";
            public static final String CONTACT = UserTable.Cols.CONTACT;
        }


        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + "( " + Cols.IMAGE + " BLOB, " +
                Cols.CONTACT + " TEXT UNIQUE );";
    }
}

