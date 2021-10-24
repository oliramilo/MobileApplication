package com.curtin.mathtest.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.curtin.mathtest.Database.Schema.TestSchema;
import com.curtin.mathtest.Database.Schema.UserSchema;

public class DatabaseHandler extends SQLiteOpenHelper {

    public static final int VERSION = 1;
    public static final String DATABASE = "mathtest.db";

    public DatabaseHandler(Context ctx) {
        super(ctx,DATABASE,null,VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(UserSchema.UserTable.CREATE_TABLE);
        sqLiteDatabase.execSQL(UserSchema.ProfileImageTable.CREATE_TABLE);
        sqLiteDatabase.execSQL(TestSchema.TestTable.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
